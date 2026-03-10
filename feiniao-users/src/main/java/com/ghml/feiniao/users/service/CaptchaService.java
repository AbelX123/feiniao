package com.ghml.feiniao.users.service;

import com.ghml.feiniao.common.dto.CaptchaVerifyDto;

public interface CaptchaService {

    void create(String phoneRaw);

    void verify(CaptchaVerifyDto dto);
}
