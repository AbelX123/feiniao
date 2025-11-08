package com.ghml.feiniao.common.constants;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description redis key前缀
 */
public class RedisPrefix {

    // 用户info prefix
    public static final String PREFIX_USER_INFO = "user:info:";

    // web端的token prefix
    public static final String PREFIX_WEB_TOKEN = "web:token:";

    // web端的refresh_token prefix
    public static final String PREFIX_WEB_REFRESH_TOKEN = "web:refresh:token:";

    // 短信验证码
    public static final String PREFIX_PHONE_VERIFIED_CODE = "phone:verified:%s:code:%s";
}
