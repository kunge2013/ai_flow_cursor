<template>
  <div class="app-editor">
    <!-- 顶部标题栏 -->
    <div class="editor-header">
      <div class="header-left">
        <div class="app-icon-wrapper">
          <div class="app-icon">212</div>
          <el-icon class="edit-icon"><EditPen /></el-icon>
        </div>
        <h2 class="editor-title">应用编排</h2>
        <el-icon class="help-icon"><QuestionFilled /></el-icon>
      </div>
      <div class="header-actions">
        <el-button 
          type="primary" 
          class="save-btn" 
          @click="onSubmit"
          :loading="loading"
          :disabled="loading"
        >
          {{ loading ? '保存中...' : '保存' }}
        </el-button>
        <el-button 
          class="reset-btn" 
          @click="resetChat"
          :disabled="loading"
          title="重置聊天记录"
        >
          重置聊天
        </el-button>
        <el-button class="close-btn" @click="onCancel" :disabled="loading">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="editor-content">
      <!-- 左侧：编排配置区域 -->
      <div class="left-panel">
        <div class="panel-header">
          <h3>编排</h3>
        </div>
        <div class="panel-content">
          <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
            <!-- 开场白 -->
            <div class="form-section">
              <label class="section-label">开场白</label>
              <div class="rich-editor-wrapper">
                <el-input 
                  v-model="form.openingRemark" 
                  type="textarea" 
                  :rows="6" 
                  placeholder="请输入开场白内容..." 
                  class="rich-textarea"
                />
                <div class="rich-toolbar">
                  <el-button size="small" class="toolbar-btn"><strong>B</strong></el-button>
                  <el-button size="small" class="toolbar-btn"><em>I</em></el-button>
                  <el-button size="small" class="toolbar-btn"><s>S</s></el-button>
                  <el-button size="small" class="toolbar-btn">
                    <el-icon><Link /></el-icon>
                  </el-button>
                  <el-button size="small" class="toolbar-btn">
                    <el-icon><List /></el-icon>
                  </el-button>
                  <el-button size="small" class="toolbar-btn">
                    <el-icon><Grid /></el-icon>
                  </el-button>
                  <el-button size="small" class="toolbar-btn">
                    <el-icon><Check /></el-icon>
                  </el-button>
                  <el-button size="small" class="toolbar-btn">
                    <el-icon><Document /></el-icon>
                  </el-button>
                  <el-button size="small" class="toolbar-btn">
                    <el-icon><ChatDotRound /></el-icon>
                  </el-button>
                  <el-button size="small" class="toolbar-btn">
                    <el-icon><Picture /></el-icon>
                  </el-button>
                  <el-button size="small" class="toolbar-btn">
                    <el-icon><VideoPlay /></el-icon>
                  </el-button>
                  <el-button size="small" class="toolbar-btn">
                    <el-icon><MoreFilled /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 预设问题 -->
            <div class="form-section">
              <label class="section-label">预设问题</label>
              <div class="preset-questions">
                <el-input 
                  v-model="form.presetQuestion" 
                  placeholder="请输入预设问题" 
                  class="preset-input"
                />
                <div class="preset-actions">
                  <el-button size="small" class="add-btn">
                    <el-icon><Plus /></el-icon>
                  </el-button>
                  <el-button size="small" class="delete-btn">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 快捷指令 -->
            <div class="form-section">
              <label class="section-label">快捷指令</label>
              <p class="section-desc">快捷指令是对话输入框上方的按钮,配置完成后,用户可以快速发起预设对话</p>
              <el-button size="small" class="add-btn">
                <el-icon><Plus /></el-icon>
              </el-button>
            </div>

            <!-- AI模型 -->
            <div class="form-section">
              <label class="section-label required">AI模型</label>
              <div class="model-section">
                <el-select v-model="form.model" placeholder="请选择模型" class="model-select">
                  <el-option label="OpenAI" value="openai" />
                  <el-option label="Claude" value="claude" />
                  <el-option label="Gemini" value="gemini" />
                </el-select>
                <el-button size="small" class="config-btn">
                  <el-icon><Setting /></el-icon>
                  参数配置
                </el-button>
              </div>
            </div>

            <!-- 知识库 -->
            <div class="form-section">
              <label class="section-label">知识库</label>
              <p class="section-desc">添加知识库后,用户发送消息时,智能体能够引用文本知识中的内容回答用户问题。</p>
              <div class="knowledge-actions">
                <el-button size="small" class="config-btn">
                  <el-icon><Setting /></el-icon>
                  参数配置
                </el-button>
                <el-button size="small" class="add-btn">
                  <el-icon><Plus /></el-icon>
                  添加
                </el-button>
              </div>
            </div>

            <!-- 历史聊天记录 -->
            <div class="form-section">
              <label class="section-label">历史聊天记录</label>
              <el-input 
                v-model="form.historyCount" 
                placeholder="1" 
                class="history-input"
              />
            </div>

            <!-- 个性化设置 -->
            <div class="form-section">
              <label class="section-label">个性化设置</label>
              <div class="setting-item">
                <span class="setting-label">多会话模式:</span>
                <el-switch v-model="form.multiSession" active-text="开" inactive-text="关" />
              </div>
            </div>
          </el-form>
        </div>
      </div>

      <!-- 右侧：预览区域 -->
      <div class="right-panel">
        <div class="panel-header">
          <h3>预览</h3>
        </div>
        <div class="panel-content">
          <!-- 聊天界面预览 -->
          <div class="chat-preview">
            <!-- 聊天记录 -->
            <div class="chat-messages" ref="chatMessagesRef">
              <!-- 开场白消息 -->
              <div v-if="form.openingRemark" class="message bot-message">
                <div class="message-avatar">
                  <div class="app-icon-small">212</div>
                </div>
                <div class="message-content">
                  <div class="message-header">
                    <span class="app-name">212</span>
                    <span class="message-time">{{ getCurrentTime() }}</span>
                  </div>
                  <div class="message-text">{{ form.openingRemark }}</div>
                </div>
              </div>
              
              <!-- 动态聊天消息 -->
              <div 
                v-for="(message, index) in messages" 
                :key="index"
                :class="['message', message.type === 'user' ? 'user-message' : 'bot-message']"
              >
                <div v-if="message.type === 'bot'" class="message-avatar">
                  <div class="app-icon-small">212</div>
                </div>
                <div class="message-content">
                  <div class="message-header">
                    <span v-if="message.type === 'bot'" class="app-name">212</span>
                    <span class="message-time">{{ message.time }}</span>
                  </div>
                  <div class="message-text">{{ message.content }}</div>
                </div>
                <div v-if="message.type === 'user'" class="message-avatar">
                  <div class="user-avatar">M</div>
                </div>
              </div>
            </div>
            
            <!-- 聊天输入区域 -->
            <div class="chat-input">
              <div class="input-wrapper">
                <el-button 
                  size="small" 
                  class="clear-btn"
                  @click="clearInput"
                  :disabled="!currentMessage.trim()"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
                <el-input 
                  v-model="currentMessage"
                  placeholder="来说点什么吧... (Shift + Enter = 换行)" 
                  class="chat-input-field"
                  @keyup.enter="sendMessage"
                  @keyup.shift.enter="handleShiftEnter"
                />
                <div class="input-actions">
                  <el-button size="small" class="upload-btn">
                    <el-icon><Picture /></el-icon>
                  </el-button>
                  <el-button 
                    size="small" 
                    class="send-btn" 
                    @click="sendMessage"
                    :disabled="!currentMessage.trim()"
                  >
                    <el-icon><Position /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, onMounted, nextTick } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { 
  EditPen, 
  QuestionFilled, 
  Close, 
  Link, 
  List, 
  Grid, 
  Check, 
  Document, 
  ChatDotRound, 
  Picture, 
  VideoPlay, 
  MoreFilled,
  Plus,
  Delete,
  Setting,
  Position
} from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => Boolean(route.params.id))

const formRef = ref<FormInstance>()
const chatMessagesRef = ref<HTMLElement>()
const currentMessage = ref('')
const loading = ref(false)

const form = reactive({
  openingRemark: '',
  presetQuestion: '2121',
  model: 'openai',
  historyCount: '1',
  multiSession: true
})

const rules = reactive<FormRules>({
  openingRemark: [
    { required: true, message: '请输入开场白内容', trigger: 'blur' }
  ],
  model: [
    { required: true, message: '请选择AI模型', trigger: 'change' }
  ]
})

// 保存应用配置
async function onSubmit() {
  try {
    // 表单验证
    await formRef.value?.validate()
    
    // 设置加载状态
    loading.value = true
    
    // 构建保存数据
    const saveData = {
      openingRemark: form.openingRemark,
      presetQuestion: form.presetQuestion,
      model: form.model,
      historyCount: parseInt(form.historyCount),
      multiSession: form.multiSession
    }
    
    // 这里调用实际的API接口
    // 根据是编辑还是新建来调用不同的接口
    if (isEdit.value) {
      // 编辑模式 - 调用更新接口
      // await updateApp(route.params.id as string, saveData)
      console.log('更新应用配置:', saveData)
    } else {
      // 新建模式 - 调用创建接口
      // await createApp(saveData)
      console.log('创建应用配置:', saveData)
    }
    
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 显示成功消息
    ElMessage.success('保存成功')
    
    // 延迟跳转，让用户看到成功消息
    setTimeout(() => {
      router.back()
    }, 1500)
    
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请检查表单内容')
  } finally {
    // 重置加载状态
    loading.value = false
  }
}

// 关闭编辑器
function onCancel() {
  // 如果有未保存的更改，提示用户确认
  if (hasUnsavedChanges()) {
    ElMessageBox.confirm(
      '您有未保存的更改，确定要离开吗？',
      '确认离开',
      {
        confirmButtonText: '确定离开',
        cancelButtonText: '继续编辑',
        type: 'warning',
      }
    ).then(() => {
      router.back()
    }).catch(() => {
      // 用户选择继续编辑，不做任何操作
    })
  } else {
    router.back()
  }
}

// 检查是否有未保存的更改
function hasUnsavedChanges(): boolean {
  // 这里可以根据实际需求检查表单是否有更改
  // 目前简单判断开场白是否有内容
  return form.openingRemark.trim() !== '' || 
         form.presetQuestion !== '2121' || 
         form.model !== 'openai' || 
         form.historyCount !== '1' || 
         !form.multiSession
}

// 重置表单
function resetForm() {
  form.openingRemark = ''
  form.presetQuestion = '2121'
  form.model = 'openai'
  form.historyCount = '1'
  form.multiSession = true
  
  // 清除表单验证状态
  formRef.value?.clearValidate()
  
  ElMessage.success('表单已重置')
}

// 重置聊天记录
function resetChat() {
  messages.value = []
  currentMessage.value = ''
  ElMessage.success('聊天记录已重置')
}

// 获取当前时间
function getCurrentTime() {
  return new Date().toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit',
    second: '2-digit'
  })
}

// 发送消息
async function sendMessage() {
  if (!currentMessage.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }

  const userMessage = currentMessage.value.trim()
  
  // 添加用户消息到聊天记录
  const message = {
    type: 'user' as const,
    content: userMessage,
    time: getCurrentTime()
  }
  
  messages.value.push(message)
  currentMessage.value = ''

  // 滚动到底部
  await nextTick()
  scrollToBottom()

  // 模拟AI回复
  setTimeout(async () => {
    try {
      // 这里可以调用实际的AI接口
      const aiMessage = {
        type: 'bot' as const,
        content: `这是AI的回复：${userMessage}`,
        time: getCurrentTime()
      }
      
      messages.value.push(aiMessage)
      
      // 再次滚动到底部
      await nextTick()
      scrollToBottom()
      
    } catch (error) {
      console.error('AI回复失败:', error)
      const errorMessage = {
        type: 'bot' as const,
        content: '抱歉，AI回复失败，请稍后重试。',
        time: getCurrentTime()
      }
      messages.value.push(errorMessage)
      
      await nextTick()
      scrollToBottom()
    }
  }, 1000)
}

// 处理 Shift + Enter 换行
function handleShiftEnter(event: KeyboardEvent) {
  event.preventDefault(); // 阻止默认换行
  currentMessage.value += '\n'; // 在输入框末尾添加换行符
}

// 清除输入框内容
function clearInput() {
  currentMessage.value = '';
}

// 滚动到底部
function scrollToBottom() {
  if (chatMessagesRef.value) {
    chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
  }
}

const messages = ref<{ type: 'user' | 'bot'; content: string; time: string }[]>([])

// 组件挂载时初始化数据
onMounted(async () => {
  if (isEdit.value) {
    try {
      // 编辑模式：加载现有数据
      const id = String(route.params.id)
      // 这里应该调用API获取应用数据
      // const appData = await getApp(id)
      
      // 模拟加载数据
      console.log('加载应用ID:', id)
      
      // 如果有数据，填充到表单中
      // form.openingRemark = appData.openingRemark || ''
      // form.presetQuestion = appData.presetQuestion || '2121'
      // form.model = appData.model || 'openai'
      // form.historyCount = appData.historyCount?.toString() || '1'
      // form.multiSession = appData.multiSession ?? true
      
    } catch (error) {
      console.error('加载应用数据失败:', error)
      ElMessage.error('加载应用数据失败')
    }
  }
  
  // 添加一些初始的聊天消息用于演示
  messages.value = [
    {
      type: 'bot',
      content: '您好！我是AI助手，有什么可以帮助您的吗？',
      time: getCurrentTime()
    }
  ]
})
</script>

<style scoped>
.app-editor {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f8f9fa;
}

.editor-header {
  background: white;
  padding: 16px 24px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.app-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.app-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
}

.edit-icon {
  position: absolute;
  top: -4px;
  right: -4px;
  background: white;
  border-radius: 50%;
  padding: 2px;
  font-size: 10px;
  color: #6c757d;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.editor-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #212529;
}

.help-icon {
  color: #6c757d;
  cursor: pointer;
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.save-btn {
  background: #007bff;
  border-color: #007bff;
  border-radius: 6px;
  padding: 8px 20px;
  font-weight: 500;
}

.reset-btn {
  background: #ffc107;
  border-color: #ffc107;
  border-radius: 6px;
  padding: 8px 20px;
  font-weight: 500;
  color: #212529;
}

.reset-btn:hover {
  background: #e0a800;
  border-color: #d39e00;
}

.close-btn {
  border-radius: 6px;
  padding: 8px 12px;
}

.editor-content {
  flex: 1;
  display: flex;
  gap: 24px;
  padding: 24px;
  overflow: hidden;
}

.left-panel, .right-panel {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.left-panel {
  flex: 1;
  min-width: 450px;
}

.right-panel {
  flex: 1;
  min-width: 550px;
}

.panel-header {
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
  background: #f8f9fa;
}

.panel-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #212529;
}

.panel-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.form-section {
  margin-bottom: 24px;
}

.section-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #495057;
  margin-bottom: 8px;
}

.section-label.required::after {
  content: ' *';
  color: #dc3545;
}

.section-desc {
  font-size: 13px;
  color: #6c757d;
  margin: 8px 0 12px 0;
  line-height: 1.5;
}

.rich-editor-wrapper {
  border: 1px solid #dee2e6;
  border-radius: 8px;
  overflow: hidden;
}

.rich-textarea :deep(.el-textarea__inner) {
  border: none;
  border-radius: 0;
  padding: 16px;
  font-size: 14px;
  line-height: 1.6;
  resize: none;
}

.rich-toolbar {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-top: 1px solid #dee2e6;
  flex-wrap: wrap;
}

.toolbar-btn {
  border: 1px solid #dee2e6;
  background: white;
  color: #495057;
  padding: 6px 10px;
  font-size: 12px;
  border-radius: 4px;
  min-width: 32px;
}

.toolbar-btn:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.preset-questions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.preset-input {
  flex: 1;
}

.preset-actions {
  display: flex;
  gap: 8px;
}

.add-btn {
  background: #28a745;
  border-color: #28a745;
  color: white;
  border-radius: 6px;
}

.delete-btn {
  background: #dc3545;
  border-color: #dc3545;
  color: white;
  border-radius: 6px;
}

.model-section {
  display: flex;
  gap: 12px;
  align-items: center;
}

.model-select {
  flex: 1;
}

.config-btn {
  background: #6c757d;
  border-color: #6c757d;
  color: white;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.knowledge-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.history-input {
  max-width: 120px;
}

.setting-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.setting-label {
  font-size: 14px;
  color: #495057;
}

/* 聊天预览样式 */
.chat-preview {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 8px;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message {
  display: flex;
  gap: 12px;
  max-width: 85%;
}

.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.bot-message {
  align-self: flex-start;
}

.message-avatar {
  flex-shrink: 0;
}

.app-icon-small {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
}

.user-avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 50%, #48dbfb 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
}

.message-content {
  flex: 1;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.app-name {
  font-size: 13px;
  font-weight: 600;
  color: #495057;
}

.message-time {
  font-size: 11px;
  color: #6c757d;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;
}

.user-message .message-text {
  background: #007bff;
  color: white;
  border-bottom-right-radius: 4px;
}

.bot-message .message-text {
  background: #f8f9fa;
  color: #495057;
  border: 1px solid #e9ecef;
  border-bottom-left-radius: 4px;
}

.error-message .message-text {
  background: #f8d7da;
  border-color: #f5c6cb;
}

.error-text {
  color: #721c24;
}

.chat-input {
  padding: 20px;
  border-top: 1px solid #e9ecef;
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 24px;
  padding: 4px;
}

.clear-btn {
  background: #6c757d;
  border-color: #6c757d;
  color: white;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  padding: 0;
}

.chat-input-field {
  flex: 1;
}

.chat-input-field :deep(.el-input__wrapper) {
  border: none;
  background: transparent;
  box-shadow: none;
  padding: 8px 0;
}

.chat-input-field :deep(.el-input__inner) {
  background: transparent;
  border: none;
  padding: 0;
  font-size: 14px;
}

.input-actions {
  display: flex;
  gap: 8px;
}

.upload-btn {
  background: #6c757d;
  border-color: #6c757d;
  color: white;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  padding: 0;
}

.send-btn {
  background: #007bff;
  border-color: #007bff;
  color: white;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  padding: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .editor-content {
    flex-direction: column;
  }
  
  .left-panel, .right-panel {
    min-width: auto;
  }
}
</style> 