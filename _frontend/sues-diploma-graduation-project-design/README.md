# 日常运动健康管理系统

## 项目说明
基于 Vue 3 + Vite 开发的健康管理系统前端项目。

## 技术栈
- Vue 3.4.x
- Vite 5.x
- Element Plus 2.x
- Pinia 2.x
- Vue Router 4.x

## 开发环境要求
- Node.js >= 18.0.0
- npm >= 9.0.0

## 快速开始

### 安装依赖
```bash
npm install
```

### 启动开发服务器
```bash
npm run dev
```

### 构建生产版本
```bash
npm run build
```

## 项目结构
```
src/
├── api/          # API 接口
├── assets/       # 静态资源
├── components/   # 公共组件
├── config/       # 配置文件
├── router/       # 路由配置
├── store/        # 状态管理
├── styles/       # 样式文件
├── utils/        # 工具函数
└── views/        # 页面组件
```

## 开发注意事项

### 浏览器控制台警告说明

### 开发环境配置
1. 模拟数据开关：src/config/dev.config.js
2. API 基础路径：src/config/api.config.js
3. 代理配置：vite.config.js

## 文档
- [开发文档](docs/DEVELOPMENT.md) - 包含错误处理规范、最佳实践等开发指南
- [API 文档](docs/API.md) - API 接口文档
- [组件文档](docs/COMPONENTS.md) - 组件使用说明

## 贡献指南
1. Fork 本仓库
2. 创建特性分支
3. 提交更改
4. 发起 Pull Request

## 许可证
MIT License