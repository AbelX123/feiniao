package com.ghml.feiniao.common.dto;

import com.ghml.feiniao.common.api.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 创作者们dto
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CreatorsDto extends PageParam {

    // 查询条件
    private List<String> platformCodes; // 平台编码
    private List<String> countryCodes; // 创作者国家代码
    private List<Integer> modelTypeIds; // 模特类型编号
    private List<Integer> modelTagIds; // 模特标签编号
    private List<Integer> genders; // 模特性别
    private List<String> ageRanges; // 模特年龄范围
    private List<Integer> specialtyIds; // 擅长品类编号
}
