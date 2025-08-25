import { request } from './http'

export interface FlowSummary {
  id: string
  name: string
  description?: string
  createdAt: string
  updatedAt: string
}

export interface FlowUpsertRequest {
  name: string
  description?: string
}

export interface FlowGraph {
  nodes: Array<{ id: string; type: string; x?: number; y?: number; text?: string; properties?: Record<string, any> }>
  edges: Array<{ id?: string; sourceNodeId: string; targetNodeId: string; label?: string }>
}

export interface FlowWithGraphResponse extends FlowSummary { graph: FlowGraph }

export interface FlowRunRequest { inputs?: Record<string, any> }
export interface FlowRunResult { flowId: string; runId: string; outputs: Record<string, any>; trace: Array<Record<string, any>> }

const BASE = '/api/flows'

export function listFlows(params?: { name?: string }) {
  return request<FlowSummary[]>(BASE, { query: params })
}

export function createFlow(body: FlowUpsertRequest) {
  return request<FlowSummary>(BASE, { method: 'POST', body })
}

export function getFlow(id: string) {
  return request<FlowWithGraphResponse>(`${BASE}/${id}`)
}

export function updateFlow(id: string, body: FlowUpsertRequest) {
  return request<FlowSummary>(`${BASE}/${id}`, { method: 'PUT', body })
}

export function deleteFlow(id: string) {
  return request<void>(`${BASE}/${id}`, { method: 'DELETE' })
}

export function getFlowGraph(id: string) {
  return request<FlowGraph>(`${BASE}/${id}/graph`)
}

export function saveFlowGraph(id: string, graph: FlowGraph) {
  return request<FlowGraph>(`${BASE}/${id}/graph`, { method: 'PUT', body: graph })
}

export function runFlow(id: string, req: FlowRunRequest) {
  return request<FlowRunResult>(`${BASE}/${id}/run`, { method: 'POST', body: req })
} 