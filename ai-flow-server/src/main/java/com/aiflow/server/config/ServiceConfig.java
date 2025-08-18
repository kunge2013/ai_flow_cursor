package com.aiflow.server.config;

import com.aiflow.server.engine.Engine;
import com.aiflow.server.service.AppService;
import com.aiflow.server.service.FlowService;
import com.aiflow.server.service.KbService;
import com.aiflow.server.service.ModelService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public FlowService flowService(Engine engine) {
        FlowService s = new FlowService();
        s.setEngine(engine);
        return s;
    }

    @Bean
    public AppService appService() {
        return new AppService();
    }

    @Bean
    public ModelService modelService() {
        return new ModelService();
    }

    @Bean
    public KbService kbService() {
        return new KbService();
    }
} 