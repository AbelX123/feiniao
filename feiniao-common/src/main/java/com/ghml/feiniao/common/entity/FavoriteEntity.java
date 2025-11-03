package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-03 13:24
 * @description
 */
@Data
@TableName("brand_favorite_creators")
public class FavoriteEntity {

    private String brandId;
    private String creatorId;
    private Date createTime;
}
