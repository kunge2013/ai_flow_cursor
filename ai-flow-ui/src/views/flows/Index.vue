<template>
  <div class="page">
    <el-card v-if="page === 'list'" class="mb-12">
      <el-form :inline="true" :model="query">
        <el-form-item label="流程名称">
          <el-input v-model="query.name" placeholder="请输入流程名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">查询</el-button>
          <el-button @click="reset">重置</el-button>
          <el-button type="success" @click="openCreate">添加流程</el-button>
        </el-form-item>
      </el-form>

      <div class="flow-cards">
        <el-card v-for="f in filteredFlows" :key="f.id" class="flow-card" @click="openEditor(f.id)">
          <template #header>
            <div class="card-title">
              <el-avatar :size="32">{{ f.name?.[0] }}</el-avatar>
              <span>{{ f.name }}</span>
            </div>
          </template>
          <div class="desc">{{ f.description || '暂无描述' }}</div>
        </el-card>
        <el-card class="flow-card create" @click="openCreate">
          <div class="create-inner"><el-icon><Plus /></el-icon> 添加流程</div>
        </el-card>
      </div>

      <el-dialog v-model="createVisible" title="创建流程" width="560px" @closed="onCreateClosed">
        <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="100px">
          <el-form-item label="流程名称" prop="name">
            <el-input v-model="createForm.name" placeholder="请输入流程名称" />
          </el-form-item>
          <el-form-item label="流程描述">
            <el-input v-model="createForm.description" type="textarea" :rows="3" placeholder="请输入流程描述" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="createVisible = false">取消</el-button>
          <el-button type="primary" @click="onCreateConfirm">确定</el-button>
        </template>
      </el-dialog>
    </el-card>

    <div v-else class="editor-wrap">
      <div class="editor-toolbar">
        <el-button @click="backToList">返回</el-button>
        <el-button type="primary" @click="saveFlow">保存</el-button>
        <el-divider direction="vertical" />
        <el-button @click="addStart">开始</el-button>
        <el-button @click="addLLM">LLM</el-button>
      </div>
      <div class="editor-body">
        <div class="canvas-wrap" ref="canvasWrapRef">
          <div class="canvas" ref="canvasRef"></div>
          <!-- Per-node right-side plus buttons -->
          <div
            v-for="ov in nodeOverlays"
            :key="ov.id"
            class="node-plus"
            :style="{ left: ov.left, top: ov.top }"
          >
            <el-popover v-model:visible="plusVisible[ov.id]" trigger="click" placement="right-start" width="160" teleported persistent>
              <div class="add-menu">
                <el-button text @click="onChooseAddFor(ov.id, 'llm')">LLM</el-button>
                <el-button text @click="onChooseAddFor(ov.id, 'classifier')">分类器</el-button>
                <el-button text @click="onChooseAddFor(ov.id, 'kb')">知识库</el-button>
                <el-button text @click="onChooseAddFor(ov.id, 'branch')">条件分支</el-button>
                <el-button text @click="onChooseAddFor(ov.id, 'script')">脚本执行</el-button>
                <el-button text @click="onChooseAddFor(ov.id, 'java')">Java增强</el-button>
                <el-button text @click="onChooseAddFor(ov.id, 'http')">HTTP请求</el-button>
                <el-button text @click="onChooseAddFor(ov.id, 'subflow')">子流程</el-button>
                <el-button text @click="onChooseAddFor(ov.id, 'reply')">直接回复</el-button>
                <el-button text type="danger" @click="onChooseAddFor(ov.id, 'end')">结束</el-button>
              </div>
              <template #reference>
                <el-button type="primary" circle><el-icon><Plus /></el-icon></el-button>
              </template>
            </el-popover>
          </div>
        </div>
        <div class="props">
          <div v-if="selected && selected.type === 'llm'">
            <el-form label-width="100px">
              <el-form-item label="节点名称">
                <el-input v-model="selected.properties.title" />
              </el-form-item>
              <el-form-item label="选择模型">
                <el-select v-model="selected.properties.model" style="width: 200px">
                  <el-option label="gpt-4o-mini" value="gpt-4o-mini" />
                  <el-option label="qwen2.5-7b-instruct" value="qwen2.5-7b-instruct" />
                </el-select>
              </el-form-item>
              <el-form-item label="输入变量">
                <el-input v-model="selected.properties.input" type="textarea" :rows="3" />
              </el-form-item>
              <el-form-item label="输出变量">
                <el-input v-model="selected.properties.output" />
              </el-form-item>
            </el-form>
          </div>
          <div v-else-if="selected && selected.type === 'classifier'">
            <el-form label-width="100px">
              <el-form-item label="节点名称">
                <el-input v-model="selected.properties.title" />
              </el-form-item>
              <el-form-item label="类别集合">
                <el-input v-model="selected.properties.labels" placeholder="逗号分隔，如：正向,负向" />
              </el-form-item>
              <el-form-item label="输出变量">
                <el-input v-model="selected.properties.output" />
              </el-form-item>
            </el-form>
          </div>
          <div v-else-if="selected">
            <el-empty description="选择一个 LLM 或 分类器 节点进行编辑" />
          </div>
          <div v-else>
            <el-empty description="请选择节点" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, nextTick, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { listFlows, createFlow, getFlowGraph, saveFlowGraph, type FlowSummary, type FlowUpsertRequest, type FlowGraph } from '../../api/flows'
import LogicFlow, { RectNode, RectNodeModel, h } from '@logicflow/core'
import { MiniMap, SelectionSelect, Control } from '@logicflow/extension'

// Register LogicFlow plugins once (v2 uses static use)
let pluginsRegistered = false
function ensurePlugins() {
  if (!pluginsRegistered) {
    LogicFlow.use(MiniMap)
    LogicFlow.use(SelectionSelect)
    LogicFlow.use(Control)
    pluginsRegistered = true
  }
}

// flow data

const query = reactive({ name: '' })
const flows = ref<FlowSummary[]>([])
const page = ref<'list' | 'editor'>('list')
const currentFlowId = ref<string>('')

async function fetchFlows() {
  flows.value = await listFlows({ name: query.name || undefined })
}

onMounted(fetchFlows)

const filteredFlows = computed(() => {
  const name = query.name.trim()
  return flows.value.filter(f => (name ? f.name.includes(name) : true))
})

function onSearch() { fetchFlows() }
function reset() { query.name = ''; fetchFlows() }

const createVisible = ref(false)
const createFormRef = ref<FormInstance>()
const createForm = reactive<FlowUpsertRequest>({ name: '', description: '' })
const createRules = reactive<FormRules<FlowUpsertRequest>>({
  name: [{ required: true, message: '请输入流程名称', trigger: 'blur' }]
})

function openCreate() { createVisible.value = true }
async function onCreateConfirm() {
  await createFormRef.value?.validate()
  const created = await createFlow({ name: createForm.name, description: createForm.description || '' })
  createVisible.value = false
  openEditor(created.id)
}
function onCreateClosed() { createForm.name = ''; createForm.description = '' }

function openEditor(id: string) {
  currentFlowId.value = id
  page.value = 'editor'
  // Defer init until DOM updates
  nextTick().then(() => {
    nextTickInit()
  })
}
function backToList() { page.value = 'list' }

// LogicFlow
const canvasRef = ref<HTMLDivElement>()
const canvasWrapRef = ref<HTMLDivElement>()
let lf: LogicFlow | null = null
const selected = ref<any | null>(null)

// per-node plus overlays
const nodeOverlays = ref<Array<{ id: string; left: string; top: string }>>([])
const plusVisible = reactive<Record<string, boolean>>({})

class LLMNodeModel extends RectNodeModel {
  setAttributes() {
    this.width = 140; this.height = 52
  }
}
class LLMNodeView extends RectNode {
  getShape() {
    const { width, height, x, y, id } = this.props.model
    const rect = h('rect', {
      x: x - width / 2,
      y: y - height / 2,
      width,
      height,
      rx: 6,
      ry: 6,
      stroke: '#409eff',
      fill: '#fff',
      strokeWidth: 1
    })
    // plus button at right center
    const r = 10
    const cx = x + width / 2 + r + 2
    const cy = y
    const plusCircle = h('circle', {
      cx,
      cy,
      r,
      fill: '#409eff',
      stroke: 'transparent'
    })
    const plusV = h('line', { x1: cx, y1: cy - 5, x2: cx, y2: cy + 5, stroke: '#fff', strokeWidth: 2, strokeLinecap: 'round' })
    const plusH = h('line', { x1: cx - 5, y1: cy, x2: cx + 5, y2: cy, stroke: '#fff', strokeWidth: 2, strokeLinecap: 'round' })
    const plusGroup = h('g', {
      className: 'llm-plus-btn',
      style: 'cursor: pointer;',
      onClick: () => {
        // emit a custom event for Vue layer to open the add menu for this node
        ;(this as any).props?.model?.graphModel?.eventCenter?.emit('llm:plus-click', { id })
      }
    }, [plusCircle, plusV, plusH])
    return h('g', {}, [rect, plusGroup])
  }
}

class ClassifierNodeModel extends RectNodeModel {
  setAttributes() {
    this.width = 140; this.height = 52
  }
}
class ClassifierNodeView extends RectNode {}

function registerNodes() {
  lf?.register({ type: 'llm', model: LLMNodeModel, view: LLMNodeView })
  lf?.register({ type: 'classifier', model: ClassifierNodeModel, view: ClassifierNodeView })
}

function waitForCanvasReady(): Promise<HTMLDivElement> {
  return new Promise((resolve) => {
    const check = () => {
      const el = canvasRef.value as HTMLDivElement | undefined
      if (el && el.isConnected) {
        const rect = el.getBoundingClientRect()
        if (rect.width > 0 && rect.height > 0) {
          resolve(el)
          return
        }
      }
      requestAnimationFrame(check)
    }
    check()
  })
}

// Try to robustly locate node DOM element by id under canvas wrapper
function queryNodeElementById(id: string): SVGGElement | null {
  const wrapEl = canvasWrapRef.value
  if (!wrapEl) return null
  return (
    wrapEl.querySelector(`[data-node-id="${id}"]`) ||
    wrapEl.querySelector(`[data-id="${id}"]`) ||
    wrapEl.querySelector(`g[data-id="${id}"]`)
  ) as SVGGElement | null
}

async function nextTickInit() {
  ensurePlugins()
  const el = await waitForCanvasReady()
  if (lf) {
    try { lf.destroy() } catch {}
    lf = null
  }
  lf = new LogicFlow({
    container: el,
    grid: true,
  })
  registerNodes()
  const graph = await getFlowGraph(currentFlowId.value).catch(() => emptyGraph())
  const normalized = {
    nodes: (graph?.nodes || []).map((n: any) => ({
      ...n,
      x: typeof n.x === 'number' ? n.x : 0,
      y: typeof n.y === 'number' ? n.y : 0,
    })),
    edges: (graph?.edges || []).map((e: any) => ({
      id: e.id || `e-${genId()}`,
      sourceNodeId: e.sourceNodeId,
      targetNodeId: e.targetNodeId,
      label: e.label
    }))
  }
  lf.render(normalized as any)
  lf.on('llm:plus-click', ({ id }: { id: string }) => {
    if (plusVisible[id] === undefined) plusVisible[id] = false
    plusVisible[id] = !plusVisible[id]
  })

  lf.on('selection:selected', ({ nodes }) => {
    selected.value = nodes?.[0] || null
  })
  lf.on('selection:unselected', () => { selected.value = null })

  // update plus overlays on interactions
  updateNodePlusOverlays()
  window.requestAnimationFrame(() => updateNodePlusOverlays())
  lf.on('node:drag', () => updateNodePlusOverlays())
  lf.on('node:drop', () => updateNodePlusOverlays())
  lf.on('graph:transform', () => updateNodePlusOverlays())
  lf.on('node:add', () => updateNodePlusOverlays())
  lf.on('node:delete', () => updateNodePlusOverlays())
}

function updateNodePlusOverlays() {
  const wrapEl = canvasWrapRef.value
  const data = lf?.getGraphData() as any
  if (!wrapEl || !data) return
  const wrapRect = wrapEl.getBoundingClientRect()
  const gap = 12
  const btnSize = 32
  const overlays: Array<{ id: string; left: string; top: string }> = []
  for (const n of (data.nodes || [])) {
    const isEnd = n?.properties?.role === 'end' || n?.text === '结束'
    if (isEnd) continue
    const nodeEl = queryNodeElementById(n.id)
    const nodeRect = (nodeEl as any)?.getBoundingClientRect?.()
    if (!nodeRect) continue
    const left = nodeRect.right - wrapRect.left + gap - btnSize / 2
    const top = nodeRect.top - wrapRect.top + nodeRect.height / 2 - btnSize / 2
    overlays.push({ id: n.id, left: `${left}px`, top: `${top}px` })
    if (plusVisible[n.id] === undefined) plusVisible[n.id] = false
  }
  nodeOverlays.value = overlays
}

function onChooseAddFor(
  sourceId: string,
  type: 'llm' | 'classifier' | 'kb' | 'branch' | 'script' | 'java' | 'http' | 'subflow' | 'reply' | 'end'
) {
  if (!lf) return
  plusVisible[sourceId] = false
  const data = lf.getGraphData() as any
  const node = (data.nodes || []).find((n: any) => n.id === sourceId)
  if (!node) return
  const baseX = Number(node.x) || 0
  const baseY = Number(node.y) || 0
  const newId = 'n-' + genId()
  if (type === 'llm') {
    lf.addNode({ id: newId, type: 'llm', x: baseX + 180, y: baseY, text: 'LLM', properties: { title: 'LLM', model: 'gpt-4o-mini', input: '', output: 'result' } })
  } else if (type === 'classifier') {
    lf.addNode({ id: newId, type: 'classifier', x: baseX + 180, y: baseY, text: '分类器', properties: { title: '分类器', labels: 'A,B', output: 'label' } })
  } else if (type === 'kb') {
    lf.addNode({ id: newId, type: 'rect', x: baseX + 180, y: baseY, text: '知识库', properties: { kind: 'kb' } })
  } else if (type === 'branch') {
    lf.addNode({ id: newId, type: 'rect', x: baseX + 180, y: baseY, text: '条件分支', properties: { kind: 'branch' } })
  } else if (type === 'script') {
    lf.addNode({ id: newId, type: 'rect', x: baseX + 180, y: baseY, text: '脚本执行', properties: { kind: 'script' } })
  } else if (type === 'java') {
    lf.addNode({ id: newId, type: 'rect', x: baseX + 180, y: baseY, text: 'Java增强', properties: { kind: 'java' } })
  } else if (type === 'http') {
    lf.addNode({ id: newId, type: 'rect', x: baseX + 180, y: baseY, text: 'HTTP请求', properties: { kind: 'http' } })
  } else if (type === 'subflow') {
    lf.addNode({ id: newId, type: 'rect', x: baseX + 180, y: baseY, text: '子流程', properties: { kind: 'subflow' } })
  } else if (type === 'reply') {
    lf.addNode({ id: newId, type: 'rect', x: baseX + 180, y: baseY, text: '直接回复', properties: { kind: 'reply' } })
  } else if (type === 'end') {
    lf.addNode({ id: newId, type: 'rect', x: baseX + 180, y: baseY, text: '结束', properties: { role: 'end' } })
  }
  lf.addEdge({ sourceNodeId: sourceId, targetNodeId: newId })
  updateNodePlusOverlays()
}

function addStart() {
  if (!lf) return
  lf.addNode({ type: 'rect', x: 200, y: 120, text: '开始' })
}
function addLLM() {
  if (!lf) return
  lf.addNode({ type: 'llm', x: 360, y: 160, text: 'LLM', properties: { title: 'LLM', model: 'gpt-4o-mini', input: '', output: 'result' } })
}

function saveFlow() {
  if (!lf) return
  const rawData = lf.getGraphData() as any
  
  // Transform LogicFlow data to match backend DTO structure
  const data: FlowGraph = {
    nodes: (rawData.nodes || []).map((node: any) => ({
      id: node.id,
      type: node.type,
      x: typeof node.x === 'number' ? node.x : 0,
      y: typeof node.y === 'number' ? node.y : 0,
      text: typeof node.text === 'string' ? node.text : (node.text ? JSON.stringify(node.text) : ''),
      properties: node.properties || {}
    })),
    edges: (rawData.edges || []).map((edge: any) => ({
      id: edge.id,
      sourceNodeId: edge.sourceNodeId,
      targetNodeId: edge.targetNodeId,
      label: typeof edge.label === 'string' ? edge.label : (edge.label ? JSON.stringify(edge.label) : '')
    }))
  }
  
  saveFlowGraph(currentFlowId.value, data).then(() => {
    ElMessage.success('已保存')
  }).catch((error) => {
    console.error('Save failed:', error)
    ElMessage.error('保存失败: ' + (error.message || '未知错误'))
  })
}

function genId() { return Math.random().toString(36).slice(2, 10) }
function emptyGraph() { return { nodes: [], edges: [] } }
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 12px; }
.mb-12 { margin-bottom: 12px; }
.flow-cards { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 12px; }
.flow-card { cursor: pointer; }
.flow-card.create { display: flex; align-items: center; justify-content: center; color: var(--el-color-primary); }
.flow-card .card-title { display: flex; align-items: center; gap: 8px; font-weight: 600; }
.editor-wrap { height: calc(100vh - 140px); display: flex; flex-direction: column; }
.editor-toolbar { background: #fff; padding: 8px; border: 1px solid var(--el-border-color-lighter); border-radius: 6px; margin-bottom: 8px; }
.editor-body { display: grid; grid-template-columns: 1fr 320px; gap: 8px; height: 100%; }
.canvas-wrap { position: relative; }
.canvas { background: #fff; border: 1px solid var(--el-border-color-lighter); border-radius: 6px; height: 100%; }
.props { background: #fff; border: 1px solid var(--el-border-color-lighter); border-radius: 6px; padding: 12px; overflow: auto; }
.desc { color: #909399; }
.add-menu { display: flex; flex-direction: column; gap: 4px; }
.node-plus { position: absolute; z-index: 100; }
</style> 