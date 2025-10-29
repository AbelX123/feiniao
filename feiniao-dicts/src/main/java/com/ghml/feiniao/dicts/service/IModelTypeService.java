package com.ghml.feiniao.dicts.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.entity.ModeTypeEntity;
import com.ghml.feiniao.common.vo.ModelTypeVo;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 23:23
 * @description
 */
public interface IModelTypeService extends IService<ModeTypeEntity> {
    List<ModelTypeVo> getModelTypes();
}
