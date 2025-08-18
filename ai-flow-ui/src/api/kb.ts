import { request } from './http'

export interface KnowledgeBaseSummary {
  id: string
  name: string
  description?: string
  tags?: string[]
  createdAt: string
  updatedAt: string
}

export interface KnowledgeBaseUpsertRequest {
  name: string
  description?: string
  tags?: string[]
}

const BASE = '/api/kb'

export function listKb(params?: { name?: string }) {
  return request<KnowledgeBaseSummary[]>(BASE, { query: params })
}

export function createKb(body: KnowledgeBaseUpsertRequest) {
  return request<KnowledgeBaseSummary>(BASE, { method: 'POST', body })
}

export function getKb(id: string) {
  return request<KnowledgeBaseSummary>(`${BASE}/${id}`)
}

export function updateKb(id: string, body: KnowledgeBaseUpsertRequest) {
  return request<KnowledgeBaseSummary>(`${BASE}/${id}`, { method: 'PUT', body })
}

export function deleteKb(id: string) {
  return request<void>(`${BASE}/${id}`, { method: 'DELETE' })
} 