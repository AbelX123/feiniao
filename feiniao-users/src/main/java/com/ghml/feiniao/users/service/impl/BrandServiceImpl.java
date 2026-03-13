package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.constants.Bucket;
import com.ghml.feiniao.common.dto.BrandDto;
import com.ghml.feiniao.common.dto.CaptchaVerifyDto;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final CaptchaService captchaService;

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
     *
     * @param file
     * @return minio外链
     */
    @Override
    public AvatarVo uploadAvatar(MultipartFile file) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        if (file == null || file.isEmpty()) {
            throw new ServiceException(Code.PARAM_ERROR);
        }
        try {
            // 上传头像到minio
            MinIOUtils.uploadFile(minioClient, file, Bucket.AVATARS.getName(), currentUserId);
            log.info("用户[{}]更新头像成功!", SecurityUtils.getCurrentUsername());
            return regenerateAvatarUrl(currentUserId);
        } catch (Exception e) {
            log.error("OSS文件上传失败:{}", e.getMessage());
            throw new ServiceException(Code.OSS_ERROR);
        }
    }

    // 获取头像外链
    @Override
    public AvatarVo getAvatarUrl() {
        String userId = SecurityUtils.getCurrentUserId();
        try {
            BrandEntity brand = this.getById(userId);
            if (brand == null) {
                throw new ServiceException(Code.USER_NOT_EXIST);
            }

            // 若数据库中的链接仍有效，直接返回，减少重复签名
            if (StringUtils.isNotBlank(brand.getAvatarUrl())
                    && brand.getAvatarUrlExpiry() != null
                    && LocalDateTime.now().isBefore(brand.getAvatarUrlExpiry())) {
                return AvatarVo.builder()
                        .avatar(brand.getAvatarUrl())
                        .expiry(toEpochMilli(brand.getAvatarUrlExpiry()))
                        .build();
            }

            // 新用户未上传头像时，不应当作错误返回
            if (!MinIOUtils.objectExists(minioClient, Bucket.AVATARS.getName(), userId)) {
                return AvatarVo.builder()
                        .avatar(null)
                        .expiry(0L)
                        .build();
            }

            return regenerateAvatarUrl(userId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("OSS外链获取失败: {}", e.getMessage(), e);
            throw new ServiceException(Code.OSS_ERROR);
        }
    }

    private AvatarVo regenerateAvatarUrl(String objectKey) throws Exception {
        if (!MinIOUtils.objectExists(minioClient, Bucket.AVATARS.getName(), objectKey)) {
            throw new ServiceException(Code.OSS_NOT_EXIST);
        }

        int expiryHours = minIOProps.getAvatarExpiry();
        String avatarUrl = MinIOUtils.getObjectUrl(
                minioClient,
                Bucket.AVATARS.getName(),
                objectKey,
                expiryHours
        );
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(expiryHours);

        BrandEntity update = new BrandEntity();
        update.setUserId(objectKey);
        update.setAvatarUrl(avatarUrl);
        update.setAvatarUrlExpiry(expiryTime);
        boolean updated = this.updateById(update);
        if (!updated) {
            throw new ServiceException(Code.OPERATION_FAILED);
        }

        return AvatarVo.builder()
                .avatar(avatarUrl)
                .expiry(toEpochMilli(expiryTime))
                .build();
    }

    private long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Override
    public void register(BrandEntity brandEntity) {
        this.save(brandEntity);
    }

    // 部分更新产品主信息
    @Override
    public BrandVo patchBrand(BrandDto dto) {
        // 获取当前用户ID
        String brandId = SecurityUtils.getCurrentUserId();

        // 查询现有用户信息
        BrandEntity existingBrand = this.getById(brandId);

        if (existingBrand == null) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }

        // 构建更新实体
        BrandEntity updateEntity = new BrandEntity();
        updateEntity.setUserId(brandId);

        boolean needUpdate = createUpdateEntity(updateEntity, existingBrand, dto);
        if (needUpdate) {
            this.updateById(updateEntity);
        }
        return getBasicInfo(brandId);
    }

    private BrandVo getBasicInfo(String brandId) {
        BrandEntity entity = this.getById(brandId);
        if (entity == null) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }
        return BrandVo.builder()
                .username(entity.getUsername())
                .phone(entity.getPhoneNumber())
                .build();
    }

    /**
     * 构建更新实体
     *
     * @param updateEntity 更新实体
     * @param dto          参数
     * @return 是否需要更新
     */
    private boolean createUpdateEntity(BrandEntity updateEntity, BrandEntity existingBrand, BrandDto dto) {
        boolean needUpdate = false;
        // 如果存在手机号更新，先验证验证码
        if (StringUtils.isNotBlank(dto.getPhoneNumber()) && !StringUtils.equals(dto.getPhoneNumber(), existingBrand.getPhoneNumber())) {
            if (StringUtils.isBlank(dto.getVerifiedCode())) {
                throw new ServiceException(Code.PARAM_ERROR);
            }
            CaptchaVerifyDto verifyDto = new CaptchaVerifyDto();
            verifyDto.setPhone(dto.getPhoneNumber());
            verifyDto.setCaptcha(dto.getVerifiedCode());
            if (!captchaService.verify(verifyDto)) {
                throw new ServiceException(Code.VERIFIED_CODE_FAILED);
            }
            updateEntity.setPhoneNumber(dto.getPhoneNumber());
            updateEntity.setVerifiedAt(LocalDateTime.now());
            updateEntity.setPhoneVerified(1);
            needUpdate = true;
        }
        // 其余信息
        if (StringUtils.isNotBlank(dto.getUsername()) && !StringUtils.equals(dto.getUsername(), existingBrand.getUsername())) {
            // 检查用户名是否已存在（排除当前用户自己）
            LambdaQueryWrapper<BrandEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BrandEntity::getUsername, dto.getUsername())
                    .ne(BrandEntity::getUserId, existingBrand.getUserId()); // 排除自己

            Long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new ServiceException(Code.USER_EXIST);
            }
            updateEntity.setUsername(dto.getUsername());
            needUpdate = true;
        }

        return needUpdate;
    }

}
