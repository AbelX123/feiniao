package com.ghml.feiniao.users.service;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.dto.VideoUploadDto;
import com.ghml.feiniao.common.vo.CaseVo;

import java.util.List;

public interface CreatorCaseService {
    R<String> uploadCase(VideoUploadDto dto);

    R<List<CaseVo>> getCase(String creatorId);
}
