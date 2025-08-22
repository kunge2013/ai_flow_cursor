import { request } from '../../../api/http'

// 模型查询参数类型
export interface ModelQueryParams {
  currentPage?: number
  pageSize?: number
  modelName?: string
  modelType?: string
  baseModel?: string
  status?: string
  [key: string]: string | number | boolean | null | undefined
}

// 模型数据类型
export interface ModelData {
  id?: number
  modelName: string
  baseModel: string
  modelType: string
  status: string
  apiEndpoint: string
  apiKey: string
  description?: string
  maxTokens: number
  temperature: number
  topP: number
  frequencyPenalty: number
  presencePenalty: number
  stopWords?: string
  configJson?: string
  createdAt?: string
  updatedAt?: string
}

// 模型测试参数类型
export interface ModelTestParams {
  modelId?: number
  apiEndpoint: string
  apiKey: string
  testInput?: string
  maxTokens: number
  temperature: number
}

// 模型管理API接口
export const modelApi = {
  // 分页查询模型列表
  getModelPage(params: ModelQueryParams) {
    return request('/api/models', {
      method: 'GET',
      query: params
    })
  },

  // 根据ID获取模型详情
  getModelById(id: number) {
    return request(`/api/models/${id}`, {
      method: 'GET'
    })
  },

  // 创建模型
  createModel(data: ModelData) {
    return request('/api/models', {
      method: 'POST',
      body: data
    })
  },

  // 更新模型
  updateModel(id: number, data: ModelData) {
    return request(`/api/models/${id}`, {
      method: 'PUT',
      body: data
    })
  },

  // 删除模型
  deleteModel(id: number) {
    return request(`/api/models/${id}`, {
      method: 'DELETE'
    })
  },

  // 测试模型接口
  testModel(data: ModelTestParams) {
    return request('/api/models/test', {
      method: 'POST',
      body: data
    })
  },

  // 获取模型类型列表
  getModelTypes() {
    return request('/api/models/types', {
      method: 'GET'
    })
  },

  // 获取基础模型列表
  getBaseModels() {
    return request('/api/models/base-models', {
      method: 'GET'
    })
  }
} 