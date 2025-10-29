package com.ghml.feiniao.dicts.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.vo.SpecialtyVo;
import com.ghml.feiniao.dicts.service.SpecialtyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 11:01
 * @description
 */
@RestController
@RequestMapping("/api/dicts")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    // 获取擅长类目列表
    @GetMapping("/specialties")
    public R<List<SpecialtyVo>> getSpecialties() {
        List<SpecialtyVo> vos = specialtyService.getSpecialties();
        return R.ok(vos);
    }
}
