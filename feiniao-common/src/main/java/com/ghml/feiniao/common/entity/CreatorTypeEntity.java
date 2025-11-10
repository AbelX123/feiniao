package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-09 13:26
 * @description
 */
@Data
@TableName(value = "creator_type_mapping")
public class CreatorTypeEntity {

    @TableId("creator_id")
    private String creatorId;
    private Integer modelTypeId;
}
