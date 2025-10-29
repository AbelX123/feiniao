package com.ghml.feiniao.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 23:31
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelTypeVo {

    private Integer modelTypeId;
    private String modelTypeName;
}
