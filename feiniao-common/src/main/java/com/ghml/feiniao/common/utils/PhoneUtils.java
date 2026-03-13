package com.ghml.feiniao.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号处理工具（规范化、解析等）
 *
 * @author YUHUAI
 */
public final class PhoneUtils {

    private static final Pattern PHONE_JSON_PATTERN =
            Pattern.compile("\"(?:phone|phoneFull)\"\\s*:\\s*\"([^\"]+)\"");

    private PhoneUtils() {
    }

    /**
     * 规范化手机号：支持纯手机号或 JSON 字符串（如 "8613800138000" 或 {"phone":"8613800138000"}）
     *
     * @param raw 原始入参
     * @return 规范化后的手机号，无法解析时返回 null
     */
    public static String normalizePhone(String raw) {
        if (raw == null) {
            return null;
        }
        String phone = StringUtils.trim(raw);
        if (phone.startsWith("\"") && phone.endsWith("\"") && phone.length() >= 2) {
            return phone.substring(1, phone.length() - 1);
        }
        Matcher matcher = PHONE_JSON_PATTERN.matcher(phone);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return phone;
    }
}
