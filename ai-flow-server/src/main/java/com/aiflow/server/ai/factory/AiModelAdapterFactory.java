package com.aiflow.server.ai.factory;

import com.aiflow.server.ai.adapter.AiModelAdapter;
import com.aiflow.server.ai.model.AiModelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AI模型适配器工厂类
 */
@Slf4j
@Component
public class AiModelAdapterFactory {

    private final Map<String, AiModelAdapter> adapterMap;

    @Autowired
    public AiModelAdapterFactory(List<AiModelAdapter> adapters) {
        this.adapterMap = adapters.stream()
                .collect(Collectors.toMap(AiModelAdapter::getModelType, Function.identity()));
        
        log.info("已加载的AI模型适配器: {}", adapterMap.keySet());
    }

    /**
     * 根据模型类型获取适配器
     *
     * @param modelType 模型类型
     * @return 对应的适配器
     */
    public AiModelAdapter getAdapter(String modelType) {
        AiModelAdapter adapter = adapterMap.get(modelType);
        if (adapter == null) {
            log.warn("未找到模型类型 {} 对应的适配器，使用默认适配器", modelType);
            // 如果没有找到对应的适配器，返回第一个可用的适配器
            return adapterMap.values().iterator().next();
        }
        return adapter;
    }

    /**
     * 根据模型类型枚举获取适配器
     *
     * @param modelType 模型类型枚举
     * @return 对应的适配器
     */
    public AiModelAdapter getAdapter(AiModelType modelType) {
        return getAdapter(modelType.getCode());
    }

    /**
     * 获取所有可用的模型类型
     *
     * @return 模型类型列表
     */
    public List<String> getAvailableModelTypes() {
        return List.copyOf(adapterMap.keySet());
    }

    /**
     * 检查模型类型是否支持
     *
     * @param modelType 模型类型
     * @return 是否支持
     */
    public boolean isModelTypeSupported(String modelType) {
        return adapterMap.containsKey(modelType);
    }
} 