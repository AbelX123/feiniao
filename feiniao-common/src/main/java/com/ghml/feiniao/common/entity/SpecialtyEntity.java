package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 23:15
 * @description 擅长品类
 */
@Data
@TableName("specialty")
public class SpecialtyEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer specialtyId;

    private String specialtyName;
}
