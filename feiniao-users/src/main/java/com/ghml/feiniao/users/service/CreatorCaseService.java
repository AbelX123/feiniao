package com.ghml.feiniao.users.service;

import com.ghml.feiniao.common.dto.VideoUploadDto;
import com.ghml.feiniao.common.vo.CaseVo;

import java.util.List;

public interface CreatorCaseService {
    String uploadCase(VideoUploadDto dto);

    List<CaseVo> getCases(String creatorId);
}
