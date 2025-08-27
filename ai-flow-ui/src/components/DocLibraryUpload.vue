<template>
  <div class="doc-library-upload">
    <div class="upload-header">
      <h4>文档库上传</h4>
      <p class="upload-desc">选择包含文档的文件夹进行批量上传</p>
    </div>
    
    <div class="upload-area">
      <el-upload
        ref="uploadRef"
        class="folder-upload"
        drag
        :action="uploadAction"
        :auto-upload="false"
        :on-change="handleFileChange"
        :on-remove="handleFileRemove"
        :file-list="fileList"
        :before-upload="beforeUpload"
        :multiple="true"
        :accept="accept"
        :limit="100"
        :on-exceed="handleExceed"
        directory
      >
        <el-icon class="el-icon--upload"><FolderOpened /></el-icon>
        <div class="el-upload__text">
          将文件夹拖到此处，或<em>点击选择文件夹</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            {{ tipText }}
          </div>
        </template>
      </el-upload>
    </div>
    
    <div class="file-preview" v-if="fileList.length > 0">
      <div class="preview-header">
        <span class="file-count">已选择 {{ fileList.length }} 个文件</span>
        <el-button type="text" @click="clearFiles">清空</el-button>
      </div>
      
      <div class="file-list">
        <div
          v-for="(file, index) in fileList"
          :key="index"
          class="file-item"
          :class="{ 'file-error': file.status === 'error' }"
        >
          <div class="file-info">
            <el-icon class="file-icon">
              <Document v-if="isDocument(file)" />
              <Folder v-else />
            </el-icon>
            <span class="file-name">{{ file.name }}</span>
            <span class="file-size">{{ formatFileSize(file.size) }}</span>
          </div>
          <div class="file-status">
            <el-tag v-if="file.status === 'error'" type="danger" size="small">
              不支持
            </el-tag>
            <el-tag v-else-if="file.status === 'ready'" type="success" size="small">
              就绪
            </el-tag>
          </div>
        </div>
      </div>
    </div>
    
    <div class="upload-actions" v-if="fileList.length > 0">
      <el-button type="primary" @click="handleUpload" :loading="uploading" :disabled="!hasValidFiles">
        开始上传 ({{ validFileCount }} 个文件)
      </el-button>
      <el-button @click="clearFiles">取消</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  FolderOpened, 
  Document, 
  Folder 
} from '@element-plus/icons-vue'

interface Props {
  modelValue?: File[]
  accept?: string
  tipText?: string
  uploadAction?: string
  maxSize?: number // MB
}

interface Emits {
  (e: 'update:modelValue', files: File[]): void
  (e: 'upload-success', response: any): void
  (e: 'upload-error', error: any): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => [],
  accept: '.txt,.md,.doc,.docx,.xlsx,.xls,.pdf',
  tipText: '支持 markdown, doc, docx, xlsx, xls, txt, pdf 等格式文件，支持文件夹递归上传',
  uploadAction: '#',
  maxSize: 50
})

const emit = defineEmits<Emits>()

const uploadRef = ref()
const fileList = ref<any[]>([])
const uploading = ref(false)

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

const validFileCount = computed(() => {
  return fileList.value.filter(file => file.status !== 'error').length
})

const hasValidFiles = computed(() => {
  return validFileCount.value > 0
})

watch(() => props.modelValue, (newVal) => {
  if (newVal && newVal.length > 0) {
    fileList.value = newVal.map((file, index) => ({
      name: file.name,
      size: file.size,
      type: file.type,
      uid: index,
      raw: file,
      status: 'ready'
    }))
  }
}, { immediate: true })

const handleFileChange = (file: any, uploadedFileList: any[]) => {
  // 验证文件类型和大小
  const validatedFiles = uploadedFileList.map(item => {
    const isValidType = supportedTypes.includes(item.type) || 
                       supportedExtensions.some(ext => item.name.toLowerCase().endsWith(ext))
    
    const fileSizeMB = item.size / (1024 * 1024)
    const isValidSize = fileSizeMB <= props.maxSize
    
    return {
      ...item,
      status: isValidType && isValidSize ? 'ready' : 'error'
    }
  })
  
  fileList.value = validatedFiles
  const files = validatedFiles.map(item => item.raw).filter(Boolean)
  emit('update:modelValue', files)
}

const handleFileRemove = (file: any, uploadedFileList: any[]) => {
  const files = uploadedFileList.map(item => item.raw).filter(Boolean)
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
  ElMessage.warning(`最多只能上传 100 个文件`)
}

const isDocument = (file: any) => {
  return file.type && file.type !== 'inode/directory'
}

const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const handleUpload = async () => {
  if (validFileCount.value === 0) {
    ElMessage.warning('没有可上传的有效文件')
    return
  }
  
  uploading.value = true
  
  try {
    const validFiles = fileList.value.filter(file => file.status === 'ready')
    
    // 读取文件内容并调用后端接口
    const uploadPromises = validFiles.map(async (fileItem) => {
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
.doc-library-upload {
  width: 100%;
}

.upload-header {
  margin-bottom: 20px;
  text-align: center;
}

.upload-header h4 {
  margin: 0 0 8px 0;
  color: #303133;
}

.upload-desc {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.upload-area {
  margin-bottom: 20px;
}

.folder-upload {
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.folder-upload:hover {
  border-color: #409eff;
}

.file-preview {
  margin-bottom: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.preview-header {
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.file-count {
  font-size: 14px;
  color: #606266;
}

.file-list {
  max-height: 300px;
  overflow-y: auto;
  padding: 8px 0;
}

.file-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.file-item:last-child {
  border-bottom: none;
}

.file-item.file-error {
  background-color: #fef0f0;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.file-icon {
  color: #909399;
  font-size: 16px;
}

.file-name {
  flex: 1;
  color: #303133;
  font-size: 14px;
  word-break: break-all;
}

.file-size {
  color: #909399;
  font-size: 12px;
  white-space: nowrap;
}

.file-status {
  margin-left: 12px;
}

.upload-actions {
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