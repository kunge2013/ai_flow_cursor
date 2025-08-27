package com.aiflow.server.service;

import com.aiflow.server.dto.VectorSearchDtos.VectorSearchRequest;
import com.aiflow.server.dto.VectorSearchDtos.VectorSearchResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * LangChain4j RAG服务接口
 * 提供基于LangChain4j的检索增强生成功能
 */
public interface LangChainRagService {
    
    /**
     * 处理文档上传并进行RAG处理
     * @param kbId 知识库ID
     * @param file 上传的文件
     * @return 处理结果
     */
    DocumentProcessResult processDocument(String kbId, MultipartFile file);
    
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
     * @param request 搜索请求
     * @return 搜索响应
     */
    VectorSearchResponse searchSimilar(VectorSearchRequest request);
    
    /**
     * 获取知识库的RAG统计信息
     * @param kbId 知识库ID
     * @return 统计信息
     */
    RagStatistics getRagStatistics(String kbId);
    
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
    
    /**
     * RAG统计信息
     */
    class RagStatistics {
        private String kbId;
        private long totalDocuments;
        private long totalChunks;
        private long totalEmbeddings;
        private String vectorModel;
        private long lastUpdated;
        
        // Getters and Setters
        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
        
        public long getTotalDocuments() { return totalDocuments; }
        public void setTotalDocuments(long totalDocuments) { this.totalDocuments = totalDocuments; }
        
        public long getTotalChunks() { return totalChunks; }
        public void setTotalChunks(long totalChunks) { this.totalChunks = totalChunks; }
        
        public long getTotalEmbeddings() { return totalEmbeddings; }
        public void setTotalEmbeddings(long totalEmbeddings) { this.totalEmbeddings = totalEmbeddings; }
        
        public String getVectorModel() { return vectorModel; }
        public void setVectorModel(String vectorModel) { this.vectorModel = vectorModel; }
        
        public long getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
    }
}
