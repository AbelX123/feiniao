package com.ghml.feiniao.users.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.entity.CreatorTypeEntity;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-09 13:28
 * @description
 */
public interface CreatorTypeService extends IService<CreatorTypeEntity> {
    void removeAndSaveBatch(List<CreatorTypeEntity> cts);
}
