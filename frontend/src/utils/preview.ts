import { buildApiUrl } from '@/api/client'
import type { CodeGenType, EntityId } from '@/types/domain'

/**
 * 生成代码预览地址。
 * 后端 StaticResourceController 读取 code_output/{codeGenType}_{appId} 目录；
 * Vue 项目使用构建后的 dist/index.html，其他类型直接访问目录首页。
 */
export const resolvePreviewUrl = (appId: EntityId, codeGenType?: CodeGenType) => {
  if (!codeGenType) return ''
  const directory = `${codeGenType}_${appId}`
  const suffix = codeGenType === 'vue_project' ? '/dist/index.html' : '/'
  const apiUrl = buildApiUrl(`/static/${encodeURIComponent(directory)}${suffix}`)

  if (import.meta.env.DEV && apiUrl.startsWith('/')) {
    // 开发时继续通过 Vite 的 /api 代理访问后端，同时使用不同 loopback 主机隔离生成页面。
    const currentHost = window.location.hostname
    const previewHost = currentHost === 'localhost'
      ? '127.0.0.1'
      : currentHost === '127.0.0.1' ? 'localhost' : currentHost
    return `${window.location.protocol}//${previewHost}:${window.location.port}${apiUrl}`
  }
  return apiUrl
}

export const withCacheBuster = (url: string) => {
  const parsed = new URL(url, window.location.href)
  parsed.searchParams.set('_preview', String(Date.now()))
  return parsed.toString()
}
