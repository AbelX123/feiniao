package com.ghml.feiniao.dicts.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.vo.PlatformVo;
import com.ghml.feiniao.dicts.service.IPlatformService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 23:50
 * @description
 */
@RestController
@RequestMapping("/api/dicts")
public class PlatformController {

    private final IPlatformService platformService;

    public PlatformController(IPlatformService platformService) {
        this.platformService = platformService;
    }

    // 获取平台列表
    @GetMapping("/platforms")
    public R<List<PlatformVo>> getPlatforms() {
        List<PlatformVo> vos = platformService.getPlatforms();
        return R.ok(vos);
    }
}
