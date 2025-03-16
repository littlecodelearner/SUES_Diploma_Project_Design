<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDate, formatISOtoLocal } from '@/utils/date'
import { deleteHealthDataBatch, updateHealthDataInBatch } from '@/api/healthData'
import HealthDataForm from './HealthDataForm.vue'
import HealthDataBatchForm from './HealthDataBatchForm.vue'
import ScrollablePagination from '@/components/common/ScrollablePagination.vue'
import { 
  Delete, Plus, Calendar, Search, Timer, Edit,
  TrendCharts, ScaleToOriginal, 
  ArrowDown, ArrowUp, Warning, InfoFilled 
} from '@element-plus/icons-vue'

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
const searchActive = ref(false)
const filteredData = computed(() => {
  if (!searchKeyword.value) return props.data
  const keyword = searchKeyword.value.toLowerCase()
  searchActive.value = true
  return props.data.filter(item => 
    formatDate(item.measurementDate).toLowerCase().includes(keyword) ||
    item.heartRate?.toString().includes(keyword) ||
    item.height?.toString().includes(keyword) ||
    item.weight?.toString().includes(keyword) ||
    item.bmi?.toString().includes(keyword)
  )
})

// 排序选项
const sortOptions = [
  { label: '日期（最新优先）', value: 'date-desc' },
  { label: '日期（最早优先）', value: 'date-asc' },
  { label: 'BMI（从低到高）', value: 'bmi-asc' },
  { label: 'BMI（从高到低）', value: 'bmi-desc' },
  { label: '心率（从低到高）', value: 'heart-asc' },
  { label: '心率（从高到低）', value: 'heart-desc' },
  { label: '体重（从轻到重）', value: 'weight-asc' },
  { label: '体重（从重到轻）', value: 'weight-desc' }
]
const currentSort = ref('date-desc')

// 排序后的数据
const sortedData = computed(() => {
  const data = [...filteredData.value]
  
  switch (currentSort.value) {
    case 'date-desc':
      return data.sort((a, b) => new Date(b.measurementDate) - new Date(a.measurementDate))
    case 'date-asc':
      return data.sort((a, b) => new Date(a.measurementDate) - new Date(b.measurementDate))
    case 'bmi-asc':
      return data.sort((a, b) => (a.bmi || 0) - (b.bmi || 0))
    case 'bmi-desc':
      return data.sort((a, b) => (b.bmi || 0) - (a.bmi || 0))
    case 'heart-asc':
      return data.sort((a, b) => (a.heartRate || 0) - (b.heartRate || 0))
    case 'heart-desc':
      return data.sort((a, b) => (b.heartRate || 0) - (a.heartRate || 0))
    case 'weight-asc':
      return data.sort((a, b) => (a.weight || 0) - (b.weight || 0))
    case 'weight-desc':
      return data.sort((a, b) => (b.weight || 0) - (a.weight || 0))
    default:
      return data
  }
})

// 健康指标状态判断
const getBMIStatus = (bmi) => {
  if (!bmi) return { text: '未知', type: 'info' }
  // 确保bmi是数字类型
  const bmiValue = typeof bmi === 'string' ? parseFloat(bmi) : bmi
  if (isNaN(bmiValue)) return { text: '未知', type: 'info' }
  
  if (bmiValue < 18.5) return { text: '偏瘦', type: 'warning' }
  if (bmiValue >= 18.5 && bmiValue <= 23.9) return { text: '健康', type: 'success' }
  if (bmiValue >= 24.0 && bmiValue <= 27.9) return { text: '过重', type: 'warning' }
  return { text: '肥胖', type: 'danger' }
}

const getHeartRateStatus = (heartRate) => {
  if (!heartRate) return { text: '未知', type: 'info' }
  // 确保heartRate是数字类型
  const heartRateValue = typeof heartRate === 'string' ? parseInt(heartRate) : heartRate
  if (isNaN(heartRateValue)) return { text: '未知', type: 'info' }
  
  if (heartRateValue < 60) return { text: '偏慢', type: 'warning' }
  if (heartRateValue >= 60 && heartRateValue <= 100) return { text: '正常', type: 'success' }
  return { text: '偏快', type: 'danger' }
}

// 统计数据
const statistics = computed(() => {
  if (!props.data.length) return null

  // 最新记录
  const latest = [...props.data].sort((a, b) => 
    new Date(b.measurementDate) - new Date(a.measurementDate)
  )[0]

  // 计算趋势
  const dataByDate = [...props.data]
    .sort((a, b) => new Date(a.measurementDate) - new Date(b.measurementDate))
  
  let heartRateTrend = 'stable'
  let weightTrend = 'stable'
  let bmiTrend = 'stable'

  if (dataByDate.length >= 2) {
    const current = dataByDate[dataByDate.length - 1]
    const previous = dataByDate[dataByDate.length - 2]
    
    heartRateTrend = current.heartRate > previous.heartRate ? 'up' : 
                     current.heartRate < previous.heartRate ? 'down' : 'stable'
    
    weightTrend = current.weight > previous.weight ? 'up' : 
                  current.weight < previous.weight ? 'down' : 'stable'
    
    bmiTrend = current.bmi > previous.bmi ? 'up' : 
               current.bmi < previous.bmi ? 'down' : 'stable'
  }

  return {
    latestDate: latest.measurementDate,
    heartRate: {
      value: latest.heartRate,
      status: getHeartRateStatus(latest.heartRate),
      trend: heartRateTrend
    },
    weight: {
      value: latest.weight,
      trend: weightTrend
    },
    bmi: {
      value: latest.bmi,
      status: getBMIStatus(latest.bmi),
      trend: bmiTrend
    }
  }
})

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

// 删除记录
const handleDelete = async (records) => {
  // 确保 records 是数组
  const recordsToDelete = Array.isArray(records) ? records : [records]
  const isBatch = recordsToDelete.length > 1

  try {
    await ElMessageBox.confirm(
      isBatch 
        ? `确定要删除选中的 ${recordsToDelete.length} 条健康数据记录吗？此操作不可恢复！`
        : '确定要删除这条健康数据记录吗？此操作不可恢复！',
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        draggable: true,
        icon: Delete
      }
    )

    // 提取要删除的记录ID
    const healthDataIds = recordsToDelete.map(record => record.healthDataId)
    const response = await deleteHealthDataBatch(healthDataIds)

    if (response.code === 200) {
      ElMessage.success(isBatch ? '批量删除成功' : '删除成功')
      // 如果是批量删除，清空选中记录
      if (isBatch) {
        selectedRecords.value = []
      }
      emit('refresh') // 刷新数据
    } else {
      ElMessage.error(response.message || (isBatch ? '批量删除失败' : '删除失败'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

// 获取趋势图标
const getTrendIcon = (trend) => {
  if (trend === 'up') return ArrowUp
  if (trend === 'down') return ArrowDown
  return null
}

// 获取趋势类
const getTrendClass = (trend, isPositive = false) => {
  if (trend === 'up') return isPositive ? 'trend-positive' : 'trend-negative'
  if (trend === 'down') return isPositive ? 'trend-negative' : 'trend-positive'
  return ''
}

// 清除搜索
const clearSearch = () => {
  searchKeyword.value = ''
  searchActive.value = false
}

// 字段格式化
const formatValue = (value, unit = '', decimals = 1) => {
  if (value === undefined || value === null) return '未记录'
  return typeof value === 'number' ? 
    `${value.toFixed(decimals)} ${unit}` : 
    `${value} ${unit}`
}

// 批量添加相关
const showBatchAddForm = ref(false)

// 处理批量添加成功
const handleBatchAddSuccess = () => {
  showBatchAddForm.value = false
  emit('refresh')
}

// 批量修改相关
const showBatchEditForm = ref(false)
const selectedRecords = ref([])

// 处理选择变化
const handleSelectionChange = (item, checked) => {
  if (checked) {
    selectedRecords.value.push(item)
  } else {
    const index = selectedRecords.value.findIndex(record => record.healthDataId === item.healthDataId)
    if (index !== -1) {
      selectedRecords.value.splice(index, 1)
    }
  }
}

// 检查记录是否被选中
const isSelected = (item) => {
  return selectedRecords.value.some(record => record.healthDataId === item.healthDataId)
}

// 处理批量修改
const handleBatchEdit = () => {
  if (!selectedRecords.value.length) {
    ElMessage.warning('请先选择要修改的记录')
    return
  }
  showBatchEditForm.value = true
}

// 处理批量修改成功
const handleBatchEditSuccess = () => {
  showBatchEditForm.value = false
  selectedRecords.value = []
  emit('refresh')
}
</script>

<template>
  <div class="health-data-card-view">
    <!-- 最新健康数据统计卡片 -->
    <div v-if="statistics && !searchActive" class="latest-stats">
      <div class="stats-header">
        <h3>最新健康数据</h3>
        <div class="stats-date-container">
          <el-icon><Calendar /></el-icon>
          <span class="stats-date">{{ formatISOtoLocal(statistics.latestDate) }}</span>
          <!-- 调试输出，开发完成后可删除 -->
          <span class="debug-info" style="display: none;">
            原始日期: {{ statistics.latestDate }} | 
            格式化后: {{ formatISOtoLocal(statistics.latestDate) }}
          </span>
        </div>
      </div>

      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-icon heart-rate">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">心率</div>
            <div class="stat-value-row">
              <div class="stat-value">{{ formatValue(statistics.heartRate.value, 'bpm', 0) }}</div>
              <el-tag size="small" :type="statistics.heartRate.status.type">
                {{ statistics.heartRate.status.text }}
              </el-tag>
              <el-icon 
                v-if="getTrendIcon(statistics.heartRate.trend)" 
                :class="getTrendClass(statistics.heartRate.trend, false)"
              >
                <component :is="getTrendIcon(statistics.heartRate.trend)" />
              </el-icon>
            </div>
          </div>
        </div>

        <div class="stat-item">
          <div class="stat-icon weight">
            <el-icon><ScaleToOriginal /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">体重</div>
            <div class="stat-value-row">
              <div class="stat-value">{{ formatValue(statistics.weight.value, 'kg') }}</div>
              <el-icon 
                v-if="getTrendIcon(statistics.weight.trend)" 
                :class="getTrendClass(statistics.weight.trend, false)"
              >
                <component :is="getTrendIcon(statistics.weight.trend)" />
              </el-icon>
            </div>
          </div>
        </div>

        <div class="stat-item">
          <div class="stat-icon bmi">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">BMI</div>
            <div class="stat-value-row">
              <div class="stat-value">{{ formatValue(statistics.bmi.value, '', 1) }}</div>
              <el-tag size="small" :type="statistics.bmi.status.type">
                {{ statistics.bmi.status.text }}
              </el-tag>
              <el-icon 
                v-if="getTrendIcon(statistics.bmi.trend)" 
                :class="getTrendClass(statistics.bmi.trend, false)"
              >
                <component :is="getTrendIcon(statistics.bmi.trend)" />
              </el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 工具栏 -->
    <div class="card-toolbar">
      <div class="toolbar-left">
        <el-button
          type="primary"
          :icon="Plus"
          @click="showBatchAddForm = true"
          class="add-btn"
        >
          批量添加记录
        </el-button>
        <el-button
          type="warning"
          :icon="Edit"
          @click="handleBatchEdit"
          class="edit-btn"
          :disabled="!selectedRecords.length"
        >
          批量修改 ({{ selectedRecords.length }})
        </el-button>
        <el-button
          v-if="selectedRecords.length"
          type="danger"
          :icon="Delete"
          @click="handleDelete(selectedRecords)"
          class="delete-btn"
        >
          删除选中 ({{ selectedRecords.length }})
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-select 
          v-model="currentSort" 
          placeholder="排序方式" 
          size="default"
          class="sort-select"
        >
          <el-option 
            v-for="option in sortOptions" 
            :key="option.value" 
            :label="option.label" 
            :value="option.value" 
          />
        </el-select>

        <el-input
          v-model="searchKeyword"
          placeholder="搜索记录..."
          clearable
          :prefix-icon="Search"
          class="search-input"
          @clear="clearSearch"
        />
      </div>
    </div>

    <!-- 搜索结果提示 -->
    <div v-if="searchActive" class="search-result-info">
      <el-alert
        type="info"
        :closable="false"
        show-icon
      >
        <template #title>
          <span>
            搜索结果: 找到 {{ filteredData.length }} 条记录
            <el-button type="text" @click="clearSearch">清除搜索</el-button>
          </span>
        </template>
      </el-alert>
    </div>

    <!-- 健康数据卡片流 -->
    <div 
      v-loading="loading" 
      element-loading-text="加载健康数据中..." 
      class="card-container"
    >
      <el-empty 
        v-if="sortedData.length === 0" 
        description="暂无健康数据记录"
        :image-size="120"
      >
        <template #description>
          <p>开始记录您的健康数据，跟踪您的健康状况变化</p>
        </template>
        <el-button type="primary" @click="showBatchAddForm = true">添加第一条记录</el-button>
      </el-empty>

      <div v-else class="card-grid">
        <div 
          v-for="item in sortedData" 
          :key="item.healthDataId" 
          class="health-data-card"
        >
          <div class="card-header">
            <div class="card-selection">
              <el-checkbox 
                :model-value="isSelected(item)"
                @change="(val) => handleSelectionChange(item, val)"
              />
            </div>
            <div class="card-date">
              <el-icon><Calendar /></el-icon>
              <div class="date-text-container">
                <span class="date-text">
                  记录日期: {{ formatISOtoLocal(item.measurementDate) }}
                  <el-tooltip
                    content="系统仅记录健康数据的日期信息，不含具体时间"
                    placement="top"
                  >
                    <el-icon class="info-icon"><InfoFilled /></el-icon>
                  </el-tooltip>
                </span>
              </div>
            </div>
            <div class="card-actions">
              <el-button 
                type="primary" 
                size="small" 
                text 
                @click="handleEdit(item)"
              >
                修改
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                text 
                @click="handleDelete(item)"
              >
                删除
              </el-button>
            </div>
          </div>

          <div class="card-body">
            <div class="data-item">
              <div class="data-label">
                <el-icon><Timer /></el-icon>
                <span>心率</span>
              </div>
              <div class="data-value">
                <div class="value-content">
                  <span>{{ item.heartRate || '--' }} bpm</span>
                </div>
                <el-tag 
                  v-if="item.heartRate" 
                  size="small" 
                  :type="getHeartRateStatus(item.heartRate).type"
                  class="status-tag"
                >
                  {{ getHeartRateStatus(item.heartRate).text }}
                </el-tag>
              </div>
            </div>

            <div class="data-item">
              <div class="data-label">
                <el-icon><ScaleToOriginal /></el-icon>
                <span>体重</span>
              </div>
              <div class="data-value">
                <div class="value-content">
                  <span>{{ item.weight || '--' }} kg</span>
                </div>
              </div>
            </div>

            <div class="data-item">
              <div class="data-label">
                <el-icon><TrendCharts /></el-icon>
                <span>BMI</span>
              </div>
              <div class="data-value">
                <div class="value-content">
                  <span>{{ item.bmi !== null && item.bmi !== undefined ? (typeof item.bmi === 'number' ? item.bmi.toFixed(1) : item.bmi) : '--' }}</span>
                </div>
                <el-tag 
                  v-if="item.bmi" 
                  size="small" 
                  :type="getBMIStatus(item.bmi).type"
                  class="status-tag"
                >
                  {{ getBMIStatus(item.bmi).text }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div>
      <scrollable-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[15, 25, 35, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="$emit('size-change', $event)"
        @current-change="$emit('page-change', $event)"
        tooltip-content="提示：由于健康数据记录较为复杂，系统在翻页时可能需要一定的加载时间，请耐心等待数据加载完成。"
        scroll-target=".health-data-card-view"
        :top-offset="100"
      />
    </div>

    <!-- 批量添加表单弹窗 -->
    <el-dialog
      v-model="showBatchAddForm"
      title="批量添加健康数据"
      width="800px"
      class="batch-add-dialog"
      destroy-on-close
      :modal-append-to-body="true"
      :close-on-click-modal="false"
      align-center
      top="5vh"
      :append-to-body="true"
      :fullscreen="false"
    >
      <health-data-batch-form
        :user-id="userId"
        @success="handleBatchAddSuccess"
        @cancel="showBatchAddForm = false"
      />
    </el-dialog>

    <!-- 批量修改表单弹窗 -->
    <el-dialog
      v-model="showBatchEditForm"
      title="批量修改健康数据"
      width="800px"
      class="batch-edit-dialog"
      destroy-on-close
      :modal-append-to-body="true"
      :close-on-click-modal="false"
      align-center
      top="5vh"
      :append-to-body="true"
      :fullscreen="false"
    >
      <health-data-batch-form
        :user-id="userId"
        :edit-mode="true"
        :initial-data="selectedRecords"
        @success="handleBatchEditSuccess"
        @cancel="showBatchEditForm = false"
      />
    </el-dialog>

    <!-- 编辑表单弹窗 -->
    <el-dialog
      v-model="showEditForm"
      title="修改健康数据"
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
.health-data-card-view {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.latest-stats {
  background: linear-gradient(135deg, var(--el-color-primary-light-8) 0%, var(--el-color-primary-light-9) 100%);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
  flex-wrap: wrap;
  gap: 10px;
}

.stats-header h3 {
  margin: 0;
  font-size: 18px;
  color: var(--el-color-primary-dark-2);
}

.stats-date-container {
  overflow: hidden;
  display: flex;
  align-items: center;
  gap: 8px;
}

.stats-date {
  font-size: 15px;
  color: var(--el-color-primary);
  white-space: nowrap;
  font-weight: 500;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.stat-item {
  background-color: white;
  border-radius: 10px;
  padding: 18px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon :deep(svg) {
  width: 24px;
  height: 24px;
  color: white;
}

.stat-icon.heart-rate {
  background-color: #FF6B6B;
}

.stat-icon.weight {
  background-color: #4ECDC4;
}

.stat-icon.bmi {
  background-color: #45B7D1;
}

.stat-content {
  flex: 1;
  min-width: 0; /* 防止内容溢出 */
}

.stat-label {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-bottom: 6px;
}

.stat-value-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.trend-positive {
  color: var(--el-color-success);
}

.trend-negative {
  color: var(--el-color-danger);
}

.card-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
  flex-wrap: wrap;
  gap: 16px;
}

.toolbar-left {
  display: flex;
  gap: 16px;
  align-items: center;
}

.toolbar-right {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.sort-select {
  width: 180px;
}

.search-input {
  width: 200px;
}

.search-result-info {
  margin-bottom: 18px;
}

.card-container {
  min-height: 300px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

.health-data-card {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  position: relative;
}

.health-data-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.card-header {
  padding: 16px 20px 14px;
  background-color: var(--el-color-primary-light-9);
  border-bottom: 1px solid var(--el-border-color-light);
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-left: 40px;
}

.card-date {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  color: var(--el-text-color-secondary);
}

.date-text-container {
  display: flex;
  align-items: center;
}

.date-text {
  display: flex;
  align-items: center;
  gap: 4px;
}

.info-icon {
  font-size: 14px;
  color: var(--el-color-info);
  cursor: help;
}

.card-actions {
  display: flex;
  gap: 16px;
  justify-content: flex-end;
  width: 100%;
  border-top: 1px dashed rgba(var(--el-border-color-light-rgb), 0.7);
  padding-top: 10px;
}

.card-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.data-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.data-label {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--el-text-color-regular);
  min-width: 80px;
}

.data-value {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  font-weight: 500;
  margin-left: 16px;
  max-width: 65%;
}

.value-content {
  display: flex;
  align-items: center;
  min-width: 70px;
}

.status-tag {
  margin-left: 8px;
  white-space: nowrap;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .card-toolbar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .toolbar-right {
    flex-direction: column;
    width: 100%;
  }
  
  .sort-select,
  .search-input {
    width: 100%;
  }

  .card-header {
    padding: 14px 16px 12px;
  }
  
  .card-date {
    margin-bottom: 0;
  }
  
  .card-actions {
    justify-content: flex-start;
    padding-top: 8px;
  }

  .data-item {
    flex-wrap: wrap;
  }

  .data-value {
    max-width: 100%;
    margin-left: 0;
    margin-top: 6px;
  }
  
  .stats-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .stats-date-container {
    width: 100%;
    margin-top: 4px;
  }
}

/* 添加一个自定义CSS变量来支持渐变背景 */
:root {
  --el-color-primary-light-9-rgb: 236, 245, 255; /* Element Plus主题浅色的RGB值，可能需要根据实际主题调整 */
  --el-border-color-light-rgb: 220, 223, 230;
}

.card-date :deep(svg) {
  color: var(--el-color-primary);
  font-size: 18px;
}

.stats-date-container :deep(svg) {
  color: var(--el-color-primary);
  font-size: 16px;
}

.batch-add-dialog :deep(.el-dialog__body) {
  padding: 0;
}

/* 添加自定义样式确保弹窗居中显示 */
.batch-add-dialog {
  display: flex;
  justify-content: center;
  align-items: center;
}

.batch-add-dialog :deep(.el-dialog) {
  margin: 0 auto !important;
  max-width: 95%;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.batch-add-dialog :deep(.el-dialog__header) {
  padding: 16px 20px;
  margin-right: 0;
}

.batch-add-dialog :deep(.el-dialog__body) {
  flex: 1;
  overflow: auto;
  padding: 0;
}

.edit-btn {
  background-color: var(--el-color-warning-light-3);
  border-color: var(--el-color-warning-light-5);
  color: var(--el-color-warning-dark-2);
}

.edit-btn:hover {
  background-color: var(--el-color-warning-light-5);
  border-color: var(--el-color-warning-light-7);
  color: var(--el-color-warning-dark-2);
}

.edit-btn:disabled {
  background-color: var(--el-color-info-light-8);
  border-color: var(--el-color-info-light-8);
  color: var(--el-text-color-disabled);
}

.card-selection {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 1;
}

.delete-btn {
  background-color: var(--el-color-danger-light-3);
  border-color: var(--el-color-danger-light-5);
  color: var(--el-color-danger-dark-2);
}

.delete-btn:hover {
  background-color: var(--el-color-danger-light-5);
  border-color: var(--el-color-danger-light-7);
  color: var(--el-color-danger-dark-2);
}
</style> 