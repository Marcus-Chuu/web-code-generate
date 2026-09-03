import axios from 'axios'
import type { AxiosRequestConfig } from 'axios'
import type { BaseResponse } from '@/types/domain'

const configuredBaseUrl = import.meta.env.VITE_API_BASE_URL || '/api'
export const API_BASE_URL = configuredBaseUrl.replace(/\/$/, '')

export class ApiError extends Error {
  code?: number
  status?: number

  constructor(message: string, code?: number, status?: number) {
    super(message)
    this.name = 'ApiError'
    this.code = code
    this.status = status
  }
}

export const http = axios.create({
  baseURL: API_BASE_URL,
  timeout: 60_000,
  withCredentials: true,
  headers: { Accept: 'application/json' },
})

export async function apiRequest<T>(config: AxiosRequestConfig): Promise<T> {
  try {
    const response = await http.request<BaseResponse<T>>(config)
    const payload = response.data
    if (payload.code !== 0) {
      if (payload.code === 40100) window.dispatchEvent(new CustomEvent('app:unauthorized'))
      throw new ApiError(payload.message || '请求失败，请稍后重试', payload.code, response.status)
    }
    return payload.data
  } catch (error) {
    if (error instanceof ApiError) throw error
    if (axios.isAxiosError<BaseResponse<unknown>>(error)) {
      const payload = error.response?.data
      if (payload?.code === 40100) window.dispatchEvent(new CustomEvent('app:unauthorized'))
      throw new ApiError(
        payload?.message || (error.code === 'ECONNABORTED' ? '请求超时，请稍后重试' : '无法连接服务器'),
        payload?.code,
        error.response?.status,
      )
    }
    throw error instanceof Error ? error : new Error('操作失败，请稍后重试')
  }
}

export const apiErrorMessage = (error: unknown, fallback = '操作失败，请稍后重试') =>
  error instanceof Error && error.message ? error.message : fallback

export const buildApiUrl = (path: string) =>
  `${API_BASE_URL}${path.startsWith('/') ? path : `/${path}`}`

export const buildSseUrl = (path: string) => {
  const configured = import.meta.env.VITE_SSE_BASE_URL?.replace(/\/$/, '')
  // 默认与普通接口同源，开发环境由 Vite 代理转发并保持流式传输，避免跨域 Cookie 丢失。
  const base = configured || API_BASE_URL
  return `${base}${path.startsWith('/') ? path : `/${path}`}`
}
