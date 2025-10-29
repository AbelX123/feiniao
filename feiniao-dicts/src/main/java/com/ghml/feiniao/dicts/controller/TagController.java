package com.ghml.feiniao.dicts.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.vo.TagVo;
import com.ghml.feiniao.dicts.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 22:41
 * @description
 */
@RestController
@RequestMapping("/api/dicts")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    // 获取标签列表
    @GetMapping("/tags")
    public R<List<TagVo>> getTags() {
        List<TagVo> vos = tagService.getTags();
        return R.ok(vos);
    }
}
