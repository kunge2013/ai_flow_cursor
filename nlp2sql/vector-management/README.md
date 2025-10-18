# 数据库 Schema 管控模块

## 项目简介

本模块旨在提供一个完整的 `evidence` 及 `schema` 的管控能力，用于初始化数据库的 `schema` 信息，并支持对业务逻辑解释（evidence）进行增删改查操作。

---

## 功能特性

- **数据库 Schema 初始化**  
  提供一键初始化功能，快速生成指定的数据库表结构。

- **业务逻辑管理**  
  支持对业务逻辑的增删改查操作，方便开发者维护复杂的业务规则。

- **RESTful API 接口**  
  基于 Spring Boot 提供标准的 RESTful API，便于与其他系统集成。

- **图形化管控页面**  
  提供蓝白色调的图形化界面，包含两个 Tab 页面：
  - **业务逻辑解释管控**：新增、召回、删除业务逻辑解释。
  - **Schema 管控**：初始化数据库 Schema。

---

## 技术栈

- **后端**: Java 17+ (Spring Boot)
- **前端**: HTML, CSS, JavaScript
- **数据库**: MySQL / PostgreSQL

---

## 安装与部署

### 前置依赖

- [Java](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) >= 17
- [MySQL](https://www.mysql.com/) 或 [PostgreSQL](https://www.postgresql.org/) >= 13
- [Maven](https://maven.apache.org/) 或 [Gradle](https://gradle.org/)

### 快速开始

1. **修改配置文件**

   在 `src/main/resources/application.yml` 中修改相关配置：
   ```yaml
   spring:
     ai:
       dashscope:
         api-key: your_api_key
       vectorstore:
         analytic:
           collectName: your_collect_name
           regionId: cn-hangzhou
           dbInstanceId: your_db_instance_id
           managerAccount: your_manager_account
           managerAccountPassword: your_manager_account_password
           namespace: your_namespace
           namespacePassword: your_namespace_password
           defaultTopK: 6
           defaultSimilarityThreshold: 0.75
           accessKeyId: your_access_key_id
           accessKeySecret: your_access_key_secret
   ```

2. **构建项目**

   使用 Maven 构建项目：
   ```bash
   mvn clean install
   ```

   或使用 Gradle：
   ```bash
   gradle build
   ```
3. **启动服务**

   启动 Spring Boot 应用：
   ```bash
   java -jar target/vector-management-1.0.0.jar
   ```

4. **访问管控页面**

   打开浏览器，访问以下地址：
   ```
   http://localhost:8061/index.html
   ```

---

## 使用说明

### 业务逻辑解释管控

1. **新增业务逻辑解释**
  - 切换到“业务逻辑解释管控” Tab。
  - 在“新增业务逻辑解释”部分填写内容和类型，点击“新增业务逻辑”按钮。

2. **召回业务逻辑解释**
  - 在“召回业务逻辑解释”部分输入查询关键词，点击“搜索业务逻辑”按钮。

3. **删除业务逻辑解释**
  - 在“删除业务逻辑解释”部分输入 ID，点击“删除业务逻辑”按钮。

### Schema 管控

1. **初始化数据库 Schema**
  - 切换到“Schema 管控” Tab。
  - 在“初始化当前数据库的 schema”部分填写数据库连接信息和表名，点击“初始化 Schema”按钮。

---

## API 文档

以下为部分接口示例：

### 初始化当前数据库的 Schema

- **请求方法**: `POST`
- **URL**: `/init/schema`
- **请求体**:
  ```json
  {
    "dbConfig": {
      "url": "jdbc:mysql://ip:port/database",
      "username": "username",
      "password": "password", 
      "connectionType": "jdbc",
      "dialectType": "mysql" 
    },
    "tables": [
      "customers"
    ]
  }
  ```
- **返回示例**:
  ```json
  true
  ```

### 新增业务逻辑解释

- **请求方法**: `POST`
- **URL**: `/add/evidence`
- **请求体**:
  ```json
  [
    {
      "content": "冬天指的是今年11月到第二年的3月",
      "type": 1 
    },
    {
      "content": "计算销量时只统计收货状态为已收货的订单",
      "type": 2  
    }
  ]
  ```
- **返回示例**:
  ```json
  true
  ```

### 召回业务逻辑解释

- **请求方法**: `POST`
- **URL**: `/search`
- **请求体**:
  ```json
  {
    "query": "冬季",
    "vectorType": "evidence",
    "topK": 100
  }
  ```
- **返回示例**:
  ```json
  [
    {
      "id": "33280667-0aad-449d-b1ad-78cf140ff134",
      "text": "计算销量时只统计收货状态为已收货的订单",
      "media": null,
      "metadata": {
        "vectorType": "evidence",
        "evidenceType": 2
      },
      "score": null
    }
  ]
  ```

### 删除业务逻辑解释

- **请求方法**: `POST`
- **URL**: `/delete`
- **请求体**:
  ```json
  {
    "id": "33280667-0aad-449d-b1ad-78cf140ff134"
  }
  ```
- **返回示例**:
  ```json
  true
  ```

---

## 贡献指南

欢迎参与本项目的开发与优化！以下是贡献流程：
请参[考贡献指](https://github.com/alibaba/spring-ai-alibaba/blob/main/CONTRIBUTING.md)南了解如何参与 Spring AI 阿里巴巴的开发。

---

## 许可证

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源协议。

---

## 联系方式

如有任何问题，请联系：
- 邮箱: kunan.lw@alibaba-inc.com
- GitHub: [willyomg](https://github.com/willyomg)

- 邮箱: xuqirui.xqr@alibaba-inc.com
- GitHub: [littleahri](https://github.com/littleahri)
