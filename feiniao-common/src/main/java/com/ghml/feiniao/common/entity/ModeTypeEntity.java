package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 23:06
 * @description 模特类型
 */
@Data
@TableName("model_type")
public class ModeTypeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer modelTypeId;

    private String modelTypeName;
}
