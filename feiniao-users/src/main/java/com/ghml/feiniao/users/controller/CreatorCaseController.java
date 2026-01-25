package com.ghml.feiniao.users.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.dto.VideoUploadDto;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.vo.CaseVo;
import com.ghml.feiniao.users.service.CreatorCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/creators/cases")
@RequiredArgsConstructor
public class CreatorCaseController {

    private final CreatorCaseService creatorCaseService;

    // 案例上传
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<String> uploadCase(@ModelAttribute VideoUploadDto videoUploadDto) {
        try {
            return creatorCaseService.uploadCase(videoUploadDto);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 根据创作者的编号获取案例
    @GetMapping("/{creatorId}")
    public R<List<CaseVo>> getCase(@PathVariable("creatorId") String creatorId) {
        try {
            return creatorCaseService.getCase(creatorId);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

}
