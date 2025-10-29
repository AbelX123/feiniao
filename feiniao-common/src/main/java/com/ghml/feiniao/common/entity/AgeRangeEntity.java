package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 23:12
 * @description 年龄范围
 */
@Data
@TableName("age_range")
public class AgeRangeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String ageRange;

    private String ageRangeDesc;
}
