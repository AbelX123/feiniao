package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_records")
public class OrderRecordEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id")
    private String orderId;

    @TableField(value = "product_name")
    private String productName;

    @TableField(value = "product_details")
    private String productDetails;

    @TableField(value = "video_format")
    private Integer videoFormat;

    @TableField(value = "video_num")
    private Integer videoNum;

    @TableField(value = "creator_id")
    private String creatorId;

    @TableField(value = "brand_id")
    private String brandId;

    @TableField(value = "platform_code")
    private String platformCode;

    @TableField(value = "order_amount")
    private BigDecimal orderAmount;

    @TableField(value = "order_status")
    private Integer orderStatus;

    @TableField(value = "create_time")
    private LocalDateTime createTime;
}
