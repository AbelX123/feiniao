package com.ghml.feiniao.users.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.vo.CaptchaVo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    // 创建验证码
    @PostMapping("create")
    public R<CaptchaVo> create(@RequestBody String phone) {

        return null;
    }

    // 验证验证码
    @GetMapping("verify")
    public R<?> verify(@RequestBody String captcha) {

        return null;
    }
}
