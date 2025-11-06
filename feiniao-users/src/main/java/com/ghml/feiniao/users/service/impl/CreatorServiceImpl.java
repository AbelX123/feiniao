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
import com.ghml.feiniao.common.vo.CreatorVo;
import com.ghml.feiniao.security.utils.SecurityUtils;
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

    public CreatorServiceImpl(CreatorMapper creatorMapper,
                              MinioClient minioClient) {
        this.creatorMapper = creatorMapper;
        this.minioClient = minioClient;
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
        return CreatorVo.builder()
                .userId(creatorId)
                .username(entity.getUsername())
                .countryName(entity.getCountryName())
                .gender(Gender.getDescByCode(entity.getGender()))
                .modelTypes(types)
                .platforms(platforms)
                .specialties(specialties)
                .tags(tags)
                .caseVos(caseVos)
                .build();
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
