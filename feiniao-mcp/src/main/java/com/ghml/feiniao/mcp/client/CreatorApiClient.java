package com.ghml.feiniao.mcp.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.dto.CreatorsDto;
import com.ghml.feiniao.common.utils.PageResult;
import com.ghml.feiniao.common.vo.CreatorDisplayVo;
import com.ghml.feiniao.mcp.config.FeiniaoApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 调用 users 和 dicts 模块 API 的客户端
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CreatorApiClient {

    private final FeiniaoApiProperties apiProperties;

    public PageResult<CreatorDisplayVo> selectCreators(CreatorsDto dto) {
        WebClient client = createUsersClient();
        R<PageResult<CreatorDisplayVo>> body = client.post()
                .uri("/api/users/creators")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<R<PageResult<CreatorDisplayVo>>>() {})
                .block();
        if (body == null || !"0000".equals(body.getCode())) {
            throw new IllegalStateException("调用创作者列表 API 失败: " + (body != null ? body.getMsg() : "无响应"));
        }
        return body.getData();
    }

    public JSONObject getCreatorFilterOptions() {
        WebClient dictsClient = WebClient.builder()
                .baseUrl(apiProperties.getDictsBaseUrl() + "/api/dicts")
                .build();
        JSONObject result = new JSONObject();
        try {
            result.put("使用说明", "与用户对话时仅使用各选项的「名称」进行自然语言描述。");
            result.put("模特类型", toNaturalList(fetchList(dictsClient, "/model-types"), "modelTypeName", "modelTypeId"));
            result.put("平台", toNaturalList(fetchList(dictsClient, "/platforms"), "platformName", "platformCode"));
            result.put("国家地区", toNaturalList(fetchList(dictsClient, "/countries"), "countryName", "countryCode"));
            result.put("年龄段", toNaturalList(fetchList(dictsClient, "/age-ranges"), "ageRangeDesc", "ageRange"));
            result.put("擅长品类", toNaturalList(fetchList(dictsClient, "/specialties"), "specialtyName", "specialtyId"));
            result.put("模特标签", toNaturalList(fetchList(dictsClient, "/tags"), "tagName", "tagId"));
            result.put("性别", List.of(
                    new JSONObject().fluentPut("名称", "男").fluentPut("对应值", 1),
                    new JSONObject().fluentPut("名称", "女").fluentPut("对应值", 2)
            ));
        } catch (Exception e) {
            log.warn("获取筛选选项失败: {}", e.getMessage());
            throw new IllegalStateException("获取模特筛选选项失败: " + e.getMessage());
        }
        return result;
    }

    private List<JSONObject> toNaturalList(List<?> raw, String nameKey, String idKey) {
        List<JSONObject> list = new ArrayList<>();
        for (Object o : raw) {
            String name = null;
            Object id = null;
            if (o instanceof JSONObject jo) {
                name = jo.getString(nameKey);
                id = jo.get(idKey);
            } else if (o instanceof java.util.Map<?, ?> map) {
                Object n = map.get(nameKey);
                name = n != null ? n.toString() : null;
                id = map.get(idKey);
            }
            if (name != null) {
                JSONObject item = new JSONObject();
                item.put("名称", name);
                item.put("对应值", id);
                list.add(item);
            }
        }
        return list;
    }

    private WebClient createUsersClient() {
        WebClient.Builder builder = WebClient.builder().baseUrl(apiProperties.getUsersBaseUrl());
        if (apiProperties.getUsersAuthHeader() != null && !apiProperties.getUsersAuthHeader().isBlank()) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, apiProperties.getUsersAuthHeader());
        }
        return builder.build();
    }

    private List<?> fetchList(WebClient client, String path) {
        String json = client.get()
                .uri(path)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        R<?> r = JSON.parseObject(json, R.class);
        if (r == null || !"0000".equals(r.getCode())) {
            throw new IllegalStateException("请求失败: " + path);
        }
        Object data = r.getData();
        return data instanceof List ? (List<?>) data : List.of();
    }
}
