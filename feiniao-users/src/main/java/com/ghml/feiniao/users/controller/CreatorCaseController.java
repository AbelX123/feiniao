//package com.ghml.feiniao.users.controller;
//
//import com.ghml.feiniao.common.api.R;
//import com.ghml.feiniao.common.dto.CaseDto;
//import com.ghml.feiniao.common.exception.ServiceException;
//import com.ghml.feiniao.users.service.CreatorCaseService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@RequestMapping("/api/creators/cases")
//@RestController
//@RequiredArgsConstructor
//public class CreatorCaseController {
//
//    private final CreatorCaseService creatorCaseService;
//
//    // 案例上传预创建
//    @PostMapping("/pre")
//    public R<String> pre() {
//        try {
//            String caseId = creatorCaseService.pre();
//            return R.ok(caseId);
//        } catch (ServiceException e) {
//            return R.failed(e.getCode());
//        }
//    }
//
//
//    // 案例封面上传
//    @PostMapping("/cover")
//    public R<?> uploadCover(@RequestParam("cover") MultipartFile cover) {
//        try {
//            creatorCaseService.uploadCover(cover);
//            return R.ok();
//        } catch (ServiceException e) {
//            return R.failed(e.getCode());
//        }
//    }
//
//    // 案例视频上传
//    @PostMapping("/video")
//    public R<?> uploadVideo(@RequestParam("video") MultipartFile video) {
//        try {
//            creatorCaseService.uploadVideo(video);
//            return R.ok();
//        } catch (ServiceException e) {
//            return R.failed(e.getCode());
//        }
//    }
//
//    // 创作者案例上传
//    @PostMapping
//    public R<?> uploadCases(@RequestBody CaseDto caseDto) {
//        try {
//            creatorCaseService.uploadCase(caseDto);
//            return R.ok();
//        } catch (ServiceException e) {
//            return R.failed(e.getCode());
//        }
//    }
//
//    // 创建者案例孤儿文件，孤儿记录删除
//    @DeleteMapping
//    public R<?> delOrphan(@RequestBody CaseDto caseDto) {
//        try {
//            creatorCaseService.delOrphan(caseDto);
//            return R.ok();
//        } catch (ServiceException e) {
//            return R.failed(e.getCode());
//        }
//    }
//
//    // 获取创作者案例列表
//
//    // 获取创作者案例详情
//}
