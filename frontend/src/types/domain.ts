export type EntityId = number | string
export type UserRole = 'user' | 'admin' | 'ban'
export type MessageType = 'user' | 'ai'
export type CodeGenType = 'html' | 'multi_file' | 'vue_project'

export interface BaseResponse<T> {
  code: number
  data: T
  message: string
}

export interface PageResult<T> {
  records: T[]
  pageNumber: number | string
  pageSize: number | string
  totalPage: number | string
  totalRow: number | string
}

export interface PageQuery {
  pageNum: number
  pageSize: number
  sortField?: string
  sortOrder?: 'ascend' | 'descend'
}

export interface LoginUserVO {
  id: EntityId
  userAccount: string
  userName?: string
  userAvatar?: string
  userProfile?: string
  userRole: UserRole
  createTime?: string
  updateTime?: string
}

export interface UserVO {
  id: EntityId
  userAccount: string
  userName?: string
  userAvatar?: string
  userProfile?: string
  userRole: UserRole
  createTime?: string
}

export interface UserLoginRequest {
  userAccount: string
  userPassword: string
}

export interface UserRegisterRequest extends UserLoginRequest {
  checkPassword: string
  userAvatar: string
}

export interface UserQueryRequest extends PageQuery {
  id?: EntityId
  userAccount?: string
  userName?: string
  userProfile?: string
  userRole?: UserRole
}

export interface UserUpdateRequest {
  id: EntityId
  userName?: string
  userAvatar?: string
  userProfile?: string
  userRole: UserRole
}

export interface AppVO {
  id: EntityId
  appName?: string
  cover?: string
  initPrompt?: string
  codeGenType?: CodeGenType
  deployKey?: string
  deployedTime?: string
  priority?: number
  userId?: EntityId
  user?: UserVO
  editTime?: string
  createTime?: string
  updateTime?: string
}

export interface AppQueryRequest extends PageQuery {
  id?: EntityId
  appName?: string
  cover?: string
  initPrompt?: string
  codeGenType?: CodeGenType
  deployKey?: string
  priority?: number
  userId?: EntityId
}

export interface AppUpdateRequest {
  id: EntityId
  appName?: string
}

export interface AppAdminUpdateRequest extends AppUpdateRequest {
  cover?: string
  priority?: number
}

export interface ChatHistory {
  id: EntityId
  message: string
  messageType: MessageType
  appId: EntityId
  userId: EntityId
  createTime?: string
  updateTime?: string
}

export interface ChatHistoryQueryRequest extends PageQuery {
  id?: EntityId
  message?: string
  messageType?: MessageType
  appId?: EntityId
  userId?: EntityId
  lastCreateTime?: string
}
