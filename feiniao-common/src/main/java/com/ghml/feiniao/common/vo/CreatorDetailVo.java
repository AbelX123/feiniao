package com.ghml.feiniao.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 15:24
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatorDetailVo {

    private String creatorId;
    private String creatorName;
    private String countryName;
    private String gender;
    private List<String> modelTypes;
    private List<String> platforms;
    private List<String> specialties;
    private List<String> tags;
    private List<CaseVo> caseVos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CaseVo {
        private String caseId;
        private String caseTitle;
        private String coverUrl;
    }
}
