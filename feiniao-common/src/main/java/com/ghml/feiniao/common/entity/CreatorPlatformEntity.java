package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-09 13:14
 * @description 创作者平台映射关系实体
 */
@Data
@TableName("creator_platform_mapping")
public class CreatorPlatformEntity {

    @TableId("creator_id")
    private String creatorId;
    private String platformCode;
}
