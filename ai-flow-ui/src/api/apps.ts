import { request } from './http'

export interface AiAppSummary {
  id: string
  name: string
  description?: string
  iconUrl?: string
  appType: 'simple' | 'advanced'
  model?: string
  prompt?: string
  openingRemark?: string
  createdAt: string
  updatedAt: string
}

export interface AiAppUpsertRequest {
  name: string
  description?: string
  iconUrl?: string
  appType: 'simple' | 'advanced'
  model?: string
  prompt?: string
  openingRemark?: string
}

const BASE = '/api/apps'

export function listApps(params?: { name?: string; type?: 'simple' | 'advanced' }) {
  return request<AiAppSummary[]>(BASE, { query: params })
}

export function createApp(body: AiAppUpsertRequest) {
  return request<AiAppSummary>(BASE, { method: 'POST', body })
}

export function getApp(id: string) {
  return request<AiAppSummary>(`${BASE}/${id}`)
}

export function updateApp(id: string, body: AiAppUpsertRequest) {
  return request<AiAppSummary>(`${BASE}/${id}`, { method: 'PUT', body })
}

export function deleteApp(id: string) {
  return request<void>(`${BASE}/${id}`, { method: 'DELETE' })
} 