package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.constants.Bucket;
import com.ghml.feiniao.common.constants.Gender;
import com.ghml.feiniao.common.constants.PhoneStatus;
import com.ghml.feiniao.common.constants.RedisPrefix;
import com.ghml.feiniao.common.dto.CreatorDto;
import com.ghml.feiniao.common.dto.CreatorsDto;
import com.ghml.feiniao.common.entity.*;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.mapper.CreatorMapper;
import com.ghml.feiniao.common.service.RedisService;
import com.ghml.feiniao.common.utils.EntityUpdateHelper;
import com.ghml.feiniao.common.utils.PageResult;
import com.ghml.feiniao.common.vo.*;
import com.ghml.feiniao.security.utils.SecurityUtils;
import com.ghml.feiniao.users.config.MinIOProps;
import com.ghml.feiniao.users.service.*;
import com.ghml.feiniao.users.utils.MinIOUtils;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreatorServiceImpl extends ServiceImpl<CreatorMapper, CreatorEntity> implements CreatorService {

    private final CreatorMapper creatorMapper;
    private final RedisService redisService;
    private final TransactionTemplate transactionTemplate;
    private final CreatorPlatformService creatorPlatformService;
    private final CreatorTypeService creatorTypeService;
    private final CreatorTagService creatorTagService;
    private final CreatorSpecialtyService creatorSpecialtyService;
    private final CreatorCaseService creatorCaseService;
    private final MinioClient minioClient;
    private final MinIOProps minIOProps;

    // 多条件分页查询创作者（支持免鉴权调用，如 MCP 工具）
    @Override
    public PageResult<CreatorDisplayVo> selectCreatorsByConditions(CreatorsDto query) {
        // 获取当前用户，未登录时为 null（isFavorite 均为 0）
        String currentUserId = SecurityUtils.getCurrentUserIdOrNull();
        // 创建分页对象
        Page<CreatorEntity> page = Page.of(query.getPageNum(), query.getPageSize());
        // 执行查询
        Page<CreatorEntity> result = creatorMapper.selectCreatorsByConditions(page, query, currentUserId);
        // 将entity转换为vo
        List<CreatorDisplayVo> vos = convertToVoList(result.getRecords());
        // 构建返回结果
        return PageResult.<CreatorDisplayVo>builder()
                .records(vos)
                .total(result.getTotal())
                .current(result.getCurrent())
                .size(result.getSize())
                .pages(result.getPages())
                .build();
    }

    // 查询创作者类别
    @Override
    public List<ModelTypeVo> getModelTypesById(String creatorId) {
        List<ModelTypeEntity> modelTypes = creatorMapper.getModelTypesById(creatorId);
        return modelTypes.stream()
                .map(type -> ModelTypeVo.builder()
                        .modelTypeId(type.getModelTypeId())
                        .modelTypeName(type.getModelTypeName())
                        .build())
                .toList();
    }

    // 查询创作者平台
    @Override
    public List<PlatformVo> getPlatforms(String creatorId) {
        List<PlatformEntity> platforms = creatorMapper.getPlatforms(creatorId);
        return platforms.stream()
                .map(platform -> PlatformVo.builder()
                        .platformCode(platform.getPlatformCode())
                        .platformName(platform.getPlatformName())
                        .build())
                .toList();
    }

    // 查询擅长品类
    @Override
    public List<SpecialtyVo> getSpecialties(String creatorId) {
        List<SpecialtyEntity> specialties = creatorMapper.getSpecialties(creatorId);
        return specialties.stream()
                .map(specialty -> SpecialtyVo.builder()
                        .specialtyId(specialty.getSpecialtyId())
                        .specialtyName(specialty.getSpecialtyName())
                        .build())
                .toList();
    }

    // 查询模特标签
    @Override
    public List<TagVo> getTags(String creatorId) {
        List<TagEntity> tags = creatorMapper.getTags(creatorId);
        return tags.stream()
                .map(tag -> TagVo.builder()
                        .tagId(tag.getTagId())
                        .tagName(tag.getTagName())
                        .build()).toList();
    }

    // 更新创作者信息
    @Override
    public CreatorDetailsVo patchCreator(CreatorDto dto) {
        // 获取当前用户id
        final String creatorId = SecurityUtils.getCurrentUserId();

        // 处理基本更新
        CreatorEntity entity = new CreatorEntity();
        entity.setUserId(creatorId);
        // 如果存在手机号更新，验证验证码状态
        if (StringUtils.isNotBlank(dto.getPhoneFull())) {
            String key = RedisPrefix.PREFIX_PHONE_VERIFIED_CODE + dto.getPhoneFull();
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
        EntityUpdateHelper.setIfNotBlank(dto::getUsername, entity::setUsername);
        EntityUpdateHelper.setIfNotNull(dto::getVideoPrice, entity::setVideoPrice);
        EntityUpdateHelper.setIfNotBlank(dto::getCountryCode, entity::setCountryCode);
        EntityUpdateHelper.setIfNotNull(dto::getGender, entity::setGender);
        EntityUpdateHelper.setIfNotBlank(dto::getAgeRange, entity::setAgeRange);
        EntityUpdateHelper.setIfNotNull(dto::getIsAvailable, entity::setIsAvailable);

        // 处理平台关系更新
        List<CreatorPlatformEntity> cps = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dto.getPlatformCodes())) {
            List<String> platformCodes = dto.getPlatformCodes();
            platformCodes.forEach(code -> {
                CreatorPlatformEntity cpE = new CreatorPlatformEntity();
                cpE.setCreatorId(creatorId);
                cpE.setPlatformCode(code);
                cps.add(cpE);
            });
        }

        // 处理类型更新
        List<CreatorTypeEntity> cts = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dto.getModelTypeIds())) {
            List<Integer> types = dto.getModelTypeIds();
            types.forEach(type -> {
                CreatorTypeEntity ctE = new CreatorTypeEntity();
                ctE.setCreatorId(creatorId);
                ctE.setModelTypeId(type);
                cts.add(ctE);
            });
        }

        // 处理标签更新
        List<CreatorTagEntity> cTags = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dto.getModelTagIds())) {
            List<Integer> tags = dto.getModelTagIds();
            tags.forEach(tag -> {
                CreatorTagEntity cTagE = new CreatorTagEntity();
                cTagE.setCreatorId(creatorId);
                cTagE.setTagId(tag);
                cTags.add(cTagE);
            });
        }

        // 处理品类更新
        List<CreatorSpecialtyEntity> csEs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dto.getSpecialtyIds())) {
            List<Integer> specialties = dto.getSpecialtyIds();
            specialties.forEach(specialty -> {
                CreatorSpecialtyEntity csE = new CreatorSpecialtyEntity();
                csE.setCreatorId(creatorId);
                csE.setSpecialtyId(specialty);
                csEs.add(csE);
            });
        }

        // 开启事务更新
        transactionTemplate.executeWithoutResult(status -> {
            try {
                // 基本更新
                this.updateById(entity);
                // 平台关系更新
                creatorPlatformService.removeAndSaveBatch(cps);
                // 类型更新
                creatorTypeService.removeAndSaveBatch(cts);
                // 标签更新
                creatorTagService.removeAndSaveBatch(cTags);
                // 品类更新
                creatorSpecialtyService.removeAndSaveBatch(csEs);
            } catch (ServiceException e) {
                status.setRollbackOnly();
                log.warn("创作者[{}]更新业务异常: {}", creatorId, e.getMessage());
                throw e;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.warn("创作者[{}]更新系统异常: {}", creatorId, e.getMessage());
                throw new ServiceException(Code.OPERATION_FAILED);
            }
        });
        return this.getCreatorById(creatorId);
    }


    // 查询创作者详情
    @Override
    public CreatorDetailsVo getCreatorById(String creatorId) {
        Optional<CreatorEntity> opt = creatorMapper.getOptByCreatorId(creatorId);
        if (opt.isEmpty()) {
            throw new ServiceException(Code.CREATOR_NOT_EXIST);
        }
        CreatorEntity entity = opt.get();
        String avatarUrl = getOrRefreshAvatarUrl(entity, false);
        // 查询类别
        List<ModelTypeVo> types = this.getModelTypesById(creatorId);
        // 查询平台
        List<PlatformVo> platforms = this.getPlatforms(creatorId);
        // 查询擅长品类
        List<SpecialtyVo> specialties = this.getSpecialties(creatorId);
        // 查询模特标签
        List<TagVo> tags = this.getTags(creatorId);
        // 查询案例
        List<CaseVo> caseVos = creatorCaseService.getCases(creatorId);
        // 构建vo
        return CreatorDetailsVo.builder()
                .userId(creatorId)
                .username(entity.getUsername())
                .videoPrice(entity.getVideoPrice())
                .gender(Gender.getDescByCode(entity.getGender()))
                .ageRangeDesc(entity.getAgeRangeDesc())
                .isAvailable(entity.getIsAvailable())
                .countryName(entity.getCountryName())
                .coverUrl(avatarUrl)
                .modelTypes(types.stream().map(ModelTypeVo::getModelTypeName).toList())
                .platforms(platforms.stream().map(PlatformVo::getPlatformName).toList())
                .specialties(specialties.stream().map(SpecialtyVo::getSpecialtyName).toList())
                .tags(tags.stream().map(TagVo::getTagName).toList())
                .caseVos(caseVos)
                .build();
    }

    // 通过产品主编号分页获取收藏的创作者
    @Override
    public PageResult<CreatorDisplayVo> favoriteCreators(CreatorsDto dto) {
        // 获取产品主编号
        String brandId = SecurityUtils.getCurrentUserId();
        // 创建分页对象
        Page<CreatorEntity> page = Page.of(dto.getPageNum(), dto.getPageSize());
        // 执行查询
        Page<CreatorEntity> result = creatorMapper.favoriteCreators(page, brandId);
        // 将entity转换为vo
        List<CreatorDisplayVo> vos = convertToVoList(result.getRecords());
        // 构建返回结果
        return PageResult.<CreatorDisplayVo>builder()
                .records(vos)
                .total(result.getTotal())
                .current(result.getCurrent())
                .size(result.getSize())
                .pages(result.getPages())
                .build();
    }

    @Override
    public void register(CreatorEntity creator) {
        this.save(creator);
    }

    @Override
    public AvatarVo uploadAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException(Code.PARAM_ERROR);
        }
        String creatorId = SecurityUtils.getCurrentUserId();
        try {
            MinIOUtils.uploadFile(minioClient, file, Bucket.AVATARS.getName(), creatorId);
            return regenerateAvatarUrl(creatorId, true);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("上传创作者头像失败: {}", e.getMessage(), e);
            throw new ServiceException(Code.OSS_ERROR);
        }
    }

    @Override
    public AvatarVo getAvatarUrl() {
        String creatorId = SecurityUtils.getCurrentUserId();
        CreatorEntity creator = this.getById(creatorId);
        if (creator == null) {
            throw new ServiceException(Code.CREATOR_NOT_EXIST);
        }

        String avatarUrl = getOrRefreshAvatarUrl(creator, true);
        if (StringUtils.isBlank(avatarUrl)) {
            throw new ServiceException(Code.OSS_NOT_EXIST);
        }

        CreatorEntity refreshed = this.getById(creatorId);
        if (refreshed == null || refreshed.getAvatarUrlExpiry() == null) {
            throw new ServiceException(Code.OPERATION_FAILED);
        }

        return AvatarVo.builder()
                .avatar(avatarUrl)
                .expiry(toEpochMilli(refreshed.getAvatarUrlExpiry()))
                .build();
    }

    // 将Entity列表转换为VO列表
    private List<CreatorDisplayVo> convertToVoList(List<CreatorEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        // 转换每个Entity为VO
        return entities.stream()
                .map(entity -> CreatorDisplayVo.builder()
                        .userId(entity.getUserId()) // 模特编号
                        .username(entity.getUsername()) // 模特名称
                        .coverUrl(getOrRefreshAvatarUrl(entity, false)) // 模特展示头像
                        .videoPrice(entity.getVideoPrice()) // 拍摄价格
                        .gender(Gender.getDescByCode(entity.getGender())) // 性别
                        .countryName(entity.getCountryName()) // 国家名称
                        .ageRangeDesc(entity.getAgeRangeDesc()) // 年龄范围
                        .isAvailable(entity.getIsAvailable()) // 是否接单
                        .isFavorite(entity.getIsFavorite()) // 是否被收藏
                        .build())
                .collect(Collectors.toList());
    }

    private String getOrRefreshAvatarUrl(CreatorEntity entity, boolean failOnMissing) {
        if (entity == null || StringUtils.isBlank(entity.getUserId())) {
            if (failOnMissing) {
                throw new ServiceException(Code.PARAM_ERROR);
            }
            return null;
        }

        if (StringUtils.isNotBlank(entity.getAvatarUrl())
                && entity.getAvatarUrlExpiry() != null
                && LocalDateTime.now().isBefore(entity.getAvatarUrlExpiry())) {
            return entity.getAvatarUrl();
        }

        try {
            AvatarVo refreshed = regenerateAvatarUrl(entity.getUserId(), failOnMissing);
            return refreshed.getAvatar();
        } catch (ServiceException e) {
            if (failOnMissing) {
                throw e;
            }
            log.warn("刷新创作者头像链接失败, creatorId={}, reason={}", entity.getUserId(), e.getMessage());
            return entity.getAvatarUrl();
        } catch (Exception e) {
            if (failOnMissing) {
                throw new ServiceException(Code.OSS_ERROR);
            }
            log.warn("刷新创作者头像链接异常, creatorId={}, reason={}", entity.getUserId(), e.getMessage());
            return entity.getAvatarUrl();
        }
    }

    private AvatarVo regenerateAvatarUrl(String creatorId, boolean failOnMissing) throws Exception {
        if (MinIOUtils.objectExists(minioClient, Bucket.AVATARS.getName(), creatorId)) {
            if (failOnMissing) {
                throw new ServiceException(Code.OSS_NOT_EXIST);
            }
            return AvatarVo.builder().avatar(null).expiry(0L).build();
        }

        int expiryHours = minIOProps.getAvatarExpiry();
        String avatarUrl = MinIOUtils.getObjectUrl(
                minioClient,
                Bucket.AVATARS.getName(),
                creatorId,
                expiryHours
        );
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(expiryHours);

        CreatorEntity update = new CreatorEntity();
        update.setUserId(creatorId);
        update.setAvatarUrl(avatarUrl);
        update.setAvatarUrlExpiry(expiryTime);
        if (!this.updateById(update)) {
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
}
