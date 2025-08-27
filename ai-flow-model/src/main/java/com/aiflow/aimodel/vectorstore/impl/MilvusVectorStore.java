package com.aiflow.aimodel.vectorstore.impl;

import com.aiflow.aimodel.vectorstore.VectorStore;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import dev.langchain4j.data.embedding.Embedding;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Milvus向量存储实现
 */

@Slf4j
@Service
public class MilvusVectorStore implements VectorStore {
    
    private final String storeType;
    private final String storeName;
    private final String host;
    private final int port;
    private final String collectionName;
    private final MilvusEmbeddingStore embeddingStore;
    
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
                .dimension(384) // 默认维度，可通过配置覆盖
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
    
    // EmbeddingStore<TextSegment> 接口方法实现
    @Override
    public String add(Embedding embedding, TextSegment textSegment) {
        return embeddingStore.add(embedding, textSegment);
    }
    
    @Override
    public void add(String id, Embedding embedding) {
        embeddingStore.add(id, embedding);
    }
    
    @Override
    public String add(Embedding embedding) {
        return embeddingStore.add(embedding);
    }
    
    @Override
    public List<String> addAll(List<Embedding> embeddings) {
        return embeddingStore.addAll(embeddings);
    }
    
    @Override
    public List<String> addAll(List<Embedding> embeddings, List<TextSegment> textSegments) {
        return embeddingStore.addAll(embeddings, textSegments);
    }
    
    @Override
    public List<EmbeddingMatch<TextSegment>> findRelevant(Embedding embedding, int maxResults, double minRelevanceScore) {
        return embeddingStore.findRelevant(embedding, maxResults, minRelevanceScore);
    }
}
