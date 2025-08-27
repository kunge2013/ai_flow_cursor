package com.aiflow.aimodel.adapter.impl;

import com.aiflow.aimodel.adapter.AiModelAdapter;
import com.aiflow.aimodel.model.AiModelConfig;
import com.aiflow.aimodel.model.AiModelType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * 智普AI模型适配器
 * 基于HTTP客户端调用智普AI API
 */
@Slf4j
@Component
public class ZhipuAiAdapter implements AiModelAdapter {
    
    private static final String DEFAULT_API_ENDPOINT = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public AiModelType getSupportedType() {
        return AiModelType.ZHIPU_AI;
    }
    
    @Override
    public boolean isValidConfig(AiModelConfig config) {
        return config != null 
                && config.getType() == AiModelType.ZHIPU_AI
                && config.getApiKey() != null && !config.getApiKey().trim().isEmpty()
                && config.getBaseModel() != null && !config.getBaseModel().trim().isEmpty();
    }
    
    @Override
    public CompletableFuture<String> testConnection(AiModelConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String response = generateText(config, "Hello, this is a test message.");
                log.info("智普AI连接测试成功: {}", response);
                return "连接成功！测试响应: " + response;
            } catch (Exception e) {
                log.error("智普AI连接测试失败", e);
                return "连接失败: " + e.getMessage();
            }
        });
    }
    
    @Override
    public String generateText(AiModelConfig config, String prompt) {
        try {
            return callZhipuAiApi(config, prompt, null, null, null);
        } catch (Exception e) {
            log.error("智普AI文本生成失败", e);
            throw new RuntimeException("智普AI文本生成失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CompletableFuture<String> generateTextAsync(AiModelConfig config, String prompt) {
        return CompletableFuture.supplyAsync(() -> generateText(config, prompt));
    }
    
    @Override
    public String generateText(AiModelConfig config, String prompt, 
                             Integer maxTokens, Double temperature, Double topP) {
        try {
            return callZhipuAiApi(config, prompt, maxTokens, temperature, topP);
        } catch (Exception e) {
            log.error("智普AI文本生成失败", e);
            throw new RuntimeException("智普AI文本生成失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CompletableFuture<String> generateTextAsync(AiModelConfig config, String prompt,
                                                     Integer maxTokens, Double temperature, Double topP) {
        return CompletableFuture.supplyAsync(() -> 
            generateText(config, prompt, maxTokens, temperature, topP));
    }
    
    @Override
    public Stream<String> generateTextStream(AiModelConfig config, String prompt) {
        try {
            String response = generateText(config, prompt);
            return Stream.of(response);
        } catch (Exception e) {
            log.error("智普AI流式文本生成失败", e);
            throw new RuntimeException("智普AI流式文本生成失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Stream<String> generateTextStream(AiModelConfig config, String prompt,
                                          Integer maxTokens, Double temperature, Double topP) {
        try {
            String response = generateText(config, prompt, maxTokens, temperature, topP);
            return Stream.of(response);
        } catch (Exception e) {
            log.error("智普AI流式文本生成失败", e);
            throw new RuntimeException("智普AI流式文本生成失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ModelInfo getModelInfo(AiModelConfig config) {
        return new ModelInfo(
            config.getBaseModel(),
            config.getName(),
            "智普AI",
            config.getMaxTokens().longValue(),
            new String[]{"text-generation", "chat", "streaming"},
            LocalDateTime.now()
        );
    }
    
    @Override
    public boolean isAvailable(AiModelConfig config) {
        try {
            return testConnection(config).get() != null;
        } catch (Exception e) {
            log.error("检查智普AI模型可用性失败", e);
            return false;
        }
    }
    
    @Override
    public ModelStatus getModelStatus(AiModelConfig config) {
        if (!isValidConfig(config)) {
            return ModelStatus.ERROR;
        }
        
        try {
            if (isAvailable(config)) {
                return ModelStatus.AVAILABLE;
            } else {
                return ModelStatus.UNAVAILABLE;
            }
        } catch (Exception e) {
            return ModelStatus.ERROR;
        }
    }
    
    /**
     * 调用智普AI API
     */
    private String callZhipuAiApi(AiModelConfig config, String prompt, 
                                 Integer maxTokens, Double temperature, Double topP) {
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + config.getApiKey());
            
            // 构建请求体
            String requestBody = buildRequestBody(config, prompt, maxTokens, temperature, topP);
            
            // 发送请求
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            String apiEndpoint = config.getApiEndpoint() != null ? 
                config.getApiEndpoint() : DEFAULT_API_ENDPOINT;
            
            ResponseEntity<String> response = restTemplate.exchange(
                apiEndpoint, 
                HttpMethod.POST, 
                entity, 
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return parseResponse(response.getBody());
            } else {
                throw new RuntimeException("智普AI API调用失败，状态码: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("调用智普AI API失败", e);
            throw new RuntimeException("调用智普AI API失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 构建请求体
     */
    private String buildRequestBody(AiModelConfig config, String prompt, 
                                  Integer maxTokens, Double temperature, Double topP) {
        try {
            // 使用配置中的参数，如果没有则使用传入的参数
            int finalMaxTokens = maxTokens != null ? maxTokens : config.getMaxTokens();
            double finalTemperature = temperature != null ? temperature : config.getTemperature();
            double finalTopP = topP != null ? topP : config.getTopP();
            
            // 构建智普AI API请求格式
            String requestBody = String.format(
                "{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}]," +
                "\"max_tokens\":%d,\"temperature\":%.2f,\"top_p\":%.2f,\"stream\":false}",
                config.getBaseModel(),
                prompt.replace("\"", "\\\""),
                finalMaxTokens,
                finalTemperature,
                finalTopP
            );
            
            return requestBody;
        } catch (Exception e) {
            log.error("构建智普AI请求体失败", e);
            throw new RuntimeException("构建智普AI请求体失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 解析API响应
     */
    private String parseResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            
            // 检查是否有错误
            if (rootNode.has("error")) {
                String errorMessage = rootNode.get("error").get("message").asText();
                throw new RuntimeException("智普AI API错误: " + errorMessage);
            }
            
            // 提取生成的文本
            if (rootNode.has("choices") && rootNode.get("choices").isArray() && 
                rootNode.get("choices").size() > 0) {
                JsonNode choice = rootNode.get("choices").get(0);
                if (choice.has("message") && choice.get("message").has("content")) {
                    return choice.get("message").get("content").asText();
                }
            }
            
            throw new RuntimeException("无法解析智普AI API响应: " + responseBody);
            
        } catch (Exception e) {
            log.error("解析智普AI API响应失败", e);
            throw new RuntimeException("解析智普AI API响应失败: " + e.getMessage(), e);
        }
    }
}
