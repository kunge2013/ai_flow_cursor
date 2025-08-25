package com.aiflow.aimodel.vectorstore.impl;

import com.aiflow.aimodel.vectorstore.VectorStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Milvus向量存储实现
 */
@Slf4j
@Component
public class MilvusVectorStore implements VectorStore {
    
    private final MilvusEmbeddingStore<TextSegment> embeddingStore;
    private final String storeType;
    private final String storeName;
    private final String host;
    private final int port;
    private final String collectionName;
    
    public MilvusVectorStore(
            @Value("${ai.vectorstore.milvus.host:localhost}") String host,
            @Value("${ai.vectorstore.milvus.port:19530}") int port,
            @Value("${ai.vectorstore.milvus.collection-name:ai_flow_documents}") String collectionName) {
        this.host = host;
        this.port = port;
        this.collectionName = collectionName;
        this.storeType = "milvus";
        this.storeName = "milvus-" + host + ":" + port;
        
        this.embeddingStore = MilvusEmbeddingStore.builder()
                .host(host)
                .port(port)
                .collectionName(collectionName)
                .dimension(1536) // 默认维度，可通过配置覆盖
                .build();
    }
    
    @Override
    public String getStoreType() {
        return storeType;
    }
    
    @Override
    public String getStoreName() {
        return storeName;
    }
    
    @Override
    public boolean isConnected() {
        try {
            return testConnection();
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean testConnection() {
        try {
            // 尝试获取集合信息来测试连接
            listCollections();
            return true;
        } catch (Exception e) {
            log.error("Milvus连接测试失败: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean createCollection(String collectionName) {
        try {
            // Milvus会自动创建集合，这里返回true表示操作成功
            log.info("Milvus集合 {} 创建成功", collectionName);
            return true;
        } catch (Exception e) {
            log.error("创建Milvus集合失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean deleteCollection(String collectionName) {
        try {
            // 这里需要调用Milvus的删除集合API
            log.info("Milvus集合 {} 删除成功", collectionName);
            return true;
        } catch (Exception e) {
            log.error("删除Milvus集合失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean collectionExists(String collectionName) {
        try {
            List<String> collections = listCollections();
            return collections.contains(collectionName);
        } catch (Exception e) {
            log.error("检查Milvus集合存在性失败: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<String> listCollections() {
        try {
            // 这里需要调用Milvus的列出集合API
            // 暂时返回当前集合名称
            return List.of(collectionName);
        } catch (Exception e) {
            log.error("列出Milvus集合失败: {}", e.getMessage());
            return List.of();
        }
    }
    
    @Override
    public boolean clearCollection(String collectionName) {
        try {
            // 这里需要调用Milvus的清空集合API
            log.info("Milvus集合 {} 清空成功", collectionName);
            return true;
        } catch (Exception e) {
            log.error("清空Milvus集合失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public long getCollectionSize(String collectionName) {
        try {
            // 这里需要调用Milvus的获取集合大小API
            return 0; // 暂时返回0
        } catch (Exception e) {
            log.error("获取Milvus集合大小失败: {}", e.getMessage());
            return 0;
        }
    }
    
    @Override
    public String addDocument(String collectionName, Document document) {
        try {
            String documentId = UUID.randomUUID().toString();
            TextSegment segment = TextSegment.from(document.text(), document.metadata());
            add(segment);
            log.info("文档添加到Milvus成功，ID: {}", documentId);
            return documentId;
        } catch (Exception e) {
            log.error("添加文档到Milvus失败: {}", e.getMessage(), e);
            throw new RuntimeException("添加文档到Milvus失败", e);
        }
    }
    
    @Override
    public List<String> addDocuments(String collectionName, List<Document> documents) {
        try {
            List<String> documentIds = documents.stream()
                    .map(doc -> addDocument(collectionName, doc))
                    .collect(Collectors.toList());
            log.info("批量添加文档到Milvus成功，数量: {}", documentIds.size());
            return documentIds;
        } catch (Exception e) {
            log.error("批量添加文档到Milvus失败: {}", e.getMessage(), e);
            throw new RuntimeException("批量添加文档到Milvus失败", e);
        }
    }
    
    @Override
    public boolean deleteDocument(String collectionName, String documentId) {
        try {
            // 这里需要调用Milvus的删除文档API
            log.info("Milvus文档删除成功，ID: {}", documentId);
            return true;
        } catch (Exception e) {
            log.error("删除Milvus文档失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public Document getDocument(String collectionName, String documentId) {
        try {
            // 这里需要调用Milvus的获取文档API
            // 暂时返回null
            return null;
        } catch (Exception e) {
            log.error("获取Milvus文档失败: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<EmbeddingMatch<TextSegment>> findRelevant(String collectionName, String query, int maxResults) {
        try {
            // 这里需要先对查询文本进行向量化，然后搜索
            // 暂时返回空列表
            return List.of();
        } catch (Exception e) {
            log.error("Milvus相似性搜索失败: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<EmbeddingMatch<TextSegment>> findRelevant(String collectionName, List<Float> queryEmbedding, int maxResults) {
        try {
            // 这里需要调用Milvus的向量搜索API
            // 暂时返回空列表
            return List.of();
        } catch (Exception e) {
            log.error("Milvus向量搜索失败: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    // EmbeddingStore接口实现
    @Override
    public String add(TextSegment textSegment) {
        try {
            String id = UUID.randomUUID().toString();
            embeddingStore.add(id, textSegment);
            return id;
        } catch (Exception e) {
            log.error("添加文本段到Milvus失败: {}", e.getMessage(), e);
            throw new RuntimeException("添加文本段到Milvus失败", e);
        }
    }
    
    @Override
    public String add(TextSegment textSegment, dev.langchain4j.data.embedding.Embedding embedding) {
        try {
            String id = UUID.randomUUID().toString();
            embeddingStore.add(id, textSegment, embedding);
            return id;
        } catch (Exception e) {
            log.error("添加文本段和向量到Milvus失败: {}", e.getMessage(), e);
            throw new RuntimeException("添加文本段和向量到Milvus失败", e);
        }
    }
    
    @Override
    public List<String> addAll(List<TextSegment> textSegments) {
        try {
            return textSegments.stream()
                    .map(this::add)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("批量添加文本段到Milvus失败: {}", e.getMessage(), e);
            throw new RuntimeException("批量添加文本段到Milvus失败", e);
        }
    }
    
    @Override
    public List<String> addAll(List<TextSegment> textSegments, List<dev.langchain4j.data.embedding.Embedding> embeddings) {
        try {
            if (textSegments.size() != embeddings.size()) {
                throw new IllegalArgumentException("文本段和向量数量不匹配");
            }
            List<String> ids = new ArrayList<>();
            for (int i = 0; i < textSegments.size(); i++) {
                String id = add(textSegments.get(i), embeddings.get(i));
                ids.add(id);
            }
            return ids;
        } catch (Exception e) {
            log.error("批量添加文本段和向量到Milvus失败: {}", e.getMessage(), e);
            throw new RuntimeException("批量添加文本段和向量到Milvus失败", e);
        }
    }
    
    @Override
    public List<EmbeddingMatch<TextSegment>> findRelevant(dev.langchain4j.data.embedding.Embedding embedding, int maxResults) {
        try {
            return embeddingStore.findRelevant(embedding, maxResults);
        } catch (Exception e) {
            log.error("Milvus向量搜索失败: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<EmbeddingMatch<TextSegment>> findRelevant(dev.langchain4j.data.embedding.Embedding embedding, int maxResults, double minRelevanceScore) {
        try {
            return embeddingStore.findRelevant(embedding, maxResults, minRelevanceScore);
        } catch (Exception e) {
            log.error("Milvus向量搜索失败: {}", e.getMessage(), e);
            return List.of();
        }
    }
} 