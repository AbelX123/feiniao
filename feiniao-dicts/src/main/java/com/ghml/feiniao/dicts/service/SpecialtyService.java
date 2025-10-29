package com.ghml.feiniao.dicts.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.entity.SpecialtyEntity;
import com.ghml.feiniao.common.vo.SpecialtyVo;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 11:05
 * @description
 */
public interface SpecialtyService extends IService<SpecialtyEntity> {
    List<SpecialtyVo> getSpecialties();
}
