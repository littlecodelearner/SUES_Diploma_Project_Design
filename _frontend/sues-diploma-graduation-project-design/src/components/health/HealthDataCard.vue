<!-- 健康数据记录卡片组件 -->
<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  addHealthData,
  addHealthDataBatch,
  updateHealthDataInBatch,
  deleteHealthDataBatch,
  getHealthDataList,
  getHealthDataTrend 
} from '@/api/healthData'
import { Calendar, Delete, Edit, Plus, Search, TrendCharts } from '@element-plus/icons-vue'

const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

// 数据状态
const loading = ref(false)
const healthData = ref([])
const selectedData = ref([])
const showAddForm = ref(false)
const showEditForm = ref(false)
const editingData = ref(null)
const chartLoading = ref(false)
const chartData = ref(null)

// 分页配置
const pagination = ref({
  current: 1,
  size: 15,
  total: 0
})

// 筛选条件
const dateRange = ref([])

// 计算最新的健康指标
const latestMetrics = computed(() => {
  if (!healthData.value.length) return null
  const latest = healthData.value[0]
  return {
    heartRate: latest.heartRate,
    weight: latest.weight,
    bmi: latest.bmi,
    height: latest.height,
    measurementDate: latest.measurementDate
  }
})

// 计算BMI状态
const getBmiStatus = (bmi) => {
  if (!bmi) return { text: '未知', type: 'info' }
  if (bmi < 18.5) return { text: '偏瘦', type: 'warning' }
  if (bmi < 24) return { text: '正常', type: 'success' }
  if (bmi < 28) return { text: '过重', type: 'warning' }
  return { text: '肥胖', type: 'danger' }
}

// 计算心率状态
const getHeartRateStatus = (heartRate) => {
  if (!heartRate) return { text: '未知', type: 'info' }
  if (heartRate < 60) return { text: '偏慢', type: 'warning' }
  if (heartRate > 100) return { text: '偏快', type: 'danger' }
  return { text: '正常', type: 'success' }
}

// 获取健康数据列表
const fetchHealthData = async () => {
  try {
    loading.value = true
    const response = await getHealthDataList({
      userId: props.userId,
      current: pagination.value.current,
      size: pagination.value.size,
      startDateTime: dateRange.value?.[0]?.toISOString(),
      endDateTime: dateRange.value?.[1]?.toISOString(),
      isAsc: false // 默认按时间降序，最新数据在前
    })

    if (response.code === 200) {
      healthData.value = response.data.dataList.map(item => ({
        ...item,
        bmi: item.weight && item.height ? (item.weight / Math.pow(item.height / 100, 2)).toFixed(1) : null
      }))
      pagination.value.total = response.data.total
    } else {
      ElMessage.error(response.message || '获取健康数据失败')
    }
  } catch (error) {
    console.error('获取健康数据失败:', error)
    ElMessage.error('获取健康数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 获取健康数据趋势
const fetchHealthDataTrend = async () => {
  try {
    chartLoading.value = true
    const response = await getHealthDataTrend({
      userId: props.userId,
      offset: 0,
      size: 30, // 获取最近30条记录的趋势
      startDate: dateRange.value?.[0]?.toISOString(),
      endDate: dateRange.value?.[1]?.toISOString()
    })

    if (response.code === 200) {
      chartData.value = response.data
    }
  } catch (error) {
    console.error('获取健康数据趋势失败:', error)
  } finally {
    chartLoading.value = false
  }
}

// 处理批量删除
const handleBatchDelete = async (records = selectedData.value) => {
  if (!records.length) {
    ElMessage.warning('请选择要删除的记录')
    return
  }

  try {
    await ElMessageBox.confirm('确定要删除选中的记录吗？此操作不可恢复！', '删除确认', {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })

    const ids = records.map(item => item.healthDataId)
    const response = await deleteHealthDataBatch(ids)

    if (response.code === 200) {
      ElMessage.success('删除成功')
      selectedData.value = []
      await fetchHealthData()
      await fetchHealthDataTrend()
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

// 处理添加/编辑表单提交
const handleFormSubmit = async (formData, isEdit = false) => {
  if (!formData.measurementDate) {
    ElMessage.warning('请选择测量时间')
    return
  }

  if (!formData.heartRate && !formData.weight) {
    ElMessage.warning('请至少填写一项健康数据')
    return
  }

  try {
    const api = isEdit ? updateHealthDataInBatch : addHealthDataBatch
    const response = await api([{
      ...formData,
      userId: props.userId,
      ...(isEdit ? { healthDataId: editingData.value.healthDataId } : {})
    }])

    if (response.code === 200) {
      ElMessage.success(isEdit ? '修改成功' : '添加成功')
      showAddForm.value = false
      showEditForm.value = false
      editingData.value = null
      await fetchHealthData()
      await fetchHealthDataTrend()
    } else {
      ElMessage.error(response.message || (isEdit ? '修改失败' : '添加失败'))
    }
  } catch (error) {
    console.error(isEdit ? '修改失败:' : '添加失败:', error)
    ElMessage.error(isEdit ? '修改失败，请稍后重试' : '添加失败，请稍后重试')
  }
}

// 监听日期范围变化
watch(dateRange, () => {
  fetchHealthData()
  fetchHealthDataTrend()
}, { deep: true })

// 初始化
onMounted(() => {
  // 设置默认日期范围为最近一个月
  const end = new Date()
  const start = new Date()
  start.setMonth(start.getMonth() - 1)
  dateRange.value = [start, end]
  
  fetchHealthData()
  fetchHealthDataTrend()
})
</script>

<template>
  <div class="health-data-card">
    <!-- 最新健康指标卡片 -->
    <div class="metrics-grid" v-if="latestMetrics">
      <el-card class="metric-card" shadow="hover">
        <div class="metric-content">
          <div class="metric-icon heart-rate">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-value">{{ latestMetrics.heartRate || '--' }}</div>
            <div class="metric-label">心率 (bpm)</div>
            <el-tag 
              :type="getHeartRateStatus(latestMetrics.heartRate).type"
              size="small"
              class="metric-status"
            >
              {{ getHeartRateStatus(latestMetrics.heartRate).text }}
            </el-tag>
          </div>
        </div>
        <div class="metric-date" v-if="latestMetrics.measurementDate">
          {{ new Date(latestMetrics.measurementDate).toLocaleString() }}
        </div>
      </el-card>

      <el-card class="metric-card" shadow="hover">
        <div class="metric-content">
          <div class="metric-icon weight">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-value">{{ latestMetrics.weight || '--' }}</div>
            <div class="metric-label">体重 (kg)</div>
            <div class="metric-sub" v-if="latestMetrics.height">
              身高: {{ latestMetrics.height }} cm
            </div>
          </div>
        </div>
        <div class="metric-date" v-if="latestMetrics.measurementDate">
          {{ new Date(latestMetrics.measurementDate).toLocaleString() }}
        </div>
      </el-card>

      <el-card class="metric-card" shadow="hover">
        <div class="metric-content">
          <div class="metric-icon bmi">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-value">{{ latestMetrics.bmi || '--' }}</div>
            <div class="metric-label">BMI</div>
            <el-tag 
              :type="getBmiStatus(latestMetrics.bmi).type"
              size="small"
              class="metric-status"
            >
              {{ getBmiStatus(latestMetrics.bmi).text }}
            </el-tag>
          </div>
        </div>
        <div class="metric-date" v-if="latestMetrics.measurementDate">
          {{ new Date(latestMetrics.measurementDate).toLocaleString() }}
        </div>
      </el-card>
    </div>

    <!-- 健康数据趋势图 -->
    <el-card v-loading="chartLoading" class="trend-chart" v-if="chartData">
      <template #header>
        <div class="card-header">
          <span>健康数据趋势</span>
        </div>
      </template>
      <img :src="'data:image/png;base64,' + chartData" alt="健康数据趋势图" style="width: 100%;" />
    </el-card>

    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button type="primary" :icon="Plus" @click="showAddForm = true">
          添加记录
        </el-button>
        <el-button 
          type="danger" 
          :icon="Delete"
          :disabled="!selectedData.length"
          @click="handleBatchDelete()"
        >
          删除选中 ({{ selectedData.length }})
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :shortcuts="[
            { text: '最近一周', value: () => {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
              return [start, end]
            }},
            { text: '最近一月', value: () => {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
              return [start, end]
            }},
            { text: '最近三月', value: () => {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
              return [start, end]
            }}
          ]"
        />
      </div>
    </div>

    <!-- 数据列表 -->
    <el-table
      v-loading="loading"
      :data="healthData"
      style="width: 100%"
      @selection-change="selectedData = $event"
    >
      <el-table-column type="selection" width="55" />
      
      <el-table-column label="记录时间" prop="measurementDate" sortable width="180">
        <template #default="{ row }">
          {{ new Date(row.measurementDate).toLocaleString() }}
        </template>
      </el-table-column>
      
      <el-table-column label="心率" prop="heartRate" width="180">
        <template #default="{ row }">
          <div class="health-value" v-if="row.heartRate">
            <span>{{ row.heartRate }} bpm</span>
            <el-tag 
              :type="getHeartRateStatus(row.heartRate).type"
              size="small"
            >
              {{ getHeartRateStatus(row.heartRate).text }}
            </el-tag>
          </div>
          <span v-else>--</span>
        </template>
      </el-table-column>
      
      <el-table-column label="身高" prop="height" width="120">
        <template #default="{ row }">
          {{ row.height ? `${row.height} cm` : '--' }}
        </template>
      </el-table-column>
      
      <el-table-column label="体重" prop="weight" width="120">
        <template #default="{ row }">
          {{ row.weight ? `${row.weight} kg` : '--' }}
        </template>
      </el-table-column>
      
      <el-table-column label="BMI" prop="bmi" width="180">
        <template #default="{ row }">
          <div class="health-value" v-if="row.bmi">
            <span>{{ row.bmi }}</span>
            <el-tag 
              :type="getBmiStatus(row.bmi).type"
              size="small"
            >
              {{ getBmiStatus(row.bmi).text }}
            </el-tag>
          </div>
          <span v-else>--</span>
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button 
            type="primary" 
            link
            :icon="Edit"
            @click="editingData = {...row}; showEditForm = true"
          >
            编辑
          </el-button>
          <el-button 
            type="danger" 
            link
            :icon="Delete"
            @click="handleBatchDelete([row])"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页器 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[15, 30, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchHealthData"
        @current-change="fetchHealthData"
      />
    </div>

    <!-- 添加/编辑表单弹窗 -->
    <el-dialog
      :title="editingData ? '编辑健康数据' : '添加健康数据'"
      :model-value="showAddForm || showEditForm"
      @close="showAddForm = showEditForm = false; editingData = null"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="editingData || {}"
        label-position="top"
      >
        <el-form-item label="心率 (bpm)" prop="heartRate">
          <el-input-number
            :model-value="editingData?.heartRate"
            @update:model-value="val => editingData && (editingData.heartRate = val)"
            :min="0"
            :max="300"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="身高 (cm)" prop="height">
          <el-input-number
            :model-value="editingData?.height"
            @update:model-value="val => editingData && (editingData.height = val)"
            :min="0"
            :max="300"
            :precision="1"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="体重 (kg)" prop="weight">
          <el-input-number
            :model-value="editingData?.weight"
            @update:model-value="val => editingData && (editingData.weight = val)"
            :min="0"
            :max="500"
            :precision="2"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="测量时间" prop="measurementDate">
          <el-date-picker
            :model-value="editingData?.measurementDate"
            @update:model-value="val => editingData && (editingData.measurementDate = val)"
            type="datetime"
            style="width: 100%"
            :disabled-date="time => time > Date.now()"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddForm = showEditForm = false; editingData = null">
          取消
        </el-button>
        <el-button 
          type="primary"
          @click="handleFormSubmit(editingData || {}, !!editingData)"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.health-data-card {
  padding: 20px;
  background: var(--el-bg-color);
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.metric-card {
  transition: transform 0.3s ease;
}

.metric-card:hover {
  transform: translateY(-5px);
}

.metric-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.metric-icon.heart-rate {
  background-color: var(--el-color-danger-light-9);
  color: var(--el-color-danger);
}

.metric-icon.weight {
  background-color: var(--el-color-warning-light-9);
  color: var(--el-color-warning);
}

.metric-icon.bmi {
  background-color: var(--el-color-success-light-9);
  color: var(--el-color-success);
}

.metric-info {
  flex: 1;
}

.metric-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  line-height: 1;
  margin-bottom: 4px;
}

.metric-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-bottom: 8px;
}

.metric-sub {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}

.metric-status {
  font-size: 12px;
}

.metric-date {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 8px;
  text-align: right;
}

.trend-chart {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  gap: 16px;
  flex-wrap: wrap;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.health-value {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

@media screen and (max-width: 768px) {
  .metrics-grid {
    grid-template-columns: 1fr;
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-left,
  .toolbar-right {
    flex-direction: column;
    width: 100%;
  }
}
</style> 