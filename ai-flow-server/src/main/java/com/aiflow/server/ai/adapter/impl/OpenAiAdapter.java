package com.aiflow.server.ai.adapter.impl;

import com.aiflow.server.ai.adapter.AiModelAdapter;
import com.aiflow.server.dto.ModelDTO;
import com.aiflow.server.dto.ModelTestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * OpenAI模型适配器
 */
@Slf4j
@Component
public class OpenAiAdapter implements AiModelAdapter {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String testConnection(ModelTestDTO testDTO) {
        try {
            String requestBody = String.format("""
                {
                    "model": "gpt-3.5-turbo",
                    "messages": [
                        {
                            "role": "user",
                            "content": "Hello, this is a test message."
                        }
                    ],
                    "max_tokens": %d,
                    "temperature": %f
                }
                """, testDTO.getMaxTokens(), testDTO.getTemperature());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(testDTO.getApiEndpoint() + "/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + testDTO.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                log.info("OpenAI连接测试成功: {}", response.body());
                return "连接成功！测试响应: " + response.body();
            } else {
                log.error("OpenAI连接测试失败，状态码: {}", response.statusCode());
                return "连接失败，状态码: " + response.statusCode() + ", 响应: " + response.body();
            }
        } catch (Exception e) {
            log.error("OpenAI连接测试失败", e);
            return "连接失败: " + e.getMessage();
        }
    }

    @Override
    public String generateText(ModelDTO modelDTO, String prompt, Integer maxTokens, Double temperature) {
        try {
            String requestBody = String.format("""
                {
                    "model": "%s",
                    "messages": [
                        {
                            "role": "user",
                            "content": "%s"
                        }
                    ],
                    "max_tokens": %d,
                    "temperature": %f
                }
                """, 
                modelDTO.getBaseModel(),
                prompt.replace("\"", "\\\""),
                maxTokens != null ? maxTokens : modelDTO.getMaxTokens(),
                temperature != null ? temperature : modelDTO.getTemperature()
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(modelDTO.getApiEndpoint() + "/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + modelDTO.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                log.info("OpenAI文本生成成功: {}", response.body());
                return response.body();
            } else {
                throw new RuntimeException("OpenAI文本生成失败，状态码: " + response.statusCode() + ", 响应: " + response.body());
            }
        } catch (Exception e) {
            log.error("OpenAI文本生成失败", e);
            throw new RuntimeException("OpenAI文本生成失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getModelType() {
        return "openai";
    }

    @Override
    public boolean isValidConfig(ModelDTO modelDTO) {
        return modelDTO != null 
                && modelDTO.getApiKey() != null && !modelDTO.getApiKey().trim().isEmpty()
                && modelDTO.getApiEndpoint() != null && !modelDTO.getApiEndpoint().trim().isEmpty()
                && modelDTO.getBaseModel() != null && !modelDTO.getBaseModel().trim().isEmpty();
    }
} 