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

    // 案例上传（返回案例信息含 coverUrl、videoUrl，便于前端立即展示）
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<CaseVo> uploadCase(@ModelAttribute VideoUploadDto videoUploadDto) {
        try {
            CaseVo vo = creatorCaseService.uploadCase(videoUploadDto);
            return R.ok(vo);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 根据创作者的编号获取案例
    @GetMapping("/{creatorId}")
    public R<List<CaseVo>> getCase(@PathVariable("creatorId") String creatorId) {
        try {
            List<CaseVo> vos = creatorCaseService.getCases(creatorId);
            return R.ok(vos);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 根据案例编号获取案例（用于按需刷新外链）
    @GetMapping("/id/{caseId}")
    public R<CaseVo> getCaseById(@PathVariable("caseId") String caseId) {
        try {
            CaseVo vo = creatorCaseService.getCaseById(caseId);
            return R.ok(vo);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

}
