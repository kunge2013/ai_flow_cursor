package com.aiflow.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseInitService implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化数据库...");
        
        try {
            // 初始化向量存储相关表
            initVectorTables();
            log.info("数据库初始化完成");
        } catch (Exception e) {
            log.error("数据库初始化失败", e);
        }
    }

    private void initVectorTables() throws Exception {
        // 读取 schema-vector.sql 文件
        ClassPathResource resource = new ClassPathResource("schema-vector.sql");
        String sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        
        // 分割 SQL 语句并执行
        String[] statements = sql.split(";");
        for (String statement : statements) {
            statement = statement.trim();
            if (!statement.isEmpty() && !statement.startsWith("--")) {
                try {
                    jdbcTemplate.execute(statement);
                    log.debug("执行 SQL: {}", statement.substring(0, Math.min(statement.length(), 50)) + "...");
                } catch (Exception e) {
                    // 忽略表已存在的错误
                    if (!e.getMessage().contains("already exists") && !e.getMessage().contains("Duplicate key")) {
                        log.warn("执行 SQL 失败: {}", e.getMessage());
                    }
                }
            }
        }
    }
} 