<template>
  <div class="kb-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>AI知识库</h2>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入知识库名称"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 知识库列表 -->
    <div class="kb-list-section">
      <div class="kb-grid">
        <!-- 创建知识库卡片 -->
        <div class="kb-card create-card" @click="showCreateDialog">
          <div class="create-icon">
            <el-icon><Plus /></el-icon>
          </div>
          <div class="create-text">+ 创建知识库</div>
        </div>

        <!-- 知识库卡片列表 -->
        <div
          v-for="kb in kbList"
          :key="kb.id"
          class="kb-card"
          @click="handleKbClick(kb)"
          @mouseenter="showActions(kb.id)"
          @mouseleave="handleCardMouseLeave"
        >
          <div class="kb-card-header">
            <h3 class="kb-name">{{ kb.name }}</h3>
            <div 
              class="kb-actions" 
              v-show="hoveredKbId === kb.id" 
              @click.stop
              @mouseenter="handleActionsMouseEnter"
              @mouseleave="handleActionsMouseLeave"
            >
              <el-dropdown 
                @command="handleAction" 
                trigger="click"
                placement="bottom-end"
                :teleported="false"
                @visible-change="handleDropdownVisibleChange"
                popper-class="kb-actions-dropdown"
              >
                <el-button type="text" class="action-btn">
                  <el-icon><MoreFilled /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :command="{ action: 'edit', kb }">
                      <el-icon><Edit /></el-icon>编辑
                    </el-dropdown-item>
                    <el-dropdown-item :command="{ action: 'delete', kb }">
                      <el-icon><Delete /></el-icon>删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
          <div class="kb-description">{{ kb.description || '暂无描述' }}</div>
          <div class="kb-meta">
            <span class="kb-tags">
              <el-tag
                v-for="tag in kb.tags || []"
                :key="tag"
                size="small"
                class="kb-tag"
              >
                {{ tag }}
              </el-tag>
            </span>
            <span class="kb-time">{{ formatTime(kb.createdAt) }}</span>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          :pager-count="7"
          prev-text="上一页"
          next-text="下一页"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 创建/编辑知识库对话框 -->
    <el-dialog
      v-model="kbDialogVisible"
      :title="isEdit ? '编辑知识库' : '创建知识库'"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="kbFormRef"
        :model="kbForm"
        :rules="kbFormRules"
        label-width="100px"
      >
        <el-form-item label="知识库名称" prop="name">
          <el-input v-model="kbForm.name" placeholder="请输入知识库名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="kbForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入知识库描述"
          />
        </el-form-item>
        <el-form-item label="向量模型" prop="vectorModel">
          <el-select v-model="kbForm.vectorModel" placeholder="请选择向量模型" style="width: 100%">
            <el-option label="OpenAI向量" value="openai" />
            <el-option label="智普向量" value="zhipu" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="kbForm.status"
            active-text="启用"
            inactive-text="禁用"
            :active-value="true"
            :inactive-value="false"
          />
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input
            v-model="kbForm.tagsInput"
            placeholder="请输入标签，用逗号分隔"
            @blur="handleTagsBlur"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="kbDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveKb" :loading="saving">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 知识库详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentKb?.name"
      width="80%"
      class="kb-detail-dialog"
      @close="handleDetailClose"
    >
      <div class="kb-detail-content">
        <!-- 左侧Tab菜单 -->
        <div class="kb-detail-left">
          <el-tabs v-model="activeTab" class="kb-tabs">
            <el-tab-pane label="文档" name="documents">
              <div class="tab-content">
                <div class="document-actions">
                  <el-button type="primary" @click="showUploadDialog">
                    <el-icon><Upload /></el-icon>
                    文件上传
                  </el-button>
                  <el-button type="primary" @click="showManualInputDialog">
                    <el-icon><Edit /></el-icon>
                    手动录入
                  </el-button>
                  <el-button type="primary" @click="showDocLibraryDialog">
                    <el-icon><Folder /></el-icon>
                    文档库上传
                  </el-button>
                </div>
                <div class="document-list">
                  <el-table v-if="documentList.length > 0" :data="documentList" style="width: 100%">
                    <el-table-column prop="title" label="标题" min-width="200" />
                    <el-table-column prop="type" label="类型" width="100" />
                    <el-table-column prop="size" label="大小" width="100">
                      <template #default="{ row }">
                        {{ formatFileSize(row.size || 0) }}
                      </template>
                    </el-table-column>
                    <el-table-column prop="createdAt" label="创建时间" width="180">
                      <template #default="{ row }">
                        {{ formatTime(row.createdAt) }}
                      </template>
                    </el-table-column>
                    <el-table-column label="操作" width="120">
                      <template #default="{ row }">
                        <el-button type="danger" size="small" @click="handleDeleteDocument(row)">
                          删除
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                  <el-empty v-else description="暂无文档" />
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="命中测试" name="testing">
              <div class="tab-content">
                <div class="test-config">
                  <el-row :gutter="20">
                    <el-col :span="8">
                      <el-input-number
                        v-model="testConfig.topK"
                        :min="1"
                        :max="100"
                        placeholder="条数"
                        style="width: 100%"
                      >
                        <template #prefix>条数</template>
                      </el-input-number>
                    </el-col>
                    <el-col :span="8">
                      <el-input-number
                        v-model="testConfig.scoreThreshold"
                        :min="0"
                        :max="1"
                        :step="0.1"
                        placeholder="Score阈值"
                        style="width: 100%"
                      >
                        <template #prefix>Score阈值</template>
                      </el-input-number>
                    </el-col>
                  </el-row>
                </div>
                <div class="chat-container">
                  <div class="chat-messages" ref="chatMessagesRef">
                    <div
                      v-for="(message, index) in chatMessages"
                      :key="index"
                      :class="['chat-message', message.type]"
                    >
                      <div class="message-content">{{ message.content }}</div>
                      <div class="message-time">{{ formatTime(message.timestamp) }}</div>
                    </div>
                  </div>
                  <div class="chat-input">
                    <el-input
                      v-model="testInput"
                      type="textarea"
                      :rows="3"
                      placeholder="请输入测试内容"
                      @keyup.ctrl.enter="handleSendTest"
                    />
                    <el-button
                      type="primary"
                      @click="handleSendTest"
                      :loading="testing"
                      style="margin-top: 10px; width: 100%"
                    >
                      发送
                    </el-button>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </el-dialog>

    <!-- 文件上传对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="文件上传" width="600px">
      <FileUpload
        v-model="fileList"
        @upload-success="handleFileUploadSuccess"
        @upload-error="handleFileUploadError"
      />
      <template #footer>
        <el-button @click="uploadDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 手动录入对话框 -->
    <el-dialog v-model="manualInputDialogVisible" title="手动录入" width="800px">
      <el-form :model="manualInputForm" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="manualInputForm.title" placeholder="请输入文档标题" />
        </el-form-item>
        <el-form-item label="内容">
          <RichTextEditor
            v-model="manualInputForm.content"
            placeholder="请输入文档内容"
            height="400px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="manualInputDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleManualInput">保存</el-button>
      </template>
    </el-dialog>

    <!-- 文档库上传对话框 -->
    <el-dialog v-model="docLibraryDialogVisible" title="文档库上传" width="800px">
      <DocLibraryUpload
        v-model="docLibraryFiles"
        @upload-success="handleDocLibraryUploadSuccess"
        @upload-error="handleDocLibraryUploadError"
      />
      <template #footer>
        <el-button @click="docLibraryDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template> 

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  MoreFilled,
  Edit,
  Delete,
  Upload,
  Folder,
  UploadFilled
} from '@element-plus/icons-vue'
import { 
  listKb, 
  createKb, 
  updateKb, 
  deleteKb, 
  getDocuments, 
  addDocument, 
  deleteDocument, 
  testQuery,
  type KnowledgeBaseSummary,
  type KnowledgeBaseUpsertRequest,
  type DocumentInfo,
  type DocumentUploadRequest,
  type TestQueryRequest
} from '../../api/kb'
import { 
  searchSimilar,
  type VectorSearchRequest,
  type VectorSearchResponse
} from '../../api/vector'
import RichTextEditor from '../../components/RichTextEditor.vue'
import FileUpload from '../../components/FileUpload.vue'
import DocLibraryUpload from '../../components/DocLibraryUpload.vue'

// 搜索表单
const searchForm = reactive({
  name: ''
})

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 知识库列表
const kbList = ref<KnowledgeBaseSummary[]>([])
const loading = ref(false)
const hoveredKbId = ref('')
const hideActionsTimer = ref<number | null>(null)

// 对话框状态
const kbDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const uploadDialogVisible = ref(false)
const manualInputDialogVisible = ref(false)
const docLibraryDialogVisible = ref(false)

// 表单相关
const kbFormRef = ref()
const isEdit = ref(false)
const saving = ref(false)
const kbForm = reactive({
  id: '',
  name: '',
  description: '',
  vectorModel: 'openai',
  status: true,
  tags: [] as string[],
  tagsInput: ''
})

// 表单验证规则
const kbFormRules = {
  name: [
    { required: true, message: '请输入知识库名称', trigger: 'blur' }
  ],
  vectorModel: [
    { required: true, message: '请选择向量模型', trigger: 'change' }
  ]
}

// 知识库详情相关
const currentKb = ref<KnowledgeBaseSummary | null>(null)
const activeTab = ref('documents')
const documentList = ref<DocumentInfo[]>([])

// 测试配置
const testConfig = reactive({
  topK: 5,
  scoreThreshold: 0.7
})

// 聊天相关
const chatMessages = ref<Array<{type: string, content: string, timestamp: Date}>>([])
const testInput = ref('')
const testing = ref(false)
const chatMessagesRef = ref()

// 文件上传相关
const fileList = ref<File[]>([])
const uploading = ref(false)

// 手动录入相关
const manualInputForm = reactive({
  title: '',
  content: ''
})

// 文档库上传相关
const docLibraryFiles = ref<File[]>([])

// 获取知识库列表
const getKbList = async () => {
  loading.value = true
  try {
    const params = {
      name: searchForm.name || undefined,
      page: pagination.currentPage,
      size: pagination.pageSize
    }
    
    const response = await listKb(params)
    kbList.value = response.records
    pagination.total = response.total
  } catch (error) {
    console.error('获取知识库列表失败:', error)
    ElMessage.error('获取知识库列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  getKbList()
}

// 重置搜索
const handleReset = () => {
  searchForm.name = ''
  pagination.currentPage = 1
  getKbList()
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  getKbList()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.currentPage = page
  getKbList()
}

// 显示操作按钮
const showActions = (kbId: string) => {
  hoveredKbId.value = kbId
}

// 隐藏操作按钮
const hideActions = () => {
  hoveredKbId.value = ''
}

// 处理卡片鼠标离开事件
const handleCardMouseLeave = () => {
  // 延迟隐藏，避免闪屏
  setTimeout(() => {
    if (hoveredKbId.value) {
      hideActions()
    }
  }, 150)
}

// 处理操作按钮区域鼠标进入事件
const handleActionsMouseEnter = () => {
  // 鼠标进入操作按钮区域时，保持显示状态
  // 清除之前的隐藏定时器
  if (hideActionsTimer.value) {
    clearTimeout(hideActionsTimer.value)
    hideActionsTimer.value = null
  }
}

// 处理操作按钮区域鼠标离开事件
const handleActionsMouseLeave = () => {
  // 鼠标离开操作按钮区域时，延迟隐藏操作按钮
  if (hideActionsTimer.value) {
    clearTimeout(hideActionsTimer.value)
  }
  hideActionsTimer.value = setTimeout(() => {
    hideActions()
  }, 200)
}

// 处理下拉菜单显示状态变化
const handleDropdownVisibleChange = (visible: boolean) => {
  if (visible) {
    // 下拉菜单打开时，保持操作按钮显示
    // 不需要做任何操作，因为菜单已经打开
  } else {
    // 下拉菜单关闭时，延迟隐藏操作按钮
    setTimeout(() => {
      if (hoveredKbId.value) {
        hideActions()
      }
    }, 200)
  }
}

// 处理操作
const handleAction = async (command: { action: string, kb: KnowledgeBaseSummary }) => {
  const { action, kb } = command
  
  if (action === 'edit') {
    // 编辑操作：直接弹出编辑对话框
    handleEdit(kb)
  } else if (action === 'delete') {
    // 删除操作：弹出确认对话框
    handleDelete(kb)
  }
}

// 点击知识库卡片
const handleKbClick = async (kb: KnowledgeBaseSummary) => {
  currentKb.value = kb
  detailDialogVisible.value = true
  activeTab.value = 'documents'
  await getDocumentList()
}

// 显示创建对话框
const showCreateDialog = () => {
  isEdit.value = false
  resetKbForm()
  kbDialogVisible.value = true
}

// 编辑知识库
const handleEdit = (kb: KnowledgeBaseSummary) => {
  isEdit.value = true
  Object.assign(kbForm, {
    id: kb.id,
    name: kb.name,
    description: kb.description || '',
    vectorModel: kb.vectorModel || 'openai',
    status: kb.status !== false,
    tags: kb.tags || [],
    tagsInput: (kb.tags || []).join(',')
  })
  kbDialogVisible.value = true
}

// 删除知识库
const handleDelete = async (kb: KnowledgeBaseSummary) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除知识库 "${kb.name}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteKb(kb.id)
    ElMessage.success('删除成功')
    getKbList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 保存知识库
const handleSaveKb = async () => {
  try {
    if (kbFormRef.value) {
      await (kbFormRef.value as any).validate()
    }
    
    saving.value = true
    
    if (isEdit.value) {
      // 更新现有知识库
      await updateKb(kbForm.id, {
        name: kbForm.name,
        description: kbForm.description,
        vectorModel: kbForm.vectorModel,
        status: kbForm.status,
        tags: kbForm.tags
      })
      ElMessage.success('更新成功')
    } else {
      // 创建新知识库
      await createKb({
        name: kbForm.name,
        description: kbForm.description,
        vectorModel: kbForm.vectorModel,
        status: kbForm.status,
        tags: kbForm.tags
      })
      ElMessage.success('创建成功')
    }
    
    kbDialogVisible.value = false
    getKbList()
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 重置表单
const resetKbForm = () => {
  Object.assign(kbForm, {
    id: '',
    name: '',
    description: '',
    vectorModel: 'openai',
    status: true,
    tags: [] as string[],
    tagsInput: ''
  })
}

// 处理标签输入
const handleTagsBlur = () => {
  if (kbForm.tagsInput) {
    kbForm.tags = kbForm.tagsInput.split(',').map(tag => tag.trim()).filter(tag => tag)
  } else {
    kbForm.tags = []
  }
}

// 对话框关闭处理
const handleDialogClose = () => {
  resetKbForm()
  if (kbFormRef.value) {
    (kbFormRef.value as any).clearValidate()
  }
}

// 详情对话框关闭处理
const handleDetailClose = () => {
  currentKb.value = null
  activeTab.value = 'documents'
  chatMessages.value = []
  testInput.value = ''
}

// 显示上传对话框
const showUploadDialog = () => {
  uploadDialogVisible.value = true
  fileList.value = []
}

// 显示手动录入对话框
const showManualInputDialog = () => {
  manualInputDialogVisible.value = true
  manualInputForm.title = ''
  manualInputForm.content = ''
}

// 显示文档库对话框
const showDocLibraryDialog = () => {
  ElMessage.info('文档库上传功能开发中')
}

// 处理文件变化
const handleFileChange = (file: any) => {
  console.log('文件变化:', file)
}

// 获取文档列表
const getDocumentList = async () => {
  if (!currentKb.value) return
  
  try {
    const docs = await getDocuments(currentKb.value.id)
    documentList.value = docs
  } catch (error) {
    console.error('获取文档列表失败:', error)
    ElMessage.error('获取文档列表失败')
  }
}

// 处理文件上传
const handleUpload = async () => {
  if (!currentKb.value || !fileList.value.length) return
  
  try {
    uploading.value = true
    
    for (const file of fileList.value) {
      const content = await readFileContent(file)
      await addDocument(currentKb.value.id, {
        title: file.name,
        content: content,
        type: file.name.split('.').pop() || 'text',
        size: file.size
      })
    }
    
    ElMessage.success('上传成功')
    uploadDialogVisible.value = false
    fileList.value = []
    getDocumentList()
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}

// 文件上传成功处理
const handleFileUploadSuccess = (response: any) => {
  ElMessage.success('文件上传成功')
  uploadDialogVisible.value = false
  fileList.value = []
  // 刷新文档列表
  if (currentKb.value) {
    getDocumentList()
  }
}

// 文件上传失败处理
const handleFileUploadError = (error: any) => {
  ElMessage.error('文件上传失败')
  console.error('文件上传失败:', error)
}

// 处理手动录入
const handleManualInput = async () => {
  if (!currentKb.value || !manualInputForm.title || !manualInputForm.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  
  try {
    await addDocument(currentKb.value.id, {
      title: manualInputForm.title,
      content: manualInputForm.content,
      type: 'text',
      size: manualInputForm.content.length
    })
    
    ElMessage.success('添加成功')
    manualInputDialogVisible.value = false
    manualInputForm.title = ''
    manualInputForm.content = ''
    getDocumentList()
  } catch (error) {
    console.error('添加失败:', error)
    ElMessage.error('添加失败')
  }
}

// 处理命中测试
const handleSendTest = async () => {
  if (!currentKb.value || !testInput.value.trim()) {
    ElMessage.warning('请输入测试内容')
    return
  }
  
  try {
    testing.value = true
    
    // 使用向量检索进行相似度搜索
    const vectorRequest: VectorSearchRequest = {
      query: testInput.value.trim(),
      kbId: currentKb.value.id,
      topK: testConfig.topK,
      scoreThreshold: testConfig.scoreThreshold
    }
    
    const vectorResponse = await searchSimilar(vectorRequest)
    
    // 添加用户消息
    chatMessages.value.push({
      type: 'user',
      content: testInput.value.trim(),
      timestamp: new Date()
    })
    
    // 构建AI回复，包含检索结果
    let aiResponse = '根据向量检索结果，找到以下相关文档：\n\n'
    
    if (vectorResponse.results.length > 0) {
      vectorResponse.results.forEach((result, index) => {
        aiResponse += `${index + 1}. **${result.title}** (相似度: ${(result.score * 100).toFixed(1)}%)\n`
        aiResponse += `   内容: ${result.content.substring(0, 100)}...\n\n`
      })
    } else {
      aiResponse = '未找到相关文档，请尝试调整搜索参数或使用不同的查询词。'
    }
    
    // 添加AI回复
    chatMessages.value.push({
      type: 'ai',
      content: aiResponse,
      timestamp: new Date()
    })
    
    testInput.value = ''
    
    // 滚动到底部
    setTimeout(() => {
      if (chatMessagesRef.value) {
        chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
      }
    }, 100)
    
  } catch (error) {
    console.error('测试失败:', error)
    ElMessage.error('测试失败')
    
    // 添加错误消息
    chatMessages.value.push({
      type: 'ai',
      content: '抱歉，向量检索失败，请稍后重试。',
      timestamp: new Date()
    })
  } finally {
    testing.value = false
  }
}

// 读取文件内容
const readFileContent = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      resolve(e.target?.result as string)
    }
    reader.onerror = reject
    reader.readAsText(file)
  })
}

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 删除文档
const handleDeleteDocument = async (doc: DocumentInfo) => {
  if (!currentKb.value) return
  
  try {
    await ElMessageBox.confirm(
      `确定要删除文档 "${doc.title}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteDocument(currentKb.value.id, doc.id)
    ElMessage.success('删除成功')
    getDocumentList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 文档库上传成功处理
const handleDocLibraryUploadSuccess = (response: any) => {
  ElMessage.success('文档库上传成功')
  docLibraryDialogVisible.value = false
  docLibraryFiles.value = []
  // 刷新文档列表
  if (currentKb.value) {
    getDocumentList()
  }
}

// 文档库上传失败处理
const handleDocLibraryUploadError = (error: any) => {
  ElMessage.error('文档库上传失败')
  console.error('文档库上传失败:', error)
}

// 格式化时间
const formatTime = (time: string | Date) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 页面加载时获取数据
onMounted(() => {
  getKbList()
})
</script>

<style scoped>
.kb-container {
  padding: 10px;
  background-color: #f5f7fa;
  height: 100vh;
  overflow: hidden;
  box-sizing: border-box;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding: 15px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  height: 60px;
  box-sizing: border-box;
}

.page-header h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.search-section {
  margin-bottom: 10px;
  padding: 15px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  height: 60px;
  box-sizing: border-box;
}

.kb-list-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 0;
  height: calc(100vh - 20px - 70px - 70px - 90px);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  overflow: hidden;
}

.kb-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 15px;
  padding: 15px;
  flex: 1;
  overflow-y: auto;
}

.kb-card {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  min-height: 70px;
  max-height: 100px;
  display: flex;
  flex-direction: column;
}

.kb-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  transform: translateY(-2px);
}

.create-card {
  border: 2px dashed #d9d9d9;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  min-height: 70px;
  max-height: 100px;
}

.create-card:hover {
  border-color: #409eff;
  color: #409eff;
}

.create-icon {
  font-size: 24px;
  margin-bottom: 5px;
}

.create-text {
  font-size: 14px;
  font-weight: 500;
}

.kb-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
  position: relative;
}

.kb-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.kb-actions {
  position: absolute;
  top: 0;
  right: 0;
  z-index: 10;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.action-btn {
  padding: 4px;
  color: #909399;
  border: none;
  background: transparent;
}

.action-btn:hover {
  color: #409eff;
  background: rgba(64, 158, 255, 0.1);
  border-radius: 4px;
}

/* 下拉菜单样式 */
:deep(.kb-actions-dropdown) {
  margin-top: 5px;
}

:deep(.kb-actions-dropdown .el-dropdown-menu) {
  min-width: 120px;
  padding: 5px 0;
}

:deep(.kb-actions-dropdown .el-dropdown-menu__item) {
  padding: 8px 16px;
  font-size: 14px;
}

:deep(.kb-actions-dropdown .el-dropdown-menu__item:hover) {
  background-color: #f5f7fa;
  color: #409eff;
}

.kb-description {
  color: #606266;
  margin-bottom: 10px;
  flex: 1;
  line-height: 1.4;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.kb-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  font-size: 12px;
}

.kb-tags {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.kb-tag {
  margin: 0;
}

.kb-time {
  color: #909399;
  font-size: 12px;
}

.pagination-section {
  padding: 15px 20px;
  text-align: right;
  background: white;
  border-top: 1px solid #ebeef5;
  height: 70px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  position: relative;
  z-index: 10;
}

/* 知识库详情对话框样式 */
.kb-detail-dialog {
  :deep(.el-dialog__body) {
    padding: 0;
  }
}

.kb-detail-content {
  display: flex;
  height: 600px;
}

.kb-detail-left {
  flex: 1;
  padding: 20px;
}

.kb-tabs {
  height: 100%;
}

.tab-content {
  padding: 20px 0;
}

.document-actions {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.document-list {
  min-height: 200px;
}

.test-config {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.chat-container {
  height: 400px;
  display: flex;
  flex-direction: column;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
}

.chat-messages {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  background: #fafafa;
}

.chat-message {
  margin-bottom: 15px;
  padding: 10px 15px;
  border-radius: 8px;
  max-width: 80%;
}

.chat-message.user {
  background: #409eff;
  color: white;
  margin-left: auto;
}

.chat-message.ai {
  background: white;
  border: 1px solid #e4e7ed;
}

.message-content {
  margin-bottom: 5px;
  line-height: 1.5;
}

.message-time {
  font-size: 12px;
  opacity: 0.7;
}

.chat-input {
  padding: 15px;
  background: white;
  border-top: 1px solid #e4e7ed;
}

/* 上传对话框样式 */
.upload-demo {
  text-align: center;
}

/* 分页组件样式优化 */
:deep(.el-pagination) {
  justify-content: flex-end;
  width: 100%;
  height: 100%;
  align-items: center;
}

:deep(.el-pagination .el-pagination__total) {
  margin-right: 16px;
  white-space: nowrap;
}

:deep(.el-pagination .el-pagination__sizes) {
  margin-right: 16px;
  white-space: nowrap;
}

:deep(.el-pagination .el-pagination__jump) {
  margin-left: 16px;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 4px;
  height: 100%;
  min-width: 160px;
  padding: 0 8px;
}

:deep(.el-pagination .el-pagination__jump .el-input__inner) {
  width: 35px;
  text-align: center;
  margin: 0 6px;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto) {
  margin: 0 6px;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__classifier) {
  margin: 0 6px;
}

/* 分页中文文本样式 */
:deep(.el-pagination .el-pagination__total) {
  color: #606266;
}

:deep(.el-pagination .el-pagination__total::before) {
  content: "总条数 ";
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner) {
  color: #606266;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner::after) {
  content: "条/页";
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto) {
  color: #606266;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto::before) {
  content: "跳转到";
}

:deep(.el-pagination .el-pagination__jump .el-pagination__classifier) {
  color: #606266;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__classifier::before) {
  content: "页";
}

/* 隐藏原始的英文文本 */
:deep(.el-pagination .el-pagination__total) {
  font-size: 0;
}

:deep(.el-pagination .el-pagination__total::before) {
  font-size: 14px;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner) {
  font-size: 0;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner::after) {
  font-size: 14px;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto) {
  font-size: 0;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto::before) {
  font-size: 14px;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__classifier) {
  font-size: 0;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__classifier::before) {
  font-size: 14px;
}

/* 确保跳转区域有足够空间显示"页"字 */
:deep(.el-pagination .el-pagination__jump) {
  overflow: visible;
  position: relative;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__classifier::before) {
  content: "页";
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
  display: inline-block;
  min-width: 16px;
  text-align: center;
}
</style> 