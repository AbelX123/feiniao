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
import com.ghml.feiniao.common.vo.CreatorVo;
import com.ghml.feiniao.security.utils.SecurityUtils;
import com.ghml.feiniao.users.service.*;
import com.ghml.feiniao.users.utils.MinIOUtils;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class CreatorServiceImpl extends ServiceImpl<CreatorMapper, CreatorEntity> implements CreatorService {

    private final CreatorMapper creatorMapper;
    private final MinioClient minioClient;
    private final RedisService redisService;
    private final TransactionTemplate transactionTemplate;
    private final CreatorPlatformService creatorPlatformService;
    private final CreatorTypeService creatorTypeService;
    private final CreatorTagService creatorTagService;
    private final CreatorSpecialtyService creatorSpecialtyService;

    public CreatorServiceImpl(CreatorMapper creatorMapper, MinioClient minioClient, RedisService redisService, TransactionTemplate transactionTemplate, CreatorPlatformService creatorPlatformService, CreatorTypeService creatorTypeService, CreatorTagService creatorTagService, CreatorSpecialtyService creatorSpecialtyService) {
        this.creatorMapper = creatorMapper;
        this.minioClient = minioClient;
        this.redisService = redisService;
        this.transactionTemplate = transactionTemplate;
        this.creatorPlatformService = creatorPlatformService;
        this.creatorTypeService = creatorTypeService;
        this.creatorTagService = creatorTagService;
        this.creatorSpecialtyService = creatorSpecialtyService;
    }

    // 多条件分页查询创作者
    @Override
    public PageResult<CreatorVo> selectCreatorsByConditions(CreatorsDto query) {
        // 创建分页对象
        Page<CreatorEntity> page = Page.of(query.getPageNum(), query.getPageSize());
        // 执行查询
        Page<CreatorEntity> result = creatorMapper.selectCreatorsByConditions(page, query);
        // 将entity转换为vo
        List<CreatorVo> vos = convertToVoList(result.getRecords());
        // 构建返回结果
        return PageResult.<CreatorVo>builder().records(vos).total(result.getTotal()).current(result.getCurrent()).size(result.getSize()).pages(result.getPages()).build();
    }

    // 查询创作者类别
    @Override
    public List<String> getModelTypesById(String creatorId) {
        return creatorMapper.getModelTypesById(creatorId);
    }

    // 查询创作者平台
    @Override
    public List<String> getPlatforms(String creatorId) {
        return creatorMapper.getPlatforms(creatorId);
    }

    // 查询擅长品类
    @Override
    public List<String> getSpecialties(String creatorId) {
        return creatorMapper.getSpecialties(creatorId);
    }

    // 查询模特标签
    @Override
    public List<String> getTags(String creatorId) {
        return creatorMapper.getTags(creatorId);
    }

    // 查询案例
    @Override
    public List<CreatorVo.CaseVo> getCaseVos(String creatorId) {
        return creatorMapper.getCaseVos(creatorId);
    }

    // 更新创作者信息
    @Override
    public CreatorVo patchCreator(CreatorDto dto) {
        // 获取当前用户id
        final String creatorId = SecurityUtils.getCurrentUserId();

        // 处理基本更新
        CreatorEntity entity = new CreatorEntity();
        // 如果存在手机号更新，验证验证码状态
        if (!dto.getPhoneFull().isEmpty()) {
            String key = String.format(RedisPrefix.PREFIX_PHONE_VERIFIED_CODE, dto.getPhoneFull(), creatorId);
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
    public CreatorVo getCreatorById(String creatorId) {
        Optional<CreatorEntity> opt = creatorMapper.getOptByCreatorId(creatorId);
        if (opt.isEmpty()) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }
        CreatorEntity entity = opt.get();
        // 查询类别
        List<String> types = this.getModelTypesById(creatorId);
        // 查询平台
        List<String> platforms = this.getPlatforms(creatorId);
        // 查询擅长品类
        List<String> specialties = this.getSpecialties(creatorId);
        // 查询模特标签
        List<String> tags = this.getTags(creatorId);
        // 查询案例
        List<CreatorVo.CaseVo> caseVos = this.getCaseVos(creatorId);
        // 构建vo
        return CreatorVo.builder().userId(creatorId).username(entity.getUsername()).countryName(entity.getCountryName()).gender(Gender.getDescByCode(entity.getGender())).modelTypes(types).platforms(platforms).specialties(specialties).tags(tags).caseVos(caseVos).build();
    }

    // 通过产品主编号分页获取收藏的创作者
    @Override
    public PageResult<CreatorVo> favoriteCreators(CreatorsDto dto) {
        // 获取产品主编号
        String brandId = SecurityUtils.getCurrentUserId();
        // 创建分页对象
        Page<CreatorEntity> page = Page.of(dto.getPageNum(), dto.getPageSize());
        // 执行查询
        Page<CreatorEntity> result = creatorMapper.favoriteCreators(page, brandId);
        // 转换每个entity为vo
        List<CreatorVo> vos = result.getRecords().stream().map(entity -> CreatorVo.builder().userId(entity.getUserId()).username(entity.getUsername()).videoPrice(entity.getVideoPrice()).countryName(entity.getCountryName()).gender(Gender.getDescByCode(entity.getGender())).ageRange(entity.getAgeRangeDesc()).build()).toList();
        // 构建返回结果
        return PageResult.<CreatorVo>builder().records(vos).total(result.getTotal()).current(result.getCurrent()).size(result.getSize()).pages(result.getPages()).build();
    }

    // 上传视频到OSS
    @Override
    public String uploadVideo(MultipartFile video) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        String filename = currentUserId + "." + StringUtils.substringAfter(video.getOriginalFilename(), ".");
        try {
            return MinIOUtils.uploadFile(minioClient, video, Bucket.VIDEOS.getName(), filename);
        } catch (Exception e) {
            log.error("OSS视频上传失败:{}", e.getMessage());
            throw new ServiceException(Code.OSS_ERROR);
        }
    }

    @Override
    public void register(CreatorEntity creator) {
        this.save(creator);
    }

    // 将Entity列表转换为VO列表
    private List<CreatorVo> convertToVoList(List<CreatorEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        // 转换每个Entity为VO
        return entities.stream().map(entity -> CreatorVo.builder().userId(entity.getUserId()).username(entity.getUsername()).videoPrice(entity.getVideoPrice()).countryCode(entity.getCountryCode()).gender(Gender.getDescByCode(entity.getGender())).ageRange(entity.getAgeRange()).countryName(entity.getCountryName()).build()).collect(Collectors.toList());
    }
}
