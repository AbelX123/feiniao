package com.ghml.feiniao.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("menus")
public class MenuEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "menu_id", type = IdType.AUTO)
    private Integer menuId;

    @TableField("parent_id")
    private Integer parentId = 0;

    @TableField("menu_key")
    private String menuKey;

    @TableField("menu_label")
    private String menuLabel;

    @TableField("icon")
    private String icon;

    @TableField("path")
    private String path;

    @TableField("sort_order")
    private Integer sortOrder = 0;

    @TableField("is_visible")
    private Boolean isVisible = true;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
