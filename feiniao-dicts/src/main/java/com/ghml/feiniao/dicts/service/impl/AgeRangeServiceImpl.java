package com.ghml.feiniao.dicts.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.entity.AgeRangeEntity;
import com.ghml.feiniao.common.mapper.AgeRangeMapper;
import com.ghml.feiniao.common.vo.AgeRangeVo;
import com.ghml.feiniao.dicts.service.AgeRangeService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 10:50
 * @description
 */
@Service
public class AgeRangeServiceImpl extends ServiceImpl<AgeRangeMapper, AgeRangeEntity> implements AgeRangeService {
    @Override
    public List<AgeRangeVo> getAgeRanges() {
        List<AgeRangeEntity> entities = this.list();
        return convertToVoList(entities);
    }

    // 将Entity列表转换为VO列表
    private List<AgeRangeVo> convertToVoList(List<AgeRangeEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        // 转换每个Entity为VO
        return entities.stream().map(entity -> AgeRangeVo.builder()
                .ageRange(entity.getAgeRange())
                .ageRangeDesc(entity.getAgeRangeDesc())
                .build()
        ).collect(Collectors.toList());
    }
}
