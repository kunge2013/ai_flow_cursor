# 流程节点模块

这个目录包含了从主组件中抽取出来的流程节点定义。

## 文件结构

- `LLMNode.ts` - LLM节点的模型和视图类
- `ClassifierNode.ts` - 分类器节点的模型和视图类  
- `index.ts` - 节点注册管理模块

## 使用方法

在主组件中导入并使用：

```typescript
import { registerNodes } from './nodes'

// 在LogicFlow初始化后注册节点
registerNodes(lf)
```

## 节点类型

### LLM节点
- 类型：`llm`
- 模型：`LLMNodeModel`
- 视图：`LLMNodeView`
- 特性：带有右侧加号按钮的自定义矩形节点

### 分类器节点
- 类型：`classifier`
- 模型：`ClassifierNodeModel`
- 视图：`ClassifierNodeView`
- 特性：标准矩形节点

## 扩展新节点

要添加新的节点类型：

1. 创建新的节点文件（如 `NewNode.ts`）
2. 在 `index.ts` 中导入并注册新节点
3. 更新 `nodeRegistries` 数组

