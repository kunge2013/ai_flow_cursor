## AI Flow 后端接口设计（Spring Boot 3 + Swagger/OpenAPI）

本设计文档基于前端 `ai-flow-ui` 的现有页面与状态结构，提供后端可落地的 CRUD 接口规范，便于快速生成 Java 业务代码与控制器。

### 技术栈与规范
- **框架**: Spring Boot 3.x（Spring Web, Validation）
- **API 文档**: springdoc-openapi（Swagger UI）
- **数据格式**: JSON，UTF-8
- **时间**: ISO8601（示例：`2025-01-01T12:00:00Z`）
- **ID**: 建议使用字符串（`UUID` 或短 ID）。
- **基础路径**: `/api`

Maven 依赖（参考）：
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.5.0</version>
</dependency>
```
Swagger UI: 运行后访问 `/swagger-ui.html`

---

### 统一约定
- **分页**: `page`（默认 0）, `size`（默认 20）, `sort`（如：`createdAt,desc`）。
- **错误响应**（示例）：
```json
{
  "timestamp": "2025-01-01T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "参数校验失败: name 不能为空",
  "path": "/api/flows"
}
```
- **校验**: 使用 `jakarta.validation` 注解（如 `@NotBlank`, `@Size`, `@Min`）。

---

## 领域模型概览
结合前端：
- 流程 Flow：`id`, `name`, `description`, `graph(nodes/edges)`，以及 `createdAt/updatedAt`
- 应用 AiApp：`id`, `name`, `description`, `iconUrl`, `appType(simple|advanced)`, `model`, `prompt`, `openingRemark`, `createdAt/updatedAt`
- 模型 ModelConfig：供前端选择 LLM 模型
- 知识库 KnowledgeBase：占位与扩展（前端已预留入口）

图结构（Graph）中的节点类型（部分）：`llm`, `classifier`, `kb`, `branch`, `script`, `java`, `http`, `subflow`, `reply`, `end`

---

## DTO 定义（示意）
建议在 `dto` 包内定义如下 DTO（控制器输入/输出）：

```java
// Flow 简要信息
public class FlowSummary {
  private String id;
  private String name;
  private String description;
  private Instant createdAt;
  private Instant updatedAt;
}

// 创建/更新 Flow 入参
public class FlowUpsertRequest {
  @NotBlank
  @Size(max = 100)
  private String name;
  @Size(max = 1000)
  private String description;
}

// 图数据（与前端保持结构兼容）
public class FlowGraph {
  private List<GraphNode> nodes;
  private List<GraphEdge> edges;
}

public class GraphNode {
  @NotBlank private String id;
  @NotBlank private String type; // 如 llm/classifier/rect
  private Double x;
  private Double y;
  private String text;
  private Map<String, Object> properties; // 兼容不同节点配置
}

public class GraphEdge {
  private String id; // 可选
  @NotBlank private String sourceNodeId;
  @NotBlank private String targetNodeId;
  private String label;
}

public class FlowWithGraphResponse {
  private String id;
  private String name;
  private String description;
  private Instant createdAt;
  private Instant updatedAt;
  private FlowGraph graph;
}

// 运行 Flow 入参/出参（可扩展）
public class FlowRunRequest {
  private Map<String, Object> inputs; // 运行时变量
}

public class FlowRunResult {
  private String flowId;
  private String runId;
  private Map<String, Object> outputs;
  private List<Map<String, Object>> trace; // 每步执行轨迹（可选）
}

// AiApp
public class AiAppSummary {
  private String id;
  private String name;
  private String description;
  private String iconUrl;
  @NotBlank private String appType; // simple|advanced
  private String model;
  private String prompt;
  private String openingRemark;
  private Instant createdAt;
  private Instant updatedAt;
}

public class AiAppUpsertRequest {
  @NotBlank @Size(max = 100) private String name;
  @Size(max = 1000) private String description;
  private String iconUrl;
  @NotBlank private String appType; // simple|advanced
  private String model;
  private String prompt;
  private String openingRemark;
}

// 模型配置（供前端下拉）
public class ModelConfig {
  private String id;
  private String name;     // 显示名，如 gpt-4o-mini
  private String provider; // openai/aliyun/…
  private String model;    // 精确型号
  private Boolean enabled;
}

// 知识库（占位）
public class KnowledgeBaseSummary {
  private String id;
  private String name;
  private String description;
  private List<String> tags;
  private Instant createdAt;
  private Instant updatedAt;
}

public class KnowledgeBaseUpsertRequest {
  @NotBlank @Size(max = 100) private String name;
  @Size(max = 1000) private String description;
  private List<String> tags;
}
```

---

## 接口一览

### 1) Flow 流程管理
- 资源路径：`/api/flows`

1. 查询列表（可按名称模糊）
  - GET `/api/flows?name={name}&page=0&size=20&sort=createdAt,desc`
  - 响应：`List<FlowSummary>` 或分页包装（视实现）
  - 示例响应：
```json
[
  {"id":"f1","name":"客服流程","description":"…","createdAt":"2025-01-01T12:00:00Z","updatedAt":"2025-01-01T12:00:00Z"}
]
```

2. 创建流程
  - POST `/api/flows`
  - RequestBody: `FlowUpsertRequest`
  - 201 响应：`FlowSummary`
  - 示例请求：
```json
{"name":"知识问答","description":"面向内部知识库"}
```

3. 获取流程详情（含图）
  - GET `/api/flows/{id}`
  - 200 响应：`FlowWithGraphResponse`

4. 更新流程基本信息
  - PUT `/api/flows/{id}`
  - RequestBody: `FlowUpsertRequest`
  - 200 响应：`FlowSummary`

5. 删除流程
  - DELETE `/api/flows/{id}`
  - 204 无内容

6. 获取流程图
  - GET `/api/flows/{id}/graph`
  - 200 响应：`FlowGraph`

7. 更新/保存流程图
  - PUT `/api/flows/{id}/graph`
  - RequestBody：`FlowGraph`
  - 200 响应：`FlowGraph`

8. 运行流程（可选，用于调试）
  - POST `/api/flows/{id}/run`
  - RequestBody：`FlowRunRequest`
  - 200 响应：`FlowRunResult`

节点属性建议（与前端一致）：
- `llm`: `{ "title": "LLM", "model": "gpt-4o-mini", "input": "...", "output": "result", "temperature": 0.7, "maxTokens": 2048 }`
- `classifier`: `{ "title": "分类器", "labels": "A,B" 或 ["A","B"], "output": "label" }`
- 其他类型：存入 `properties` 的通用键值（后续再细化）


### 2) AiApp 应用管理
- 资源路径：`/api/apps`

1. 查询列表
  - GET `/api/apps?name={kw}&type={simple|advanced}&page=0&size=20`
  - 200 响应：`List<AiAppSummary>`

2. 创建应用
  - POST `/api/apps`
  - RequestBody：`AiAppUpsertRequest`
  - 201 响应：`AiAppSummary`

3. 获取详情
  - GET `/api/apps/{id}`
  - 200 响应：`AiAppSummary`

4. 更新
  - PUT `/api/apps/{id}`
  - RequestBody：`AiAppUpsertRequest`
  - 200 响应：`AiAppSummary`

5. 删除
  - DELETE `/api/apps/{id}`
  - 204 无内容


### 3) 模型配置 ModelConfig（供下拉框）
- 资源路径：`/api/models`

1. 查询列表
  - GET `/api/models?enabled=true`
  - 200 响应：`List<ModelConfig>`

2. 创建
  - POST `/api/models`
  - RequestBody：`ModelConfig`
  - 201 响应：`ModelConfig`

3. 获取
  - GET `/api/models/{id}`
  - 200 响应：`ModelConfig`

4. 更新
  - PUT `/api/models/{id}`
  - RequestBody：`ModelConfig`
  - 200 响应：`ModelConfig`

5. 删除
  - DELETE `/api/models/{id}`
  - 204 无内容


### 4) 知识库 KnowledgeBase（占位，后续扩展）
- 资源路径：`/api/kb`

1. 列表
  - GET `/api/kb?name={kw}` → `List<KnowledgeBaseSummary>`
2. 创建
  - POST `/api/kb`（`KnowledgeBaseUpsertRequest`） → `KnowledgeBaseSummary`
3. 详情
  - GET `/api/kb/{id}` → `KnowledgeBaseSummary`
4. 更新
  - PUT `/api/kb/{id}`（`KnowledgeBaseUpsertRequest`） → `KnowledgeBaseSummary`
5. 删除
  - DELETE `/api/kb/{id}` → 204

（如需管理文档、文件向量等，可追加：`/api/kb/{id}/docs`）

---

## 控制器与注解（示例）

```java
@RestController
@RequestMapping("/api/flows")
@Tag(name = "Flow", description = "流程管理")
public class FlowController {

  @GetMapping
  @Operation(summary = "查询流程列表")
  public List<FlowSummary> listFlows(
      @Parameter(description = "名称模糊匹配") @RequestParam(required = false) String name,
      @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size,
      @Parameter(description = "排序") @RequestParam(required = false) String sort) {
    // TODO
    return List.of();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "创建流程")
  public FlowSummary create(@Valid @RequestBody FlowUpsertRequest req) { /* ... */ return null; }

  @GetMapping("/{id}")
  @Operation(summary = "获取流程详情（含图）")
  public FlowWithGraphResponse get(@PathVariable String id) { /* ... */ return null; }

  @PutMapping("/{id}")
  @Operation(summary = "更新流程基本信息")
  public FlowSummary update(@PathVariable String id, @Valid @RequestBody FlowUpsertRequest req) { /* ... */ return null; }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "删除流程")
  public void delete(@PathVariable String id) { /* ... */ }

  @GetMapping("/{id}/graph")
  @Operation(summary = "获取流程图")
  public FlowGraph getGraph(@PathVariable String id) { /* ... */ return null; }

  @PutMapping("/{id}/graph")
  @Operation(summary = "保存流程图")
  public FlowGraph saveGraph(@PathVariable String id, @Valid @RequestBody FlowGraph graph) { /* ... */ return null; }

  @PostMapping("/{id}/run")
  @Operation(summary = "运行流程（调试）")
  public FlowRunResult run(@PathVariable String id, @RequestBody FlowRunRequest req) { /* ... */ return null; }
}
```

> 其他控制器（`AppController`, `ModelController`, `KnowledgeBaseController`）按上述风格定义。

---

## 示例请求/响应

- 创建流程
```http
POST /api/flows
Content-Type: application/json

{"name":"客服流程","description":"处理基础咨询"}
```
响应：
```json
{
  "id": "f_01HXYZ",
  "name": "客服流程",
  "description": "处理基础咨询",
  "createdAt": "2025-01-01T12:00:00Z",
  "updatedAt": "2025-01-01T12:00:00Z"
}
```

- 保存流程图
```http
PUT /api/flows/f_01HXYZ/graph
Content-Type: application/json

{
  "nodes": [
    {"id":"start-abc","type":"rect","x":200,"y":120,"text":"开始","properties":{"role":"start"}},
    {"id":"n-1","type":"llm","x":360,"y":160,"text":"LLM","properties":{"title":"LLM","model":"gpt-4o-mini","input":"${question}","output":"result","temperature":0.7,"maxTokens":2048}}
  ],
  "edges": [
    {"sourceNodeId":"start-abc","targetNodeId":"n-1"}
  ]
}
```

- 运行流程（调试）
```http
POST /api/flows/f_01HXYZ/run
Content-Type: application/json

{"inputs": {"question": "你好，今天天气如何？"}}
```
响应（示例）：
```json
{
  "flowId": "f_01HXYZ",
  "runId": "r_20250101_0001",
  "outputs": {"result": "今天天气晴朗…"},
  "trace": [
    {"nodeId":"n-1","type":"llm","model":"gpt-4o-mini","input":"…","output":"…","durationMs": 1200}
  ]
}
```

---

## 实现建议
- ID 生成：使用 `UUID`；前端短 ID 可在创建后由后端返回并回填。
- 持久化：`Flow` 与 `Graph` 可拆分两表，或 `graph` 以 JSON 存储。
- 安全：若涉及模型 API Key，请仅在后端安全存储；前端接口不返回敏感字段。
- CORS：开放给 `ai-flow-ui` 所在域。
- 日志与审计：记录 `run` 的输入与输出（可脱敏）。

---

## 前后端字段映射要点
- Flow 列表页筛选用 `name`；创建/编辑表单使用 `name`, `description`；保存编辑器图形时用 `/flows/{id}/graph`。
- LLM 节点前端使用字段：`properties.model`, `properties.input`, `properties.output` 等；后端保持兼容并进行校验（如必填）。
- Classifier 节点：`properties.labels` 可支持字符串或数组，后端可在保存时标准化为数组。

---

## 目录建议
- `controller`：`FlowController`, `AppController`, `ModelController`, `KnowledgeBaseController`
- `dto`：请求/响应模型
- `service`：业务逻辑（含运行引擎接口）
- `repository` / `mapper`：数据持久化
- `config`：OpenAPI/CORS 等配置（springdoc 默认即可）

以上即为与现有前端契合的后端接口与数据结构设计，可直接据此生成 Spring Boot 3 控制器与实体/DTO，并通过 Swagger UI 进行联调。 