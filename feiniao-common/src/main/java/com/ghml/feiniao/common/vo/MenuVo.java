package com.ghml.feiniao.common.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 菜单vo
 */
@Data
@Builder
public class MenuVo {
    private String key;
    private String label;
    private String icon;
    private String path;
    private Integer order;
}
