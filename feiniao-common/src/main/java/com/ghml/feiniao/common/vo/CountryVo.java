package com.ghml.feiniao.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 11:30
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryVo {

    private String countryCode;
    private String countryName;
}
