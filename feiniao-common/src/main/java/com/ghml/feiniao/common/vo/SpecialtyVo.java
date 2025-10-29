package com.ghml.feiniao.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 11:06
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialtyVo {

    private Integer specialtyId;
    private String specialtyName;
}
