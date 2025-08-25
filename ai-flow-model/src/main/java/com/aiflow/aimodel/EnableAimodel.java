package com.aiflow.aimodel;

import com.aiflow.aimodel.config.AiModelAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 AI Model 模块的注解
 * 使用此注解可以自动配置 ai-flow-model 模块
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AiModelAutoConfiguration.class)
public @interface EnableAimodel {
} 