package com.zl.zlaiagent.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 网页搜索工具（SearchAPI）
 */
public class WebSearchTool {

    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    private final String apiKey;

    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    @Tool(description = "Search for information from the web (Baidu, fallback to Google)")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query) {
        String baiduResult = searchWithEngine(query, "baidu");
        if (!baiduResult.startsWith("Error searching")) {
            return baiduResult;
        }
        // 百度常对英文查询返回空 organic_results，回退 Google
        String googleResult = searchWithEngine(query, "google");
        if (!googleResult.startsWith("Error searching")) {
            return googleResult;
        }
        return baiduResult + " | Google fallback: " + googleResult;
    }

    private String searchWithEngine(String query, String engine) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("api_key", apiKey);
        paramMap.put("engine", engine);
        try {
            String response = HttpUtil.get(SEARCH_API_URL, paramMap);
            if (response == null || response.isBlank()) {
                return "Error searching " + engine + ": empty HTTP response";
            }
            JSONObject jsonObject = JSONUtil.parseObj(response);

            // SearchAPI 鉴权失败等：顶层通常只有 error 字段
            if (jsonObject.containsKey("error")) {
                return "Error searching " + engine + ": " + jsonObject.getStr("error");
            }

            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            if (organicResults == null || organicResults.isEmpty()) {
                String preview = response.length() > 300 ? response.substring(0, 300) + "..." : response;
                return "Error searching " + engine + ": no organic_results. Response preview: " + preview;
            }
            int end = Math.min(5, organicResults.size());
            List<Object> objects = organicResults.subList(0, end);
            return objects.stream()
                    .map(obj -> ((JSONObject) obj).toString())
                    .collect(Collectors.joining(","));
        } catch (Exception e) {
            return "Error searching " + engine + ": " + e.getMessage();
        }
    }
}
