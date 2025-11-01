package com.ghml.feiniao.users.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.constants.HttpHeaders;
import com.ghml.feiniao.common.dto.BrandDto;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.vo.BrandDetailVo;
import com.ghml.feiniao.common.vo.BrandVo;
import com.ghml.feiniao.users.service.IBrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 产品主资源入口
 */
@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final IBrandService brandService;

    public BrandController(IBrandService brandService) {
        this.brandService = brandService;
    }

    // 注册
    @PostMapping
    public R<?> registerCreator(@RequestBody BrandDto brandDto) {
        try {
            brandService.registerCreators(brandDto);
            return R.ok();
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 登录
    @PostMapping("/login")
    public R<BrandVo> login(@RequestBody BrandDto brandDto) {
        try {
            BrandVo vo = brandService.login(brandDto);
            return R.ok(vo);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 刷新token
    @PostMapping("/refresh-token")
    public R<BrandVo> refresh(@RequestHeader(HttpHeaders.REFRESH_TOKEN) String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            return R.failed("刷新令牌不能为空");
        }
        try {
            BrandVo vo = brandService.refreshToken(refreshToken);
            return R.ok(vo);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 获取产品主信息
    @GetMapping
    public R<BrandDetailVo> getBrandById() {
        try {
            BrandDetailVo vo = brandService.getBrandById();
            return R.ok(vo);
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
