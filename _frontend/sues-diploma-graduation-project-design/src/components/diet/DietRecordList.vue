<template>
  <div class="diet-record-list">
    <!-- 筛选工具栏 -->
    <div class="filter-toolbar">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :shortcuts="dateShortcuts"
            value-format="YYYY-MM-DDTHH:mm:ss.SSSZ"
            @change="handleDateRangeChange"
          />
        </el-col>
        <el-col :span="4">
          <!-- 使用新的通用进餐类型选择组件 -->
          <meal-type-select
            v-model="selectedMealType"
            placeholder="用餐类型"
            @change="handleFilterChange"
            ref="mealTypeSelectRef"
          />
        </el-col>
        <el-col :span="12" class="toolbar-right">
          <el-button-group>
            <el-button
              :icon="Refresh"
              @click="refreshList"
              :loading="loading"
            >
              刷新
            </el-button>
            <el-button
              v-if="selectedRecords.length > 0"
              type="warning"
              :icon="Edit"
              @click="handleBatchEdit"
            >
              批量修改
            </el-button>
            <el-button
              v-if="selectedRecords.length > 0"
              type="danger"
              :icon="Delete"
              @click="handleBatchDelete"
            >
              批量删除
            </el-button>
            <el-button
              type="primary"
              :icon="Plus"
              @click="$emit('add')"
            >
              添加记录
            </el-button>
          </el-button-group>
        </el-col>
      </el-row>
    </div>

    <!-- 记录列表 -->
    <el-row :gutter="20" v-loading="loading">
      <template v-if="records && records.length > 0">
        <el-col 
          v-for="record in records"
          :key="record.dietId"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
          :xl="6"
          class="record-item"
        >
          <el-card 
            shadow="hover" 
            class="record-card"
            :class="{ 'is-selected': selectedRecords.includes(record.dietId) }"
          >
            <template #header>
              <div class="card-header">
                <div class="header-row">
                  <div class="header-left">
                    <el-checkbox
                      v-model="selectedRecords"
                      :label="record.dietId"
                      @change="handleSelectionChange"
                    />
                    <el-tag :type="getMealTypeTag(record.mealType)">
                      {{ getMealTypeLabel(record.mealType) }}
                    </el-tag>
                  </div>
                  <div class="header-date">
                    <span class="meal-time">
                      {{ formatDateTime(record.mealTime) }}
                    </span>
                  </div>
                </div>
                <div class="header-actions">
                  <el-tooltip content="修改" placement="top">
                    <el-button
                      type="primary"
                      :icon="Edit"
                      size="small"
                      @click="$emit('edit', record)"
                    >
                      修改
                    </el-button>
                  </el-tooltip>
                  <el-tooltip content="删除" placement="top">
                    <el-button
                      type="danger"
                      :icon="Delete"
                      size="small"
                      @click="handleDelete(record)"
                    >
                      删除
                    </el-button>
                  </el-tooltip>
                </div>
              </div>
            </template>

            <!-- 食物列表 -->
            <div class="foods-list">
              <div 
                v-for="food in record.foodsDetailsDTOList"
                :key="food.foodId"
                class="food-item"
              >
                <div class="food-info">
                  <span class="food-name">{{ food.foodName }}</span>
                  <span class="food-quantity">{{ food.quantity }}g</span>
                </div>
                <el-tooltip
                  effect="dark"
                  placement="top"
                  :content="getNutritionInfo(food, food.quantity)"
                >
                  <div class="nutrition-bar">
                    <div 
                      class="nutrition-item protein"
                      :style="{ width: getNutritionPercentage(food.protein, food.quantity) + '%' }"
                    />
                    <div 
                      class="nutrition-item fat"
                      :style="{ width: getNutritionPercentage(food.fat, food.quantity) + '%' }"
                    />
                    <div 
                      class="nutrition-item carbs"
                      :style="{ width: getNutritionPercentage(food.carbohydrates, food.quantity) + '%' }"
                    />
                  </div>
                </el-tooltip>
              </div>
            </div>

            <!-- 总计营养信息 -->
            <div class="total-nutrition">
              <div class="nutrition-total">
                <span class="label">总热量</span>
                <span class="value">{{ calculateTotalCalories(record) }} kcal</span>
              </div>
              <div class="nutrition-details">
                <span>蛋白质: {{ calculateTotalNutrition(record, 'protein') }}g</span>
                <span>脂肪: {{ calculateTotalNutrition(record, 'fat') }}g</span>
                <span>碳水: {{ calculateTotalNutrition(record, 'carbohydrates') }}g</span>
              </div>
            </div>

            <!-- 备注信息 -->
            <div v-if="record.mealNote || record.mealPlace" class="record-notes">
              <div v-if="record.mealPlace" class="meal-place">
                <el-icon><Location /></el-icon>
                {{ record.mealPlace }}
              </div>
              <div v-if="record.mealNote" class="meal-note">
                <el-icon><Memo /></el-icon>
                {{ record.mealNote }}
              </div>
            </div>
          </el-card>
        </el-col>
      </template>
      <template v-else>
        <el-col :span="24">
          <el-empty 
            description="暂无饮食记录" 
            :image-size="200"
          >
            <el-button type="primary" @click="$emit('add')">
              添加记录
            </el-button>
          </el-empty>
        </el-col>
      </template>
    </el-row>

    <!-- 分页器 -->
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
  </div>
</template>

<script setup>
import {computed, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Delete, Edit, Location, Memo, Plus, Refresh} from '@element-plus/icons-vue'
import {deleteDietRecordInBulk} from '@/api/diet'
import {formatDate} from '@/utils/date'
import MealTypeSelect from '@/components/common/MealTypeSelect.vue'
import ScrollablePagination from '@/components/common/ScrollablePagination.vue'

const props = defineProps({
  loading: {
    type: Boolean,
    default: false
  },
  records: {
    type: Array,
    default: () => []
  },
  total: {
    type: Number,
    default: 0
  },
  currentPage: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 15
  }
})

const emit = defineEmits([
  'update:currentPage',
  'update:pageSize',
  'refresh',
  'add',
  'edit',
  'batch-edit',
  'selection-change',
  'size-change',
  'page-change'
])

// 分页相关 - 使用computed同步父组件的值
const currentPage = computed({
  get: () => props.currentPage,
  set: (val) => emit('update:currentPage', val)
})

const pageSize = computed({
  get: () => props.pageSize,
  set: (val) => emit('update:pageSize', val)
})

// 筛选相关
const dateRange = ref(null)
const selectedMealType = ref('')
const mealTypeSelectRef = ref(null)

// 日期快捷选项
const dateShortcuts = [
  {
    text: '最近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    }
  },
  {
    text: '最近一月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    }
  }
]

// 获取餐食类型标签样式
const getMealTypeTag = (type) => {
  // 使用MealTypeSelect组件提供的方法
  return mealTypeSelectRef.value?.getMealTypeTag(type) || ''
}

// 获取餐食类型显示文本
const getMealTypeLabel = (type) => {
  // 使用MealTypeSelect组件提供的方法
  return mealTypeSelectRef.value?.getMealTypeLabel(type) || type
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  return formatDate(datetime, 'YYYY-MM-DD HH:mm')
}

// 计算营养成分百分比
const getNutritionPercentage = (value, quantity) => {
  if (!value || !quantity) return 0
  const total = value * quantity / 100
  return Math.min(total * 2, 100) // 将数值映射到0-100的范围
}

// 获取营养成分提示信息
const getNutritionInfo = (food, quantity) => {
  const factor = quantity / 100
  return `
    热量: ${(food.calories * factor).toFixed(1)}kcal
    蛋白质: ${(food.protein * factor).toFixed(1)}g
    脂肪: ${(food.fat * factor).toFixed(1)}g
    碳水: ${(food.carbohydrates * factor).toFixed(1)}g
    水分: ${(food.water * factor).toFixed(1)}ml
  `
}

// 计算总热量
const calculateTotalCalories = (record) => {
  return record.foodsDetailsDTOList.reduce((total, food) => {
    return total + (food.calories * food.quantity / 100)
  }, 0).toFixed(1)
}

// 计算总营养成分
const calculateTotalNutrition = (record, nutrient) => {
  return record.foodsDetailsDTOList.reduce((total, food) => {
    return total + (food[nutrient] * food.quantity / 100)
  }, 0).toFixed(1)
}

// 处理删除
const handleDelete = async (record) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条饮食记录吗？此操作不可恢复！',
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await deleteDietRecordInBulk({
      dietIdList: [record.dietId]
    })

    if (response.code === 200) {
      ElMessage.success('删除成功')
      emit('refresh')
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

// 处理筛选条件变化
const handleFilterChange = () => {
  currentPage.value = 1
  emit('refresh')
}

// 处理日期范围变化
const handleDateRangeChange = () => {
  currentPage.value = 1
  emit('refresh')
}

// 处理分页变化
const handleCurrentChange = (page) => {
  emit('update:currentPage', page)
  emit('page-change', page)
}

// 刷新列表
const refreshList = () => {
  emit('refresh')
}

// 选中的记录
const selectedRecords = ref([])

// 处理选择变化
const handleSelectionChange = () => {
  emit('selection-change', selectedRecords.value)
}

// 处理批量删除
const handleBatchDelete = async () => {
  if (selectedRecords.value.length === 0) {
    ElMessage.warning('请先选择要删除的记录')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRecords.value.length} 条记录吗？此操作不可恢复！`,
      '批量删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await deleteDietRecordInBulk({
      dietIdList: selectedRecords.value
    })

    if (response.code === 200) {
      ElMessage.success(`成功删除 ${selectedRecords.value.length} 条记录`)
      selectedRecords.value = []
      emit('refresh')
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

// 处理批量修改
const handleBatchEdit = () => {
  if (selectedRecords.value.length === 0) {
    ElMessage.warning('请先选择要修改的记录')
    return
  }

  // 找出选中的记录对象
  const selectedRecordObjects = props.records.filter(
    record => selectedRecords.value.includes(record.dietId)
  )
  
  emit('batch-edit', selectedRecordObjects)
}

// 暴露筛选条件
defineExpose({
  getFilters: () => ({
    dateRange: dateRange.value,
    mealType: selectedMealType.value
  })
})

// 处理页面大小变化
const handleSizeChange = (size) => {
  emit('update:pageSize', size)
  emit('size-change', size)
}
</script>

<style scoped>
.diet-record-list {
  padding: 20px 0;
}

.filter-toolbar {
  margin-bottom: 24px;
  background-color: var(--el-bg-color);
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.toolbar-right {
  display: flex;
  justify-content: flex-end;
}

.record-item {
  margin-bottom: 24px;
  padding: 0 8px;
  display: flex;
}

.record-card {
  height: 100%;
  width: 100%;
  transition: all 0.3s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.record-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 0 4px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.header-date {
  margin-left: auto;
  margin-right: 8px;
  flex-shrink: 1;
  overflow: hidden;
  text-overflow: ellipsis;
}

.meal-time {
  color: var(--el-text-color-secondary);
  font-size: 0.9em;
  white-space: nowrap;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  width: 100%;
  flex-direction: row;
}

.header-actions .el-button {
  flex: 1;
  height: 32px;
  padding: 6px 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.foods-list {
  margin: 15px 0;
  max-height: 300px;
  overflow-y: auto;
  padding-right: 4px;
}

.foods-list::-webkit-scrollbar {
  width: 4px;
}

.foods-list::-webkit-scrollbar-thumb {
  background-color: var(--el-border-color);
  border-radius: 4px;
}

.foods-list::-webkit-scrollbar-track {
  background-color: var(--el-fill-color-light);
  border-radius: 4px;
}

.food-item {
  margin-bottom: 12px;
  padding: 12px;
  border-radius: 6px;
  background-color: var(--el-fill-color-light);
}

.food-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  align-items: center;
}

.food-name {
  font-weight: 500;
  font-size: 1.05em;
  max-width: 70%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.food-quantity {
  color: var(--el-text-color-secondary);
  font-size: 0.95em;
  font-weight: 500;
}

.nutrition-bar {
  height: 4px;
  display: flex;
  background-color: var(--el-fill-color);
  border-radius: 2px;
  overflow: hidden;
}

.nutrition-item {
  height: 100%;
  transition: width 0.3s ease;
}

.protein {
  background-color: #67c23a;
}

.fat {
  background-color: #e6a23c;
}

.carbs {
  background-color: #409eff;
}

.total-nutrition {
  margin-top: 18px;
  border-top: 1px solid var(--el-border-color-lighter);
  background-color: var(--el-fill-color-blank);
  border-radius: 6px;
  padding: 15px;
}

.nutrition-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.nutrition-total .label {
  font-weight: 500;
}

.nutrition-total .value {
  font-size: 1.2em;
  color: var(--el-color-primary);
}

.nutrition-details {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  color: var(--el-text-color-secondary);
  font-size: 0.9em;
}

.nutrition-details span {
  flex: 1 0 30%;
  white-space: nowrap;
}

.record-notes {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid var(--el-border-color-lighter);
  font-size: 0.9em;
  color: var(--el-text-color-regular);
}

.meal-place,
.meal-note {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-bottom: 5px;
}

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

:deep(.el-pagination .el-pagination__sizes) {
  margin-right: 16px;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: var(--el-color-primary);
}

:deep(.el-pagination.is-background .el-pager li) {
  margin: 0 4px;
}

/* 响应式布局优化 */
@media screen and (max-width: 1400px) {
  .record-item {
    margin-bottom: 20px;
  }
}

@media screen and (max-width: 768px) {
  .filter-toolbar {
    padding: 12px;
  }

  .filter-toolbar .el-row > .el-col {
    margin-bottom: 12px;
  }

  .toolbar-right {
    justify-content: center;
  }

  .record-item {
    margin-bottom: 16px;
  }

  .pagination-container {
    margin-top: 24px;
    padding: 12px;
  }
}

/* 添加选中状态样式 */
.record-card.is-selected {
  border: 2px solid var(--el-color-primary);
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(64, 158, 255, 0.2);
}

/* 优化复选框样式 */
:deep(.el-checkbox) {
  margin-right: 8px;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
}

:deep(.el-checkbox__inner:hover) {
  border-color: var(--el-color-primary);
}

/* 批量操作按钮样式 */
.toolbar-right .el-button-group {
  display: flex;
  gap: 8px;
}

.toolbar-right .el-button {
  border-radius: 4px;
}
</style> 