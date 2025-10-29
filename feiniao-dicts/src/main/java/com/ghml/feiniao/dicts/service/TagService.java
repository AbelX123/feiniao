package com.ghml.feiniao.dicts.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.entity.TagEntity;
import com.ghml.feiniao.common.vo.TagVo;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 22:48
 * @description
 */
public interface TagService extends IService<TagEntity> {
    List<TagVo> getTags();
}
