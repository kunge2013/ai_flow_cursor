package com.aiflow.aimodel.embedding.impl;

import com.aiflow.aimodel.embedding.EmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.QianfanEmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 千帆向量模型实现
 */
@Slf4j
@Component
public class QianfanEmbeddingModel implements EmbeddingModel {
    
    private final QianfanEmbeddingModel embeddingModel;
    private final String modelName;
    private final String modelType;
    private final int dimensions;
    
    public QianfanEmbeddingModel(
            @Value("${ai.model.qianfan.api-key}") String apiKey,
            @Value("${ai.model.qianfan.secret-key}") String secretKey,
            @Value("${ai.model.qianfan.embedding-model:text-embedding-v1}") String embeddingModelName) {
        this.embeddingModel = QianfanEmbeddingModel.builder()
                .apiKey(apiKey)
                .secretKey(secretKey)
                .modelName(embeddingModelName)
                .build();
        this.modelName = embeddingModelName;
        this.modelType = "qianfan";
        // 千帆 text-embedding-v1 模型维度为1024
        this.dimensions = "text-embedding-v1".equals(embeddingModelName) ? 1024 : 1536;
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
            return embeddingModel.embed(text);
        } catch (Exception e) {
            log.error("千帆向量化失败: {}", e.getMessage(), e);
            throw new RuntimeException("千帆向量化失败", e);
        }
    }
    
    @Override
    public List<Embedding> embedAll(List<String> texts) {
        try {
            return embeddingModel.embedAll(texts);
        } catch (Exception e) {
            log.error("千帆批量向量化失败: {}", e.getMessage(), e);
            throw new RuntimeException("千帆批量向量化失败", e);
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
            log.error("千帆连接测试失败: {}", e.getMessage());
            return false;
        }
    }
} 