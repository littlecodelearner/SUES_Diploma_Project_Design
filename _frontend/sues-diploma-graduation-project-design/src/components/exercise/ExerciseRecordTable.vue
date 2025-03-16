<script setup>
import { ref, watch, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, Search } from '@element-plus/icons-vue'
import { getExerciseRecords, deleteExerciseRecord, getExerciseTypeList, batchDeleteExerciseRecords } from '@/api/exercise'
import { useUserStore } from '@/store/user'
import ExerciseRecordUpdateForm from './ExerciseRecordUpdateForm.vue'

const userStore = useUserStore()
const loading = ref(false)
const records = ref([])
const searchKeyword = ref('')

// 分页参数
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 日期范围
const dateRange = ref([])

// 过滤和搜索记录
const filteredRecords = computed(() => {
  let result = records.value

  // 关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(record => {
      return (
        // 搜索运动类型名称
        record.exerciseTypesList.some(type => 
          type.exerciseName.toLowerCase().includes(keyword)
        ) ||
        // 搜索备注
        (record.exerciseNote && 
          record.exerciseNote.toLowerCase().includes(keyword)) ||
        // 搜索其他数值型字段（转为字符串后搜索）
        String(record.duration).includes(keyword) ||
        String(record.distance).includes(keyword) ||
        String(record.caloriesBurned).includes(keyword) ||
        String(record.heartRate).includes(keyword)
      )
    })
  }

  // 更新总条数（移动到这里，确保在过滤后更新总数）
  pagination.value.total = result.length

  // 分页处理
  const start = (pagination.value.currentPage - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  return result.slice(start, end)
})

const emit = defineEmits(['records-update'])

// 获取所有运动记录（用于统计）
const fetchAllRecords = async () => {
  if (!userStore.userId) return
  
  try {
    const response = await getExerciseRecords({
      userId: userStore.userId,
      offset: 0,
      size: 1000, // 设置一个足够大的数字来获取所有数据
      startDate: dateRange.value?.[0] ? new Date(dateRange.value[0]).toISOString() : null,
      endDate: dateRange.value?.[1] ? new Date(dateRange.value[1]).toISOString() : null
    })
    
    if (response.code === 200) {
      const allRecords = response.data || []
      // 发送所有数据用于统计
      emit('records-update', allRecords)
      return allRecords
    }
  } catch (error) {
    console.error('获取运动记录失败:', error)
    return []
  }
}

// 获取运动记录列表（分页）
const fetchRecords = async () => {
  if (!userStore.userId) return
  
  try {
    loading.value = true
    // 先获取所有记录用于统计和搜索
    const allRecords = await fetchAllRecords()
    if (allRecords) {
      records.value = allRecords
      // 立即更新总条数（不依赖computed）
      pagination.value.total = allRecords.length
    }
  } catch (error) {
    console.error('获取运动记录失败:', error)
    ElMessage.error('获取运动记录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 监听日期范围变化
watch(dateRange, () => {
  handleDateRangeChange()
})

// 监听搜索关键词变化
watch(searchKeyword, () => {
  handleSearch()
})

// 处理日期范围变化
const handleDateRangeChange = async () => {
  pagination.value.currentPage = 1
  await fetchRecords()
}

// 处理搜索
const handleSearch = () => {
  pagination.value.currentPage = 1
}

// 处理页码变化
const handlePageChange = (page) => {
  pagination.value.currentPage = page
  // 滚动到表格顶部
  const tableEl = document.querySelector('.exercise-record-table')
  if (tableEl) {
    tableEl.scrollIntoView({ behavior: 'smooth' })
  }
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  // 当每页条数改变时，如果当前页没有数据，则跳转到第一页
  const maxPage = Math.ceil(pagination.value.total / size)
  if (pagination.value.currentPage > maxPage) {
    pagination.value.currentPage = 1
  }
}

// 重置筛选条件
const resetFilters = () => {
  dateRange.value = []
  searchKeyword.value = ''
  pagination.value.currentPage = 1
  fetchRecords()
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 处理删除记录
const handleDelete = async (record) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条运动记录吗？此操作不可恢复。',
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await deleteExerciseRecord(record.exerciseRecordId)
    
    if (response.code === 200) {
      ElMessage.success('删除成功')
      fetchRecords()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error === 'cancel') return
    console.error('删除运动记录失败:', error)
    ElMessage.error('删除失败，请稍后重试')
  }
}

// 更新表单ref
const updateFormRef = ref(null)

// 处理更新按钮点击
const handleUpdate = (row) => {
  updateFormRef.value?.open(row)
}

// 处理更新成功
const handleUpdateSuccess = () => {
  fetchRecords()
}

// 添加运动类型列表状态
const exerciseTypes = ref([])

// 获取运动类型列表
const fetchExerciseTypes = async () => {
  try {
    const response = await getExerciseTypeList()
    if (response.code === 200) {
      exerciseTypes.value = response.data
    } else {
      console.error('获取运动类型列表失败:', response.message)
    }
  } catch (error) {
    console.error('获取运动类型列表失败:', error)
  }
}

// 在组件初始化时获取运动类型列表
onMounted(() => {
  fetchExerciseTypes()
  fetchRecords()
})

// 初始化
fetchRecords()

// 暴露方法给父组件
defineExpose({
  fetchRecords
})

// 选中的记录
const selectedRecords = ref([])

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedRecords.value = selection
}

// 处理批量删除
const handleBatchDelete = async () => {
  if (!selectedRecords.value.length) return

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRecords.value.length} 条运动记录吗？此操作不可恢复。`,
      '批量删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const recordIds = selectedRecords.value.map(record => record.exerciseRecordId)
    const response = await batchDeleteExerciseRecords(recordIds)
    
    if (response.code === 200) {
      ElMessage.success('批量删除成功')
      fetchRecords()
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error) {
    if (error === 'cancel') return
    console.error('批量删除运动记录失败:', error)
    ElMessage.error('批量删除失败，请稍后重试')
  }
}

// 监听记录数据变化
watch(records, (newRecords) => {
  // 如果当前页没有数据，且不是第一页，则跳转到第一页
  if (filteredRecords.value.length === 0 && pagination.value.currentPage > 1) {
    pagination.value.currentPage = 1
  }
}, { deep: true })
</script>

<template>
  <div class="exercise-record-table-container">
    <!-- 筛选和搜索区域 -->
    <div class="filter-section">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索运动类型、备注等信息"
        clearable
        @input="handleSearch"
        style="width: 300px"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        @change="handleDateRangeChange"
      />
      
      <el-button @click="resetFilters" :disabled="!dateRange.length && !searchKeyword">
        重置筛选
      </el-button>

      <el-button 
        type="danger" 
        :icon="Delete"
        :disabled="!selectedRecords.length"
        @click="handleBatchDelete"
      >
        批量删除
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table
      :data="filteredRecords"
      v-loading="loading"
      style="width: 100%"
      border
      size="small"
      @selection-change="handleSelectionChange"
      class="compact-table"
    >
      <!-- 添加多选列 -->
      <el-table-column
        type="selection"
        width="40"
        align="center"
        fixed="left"
      />

      <el-table-column
        type="index"
        label="序号"
        width="50"
        align="center"
        fixed="left"
      />

      <el-table-column 
        label="运动类型" 
        min-width="120"
        align="center"
        fixed="left"
      >
        <template #default="{ row }">
          <div class="exercise-types">
            <el-tag
              v-for="type in row.exerciseTypesList"
              :key="type.exerciseTypeId"
              class="exercise-type-tag"
              size="small"
              effect="plain"
            >
              {{ type.exerciseName }}
            </el-tag>
          </div>
        </template>
      </el-table-column>

      <el-table-column
        prop="duration"
        label="时长"
        width="80"
        align="center"
        sortable
      >
        <template #default="{ row }">
          <span class="numeric-cell">{{ row.duration }}分</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="distance"
        label="距离"
        width="80"
        align="center"
        sortable
      >
        <template #default="{ row }">
          <span class="numeric-cell">{{ row.distance ? row.distance.toFixed(1) : '-' }}km</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="caloriesBurned"
        label="消耗"
        width="80"
        align="center"
        sortable
      >
        <template #default="{ row }">
          <span class="numeric-cell">{{ row.caloriesBurned || '-' }}kcal</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="heartRate"
        label="心率"
        width="80"
        align="center"
        sortable
      >
        <template #default="{ row }">
          <span class="numeric-cell">{{ row.heartRate || '-' }}bpm</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="exerciseNote"
        label="备注"
        min-width="150"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          <span class="note-cell">{{ row.exerciseNote || '-' }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="exerciseDate"
        label="日期"
        width="150"
        align="center"
        sortable
      >
        <template #default="{ row }">
          <span class="date-cell">{{ formatDateTime(row.exerciseDate) }}</span>
        </template>
      </el-table-column>

      <el-table-column
        label="操作"
        width="80"
        align="center"
        fixed="right"
      >
        <template #default="{ row }">
          <div class="operation-buttons">
            <el-button
              type="primary"
              :icon="Edit"
              size="small"
              circle
              class="mini-btn"
              @click="handleUpdate(row)"
            />
            <el-button
              type="danger"
              :icon="Delete"
              size="small"
              circle
              class="mini-btn"
              @click="handleDelete(row)"
            />
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页器 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        :disabled="loading"
        :hide-on-single-page="false"
        small
      />
    </div>

    <!-- 添加更新表单组件 -->
    <exercise-record-update-form
      ref="updateFormRef"
      :exercise-types="exerciseTypes"
      @update-success="handleUpdateSuccess"
    />
  </div>
</template>

<style>
/* 全局样式，确保优先级 */
:root {
  --table-header-height: 40px;
  --table-row-height: 40px;
  --table-font-size: 14px;
  --table-header-bg: var(--el-color-primary-light-9);
  --table-header-color: var(--el-color-primary-dark-2);
  --table-hover-bg: var(--el-color-primary-light-9);
}

/* 容器样式 */
.exercise-record-table-container {
  width: 100%;
  margin-top: 16px;
}

/* 表格基础样式 */
.exercise-record-table-container :deep(.el-table) {
  --el-table-header-bg-color: var(--table-header-bg) !important;
  --el-table-header-text-color: var(--table-header-color) !important;
  --el-table-row-hover-bg-color: var(--table-hover-bg) !important;
  font-size: var(--table-font-size) !important;
  border-radius: 8px !important;
  overflow: hidden !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05) !important;
}

/* 表头样式 */
.exercise-record-table-container :deep(.el-table__header-wrapper th) {
  background-color: var(--table-header-bg) !important;
  color: var(--table-header-color) !important;
  font-weight: 600 !important;
  height: var(--table-header-height) !important;
  line-height: var(--table-header-height) !important;
  padding: 4px 0 !important;
}

/* 表格行样式 */
.exercise-record-table-container :deep(.el-table__row) {
  height: var(--table-row-height) !important;
}

/* 表格单元格样式 */
.exercise-record-table-container :deep(.el-table__cell) {
  padding: 4px 0 !important;
  height: var(--table-row-height) !important;
  line-height: var(--table-row-height) !important;
}

/* 运动类型标签容器 */
.exercise-record-table-container .exercise-types {
  display: flex !important;
  gap: 4px !important;
  flex-wrap: wrap !important;
  justify-content: center !important;
  padding: 4px 0 !important;
}

/* 运动类型标签 */
.exercise-record-table-container :deep(.exercise-type-tag) {
  margin: 2px !important;
  font-size: 12px !important;
  border-radius: 4px !important;
  padding: 0 8px !important;
  height: 20px !important;
  line-height: 18px !important;
  background-color: var(--el-color-primary-light-9) !important;
  border-color: var(--el-color-primary-light-7) !important;
  color: var(--el-color-primary-dark-2) !important;
}

/* 数字单元格 */
.exercise-record-table-container .numeric-cell {
  font-family: 'Roboto Mono', monospace !important;
  font-size: 13px !important;
  font-weight: 500 !important;
  color: var(--el-text-color-primary) !important;
}

/* 操作按钮容器 */
.exercise-record-table-container :deep(.operation-buttons) {
  display: flex !important;
  gap: 4px !important;
  justify-content: center !important;
  align-items: center !important;
}

/* 操作按钮 */
.exercise-record-table-container :deep(.operation-buttons .mini-btn) {
  padding: 0 !important;
  width: 24px !important;
  height: 24px !important;
  min-height: 24px !important;
}

.exercise-record-table-container :deep(.operation-buttons .mini-btn .el-icon) {
  font-size: 12px !important;
}

/* 筛选区域 */
.exercise-record-table-container .filter-section {
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

/* 分页容器 */
.exercise-record-table-container .pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .exercise-record-table-container .filter-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .exercise-record-table-container .filter-section .el-input {
    width: 100% !important;
  }
  
  .exercise-record-table-container :deep(.el-table__fixed),
  .exercise-record-table-container :deep(.el-table__fixed-right) {
    display: none !important;
  }
}
</style> 