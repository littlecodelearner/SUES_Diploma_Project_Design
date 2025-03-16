# 开发文档

## 相关文档
- [API 文档](API.md)
- [组件文档](COMPONENTS.md)
- [错误码文档](ERROR_CODES.md)
- [样式指南](STYLE_GUIDE.md)

## 目录
- [错误处理规范](#错误处理规范)
- [状态管理规范](#状态管理规范)
- [组件开发规范](#组件开发规范)
- [API 接口规范](#api-接口规范)
- [数据处理规范](#数据处理规范)
- [最佳实践](#最佳实践)
- [常见问题](#常见问题)
- [待办事项](#待办事项)
- [更新日志](#更新日志)

## 错误处理规范

### 1. HTTP 错误处理策略

#### 1.1 404 错误处理
- **问题描述**: 当用户没有创建健康档案时,API 会返回 404 错误。这种情况不应该显示为错误,而应该引导用户创建档案。
- **解决方案**: 
  - 在 `request.js` 的响应拦截器中不处理 404 错误
  - 让具体的组件处理 404 错误,以便提供更友好的用户提示
  ```javascript
  // 在响应拦截器中
  if (error.response?.status !== 404) {
    ElMessage.error('请求失败，请稍后重试')
  }
  
  // 在组件中
  if (error.response?.status === 404) {
    ElMessage.info('您还没有创建健康档案，请先创建')
  }
  ```

#### 1.2 业务错误处理
- **规范**: 
  - 200: 成功响应
  - 1005: 健康档案获取失败
  - 其他业务错误码: 根据具体业务场景处理

### 2. 用户提示规范

#### 2.1 提示类型
- **错误提示** (error):
  - 网络错误
  - 服务器错误
  - 业务操作失败
  ```javascript
  ElMessage.error('⚠️ 请求失败\n💡 请稍后重试')
  ```

- **信息提示** (info):
  - 功能引导
  - 状态说明
  ```javascript
  ElMessage.info('📝 您还没有创建健康档案，请先创建')
  ```

- **成功提示** (success):
  - 操作成功确认
  ```javascript
  ElMessage.success('✅ 操作成功')
  ```

#### 2.2 提示样式
- 使用表情符号增加友好度
- 合理使用换行增加可读性
- 适当的显示时长
- 重要提示添加关闭按钮

### 3. 最佳实践

1. **错误分层处理**:
   - 通用错误在拦截器中处理
   - 业务相关错误在组件中处理
   - 特殊状态（如 404）单独处理

2. **用户体验优化**:
   - 错误提示要明确且友好
   - 提供后续操作建议
   - 避免技术术语
   - 适当的提示时长

3. **开发建议**:
   - 在开发新功能时，先规划错误处理策略
   - 保持错误处理的一致性
   - 记录所有可能的错误情况

## 状态管理规范

### 1. Pinia 状态持久化
- **问题描述**: 页面刷新后 Pinia store 状态丢失,导致用户需要重新登录
- **解决方案**: 
  ```javascript
  // store/user.js
  const userId = ref(localStorage.getItem('userId') || null)
  
  const setUserInfo = (info) => {
    userId.value = info.userId
    localStorage.setItem('userId', info.userId)
  }
  ```
- **注意事项**:
  - 只持久化必要的状态
  - 敏感信息不要存储在 localStorage
  - 考虑数据过期处理

### 2. 状态初始化
- 在 store 定义中初始化默认值
- 在组件挂载时检查并恢复状态
- 处理状态恢复失败的情况

## 组件开发规范

### 1. 生命周期钩子使用
- **mounted**: 获取初始数据,DOM操作
- **onBeforeMount**: 准备组件数据
- **onUnmounted**: 清理定时器,事件监听等

### 2. 错误边界处理
- 使用 try-catch 包装异步操作
- 提供错误状态的 UI 展示
- 实现错误恢复机制

### 3. 加载状态处理
```vue
<template>
  <div v-loading="loading">
    <el-empty v-if="!data && !loading" />
    <div v-else>内容</div>
  </div>
</template>
```

## API 接口规范

### 1. 接口定义
```javascript
// api/health.js
/**
 * 获取用户健康档案
 * @param {number} userId - 用户ID
 * @returns {Promise<{
 *   code: number,
 *   message: string,
 *   data: HealthProfile
 * }>}
 */
export const getHealthProfile = (userId) => {
  return http.get(`${ApiPath.HEALTH.PROFILE}/${userId}`)
}
```

### 2. 响应码说明
| 状态码 | 说明 | 处理方式 |
|--------|------|----------|
| 200 | 成功 | 正常处理响应数据 |
| 404 | 资源不存在 | 引导用户创建 |
| 1005 | 健康档案获取失败 | 显示错误信息 |

### 3. 模拟数据配置
```javascript
// config/dev.config.js
export default {
  MOCK_ENABLED: false,
  API_BASE_URL: 'http://localhost:21167/api'
}
```

## 数据处理规范

### 1. 搜索和分页处理策略

#### 1.1 前端搜索与分页的问题
- **问题描述**: 当同时使用前端搜索和后端分页时，会导致分页数据不准确。例如：设置每页显示20条记录，但由于前端搜索过滤后的数据可能少于20条，导致显示数量不正确。
- **错误示例**:
```javascript
// ❌ 错误的实现：前端搜索 + 后端分页
const filteredData = computed(() => {
  return data.filter(item => item.name.includes(searchText.value))
})
```

#### 1.2 推荐的解决方案
- **方案一：完全前端处理**
  - 适用场景：数据量小（通常小于1000条）
  - 实现方式：一次性获取所有数据，在前端进行搜索和分页
  ```javascript
  // ✅ 正确的前端实现
  const processData = (data, search, page, pageSize) => {
    const filtered = data.filter(item => 
      item.name.toLowerCase().includes(search.toLowerCase())
    )
    const start = (page - 1) * pageSize
    return filtered.slice(start, start + pageSize)
  }
  ```

- **方案二：完全后端处理（推荐）**
  - 适用场景：大数据量、需要精确的分页控制
  - 实现方式：将搜索条件传递给后端，由后端进行搜索和分页
  ```javascript
  // ✅ 正确的后端实现
  const fetchData = async (search, page, pageSize) => {
    const response = await api.getData({
      keyword: search,
      current: page,
      size: pageSize
    })
    return response.data
  }
  ```

#### 1.3 选择标准
1. 数据量大小
   - < 1000条：可以考虑前端处理
   - > 1000条：建议使用后端处理

2. 性能要求
   - 需要即时搜索：考虑前端处理
   - 对准确性要求高：使用后端处理

3. 用户体验
   - 前端处理：响应更快，但初始加载较慢
   - 后端处理：初始加载快，但每次搜索都需要请求

#### 1.4 最佳实践建议
1. 明确区分处理方式
   - 不要混合使用前端搜索和后端分页
   - 选择一种方案并始终保持一致

2. 性能优化
   - 使用防抖处理搜索输入
   - 添加适当的加载状态
   - 缓存已请求的数据

3. 用户体验
   - 显示总数据量和当前页信息
   - 提供清晰的加载反馈
   - 保持搜索和分页的响应及时

4. 代码组织
   - 将搜索和分页逻辑封装为可复用的组件
   - 使用计算属性处理数据转换
   - 保持代码结构清晰

### 2. 日期格式处理规范

#### 2.1 后端支持的日期格式
- **问题描述**: 后端只支持特定的日期格式，使用不支持的格式会导致反序列化错误。
- **支持的格式**:
  1. `yyyy-MM-dd`: 纯日期格式
  2. `yyyy-MM-dd'T'HH:mm:ss.SSSX`: ISO 8601格式（带时区）
  3. `yyyy-MM-dd'T'HH:mm:ss.SSS`: ISO 8601格式（不带时区）
  4. `EEE, dd MMM yyyy HH:mm:ss zzz`: RFC 格式

#### 2.2 日期处理最佳实践
```javascript
// 日期格式化工具函数
const formatDate = (date) => {
  if (!date) return null
  
  // 如果已经是字符串格式，检查是否符合支持的格式
  if (typeof date === 'string') {
    // 检查是否符合任一支持的格式
    const isValidFormat = [
      /^\d{4}-\d{2}-\d{2}$/,  // yyyy-MM-dd
      /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}[+-]\d{2}:\d{2}$/,  // yyyy-MM-dd'T'HH:mm:ss.SSSX
      /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}$/,  // yyyy-MM-dd'T'HH:mm:ss.SSS
      /^[A-Za-z]{3}, \d{2} [A-Za-z]{3} \d{4} \d{2}:\d{2}:\d{2} [A-Z]{3}$/  // EEE, dd MMM yyyy HH:mm:ss zzz
    ].some(regex => regex.test(date))

    if (isValidFormat) return date
  }
  
  // 如果是日期对象或其他格式，转换为 "yyyy-MM-dd'T'HH:mm:ss.SSS" 格式
  const d = new Date(date)
  return d.toISOString().split('Z')[0] // 移除末尾的 'Z' 以符合格式
}
```

#### 2.3 Element Plus 日期选择器配置
```vue
<template>
  <el-date-picker
    v-model="date"
    type="datetime"
    placeholder="选择日期时间"
    format="YYYY-MM-DD HH:mm:ss"
    value-format="YYYY-MM-DDTHH:mm:ss.SSS"
  />
</template>
```

#### 2.4 注意事项
- 在发送请求前，始终使用 `formatDate` 函数处理日期
- 使用 Element Plus 日期选择器时，正确配置 `format` 和 `value-format`
- 避免直接发送 JavaScript Date 对象到后端
- 注意时区问题，建议统一使用 UTC 时间

## 常见问题

### 1. Element Plus 相关

#### 1.1 按钮类型废弃警告
- **问题**: type="text" 将在 v3.0.0 废弃
- **解决**: 使用 type="default" 或迁移到 el-link
- **临时**: 可以忽略警告

#### 1.2 表单验证
- 确保 rules 和 model 配置正确
- 使用 ref 获取表单实例
- 在提交前进行验证

### 2. Vue Router 相关

#### 2.1 路由守卫
- 检查登录状态
- 处理未授权访问
- 保存上一页面路径

## 开发环境配置

### 1. 开发服务器
```javascript
// vite.config.js
export default {
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:21167',
        changeOrigin: true
      }
    }
  }
}
```

### 2. 环境变量
- `.env.development`: 开发环境配置
- `.env.production`: 生产环境配置

## 待办事项

- [ ] 实现健康档案创建页面
- [ ] 实现健康档案编辑页面
- [ ] 完善错误码文档
- [ ] 添加全局错误处理组件
- [ ] 添加路由权限控制
- [ ] 实现数据可视化功能
- [ ] 优化移动端适配
- [ ] 添加单元测试

## 更新日志

### 2024-03-21
- 优化了 404 错误处理逻辑
- 规范化了错误提示信息
- 创建了开发文档
- 添加了状态管理规范
- 补充了常见问题解决方案 

## 防御性编程最佳实践

### 1. API 数据处理

#### 1.1 空值处理
```javascript
// ❌ 错误示例 - 直接使用 API 返回数据
{{ data.value.toFixed(2) }}

// ✅ 正确示例 - 使用空值合并
{{ (data.value || 0).toFixed(2) }}
```

#### 1.2 数据格式化
- 在显示前进行格式化
- 为所有可能的空值提供默认值
- 使用 `toFixed()` 等方法前确保数据存在

```javascript
// 数值格式化示例
const formatNumber = (value, decimals = 0) => {
  if (value === null || value === undefined) return '0'
  return Number(value).toFixed(decimals)
}

// 使用示例
{{ formatNumber(nutritionData.calories, 1) }} kcal
```

### 2. 组件数据初始化

#### 2.1 响应式数据初始化
```javascript
// ❌ 错误示例
const data = ref()

// ✅ 正确示例
const data = ref({
  value: 0,
  items: [],
  status: null
})
```

#### 2.2 Props 默认值
```javascript
const props = defineProps({
  value: {
    type: Number,
    default: 0
  },
  items: {
    type: Array,
    default: () => []
  }
})
```

### 3. 错误处理策略

#### 3.1 API 调用错误处理
```javascript
try {
  const response = await api.getData()
  if (response.code === 200 && response.data) {
    // 处理成功响应
    data.value = response.data
  } else {
    // 处理业务错误
    ElMessage.warning(response.message || '获取数据失败')
  }
} catch (error) {
  // 处理网络错误
  console.error('API调用失败:', error)
  ElMessage.error('网络请求失败，请稍后重试')
} finally {
  loading.value = false
}
```

#### 3.2 数据验证
```javascript
// 数据验证函数示例
const validateData = (data) => {
  if (!data) return false
  if (typeof data.value !== 'number') return false
  if (data.value < 0 || data.value > 1000) return false
  return true
}

// 使用验证
const processData = (data) => {
  if (!validateData(data)) {
    console.warn('数据验证失败:', data)
    return null
  }
  return data.value
}
```

### 4. 用户输入处理

#### 4.1 表单输入验证
```javascript
const rules = {
  value: [
    { required: true, message: '请输入数值' },
    { 
      validator: (rule, value) => {
        if (value && (isNaN(value) || value < 0)) {
          return Promise.reject('请输入有效的正数')
        }
        return Promise.resolve()
      }
    }
  ]
}
```

#### 4.2 输入数据清理
```javascript
const cleanInput = (value) => {
  if (typeof value !== 'string') return ''
  return value.trim().replace(/[<>]/g, '')
}
```

### 5. 状态管理安全

#### 5.1 Store 状态初始化
```javascript
const useStore = defineStore('main', {
  state: () => ({
    data: null,
    error: null,
    loading: false
  }),
  actions: {
    resetState() {
      this.data = null
      this.error = null
      this.loading = false
    }
  }
})
```

#### 5.2 状态持久化
```javascript
// 安全的状态持久化
const saveState = (key, value) => {
  try {
    localStorage.setItem(key, JSON.stringify(value))
  } catch (error) {
    console.error('状态持久化失败:', error)
    // 可以选择清理存储
    localStorage.removeItem(key)
  }
}

// 安全的状态恢复
const loadState = (key, defaultValue = null) => {
  try {
    const value = localStorage.getItem(key)
    return value ? JSON.parse(value) : defaultValue
  } catch (error) {
    console.error('状态恢复失败:', error)
    return defaultValue
  }
}
```

### 6. 组件生命周期安全

#### 6.1 异步操作处理
```javascript
const component = {
  setup() {
    const mounted = ref(false)
    
    onMounted(() => {
      mounted.value = true
    })
    
    onUnmounted(() => {
      mounted.value = false
    })
    
    const fetchData = async () => {
      try {
        const data = await api.getData()
        // 确保组件仍然挂载
        if (mounted.value) {
          processData(data)
        }
      } catch (error) {
        console.error('获取数据失败:', error)
      }
    }
  }
}
```

#### 6.2 资源清理
```javascript
onMounted(() => {
  const timer = setInterval(() => {
    // 定时操作
  }, 1000)
  
  // 确保清理资源
  onUnmounted(() => {
    clearInterval(timer)
  })
})
```

### 7. 调试与日志

#### 7.1 开发环境日志
```javascript
const logDebug = (message, data) => {
  if (import.meta.env.DEV) {
    console.log(`[Debug] ${message}:`, data)
  }
}
```

#### 7.2 错误追踪
```javascript
const trackError = (error, context) => {
  console.error(`[${context}] 错误:`, error)
  // 可以添加错误上报逻辑
}
```

### 8. 性能考虑

#### 8.1 大数据列表处理
```javascript
// 使用虚拟列表
<el-virtual-list
  :items="largeDataList"
  :item-size="50"
>
  <template #default="{ item }">
    <item-component
      :key="item.id"
      :data="item"
      @error="handleError"
    />
  </template>
</el-virtual-list>
```

#### 8.2 防抖与节流
```javascript
import { debounce } from 'lodash-es'

const debouncedSearch = debounce((query) => {
  if (!query?.trim()) return
  performSearch(query)
}, 300)
```

这些最佳实践将帮助开发者：
- 提高代码的健壮性和可维护性
- 减少运行时错误
- 提供更好的用户体验
- 便于调试和错误追踪

## 前端分页实现方案

### 场景描述
在管理后台的用户管理模块中，需要实现数据分页功能。由于后端API返回格式固定，我们需要在前端实现一个既能配合后端分页，又能保持良好用户体验的解决方案。

### 技术实现

#### 1. 数据结构设计
```javascript
// 表格数据
const allData = ref([])          // 存储当前页数据
const loading = ref(false)       // 加载状态
const noDataText = ref('暂无数据') // 无数据提示

// 分页参数
const pagination = ref({
  currentPage: 1,  // 当前页码
  pageSize: 10,    // 每页条数
  total: 0        // 总数据量
})
```

#### 2. 数据处理流程
1. **数据获取**：
   ```javascript
   const fetchUsers = async () => {
     try {
       const response = await axios.get('/admin/details', {
         params: {
           page: pagination.value.currentPage,
           size: pagination.value.pageSize,
           userName: searchForm.value.searchText
         }
       })
       
       if (response.data.code === 200) {
         allData.value = response.data.data
         updateTotalCount(response.data)
       }
     } catch (error) {
       handleError(error)
     }
   }
   ```

2. **总数据量计算**：
   ```javascript
   // 根据当前页数据估算总数
   const currentPageSize = response.data.data.length
   if (currentPageSize === pagination.value.pageSize) {
     // 当前页数据量等于页大小，预估还有下一页
     pagination.value.total = (pagination.value.currentPage * pagination.value.pageSize) + pagination.value.pageSize
   } else {
     // 当前页数据量小于页大小，说明是最后一页
     pagination.value.total = ((pagination.value.currentPage - 1) * pagination.value.pageSize) + currentPageSize
   }
   ```

3. **数据展示处理**：
   ```javascript
   const processedData = computed(() => {
     let result = [...allData.value]
     
     // 搜索过滤
     if (searchForm.value.searchText) {
       result = filterData(result)
     }
     
     // 排序处理
     if (sortConfig.value.field) {
       result = sortData(result)
     }
     
     return result
   })
   ```

#### 3. 交互处理
1. **页码变化**：
   ```javascript
   const handleCurrentChange = (page) => {
     pagination.value.currentPage = page
     fetchUsers() // 重新请求数据
   }
   ```

2. **每页条数变化**：
   ```javascript
   const handleSizeChange = (size) => {
     pagination.value.pageSize = size
     pagination.value.currentPage = 1
     fetchUsers() // 重新请求数据
   }
   ```

3. **分页组件配置**：
   ```html
   <el-pagination
     background
     :current-page="pagination.currentPage"
     :page-size="pagination.pageSize"
     :page-sizes="[10, 20, 50, 100]"
     :total="pagination.total"
     layout="total, sizes, prev, pager, next, jumper"
     @size-change="handleSizeChange"
     @current-change="handleCurrentChange"
   />
   ```

### 优化策略

1. **性能优化**：
   - 搜索和排序在前端进行，减少不必要的服务器请求
   - 使用计算属性处理数据，避免重复计算
   - 添加loading状态，提升用户体验

2. **错误处理**：
   - 网络请求错误统一处理
   - 数据为空时显示友好提示
   - 加载失败时提供重试选项

3. **用户体验**：
   - 分页组件添加背景色，提高可点击区域
   - 提供多种分页大小选择
   - 支持页码快速跳转
   - 保持搜索条件和排序状态

### 注意事项

1. **数据一致性**：
   - 切换页码时重新请求数据，确保数据最新
   - 修改每页条数时重置页码为1
   - 搜索时重置页码为1

2. **边界处理**：
   - 处理空数据情况
   - 处理网络请求失败情况
   - 处理数据格式异常情况

3. **扩展性考虑**：
   - 分页逻辑可复用于其他列表页面
   - 支持自定义排序和过滤规则
   - 预留接口参数扩展空间

### 最佳实践

1. **代码组织**：
   - 将分页逻辑封装为可复用的组件或hooks
   - 使用计算属性处理数据转换
   - 保持单一职责原则

2. **命名规范**：
   - 使用语义化的变量和方法名
   - 保持命名风格统一
   - 添加必要的注释说明

3. **测试建议**：
   - 测试页码切换功能
   - 测试每页条数切换功能
   - 测试搜索和排序功能
   - 测试边界条件和错误情况

### 后续优化方向

1. **缓存优化**：
   - 实现数据缓存，减少重复请求
   - 记住用户的分页设置

2. **性能提升**：
   - 实现虚拟滚动
   - 优化大数据量排序算法

3. **功能增强**：
   - 添加列设置功能
   - 支持多字段排序
   - 添加数据导出功能

// ... rest of the content ... 