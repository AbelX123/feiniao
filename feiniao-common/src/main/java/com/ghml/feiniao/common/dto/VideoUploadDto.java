package com.ghml.feiniao.common.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 案例dto
 */
@Data
public class VideoUploadDto {

    private String title; // 案例标题

    private MultipartFile video; // 案例视频

    private MultipartFile cover; // 案例封面

}
