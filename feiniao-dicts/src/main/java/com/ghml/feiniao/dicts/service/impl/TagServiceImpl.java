package com.ghml.feiniao.dicts.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.entity.TagEntity;
import com.ghml.feiniao.common.mapper.TagMapper;
import com.ghml.feiniao.common.vo.TagVo;
import com.ghml.feiniao.dicts.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 22:49
 * @description
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, TagEntity> implements TagService {
    @Override
    public List<TagVo> getTags() {
        List<TagEntity> entities = this.list();
        return convertToVoList(entities);
    }

    // 将Entity列表转换为Vo列表
    private List<TagVo> convertToVoList(List<TagEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        // 转换每个Entity为VO
        return entities.stream().map(entity -> TagVo.builder()
                .tagId(entity.getTagId())
                .tagName(entity.getTagName())
                .build()
        ).collect(Collectors.toList());
    }
}
