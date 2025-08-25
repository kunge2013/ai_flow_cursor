package com.aiflow.aimodel.factory;

import com.aiflow.aimodel.vectorstore.VectorStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 向量存储工厂
 */
@Slf4j
@Component
public class VectorStoreFactory {
    
    private final Map<String, VectorStore> vectorStores = new ConcurrentHashMap<>();
    private final String defaultStoreType;
    
    @Autowired
    public VectorStoreFactory(List<VectorStore> stores,
                            @Value("${ai.vectorstore.default-type:milvus}") String defaultStoreType) {
        this.defaultStoreType = defaultStoreType;
        
        for (VectorStore store : stores) {
            String key = store.getStoreType() + ":" + store.getStoreName();
            vectorStores.put(key, store);
            log.info("注册向量存储: {} -> {}", key, store.getClass().getSimpleName());
        }
    }
    
    /**
     * 获取默认的向量存储
     */
    public VectorStore getDefaultVectorStore() {
        return getVectorStore(defaultStoreType);
    }
    
    /**
     * 根据存储类型获取向量存储
     */
    public VectorStore getVectorStore(String storeType) {
        // 查找匹配的存储
        for (Map.Entry<String, VectorStore> entry : vectorStores.entrySet()) {
            if (entry.getKey().startsWith(storeType + ":")) {
                return entry.getValue();
            }
        }
        
        // 如果没有找到，返回默认的存储
        for (Map.Entry<String, VectorStore> entry : vectorStores.entrySet()) {
            if (entry.getKey().startsWith(defaultStoreType + ":")) {
                log.warn("未找到类型为 {} 的向量存储，使用默认的 {} 存储", storeType, defaultStoreType);
                return entry.getValue();
            }
        }
        
        throw new RuntimeException("未找到可用的向量存储");
    }
    
    /**
     * 根据存储类型和名称获取向量存储
     */
    public VectorStore getVectorStore(String storeType, String storeName) {
        String key = storeType + ":" + storeName;
        VectorStore store = vectorStores.get(key);
        if (store == null) {
            throw new RuntimeException("未找到向量存储: " + key);
        }
        return store;
    }
    
    /**
     * 获取所有可用的向量存储
     */
    public List<VectorStore> getAllVectorStores() {
        return List.copyOf(vectorStores.values());
    }
    
    /**
     * 获取所有可用的向量存储类型
     */
    public List<String> getAllVectorStoreTypes() {
        return vectorStores.values().stream()
                .map(VectorStore::getStoreType)
                .distinct()
                .toList();
    }
    
    /**
     * 测试所有向量存储的连接
     */
    public Map<String, Boolean> testAllConnections() {
        Map<String, Boolean> results = new ConcurrentHashMap<>();
        
        vectorStores.forEach((key, store) -> {
            try {
                boolean connected = store.testConnection();
                results.put(key, connected);
                log.info("向量存储 {} 连接测试: {}", key, connected ? "成功" : "失败");
            } catch (Exception e) {
                results.put(key, false);
                log.error("向量存储 {} 连接测试异常: {}", key, e.getMessage());
            }
        });
        
        return results;
    }
    
    /**
     * 获取默认存储类型
     */
    public String getDefaultStoreType() {
        return defaultStoreType;
    }
} 