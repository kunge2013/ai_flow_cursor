<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑模型' : '新增模型'"
    width="800px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      class="model-edit-form"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="模型名称" prop="modelName">
            <el-input
              v-model="formData.modelName"
              placeholder="请输入模型名称"
              clearable
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="基础模型" prop="baseModel">
            <el-select
              v-model="formData.baseModel"
              placeholder="请选择基础模型"
              style="width: 100%"
              clearable
            >
              <el-option label="GPT-4" value="gpt-4" />
              <el-option label="GPT-3.5" value="gpt-3.5" />
              <el-option label="Claude" value="claude" />
              <el-option label="Gemini" value="gemini" />
              <el-option label="文心一言" value="wenxin" />
              <el-option label="通义千问" value="qwen" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="模型类型" prop="modelType">
            <el-select
              v-model="formData.modelType"
              placeholder="请选择模型类型"
              style="width: 100%"
              clearable
            >
              <el-option label="文本生成" value="text" />
              <el-option label="图像生成" value="image" />
              <el-option label="语音识别" value="speech" />
              <el-option label="多模态" value="multimodal" />
              <el-option label="代码生成" value="code" />
              <el-option label="翻译" value="translation" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio label="active">启用</el-radio>
              <el-radio label="inactive">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="API地址" prop="apiEndpoint">
        <el-input
          v-model="formData.apiEndpoint"
          placeholder="请输入API地址"
          clearable
        />
      </el-form-item>

      <el-form-item label="API密钥" prop="apiKey">
        <el-input
          v-model="formData.apiKey"
          type="password"
          placeholder="请输入API密钥"
          clearable
          show-password
        />
      </el-form-item>

      <el-form-item label="模型描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入模型描述"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="最大Token数" prop="maxTokens">
            <el-input-number
              v-model="formData.maxTokens"
              :min="1"
              :max="100000"
              style="width: 100%"
              placeholder="请输入最大Token数"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="温度" prop="temperature">
            <el-slider
              v-model="formData.temperature"
              :min="0"
              :max="2"
              :step="0.1"
              show-input
              :show-input-controls="false"
              input-size="small"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="高级配置">
        <el-collapse v-model="activeCollapse">
          <el-collapse-item title="高级参数配置" name="advanced">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="Top P" prop="topP">
                  <el-slider
                    v-model="formData.topP"
                    :min="0"
                    :max="1"
                    :step="0.01"
                    show-input
                    :show-input-controls="false"
                    input-size="small"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="频率惩罚" prop="frequencyPenalty">
                  <el-slider
                    v-model="formData.frequencyPenalty"
                    :min="-2"
                    :max="2"
                    :step="0.1"
                    show-input
                    :show-input-controls="false"
                    input-size="small"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="存在惩罚" prop="presencePenalty">
                  <el-slider
                    v-model="formData.presencePenalty"
                    :min="-2"
                    :max="2"
                    :step="0.1"
                    show-input
                    :show-input-controls="false"
                    input-size="small"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="停止词" prop="stopWords">
                  <el-input
                    v-model="formData.stopWords"
                    placeholder="请输入停止词，用逗号分隔"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
        </el-collapse>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="success" @click="handleTest" :loading="testing">
          {{ testing ? '测试中...' : '测试' }}
        </el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">
          {{ saving ? '保存中...' : '保存' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { modelApi } from '../api/modelApi'

// 定义组件属性
interface Props {
  visible: boolean
  modelData: any
}

// 定义组件事件
interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 表单引用
const formRef = ref<FormInstance>()

// 对话框可见性
const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

// 是否为编辑模式
const isEdit = computed(() => Object.keys(props.modelData).length > 0)

// 表单数据
const formData = reactive({
  modelName: '',
  baseModel: '',
  modelType: '',
  status: 'active',
  apiEndpoint: '',
  apiKey: '',
  description: '',
  maxTokens: 4096,
  temperature: 0.7,
  topP: 1,
  frequencyPenalty: 0,
  presencePenalty: 0,
  stopWords: ''
})

// 表单验证规则
const formRules: FormRules = {
  modelName: [
    { required: true, message: '请输入模型名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  baseModel: [
    { required: true, message: '请选择基础模型', trigger: 'change' }
  ],
  modelType: [
    { required: true, message: '请选择模型类型', trigger: 'change' }
  ],
  apiEndpoint: [
    { required: true, message: '请输入API地址', trigger: 'blur' },
    { type: 'url', message: '请输入正确的URL格式', trigger: 'blur' }
  ],
  apiKey: [
    { required: true, message: '请输入API密钥', trigger: 'blur' }
  ],
  maxTokens: [
    { required: true, message: '请输入最大Token数', trigger: 'blur' },
    { type: 'number', min: 1, message: 'Token数必须大于0', trigger: 'blur' }
  ]
}

// 高级配置折叠面板
const activeCollapse = ref(['advanced'])

// 加载状态
const saving = ref(false)
const testing = ref(false)

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    modelName: '',
    baseModel: '',
    modelType: '',
    status: 'active',
    apiEndpoint: '',
    apiKey: '',
    description: '',
    maxTokens: 4096,
    temperature: 0.7,
    topP: 1,
    frequencyPenalty: 0,
    presencePenalty: 0,
    stopWords: ''
  })
  
  // 清除验证错误
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

// 监听模型数据变化，填充表单
watch(
  () => props.modelData,
  (newData) => {
    if (newData && Object.keys(newData).length > 0) {
      // 编辑模式，填充现有数据
      Object.assign(formData, {
        modelName: newData.modelName || '',
        baseModel: newData.baseModel || '',
        modelType: newData.modelType || '',
        status: newData.status || 'active',
        apiEndpoint: newData.apiEndpoint || '',
        apiKey: newData.apiKey || '',
        description: newData.description || '',
        maxTokens: newData.maxTokens || 4096,
        temperature: newData.temperature || 0.7,
        topP: newData.topP || 1,
        frequencyPenalty: newData.frequencyPenalty || 0,
        presencePenalty: newData.presencePenalty || 0,
        stopWords: newData.stopWords || ''
      })
    } else {
      // 新增模式，重置表单
      resetForm()
    }
  },
  { immediate: true, deep: true }
)

// 保存模型
const handleSave = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    saving.value = true
    
    if (isEdit.value) {
      // 更新模型
      await modelApi.updateModel(props.modelData.id, formData)
    } else {
      // 创建模型
      await modelApi.createModel(formData)
    }
    
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    emit('success')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 测试模型
const handleTest = async () => {
  if (!formRef.value) return
  
  try {
    // 只验证必要的字段
    const isValid = await formRef.value.validateField(['apiEndpoint', 'apiKey'])
    if (!isValid) return
    
    testing.value = true
    
    ElMessage.info('正在测试模型接口...')
    
    const testData = {
      modelId: props.modelData.id,
      apiEndpoint: formData.apiEndpoint,
      apiKey: formData.apiKey,
      maxTokens: formData.maxTokens || 100,
      temperature: formData.temperature || 0.7
    }
    
    const result = await modelApi.testModel(testData)
    ElMessage.success(result.data || '模型接口测试成功！')
  } catch (error) {
    console.error('测试失败:', error)
    ElMessage.error('模型接口测试失败')
  } finally {
    testing.value = false
  }
}

// 取消操作
const handleCancel = () => {
  ElMessageBox.confirm(
    '确定要取消编辑吗？未保存的内容将丢失。',
    '确认取消',
    {
      confirmButtonText: '确定',
      cancelButtonText: '继续编辑',
      type: 'warning'
    }
  ).then(() => {
    handleClose()
  }).catch(() => {
    // 用户选择继续编辑
  })
}

// 关闭对话框
const handleClose = () => {
  resetForm()
  emit('update:visible', false)
}
</script>

<style scoped>
.model-edit-form {
  max-height: 60vh;
  overflow-y: auto;
}

.dialog-footer {
  text-align: right;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-collapse-item__header) {
  font-weight: 500;
  color: #409eff;
}

:deep(.el-slider__input) {
  width: 80px;
}

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-textarea__inner) {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
}

/* 自定义滚动条样式 */
.model-edit-form::-webkit-scrollbar {
  width: 6px;
}

.model-edit-form::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.model-edit-form::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.model-edit-form::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style> 