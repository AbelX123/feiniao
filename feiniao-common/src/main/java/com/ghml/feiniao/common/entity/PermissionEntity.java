package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-03 17:22
 * @description
 */
@Data
@TableName("permissions")
public class PermissionEntity {

    @TableId
    private Integer permissionId;
    private String permissionCode;
    private String permissionDesc;
    private String httpMethod;
    private String apiPath;
    private Date createTime;
    private Date updateTime;
}
