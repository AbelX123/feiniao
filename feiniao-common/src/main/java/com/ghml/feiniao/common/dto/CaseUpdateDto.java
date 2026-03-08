package com.ghml.feiniao.common.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CaseUpdateDto {
    private String caseTitle; // 案例标题（可选）
    private MultipartFile cover; // 新封面（可选）
    private MultipartFile video; // 新视频（可选）
}
