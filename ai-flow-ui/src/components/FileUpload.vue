<template>
  <div class="file-upload">
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
    // 这里可以调用实际的上传接口
    // 模拟上传过程
    for (let i = 0; i < fileList.value.length; i++) {
      const file = fileList.value[i]
      await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟上传延迟
      
      // 模拟上传进度
      ElMessage.success(`文件 ${file.name} 上传成功`)
    }
    
    emit('upload-success', { files: fileList.value })
    ElMessage.success('所有文件上传完成')
  } catch (error) {
    emit('upload-error', error)
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
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