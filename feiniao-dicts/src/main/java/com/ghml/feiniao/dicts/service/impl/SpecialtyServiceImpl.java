package com.ghml.feiniao.dicts.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.entity.SpecialtyEntity;
import com.ghml.feiniao.common.mapper.SpecialtyMapper;
import com.ghml.feiniao.common.vo.SpecialtyVo;
import com.ghml.feiniao.dicts.service.SpecialtyService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 11:09
 * @description
 */
@Service
public class SpecialtyServiceImpl extends ServiceImpl<SpecialtyMapper, SpecialtyEntity> implements SpecialtyService {
    @Override
    public List<SpecialtyVo> getSpecialties() {
        List<SpecialtyEntity> entities = this.list();
        return convertToVoList(entities);
    }

    // 将Entity列表转换为VO列表
    private List<SpecialtyVo> convertToVoList(List<SpecialtyEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        // 转换每个Entity为VO
        return entities.stream().map(entity -> SpecialtyVo.builder()
                .specialtyId(entity.getSpecialtyId())
                .specialtyName(entity.getSpecialtyName())
                .build()
        ).collect(Collectors.toList());
    }
}
