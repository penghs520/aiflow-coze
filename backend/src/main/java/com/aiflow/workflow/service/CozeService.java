package com.aiflow.workflow.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 扣子API集成服务
 */
@Slf4j
@Service
public class CozeService {

    @Value("${app.coze.base-url}")
    private String baseUrl;

    @Value("${app.coze.api-key}")
    private String apiKey;

    @Value("${app.coze.simulate:true}")
    private boolean simulate;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CozeService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 执行工作流
     */
    public CozeExecuteResponse executeWorkflow(String workflowId, Map<String, Object> parameters) {
        if (simulate) {
            return simulateExecute(workflowId);
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("workflow_id", workflowId);
            requestBody.put("parameters", parameters);
            requestBody.put("execute_mode", "async");

            String json = objectMapper.writeValueAsString(requestBody);

            Request request = new Request.Builder()
                    .url(baseUrl + "/v1/workflow/run")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(json, MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("请求失败: " + response);
                }

                String responseBody = response.body().string();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                CozeExecuteResponse result = new CozeExecuteResponse();
                result.setExecuteId(jsonNode.get("execute_id").asText());
                result.setStatus("processing");
                return result;
            }
        } catch (Exception e) {
            log.error("执行工作流失败", e);
            throw new RuntimeException("执行工作流失败: " + e.getMessage());
        }
    }

    /**
     * 查询任务状态
     */
    public CozeTaskStatus queryTaskStatus(String workflowId, String executeId) {
        if (simulate) {
            return simulateStatus(executeId);
        }

        try {
            Request request = new Request.Builder()
                    .url(baseUrl + "/v1/workflows/" + workflowId + "/run_histories/" + executeId)
                    .header("Authorization", "Bearer " + apiKey)
                    .get()
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("请求失败: " + response);
                }

                String responseBody = response.body().string();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                CozeTaskStatus status = new CozeTaskStatus();
                status.setStatus(jsonNode.get("status").asText());
                status.setProgress(jsonNode.has("progress") ? jsonNode.get("progress").asInt() : 0);

                if (jsonNode.has("result")) {
                    status.setResult(jsonNode.get("result").toString());
                }
                if (jsonNode.has("error_message")) {
                    status.setErrorMessage(jsonNode.get("error_message").asText());
                }

                return status;
            }
        } catch (Exception e) {
            log.error("查询任务状态失败", e);
            throw new RuntimeException("查询任务状态失败: " + e.getMessage());
        }
    }

    /**
     * 模拟执行
     */
    private CozeExecuteResponse simulateExecute(String workflowId) {
        log.info("【模拟模式】执行工作流: {}", workflowId);
        CozeExecuteResponse response = new CozeExecuteResponse();
        response.setExecuteId("sim_" + System.currentTimeMillis());
        response.setStatus("processing");
        return response;
    }

    /**
     * 模拟查询状态
     */
    private CozeTaskStatus simulateStatus(String executeId) {
        log.info("【模拟模式】查询任务状态: {}", executeId);
        CozeTaskStatus status = new CozeTaskStatus();
        status.setStatus("completed");
        status.setProgress(100);
        status.setResult("{\"output\": \"模拟结果\"}");
        return status;
    }

    @Data
    public static class CozeExecuteResponse {
        private String executeId;
        private String status;
    }

    @Data
    public static class CozeTaskStatus {
        private String status;
        private Integer progress;
        private String result;
        private String errorMessage;

        public boolean isCompleted() {
            return "completed".equals(status);
        }

        public boolean isFailed() {
            return "failed".equals(status);
        }
    }

    /**
     * 查询工作流参数定义
     */
    public List<Map<String, Object>> getWorkflowParameters(String workflowId) {
        if (simulate) {
            // 模拟模式返回空参数列表
            return new ArrayList<>();
        }

        try {
            // 构建带参数的请求 URL
            String urlStr = baseUrl + "/v1/workflows/" + workflowId + "?include_input_output=true";

            Request request = new Request.Builder()
                    .url(urlStr)
                    .header("Authorization", "Bearer " + apiKey)
                    .get()
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("请求失败: " + response);
                }

                ResponseBody body = response.body();
                if (body == null) {
                    throw new IOException("响应体为空");
                }
                String responseBody = body.string();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                // 解析返回的数据结构: data.input.parameters
                if (jsonNode.has("data") && jsonNode.get("data").has("input")) {
                    JsonNode inputNode = jsonNode.get("data").get("input");
                    if (inputNode.has("parameters")) {
                        JsonNode parameters = inputNode.get("parameters");
                        // 将参数对象转换为数组格式
                        return convertParametersToArray(parameters);
                    }
                }
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("查询工作流参数定义失败", e);
            throw new RuntimeException("查询工作流参数定义失败: " + e.getMessage());
        }
    }

    /**
     * 将参数对象转换为数组格式
     * 输入: {"img": {...}, "title": {...}}
     * 输出: [{"key": "img", ...}, {"key": "title", ...}]
     */
    private List<Map<String, Object>> convertParametersToArray(JsonNode parametersNode) {
        if (!parametersNode.isObject()) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        var fields = parametersNode.fields();

        while (fields.hasNext()) {
            var entry = fields.next();
            Map<String, Object> paramObj = new LinkedHashMap<>();

            // 设置 key 字段
            paramObj.put("key", entry.getKey());

            // 复制其他字段
            var valueNode = entry.getValue();
            if (valueNode.has("required")) {
                paramObj.put("required", valueNode.get("required").asBoolean());
            } else {
                paramObj.put("required", false);
            }
            if (valueNode.has("description")) {
                paramObj.put("description", valueNode.get("description").asText());
            }
            if (valueNode.has("type")) {
                paramObj.put("type", convertCozeType(valueNode.get("type").asText()));
            }
            if (valueNode.has("default_value")) {
                paramObj.put("default", valueNode.get("default_value").asText());
            }

            result.add(paramObj);
        }

        // 排序：必填参数在前，然后按 key 字母顺序
        result.sort((a, b) -> {
            boolean aRequired = (boolean) a.get("required");
            boolean bRequired = (boolean) b.get("required");
            String aKey = (String) a.get("key");
            String bKey = (String) b.get("key");

            if (aRequired != bRequired) {
                return aRequired ? -1 : 1; // 必填在前
            }
            return aKey.compareTo(bKey); // 按 key 字母顺序
        });

        return result;
    }

    /**
     * 转换 Coze 参数类型为前端统一类型
     */
    private String convertCozeType(String cozeType) {
        return switch (cozeType) {
            case "integer", "number" -> "number";
            case "image" -> "image_file";
            case "video" -> "video_file";
            case "audio" -> "audio_file";
            case "boolean" -> "boolean";
            case "array" -> "multi_select";
            default -> "text";
        };
    }
}


