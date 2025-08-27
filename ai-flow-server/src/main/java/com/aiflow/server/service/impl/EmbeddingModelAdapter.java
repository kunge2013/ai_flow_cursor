package com.aiflow.server.service.impl;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;

import java.util.List;

/**
 * 嵌入模型适配器
 * 将ai-flow-model的EmbeddingModel转换为LangChain4j的EmbeddingModel
 */
public class EmbeddingModelAdapter implements EmbeddingModel {
    
    private final com.aiflow.aimodel.embedding.EmbeddingModel aiFlowEmbeddingModel;
    
    public EmbeddingModelAdapter(com.aiflow.aimodel.embedding.EmbeddingModel aiFlowEmbeddingModel) {
        this.aiFlowEmbeddingModel = aiFlowEmbeddingModel;
    }
    
    @Override
    public Response<Embedding> embed(String text) {
        try {
            Embedding embedding = aiFlowEmbeddingModel.embed(text);
            return Response.from(embedding);
        } catch (Exception e) {
            throw new RuntimeException("嵌入失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
        try {
            List<Embedding> embeddings = aiFlowEmbeddingModel.embedAllSegments(textSegments);
            return Response.from(embeddings);
        } catch (Exception e) {
            throw new RuntimeException("批量嵌入失败: " + e.getMessage(), e);
        }
    }
} 