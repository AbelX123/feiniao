package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sms_send_log")
public class SmsSendLogEntity {

    @TableId("id")
    private Long id;

    @TableField("phone")
    private String phone;

    @TableField("biz_type")
    private String bizType;

    @TableField("template_code")
    private String templateCode;

    @TableField("sign_name")
    private String signName;

    @TableField("provider")
    private String provider;

    @TableField("provider_code")
    private String providerCode;

    @TableField("provider_message")
    private String providerMessage;

    @TableField("request_id")
    private String requestId;

    @TableField("biz_id")
    private String bizId;

    @TableField("status")
    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
