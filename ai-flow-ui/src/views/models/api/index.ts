import axios from 'axios'
import type { 
  ModelInfo, 
  ModelSearchForm, 
  PaginationInfo, 
  ApiResponse, 
  ModelListResponse 
} from '../types'

// 创建axios实例
const api = axios.create({
  baseURL: '/api/models',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 可以在这里添加token等认证信息
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    console.error('API请求错误:', error)
    return Promise.reject(error)
  }
)

// 获取模型列表
export const getModelList = async (
  params: ModelSearchForm & PaginationInfo
): Promise<ApiResponse<ModelListResponse>> => {
  return api.get('/list', { params })
}

// 获取模型详情
export const getModelDetail = async (id: number): Promise<ApiResponse<ModelInfo>> => {
  return api.get(`/detail/${id}`)
}

// 创建模型
export const createModel = async (data: ModelInfo): Promise<ApiResponse<ModelInfo>> => {
  return api.post('/create', data)
}

// 更新模型
export const updateModel = async (id: number, data: Partial<ModelInfo>): Promise<ApiResponse<ModelInfo>> => {
  return api.put(`/update/${id}`, data)
}

// 删除模型
export const deleteModel = async (id: number): Promise<ApiResponse<boolean>> => {
  return api.delete(`/delete/${id}`)
}

// 测试模型接口
export const testModel = async (data: Pick<ModelInfo, 'apiEndpoint' | 'apiKey'>): Promise<ApiResponse<boolean>> => {
  return api.post('/test', data)
}

// 启用/禁用模型
export const toggleModelStatus = async (id: number, status: 'active' | 'inactive'): Promise<ApiResponse<boolean>> => {
  return api.patch(`/status/${id}`, { status })
}

// 批量操作
export const batchOperation = async (ids: number[], operation: 'enable' | 'disable' | 'delete'): Promise<ApiResponse<boolean>> => {
  return api.post('/batch', { ids, operation })
}

// 导出模型配置
export const exportModels = async (params?: ModelSearchForm): Promise<Blob> => {
  const response = await api.get('/export', { 
    params, 
    responseType: 'blob' 
  })
  return response
}

// 导入模型配置
export const importModels = async (file: File): Promise<ApiResponse<{ success: number; failed: number }>> => {
  const formData = new FormData()
  formData.append('file', file)
  
  return api.post('/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export default api 