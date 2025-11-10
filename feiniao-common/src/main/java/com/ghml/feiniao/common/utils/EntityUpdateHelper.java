package com.ghml.feiniao.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-08 23:30
 * @description 实体属性更新工具类，用于部分更新实体属性，避免空值覆盖
 */
public class EntityUpdateHelper {

    // 当值不为null时设置属性
    public static <T> void setIfNotNull(Supplier<T> getter, Consumer<T> setter) {
        T value = getter.get();
        if (value != null) {
            setter.accept(value);
        }
    }

    // 当字符串不为空时设置属性
    public static void setIfNotBlank(Supplier<String> getter, Consumer<String> setter) {
        String value = getter.get();
        if (StringUtils.isNotBlank(value)) {
            setter.accept(value);
        }
    }
}
