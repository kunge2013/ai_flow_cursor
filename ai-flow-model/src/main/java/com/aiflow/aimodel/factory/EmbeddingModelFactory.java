package com.aiflow.aimodel.factory;

import com.aiflow.aimodel.embedding.EmbeddingModel;
import com.aiflow.aimodel.model.AiModelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 向量模型工厂
 */
@Slf4j
@Component
public class EmbeddingModelFactory {
    
    private final Map<String, EmbeddingModel> embeddingModels = new ConcurrentHashMap<>();
    
    @Autowired
    public EmbeddingModelFactory(List<EmbeddingModel> models) {
        for (EmbeddingModel model : models) {
            String key = model.getModelType() + ":" + model.getModelName();
            embeddingModels.put(key, model);
            log.info("注册向量模型: {} -> {}", key, model.getClass().getSimpleName());
        }
    }
    
    /**
     * 根据模型类型获取向量模型
     */
    public EmbeddingModel getEmbeddingModel(AiModelType modelType) {
        return getEmbeddingModel(modelType.getCode());
    }
    
    /**
     * 根据模型类型代码获取向量模型
     */
    public EmbeddingModel getEmbeddingModel(String modelType) {
        // 查找匹配的模型
        for (Map.Entry<String, EmbeddingModel> entry : embeddingModels.entrySet()) {
            if (entry.getKey().startsWith(modelType + ":")) {
                return entry.getValue();
            }
        }
        
        // 如果没有找到，返回默认的OpenAI模型
        for (Map.Entry<String, EmbeddingModel> entry : embeddingModels.entrySet()) {
            if (entry.getKey().startsWith("sentence-transformers:all-minilm-l6-v2")) {
                log.warn("未找到类型为 {} 的向量模型，使用默认的OpenAI模型", modelType);
                return entry.getValue();
            }
        }
        
        throw new RuntimeException("未找到可用的向量模型");
    }
    
    /**
     * 根据模型类型和名称获取向量模型
     */
    public EmbeddingModel getEmbeddingModel(String modelType, String modelName) {
        String key = modelType + ":" + modelName;
        EmbeddingModel model = embeddingModels.get(key);
        if (model == null) {
            throw new RuntimeException("未找到向量模型: " + key);
        }
        return model;
    }
    
    /**
     * 获取所有可用的向量模型
     */
    public List<EmbeddingModel> getAllEmbeddingModels() {
        return List.copyOf(embeddingModels.values());
    }
    
    /**
     * 获取所有可用的向量模型类型
     */
    public List<String> getAllEmbeddingModelTypes() {
        return embeddingModels.values().stream()
                .map(EmbeddingModel::getModelType)
                .distinct()
                .toList();
    }
    
    /**
     * 测试所有向量模型的连接
     */
    public Map<String, Boolean> testAllConnections() {
        Map<String, Boolean> results = new ConcurrentHashMap<>();
        
        embeddingModels.forEach((key, model) -> {
            try {
                boolean connected = model.testConnection();
                results.put(key, connected);
                log.info("向量模型 {} 连接测试: {}", key, connected ? "成功" : "失败");
            } catch (Exception e) {
                results.put(key, false);
                log.error("向量模型 {} 连接测试异常: {}", key, e.getMessage());
            }
        });
        
        return results;
    }
} 