package com.ghml.feiniao.users.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.dto.CreatorDto;
import com.ghml.feiniao.common.entity.CreatorEntity;
import com.ghml.feiniao.common.utils.PageResult;
import com.ghml.feiniao.common.vo.CreatorVo;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
public interface CreatorService extends IService<CreatorEntity> {
    PageResult<CreatorVo> selectCreatorsByConditions(CreatorDto creatorDto);
}
