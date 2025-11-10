package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.entity.CreatorTagEntity;
import com.ghml.feiniao.common.mapper.CreatorTagMapper;
import com.ghml.feiniao.users.service.CreatorTagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-09 13:37
 * @description
 */
@Service
public class CreatorTagServiceImpl
        extends ServiceImpl<CreatorTagMapper, CreatorTagEntity>
        implements CreatorTagService {

    private final CreatorTagMapper creatorTagMapper;

    public CreatorTagServiceImpl(CreatorTagMapper creatorTagMapper) {
        this.creatorTagMapper = creatorTagMapper;
    }


    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeAndSaveBatch(List<CreatorTagEntity> cTags) {
        // 删除
        List<String> creatorIds = cTags.stream()
                .map(CreatorTagEntity::getCreatorId)
                .distinct()
                .toList();
        creatorTagMapper.deleteBatchIds(creatorIds);

        // 分批插入
        final int BATCH_SIZE = 1000; // 每批次处理的记录数
        for (int i = 0; i < cTags.size(); i += BATCH_SIZE) {
            List<CreatorTagEntity> batchList = cTags.subList(i, Math.min(i + BATCH_SIZE, cTags.size()));
            this.saveBatch(batchList);
        }
    }
}
