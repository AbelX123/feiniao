package com.ghml.feiniao.users.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.entity.CreatorPlatformEntity;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-09 13:16
 * @description
 */
public interface CreatorPlatformService extends IService<CreatorPlatformEntity> {
    void removeAndSaveBatch(List<CreatorPlatformEntity> cps);
}
