# LogicFlow连线显示问题修复

## 问题描述
查询接口后连线没有显示，节点可以正常显示但连线缺失。

## 问题原因
LogicFlow 2.x版本需要明确的edge类型才能正确渲染连线。

## 修复方案

### 1. 添加edge类型
在数据转换时为每个edge添加`type: 'polyline'`属性：

```typescript
edges: (graph?.edges || []).map((e: any) => ({
  id: e.id || `e-${genId()}`,
  type: 'polyline',  // 添加这行
  sourceNodeId: e.sourceNodeId,
  targetNodeId: e.targetNodeId,
  label: e.label
}))
```

### 2. 修复addEdge调用
在动态添加连线时也要指定类型：

```typescript
lf.addEdge({ 
  type: 'polyline',  // 添加这行
  sourceNodeId: sourceId, 
  targetNodeId: newId 
})
```

### 3. 优化LogicFlow配置
添加必要的配置选项：

```typescript
lf = new LogicFlow({
  container: el,
  grid: true,
  enableMobx: false,
  nodeTextEdit: false,
  edgeTextEdit: false,
  enableTextEdit: false,
  enableHistory: false,
  enableSilentMode: true,
  width: el.offsetWidth,
  height: el.offsetHeight
})
```

## 测试数据
使用以下测试数据验证修复：

```json
{
  "nodes": [
    {
      "id": "3efe4075-fc5e-4192-902c-20c974910219",
      "type": "rect",
      "x": 200.0,
      "y": 105.0,
      "text": "xx",
      "properties": {
        "width": 100,
        "height": 80
      }
    },
    {
      "id": "5003db69-de18-4de7-a129-e64ec5b3ad27",
      "type": "llm",
      "x": 451.0,
      "y": 104.0,
      "text": "de",
      "properties": {
        "title": "LLM",
        "model": "gpt-4o-mini",
        "input": "",
        "output": "result",
        "width": 140,
        "height": 52
      }
    }
  ],
  "edges": [
    {
      "id": "5ea27ec2-b376-48d5-bd87-7b18ddc4e24b",
      "type": "polyline",
      "sourceNodeId": "3efe4075-fc5e-4192-902c-20c974910219",
      "targetNodeId": "5003db69-de18-4de7-a129-e64ec5b3ad27",
      "label": ""
    }
  ]
}
```

## 验证步骤
1. 打开流程编辑界面
2. 加载包含连线的流程数据
3. 确认连线正确显示
4. 测试动态添加节点和连线
5. 保存流程并重新加载验证

## 注意事项
- LogicFlow 2.x版本对数据格式要求更严格
- 必须为每个edge指定type属性
- 建议使用'polyline'作为默认连线类型
- 确保sourceNodeId和targetNodeId正确对应存在的节点 