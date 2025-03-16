# 测试规范

## 单元测试
使用 Vitest 进行单元测试。

### 组件测试示例
```javascript
import { mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import HealthProfileCard from '@/components/HealthProfileCard.vue'

describe('HealthProfileCard', () => {
  it('显示加载状态', () => {
    const wrapper = mount(HealthProfileCard, {
      props: { loading: true }
    })
    expect(wrapper.find('.loading').exists()).toBe(true)
  })

  it('触发编辑事件', async () => {
    const wrapper = mount(HealthProfileCard)
    await wrapper.find('.edit-button').trigger('click')
    expect(wrapper.emitted('edit')).toBeTruthy()
  })
})
```

## E2E测试
使用 Cypress 进行端到端测试。

### 测试用例示例
```javascript
describe('健康档案功能', () => {
  it('创建健康档案', () => {
    cy.login('testuser')
    cy.visit('/dashboard')
    cy.get('.create-profile-button').click()
    cy.get('#medicalHistory').type('无')
    cy.get('#allergyHistory').type('无')
    cy.get('form').submit()
    cy.contains('创建成功').should('be.visible')
  })
}) 

## 数据处理测试

### 1. 空值处理测试
```javascript
import { describe, it, expect } from 'vitest'
import { formatNumber } from '@/utils/format'

describe('数值格式化', () => {
  it('处理 undefined 值', () => {
    expect(formatNumber(undefined)).toBe('0')
  })

  it('处理 null 值', () => {
    expect(formatNumber(null)).toBe('0')
  })

  it('处理有效数值', () => {
    expect(formatNumber(123.456, 2)).toBe('123.46')
  })

  it('处理零值', () => {
    expect(formatNumber(0)).toBe('0')
  })

  it('处理负数', () => {
    expect(formatNumber(-123.456, 2)).toBe('-123.46')
  })
})
```

### 2. 数据转换测试
```javascript
describe('数据转换', () => {
  const mockData = {
    value: 123.456,
    unit: 'kg'
  }

  it('转换完整数据', () => {
    const result = transformData(mockData)
    expect(result).toEqual({
      value: '123.46',
      unit: 'kg'
    })
  })

  it('处理缺失字段', () => {
    const result = transformData({})
    expect(result).toEqual({
      value: '0.00',
      unit: ''
    })
  })

  it('处理无效数据', () => {
    const result = transformData(null)
    expect(result).toEqual({
      value: '0.00',
      unit: ''
    })
  })
})
```

### 3. 批量数据处理测试
```javascript
describe('批量数据处理', () => {
  const mockItems = [
    { id: 1, value: 100 },
    { id: 2, value: null },
    { id: 3, value: undefined },
    { id: 4, value: 200 }
  ]

  it('正确处理数组数据', () => {
    const result = processItems(mockItems)
    expect(result).toHaveLength(4)
    expect(result[0].value).toBe('100.00')
    expect(result[1].value).toBe('0.00')
    expect(result[2].value).toBe('0.00')
    expect(result[3].value).toBe('200.00')
  })

  it('处理空数组', () => {
    const result = processItems([])
    expect(result).toHaveLength(0)
  })

  it('处理无效输入', () => {
    expect(processItems(null)).toEqual([])
    expect(processItems(undefined)).toEqual([])
  })
})
```

### 4. 异步数据处理测试
```javascript
describe('异步数据处理', () => {
  it('成功获取并处理数据', async () => {
    const mockData = {
      code: 200,
      data: {
        value: 100,
        unit: 'kg'
      }
    }

    // 模拟 API 调用
    const mockApi = vi.fn().mockResolvedValue(mockData)
    
    const result = await processAsyncData(mockApi)
    expect(result).toEqual({
      value: '100.00',
      unit: 'kg'
    })
  })

  it('处理 API 错误', async () => {
    const mockApi = vi.fn().mockRejectedValue(new Error('API Error'))
    
    await expect(processAsyncData(mockApi))
      .rejects
      .toThrow('API Error')
  })

  it('处理空响应', async () => {
    const mockApi = vi.fn().mockResolvedValue(null)
    
    const result = await processAsyncData(mockApi)
    expect(result).toEqual({
      value: '0.00',
      unit: ''
    })
  })
})
```

### 5. 组件数据处理测试
```javascript
import { mount } from '@vue/test-utils'
import DataDisplay from '@/components/DataDisplay.vue'

describe('DataDisplay 组件', () => {
  it('正确显示格式化的数值', () => {
    const wrapper = mount(DataDisplay, {
      props: {
        value: 123.456
      }
    })
    
    expect(wrapper.find('.value').text()).toBe('123.46')
  })

  it('处理无效属性值', () => {
    const wrapper = mount(DataDisplay, {
      props: {
        value: null
      }
    })
    
    expect(wrapper.find('.value').text()).toBe('0.00')
  })

  it('响应属性变化', async () => {
    const wrapper = mount(DataDisplay, {
      props: {
        value: 100
      }
    })
    
    await wrapper.setProps({ value: 200 })
    expect(wrapper.find('.value').text()).toBe('200.00')
  })
})
```

这些测试用例帮助确保：
- 数据处理逻辑的正确性
- 边界情况的处理
- 错误处理的可靠性
- 组件渲染的准确性 