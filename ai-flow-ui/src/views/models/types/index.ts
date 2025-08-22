// 模型基础信息接口
export interface ModelInfo {
  id?: number
  modelName: string
  baseModel: string
  modelType: string
  status: 'active' | 'inactive'
  apiEndpoint: string
  apiKey?: string
  description?: string
  maxTokens: number
  temperature: number
  topP: number
  frequencyPenalty: number
  presencePenalty: number
  stopWords?: string
  createTime?: string
  updateTime?: string
}

// 搜索表单接口
export interface ModelSearchForm {
  modelName: string
  modelType: string
  baseModel: string
}

// 分页信息接口
export interface PaginationInfo {
  currentPage: number
  pageSize: number
  total: number
}

// 模型类型选项
export const MODEL_TYPE_OPTIONS = [
  { label: '文本生成', value: 'text' },
  { label: '图像生成', value: 'image' },
  { label: '语音识别', value: 'speech' },
  { label: '多模态', value: 'multimodal' },
  { label: '代码生成', value: 'code' },
  { label: '翻译', value: 'translation' }
]

// 基础模型选项
export const BASE_MODEL_OPTIONS = [
  { label: 'GPT-4', value: 'gpt-4' },
  { label: 'GPT-3.5', value: 'gpt-3.5' },
  { label: 'Claude', value: 'claude' },
  { label: 'Gemini', value: 'gemini' },
  { label: '文心一言', value: 'wenxin' },
  { label: '通义千问', value: 'qwen' }
]

// 模型状态选项
export const MODEL_STATUS_OPTIONS = [
  { label: '启用', value: 'active' },
  { label: '禁用', value: 'inactive' }
]

// API响应接口
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  success: boolean
}

// 模型列表响应 - 修复为与后端MyBatis-Plus分页格式一致
export interface ModelListResponse {
  records: ModelInfo[]
  total: number
  size: number
  current: number
  pages: number
} 