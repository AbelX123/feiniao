package com.ghml.feiniao.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghml.feiniao.common.dto.CreatorDto;
import com.ghml.feiniao.common.entity.CreatorEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Mapper
public interface CreatorMapper extends BaseMapper<CreatorEntity> {

    // 多条件分页查询创作者
    Page<CreatorEntity> selectCreatorsByConditions(@Param("page") Page<CreatorEntity> page,
                                                   @Param("query") CreatorDto query);
}
