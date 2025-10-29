package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 22:43
 * @description
 */
@Data
@TableName("model_tag")
public class TagEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId("tag_id")
    private Integer tagId;
    private String tagName;
}
