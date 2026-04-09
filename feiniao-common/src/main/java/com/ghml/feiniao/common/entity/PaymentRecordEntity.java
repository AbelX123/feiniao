package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_records")
public class PaymentRecordEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "out_trade_no", type = IdType.INPUT)
    private Long outTradeNo;

    @TableField(value = "order_id")
    private String orderId;

    @TableField(value = "trade_no")
    private String tradeNo;

    @TableField(value = "trade_status")
    private String tradeStatus;

    @TableField(value = "total_amount")
    private BigDecimal totalAmount;

    @TableField(value = "receipt_amount")
    private BigDecimal receiptAmount;

    @TableField(value = "buyer_pay_amount")
    private BigDecimal buyerPayAmount;

    @TableField(value = "point_amount")
    private BigDecimal pointAmount;

    @TableField(value = "subject")
    private String subject;

    @TableField(value = "gmt_payment")
    private LocalDateTime gmtPayment;

    @TableField(value = "gmt_close")
    private LocalDateTime gmtClose;

    @TableField(value = "fund_bill_list")
    private String fundBillList;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
