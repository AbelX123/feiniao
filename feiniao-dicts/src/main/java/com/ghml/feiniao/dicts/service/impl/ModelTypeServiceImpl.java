package com.ghml.feiniao.dicts.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.entity.ModelTypeEntity;
import com.ghml.feiniao.common.mapper.ModelTypeMapper;
import com.ghml.feiniao.common.vo.ModelTypeVo;
import com.ghml.feiniao.dicts.service.IModelTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 23:24
 * @description
 */
@Slf4j
@Service
public class ModelTypeServiceImpl extends ServiceImpl<ModelTypeMapper, ModelTypeEntity> implements IModelTypeService {
    @Override
    public List<ModelTypeVo> getModelTypes() {
        List<ModelTypeEntity> entities = this.list();
        return convertToVoList(entities);
    }

    // 将Entity列表转换为VO列表
    private List<ModelTypeVo> convertToVoList(List<ModelTypeEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        // 转换每个Entity为VO
        return entities.stream().map(entity -> ModelTypeVo.builder()
                .modelTypeId(entity.getModelTypeId())
                .modelTypeName(entity.getModelTypeName())
                .build()).collect(Collectors.toList());
    }
}
