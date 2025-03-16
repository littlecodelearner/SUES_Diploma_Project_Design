<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDate } from '@/utils/date'
import { deleteHealthDataBatch } from '@/api/healthData'
import HealthDataForm from './HealthDataForm.vue'
import { Delete, Plus, Search, Timer, TrendCharts, ScaleToOriginal, Histogram } from '@element-plus/icons-vue'

const props = defineProps({
  loading: {
    type: Boolean,
    default: false
  },
  data: {
    type: Array,
    default: () => []
  },
  pagination: {
    type: Object,
    required: true
  },
  userId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['page-change', 'size-change', 'refresh', 'add-record'])

// 搜索功能
const searchKeyword = ref('')
const filteredData = computed(() => {
  if (!searchKeyword.value) return props.data
  const keyword = searchKeyword.value.toLowerCase()
  return props.data.filter(item => 
    formatDate(item.measurementDate).toLowerCase().includes(keyword) ||
    item.heartRate?.toString().includes(keyword) ||
    item.height?.toString().includes(keyword) ||
    item.weight?.toString().includes(keyword) ||
    item.bmi?.toString().includes(keyword)
  )
})

// 统计数据
const latestHeartRate = computed(() => {
  const latest = [...props.data].sort((a, b) => 
    new Date(b.measurementDate) - new Date(a.measurementDate)
  )[0]
  return latest?.heartRate
})

const latestBMI = computed(() => {
  const latest = [...props.data].sort((a, b) => 
    new Date(b.measurementDate) - new Date(a.measurementDate)
  )[0]
  return latest?.bmi
})

const latestWeight = computed(() => {
  const latest = [...props.data].sort((a, b) => 
    new Date(b.measurementDate) - new Date(a.measurementDate)
  )[0]
  return latest?.weight
})

const totalRecords = computed(() => props.pagination.total)

// 状态样式
const getHeartRateClass = (heartRate) => {
  if (!heartRate) return ''
  if (heartRate < 60) return 'status-low'
  if (heartRate > 100) return 'status-high'
  return 'status-normal'
}

const getBMIClass = (bmi) => {
  if (!bmi) return ''
  if (bmi < 18.5) return 'status-low'
  if (bmi > 24.9) return 'status-high'
  return 'status-normal'
}

// 编辑相关
const showEditForm = ref(false)
const editingData = ref(null)

// 处理编辑
const handleEdit = (row) => {
  editingData.value = { ...row }
  showEditForm.value = true
}

// 编辑成功
const handleEditSuccess = () => {
  showEditForm.value = false
  editingData.value = null
  emit('refresh')
}

// 多选相关
const selectedRows = ref([])

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要删除的数据')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 条健康数据记录吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        draggable: true,
        icon: Delete
      }
    )

    const healthDataIds = selectedRows.value.map(row => row.healthDataId)
    const response = await deleteHealthDataBatch(healthDataIds)

    if (response.code === 200) {
      ElMessage.success('删除成功')
      selectedRows.value = []
      emit('refresh') // 刷新数据
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

// 添加新的状态判断函数
const getBMIStatus = (bmi) => {
  if (!bmi) return '未知'
  if (bmi < 18.5) return '偏瘦'
  if (bmi > 24.9) return '偏胖'
  return '正常'
}

const getBMITagType = (bmi) => {
  if (!bmi) return 'info'
  if (bmi < 18.5) return 'warning'
  if (bmi > 24.9) return 'danger'
  return 'success'
}

const getHeartRateStatus = (heartRate) => {
  if (!heartRate) return '未知'
  if (heartRate < 60) return '偏慢'
  if (heartRate > 100) return '偏快'
  return '正常'
}

const getHeartRateTagType = (heartRate) => {
  if (!heartRate) return 'info'
  if (heartRate < 60) return 'warning'
  if (heartRate > 100) return 'danger'
  return 'success'
}
</script>

<template>
  <div class="health-data-table">
    <!-- 数据统计卡片 -->
    <div class="stats-container">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Timer /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ latestHeartRate || '--' }}</div>
          <div class="stat-label">最新心率 (bpm)</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ latestBMI?.toFixed(1) || '--' }}</div>
          <div class="stat-label">最新 BMI</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><ScaleToOriginal /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ latestWeight || '--' }}</div>
          <div class="stat-label">最新体重 (kg)</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Histogram /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ totalRecords }}</div>
          <div class="stat-label">记录总数</div>
        </div>
      </div>
    </div>

    <!-- 批量操作工具栏 -->
    <div class="table-toolbar" v-if="data.length">
      <div class="toolbar-left">
        <el-button
          type="primary"
          :icon="Plus"
          @click="$emit('add-record')"
        >
          添加记录
        </el-button>
        <el-button
          type="danger"
          :disabled="!selectedRows.length"
          @click="handleBatchDelete"
          :icon="Delete"
        >
          批量删除
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索记录..."
          clearable
          :prefix-icon="Search"
        />
        <span class="selection-info" v-if="selectedRows.length">
          已选择 {{ selectedRows.length }} 项
        </span>
      </div>
    </div>

    <div class="table-container">
      <el-table
        :data="filteredData"
        :loading="loading"
        style="width: 100%"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          type="selection"
          width="45"
          align="center"
          fixed="left"
        />

        <el-table-column
          prop="measurementDate"
          label="测量日期"
          min-width="160"
          sortable
          fixed="left"
        >
          <template #default="{ row }">
            {{ formatDate(row.measurementDate) }}
          </template>
        </el-table-column>
        
        <el-table-column
          prop="heartRate"
          label="心率"
          min-width="120"
          align="center"
        >
          <template #header>
            <div class="column-header">
              <span>心率</span>
              <span class="unit">(bpm)</span>
            </div>
          </template>
          <template #default="{ row }">
            <span :class="getHeartRateClass(row.heartRate)">
              {{ row.heartRate }}
            </span>
          </template>
        </el-table-column>
        
        <el-table-column
          prop="height"
          label="身高"
          min-width="120"
          align="center"
        >
          <template #header>
            <div class="column-header">
              <span>身高</span>
              <span class="unit">(cm)</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column
          prop="weight"
          label="体重"
          min-width="120"
          align="center"
        >
          <template #header>
            <div class="column-header">
              <span>体重</span>
              <span class="unit">(kg)</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column
          prop="bmi"
          label="BMI"
          min-width="120"
          align="center"
        >
          <template #default="{ row }">
            <span :class="getBMIClass(row.bmi)">
              {{ row.bmi?.toFixed(2) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column
          label="BMI 状态"
          min-width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag
              :type="getBMITagType(row.bmi)"
              size="small"
              effect="light"
            >
              {{ getBMIStatus(row.bmi) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          label="心率状态"
          min-width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag
              :type="getHeartRateTagType(row.heartRate)"
              size="small"
              effect="light"
            >
              {{ getHeartRateStatus(row.heartRate) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          label="操作"
          width="100"
          align="center"
          fixed="right"
        >
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="$emit('size-change', $event)"
        @current-change="$emit('page-change', $event)"
      />
    </div>

    <!-- 编辑表单弹窗 -->
    <el-dialog
      v-model="showEditForm"
      title="编辑健康数据"
      width="500px"
      class="health-data-dialog"
      destroy-on-close
      append-to-body
      :modal-append-to-body="false"
      align-center
    >
      <health-data-form
        v-if="editingData"
        :user-id="userId"
        :edit-mode="true"
        :initial-data="editingData"
        @success="handleEditSuccess"
        @cancel="showEditForm = false"
      />
    </el-dialog>
  </div>
</template>

<style scoped>
.health-data-table {
  margin-top: 20px;
  height: 100%;
  min-height: 600px;
  display: flex;
  flex-direction: column;
  background: var(--el-bg-color);
  border-radius: 12px;
  box-shadow: 0 4px 24px 0 rgba(0, 0, 0, 0.08);
}

.stats-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  padding: 20px;
  background: linear-gradient(135deg, var(--el-color-primary-light-8) 0%, var(--el-color-primary-light-9) 100%);
  border-radius: 12px 12px 0 0;
}

.stat-card {
  background: var(--el-bg-color);
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px 0 rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: var(--el-color-primary-light-8);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-color-primary);
  font-size: 24px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}

.table-toolbar {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  border-bottom: 1px solid var(--el-border-color-light);
}

.toolbar-left, .toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.table-container {
  flex: 1;
  min-height: 0;
  padding: 20px;
  background: var(--el-bg-color);
}

.column-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.column-header .unit {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

:deep(.el-table__header) {
  background: var(--el-color-primary-light-9);
}

:deep(.el-table__header-row th) {
  background: var(--el-color-primary-light-9);
  color: var(--el-text-color-primary);
  font-weight: 600;
}

:deep(.el-table__row) {
  transition: all 0.3s ease;
}

:deep(.el-table__row:hover) {
  background: var(--el-color-primary-light-9) !important;
}

:deep(.el-tag) {
  width: 100%;
  max-width: 80px;
  text-align: center;
}

.status-normal {
  color: var(--el-color-success);
  font-weight: 600;
}

.status-high {
  color: var(--el-color-danger);
  font-weight: 600;
}

.status-low {
  color: var(--el-color-warning);
  font-weight: 600;
}

.pagination-container {
  padding: 20px;
  background: var(--el-bg-color);
  border-top: 1px solid var(--el-border-color-light);
  border-radius: 0 0 12px 12px;
}

.selection-info {
  padding: 6px 12px;
  background: var(--el-color-primary-light-9);
  border-radius: 4px;
  color: var(--el-color-primary);
  font-size: 14px;
  font-weight: 500;
}

:deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

@media screen and (max-width: 1200px) {
  .stats-container {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 768px) {
  .stats-container {
    grid-template-columns: 1fr;
  }

  .table-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-left, .toolbar-right {
    flex-direction: column;
    width: 100%;
  }

  :deep(.el-input) {
    width: 100%;
  }

  .selection-info {
    text-align: center;
  }
}
</style> 