package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.constants.Bucket;
import com.ghml.feiniao.common.entity.BrandEntity;
import com.ghml.feiniao.common.entity.CreatorEntity;
import com.ghml.feiniao.common.entity.FavoriteEntity;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.mapper.BrandMapper;
import com.ghml.feiniao.common.vo.BrandVo;
import com.ghml.feiniao.security.utils.SecurityUtils;
import com.ghml.feiniao.users.service.BrandService;
import com.ghml.feiniao.users.service.CreatorService;
import com.ghml.feiniao.users.service.FavoriteService;
import com.ghml.feiniao.users.utils.MinIOUtils;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Slf4j
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, BrandEntity> implements BrandService {

    private final MinioClient minioClient;

    private CreatorService creatorService;
    private FavoriteService favoriteService;

    @Autowired
    public void setFavoriteService(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Autowired
    public void setCreatorService(CreatorService creatorService) {
        this.creatorService = creatorService;
    }

    public BrandServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    // 根据编号查询产品主信息
    @Override
    public BrandVo getBrandById() {
        Optional<BrandEntity> opt = this.getOptById(SecurityUtils.getCurrentUserId());
        if (opt.isEmpty()) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }
        BrandEntity entity = opt.get();
        return BrandVo.builder()
                .userId(SecurityUtils.getCurrentUserId())
                .username(entity.getUsername())
                .phone(entity.getPhoneNumber())
                .avatar(entity.getAvatar())
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


    // 上传头像到OSS
    @Override
    public String uploadAvatar(MultipartFile file) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        String filename = currentUserId + "." + StringUtils.substringAfter(file.getOriginalFilename(), ".");
        try {
            return MinIOUtils.uploadFile(minioClient, file, Bucket.AVATARS.getName(), filename);
        } catch (Exception e) {
            log.error("OSS文件上传失败:{}", e.getMessage());
            throw new ServiceException(Code.OSS_ERROR);
        }
    }

    // 获取头像外链
    @Override
    public String getAvatarUrl(String filename) {
        // 校验filename和用户是否一致
        String currentUserId = SecurityUtils.getCurrentUserId();
        if (!StringUtils.startsWith(filename, currentUserId)) {
            throw new ServiceException(Code.OSS_NOT_EXIST);
        }
        try {
            return MinIOUtils.getObjectUrl(minioClient, Bucket.AVATARS.getName(), filename, 30);
        } catch (Exception e) {
            log.error("OSS外链获取失败:{}", e.getMessage());
            throw new ServiceException(Code.OSS_ERROR);
        }
    }

    @Override
    public void register(BrandEntity brandEntity) {
        this.save(brandEntity);
    }

}
