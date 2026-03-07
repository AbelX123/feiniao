package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.entity.CreatorSpecialtyEntity;
import com.ghml.feiniao.common.mapper.CreatorSpecialtyMapper;
import com.ghml.feiniao.users.service.CreatorSpecialtyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-09 13:52
 * @description
 */
@Service
public class CreatorSpecialtyServiceImpl
        extends ServiceImpl<CreatorSpecialtyMapper, CreatorSpecialtyEntity>
        implements CreatorSpecialtyService {

    private final CreatorSpecialtyMapper creatorSpecialtyMapper;

    public CreatorSpecialtyServiceImpl(CreatorSpecialtyMapper creatorSpecialtyMapper) {
        this.creatorSpecialtyMapper = creatorSpecialtyMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeAndSaveBatch(List<CreatorSpecialtyEntity> csEs) {
        if (CollectionUtils.isEmpty(csEs)) {
            return;
        }
        // 删除
        List<String> creatorIds = csEs.stream()
                .map(CreatorSpecialtyEntity::getCreatorId)
                .distinct()
                .toList();
        creatorSpecialtyMapper.deleteBatchIds(creatorIds);

        // 分批插入
        final int BATCH_SIZE = 1000; // 每批次处理的记录数
        for (int i = 0; i < csEs.size(); i += BATCH_SIZE) {
            List<CreatorSpecialtyEntity> batchList = csEs.subList(i, Math.min(i + BATCH_SIZE, csEs.size()));
            this.saveBatch(batchList);
        }
    }
}
