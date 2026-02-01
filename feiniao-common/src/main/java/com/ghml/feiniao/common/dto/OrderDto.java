package com.ghml.feiniao.common.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDto {
    private String productName;
    private String productDetails;
    private Integer videoFormat;
    private Integer videoNum;
    private String creatorId;
    private String platformCode;
    private BigDecimal orderAmount;
}
