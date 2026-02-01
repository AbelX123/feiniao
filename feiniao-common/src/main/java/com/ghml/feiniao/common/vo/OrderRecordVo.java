package com.ghml.feiniao.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderRecordVo implements Serializable {
    private String orderId; // 订单编号
    private LocalDateTime createTime; // 订单创建时间
    private String coverUrl; // 头像链接
    private Integer genderCode; // 性别编码
    private String gender; // 性别描述
    private String ageRangeDesc; // 年龄
    private String countryName; // 国籍
    private String productName; // 产品名称
    private String platformName; // 使用平台
    private Integer videoNum; // 视频数量
    private String productDetails; // 产品卖点
    private Integer videoFormat; // 视频格式
    private BigDecimal orderAmount; // 订单金额
    private Integer orderStatus; // 订单状态
}
