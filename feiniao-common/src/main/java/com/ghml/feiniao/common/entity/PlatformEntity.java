package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 23:10
 * @description 平台
 */
@Data
@TableName("platform")
public class PlatformEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId("platform_code")
    private String platformCode;

    private String platformName;

}
