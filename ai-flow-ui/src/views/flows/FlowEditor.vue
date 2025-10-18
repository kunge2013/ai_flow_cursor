<template>
  <div class="flow-editor">
    <!-- 顶部工具栏 -->
    <div class="editor-toolbar">
      <div class="toolbar-left">
        <el-button @click="$emit('back')">返回</el-button>
        <el-divider direction="vertical" />
        <el-button type="primary" @click="saveFlow">保存</el-button>
        <el-button @click="previewFlow">预览</el-button>
        <el-button @click="testDrawer">测试抽屉</el-button>
        <el-button @click="testNodePanel">测试节点面板</el-button>
      </div>
      
      <div class="toolbar-right">
        <el-button-group>
          <el-button @click="zoomIn">放大</el-button>
          <el-button @click="zoomOut">缩小</el-button>
          <el-button @click="fitView">适应</el-button>
          <el-button @click="beautifyLayout">布局</el-button>
        </el-button-group>
      </div>
    </div>

    <!-- 主编辑区域 -->
    <div class="editor-main">
      <!-- 画布区域 -->
      <div class="canvas-container">
        <div 
          ref="canvasRef" 
          class="logic-canvas"
          @drop="onDrop"
          @dragover="onDragOver"
        >
          <!-- 占位符只在未初始化时显示 -->
          <div v-if="!isLogicFlowReady" class="canvas-placeholder">
            <div class="placeholder-content">
              <h3>LogicFlow画布</h3>
              <p>正在初始化画布...</p>
              <p>如果长时间未显示，请检查控制台错误</p>
              <p>调试信息: {{ debugInfo }}</p>
            </div>
          </div>

          <!-- 节点面板切换按钮 -->
          <div class="node-panel-toggle" @click="toggleNodePanel" v-show="isLogicFlowReady">
            <i :class="nodePanelVisible ? 'el-icon-d-arrow-left' : 'el-icon-d-arrow-right'"></i>
          </div>
      </div>
        </div>
    </div>

    <!-- 左侧节点面板抽屉 -->
    <el-drawer
      v-model="nodePanelVisible"
      :title="nodePanelTitle"
      direction="ltr"
      size="280px"
      :before-close="handleNodePanelClose"
      class="node-panel-drawer"
    >
      <div class="node-panel-content">
        <div class="node-category">
          <h4>基础节点</h4>
          <div class="node-list">
            <div 
              v-for="node in basicNodes" 
              :key="node.type"
              class="node-item"
              :draggable="true"
              @dragstart="onNodeDragStart($event, node)"
            >
              <div class="node-icon" :style="{ backgroundColor: node.color }">
                <i :class="node.icon"></i>
              </div>
              <span class="node-label">{{ node.label }}</span>
            </div>
          </div>
        </div>

        <div class="node-category">
          <h4>AI节点</h4>
          <div class="node-list">
            <div 
              v-for="node in aiNodes" 
              :key="node.type"
              class="node-item"
              :draggable="true"
              @dragstart="onNodeDragStart($event, node)"
            >
              <div class="node-icon" :style="{ backgroundColor: node.color }">
                <i :class="node.icon"></i>
              </div>
              <span class="node-label">{{ node.label }}</span>
            </div>
          </div>
        </div>

        <div class="node-category">
          <h4>逻辑节点</h4>
          <div class="node-list">
            <div 
              v-for="node in logicNodes" 
              :key="node.type"
              class="node-item"
              :draggable="true"
              @dragstart="onNodeDragStart($event, node)"
            >
              <div class="node-icon" :style="{ backgroundColor: node.color }">
                <i :class="node.icon"></i>
              </div>
              <span class="node-label">{{ node.label }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>

    <!-- 右侧属性面板抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      :title="drawerTitle"
      direction="rtl"
      size="400px"
      :before-close="handleDrawerClose"
      class="property-drawer"
    >
      <div class="drawer-content">
        <!-- 节点属性编辑 -->
        <div v-if="selectedNode" class="node-properties">
          <el-form label-width="80px">
            <el-form-item label="节点名称">
              <el-input v-model="selectedNode.properties.name" @input="onNodeUpdate" />
            </el-form-item>
            
            <!-- LLM节点特有配置 -->
            <template v-if="selectedNode.properties?.nodeType === 'llm'">
              <el-form-item label="选择模型">
                <el-select v-model="selectedNode.properties.model" @change="onNodeUpdate">
                  <el-option label="gpt-4o-mini" value="gpt-4o-mini" />
                  <el-option label="qwen2.5-7b-instruct" value="qwen2.5-7b-instruct" />
                </el-select>
              </el-form-item>
              <el-form-item label="输入变量">
                <el-input 
                  v-model="selectedNode.properties.input" 
                  type="textarea" 
                  :rows="3" 
                  @input="onNodeUpdate"
                />
              </el-form-item>
              <el-form-item label="输出变量">
                <el-input v-model="selectedNode.properties.output" @input="onNodeUpdate" />
              </el-form-item>
            </template>
            
            <!-- 分类器节点特有配置 -->
            <template v-if="selectedNode.properties?.nodeType === 'classifier'">
              <el-form-item label="类别集合">
                <el-input 
                  v-model="selectedNode.properties.labels" 
                  placeholder="逗号分隔，如：正向,负向" 
                  @input="onNodeUpdate"
                />
              </el-form-item>
              <el-form-item label="输出变量">
                <el-input v-model="selectedNode.properties.output" @input="onNodeUpdate" />
              </el-form-item>
            </template>
            
            <!-- 组织器节点特有配置 -->
            <template v-if="selectedNode.properties?.nodeType === 'organizer'">
              <el-form-item label="组织类型">
                <el-select v-model="selectedNode.properties.organizeType" @change="onNodeUpdate">
                  <el-option label="分类整理" value="classify" />
                  <el-option label="排序整理" value="sort" />
                  <el-option label="分组整理" value="group" />
                  <el-option label="过滤整理" value="filter" />
                  <el-option label="合并整理" value="merge" />
                </el-select>
              </el-form-item>
              <el-form-item label="输入数据">
                <el-input 
                  v-model="selectedNode.properties.inputData" 
                  type="textarea" 
                  :rows="3" 
                  placeholder="输入要整理的数据或变量名"
                  @input="onNodeUpdate"
                />
              </el-form-item>
              <el-form-item label="整理规则">
                <el-input 
                  v-model="selectedNode.properties.organizeRules" 
                  type="textarea" 
                  :rows="4" 
                  placeholder="定义整理规则，如：按类别分组、按时间排序等"
                  @input="onNodeUpdate"
                />
              </el-form-item>
              <el-form-item label="输出变量">
                <el-input v-model="selectedNode.properties.output" @input="onNodeUpdate" />
              </el-form-item>
              <el-form-item label="输出格式">
                <el-select v-model="selectedNode.properties.outputFormat" @change="onNodeUpdate">
                  <el-option label="JSON" value="json" />
                  <el-option label="数组" value="array" />
                  <el-option label="对象" value="object" />
                  <el-option label="字符串" value="string" />
                </el-select>
              </el-form-item>
            </template>
            
            <!-- 通用配置 -->
            <el-form-item label="描述">
              <el-input 
                v-model="selectedNode.properties.description" 
                type="textarea" 
                :rows="2" 
                @input="onNodeUpdate"
              />
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 空状态 -->
        <div v-else class="empty-state">
          <el-empty description="选择节点进行编辑" />
          <div class="help-text">
            <p><strong>使用提示：</strong></p>
            <ul>
              <li>从左侧拖拽节点到画布</li>
              <li>点击节点编辑属性</li>
              <li>使用工具栏进行画布操作</li>
            </ul>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { registerCustomNodes } from './nodes/index'
import LogicFlow from '@logicflow/core'
import '@logicflow/core/dist/index.css'

interface Props {
  flowId: string
}

const props = defineProps<Props>()
const emit = defineEmits<{
  back: []
  save: [data: any]
}>()

// 编辑器状态
const canvasRef = ref<HTMLDivElement>()
const selectedNode = ref<any>(null)
const lf = ref<LogicFlow | null>(null)
const debugInfo = ref<string>('初始化中...')
const isInitializing = ref(false)
const isLogicFlowReady = ref(false)
const isComponentMounted = ref(false)

// 抽屉状态
const drawerVisible = ref(true)
const nodePanelVisible = ref(false)
const drawerTitle = computed(() => {
  if (selectedNode.value) {
    const nodeType = selectedNode.value.properties?.nodeType || selectedNode.value.type
    const nodeLabel = getNodeTypeLabel(nodeType)
    return `${nodeLabel}配置`
  }
  return '属性设置'
})
const nodePanelTitle = computed(() => {
  return '节点组件'
})

// 节点类型定义
const basicNodes = [
  { type: 'start', label: '开始', icon: 'el-icon-video-play', color: '#67C23A' },
  { type: 'end', label: '结束', icon: 'el-icon-video-pause', color: '#F56C6C' }
]

const aiNodes = [
  { type: 'llm', label: 'LLM', icon: 'el-icon-cpu', color: '#409EFF' },
  { type: 'classifier', label: '分类器', icon: 'el-icon-collection-tag', color: '#E6A23C' },
  { type: 'knowledge-base', label: '知识库', icon: 'el-icon-folder-opened', color: '#909399' }
]

const logicNodes = [
  { type: 'condition', label: '条件分支', icon: 'el-icon-sort', color: '#9C27B0' },
  { type: 'script', label: '脚本执行', icon: 'el-icon-edit-outline', color: '#FF5722' },
  { type: 'http', label: 'HTTP请求', icon: 'el-icon-link', color: '#607D8B' },
  { type: 'organizer', label: '组织器', icon: 'el-icon-files', color: '#9C27B0' },
  { type: 'subflow', label: '子流程', icon: 'el-icon-connection', color: '#795548' }
]

// 所有节点
const allNodes = computed(() => [...basicNodes, ...aiNodes, ...logicNodes])

// 获取节点类型标签
function getNodeTypeLabel(nodeType: string) {
  const node = allNodes.value.find(n => n.type === nodeType)
  return node?.label || nodeType
}

// 抽屉关闭处理
function handleDrawerClose(done: () => void) {
  selectedNode.value = null
  done()
}

// 节点面板关闭处理
function handleNodePanelClose(done: () => void) {
  nodePanelVisible.value = false
  done()
}

// 切换节点面板
function toggleNodePanel() {
  nodePanelVisible.value = !nodePanelVisible.value
  console.log('节点面板状态:', nodePanelVisible.value)
}

// 测试抽屉功能
function testDrawer() {
  console.log("=== 测试抽屉功能 ===")
  console.log("当前抽屉状态:", drawerVisible.value)
  console.log("当前选中节点:", selectedNode.value)
  
  // 创建一个测试节点数据
  const testNode = {
    id: "test_node_123",
    type: "llm",
    x: 200,
    y: 200,
    properties: {
      name: "测试LLM节点",
      nodeType: "llm",
      model: "gpt-4o-mini",
      input: "测试输入",
      output: "测试输出",
      description: "这是一个测试节点"
    }
  }
  
  selectedNode.value = testNode
  drawerVisible.value = true
  
  console.log("设置测试节点:", selectedNode.value)
  console.log("设置抽屉状态:", drawerVisible.value)
  
  ElMessage.success("测试抽屉已打开")
}

// 测试节点面板功能
function testNodePanel() {
  console.log("=== 测试节点面板功能 ===")
  console.log("当前节点面板状态:", nodePanelVisible.value)
  console.log("LogicFlow是否就绪:", isLogicFlowReady.value)
  
  nodePanelVisible.value = !nodePanelVisible.value
  
  console.log("切换后节点面板状态:", nodePanelVisible.value)
  ElMessage.success(`节点面板已${nodePanelVisible.value ? "打开" : "关闭"}`)
}

// 安全的调试函数
function debugLogicFlow() {
  if (!isComponentMounted.value) {
    console.log('组件已卸载，跳过调试')
    return
  }
  
  console.log('=== LogicFlow 调试信息 ===')
  console.log('canvasRef.value:', canvasRef.value)
  console.log('lf.value:', lf.value)
  console.log('selectedNode.value:', selectedNode.value)
  console.log('isLogicFlowReady.value:', isLogicFlowReady.value)
  
  if (canvasRef.value) {
    try {
      const rect = canvasRef.value.getBoundingClientRect()
      console.log('容器尺寸:', rect)
      debugInfo.value = `容器尺寸: ${rect.width}x${rect.height}`
    } catch (error) {
      console.error('获取容器尺寸失败:', error)
      debugInfo.value = '获取容器尺寸失败'
    }
  }
  
  if (lf.value) {
    console.log('LogicFlow实例:', lf.value)
    try {
      const graphData = lf.value.getGraphData()
      console.log('图数据:', graphData)
      debugInfo.value = `LogicFlow已初始化，容器尺寸: ${canvasRef.value?.getBoundingClientRect().width}x${canvasRef.value?.getBoundingClientRect().height}`
    } catch (error) {
      console.error('获取图数据失败:', error)
      debugInfo.value = `LogicFlow已初始化，但获取图数据失败: ${error}`
    }
  } else {
    debugInfo.value = 'LogicFlow未初始化'
  }
}

// 初始化LogicFlow
async function initLogicFlow() {
  if (isInitializing.value || !isComponentMounted.value) {
    console.log('LogicFlow正在初始化中或组件已卸载，跳过重复初始化')
    return
  }
  
  isInitializing.value = true
  isLogicFlowReady.value = false
  
  console.log('开始初始化LogicFlow...')
  debugInfo.value = '开始初始化...'
  
  if (!canvasRef.value) {
    console.error('canvasRef未找到')
    debugInfo.value = '错误: canvasRef未找到'
    isInitializing.value = false
    return
  }
  
  try {
    // 等待DOM完全渲染
    await nextTick()
    
    const container = canvasRef.value
    
    // 确保容器有正确的尺寸和样式
    container.style.width = '100%'
    container.style.height = '100%'
    container.style.minHeight = '400px'
    container.style.position = 'relative'
    container.style.overflow = 'hidden'
    
    // 再次等待DOM更新
    await nextTick()
    
    // 检查组件是否仍然挂载
    if (!isComponentMounted.value) {
      console.log('组件已卸载，停止初始化')
      isInitializing.value = false
      return
    }
    
    // 获取容器实际尺寸
    const containerRect = container.getBoundingClientRect()
    console.log('容器尺寸:', containerRect)
    
    // 确保容器有最小尺寸
    const width = Math.max(containerRect.width || 800, 800)
    const height = Math.max(containerRect.height || 600, 600)
    
    debugInfo.value = `容器尺寸: ${width}x${height}`
    
    // 创建LogicFlow实例
    lf.value = new LogicFlow({
      container: container,
      width: width,
      height: height,
      grid: true,
      background: {
        backgroundColor: '#fafafa'
      },
      keyboard: {
        enabled: true
      },
      style: {
        rect: {
          rx: 5,
          ry: 5,
          strokeWidth: 1
        }
      }
    })

    // 调用render方法渲染画布
    try {
      lf.value.render()
      console.log("LogicFlow渲染调用成功")
      debugInfo.value = "LogicFlow渲染调用成功"
    } catch (error) {
      console.error("LogicFlow渲染调用失败:", error)
      debugInfo.value = `LogicFlow渲染调用失败: ${error}`
    }

    console.log('LogicFlow实例创建成功:', lf.value)
    debugInfo.value = 'LogicFlow实例创建成功'
    
    // 注册自定义节点
    try {
      registerCustomNodes(lf.value)
      console.log('自定义节点注册成功')
      debugInfo.value = '自定义节点注册成功'
    } catch (error) {
      console.error('自定义节点注册失败:', error)
      debugInfo.value = `自定义节点注册失败: ${error}`
    }
    
    // 确保LogicFlow完全初始化后再添加事件监听器
    await nextTick()
    
    console.log("=== 开始添加事件监听器 ===")
    console.log("LogicFlow实例:", lf.value)
    console.log("LogicFlow实例类型:", typeof lf.value)
    console.log("LogicFlow实例方法:", Object.getOwnPropertyNames(lf.value))

    // 监听节点点击事件
    lf.value.on("node:click", (data: any) => {
      console.log("=== 节点点击事件触发 ===")
      console.log("事件数据:", data)
      console.log("组件挂载状态:", isComponentMounted.value)
      console.log("抽屉当前状态:", drawerVisible.value)
      
      if (!isComponentMounted.value) {
        console.log("组件已卸载，忽略节点点击")
        return
      }
      
      selectedNode.value = data.data
      drawerVisible.value = true
      
      console.log("选中节点:", selectedNode.value)
      console.log("抽屉状态已设置为:", drawerVisible.value)
    })
    
    // 监听画布点击事件
    lf.value.on('blank:click', () => {
      if (!isComponentMounted.value) return
      console.log('画布点击事件')
      selectedNode.value = null
      drawerVisible.value = false
    })
    
    // 监听窗口大小变化
    const resizeHandler = () => {
      if (lf.value && canvasRef.value && isComponentMounted.value) {
        try {
          const rect = canvasRef.value.getBoundingClientRect()
          lf.value.resize(rect.width, rect.height)
        } catch (error) {
          console.error('窗口大小调整失败:', error)
        }
      }
    }
    window.addEventListener('resize', resizeHandler)
    
    // 保存resize处理器以便清理
    ;(lf.value as any)._resizeHandler = resizeHandler
    
    // 标记LogicFlow已准备就绪
    isLogicFlowReady.value = true
    
    console.log('LogicFlow初始化完成')
    debugInfo.value = 'LogicFlow初始化完成'
    
    // 添加一个测试节点来验证渲染
    setTimeout(() => {
      if (lf.value && isComponentMounted.value) {
        try {
          const testNode = {
            id: 'test_node_1',
            type: 'rect',
            x: 100,
            y: 100,
            text: '测试节点'
          }
          lf.value.addNode(testNode)
          console.log('测试节点添加成功')
        } catch (error) {
          console.error('添加测试节点失败:', error)
        }
      }
    }, 500)
    
  } catch (error) {
    console.error('LogicFlow初始化失败:', error)
    debugInfo.value = `初始化失败: ${error}`
    ElMessage.error(`LogicFlow初始化失败: ${error}`)
  } finally {
    isInitializing.value = false
  }
}

// 清理函数
function cleanup() {
  console.log('开始清理资源...')
  
  if (lf.value) {
    try {
      // 移除事件监听器
      const resizeHandler = (lf.value as any)._resizeHandler
      if (resizeHandler) {
        window.removeEventListener('resize', resizeHandler)
      }
      
      // 清理LogicFlow实例
      if (typeof lf.value.destroy === 'function') {
        lf.value.destroy()
      }
    } catch (error) {
      console.error('清理LogicFlow失败:', error)
    }
    lf.value = null
  }
  
  isInitializing.value = false
  isLogicFlowReady.value = false
  selectedNode.value = null
  drawerVisible.value = false
  nodePanelVisible.value = false
}

// 组件挂载时初始化
onMounted(async () => {
  console.log('组件挂载，开始初始化...')
  isComponentMounted.value = true
  debugInfo.value = '组件挂载，开始初始化...'
  
  // 延迟初始化，确保DOM完全渲染
  setTimeout(async () => {
    if (isComponentMounted.value) {
      await initLogicFlow()
      // 延迟调试，确保初始化完成
      setTimeout(() => {
        if (isComponentMounted.value) {
          debugLogicFlow()
        }
      }, 1000)
    }
  }, 200)
})

// 组件卸载时清理
onUnmounted(() => {
  console.log('组件卸载，清理资源...')
  isComponentMounted.value = false
  cleanup()
})

// 拖拽开始
function onNodeDragStart(event: DragEvent, nodeType: any) {
  if (!event.dataTransfer) return
  event.dataTransfer.setData('application/json', JSON.stringify(nodeType))
  event.dataTransfer.effectAllowed = 'copy'
  console.log('拖拽开始:', nodeType)
}

// 拖拽悬停
function onDragOver(event: DragEvent) {
  event.preventDefault()
  if (event.dataTransfer) {
    event.dataTransfer.dropEffect = 'copy'
  }
}

// 拖拽放置
async function onDrop(event: DragEvent) {
  event.preventDefault()
  
  if (!isComponentMounted.value) {
    console.log('组件已卸载，忽略拖拽事件')
    return
  }
  
  console.log('拖拽放置事件触发')
  
  if (!event.dataTransfer) {
    console.error('拖拽数据不存在')
    ElMessage.error('拖拽数据不存在')
    return
  }
  
  if (!lf.value) {
    console.error('LogicFlow实例不存在')
    ElMessage.error('LogicFlow未初始化，请稍后重试')
    return
  }
  
  if (!canvasRef.value) {
    console.error('画布引用不存在')
    ElMessage.error('画布引用不存在')
    return
  }
  
  try {
    const nodeTypeData = event.dataTransfer.getData('application/json')
    if (!nodeTypeData) {
      console.error('拖拽数据为空')
      ElMessage.error('拖拽数据为空')
      return
    }
    
    const nodeType = JSON.parse(nodeTypeData)
    console.log('添加节点:', nodeType)
    
    // 获取鼠标位置
    const rect = canvasRef.value.getBoundingClientRect()
    console.log('画布位置:', rect)
    
    const x = event.clientX - rect.left
    const y = event.clientY - rect.top
    
    console.log('鼠标位置:', { x, y })
    
    // 创建节点数据
    const nodeData: any = {
      id: `${nodeType.type}_${Date.now()}`,
      type: nodeType.type,
      x: x,
      y: y,
      properties: {
        name: nodeType.label,
        nodeType: nodeType.type,
        description: ''
      }
    }
    
    // 根据节点类型设置特定属性
    if (nodeType.type === 'llm') {
      nodeData.properties.model = 'gpt-4o-mini'
      nodeData.properties.input = ''
      nodeData.properties.output = 'result'
    } else if (nodeType.type === 'classifier') {
      nodeData.properties.labels = ''
      nodeData.properties.output = 'result'
    } else if (nodeType.type === 'organizer') {
      nodeData.properties.organizeType = 'classify'
      nodeData.properties.inputData = ''
      nodeData.properties.organizeRules = ''
      nodeData.properties.output = 'result'
      nodeData.properties.outputFormat = 'json'
    } else {
      nodeData.properties.output = 'result'
    }
    
    console.log('创建的节点数据:', nodeData)
    
    // 在LogicFlow中添加节点
    lf.value.addNode(nodeData)
    
    // 使用nextTick确保DOM更新完成后再更新selectedNode
    await nextTick()
    
    // 检查组件是否仍然挂载
    if (isComponentMounted.value) {
      // 选中新添加的节点并打开抽屉
      selectedNode.value = nodeData
      drawerVisible.value = true
      console.log('新添加的节点:', selectedNode.value)
      
      ElMessage.success(`已添加${nodeType.label}节点`)
    }
  } catch (error) {
    console.error('拖拽添加节点失败:', error)
    ElMessage.error(`添加节点失败: ${error}`)
  }
}

// 节点更新
function onNodeUpdate() {
  if (!selectedNode.value || !lf.value || !isComponentMounted.value) return
  
  console.log('节点更新:', selectedNode.value)
  
  // 更新LogicFlow中的节点
  try {
    lf.value.setProperties(selectedNode.value.id, selectedNode.value.properties)
    console.log('LogicFlow节点属性更新成功')
  } catch (error) {
    console.error('LogicFlow节点属性更新失败:', error)
  }
}

// 工具栏操作
function zoomIn() {
  if (lf.value && isComponentMounted.value) {
    lf.value.zoom(true)
  }
}

function zoomOut() {
  if (lf.value && isComponentMounted.value) {
    lf.value.zoom(false)
  }
}

function fitView() {
  if (lf.value && isComponentMounted.value) {
    lf.value.fitView()
  }
}

function beautifyLayout() {
  if (lf.value && isComponentMounted.value) {
    try {
      // 简单的自动布局
      const nodes = (lf.value.getGraphData() as any).nodes
      const spacing = 200
      
      nodes.forEach((node: any, index: number) => {
        const x = 100 + (index % 3) * spacing
        const y = 100 + Math.floor(index / 3) * spacing
        ;(lf.value as any).setNodeData(node.id, { x, y })
      })
      
      ElMessage.success('布局完成')
    } catch (error) {
      console.error('布局失败:', error)
      ElMessage.error('布局失败')
    }
  } else {
    ElMessage.info('自动布局功能开发中...')
  }
}

// 保存流程
async function saveFlow() {
  try {
    if (lf.value && isComponentMounted.value) {
      const graphData = lf.value.getGraphData()
      emit('save', graphData)
      ElMessage.success('保存成功')
    } else {
      ElMessage.error('LogicFlow未初始化')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

// 预览流程
function previewFlow() {
  ElMessage.info('预览功能开发中...')
}
</script>

<style scoped>
.flow-editor {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.editor-toolbar {
  height: 56px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.editor-main {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.canvas-container {
  flex: 1;
  position: relative;
  background: #fafafa;
  min-height: 400px;
}

.logic-canvas {
  width: 100%;
  height: 100%;
  min-height: 400px;
  position: relative;
}

.canvas-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
}

.placeholder-content {
  text-align: center;
  color: #666;
}

.placeholder-content h3 {
  margin: 0 0 16px 0;
  color: #333;
}

.placeholder-content p {
  margin: 8px 0;
  font-size: 14px;
}

/* 节点面板切换按钮 */
.node-panel-toggle {
  position: absolute;
  top: 20px;
  left: 20px;
  width: 40px;
  height: 40px;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1001;
  transition: all 0.2s;
}

.node-panel-toggle:hover {
  background: #f5f7fa;
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.node-panel-toggle i {
  font-size: 16px;
  color: #606266;
}

.node-panel-toggle:hover i {
  color: #409eff;
}
/* 节点面板抽屉样式 */
.node-panel-drawer {
  z-index: 2000;
}

.node-panel-content {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.node-category {
  margin-bottom: 24px;
}

.node-category h4 {
  margin: 0 0 12px 0;
  font-size: 12px;
  font-weight: 600;
  color: #909399;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.node-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.node-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: grab;
  transition: all 0.2s;
  background: white;
}

.node-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
  transform: translateY(-1px);
}

.node-item:active {
  cursor: grabbing;
  transform: translateY(0);
}

.node-icon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  margin-bottom: 6px;
}

.node-label {
  font-size: 12px;
  color: #606266;
  text-align: center;
  line-height: 1.2;
}

/* 属性面板抽屉样式 */
.property-drawer {
  z-index: 2000;
}

.drawer-content {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.node-properties h4 {
  margin: 0 0 16px 0;
  color: #303133;
  font-size: 14px;
  font-weight: 600;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  padding: 20px;
}

.help-text {
  margin-top: 20px;
  text-align: left;
  font-size: 12px;
  color: #606266;
}

.help-text ul {
  margin: 8px 0;
  padding-left: 20px;
}

.help-text li {
  margin: 4px 0;
}

/* 抽屉动画优化 */
.node-panel-drawer :deep(.el-drawer__body) {
  padding: 0;
}

.node-panel-drawer :deep(.el-drawer__header) {
  padding: 20px 20px 0 20px;
  margin-bottom: 0;
  border-bottom: 1px solid #e4e7ed;
}

.node-panel-drawer :deep(.el-drawer__title) {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.property-drawer :deep(.el-drawer__body) {
  padding: 0;
}

.property-drawer :deep(.el-drawer__header) {
  padding: 20px 20px 0 20px;
  margin-bottom: 0;
  border-bottom: 1px solid #e4e7ed;
}

.property-drawer :deep(.el-drawer__title) {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
</style>
