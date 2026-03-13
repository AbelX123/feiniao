package com.ghml.feiniao.users.service;

import com.ghml.feiniao.common.dto.CaptchaVerifyDto;

public interface CaptchaService {

    /**
     * 创建并发送验证码
     *
     * @param phoneRaw 手机号（原始格式）
     * @return true 创建成功，false 创建失败
     */
    boolean create(String phoneRaw);

    /**
     * 验证验证码
     *
     * @param dto 包含手机号和验证码
     * @return true 验证通过，false 验证失败
     */
    boolean verify(CaptchaVerifyDto dto);
}
