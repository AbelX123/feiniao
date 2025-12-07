package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.constants.Bucket;
import com.ghml.feiniao.common.constants.PhoneStatus;
import com.ghml.feiniao.common.constants.RedisPrefix;
import com.ghml.feiniao.common.dto.BrandDto;
import com.ghml.feiniao.common.entity.BrandEntity;
import com.ghml.feiniao.common.entity.CreatorEntity;
import com.ghml.feiniao.common.entity.FavoriteEntity;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.mapper.BrandMapper;
import com.ghml.feiniao.common.service.RedisService;
import com.ghml.feiniao.common.vo.AvatarVo;
import com.ghml.feiniao.common.vo.BrandVo;
import com.ghml.feiniao.security.utils.SecurityUtils;
import com.ghml.feiniao.users.config.MinIOProps;
import com.ghml.feiniao.users.service.*;
import com.ghml.feiniao.users.utils.MinIOUtils;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BrandServiceImpl extends ServiceImpl<BrandMapper, BrandEntity> implements BrandService {

    private final MinioClient minioClient;
    private final CreatorService creatorService;
    private final FavoriteService favoriteService;
    private final RedisService redisService;
    private final MinIOProps minIOProps;
    private final MenuService menuService;

    // 根据编号查询产品主信息
    @Override
    public BrandVo getBrandById() {
        Optional<BrandEntity> opt = this.getOptById(SecurityUtils.getCurrentUserId());
        if (opt.isEmpty()) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }

        // 获取头像外链
        AvatarVo avatar = getAvatarUrl();

        BrandEntity brandEntity = opt.get();
        return BrandVo.builder()
                .userId(SecurityUtils.getCurrentUserId())
                .username(brandEntity.getUsername())
                .phone(brandEntity.getPhoneNumber())
                .avatar(avatar)
                .build();
    }

    // 收藏创作者
    @Override
    public void followCreator(String creatorId) {
        // 查询产品主编号
        String brandId = SecurityUtils.getCurrentUserId();
        // 检查creatorId存在
        Optional<CreatorEntity> opt = creatorService.getOptById(creatorId);
        if (opt.isEmpty()) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }
        // 重复收藏，幂等处理，静默成功
        LambdaQueryWrapper<FavoriteEntity> favorQ = new LambdaQueryWrapper<>();
        favorQ.eq((SFunction<FavoriteEntity, String>) FavoriteEntity::getBrandId, brandId);
        favorQ.eq((SFunction<FavoriteEntity, String>) FavoriteEntity::getCreatorId, creatorId);
        if (favoriteService.getOneOpt(favorQ).isPresent()) {
            return;
        }
        // 收藏
        FavoriteEntity entity = new FavoriteEntity();
        entity.setBrandId(brandId);
        entity.setCreatorId(creatorId);
        try {
            favoriteService.save(entity);
        } catch (Exception e) {
            log.error("收藏创作者数据库操作错误:{}", e.getMessage());
            throw new ServiceException(Code.OPERATION_FAILED);
        }
    }

    // 取消收藏创作者
    @Override
    public void unfollowCreator(String creatorId) {
        // 查询产品主编号
        String brandId = SecurityUtils.getCurrentUserId();
        // 检查creatorId存在
        Optional<CreatorEntity> opt = creatorService.getOptById(creatorId);
        if (opt.isEmpty()) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }
        // 重复取消，幂等处理，静默成功
        LambdaQueryWrapper<FavoriteEntity> favorQ = new LambdaQueryWrapper<>();
        favorQ.eq((SFunction<FavoriteEntity, String>) FavoriteEntity::getBrandId, brandId);
        favorQ.eq((SFunction<FavoriteEntity, String>) FavoriteEntity::getCreatorId, creatorId);
        if (favoriteService.getOneOpt(favorQ).isEmpty()) {
            return;
        }
        // 取消
        try {
            favoriteService.remove(favorQ);
        } catch (Exception e) {
            log.error("取消创作者数据库操作错误:{}", e.getMessage());
            throw new ServiceException(Code.OPERATION_FAILED);
        }
    }


    /**
     * 头像上传
     * @param file
     * @return minio外链
     */
    @Override
    public AvatarVo uploadAvatar(MultipartFile file) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        try {
            // 上传头像到minio
            MinIOUtils.uploadFile(minioClient, file, Bucket.AVATARS.getName(), currentUserId);
            return getAvatarUrl();
        } catch (Exception e) {
            log.error("OSS文件上传失败:{}", e.getMessage());
            throw new ServiceException(Code.OSS_ERROR);
        }
    }

    // 获取头像外链
    @Override
    public AvatarVo getAvatarUrl() {
        // 校验filename和用户是否一致
        String filename = SecurityUtils.getCurrentUserId();
        try {
            int expiryHours = minIOProps.getAvatarExpiry();

            // 获取外链
            String avatar = MinIOUtils.getObjectUrl(
                    minioClient,
                    Bucket.AVATARS.getName(),
                    filename,
                    expiryHours
            );

            // 计算失效时间
            long generateAt = System.currentTimeMillis();
            long expiryMillis = expiryHours * 3600L * 1000L;
            long expiry = generateAt + expiryMillis;

            return AvatarVo.builder()
                    .avatar(avatar)
                    .expiry(expiry)
                    .build();
        } catch (Exception e) {
            log.error("OSS外链获取失败: {}", e.getMessage(), e);
            throw new ServiceException(Code.OSS_ERROR);
        }
    }

    @Override
    public void register(BrandEntity brandEntity) {
        this.save(brandEntity);
    }

    // 部分更新产品主信息
    @Override
    public BrandVo patchBrand(BrandDto dto) {
        // 获取userId
        String brandId = SecurityUtils.getCurrentUserId();

        BrandEntity entity = new BrandEntity();
        // 如果存在手机号更新，验证验证码状态
        if (!dto.getPhoneFull().isEmpty()) {
            String key = String.format(RedisPrefix.PREFIX_PHONE_VERIFIED_CODE, dto.getPhoneFull(), brandId);
            String codeInCache = (String) redisService.get(key);
            if (StringUtils.isEmpty(codeInCache) || !StringUtils.equals(codeInCache, dto.getVerifiedCode())) {
                throw new ServiceException(Code.VERIFIED_CODE_EXPIRED);
            } else {
                // 验证完成让验证码失效
                Boolean delete = redisService.delete(key);
                if (!delete) {
                    throw new ServiceException(Code.OPERATION_FAILED);
                }
            }
            entity.setPhoneCountryCode(dto.getPhoneCountryCode());
            entity.setPhoneNumber(dto.getPhoneNumber());
            entity.setPhoneFull(dto.getPhoneFull());
            entity.setPhoneVerified(PhoneStatus.VERIFIED.getCode());
            entity.setVerifiedAt(new Date());
        }
        entity.setUserId(brandId);
        if (StringUtils.isNotBlank(dto.getUsername())) {
            entity.setUsername(dto.getUsername());
        }
        boolean update = this.updateById(entity);
        if (!update) {
            throw new ServiceException(Code.OPERATION_FAILED);
        }
        return getBrandById();
    }

}
