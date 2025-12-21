package com.ghml.feiniao.common.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class CaseVo implements Serializable {
    private String caseId; // 案例编号
    private String caseTitle; // 案例标题
    private String coverUrl; // 案例封面图片地址
    private String videoUrl; // 案例视频地址
    private String status; // 案例状态
    private LocalDateTime createTime; // 案例创建时间
}
