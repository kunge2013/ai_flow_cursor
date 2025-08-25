package com.aiflow.aimodel.config;

import com.aiflow.aimodel.adapter.AiModelAdapter;
import com.aiflow.aimodel.factory.AiModelFactory;
import com.aiflow.aimodel.service.AiModelService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * AI模型模块自动配置类
 */
@Configuration
@ComponentScan("com.aiflow.aimodel")
@EnableConfigurationProperties(AiModelProperties.class)
public class AiModelAutoConfiguration {
    
    /**
     * 配置AI模型工厂
     */
    @Bean
    @ConditionalOnMissingBean
    public AiModelFactory aiModelFactory(List<AiModelAdapter> adapters) {
        return new AiModelFactory(adapters);
    }
    
    /**
     * 配置AI模型服务
     */
    @Bean
    @ConditionalOnMissingBean
    public AiModelService aiModelService(AiModelFactory modelFactory) {
        return new AiModelService(modelFactory);
    }
} 