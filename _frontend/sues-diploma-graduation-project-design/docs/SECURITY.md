# 安全规范

## 数据安全

### 1. 敏感信息处理
- 密码必须加密存储
- 使用 HTTPS 传输
- 敏感信息不存储在 localStorage

### 2. XSS 防护
```javascript
// 使用 v-html 时必须过滤内容
const sanitizeHtml = (html) => {
  // 实现 HTML 过滤逻辑
}
```

### 3. CSRF 防护
- 使用 CSRF Token
- 验证请求来源

## 权限控制

### 1. 路由权限
```javascript
// router/index.js
const routes = [
  {
    path: '/dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  }
]
```

### 2. 接口权限
- 使用 JWT Token
- 接口鉴权中间件 