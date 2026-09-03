import { apiRequest, buildApiUrl, buildSseUrl, ApiError } from '@/api/client'
import type {
  AppAdminUpdateRequest,
  AppQueryRequest,
  AppUpdateRequest,
  AppVO,
  BaseResponse,
  ChatHistory,
  ChatHistoryQueryRequest,
  EntityId,
  LoginUserVO,
  PageResult,
  UserLoginRequest,
  UserQueryRequest,
  UserRegisterRequest,
  UserUpdateRequest,
  UserVO,
} from '@/types/domain'

export const userService = {
  register: (data: UserRegisterRequest) => apiRequest<EntityId>({ url: '/user/register', method: 'POST', data }),
  login: (data: UserLoginRequest) => apiRequest<LoginUserVO>({ url: '/user/login', method: 'POST', data }),
  logout: () => apiRequest<boolean>({ url: '/user/logout', method: 'POST' }),
  list: (data: UserQueryRequest) => apiRequest<PageResult<UserVO>>({ url: '/user/list/page/vo', method: 'POST', data }),
  update: (data: UserUpdateRequest) => apiRequest<boolean>({ url: '/user/update', method: 'POST', data }),
  remove: (id: EntityId) => apiRequest<boolean>({ url: '/user/delete', method: 'POST', data: { id } }),
}

export const appService = {
  create: (initPrompt: string) => apiRequest<EntityId>({ url: '/app/add', method: 'POST', data: { initPrompt } }),
  get: (id: EntityId) => apiRequest<AppVO>({ url: '/app/get/vo', params: { id } }),
  getAdmin: (id: EntityId) => apiRequest<AppVO>({ url: '/app/admin/get/vo', params: { id } }),
  update: (data: AppUpdateRequest) => apiRequest<boolean>({ url: '/app/update', method: 'POST', data }),
  updateAdmin: (data: AppAdminUpdateRequest) => apiRequest<boolean>({ url: '/app/admin/update', method: 'POST', data }),
  remove: (id: EntityId) => apiRequest<boolean>({ url: '/app/delete', method: 'POST', data: { id } }),
  removeAdmin: (id: EntityId) => apiRequest<boolean>({ url: '/app/admin/delete', method: 'POST', data: { id } }),
  listMine: (data: AppQueryRequest) => apiRequest<PageResult<AppVO>>({ url: '/app/my/list/page/vo', method: 'POST', data }),
  listFeatured: (data: AppQueryRequest) => apiRequest<PageResult<AppVO>>({ url: '/app/good/list/page/vo', method: 'POST', data }),
  listAdmin: (data: AppQueryRequest) => apiRequest<PageResult<AppVO>>({ url: '/app/admin/list/page/vo', method: 'POST', data }),
  deploy: (appId: EntityId) => apiRequest<string>({ url: '/app/deploy', method: 'POST', data: { appId } }),
  async download(appId: EntityId) {
    const response = await fetch(buildApiUrl(`/app/download/${appId}`), { credentials: 'include' })
    if (!response.ok) {
      let reason = `下载失败（HTTP ${response.status}）`
      try {
        const payload = await response.json() as BaseResponse<unknown>
        reason = payload.message || reason
        if (payload.code === 40100) window.dispatchEvent(new CustomEvent('app:unauthorized'))
      } catch { /* ZIP 接口错误时不一定返回 JSON。 */ }
      throw new Error(reason)
    }
    const blob = await response.blob()
    const objectUrl = URL.createObjectURL(blob)
    const anchor = document.createElement('a')
    anchor.href = objectUrl
    anchor.download = response.headers.get('Content-Disposition')?.match(/filename="?([^";]+)"?/)?.[1] || `app-${appId}.zip`
    document.body.appendChild(anchor)
    anchor.click()
    anchor.remove()
    URL.revokeObjectURL(objectUrl)
  },
}

export interface StreamHandlers {
  onChunk: (text: string) => void
  onDone: () => void
  onError: (error: Error) => void
}

type WorkflowPayload = {
  message?: string
  stepNumber?: number
  currentStep?: string
  error?: string
}

/** 将后端工作流嵌套在普通 SSE 数据中的事件转换为适合聊天气泡展示的 Markdown。 */
export const formatWorkflowContent = (input: string) => {
  const pattern = /event:\s*(workflow_start|step_completed|workflow_completed|workflow_error)\s*\r?\ndata:\s*(\{[^\r\n]*\})/g
  const parts: string[] = []
  let matched = false

  for (const match of input.matchAll(pattern)) {
    matched = true
    let data: WorkflowPayload = {}
    try { data = JSON.parse(match[2]) as WorkflowPayload } catch { /* 使用默认文案。 */ }

    if (match[1] === 'workflow_start') {
      parts.push(`### 工作流模式\n${data.message || '开始执行代码生成工作流'}\n`)
    } else if (match[1] === 'step_completed') {
      const number = data.stepNumber ? `第 ${data.stepNumber} 步` : '步骤'
      parts.push(`\n- ✓ ${number}：${data.currentStep || '执行完成'}`)
    } else if (match[1] === 'workflow_completed') {
      parts.push(`\n\n✅ ${data.message || '代码生成工作流执行完成'}`)
    } else {
      parts.push(`\n\n❌ ${data.error || data.message || '工作流执行失败'}`)
    }
  }
  return matched ? parts.join('') : input
}

export const chatService = {
  listByApp(appId: EntityId, pageSize = 50, lastCreateTime?: string) {
    return apiRequest<PageResult<ChatHistory>>({
      url: `/chatHistory/app/${appId}`,
      params: { pageSize, lastCreateTime },
    })
  },
  listAdmin: (data: ChatHistoryQueryRequest) => apiRequest<PageResult<ChatHistory>>({
    url: '/chatHistory/admin/list/page/vo', method: 'POST', data,
  }),
  stream(appId: EntityId, message: string, agent: boolean, handlers: StreamHandlers) {
    const controller = new AbortController()
    const params = new URLSearchParams({ appId: String(appId), message, agent: String(agent) })

    void (async () => {
      try {
        const response = await fetch(buildSseUrl(`/app/chat/gen/code?${params}`), {
          credentials: 'include',
          cache: 'no-store',
          headers: { Accept: 'text/event-stream', 'Cache-Control': 'no-cache' },
          signal: controller.signal,
        })
        if (!response.ok || !response.body) {
          let reason = `生成请求失败（HTTP ${response.status}）`
          try {
            const payload = await response.json() as BaseResponse<unknown>
            reason = payload.message || reason
            if (payload.code === 40100) window.dispatchEvent(new CustomEvent('app:unauthorized'))
          } catch { /* SSE 建连失败不一定返回 JSON。 */ }
          throw new ApiError(reason, undefined, response.status)
        }

        const reader = response.body.getReader()
        const decoder = new TextDecoder()
        let buffer = ''
        let completed = false
        const consume = (block: string) => {
          let eventName = 'message'
          const dataLines: string[] = []
          for (const line of block.split('\n')) {
            if (line.startsWith('event:')) eventName = line.slice(6).trim()
            if (line.startsWith('data:')) dataLines.push(line.slice(5).trimStart())
          }
          if (eventName === 'done') { completed = true; handlers.onDone(); return }
          const raw = dataLines.join('\n')
          if (!raw) return
          if (eventName === 'error' || eventName === 'business-error') throw new Error(raw)
          try {
            const parsed = JSON.parse(raw) as { d?: unknown }
            if (typeof parsed.d === 'string') {
              handlers.onChunk(agent ? formatWorkflowContent(parsed.d) : parsed.d)
            }
          } catch { handlers.onChunk(raw) }
        }

        while (!completed) {
          const { value, done } = await reader.read()
          buffer += decoder.decode(value, { stream: !done }).replace(/\r\n?/g, '\n')
          let boundary = buffer.indexOf('\n\n')
          while (boundary >= 0) {
            const block = buffer.slice(0, boundary)
            buffer = buffer.slice(boundary + 2)
            if (block) consume(block)
            boundary = buffer.indexOf('\n\n')
          }
          if (done) break
        }
        if (!completed && buffer.trim()) consume(buffer.trim())
        if (!completed && !controller.signal.aborted) throw new Error('生成连接提前结束，请重试')
      } catch (error) {
        if (!controller.signal.aborted) handlers.onError(error instanceof Error ? error : new Error('生成失败'))
      }
    })()
    return () => controller.abort()
  },
}
