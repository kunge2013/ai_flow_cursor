package com.aiflow.server.config;

import com.aiflow.server.engine.Engine;
import com.aiflow.server.service.FlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class ServiceConfig {

    @Autowired
    private FlowService flowService;
    
    @Autowired
    private Engine engine;

    @PostConstruct
    public void configureServices() {
        // Set engine for FlowService after dependency injection
        flowService.setEngine(engine);
    }
} 