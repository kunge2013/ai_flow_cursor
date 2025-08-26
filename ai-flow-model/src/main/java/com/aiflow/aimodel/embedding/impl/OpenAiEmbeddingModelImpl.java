package com.aiflow.aimodel.embedding.impl;

import com.aiflow.aimodel.embedding.EmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
// import dev.langchain4j.model.embedding.OpenAiEmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OpenAI向量模型实现
 * 暂时注释掉，等待依赖问题解决
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "ai.model.openai", name = "enable", havingValue = "true")
public class OpenAiEmbeddingModelImpl implements EmbeddingModel {
    
    // private final OpenAiEmbeddingModel embeddingModel;
    private final String modelName;
    private final String modelType;
    private final int dimensions;
    
    public OpenAiEmbeddingModelImpl(
            @Value("${ai.model.openai.api-key}") String apiKey,
            @Value("${ai.model.openai.base-url:https://api.openai.com}") String baseUrl,
            @Value("${ai.model.openai.embedding-model:text-embedding-ada-002}") String embeddingModelName) {
        // this.embeddingModel = OpenAiEmbeddingModel.builder()
        //         .apiKey(apiKey)
        //         .baseUrl(baseUrl)
        //         .modelName(embeddingModelName)
        //         .build();
        this.modelName = embeddingModelName;
        this.modelType = "openai";
        // OpenAI text-embedding-ada-002 模型维度为1536
        this.dimensions = "text-embedding-ada-002".equals(embeddingModelName) ? 1536 : 3072;
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
            // return embeddingModel.embed(text);
            throw new RuntimeException("OpenAI向量化暂时禁用，等待依赖问题解决");
        } catch (Exception e) {
            log.error("OpenAI向量化失败: {}", e.getMessage(), e);
            throw new RuntimeException("OpenAI向量化失败", e);
        }
    }
    
    @Override
    public List<Embedding> embedAllTexts(List<String> texts) {
        try {
            // return embeddingModel.embedAll(texts);
            throw new RuntimeException("OpenAI批量向量化暂时禁用，等待依赖问题解决");
        } catch (Exception e) {
            log.error("OpenAI批量向量化失败: {}", e.getMessage(), e);
            throw new RuntimeException("OpenAI批量向量化失败", e);
        }
    }
    
    @Override
    public Embedding embed(TextSegment textSegment) {
        return embed(textSegment.text());
    }
    
    @Override
    public List<Embedding> embedAllSegments(List<TextSegment> textSegments) {
        List<String> texts = textSegments.stream()
                .map(TextSegment::text)
                .collect(Collectors.toList());
        return embedAllTexts(texts);
    }
    
    @Override
    public boolean testConnection() {
        try {
            // 测试一个简单的文本向量化
            // embed("测试连接");
            return false; // 暂时返回false
        } catch (Exception e) {
            log.error("OpenAI连接测试失败: {}", e.getMessage());
            return false;
        }
    }
} 