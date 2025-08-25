package com.aiflow.server.service;

import com.aiflow.server.dto.VectorSearchDtos.*;

import java.util.List;

public interface VectorSearchService {
    
    /**
     * 向量化文档内容
     */
    VectorEmbeddingResponse embedDocument(String content, String vectorModel);
    
    /**
     * 向量相似度搜索
     */
    VectorSearchResponse searchSimilar(String query, String kbId, int topK, double scoreThreshold);
    
    /**
     * 批量向量化文档
     */
    List<VectorEmbeddingResponse> batchEmbedDocuments(List<String> contents, String vectorModel);
    
    /**
     * 保存文档向量
     */
    void saveDocumentVector(String documentId, String kbId, String content, String vectorModel);
    
    /**
     * 删除文档向量
     */
    void deleteDocumentVector(String documentId);
    
    /**
     * 更新文档向量
     */
    void updateDocumentVector(String documentId, String content, String vectorModel);
} 