package com.aiflow.server.service;

import com.aiflow.server.entity.NodeTypeEntity;
import com.aiflow.server.mapper.NodeTypeMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseInitService /* implements CommandLineRunner */ {

    private final JdbcTemplate jdbcTemplate;
    private final NodeTypeMapper nodeTypeMapper;

    // @Override
    public void run(String... args) throws Exception {
        createTablesIfNotExists();
        initializeNodeTypes();
    }

    private void createTablesIfNotExists() {
        try {
            // Create t_flow table
            String createFlowTable = """
                CREATE TABLE IF NOT EXISTS t_flow (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    flow_id VARCHAR(255) NOT NULL UNIQUE,
                    name VARCHAR(255) NOT NULL,
                    description TEXT,
                    graph_json LONGTEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    deleted TINYINT DEFAULT 0,
                    INDEX idx_flow_id (flow_id),
                    INDEX idx_deleted (deleted)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """;

            // Create t_app table
            String createAppTable = """
                CREATE TABLE IF NOT EXISTS t_app (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    app_id VARCHAR(255) NOT NULL UNIQUE,
                    name VARCHAR(255) NOT NULL,
                    description TEXT,
                    flow_id VARCHAR(255),
                    config_json TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    deleted TINYINT DEFAULT 0,
                    INDEX idx_app_id (app_id),
                    INDEX idx_flow_id (flow_id),
                    INDEX idx_deleted (deleted)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """;

            // Create t_node_type table
            String createNodeTypeTable = """
                CREATE TABLE IF NOT EXISTS t_node_type (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    type_code VARCHAR(100) NOT NULL UNIQUE,
                    type_name VARCHAR(255) NOT NULL,
                    description TEXT,
                    icon VARCHAR(255),
                    category VARCHAR(100),
                    config_schema LONGTEXT,
                    enabled BOOLEAN DEFAULT TRUE,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    deleted TINYINT DEFAULT 0,
                    INDEX idx_type_code (type_code),
                    INDEX idx_category (category),
                    INDEX idx_enabled (enabled),
                    INDEX idx_deleted (deleted)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """;

            jdbcTemplate.execute(createFlowTable);
            jdbcTemplate.execute(createAppTable);
            jdbcTemplate.execute(createNodeTypeTable);

            log.info("Database tables created successfully");
        } catch (Exception e) {
            log.error("Error creating database tables", e);
        }
    }

    private void initializeNodeTypes() {
        try {
            // Check if node types already exist
            QueryWrapper<NodeTypeEntity> wrapper = new QueryWrapper<>();
            long count = nodeTypeMapper.selectCount(wrapper);
            
            if (count > 0) {
                log.info("Node types already initialized, skipping...");
                return;
            }

            List<NodeTypeEntity> nodeTypes = Arrays.asList(
                createNodeType("start", "开始节点", "流程开始节点", "start", "control"),
                createNodeType("end", "结束节点", "流程结束节点", "stop", "control"),
                createNodeType("llm", "LLM节点", "大语言模型处理节点", "robot", "ai"),
                createNodeType("classifier", "分类器", "文本分类节点", "filter", "ai"),
                createNodeType("knowledge_base", "知识库", "知识库查询节点", "database", "data"),
                createNodeType("condition", "条件分支", "条件判断分支节点", "fork", "control"),
                createNodeType("script", "脚本执行", "脚本代码执行节点", "code", "logic"),
                createNodeType("java_enhance", "Java增强", "Java代码增强节点", "coffee", "logic"),
                createNodeType("http_request", "HTTP请求", "HTTP接口请求节点", "link", "integration"),
                createNodeType("subprocess", "子流程", "调用子流程节点", "sitemap", "control"),
                createNodeType("direct_reply", "直接回复", "直接输出回复节点", "message", "output")
            );

            for (NodeTypeEntity nodeType : nodeTypes) {
                nodeTypeMapper.insert(nodeType);
            }

            log.info("Node types initialized successfully");
        } catch (Exception e) {
            log.error("Error initializing node types", e);
        }
    }

    private NodeTypeEntity createNodeType(String code, String name, String description, String icon, String category) {
        NodeTypeEntity entity = new NodeTypeEntity();
        entity.setTypeCode(code);
        entity.setTypeName(name);
        entity.setDescription(description);
        entity.setIcon(icon);
        entity.setCategory(category);
        entity.setEnabled(true);
        return entity;
    }
} 