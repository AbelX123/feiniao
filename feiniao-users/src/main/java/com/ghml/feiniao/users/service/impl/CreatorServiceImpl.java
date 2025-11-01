package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.constants.Bucket;
import com.ghml.feiniao.common.constants.Gender;
import com.ghml.feiniao.common.dto.CreatorDto;
import com.ghml.feiniao.common.entity.CreatorEntity;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.mapper.CreatorMapper;
import com.ghml.feiniao.common.utils.PageResult;
import com.ghml.feiniao.common.vo.CreatorDetailVo;
import com.ghml.feiniao.common.vo.CreatorVo;
import com.ghml.feiniao.security.utils.SecurityUtils;
import com.ghml.feiniao.users.config.MinIOConfig;
import com.ghml.feiniao.users.service.CreatorService;
import com.ghml.feiniao.users.utils.MinIOUtils;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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
    private final MinIOConfig minIOConfig;

    public CreatorServiceImpl(CreatorMapper creatorMapper,
                              MinioClient minioClient,
                              MinIOConfig minIOConfig) {
        this.creatorMapper = creatorMapper;
        this.minioClient = minioClient;
        this.minIOConfig = minIOConfig;
    }

    // 多条件分页查询创作者
    @Override
    public PageResult<CreatorVo> selectCreatorsByConditions(CreatorDto query) {
        // 创建分页对象
        Page<CreatorEntity> page = Page.of(query.getPageNum(), query.getPageSize());
        // 执行查询
        Page<CreatorEntity> result = creatorMapper.selectCreatorsByConditions(page, query);
        // 将entity转换为vo
        List<CreatorVo> vos = convertToVoList(result.getRecords());
        // 构建返回结果
        return PageResult.<CreatorVo>builder()
                .records(vos)
                .total(result.getTotal())
                .current(result.getCurrent())
                .size(result.getSize())
                .pages(result.getPages())
                .build();
    }

    // 查询创作者详情
    @Override
    public CreatorDetailVo getCreatorById(String creatorId) {
        Optional<CreatorEntity> opt = creatorMapper.getOptByCreatorId(creatorId);
        if (opt.isEmpty()) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }
        CreatorEntity entity = opt.get();
        // 查询模特类别
        List<String> types = creatorMapper.getModelTypesById(creatorId);
        // 查询平台
        List<String> platforms = creatorMapper.getPlatforms(creatorId);
        // 查询擅长品类
        List<String> specialties = creatorMapper.getSpecialties(creatorId);
        // 查询模特标签
        List<String> tags = creatorMapper.getTags(creatorId);
        // 查询案例
        List<CreatorDetailVo.CaseVo> caseVos = creatorMapper.getCaseVos(creatorId);
        // 构建vo
        return CreatorDetailVo.builder()
                .creatorId(creatorId)
                .creatorName(entity.getUsername())
                .countryName(entity.getCountryName())
                .gender(Gender.getDescByCode(entity.getGender()))
                .modelTypes(types)
                .platforms(platforms)
                .specialties(specialties)
                .tags(tags)
                .caseVos(caseVos)
                .build();
    }

    // 收藏创作者
    @Override
    public void followCreator(String creatorId) {
        // 查询产品主编号
        Optional<String> brandIdOpt = SecurityUtils.getCurrentUserIdOptional();
        if (brandIdOpt.isEmpty()) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }
        // 检查creatorId存在
        Optional<CreatorEntity> opt = this.getOptById(creatorId);
        if (opt.isEmpty()) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }
        // 重复收藏，幂等处理，静默成功
        boolean isExist = creatorMapper.existsFavoriteRelation(brandIdOpt.get(), creatorId);
        if (isExist) {
            return;
        }
        try {
            creatorMapper.saveBrandCreator(brandIdOpt.get(), creatorId);
        } catch (Exception e) {
            log.error("收藏创作者数据库操作错误:{}", e.getMessage());
            throw new ServiceException(Code.OPERATION_FAILED);
        }
    }

    // 取消收藏创作者
    @Override
    public void unfollowCreator(String creatorId) {
        // 查询产品主编号
        Optional<String> brandIdOpt = SecurityUtils.getCurrentUserIdOptional();
        if (brandIdOpt.isEmpty()) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }
        // 检查creatorId存在
        Optional<CreatorEntity> opt = this.getOptById(creatorId);
        if (opt.isEmpty()) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }
        // 重复取消，幂等处理，静默成功
        boolean isExist = creatorMapper.existsFavoriteRelation(brandIdOpt.get(), creatorId);
        if (!isExist) {
            return;
        }
        try {
            creatorMapper.deleteBrandCreator(brandIdOpt.get(), creatorId);
        } catch (Exception e) {
            log.error("取消创作者数据库操作错误:{}", e.getMessage());
            throw new ServiceException(Code.OPERATION_FAILED);
        }
    }

    // 通过产品主编号分页获取收藏的创作者
    @Override
    public PageResult<CreatorVo> favoriteCreators(CreatorDto dto) {
        // 获取产品主编号
        String brandId = SecurityUtils.getCurrentUserId();
        // 创建分页对象
        Page<CreatorEntity> page = Page.of(dto.getPageNum(), dto.getPageSize());
        // 执行查询
        Page<CreatorEntity> result = creatorMapper.favoriteCreators(page, brandId);
        // 转换每个entity为vo
        List<CreatorVo> vos = result.getRecords().stream()
                .map(entity -> CreatorVo.builder()
                        .userId(entity.getUserId())
                        .username(entity.getUsername())
                        .videoPrice(entity.getVideoPrice())
                        .countryName(entity.getCountryName())
                        .gender(Gender.getDescByCode(entity.getGender()))
                        .ageRange(entity.getAgeRangeDesc())
                        .build()).toList();
        // 构建返回结果
        return PageResult.<CreatorVo>builder()
                .records(vos)
                .total(result.getTotal())
                .current(result.getCurrent())
                .size(result.getSize())
                .pages(result.getPages())
                .build();
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

    // 将Entity列表转换为VO列表
    private List<CreatorVo> convertToVoList(List<CreatorEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        // 转换每个Entity为VO
        return entities.stream().map(entity -> CreatorVo.builder()
                .userId(entity.getUserId())
                .username(entity.getUsername())
                .videoPrice(entity.getVideoPrice())
                .countryCode(entity.getCountryCode())
                .gender(Gender.getDescByCode(entity.getGender()))
                .ageRange(entity.getAgeRange())
                .countryName(entity.getCountryName())
                .build()
        ).collect(Collectors.toList());
    }
}
