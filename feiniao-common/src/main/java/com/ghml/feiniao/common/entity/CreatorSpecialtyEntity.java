package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-09 13:49
 * @description
 */
@Data
@TableName(value = "creator_specialty_mapping")
public class CreatorSpecialtyEntity {

    @TableId("creator_id")
    private String creatorId;
    private Integer specialtyId;
}
