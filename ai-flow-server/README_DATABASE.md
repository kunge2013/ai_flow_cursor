# AI Flow Server - 数据存储优化

## 概述

本项目已完成从内存存储到MySQL数据库的迁移，并集成了MyBatis-Plus和LiteFlow框架，实现了更加稳定和可扩展的数据存储方案。

## 主要改进

### 1. 数据库持久化
- ✅ 使用MyBatis-Plus替代内存存储(ConcurrentHashMap)
- ✅ 支持MySQL数据库自动建表
- ✅ 实现了完整的CRUD操作
- ✅ 添加了逻辑删除支持

### 2. LiteFlow框架集成
- ✅ 集成LiteFlow引擎进行节点执行
- ✅ 支持多种脚本语言(JavaScript, Python)
- ✅ 实现了各种节点类型的执行器

### 3. 节点类型管理
- ✅ 实现节点类型查询API
- ✅ 支持按分类查询节点
- ✅ 自动初始化节点类型数据

## 技术栈

### 新增依赖
- **MyBatis-Plus 3.5.7**: ORM框架，自动建表
- **MySQL Connector 8.0.33**: MySQL数据库驱动
- **LiteFlow 2.12.0**: 流程引擎框架
- **Druid 1.2.21**: 数据库连接池
- **Lombok**: 简化代码

### 核心组件

#### 数据库实体
- `FlowEntity`: 流程实体
- `AppEntity`: 应用实体  
- `NodeTypeEntity`: 节点类型实体

#### Mapper接口
- `FlowMapper`: 流程数据访问
- `AppMapper`: 应用数据访问
- `NodeTypeMapper`: 节点类型数据访问

#### LiteFlow节点组件
- `StartNodeComponent`: 开始节点
- `LlmNodeComponent`: LLM节点
- `ScriptNodeComponent`: 脚本执行节点
- `HttpRequestNodeComponent`: HTTP请求节点

## 支持的节点类型

| 节点类型 | 类型代码 | 分类 | 描述 |
|----------|----------|------|------|
| 开始节点 | start | control | 流程开始节点 |
| 结束节点 | end | control | 流程结束节点 |
| LLM节点 | llm | ai | 大语言模型处理节点 |
| 分类器 | classifier | ai | 文本分类节点 |
| 知识库 | knowledge_base | data | 知识库查询节点 |
| 条件分支 | condition | control | 条件判断分支节点 |
| 脚本执行 | script | logic | 脚本代码执行节点 |
| Java增强 | java_enhance | logic | Java代码增强节点 |
| HTTP请求 | http_request | integration | HTTP接口请求节点 |
| 子流程 | subprocess | control | 调用子流程节点 |
| 直接回复 | direct_reply | output | 直接输出回复节点 |

## API接口

### 节点类型查询
```http
GET /api/node-types                    # 查询所有节点类型
GET /api/node-types/categories         # 查询所有分类
GET /api/node-types/category/{category} # 按分类查询
GET /api/node-types/{typeCode}         # 根据类型代码查询
```

## 数据库配置

### application.yml配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_flow
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  global-config:
    db-config:
      table-prefix: t_
      logic-delete-field: deleted
      
liteflow:
  rule-source: xml
  rule-source-ext-data: classpath:flow/*.xml
```

### 数据库初始化
系统启动时会自动：
1. 创建所需数据表
2. 初始化节点类型数据
3. 支持数据迁移

## 使用说明

### 1. 环境准备
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库准备
```sql
CREATE DATABASE ai_flow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置修改
修改 `application.yml` 中的数据库连接信息。

### 4. 启动应用
```bash
mvn spring-boot:run
```

## 后续开发

### 待实现功能
1. **LLM节点**: 集成OpenAI、百度文心等LLM服务
2. **脚本执行**: 完善JavaScript、Python脚本支持
3. **知识库**: 实现向量数据库查询
4. **条件分支**: 实现复杂条件判断逻辑
5. **HTTP请求**: 支持RESTful API调用

### 扩展建议
1. 添加流程执行历史记录
2. 实现流程版本管理
3. 添加流程监控和统计
4. 支持流程模板功能
5. 实现用户权限管理

## 技术文档参考
- [MyBatis-Plus官方文档](https://baomidou.com/)
- [LiteFlow官方文档](https://liteflow.cc/)
- [Spring Boot官方文档](https://spring.io/projects/spring-boot) 