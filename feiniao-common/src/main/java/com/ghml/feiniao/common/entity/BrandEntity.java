package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 产品主
 */
@Data
@TableName("brand_profiles")
public class BrandEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId("user_id")
    private String userId; // 用户编号
    private String username;
    private String avatar;
    private String phoneCountryCode;
    private String phoneNumber;
    private String phoneFull;
    private Integer phoneVerified;
    private Date verifiedAt;
    private Date createTime;
    private Date updateTime;
}
