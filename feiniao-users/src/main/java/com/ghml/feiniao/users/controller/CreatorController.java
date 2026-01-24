package com.ghml.feiniao.users.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.dto.CreatorDto;
import com.ghml.feiniao.common.dto.CreatorsDto;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.utils.PageResult;
import com.ghml.feiniao.common.vo.*;
import com.ghml.feiniao.users.service.CreatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 创作者资源入口
 */
@RestController
@RequestMapping("/api/users/creators")
@RequiredArgsConstructor
public class CreatorController {

    private final CreatorService creatorService;

    // 分页获取创作者列表
    @PostMapping
    public R<PageResult<CreatorDisplayVo>> getCreators(@RequestBody CreatorsDto creatorsDto) {
        try {
            PageResult<CreatorDisplayVo> vo = creatorService.selectCreatorsByConditions(creatorsDto);
            return R.ok(vo);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 获取创作者详情信息
    @GetMapping("/{creatorId}")
    public R<CreatorDetailsVo> getCreatorById(@PathVariable("creatorId") String creatorId) {
        try {
            CreatorDetailsVo vo = creatorService.getCreatorById(creatorId);
            return R.ok(vo);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 获取创作者类别
    @GetMapping("/{creatorId}/types")
    public R<List<ModelTypeVo>> getModelTypesById(@PathVariable("creatorId") String creatorId) {
        try {
            List<ModelTypeVo> vos = creatorService.getModelTypesById(creatorId);
            return R.ok(vos);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 获取创作者平台
    @GetMapping("/{creatorId}/platforms")
    public R<List<PlatformVo>> getPlatforms(@PathVariable("creatorId") String creatorId) {
        try {
            List<PlatformVo> vos = creatorService.getPlatforms(creatorId);
            return R.ok(vos);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 获取创作者品类
    @GetMapping("/{creatorId}/specialties")
    public R<List<SpecialtyVo>> getSpecialties(@PathVariable("creatorId") String creatorId) {
        try {
            List<SpecialtyVo> vos = creatorService.getSpecialties(creatorId);
            return R.ok(vos);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 获取创作者模特标签
    @GetMapping("/{creatorId}/tags")
    public R<List<TagVo>> getTags(@PathVariable("creatorId") String creatorId) {
        try {
            List<TagVo> vos = creatorService.getTags(creatorId);
            return R.ok(vos);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 获取创作者案例
    @GetMapping("/{creatorId}/cases")
    public R<List<CaseVo>> getCases(@PathVariable("creatorId") String creatorId) {
        try {
            List<CaseVo> vos = creatorService.getCaseVos(creatorId);
            return R.ok(vos);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 更新创作者详情信息
    @PatchMapping
    public R<CreatorDetailsVo> patchCreator(@RequestBody CreatorDto dto) {
        try {
            CreatorDetailsVo vo = creatorService.patchCreator(dto);
            return R.ok(vo);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 通过产品主编号分页获取收藏的创作者
    @PostMapping("/favorite-creators")
    public R<PageResult<CreatorDisplayVo>> favoriteCreators(@RequestBody CreatorsDto dto) {
        try {
            PageResult<CreatorDisplayVo> vos = creatorService.favoriteCreators(dto);
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
