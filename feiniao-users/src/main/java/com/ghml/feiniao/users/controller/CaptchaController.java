package com.ghml.feiniao.users.controller;

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
        captchaService.create(phone);
        return R.ok();
    }

    // 验证验证码
    @PostMapping("/verify")
    public R<?> verify(@RequestBody CaptchaVerifyDto dto) {
        captchaService.verify(dto);
        return R.ok();
    }
}
