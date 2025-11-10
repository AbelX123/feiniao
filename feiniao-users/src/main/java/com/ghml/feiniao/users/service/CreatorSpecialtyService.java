package com.ghml.feiniao.users.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.entity.CreatorSpecialtyEntity;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-09 13:52
 * @description
 */
public interface CreatorSpecialtyService extends IService<CreatorSpecialtyEntity> {
    void removeAndSaveBatch(List<CreatorSpecialtyEntity> csEs);
}
