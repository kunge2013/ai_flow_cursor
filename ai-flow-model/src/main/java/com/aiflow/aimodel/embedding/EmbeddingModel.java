package com.aiflow.aimodel.embedding;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;

import java.util.List;

/**
 * 向量模型抽象接口
 */
public interface EmbeddingModel {
    
    /**
     * 获取模型名称
     */
    String getModelName();
    
    /**
     * 获取模型类型
     */
    String getModelType();
    
    /**
     * 获取向量维度
     */
    int getDimensions();
    
    /**
     * 将文本转换为向量
     */
    Embedding embed(String text);
    
    /**
     * 批量将文本转换为向量
     */
    List<Embedding> embedAllTexts(List<String> texts);
    
    /**
     * 将文本段落转换为向量
     */
    Embedding embed(TextSegment textSegment);
    
    /**
     * 批量将文本段落转换为向量
     */
    List<Embedding> embedAllSegments(List<TextSegment> textSegments);
    
    /**
     * 测试模型连接
     */
    boolean testConnection();
} 