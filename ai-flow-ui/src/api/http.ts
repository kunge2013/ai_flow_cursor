export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE'

export interface RequestOptions {
  method?: HttpMethod
  headers?: Record<string, string>
  body?: unknown
  query?: Record<string, string | number | boolean | undefined | null>
}

function buildQuery(params?: RequestOptions['query']): string {
  if (!params) return ''
  const usp = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v === undefined || v === null || v === '') return
    usp.append(k, String(v))
  })
  const s = usp.toString()
  return s ? `?${s}` : ''
}

export async function request<T>(url: string, options: RequestOptions = {}): Promise<T> {
  const { method = 'GET', headers = {}, body, query } = options
  const isJson = body !== undefined && !(body instanceof FormData)
  const fetchUrl = `${url}${buildQuery(query)}`
  const res = await fetch(fetchUrl, {
    method,
    headers: {
      ...(isJson ? { 'Content-Type': 'application/json' } : {}),
      ...headers
    },
    body: isJson ? JSON.stringify(body) : (body as BodyInit | undefined)
  })
  if (!res.ok) {
    let msg = `${res.status} ${res.statusText}`
    try {
      const data = await res.json()
      msg = data?.message || msg
    } catch {}
    throw new Error(msg)
  }
  const contentType = res.headers.get('content-type') || ''
  if (contentType.includes('application/json')) return res.json() as Promise<T>
  return (await res.text()) as unknown as T
} 