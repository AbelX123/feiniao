package com.ghml.feiniao.users.service;

import com.ghml.feiniao.common.dto.VideoUploadDto;
import com.ghml.feiniao.common.vo.CaseVo;

import java.util.List;

public interface CreatorCaseService {

    CaseVo uploadCase(VideoUploadDto dto);

    List<CaseVo> getCases(String creatorId);

    CaseVo getCaseById(String caseId);
}
