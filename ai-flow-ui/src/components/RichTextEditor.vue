<template>
  <div class="rich-text-editor">
    <div class="editor-toolbar">
      <el-button-group>
        <el-button size="small" @click="toggleBold" :type="isBold ? 'primary' : ''">
          <el-icon><EditPen /></el-icon>
        </el-button>
        <el-button size="small" @click="toggleItalic" :type="isItalic ? 'primary' : ''">
          <el-icon><EditPen /></el-icon>
        </el-button>
        <el-button size="small" @click="toggleUnderline" :type="isUnderline ? 'primary' : ''">
          <el-icon><EditPen /></el-icon>
        </el-button>
      </el-button-group>
      <el-button-group>
        <el-button size="small" @click="insertHeading(1)">H1</el-button>
        <el-button size="small" @click="insertHeading(2)">H2</el-button>
        <el-button size="small" @click="insertHeading(3)">H3</el-button>
      </el-button-group>
      <el-button-group>
        <el-button size="small" @click="insertList('ul')">
          <el-icon><List /></el-icon>
        </el-button>
        <el-button size="small" @click="insertList('ol')">
          <el-icon><List /></el-icon>
        </el-button>
      </el-button-group>
      <el-button-group>
        <el-button size="small" @click="insertLink">
          <el-icon><Link /></el-icon>
        </el-button>
        <el-button size="small" @click="insertImage">
          <el-icon><Picture /></el-icon>
        </el-button>
      </el-button-group>
    </div>
    <div class="editor-content">
      <div
        ref="editorRef"
        class="editor-body"
        contenteditable="true"
        @input="handleInput"
        @paste="handlePaste"
        @keydown="handleKeydown"
        v-html="content"
      ></div>
    </div>
    <div class="editor-footer">
      <span class="word-count">{{ wordCount }} 字</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  EditPen,
  List,
  Link,
  Picture
} from '@element-plus/icons-vue'

interface Props {
  modelValue: string
  placeholder?: string
  height?: string
}

interface Emits {
  (e: 'update:modelValue', value: string): void
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '请输入内容...',
  height: '300px'
})

const emit = defineEmits<Emits>()

const editorRef = ref<HTMLElement>()
const content = ref(props.modelValue)
const isBold = ref(false)
const isItalic = ref(false)
const isUnderline = ref(false)

const wordCount = computed(() => {
  const text = content.value.replace(/<[^>]*>/g, '').trim()
  return text.length
})

watch(() => props.modelValue, (newVal) => {
  if (newVal !== content.value) {
    content.value = newVal
  }
})

watch(content, (newVal) => {
  emit('update:modelValue', newVal)
})

const handleInput = () => {
  if (editorRef.value) {
    content.value = editorRef.value.innerHTML
  }
}

const handlePaste = (e: ClipboardEvent) => {
  e.preventDefault()
  const text = e.clipboardData?.getData('text/plain') || ''
  document.execCommand('insertText', false, text)
}

const handleKeydown = (e: KeyboardEvent) => {
  if (e.ctrlKey || e.metaKey) {
    switch (e.key) {
      case 'b':
        e.preventDefault()
        toggleBold()
        break
      case 'i':
        e.preventDefault()
        toggleItalic()
        break
      case 'u':
        e.preventDefault()
        toggleUnderline()
        break
    }
  }
}

const toggleBold = () => {
  document.execCommand('bold', false)
  isBold.value = !isBold.value
}

const toggleItalic = () => {
  document.execCommand('italic', false)
  isItalic.value = !isItalic.value
}

const toggleUnderline = () => {
  document.execCommand('underline', false)
  isUnderline.value = !isUnderline.value
}

const insertHeading = (level: number) => {
  const tag = `h${level}`
  document.execCommand('formatBlock', false, `<${tag}>`)
}

const insertList = (type: 'ul' | 'ol') => {
  document.execCommand('insertUnorderedList', false)
}

const insertLink = () => {
  const url = prompt('请输入链接地址:')
  if (url) {
    document.execCommand('createLink', false, url)
  }
}

const insertImage = () => {
  const url = prompt('请输入图片地址:')
  if (url) {
    document.execCommand('insertImage', false, url)
  }
}

const focus = () => {
  editorRef.value?.focus()
}

const getContent = () => {
  return content.value
}

const setContent = (html: string) => {
  content.value = html
}

defineExpose({
  focus,
  getContent,
  setContent
})
</script>

<style scoped>
.rich-text-editor {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.editor-toolbar {
  padding: 8px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.editor-content {
  position: relative;
}

.editor-body {
  min-height: v-bind(height);
  padding: 12px;
  outline: none;
  line-height: 1.6;
  overflow-y: auto;
}

.editor-body:empty::before {
  content: attr(placeholder);
  color: #c0c4cc;
  pointer-events: none;
}

.editor-footer {
  padding: 8px 12px;
  background-color: #f5f7fa;
  border-top: 1px solid #dcdfe6;
  display: flex;
  justify-content: flex-end;
}

.word-count {
  font-size: 12px;
  color: #909399;
}

.editor-body h1,
.editor-body h2,
.editor-body h3 {
  margin: 16px 0 8px 0;
  font-weight: 600;
}

.editor-body h1 {
  font-size: 24px;
}

.editor-body h2 {
  font-size: 20px;
}

.editor-body h3 {
  font-size: 18px;
}

.editor-body p {
  margin: 8px 0;
}

.editor-body ul,
.editor-body ol {
  margin: 8px 0;
  padding-left: 24px;
}

.editor-body a {
  color: #409eff;
  text-decoration: none;
}

.editor-body a:hover {
  text-decoration: underline;
}

.editor-body img {
  max-width: 100%;
  height: auto;
}
</style> 