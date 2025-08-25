package com.aiflow.aimodel.embedding.impl;

import com.aiflow.aimodel.embedding.EmbeddingModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 智普AI向量模型实现
 */
@Slf4j
@Component
public class ZhipuAiEmbeddingModel implements EmbeddingModel {
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey;
    private final String modelName;
    private final String modelType;
    private final int dimensions;
    
    public ZhipuAiEmbeddingModel(
            @Value("${ai.model.zhipu-ai.api-key:}") String apiKey,
            @Value("${ai.model.zhipu-ai.embedding-model:text-embedding-v2}") String embeddingModelName) {
        this.apiKey = apiKey;
        this.modelName = embeddingModelName;
        this.modelType = "zhipu-ai";
        // 智普AI text-embedding-v2 模型维度为1024
        this.dimensions = "text-embedding-v2".equals(embeddingModelName) ? 1024 : 1536;
    }
    
    @Override
    public String getModelName() {
        return modelName;
    }
    
    @Override
    public String getModelType() {
        return modelType;
    }
    
    @Override
    public int getDimensions() {
        return dimensions;
    }
    
    @Override
    public Embedding embed(String text) {
        try {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new RuntimeException("智普AI API Key未配置");
            }
            
            // 调用智普AI API
            List<Double> embedding = callZhipuAiApi(text);
            
            // 创建Embedding对象
            return new Embedding(embedding);
            
        } catch (Exception e) {
            log.error("智普AI向量化失败: {}", e.getMessage(), e);
            throw new RuntimeException("智普AI向量化失败", e);
        }
    }
    
    @Override
    public List<Embedding> embedAll(List<String> texts) {
        try {
            return texts.stream()
                    .map(this::embed)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("智普AI批量向量化失败: {}", e.getMessage(), e);
            throw new RuntimeException("智普AI批量向量化失败", e);
        }
    }
    
    @Override
    public Embedding embed(TextSegment textSegment) {
        return embed(textSegment.text());
    }
    
    @Override
    public List<Embedding> embedAll(List<TextSegment> textSegments) {
        List<String> texts = textSegments.stream()
                .map(TextSegment::text)
                .collect(Collectors.toList());
        return embedAll(texts);
    }
    
    @Override
    public boolean testConnection() {
        try {
            // 测试一个简单的文本向量化
            embed("测试连接");
            return true;
        } catch (Exception e) {
            log.error("智普AI连接测试失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 调用智普AI API进行向量化
     */
    private List<Double> callZhipuAiApi(String text) throws Exception {
        String url = "https://open.bigmodel.cn/api/paas/v4/embeddings";
        
        // 构建请求体
        String requestBody = String.format(
            "{\"model\":\"%s\",\"input\":[\"%s\"]}",
            modelName,
            text.replace("\"", "\\\"")
        );
        
        // 创建HTTP请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        // 发送请求
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("智普AI API调用失败: " + response.statusCode());
        }
        
        // 解析响应
        JsonNode responseJson = objectMapper.readTree(response.body());
        
        if (responseJson.has("error")) {
            throw new RuntimeException("智普AI API返回错误: " + responseJson.get("error").asText());
        }
        
        // 提取向量数据
        JsonNode data = responseJson.get("data");
        if (data == null || !data.isArray() || data.size() == 0) {
            throw new RuntimeException("智普AI API响应格式错误");
        }
        
        JsonNode embedding = data.get(0).get("embedding");
        if (embedding == null || !embedding.isArray()) {
            throw new RuntimeException("智普AI API响应中缺少向量数据");
        }
        
        // 转换为Double列表
        List<Double> result = new java.util.ArrayList<>();
        for (JsonNode value : embedding) {
            result.add(value.asDouble());
        }
        
        return result;
    }
} 