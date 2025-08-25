package com.aiflow.server;

import com.aiflow.aimodel.EnableAimodel;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAimodel
@MapperScan("com.aiflow.server.mapper")
public class AiFlowServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiFlowServerApplication.class, args);
    }
} 