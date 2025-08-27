package com.aiflow.aimodel.service;

import java.util.List;

/**
 * RAG服务接口
 * 提供基于LangChain4j的检索增强生成功能
 */
public interface RagService {
    
    /**
     * 处理文档上传并进行RAG处理
     * @param kbId 知识库ID
     * @param fileName 文件名
     * @param content 文档内容
     * @param contentType 内容类型
     * @return 处理结果
     */
    DocumentProcessResult processDocument(String kbId, String fileName, String content, String contentType);
    
    /**
     * 处理文档内容并进行RAG处理
     * @param kbId 知识库ID
     * @param title 文档标题
     * @param content 文档内容
     * @param contentType 内容类型
     * @return 处理结果
     */
    DocumentProcessResult processDocumentContent(String kbId, String title, String content, String contentType);
    
    /**
     * 使用RAG进行相似度搜索
     * @param query 查询文本
     * @param kbId 知识库ID
     * @param topK 返回结果数量
     * @param scoreThreshold 相似度阈值
     * @return 搜索结果
     */
    VectorService.VectorSearchResult searchSimilar(String query, String kbId, int topK, double scoreThreshold);
    
    /**
     * 获取知识库的RAG统计信息
     * @param kbId 知识库ID
     * @return 统计信息
     */
    VectorService.VectorStatistics getRagStatistics(String kbId);
    
    /**
     * 文档处理结果
     */
    class DocumentProcessResult {
        private String documentId;
        private String status;
        private String message;
        private List<String> chunks;
        private int totalChunks;
        private long processingTime;
        
        // Getters and Setters
        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public List<String> getChunks() { return chunks; }
        public void setChunks(List<String> chunks) { this.chunks = chunks; }
        
        public int getTotalChunks() { return totalChunks; }
        public void setTotalChunks(int totalChunks) { this.totalChunks = totalChunks; }
        
        public long getProcessingTime() { return processingTime; }
        public void setProcessingTime(long processingTime) { this.processingTime = processingTime; }
    }
}
