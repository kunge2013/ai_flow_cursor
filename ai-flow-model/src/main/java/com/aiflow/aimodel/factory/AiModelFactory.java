package com.aiflow.aimodel.factory;

import com.aiflow.aimodel.adapter.AiModelAdapter;
import com.aiflow.aimodel.model.AiModelConfig;
import com.aiflow.aimodel.model.AiModelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AI模型工厂类
 * 基于LangChain4j实现
 */
@Slf4j
@Component
public class AiModelFactory {
    
    private final Map<AiModelType, AiModelAdapter> adapterMap;
    
    @Autowired
    public AiModelFactory(List<AiModelAdapter> adapters) {
        this.adapterMap = adapters.stream()
                .collect(Collectors.toMap(AiModelAdapter::getSupportedType, Function.identity()));
        
        log.info("已加载的AI模型适配器: {}", adapterMap.keySet());
    }
    
    /**
     * 根据模型类型获取适配器
     */
    public AiModelAdapter getAdapter(AiModelType modelType) {
        AiModelAdapter adapter = adapterMap.get(modelType);
        if (adapter == null) {
            log.warn("未找到模型类型 {} 对应的适配器", modelType);
            throw new IllegalArgumentException("不支持的模型类型: " + modelType);
        }
        return adapter;
    }
    
    /**
     * 根据模型配置获取适配器
     */
    public AiModelAdapter getAdapter(AiModelConfig config) {
        if (config == null || config.getType() == null) {
            throw new IllegalArgumentException("模型配置或类型不能为空");
        }
        return getAdapter(config.getType());
    }
    
    /**
     * 根据模型类型代码获取适配器
     */
    public AiModelAdapter getAdapter(String modelTypeCode) {
        AiModelType modelType = AiModelType.fromCode(modelTypeCode);
        return getAdapter(modelType);
    }
    
    /**
     * 获取所有可用的模型类型
     */
    public List<AiModelType> getAvailableModelTypes() {
        return List.copyOf(adapterMap.keySet());
    }
    
    /**
     * 检查模型类型是否支持
     */
    public boolean isModelTypeSupported(AiModelType modelType) {
        return adapterMap.containsKey(modelType);
    }
    
    /**
     * 检查模型类型是否支持
     */
    public boolean isModelTypeSupported(String modelTypeCode) {
        return isModelTypeSupported(AiModelType.fromCode(modelTypeCode));
    }
    
    /**
     * 获取适配器数量
     */
    public int getAdapterCount() {
        return adapterMap.size();
    }
    
    /**
     * 获取所有适配器
     */
    public Map<AiModelType, AiModelAdapter> getAllAdapters() {
        return Map.copyOf(adapterMap);
    }
} 