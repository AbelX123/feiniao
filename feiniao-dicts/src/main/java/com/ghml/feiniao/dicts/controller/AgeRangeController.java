package com.ghml.feiniao.dicts.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.vo.AgeRangeVo;
import com.ghml.feiniao.dicts.service.AgeRangeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 23:49
 * @description
 */
@RestController
@RequestMapping("/api/dicts")
public class AgeRangeController {

    private final AgeRangeService ageRangeService;

    public AgeRangeController(AgeRangeService ageRangeService) {
        this.ageRangeService = ageRangeService;
    }

    // 获取年龄段列表
    @GetMapping("/age-ranges")
    public R<List<AgeRangeVo>> getAgeRanges() {
        List<AgeRangeVo> vos = ageRangeService.getAgeRanges();
        return R.ok(vos);
    }
}
