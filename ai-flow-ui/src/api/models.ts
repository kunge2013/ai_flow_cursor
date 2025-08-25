import { request } from './http'

export interface ModelConfig {
  id: string
  name: string
  provider?: string
  model?: string
  enabled?: boolean
}

const BASE = '/api/models'

export function listModels(params?: { enabled?: boolean }) {
  return request<ModelConfig[]>(BASE, { query: params })
}

export function createModel(body: Omit<ModelConfig, 'id'>) {
  return request<ModelConfig>(BASE, { method: 'POST', body })
}

export function getModel(id: string) {
  return request<ModelConfig>(`${BASE}/${id}`)
}

export function updateModel(id: string, body: Omit<ModelConfig, 'id'>) {
  return request<ModelConfig>(`${BASE}/${id}`, { method: 'PUT', body })
}

export function deleteModel(id: string) {
  return request<void>(`${BASE}/${id}`, { method: 'DELETE' })
} 