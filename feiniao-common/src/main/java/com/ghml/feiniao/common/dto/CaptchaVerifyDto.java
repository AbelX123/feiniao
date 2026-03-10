package com.ghml.feiniao.common.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class CaptchaVerifyDto {
    @JsonAlias({"phone", "phoneFull"})
    private String phone;

    @JsonAlias({"captcha", "code", "verifiedCode"})
    private String captcha;
}
