package com.ghml.feiniao.dicts.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.entity.AgeRangeEntity;
import com.ghml.feiniao.common.vo.AgeRangeVo;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 10:48
 * @description
 */
public interface AgeRangeService extends IService<AgeRangeEntity> {
    List<AgeRangeVo> getAgeRanges();
}
