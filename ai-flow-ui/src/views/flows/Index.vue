<template>
  <div class="page">
    <!-- 流程列表页面 -->
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
          <div class="create-inner">
            <el-icon><Plus /></el-icon> 
            添加流程
          </div>
        </el-card>
      </div>

      <!-- 创建流程对话框 -->
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

    <!-- 流程编辑器 -->
    <FlowEditor 
      v-else-if="page === 'editor'"
      :flow-id="currentFlowId"
      @back="backToList"
      @save="onSaveFlow"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { listFlows, createFlow, saveFlowGraph, type FlowSummary, type FlowUpsertRequest, type FlowGraph } from '../../api/flows'
import FlowEditor from './FlowEditor.vue'

// 页面状态
const query = reactive({ name: '' })
const flows = ref<FlowSummary[]>([])
const page = ref<'list' | 'editor'>('list')
const currentFlowId = ref<string>('')

// 获取流程列表
async function fetchFlows() {
  try {
    flows.value = await listFlows({ name: query.name || undefined })
  } catch (error) {
    console.error('获取流程列表失败:', error)
    ElMessage.error('获取流程列表失败')
  }
}

onMounted(fetchFlows)

const filteredFlows = computed(() => {
  const name = query.name.trim()
  return flows.value.filter(f => (name ? f.name.includes(name) : true))
})

function onSearch() { 
  fetchFlows() 
}

function reset() { 
  query.name = ''
  fetchFlows() 
}

// 创建流程
const createVisible = ref(false)
const createFormRef = ref<FormInstance>()
const createForm = reactive<FlowUpsertRequest>({ name: '', description: '' })
const createRules = reactive<FormRules<FlowUpsertRequest>>({
  name: [{ required: true, message: '请输入流程名称', trigger: 'blur' }]
})

function openCreate() { 
  createVisible.value = true 
}

async function onCreateConfirm() {
  try {
    await createFormRef.value?.validate()
    const created = await createFlow({ 
      name: createForm.name, 
      description: createForm.description || '' 
    })
    createVisible.value = false
    ElMessage.success('创建成功')
    openEditor(created.id)
  } catch (error) {
    console.error('创建流程失败:', error)
    ElMessage.error('创建流程失败')
  }
}

function onCreateClosed() { 
  createForm.name = ''
  createForm.description = '' 
}

// 编辑器操作
function openEditor(id: string) {
  currentFlowId.value = id
  page.value = 'editor'
}

function backToList() { 
  page.value = 'list'
  fetchFlows() // 刷新列表
}

async function onSaveFlow(graphData: any) {
  try {
    // 转换数据格式以匹配后端API
    const flowGraph: FlowGraph = {
      nodes: (graphData.nodes || []).map((node: any) => ({
        id: node.id,
        type: node.type,
        x: typeof node.x === 'number' ? node.x : 0,
        y: typeof node.y === 'number' ? node.y : 0,
        text: node.text || '',
        properties: node.properties || {}
      })),
      edges: (graphData.edges || []).map((edge: any) => ({
        id: edge.id,
        sourceNodeId: edge.sourceNodeId,
        targetNodeId: edge.targetNodeId,
        label: edge.label || ''
      }))
    }
    
    await saveFlowGraph(currentFlowId.value, flowGraph)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存流程失败:', error)
    ElMessage.error('保存流程失败')
  }
}
</script>

<style scoped>
.page { 
  display: flex; 
  flex-direction: column; 
  gap: 12px; 
}

.mb-12 { 
  margin-bottom: 12px; 
}

.flow-cards { 
  display: grid; 
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); 
  gap: 12px; 
}

.flow-card { 
  cursor: pointer; 
  transition: all 0.3s ease;
}

.flow-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.flow-card.create { 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  color: var(--el-color-primary);
  border: 2px dashed var(--el-border-color);
  min-height: 120px;
}

.flow-card.create:hover {
  border-color: var(--el-color-primary);
  background-color: var(--el-color-primary-light-9);
}

.flow-card .card-title { 
  display: flex; 
  align-items: center; 
  gap: 8px; 
  font-weight: 600; 
}

.create-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  font-size: 16px;
}

.desc { 
  color: #909399; 
  margin-top: 8px;
  font-size: 14px;
}
</style>
