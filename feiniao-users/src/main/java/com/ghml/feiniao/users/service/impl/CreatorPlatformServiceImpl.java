package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.entity.CreatorPlatformEntity;
import com.ghml.feiniao.common.mapper.CreatorPlatformMapper;
import com.ghml.feiniao.users.service.CreatorPlatformService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-09 13:17
 * @description
 */
@Service
public class CreatorPlatformServiceImpl extends ServiceImpl<CreatorPlatformMapper, CreatorPlatformEntity> implements CreatorPlatformService {

    private final CreatorPlatformMapper creatorPlatformMapper;

    public CreatorPlatformServiceImpl(CreatorPlatformMapper creatorPlatformMapper) {
        this.creatorPlatformMapper = creatorPlatformMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeAndSaveBatch(List<CreatorPlatformEntity> cps) {
        if (CollectionUtils.isEmpty(cps)) {
            return;
        }
        // 删除
        List<String> creatorIds = cps.stream()
                .map(CreatorPlatformEntity::getCreatorId)
                .distinct()
                .collect(Collectors.toList());
        creatorPlatformMapper.deleteBatchIds(creatorIds);

        // 分批插入
        final int BATCH_SIZE = 1000; // 每批次处理的记录数
        for (int i = 0; i < cps.size(); i += BATCH_SIZE) {
            List<CreatorPlatformEntity> batchList = cps.subList(i, Math.min(i + BATCH_SIZE, cps.size()));
            this.saveBatch(batchList, 1000);
        }
    }
}
