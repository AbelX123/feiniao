package com.ghml.feiniao.users.controller;

import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.dto.CaptchaVerifyDto;
import com.ghml.feiniao.users.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    // 创建验证码
    @PostMapping("/create")
    public R<?> create(@RequestBody String phone) {
        return captchaService.create(phone) ? R.ok() : R.failed(Code.OPERATION_FAILED);
    }

    // 验证验证码
    @PostMapping("/verify")
    public R<?> verify(@RequestBody CaptchaVerifyDto dto) {
        return captchaService.verify(dto) ? R.ok() : R.failed(Code.VERIFIED_CODE_FAILED);
    }
}
