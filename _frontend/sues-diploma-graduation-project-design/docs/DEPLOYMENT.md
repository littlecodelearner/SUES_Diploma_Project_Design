# 部署指南

## 环境要求
- Node.js >= 18.0.0
- npm >= 9.0.0
- 现代浏览器支持

## 构建步骤

### 1. 安装依赖
```bash
npm install
```

### 2. 构建生产版本
```bash
npm run build
```

### 3. 部署配置
```nginx
# nginx.conf
server {
    listen 80;
    server_name example.com;

    location / {
        root /path/to/dist;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend-server;
    }
}
```

## 环境变量配置

### 开发环境
```env
VITE_API_BASE_URL=http://localhost:21167
VITE_APP_TITLE=健康管理系统(开发)
```

### 生产环境
```env
VITE_API_BASE_URL=https://api.example.com
VITE_APP_TITLE=健康管理系统
``` 