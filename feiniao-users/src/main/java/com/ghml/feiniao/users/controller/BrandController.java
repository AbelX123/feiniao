package com.ghml.feiniao.users.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.vo.BrandVo;
import com.ghml.feiniao.users.service.BrandService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 产品主资源入口
 */
@RestController
@RequestMapping("/api/users/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    // 获取产品主信息
    @GetMapping
    public R<BrandVo> getBrandById() {
        try {
            BrandVo vo = brandService.getBrandById();
            return R.ok(vo);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 收藏创作者
    @PostMapping("/brand/favorite/creators/{creatorId}")
    public R<?> followCreator(@PathVariable("creatorId") String creatorId) {
        try {
            brandService.followCreator(creatorId);
            return R.ok();
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 取消收藏创作者
    @DeleteMapping("/brand/favorite/creators/{creatorId}")
    public R<?> unFollowCreator(@PathVariable("creatorId") String creatorId) {
        try {
            brandService.unfollowCreator(creatorId);
            return R.ok();
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 产品主头像上传OSS
    @PostMapping("/avatar")
    public R<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String s = brandService.uploadAvatar(file);
            return R.ok(s);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 获取文件外链
    @GetMapping("/avatar")
    public R<String> getAvatarUrl(@RequestParam("filename") String filename) {
        try {
            String url = brandService.getAvatarUrl(filename);
            return R.ok(url);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }
}
