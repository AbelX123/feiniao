package com.ghml.feiniao.users.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.entity.CreatorTagEntity;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-09 13:36
 * @description
 */
public interface CreatorTagService extends IService<CreatorTagEntity> {
    void removeAndSaveBatch(List<CreatorTagEntity> cTags);
}
