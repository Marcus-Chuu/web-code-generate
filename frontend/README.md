# 灵构 AI · 前端（web-weave-frontend）

<div align="center">

**基于 Vue 3 + TypeScript + Ant Design Vue 的 AI 网页代码生成前端**

用自然语言描述你的想法，AI 实时生成代码、流式对话迭代，并在右侧实时预览效果，支持一键部署与源码下载。

![Vue](https://img.shields.io/badge/Vue-3.5-4FC08D?logo=vuedotjs&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-5.8-3178C6?logo=typescript&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-7-646CFF?logo=vite&logoColor=white)
![Ant Design Vue](https://img.shields.io/badge/Ant%20Design%20Vue-4.2-0170FE?logo=antdesign&logoColor=white)
![Pinia](https://img.shields.io/badge/Pinia-3-FFD859)
![Vue Router](https://img.shields.io/badge/Vue%20Router-4.5-4FC08D)

</div>

---

## 📖 项目简介

「灵构 AI」是一个 AI 网页代码生成平台的前端项目。用户通过自然语言描述需求，AI 会实时生成网页代码并流式展示生成过程，右侧同步预览成品页面；支持与 AI 多轮对话迭代、一键部署、下载源码，以及个人应用与后台管理。

配套后端项目见仓库根目录的 [`README.md`](../README.md)（Spring Boot + LangChain4j + LangGraph4j）。

## ✨ 功能特性

### 用户功能

- 🏠 **首页（发现）**：输入提示词创建应用、切换「工作流模式」、浏览精选应用
- 💬 **AI 创作台（对话页）**：
  - SSE 流式接收 AI 回复（打字机效果渲染）
  - Markdown + 代码高亮展示
  - 右侧 iframe 实时预览生成的页面
  - 支持「工作流模式」（LangGraph4j 多步骤生成）
  - 部署、下载 ZIP 源码
- 📚 **我的应用**：分页列表、按名称搜索、编辑、删除、部署、预览
- ✏️ **应用编辑**：修改应用名称
- 👤 **登录 / 注册**：账号密码登录、头像选择注册
- 🔒 **路由守卫**：登录鉴权、管理员鉴权、游客拦截

### 管理员功能

- 👥 **用户管理**：查询、编辑（角色/昵称/头像/简介）、删除
- 📱 **应用管理**：查询、编辑（名称/封面/精选优先级）、删除
- 💬 **对话审计**：按消息类型/应用/用户查询全站对话记录

## 🛠 技术栈

| 分类 | 技术 |
| --- | --- |
| 前端框架 | Vue 3.5（Composition API + `<script setup>`） |
| 语言 | TypeScript 5.8 |
| 构建工具 | Vite 7 |
| UI 组件库 | Ant Design Vue 4 + @ant-design/icons-vue |
| 路由 | Vue Router 4 |
| 状态管理 | Pinia 3 |
| HTTP 客户端 | Axios |
| Markdown 渲染 | markdown-it + highlight.js |
| 代码规范 | ESLint 9 + Prettier 3.5 + vue-tsc |
| 接口类型生成 | @umijs/openapi（openapi2ts） |

## 📁 项目结构

```
src/
├── api/                          # 接口层
│   ├── client.ts                 # Axios 封装、SSE 地址构建、错误处理
│   └── services.ts               # userService / appService / chatService
├── assets/                       # 静态资源（Logo、AI 头像）
├── components/                   # 公共组件
│   ├── AppCard.vue               # 应用卡片
│   ├── AvatarPicker.vue          # 头像选择器
│   ├── GlobalHeader.vue          # 全局头部（导航 + 用户菜单）
│   ├── GlobalFooter.vue          # 全局底部
│   └── MarkdownRenderer.vue      # Markdown 渲染（含代码高亮）
├── config/                       # 配置
│   ├── avatars.ts                # 默认头像与可选头像
│   └── env.example.ts            # 环境变量说明
├── layouts/
│   └── BasicLayout.vue           # 基础布局（header + content + footer）
├── pages/                        # 页面
│   ├── HomePage.vue              # 首页 / 发现
│   ├── NotFoundPage.vue          # 404
│   ├── app/
│   │   ├── AppChatPage.vue       # AI 创作台（对话 + 预览）
│   │   ├── AppEditPage.vue       # 应用编辑
│   │   └── MyAppsPage.vue        # 我的应用
│   ├── admin/
│   │   ├── AppManagePage.vue     # 应用管理
│   │   ├── ChatManagePage.vue    # 对话审计
│   │   └── UserManagePage.vue    # 用户管理
│   └── user/
│       ├── UserLoginPage.vue     # 登录
│       └── UserRegisterPage.vue  # 注册
├── router/
│   └── index.ts                  # 路由配置 + 登录/管理员守卫
├── stores/
│   └── loginUser.ts              # 登录用户状态（Pinia）
├── styles/
│   └── global.css                # 全局样式与设计令牌
├── types/
│   └── domain.ts                 # 领域类型定义
├── utils/
│   ├── format.ts                 # 格式化工具
│   ├── preview.ts                # 预览地址解析
│   └── time.ts                   # 时间工具
├── App.vue                       # 根组件（主题配置）
└── main.ts                       # 应用入口
```

## 🚀 快速开始

### 环境要求

| 依赖 | 版本 |
| --- | --- |
| Node.js | 18+（建议 20+） |
| 包管理器 | npm / pnpm |

> 需要先启动后端服务（默认 `http://localhost:8123`），开发环境通过 Vite 代理转发 `/api` 请求。

### 安装依赖

```sh
npm install
```

### 环境变量

在项目根目录创建 `.env.development` / `.env.production`：

```ini
# API 基础地址（默认 /api，开发环境由 Vite 代理到后端）
VITE_API_BASE_URL=/api

# SSE 流式地址（可选，默认与 API 同源）
VITE_SSE_BASE_URL=/api
```

开发环境 Vite 已配置代理（见 `vite.config.ts`）：

```ts
server: {
  proxy: {
    '/api': { target: 'http://localhost:8123', changeOrigin: true },
  },
}
```

### 启动开发服务器

```sh
npm run dev
```

默认访问地址：`http://localhost:5173`

### 类型检查 / 构建 / 预览

```sh
npm run type-check      # vue-tsc 类型检查
npm run build           # 类型检查 + 生产构建
npm run preview         # 预览构建产物
```

### 代码检查与格式化

```sh
npm run lint            # ESLint（自动修复）
npm run format          # Prettier 格式化
```

## 🧭 页面路由

| 路径 | 页面 | 权限 |
| --- | --- | --- |
| `/` | 首页（发现） | 公开 |
| `/user/login` | 登录 | 游客 |
| `/user/register` | 注册 | 游客 |
| `/apps` | 我的应用 | 登录 |
| `/app/chat/:id` | AI 创作台 | 登录 |
| `/app/edit/:id` | 编辑应用 | 登录 |
| `/admin/users` | 用户管理 | 管理员 |
| `/admin/apps` | 应用管理 | 管理员 |
| `/admin/chats` | 对话审计 | 管理员 |
| `*` | 404 | 公开 |

## 🔄 核心业务流程

### 1. 创建应用

1. 首页输入提示词（可选开启「工作流模式」）
2. 未登录会先跳转登录并保留提示词
3. 调用后端创建应用接口获得 `appId`
4. 跳转到 `/app/chat/:id` 并自动发送初始提示词

### 2. AI 对话生成

1. 前端通过 `fetch` + `AbortController` 发起 SSE 请求（`/api/app/chat/gen/code`）
2. 流式解析 SSE 数据，逐字渲染 AI 回复（Markdown + 代码高亮）
3. 生成完成后自动刷新右侧预览 iframe

### 3. 部署与下载

1. 对话页点击「部署」调用部署接口，生成可访问地址并刷新预览
2. 点击「下载」获取源码 ZIP 包

## 📡 与后端交互

- 所有接口通过 `src/api/client.ts` 统一封装，自动携带 Cookie（`withCredentials: true`），统一处理业务错误码与登录态过期（`40100`）。
- SSE 流式接口使用原生 `fetch` 读取 `text/event-stream`，避免 Axios 对流的缓冲。
- 预览地址由 `utils/preview.ts` 根据 `codeGenType` 与 `appId` 解析到后端静态资源服务（`/api/static/{codeGenType}_{appId}`）。

## 🔗 关联项目

- [后端项目（Spring Boot + LangChain4j + LangGraph4j）](../README.md)

## ⚠️ 注意事项

- 应用生成依赖后端 AI 服务，需先启动后端并正确配置大模型 API Key。
- 预览功能依赖后端静态资源服务（`/api/static/**`）。
- 管理员相关页面需要后端 `admin` 角色，前端路由仅做展示级拦截，权限校验以后端为准。

## 📄 许可证

本项目暂未包含 LICENSE 文件，版权归作者所有。
