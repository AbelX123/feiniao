package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.dto.CreatorDto;
import com.ghml.feiniao.common.entity.CreatorEntity;
import com.ghml.feiniao.common.mapper.CreatorMapper;
import com.ghml.feiniao.common.utils.PageResult;
import com.ghml.feiniao.common.vo.CreatorVo;
import com.ghml.feiniao.users.service.CreatorService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Service
public class CreatorServiceImpl extends ServiceImpl<CreatorMapper, CreatorEntity> implements CreatorService {

    private final CreatorMapper creatorMapper;

    public CreatorServiceImpl(CreatorMapper creatorMapper) {
        this.creatorMapper = creatorMapper;
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
                .gender(entity.getGender())
                .ageRange(entity.getAgeRange())
                .countryName(entity.getCountryName()).build()).collect(Collectors.toList());
    }
}
