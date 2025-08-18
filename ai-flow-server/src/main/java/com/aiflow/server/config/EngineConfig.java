package com.aiflow.server.config;

import com.aiflow.server.engine.Engine;
import com.aiflow.server.engine.LlmNodeExecutor;
import com.aiflow.server.engine.NodeExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class EngineConfig {
    @Bean
    public Engine engine() {
        List<NodeExecutor> executors = List.of(
                new LlmNodeExecutor()
        );
        return new Engine(executors);
    }
} 