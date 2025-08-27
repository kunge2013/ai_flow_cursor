<template>
  <div class="file-upload">
    <!-- 上传方式选择 -->
    <div class="upload-mode-selector" v-if="props.useDirectUpload !== undefined">
      <el-radio-group v-model="uploadMode" size="small">
        <el-radio-button label="content">文档内容上传</el-radio-button>
        <el-radio-button label="file">文件直接上传</el-radio-button>
      </el-radio-group>
      <div class="mode-description">
        <span v-if="uploadMode === 'content'">将文件内容读取后上传到后端进行向量化</span>
        <span v-else>直接将文件上传到后端，由后端处理内容提取和向量化</span>
      </div>
    </div>
    
    <el-upload
      ref="uploadRef"
      class="upload-demo"
      drag
      :action="uploadAction"
      :auto-upload="false"
      :on-change="handleFileChange"
      :on-remove="handleFileRemove"
      :file-list="fileList"
      :before-upload="beforeUpload"
      :multiple="multiple"
      :accept="accept"
      :limit="limit"
      :on-exceed="handleExceed"
    >
      <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
      <div class="el-upload__text">
        将文件拖到此处，或<em>点击上传</em>
      </div>
      <template #tip>
        <div class="el-upload__tip">
          {{ tipText }}
        </div>
      </template>
    </el-upload>
    
    <div class="upload-actions" v-if="fileList.length > 0">
      <el-button type="primary" @click="handleUpload" :loading="uploading" :disabled="fileList.length === 0">
        开始上传
      </el-button>
      <el-button @click="clearFiles">清空文件</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'

interface Props {
  modelValue?: File[]
  multiple?: boolean
  accept?: string
  limit?: number
  tipText?: string
  uploadAction?: string
  maxSize?: number // MB
  useDirectUpload?: boolean // 是否使用直接文件上传接口
}

interface Emits {
  (e: 'update:modelValue', files: File[]): void
  (e: 'upload-success', response: any): void
  (e: 'upload-error', error: any): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => [],
  multiple: true,
  accept: '.txt,.md,.doc,.docx,.xlsx,.xls,.pdf',
  limit: 10,
  tipText: '支持 markdown, doc, docx, xlsx, xls, txt, pdf 等格式文件',
  uploadAction: '#',
  maxSize: 50,
  useDirectUpload: false
})

const emit = defineEmits<Emits>()

const uploadRef = ref()
const fileList = ref<any[]>([])
const uploading = ref(false)
const uploadMode = ref('content') // 默认使用文档内容上传模式

const supportedTypes = [
  'text/plain',
  'text/markdown',
  'application/msword',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  'application/vnd.ms-excel',
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  'application/pdf'
]

const supportedExtensions = [
  '.txt', '.md', '.doc', '.docx', '.xlsx', '.xls', '.pdf'
]

watch(() => props.modelValue, (newVal) => {
  if (newVal && newVal.length > 0) {
    fileList.value = newVal.map((file, index) => ({
      name: file.name,
      size: file.size,
      type: file.type,
      uid: index,
      raw: file
    }))
  }
}, { immediate: true })

const handleFileChange = (file: any, fileList: any[]) => {
  const files = fileList.map(item => item.raw).filter(Boolean)
  emit('update:modelValue', files)
}

const handleFileRemove = (file: any, fileList: any[]) => {
  const files = fileList.map(item => item.raw).filter(Boolean)
  emit('update:modelValue', files)
}

const beforeUpload = (file: File) => {
  // 检查文件类型
  const isValidType = supportedTypes.includes(file.type) || 
                     supportedExtensions.some(ext => file.name.toLowerCase().endsWith(ext))
  
  if (!isValidType) {
    ElMessage.error(`不支持的文件类型: ${file.name}`)
    return false
  }
  
  // 检查文件大小
  const fileSizeMB = file.size / (1024 * 1024)
  if (fileSizeMB > props.maxSize) {
    ElMessage.error(`文件大小不能超过 ${props.maxSize}MB`)
    return false
  }
  
  return true
}

const handleExceed = (files: File[]) => {
  ElMessage.warning(`最多只能上传 ${props.limit} 个文件`)
}

const handleUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请先选择文件')
    return
  }
  
  uploading.value = true
  
  try {
    if (uploadMode.value === 'file') {
      // 文件直接上传模式
      const uploadPromises = fileList.value.map(async (fileItem) => {
        const file = fileItem.raw
        if (!file) return
        
        try {
          // 触发文件上传事件，让父组件处理
          emit('upload-success', { 
            file: file,
            uploadMode: 'file',
            success: true 
          })
          
          return { file, success: true }
        } catch (error) {
          console.error(`文件 ${file.name} 处理失败:`, error)
          emit('upload-error', { file, error })
          return { file, success: false, error }
        }
      })
      
      const results = await Promise.all(uploadPromises)
      const successCount = results.filter(r => r?.success).length
      const failCount = results.length - successCount
      
      if (successCount > 0) {
        ElMessage.success(`成功处理 ${successCount} 个文件，正在上传到后端进行向量化...`)
      }
      if (failCount > 0) {
        ElMessage.warning(`${failCount} 个文件处理失败`)
      }
    } else {
      // 文档内容上传模式（原有逻辑）
      const uploadPromises = fileList.value.map(async (fileItem) => {
        const file = fileItem.raw
        if (!file) return
        
        try {
          // 读取文件内容
          const content = await readFileContent(file)
          
          // 构建文档数据
          const documentData = {
            title: file.name,
            content: content,
            type: file.name.split('.').pop() || 'text',
            size: file.size
          }
          
          // 触发上传事件，让父组件处理具体的API调用
          emit('upload-success', { 
            file: file,
            documentData: documentData,
            uploadMode: 'content',
            success: true 
          })
          
          return { file, success: true }
        } catch (error) {
          console.error(`文件 ${file.name} 处理失败:`, error)
          emit('upload-error', { file, error })
          return { file, success: false, error }
        }
      })
      
      const results = await Promise.all(uploadPromises)
      const successCount = results.filter(r => r?.success).length
      const failCount = results.length - successCount
      
      if (successCount > 0) {
        ElMessage.success(`成功处理 ${successCount} 个文件，正在上传到后端进行向量化...`)
      }
      if (failCount > 0) {
        ElMessage.warning(`${failCount} 个文件处理失败`)
      }
    }
    
  } catch (error) {
    emit('upload-error', error)
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}

// 读取文件内容的辅助函数
const readFileContent = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    
    // 根据文件类型选择读取方式
    if (file.type === 'application/pdf' || 
        file.name.toLowerCase().endsWith('.pdf')) {
      // PDF文件暂时返回文件名，因为需要特殊处理
      resolve(`PDF文件: ${file.name}\n\n注意：PDF文件内容提取功能需要后端支持`)
    } else if (file.type.includes('word') || 
               file.type.includes('excel') ||
               file.name.toLowerCase().match(/\.(doc|docx|xls|xlsx)$/)) {
      // Office文档暂时返回文件名
      resolve(`Office文档: ${file.name}\n\n注意：Office文档内容提取功能需要后端支持`)
    } else {
      // 文本文件直接读取内容
      reader.onload = (e) => {
        resolve(e.target?.result as string)
      }
      reader.onerror = reject
      reader.readAsText(file)
    }
  })
}

const clearFiles = () => {
  fileList.value = []
  emit('update:modelValue', [])
}

const getFiles = () => {
  return fileList.value.map(item => item.raw).filter(Boolean)
}

defineExpose({
  getFiles,
  clearFiles,
  handleUpload
})
</script>

<style scoped>
.file-upload {
  width: 100%;
}

.upload-mode-selector {
  margin-bottom: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.mode-description {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

.upload-actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
  justify-content: center;
}

.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}

.el-upload__text {
  margin: 10px 0;
}

.el-upload__text em {
  color: #409eff;
  font-style: normal;
}
</style> 