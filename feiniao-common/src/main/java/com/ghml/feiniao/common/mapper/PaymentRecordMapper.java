package com.ghml.feiniao.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghml.feiniao.common.entity.PaymentRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecordEntity> {

    @Select("""
            SELECT out_trade_no,
                   order_id,
                   trade_no,
                   trade_status,
                   total_amount,
                   receipt_amount,
                   buyer_pay_amount,
                   point_amount,
                   subject,
                   gmt_payment,
                   gmt_close,
                   fund_bill_list,
                   create_time,
                   update_time
            FROM payment_records
            WHERE order_id = #{orderId}
            LIMIT 1
            """)
    PaymentRecordEntity selectByOrderId(@Param("orderId") String orderId);
}
