package com.ghml.feiniao.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghml.feiniao.common.entity.OrderRecordEntity;
import com.ghml.feiniao.common.vo.OrderRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderRecordMapper extends BaseMapper<OrderRecordEntity> {
    @Select("""
            SELECT o.order_id      AS orderId,
                   o.create_time   AS createTime,
                   cp.cover_url    AS coverUrl,
                   cp.gender       AS genderCode,
                   ar.age_range_desc AS ageRangeDesc,
                   c.country_name  AS countryName,
                   o.product_name  AS productName,
                   p.platform_name AS platformName,
                   o.video_num     AS videoNum,
                   o.product_details AS productDetails,
                   o.video_format  AS videoFormat,
                   o.order_amount  AS orderAmount,
                   o.order_status  AS orderStatus
            FROM order_records o
                     LEFT JOIN creator_profiles cp ON o.creator_id = cp.user_id
                     LEFT JOIN age_range ar ON cp.age_range = ar.age_range
                     LEFT JOIN country c ON cp.country_code = c.country_code
                     LEFT JOIN platform p ON o.platform_code = p.platform_code
            WHERE o.brand_id = #{brandId}
            ORDER BY o.create_time DESC
            """)
    List<OrderRecordVo> listOrderRecords(@Param("brandId") String brandId);
}
