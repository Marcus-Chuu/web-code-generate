import type { CodeGenType } from '@/types/domain'

export const formatDate = (value?: string) => {
  if (!value) return '—'
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit',
  }).format(date)
}

export const codeTypeLabel = (value?: CodeGenType) => ({
  html: 'HTML 单页', multi_file: '多文件应用', vue_project: 'Vue 工程',
}[value || 'html'])
