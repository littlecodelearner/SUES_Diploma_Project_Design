 # 可滚动分页组件集成指南

本文档提供了将`ScrollablePagination`组件集成到系统各页面的详细步骤，以增强用户体验。

## 功能概述

`ScrollablePagination`组件是对Element Plus分页器的增强，它提供：

1. 自动滚动功能：分页切换时自动滚动到适当位置
2. 信息提示功能：通过图标和提示文本提供额外上下文信息
3. 可自定义滚动目标和偏移量

## 集成步骤

### 1. 导入组件

在需要集成该组件的页面中添加导入语句：

```javascript
import ScrollablePagination from '@/components/common/ScrollablePagination.vue'
```

### 2. 替换现有分页器

将现有的`el-pagination`组件替换为`ScrollablePagination`组件：

```vue
<!-- 修改前 -->
<el-pagination
  v-model:current-page="searchForm.current"
  v-model:page-size="searchForm.size"
  :page-sizes="[15, 30, 50]"
  :total="total"
  layout="total, sizes, prev, pager, next, jumper"
  @size-change="handleSizeChange"
  @current-change="handleCurrentChange"
/>

<!-- 修改后 -->
<scrollable-pagination
  v-model:current-page="searchForm.current"
  v-model:page-size="searchForm.size"
  :page-sizes="[15, 30, 50]"
  :total="total"
  layout="total, sizes, prev, pager, next, jumper"
  @size-change="handleSizeChange"
  @current-change="handleCurrentChange"
  :show-info="true"
  :tooltip-content="'提示：记录可能受筛选条件影响，实际显示的记录数量可能少于总数。'"
  :text-content="`总共 ${total} 条记录`"
  :scroll-target="'.container-class'"
  :top-offset="100"
/>
```

### 3. 配置滚动目标

为`ScrollablePagination`组件设置正确的滚动目标选择器（`.container-class`应替换为实际的容器类名）。

通常，滚动目标应该设置为页面或卡片的主容器元素，例如：

- 在FoodNutrition页面中: `:scroll-target="'.food-management'"`
- 在DietRecord页面中: `:scroll-target="'.diet-record-card'"`
- 在HealthData页面中: `:scroll-target="'.health-data-container'"`

### 4. 调整CSS样式

确保分页容器有合适的样式：

```css
.pagination-container {
  margin-top: 32px;
  padding: 16px;
  background: var(--el-bg-color);
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  width: 100%;
}

/* 适配ScrollablePagination组件样式 */
:deep(.scrollable-pagination-container) {
  width: 100%;
}

:deep(.pagination-wrapper) {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 保留原有的分页样式 */
:deep(.el-pagination) {
  justify-content: flex-end;
  padding: 0;
}
```

### 5. 自定义提示内容

根据页面类型自定义提示内容：

- **食物营养管理页面**: `"提示：食物列表可能受筛选条件影响，实际显示的记录数量可能少于总数。"`
- **饮食记录页面**: `"提示：饮食记录可能因为筛选条件影响，实际显示的记录数量可能少于总数。"`
- **运动记录页面**: `"提示：运动记录可能因为时间和类型筛选，实际显示的记录数量可能少于总数。"`

## 应用实例

### 食物营养管理页面

```vue
<!-- 在FoodNutrition.vue中 -->
<div class="pagination-container">
  <scrollable-pagination
    v-model:current-page="searchForm.current"
    v-model:page-size="searchForm.size"
    :page-sizes="[15, 30, 50]"
    :total="total"
    layout="total, sizes, first, prev, pager, next, last, jumper"
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
    :show-info="true"
    :tooltip-content="'提示：食物列表可能受筛选条件影响，实际显示的记录数量可能少于总数。'"
    :text-content="`总共 ${total} 种食物`"
    :scroll-target="'.food-management'"
    :top-offset="100"
  />
</div>
```

### 饮食记录页面

```vue
<!-- 在DietRecordList.vue中 -->
<div class="pagination-container">
  <scrollable-pagination
    v-model:current-page="currentPage"
    v-model:page-size="pageSize"
    :page-sizes="[15, 25, 35, 50]"
    :total="total"
    layout="total, sizes, prev, pager, next, jumper"
    background
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
    :show-info="true"
    :tooltip-content="'提示：饮食记录可能因为筛选条件影响，实际显示的记录数量可能少于总数。'"
    :text-content="`总共 ${total} 条记录`"
    :scroll-target="'.diet-record-list'"
    :top-offset="100"
  />
</div>
```

## 注意事项

1. 确保滚动目标元素有正确的定位上下文（position: relative）
2. 适当调整top-offset以避免被固定导航栏遮挡
3. 在暗黑模式下样式会自动适配，无需额外配置