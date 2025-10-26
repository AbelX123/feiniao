package com.ghml.feiniao.dicts.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dicts")
public class DictsController {

    /**
     * 获取模特类型列表
     */
    @GetMapping("/model-types")
    public String getModelTypes() {
        return "获取模特类型列表";
    }

    /**
     * 获取平台列表
     */
    @GetMapping("/platforms")
    public String getPlatforms() {
        return "获取平台列表";
    }

    /**
     * 获取年龄段列表
     */
    @GetMapping("/age-ranges")
    public String getAgeRanges() {
        return "获取年龄段列表";
    }

    /**
     * 获取擅长类目列表
     */
    @GetMapping("/expertises")
    public String getExpertises() {
        return "获取擅长类目列表";
    }

    /**
     * 获取国家列表
     */
    @GetMapping("/countries")
    public String getCountries() {
        return "获取国家列表";
    }
}