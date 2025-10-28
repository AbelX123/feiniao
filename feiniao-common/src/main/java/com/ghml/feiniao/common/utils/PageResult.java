package com.ghml.feiniao.common.utils;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Data
@Builder
public class PageResult<T> {
    private List<T> records;
    private Long total;
    private Long current;
    private Long size;
    private Long pages;
}
