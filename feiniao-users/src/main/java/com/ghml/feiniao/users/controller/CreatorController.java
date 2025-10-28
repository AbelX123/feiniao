package com.ghml.feiniao.users.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.dto.CreatorDto;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.utils.PageResult;
import com.ghml.feiniao.common.vo.CreatorVo;
import com.ghml.feiniao.users.service.CreatorService;
import org.springframework.web.bind.annotation.*;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 创作者资源入口
 */
@RestController
@RequestMapping("/api/creators")
public class CreatorController {

    private final CreatorService creatorService;

    public CreatorController(CreatorService creatorService) {
        this.creatorService = creatorService;
    }

    // 分页获取创作者列表
    @PostMapping("/query")
    public R<PageResult<CreatorVo>> getCreators(@RequestBody CreatorDto creatorDto) {
        try {
            PageResult<CreatorVo> vo = creatorService.selectCreatorsByConditions(creatorDto);
            return R.ok(vo);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 获取创作者详情信息
    @GetMapping("/{creatorId}")
    public String getCreatorById(@PathVariable("creatorId") String creatorId) {
        return R.ok("张三" + creatorId).toString();
    }
}
