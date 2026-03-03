package com.ghml.feiniao.mcp.tools;

import com.ghml.feiniao.common.dto.CreatorsDto;
import com.ghml.feiniao.common.utils.PageResult;
import com.ghml.feiniao.common.vo.CreatorDisplayVo;
import com.ghml.feiniao.mcp.client.CreatorApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 挑选模特工具
 * 参考 CreatorDetailsVo 的特征：模特类型、平台、擅长品类、标签、性别、年龄、国家等
 * 可自由选择询问这些特征便于查找相应模特
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CreatorSelectTool implements McpTool {

    private final CreatorApiClient creatorApiClient;

    @Tool(description = """
            获取模特筛选选项，供筛选时参考。
            返回格式为自然语言：模特类型、平台、国家地区、年龄段、擅长品类、模特标签、性别。
            与用户对话时：仅使用「名称」描述（如「运动健身品类」「抖音平台」），严禁提及 specialtyId、tagId、modelTypeId、platformCode、对应值 等任何技术字段或编号。
            调用 selectCreators 时再使用对应值进行筛选。
            """)
    public String getCreatorFilterOptions() {
        try {
            log.info("MCP 工具调用: getCreatorFilterOptions");
            var options = creatorApiClient.getCreatorFilterOptions();
            log.info("MCP 工具调用完成: getCreatorFilterOptions");
            return options.toJSONString();
        } catch (Exception e) {
            log.error("获取模特筛选选项失败", e);
            return "获取筛选选项失败: " + e.getMessage();
        }
    }

    @Tool(description = """
            根据条件分页查询模特列表。
            筛选参数（均为可选，多个用逗号分隔）：性别(1男2女)、平台编码、国家代码、年龄段编码、模特类型编号、标签编号、擅长品类编号。
            与用户对话时仅用自然语言（如「女性模特」「抖音平台」「运动健身品类」），严禁向用户提及任何编号、编码、id、code 等技术字段。
            """)
    public String selectCreators(
            @ToolParam(description = "性别，1=男 2=女，多个用逗号分隔如 1,2") String genders,
            @ToolParam(description = "平台编码，多个用逗号分隔") String platformCodes,
            @ToolParam(description = "国家代码，多个用逗号分隔如 CN,US") String countryCodes,
            @ToolParam(description = "年龄段编码，多个用逗号分隔") String ageRanges,
            @ToolParam(description = "模特类型编号，多个用逗号分隔") String modelTypeIds,
            @ToolParam(description = "模特标签编号，多个用逗号分隔") String modelTagIds,
            @ToolParam(description = "擅长品类编号，多个用逗号分隔") String specialtyIds,
            @ToolParam(description = "页码，默认1") Integer pageNum,
            @ToolParam(description = "每页条数，默认10") Integer pageSize) {
        try {
            log.info("MCP 工具调用: selectCreators, genders={}, platformCodes={}, countryCodes={}, ageRanges={}, modelTypeIds={}, modelTagIds={}, specialtyIds={}, pageNum={}, pageSize={}",
                    genders, platformCodes, countryCodes, ageRanges, modelTypeIds, modelTagIds, specialtyIds, pageNum, pageSize);
            CreatorsDto dto = new CreatorsDto();
            dto.setPageNum(pageNum != null ? pageNum : 1);
            dto.setPageSize(pageSize != null ? pageSize : 10);
            dto.setGenders(parseIntList(genders));
            dto.setPlatformCodes(parseStringList(platformCodes));
            dto.setCountryCodes(parseStringList(countryCodes));
            dto.setAgeRanges(parseStringList(ageRanges));
            dto.setModelTypeIds(parseIntList(modelTypeIds));
            dto.setModelTagIds(parseIntList(modelTagIds));
            dto.setSpecialtyIds(parseIntList(specialtyIds));

            PageResult<CreatorDisplayVo> result = creatorApiClient.selectCreators(dto);
            int count = result != null && result.getRecords() != null ? result.getRecords().size() : 0;
            long total = result != null ? result.getTotal() : 0L;
            log.info("MCP 工具调用完成: selectCreators, total={}, currentPageCount={}", total, count);
            return formatCreatorResult(result);
        } catch (Exception e) {
            log.error("查询模特列表失败", e);
            return "查询模特失败: " + e.getMessage();
        }
    }

    private List<Integer> parseIntList(String s) {
        if (s == null || s.isBlank()) return null;
        List<Integer> list = new ArrayList<>();
        for (String part : s.split(",")) {
            String t = part.trim();
            if (!t.isEmpty()) {
                try {
                    list.add(Integer.parseInt(t));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return list.isEmpty() ? null : list;
    }

    private List<String> parseStringList(String s) {
        if (s == null || s.isBlank()) return null;
        List<String> list = new ArrayList<>();
        for (String part : s.split(",")) {
            String t = part.trim();
            if (!t.isEmpty()) {
                list.add(t);
            }
        }
        return list.isEmpty() ? null : list;
    }

    private String formatCreatorResult(PageResult<CreatorDisplayVo> result) {
        if (result == null || result.getRecords() == null) {
            return "未查询到模特";
        }
        List<CreatorDisplayVo> records = result.getRecords();
        if (records.isEmpty()) {
            return String.format("暂无符合条件的模特，可尝试放宽筛选条件。");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("找到 %d 位模特，当前第 %d 页：\n\n", result.getTotal(), result.getCurrent()));
        for (CreatorDisplayVo v : records) {
            sb.append("· ").append(v.getUsername() != null ? v.getUsername() : "未设置昵称");
            sb.append("：").append(v.getGender() != null ? v.getGender() : "未知性别");
            sb.append("，").append(v.getAgeRangeDesc() != null ? v.getAgeRangeDesc() : "年龄未知");
            sb.append("，").append(v.getCountryName() != null ? "来自" + v.getCountryName() : "地区未知");
            sb.append("。");
            if (v.getVideoPrice() != null) {
                sb.append("视频报价").append(v.getVideoPrice()).append("美元。");
            }
            sb.append(v.getIsAvailable() != null && v.getIsAvailable() == 1 ? "当前可接单。" : "暂不接单。");
            if (v.getCoverUrl() != null && !v.getCoverUrl().isBlank()) {
                sb.append("展示图：").append(v.getCoverUrl());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
