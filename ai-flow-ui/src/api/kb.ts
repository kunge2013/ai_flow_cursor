import { request } from './http'

export interface KnowledgeBaseSummary {
  id: string
  name: string
  description?: string
  tags?: string[]
  vectorModel?: string
  status?: boolean
  createdAt: string
  updatedAt: string
}

export interface KnowledgeBaseUpsertRequest {
  name: string
  description?: string
  tags?: string[]
  vectorModel?: string
  status?: boolean
}

export interface KnowledgeBaseQueryRequest {
  name?: string
  vectorModel?: string
  status?: boolean
  page?: number
  size?: number
}

export interface KnowledgeBasePageResponse {
  records: KnowledgeBaseSummary[]
  total: number
  page: number
  size: number
}

export interface DocumentInfo {
  id: string
  title: string
  content: string
  type?: string
  size?: number
  createdAt: string
  updatedAt: string
}

export interface DocumentUploadRequest {
  title: string
  content: string
  type?: string
  size?: number
}

export interface TestQueryRequest {
  query: string
  topK?: number
  scoreThreshold?: number
}

export interface TestQueryResponse {
  query: string
  documents: DocumentInfo[]
  scores: number[]
  answer: string
}

const BASE = '/api/kb'

export function listKb(params?: KnowledgeBaseQueryRequest) {
  return request<KnowledgeBasePageResponse>(BASE, { query: params })
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

// 文档管理接口
export function getDocuments(kbId: string) {
  return request<DocumentInfo[]>(`${BASE}/${kbId}/documents`)
}

export function addDocument(kbId: string, body: DocumentUploadRequest) {
  return request<DocumentInfo>(`${BASE}/${kbId}/documents`, { method: 'POST', body })
}

export function deleteDocument(kbId: string, documentId: string) {
  return request<void>(`${BASE}/${kbId}/documents/${documentId}`, { method: 'DELETE' })
}

// 命中测试接口
export function testQuery(kbId: string, body: TestQueryRequest) {
  return request<TestQueryResponse>(`${BASE}/${kbId}/test`, { method: 'POST', body })
} 