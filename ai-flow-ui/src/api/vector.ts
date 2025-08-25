import { request } from './http'

export interface VectorEmbeddingRequest {
  content: string
  vectorModel: string
}

export interface VectorEmbeddingResponse {
  content: string
  embedding: number[]
  vectorModel: string
  tokenCount: number
  cost: number
}

export interface VectorSearchRequest {
  query: string
  kbId: string
  topK?: number
  scoreThreshold?: number
}

export interface VectorSearchResponse {
  query: string
  results: VectorSearchResult[]
  totalCount: number
  searchTime: number
}

export interface VectorSearchResult {
  documentId: string
  title: string
  content: string
  score: number
  metadata: string
}

export interface DocumentVectorRequest {
  documentId: string
  kbId: string
  content: string
  vectorModel: string
  metadata?: string
}

export interface BatchEmbeddingRequest {
  contents: string[]
  vectorModel: string
}

export interface BatchEmbeddingResponse {
  embeddings: VectorEmbeddingResponse[]
  totalTokenCount: number
  totalCost: number
}

const BASE = '/api/vector'

export function embedDocument(body: VectorEmbeddingRequest) {
  return request<VectorEmbeddingResponse>(`${BASE}/embed`, { method: 'POST', body })
}

export function searchSimilar(body: VectorSearchRequest) {
  return request<VectorSearchResponse>(`${BASE}/search`, { method: 'POST', body })
}

export function batchEmbedDocuments(body: BatchEmbeddingRequest) {
  return request<VectorEmbeddingResponse[]>(`${BASE}/batch-embed`, { method: 'POST', body })
}

export function saveDocumentVector(body: DocumentVectorRequest) {
  return request<void>(`${BASE}/document/save`, { method: 'POST', body })
}

export function deleteDocumentVector(documentId: string) {
  return request<void>(`${BASE}/document/${documentId}`, { method: 'DELETE' })
}

export function updateDocumentVector(documentId: string, body: DocumentVectorRequest) {
  return request<void>(`${BASE}/document/${documentId}`, { method: 'PUT', body })
}

export function healthCheck() {
  return request<{ status: string; service: string; timestamp: number }>(`${BASE}/health`)
} 