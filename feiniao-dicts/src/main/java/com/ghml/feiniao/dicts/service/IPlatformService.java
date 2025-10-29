package com.ghml.feiniao.dicts.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.entity.PlatformEntity;
import com.ghml.feiniao.common.vo.PlatformVo;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 23:51
 * @description
 */
public interface IPlatformService extends IService<PlatformEntity> {
    List<PlatformVo> getPlatforms();
}
