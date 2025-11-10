package com.ghml.feiniao.users.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.dto.CreatorDto;
import com.ghml.feiniao.common.dto.CreatorsDto;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.utils.PageResult;
import com.ghml.feiniao.common.vo.CreatorVo;
import com.ghml.feiniao.users.service.CreatorService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 创作者资源入口
 */
@RestController
@RequestMapping("/api/users/creators")
public class CreatorController {

    private final CreatorService creatorService;

    public CreatorController(CreatorService creatorService) {
        this.creatorService = creatorService;
    }

    // 分页获取创作者列表
    @PostMapping
    public R<PageResult<CreatorVo>> getCreators(@RequestBody CreatorsDto creatorsDto) {
        try {
            PageResult<CreatorVo> vo = creatorService.selectCreatorsByConditions(creatorsDto);
            return R.ok(vo);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 获取创作者详情信息
    @GetMapping("/{creatorId}")
    public R<CreatorVo> getCreatorById(@PathVariable("creatorId") String creatorId) {
        try {
            CreatorVo vo = creatorService.getCreatorById(creatorId);
            return R.ok(vo);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 更新创作者详情信息
    @PatchMapping
    public R<CreatorVo> patchCreator(@RequestBody CreatorDto dto) {
        try {
            CreatorVo vo = creatorService.patchCreator(dto);
            return R.ok(vo);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 通过产品主编号分页获取收藏的创作者
    @GetMapping("/favorite-creators")
    public R<PageResult<CreatorVo>> favoriteCreators(@RequestBody CreatorsDto dto) {
        try {
            PageResult<CreatorVo> vos = creatorService.favoriteCreators(dto);
            return R.ok(vos);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 创作者视频上传到OSS
    @PostMapping("/video")
    public R<String> uploadVideo(@RequestParam("video") MultipartFile video) {
        try {
            String respO = creatorService.uploadVideo(video);
            return R.ok(respO);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }
}
