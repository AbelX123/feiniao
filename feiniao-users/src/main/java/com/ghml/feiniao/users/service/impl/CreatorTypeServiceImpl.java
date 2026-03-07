package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.entity.CreatorTypeEntity;
import com.ghml.feiniao.common.mapper.CreatorTypeMapper;
import com.ghml.feiniao.users.service.CreatorTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-09 13:28
 * @description
 */
@Service
public class CreatorTypeServiceImpl
        extends ServiceImpl<CreatorTypeMapper, CreatorTypeEntity>
        implements CreatorTypeService {

    private final CreatorTypeMapper creatorTypeMapper;

    public CreatorTypeServiceImpl(CreatorTypeMapper creatorTypeMapper) {
        this.creatorTypeMapper = creatorTypeMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeAndSaveBatch(List<CreatorTypeEntity> cts) {
        if (CollectionUtils.isEmpty(cts)) {
            return;
        }
        // 删除
        List<String> creatorIds = cts.stream()
                .map(CreatorTypeEntity::getCreatorId)
                .distinct()
                .toList();
        creatorTypeMapper.deleteBatchIds(creatorIds);

        // 分批插入
        final int BATCH_SIZE = 1000;
        for (int i = 0; i < cts.size(); i += BATCH_SIZE) {
            List<CreatorTypeEntity> batchList = cts.subList(i, Math.min(i + BATCH_SIZE, cts.size()));
            this.saveBatch(batchList);
        }
    }
}
