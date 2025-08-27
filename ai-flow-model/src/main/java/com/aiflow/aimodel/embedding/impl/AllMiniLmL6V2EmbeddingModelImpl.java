package com.aiflow.aimodel.embedding.impl;

import com.aiflow.aimodel.embedding.EmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * All-MiniLM-L6-v2向量模型实现
 * 基于Sentence Transformers的轻量级多语言嵌入模型
 */
@Slf4j
@Component
@ConditionalOnClass(name = "dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel")
public class AllMiniLmL6V2EmbeddingModelImpl implements EmbeddingModel {
    
    private final AllMiniLmL6V2EmbeddingModel embeddingModel;
    private final String modelName;
    private final String modelType;
    private final int dimensions;
    
    public AllMiniLmL6V2EmbeddingModelImpl() {
        this.embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        this.modelName = "all-minilm-l6-v2";
        this.modelType = "sentence-transformers";
        // All-MiniLM-L6-v2 模型维度为384
        this.dimensions = 384;
        
        log.info("初始化All-MiniLM-L6-v2嵌入模型，维度: {}", dimensions);
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
            return embeddingModel.embed(text).content();
        } catch (Exception e) {
            log.error("All-MiniLM-L6-v2向量化失败: {}", e.getMessage(), e);
            throw new RuntimeException("All-MiniLM-L6-v2向量化失败", e);
        }
    }
    
    @Override
    public List<Embedding> embedAllTexts(List<String> texts) {
        try {
            List<TextSegment> segments = texts.stream()
                    .map(TextSegment::from)
                    .collect(Collectors.toList());
            return embeddingModel.embedAll(segments).content();
        } catch (Exception e) {
            log.error("All-MiniLM-L6-v2批量向量化失败: {}", e.getMessage(), e);
            throw new RuntimeException("All-MiniLM-L6-v2批量向量化失败", e);
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
            Embedding testEmbedding = embed("测试连接");
            return testEmbedding != null && testEmbedding.vector().length == dimensions;
        } catch (Exception e) {
            log.error("All-MiniLM-L6-v2连接测试失败: {}", e.getMessage());
            return false;
        }
    }
} 