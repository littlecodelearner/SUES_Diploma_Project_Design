# 性能优化指南

## 代码优化

### 1. 组件懒加载
```javascript
const HealthProfile = () => import('@/views/HealthProfile.vue')
```

### 2. 虚拟列表
```vue
<template>
  <el-virtual-list
    :items="exerciseList"
    :item-size="50"
  >
    <template #default="{ item }">
      <exercise-item :data="item" />
    </template>
  </el-virtual-list>
</template>
```

### 3. 图片优化
- 使用 WebP 格式
- 懒加载图片
- 合适的图片尺寸

## 缓存策略

### 1. API 缓存
```javascript
const cachedData = new Map()

const getCachedData = async (key) => {
  if (cachedData.has(key)) {
    return cachedData.get(key)
  }
  const data = await fetchData(key)
  cachedData.set(key, data)
  return data
}
```

### 2. 状态持久化
- 使用 localStorage 存储
- 定期清理过期数据
- 控制存储大小 

## 数据处理优化

### 1. 空值处理优化
```javascript
// ❌ 性能较差的实现
const getValue = (obj) => {
  if (obj === null || obj === undefined) {
    return { value: 0, unit: '' }
  }
  return {
    value: obj.value?.toFixed(2) || '0',
    unit: obj.unit || ''
  }
}

// ✅ 性能更好的实现
const getValue = (obj) => ({
  value: (obj?.value || 0).toFixed(2),
  unit: obj?.unit || ''
})
```

### 2. 大量数据处理
```javascript
// ❌ 可能导致性能问题的实现
const processItems = (items) => {
  return items.map(item => ({
    ...item,
    value: item.value?.toFixed(2) || '0',
    timestamp: new Date(item.date).getTime()
  }))
}

// ✅ 性能优化的实现
const processItems = (items) => {
  const result = new Array(items.length)
  for (let i = 0; i < items.length; i++) {
    const item = items[i]
    result[i] = {
      id: item.id,
      value: (item.value || 0).toFixed(2),
      timestamp: new Date(item.date).getTime()
    }
  }
  return result
}
```

### 3. 数据缓存策略
```javascript
// 实现简单的数据缓存
const cache = new Map()
const TTL = 5 * 60 * 1000 // 5分钟缓存

const getCachedData = async (key) => {
  const cached = cache.get(key)
  if (cached && Date.now() - cached.timestamp < TTL) {
    return cached.data
  }
  
  const data = await fetchData(key)
  cache.set(key, {
    data,
    timestamp: Date.now()
  })
  return data
}
```

### 4. 批量数据更新
```javascript
// ❌ 频繁更新
items.forEach(item => {
  updateItem(item)
})

// ✅ 批量更新
const batchUpdate = (items, batchSize = 100) => {
  const batches = []
  for (let i = 0; i < items.length; i += batchSize) {
    batches.push(items.slice(i, i + batchSize))
  }
  
  return batches.reduce(async (promise, batch) => {
    await promise
    return updateItems(batch)
  }, Promise.resolve())
}
```

### 5. 数据预处理
```javascript
// 在数据获取时预处理
const preprocessData = (data) => {
  if (!Array.isArray(data)) return []
  
  return data.reduce((acc, item) => {
    if (!item || typeof item !== 'object') return acc
    
    // 只保留需要的字段
    acc.push({
      id: item.id,
      value: (item.value || 0).toFixed(2),
      label: item.label || ''
    })
    
    return acc
  }, [])
}
```

### 6. 响应式数据优化
```javascript
// ❌ 可能导致不必要的响应式更新
const data = ref({
  items: [],
  metadata: {},
  timestamps: []
})

// ✅ 优化响应式数据结构
const items = ref([])
const metadata = ref({})
const timestamps = ref([])

// 仅在必要时更新
const updateData = (newData) => {
  if (newData.items) items.value = newData.items
  if (newData.metadata) metadata.value = newData.metadata
  if (newData.timestamps) timestamps.value = newData.timestamps
}
```

### 7. 计算属性缓存
```javascript
// 使用计算属性缓存处理结果
const processedData = computed(() => {
  return items.value.map(item => ({
    ...item,
    formattedValue: (item.value || 0).toFixed(2)
  }))
})
```

这些优化策略可以：
- 减少不必要的计算和内存使用
- 提高数据处理效率
- 优化应用响应性能
- 改善用户体验 