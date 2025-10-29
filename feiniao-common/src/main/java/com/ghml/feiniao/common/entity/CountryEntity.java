package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 23:16
 * @description
 */
@Data
@TableName("country")
public class CountryEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId("country_code")
    private String countryCode;

    private String countryName;
}
