<template>
  <div class="model-config-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>模型配置</h2>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchForm.modelName"
            placeholder="请输入模型名称"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="searchForm.modelType"
            placeholder="请选择模型类型"
            clearable
            style="width: 100%"
          >
            <el-option label="文本生成" value="text" />
            <el-option label="图像生成" value="image" />
            <el-option label="语音识别" value="speech" />
            <el-option label="多模态" value="multimodal" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="searchForm.baseModel"
            placeholder="请选择基础模型"
            clearable
            style="width: 100%"
          >
            <el-option label="GPT-4" value="gpt-4" />
            <el-option label="GPT-3.5" value="gpt-3.5" />
            <el-option label="Claude" value="claude" />
            <el-option label="Gemini" value="gemini" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="showAddModelDialog">
            <el-icon><Plus /></el-icon>
            新增模型
          </el-button>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 模型列表 -->
    <div class="model-list-section">
      <el-table
        :data="modelList"
        stripe
        style="width: 100%"
        v-loading="loading"
        height="calc(100% - 60px)"
      >
        <el-table-column prop="modelName" label="模型名称" min-width="150">
          <template #default="{ row }">
            <el-button
              type="text"
              class="model-name-btn"
              @click="handleModelClick(row)"
            >
              {{ row.modelName }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="baseModel" label="基础模型" width="120" />
        <el-table-column prop="modelType" label="模型类型" width="120" />
        <el-table-column prop="apiEndpoint" label="API地址" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
              {{ row.status === 'active' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="success"
              size="small"
              @click="handleTest(row)"
            >
              测试
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

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
          :popper-class="'pagination-popper'"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 模型编辑弹出框 -->
    <ModelEditDialog
      v-model:visible="modelEditVisible"
      :model-data="currentModel"
      @success="handleEditSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import ModelEditDialog from './components/ModelEditDialog.vue'

// 搜索表单
const searchForm = reactive({
  modelName: '',
  modelType: '',
  baseModel: ''
})

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 模型列表
const modelList = ref([])
const loading = ref(false)
const modelEditVisible = ref(false)
const currentModel = ref({})

// 获取模型列表
const getModelList = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟数据
    modelList.value = [
      {
        id: 1,
        modelName: 'GPT-4文本生成模型',
        baseModel: 'GPT-4',
        modelType: '文本生成',
        apiEndpoint: 'https://api.openai.com/v1/chat/completions',
        status: 'active',
        createTime: '2024-01-15 10:30:00'
      },
      {
        id: 2,
        modelName: 'Claude图像分析模型',
        baseModel: 'Claude',
        modelType: '多模态',
        apiEndpoint: 'https://api.anthropic.com/v1/messages',
        status: 'active',
        createTime: '2024-01-14 15:20:00'
      },
      {
        id: 3,
        modelName: 'Gemini语音识别模型',
        baseModel: 'Gemini',
        modelType: '语音识别',
        apiEndpoint: 'https://generativelanguage.googleapis.com/v1beta/models',
        status: 'inactive',
        createTime: '2024-01-13 09:15:00'
      }
    ]
    pagination.total = modelList.value.length
  } catch (error) {
    ElMessage.error('获取模型列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  getModelList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    modelName: '',
    modelType: '',
    baseModel: ''
  })
  pagination.currentPage = 1
  getModelList()
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  getModelList()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.currentPage = page
  getModelList()
}

// 点击模型名称
const handleModelClick = (row: any) => {
  currentModel.value = { ...row }
  modelEditVisible.value = true
}

// 编辑模型
const handleEdit = (row: any) => {
  currentModel.value = { ...row }
  modelEditVisible.value = true
}

// 测试模型
const handleTest = async (row: any) => {
  try {
    ElMessage.info('正在测试模型接口...')
    // 这里可以调用测试API
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('模型接口测试成功')
  } catch (error) {
    ElMessage.error('模型接口测试失败')
  }
}

// 删除模型
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除模型 "${row.modelName}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 这里调用删除API
    ElMessage.success('删除成功')
    getModelList()
  } catch (error) {
    // 用户取消删除
  }
}

// 显示新增模型对话框
const showAddModelDialog = () => {
  currentModel.value = {}
  modelEditVisible.value = true
}

// 编辑成功回调
const handleEditSuccess = () => {
  modelEditVisible.value = false
  getModelList()
  ElMessage.success('保存成功')
}

// 页面加载时获取数据
onMounted(() => {
  getModelList()
})
</script>

<style scoped>
.model-config-container {
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

.model-list-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 0;
  height: calc(100vh - 20px - 70px - 70px - 80px);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  overflow: hidden;
}

/* 表格容器样式 */
:deep(.el-table) {
  border-radius: 0;
  flex: 1;
}

:deep(.el-table__body-wrapper) {
  overflow-y: auto;
  flex: 1;
}

:deep(.el-table__footer-wrapper) {
  overflow: visible;
}

:deep(.el-table__header-wrapper) {
  overflow: visible;
}

.model-name-btn {
  color: #409eff;
  font-weight: 500;
  text-decoration: none;
}

.model-name-btn:hover {
  color: #66b1ff;
  text-decoration: underline;
}

.pagination-section {
  padding: 15px 20px;
  text-align: right;
  background: white;
  border-top: 1px solid #ebeef5;
  margin-top: 0;
  margin-left: -10px;
  height: 60px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  flex-shrink: 0;
  box-sizing: border-box;
  border-radius: 0 0 8px 8px;
  position: relative;
  z-index: 1;
}

:deep(.el-table) {
  border-radius: 0;
}

:deep(.el-table th) {
  background-color: #fafafa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: #fafafa;
}

/* 分页组件样式优化 */
:deep(.el-pagination) {
  justify-content: flex-end;
  width: 100%;
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
}

:deep(.el-pagination .el-pagination__jump .el-input__inner) {
  width: 40px;
  text-align: center;
  margin: 0 4px;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto) {
  margin: 0 4px;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__classifier) {
  margin: 0 4px;
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

/* 确保页面没有滚动条 */
html, body {
  overflow: hidden;
}

/* 确保分页组件完全可见 */
:deep(.el-table__body-wrapper) {
  overflow-y: auto;
  flex: 1;
}

/* 分页组件整体布局优化 */
:deep(.el-pagination) {
  justify-content: flex-end;
  width: 100%;
  gap: 8px;
}

:deep(.el-pagination > *:not(:last-child)) {
  margin-right: 8px;
}

/* 跳转区域特殊处理 */
:deep(.el-pagination .el-pagination__jump) {
  margin-left: 16px;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 150px;
  padding: 0 6px;
}

:deep(.el-pagination .el-pagination__jump .el-input__inner) {
  width: 30px;
  text-align: center;
  margin: 0 10px;
  padding: 0 4px;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto) {
  margin: 0 10px;
  padding: 0 4px;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__classifier) {
  margin: 0 10px;
  padding: 0 4px;
}

/* 确保"页"字有足够空间 */
:deep(.el-pagination .el-pagination__jump .el-pagination__classifier) {
  min-width: 18px;
  text-align: center;
  font-weight: 500;
}
</style> 