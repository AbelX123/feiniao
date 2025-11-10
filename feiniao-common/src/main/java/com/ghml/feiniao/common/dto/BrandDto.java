package com.ghml.feiniao.common.dto;

import com.ghml.feiniao.common.annos.ValidPhoneNumberGroup;
import lombok.Data;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Data
@ValidPhoneNumberGroup
public class BrandDto {
    private String username;
    private String phoneCountryCode;
    private String phoneNumber;
    private String phoneFull;
    private String verifiedCode;
    private Integer phoneVerified;
}
