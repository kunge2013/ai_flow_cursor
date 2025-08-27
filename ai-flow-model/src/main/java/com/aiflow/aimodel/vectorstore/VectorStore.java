package com.aiflow.aimodel.vectorstore;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;


/**
 * 向量存储抽象接口
 */
public interface VectorStore extends EmbeddingStore<TextSegment> {
    
    /**
     * 获取存储类型
     */
    String getStoreType();
    
    /**
     * 获取存储名称
     */
    String getStoreName();
    
    /**
     * 获取连接状态
     */
    } 