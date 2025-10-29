package com.ghml.feiniao.dicts.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.vo.ModelTypeVo;
import com.ghml.feiniao.dicts.service.IModelTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 23:22
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/api/dicts")
public class ModelTypeController {

    private final IModelTypeService modelTypeService;

    public ModelTypeController(IModelTypeService modelTypeService) {
        this.modelTypeService = modelTypeService;
    }

    // 获取模特类型列表
    @GetMapping("/model-types")
    public R<List<ModelTypeVo>> getModelTypes() {
        List<ModelTypeVo> vos = modelTypeService.getModelTypes();
        return R.ok(vos);
    }
}
