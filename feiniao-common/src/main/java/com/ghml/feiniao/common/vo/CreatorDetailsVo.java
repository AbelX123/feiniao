package com.ghml.feiniao.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CreatorDetailsVo extends CreatorDisplayVo {

    private List<String> modelTypes; // 模特类型列表
    private List<String> platforms; // 模特平台列表
    private List<String> specialties; // 模特擅长品类列表
    private List<String> tags; // 模特标签列表
    private List<CaseVo> caseVos; // 模特案例列表
}

