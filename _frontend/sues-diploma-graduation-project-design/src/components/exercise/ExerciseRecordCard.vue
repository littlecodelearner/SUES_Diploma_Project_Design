<template>
  <div class="exercise-record-card">
    <el-card class="record-card" shadow="hover" v-loading="loading">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h2>运动记录</h2>
            <el-button type="primary" @click="$emit('add')">
              <el-icon><Plus /></el-icon>
              添加记录
            </el-button>
          </div>
          <div class="header-right">
            <div class="filter-section">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                :shortcuts="dateShortcuts"
                @change="handleDateRangeChange"
                class="date-range-picker"
                value-format="YYYY-MM-DDTHH:mm:ss.SSSZ"
              />
              <el-button
                type="primary"
                link
                :icon="Refresh"
                @click="resetFilters"
              >
                重置筛选
              </el-button>
            </div>
            <div class="action-buttons">
              <el-button 
                type="danger" 
                :disabled="!selectedRecords.length"
                @click="$emit('batch-delete')"
              >
                <el-icon><Delete /></el-icon>
                批量删除
              </el-button>
              <el-button 
                type="primary" 
                :disabled="!selectedRecords.length"
                @click="$emit('batch-edit')"
              >
                <el-icon><Edit /></el-icon>
                批量修改
              </el-button>
            </div>
          </div>
        </div>
      </template>

      <!-- 统计概览 -->
      <div class="stats-overview">
        <div class="stat-item">
          <div class="stat-content">
            <div class="stat-header">
              <div class="stat-icon">
                <el-icon><Timer /></el-icon>
              </div>
              <div class="stat-label">今日运动时长</div>
            </div>
            <div class="stat-value-area">
              <div class="stat-value">{{ stats.today.duration || 0 }}</div>
              <div class="stat-unit">分钟</div>
            </div>
            <!-- 添加迷你趋势图 -->
            <div class="stat-trend">
              <mini-trend-chart :data="timeHistoryData" color="#409EFF" />
              <div class="trend-info" v-if="stats.today.duration > 0">
                <el-icon :class="timeComparedYesterday > 0 ? 'trend-up' : 'trend-down'">
                  <CaretTop v-if="timeComparedYesterday > 0" />
                  <CaretBottom v-else />
                </el-icon>
                <span>{{ Math.abs(timeComparedYesterday) }}% 相比昨日</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="stat-item">
          <div class="stat-content">
            <div class="stat-header">
              <div class="stat-icon">
                <el-icon><MapLocation /></el-icon>
              </div>
              <div class="stat-label">本周运动距离</div>
            </div>
            <div class="stat-value-area">
              <div class="stat-value">{{ (stats.week.distance || 0).toFixed(2) }}</div>
              <div class="stat-unit">公里</div>
            </div>
            <!-- 添加迷你趋势图 -->
            <div class="stat-trend">
              <mini-trend-chart :data="distanceHistoryData" color="#67C23A" />
              <div class="trend-info" v-if="stats.week.distance > 0">
                <el-icon :class="distanceComparedLastWeek > 0 ? 'trend-up' : 'trend-down'">
                  <CaretTop v-if="distanceComparedLastWeek > 0" />
                  <CaretBottom v-else />
                </el-icon>
                <span>{{ Math.abs(distanceComparedLastWeek) }}% 相比上周</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="stat-item">
          <div class="stat-content">
            <div class="stat-header">
              <div class="stat-icon">
                <el-icon><Calendar /></el-icon>
              </div>
              <div class="stat-label">本周运动次数</div>
            </div>
            <div class="stat-value-area">
              <div class="stat-value">{{ stats.week.count || 0 }}</div>
              <div class="stat-unit">次</div>
            </div>
            <!-- 添加迷你趋势图 -->
            <div class="stat-trend">
              <mini-trend-chart :data="countHistoryData" color="#E6A23C" />
              <div class="trend-info" v-if="stats.week.count > 0">
                <el-icon :class="countComparedLastWeek > 0 ? 'trend-up' : 'trend-down'">
                  <CaretTop v-if="countComparedLastWeek > 0" />
                  <CaretBottom v-else />
                </el-icon>
                <span>{{ Math.abs(countComparedLastWeek) }}% 相比上周</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="stat-item">
          <div class="stat-content">
            <div class="stat-header">
              <div class="stat-icon">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="stat-label">今日平均心率</div>
            </div>
            <div class="stat-value-area">
              <div class="stat-value">{{ stats.today.avgHeartRate || 0 }}</div>
              <div class="stat-unit">bpm</div>
            </div>
            <!-- 添加迷你趋势图 -->
            <div class="stat-trend">
              <mini-trend-chart :data="heartRateHistoryData" color="#F56C6C" />
              <div class="trend-info" v-if="stats.today.avgHeartRate > 0">
                <el-icon :class="heartRateComparedYesterday > 0 ? 'trend-up' : 'trend-down'">
                  <CaretTop v-if="heartRateComparedYesterday > 0" />
                  <CaretBottom v-else />
                </el-icon>
                <span>{{ Math.abs(heartRateComparedYesterday) }}% 相比昨日</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 调试信息 -->
      <div v-if="false" class="debug-info">
        <pre>记录数: {{ records.length }}, 总数: {{ total }}</pre>
        <pre>当前页: {{ currentPage }}, 每页数量: {{ pageSize }}</pre>
        <pre>选中记录: {{ selectedRecords.length }}</pre>
      </div>

      <!-- 批量操作工具栏 -->
      <div v-if="records && records.length > 0" class="batch-toolbar">
        <div class="toolbar-left">
          <el-checkbox 
            v-model="selectAll" 
            @change="handleSelectAllChange"
            :indeterminate="isIndeterminate"
          >
            全选
          </el-checkbox>
          <div v-if="selectedRecords.length > 0" class="selected-count">
            已选择 {{ selectedRecords.length }} 条记录
          </div>
        </div>
        
        <!-- 添加视图切换器 -->
        <div class="view-switcher">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="list">
              <el-icon><Document /></el-icon>
              <span class="view-label">列表</span>
            </el-radio-button>
            <el-radio-button label="card">
              <el-icon><Grid /></el-icon>
              <span class="view-label">卡片</span>
            </el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <!-- 运动记录列表（列表视图） -->
      <div v-if="viewMode === 'list'" class="records-list">
        <el-empty
          v-if="!loading && (!records || records.length === 0)"
          description="暂无运动记录"
        >
          <el-button type="primary" @click="$emit('add')">
            立即记录
          </el-button>
        </el-empty>
        <div v-else v-for="record in records" :key="record.exerciseRecordId" 
            class="record-item"
            :class="[getIntensityClass(record)]">
          <div class="intensity-indicator"></div>
          <div class="record-checkbox">
            <el-checkbox 
              v-model="selectedRecords" 
              :label="record.exerciseRecordId"
            />
          </div>
          <div class="record-date">
            {{ safeFormatDate(record.exerciseDate) }}
          </div>
          <div class="record-content">
            <div class="record-primary-info">
              <div class="record-types">
                <template v-if="record.exerciseTypesList && record.exerciseTypesList.length > 0">
                  <el-tag
                    v-for="type in record.exerciseTypesList"
                    :key="type.exerciseTypeId"
                    size="small"
                    class="type-tag"
                    :style="{ backgroundColor: getTypeBackgroundColor(type.exerciseName), 
                              color: getTypeTextColor(type.exerciseName),
                              borderColor: getTypeBorderColor(type.exerciseName) }"
                  >
                    {{ type.exerciseName }}
                  </el-tag>
                </template>
                <el-tag v-else size="small" type="info" class="type-tag">
                  未分类
                </el-tag>
              </div>
              
              <div class="record-details">
                <div class="detail-item">
                  <el-icon><Timer /></el-icon>
                  <span>{{ record.duration || 0 }}分钟</span>
                </div>
                <div class="detail-item" v-if="record.distance">
                  <el-icon><MapLocation /></el-icon>
                  <span>{{ (record.distance || 0).toFixed(2) }}公里</span>
                </div>
                <div class="detail-item" v-if="record.caloriesBurned">
                  <el-icon><Lightning /></el-icon>
                  <span>{{ record.caloriesBurned || 0 }}千卡</span>
                </div>
                <div class="detail-item" v-if="record.heartRate">
                  <el-icon><TrendCharts /></el-icon>
                  <span>{{ record.heartRate || 0 }}bpm</span>
                </div>
              </div>
            </div>
            
            <!-- 记录备注 -->
            <div class="record-note" v-if="record.exerciseNote">
              {{ record.exerciseNote }}
            </div>
            
            <!-- 展开/折叠详情按钮 -->
            <div v-if="record.exerciseNote" class="expand-toggle" @click="toggleExpandRecord(record.exerciseRecordId)">
              <el-icon v-if="!expandedRecords.includes(record.exerciseRecordId)">
                <el-icon><ArrowDown /></el-icon>
              </el-icon>
              <el-icon v-else>
                <el-icon><ArrowUp /></el-icon>
              </el-icon>
            </div>
          </div>
          <div class="record-actions">
            <el-button
              type="primary"
              link
              :icon="Edit"
              @click="$emit('edit', record)"
            >
              修改
            </el-button>
            <el-button
              type="danger"
              link
              :icon="Delete"
              @click="$emit('delete', record)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 运动记录卡片（卡片视图） -->
      <div v-else-if="viewMode === 'card'" class="records-grid">
        <el-empty
          v-if="!loading && (!records || records.length === 0)"
          description="暂无运动记录"
        >
          <el-button type="primary" @click="$emit('add')">
            立即记录
          </el-button>
        </el-empty>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :lg="8" v-for="record in records" :key="record.exerciseRecordId">
            <el-card 
              class="exercise-card" 
              shadow="hover"
              :class="[getIntensityClass(record)]"
            >
              <template #header>
                <div class="card-custom-header">
                  <div class="card-date">{{ safeFormatDate(record.exerciseDate) }}</div>
                  <div class="card-checkbox">
                    <el-checkbox 
                      v-model="selectedRecords" 
                      :label="record.exerciseRecordId"
                    />
                  </div>
                </div>
              </template>
              
              <div class="card-types">
                <template v-if="record.exerciseTypesList && record.exerciseTypesList.length > 0">
                  <el-tag
                    v-for="type in record.exerciseTypesList"
                    :key="type.exerciseTypeId"
                    size="small"
                    class="type-tag"
                    :style="{ backgroundColor: getTypeBackgroundColor(type.exerciseName), 
                              color: getTypeTextColor(type.exerciseName),
                              borderColor: getTypeBorderColor(type.exerciseName) }"
                  >
                    {{ type.exerciseName }}
                  </el-tag>
                </template>
                <el-tag v-else size="small" type="info" class="type-tag">
                  未分类
                </el-tag>
              </div>
              
              <div class="card-metrics">
                <div class="metric-item">
                  <div class="metric-icon">
                    <el-icon><Timer /></el-icon>
                  </div>
                  <div class="metric-value">{{ record.duration || 0 }}</div>
                  <div class="metric-label">分钟</div>
                </div>
                
                <div class="metric-item" v-if="record.distance">
                  <div class="metric-icon">
                    <el-icon><MapLocation /></el-icon>
                  </div>
                  <div class="metric-value">{{ (record.distance || 0).toFixed(2) }}</div>
                  <div class="metric-label">公里</div>
                </div>
                
                <div class="metric-item" v-if="record.caloriesBurned">
                  <div class="metric-icon">
                    <el-icon><Lightning /></el-icon>
                  </div>
                  <div class="metric-value">{{ record.caloriesBurned || 0 }}</div>
                  <div class="metric-label">千卡</div>
                </div>
                
                <div class="metric-item" v-if="record.heartRate">
                  <div class="metric-icon">
                    <el-icon><TrendCharts /></el-icon>
                  </div>
                  <div class="metric-value">{{ record.heartRate || 0 }}</div>
                  <div class="metric-label">bpm</div>
                </div>
              </div>
              
              <div class="card-note" v-if="record.exerciseNote">
                <div class="note-label">备注:</div>
                <div class="note-content">{{ record.exerciseNote }}</div>
              </div>
              
              <div class="card-actions">
                <el-button
                  type="primary"
                  size="small"
                  :icon="Edit"
                  @click="$emit('edit', record)"
                >
                  修改
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  :icon="Delete"
                  @click="$emit('delete', record)"
                >
                  删除
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 分页 -->
      <div v-if="total > 0">
        <scrollable-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[15, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          tooltip-content="提示：由于一条运动记录可能包含多个运动类型，系统可能显示的真实记录数量少于预期的总记录数。例如：如果用户一共有29条记录，然后前端请求了30条记录，但某条记录包含5个运动类型，那么实际可能只显示26条不同的记录。"
          text-content="查看更多记录"
          scroll-target=".record-card"
          :top-offset="120"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import {computed, onMounted, ref, watch} from 'vue'
import {
  ArrowDown,
  ArrowUp,
  Calendar,
  CaretBottom,
  CaretTop,
  Delete,
  Document,
  Edit,
  Grid,
  Lightning,
  MapLocation,
  Plus,
  Refresh,
  Timer,
  TrendCharts
} from '@element-plus/icons-vue'
import {formatDate} from '@/utils/date'
import ScrollablePagination from '@/components/common/ScrollablePagination.vue'
import MiniTrendChart from '@/components/charts/MiniTrendChart.vue'
import {calculateChange, calculateIntensity, generateHistoryData} from '@/utils/exerciseUtils'

// 增强的日期格式化函数，添加错误处理
const safeFormatDate = (date) => {
  if (!date) return '--'
  try {
    console.log('格式化日期:', date)
    return formatDate(date)
  } catch (error) {
    console.error('格式化日期失败:', error, date)
    // 尝试使用直接的ISO格式
    if (typeof date === 'string') {
      try {
        // 分离日期部分
        const datePart = date.split('T')[0]
        if (datePart && datePart.includes('-') && datePart.length >= 10) {
          return datePart // 返回YYYY-MM-DD部分
        }
      } catch (e) {
        console.error('尝试提取日期部分失败:', e)
      }
    }
    return '日期格式错误'
  }
}

const props = defineProps({
  records: {
    type: Array,
    default: () => []
  },
  stats: {
    type: Object,
    default: () => ({
      today: {
        duration: 0,
        avgHeartRate: 0
      },
      week: {
        distance: 0,
        count: 0
      }
    })
  },
  total: {
    type: Number,
    default: 0
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits([
  'add', 'edit', 'delete', 'page-change', 'size-change', 
  'selection-change', 'batch-edit', 'batch-delete',
  'date-range-change'
])

const currentPage = ref(1)
const pageSize = ref(15)
const selectedRecords = ref([])
const selectAll = ref(false)
const isIndeterminate = computed(() => {
  return selectedRecords.value.length > 0 && selectedRecords.value.length < props.records.length
})

// 全选/取消全选
const handleSelectAllChange = (val) => {
  if (val) {
    selectedRecords.value = props.records.map(record => record.exerciseRecordId)
  } else {
    selectedRecords.value = []
  }
  emit('selection-change', selectedRecords.value)
}

// 监听选中记录变化
watch(() => selectedRecords.value, (newVal) => {
  selectAll.value = newVal.length === props.records.length && props.records.length > 0
  emit('selection-change', newVal)
}, { deep: true })

// 监听记录列表变化，重置选择状态
watch(() => props.records, () => {
  // 过滤掉已经不在当前页面的记录ID
  const currentIds = props.records.map(record => record.exerciseRecordId)
  selectedRecords.value = selectedRecords.value.filter(id => currentIds.includes(id))
  // 检查全选状态
  selectAll.value = props.records.length > 0 && selectedRecords.value.length === props.records.length
}, { deep: true })

const handleSizeChange = (val) => {
  console.log('更改每页条数:', val)
  pageSize.value = val
  currentPage.value = 1 // 重置到第一页
  selectedRecords.value = [] // 重置选中状态
  emit('size-change', val)
}

const handleCurrentChange = (val) => {
  console.log('切换页码:', val)
  currentPage.value = val
  selectedRecords.value = [] // 重置选中状态
  emit('page-change', val)
}

// 监听 props 变化
watch(() => props.total, (newTotal) => {
  console.log('总记录数变化:', newTotal)
  // 如果总数为0，重置页码
  if (newTotal === 0) {
    currentPage.value = 1
    selectedRecords.value = []
  }
})

// 监听 loading 状态变化
watch(() => props.loading, (newLoading) => {
  if (!newLoading) {
    console.log('加载完成，检查是否需要重置页码')
    // 加载完成后，检查是否需要重置页码
    if (props.total === 0) {
      currentPage.value = 1
    }
  }
})

// 初始加载时记录数据
onMounted(() => {
  console.log('ExerciseRecordCard组件挂载，初始记录数:', props.records.length, '总记录数:', props.total)
})

// 监听records变化
watch(() => props.records, (newRecords) => {
  console.log('运动记录数据更新，当前记录数:', newRecords.length)
  
  // 检查运动类型
  const recordsWithoutTypes = newRecords.filter(
    record => !record.exerciseTypesList || record.exerciseTypesList.length === 0
  )
  
  if (recordsWithoutTypes.length > 0) {
    console.log(`发现${recordsWithoutTypes.length}条没有运动类型的记录`)
  }
  
  // 计算具有多个运动类型的记录数量
  const recordsWithMultipleTypes = newRecords.filter(
    record => record.exerciseTypesList && record.exerciseTypesList.length > 1
  )
  
  if (recordsWithMultipleTypes.length > 0) {
    console.log(`注意: 发现${recordsWithMultipleTypes.length}条拥有多个运动类型的记录`)
    // 计算这些记录实际包含的运动类型总数
    const totalExtraTypes = recordsWithMultipleTypes.reduce(
      (sum, record) => sum + record.exerciseTypesList.length - 1, 0
    )
    console.log(`这些记录共包含${totalExtraTypes + recordsWithMultipleTypes.length}个运动类型，比记录数多出${totalExtraTypes}个`)
    console.log(`这可能导致分页时实际显示的记录数少于预期`)
    
    // 输出详细信息
    if (recordsWithMultipleTypes.length <= 3) { // 限制日志数量
      recordsWithMultipleTypes.forEach(record => {
        console.log(`记录ID: ${record.exerciseRecordId} 包含 ${record.exerciseTypesList.length} 个运动类型:`, 
          record.exerciseTypesList.map(type => type.exerciseName).join(', '))
      })
    }
  }
}, { deep: true })

// 添加日期范围选择
const dateRange = ref(null)
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
    text: '最近一个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    }
  },
  {
    text: '最近三个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    }
  }
]

// 处理日期范围变化
const handleDateRangeChange = (val) => {
  console.log('日期选择变化:', val)
  dateRange.value = val
  if (val) {
    const [start, end] = val
    // 设置开始时间为当天开始
    const startDate = new Date(start)
    startDate.setHours(0, 0, 0, 0)
    
    // 设置结束时间为当天结束
    const endDate = new Date(end)
    endDate.setHours(23, 59, 59, 999)
    
    emit('date-range-change', [startDate, endDate])
  } else {
    emit('date-range-change', null)
  }
}

// 重置筛选条件
const resetFilters = () => {
  dateRange.value = null
  emit('date-range-change', null)
}

// 添加历史趋势数据
const timeHistoryData = computed(() => generateHistoryData('time'))
const distanceHistoryData = computed(() => generateHistoryData('distance'))
const countHistoryData = computed(() => generateHistoryData('count')) 
const heartRateHistoryData = computed(() => generateHistoryData('heartRate'))

// 计算同比变化
const timeComparedYesterday = computed(() => calculateChange(props.stats.today.duration, 42))
const distanceComparedLastWeek = computed(() => calculateChange(props.stats.week.distance, 3.5))
const countComparedLastWeek = computed(() => calculateChange(props.stats.week.count, 5))
const heartRateComparedYesterday = computed(() => calculateChange(props.stats.today.avgHeartRate, 128))

// 视图模式
const viewMode = ref('list')  // 'list' 或 'card'

// 展开的记录ID列表
const expandedRecords = ref([])

// 切换记录展开状态
const toggleExpandRecord = (recordId) => {
  const index = expandedRecords.value.indexOf(recordId)
  if (index > -1) {
    expandedRecords.value.splice(index, 1)
  } else {
    expandedRecords.value.push(recordId)
  }
}

// 获取运动强度的样式类
const getIntensityClass = (record) => {
  const intensity = calculateIntensity(record)
  return `intensity-${intensity}`
}

// 获取运动类型的背景颜色
const getTypeBackgroundColor = (typeName) => {
  const colorMap = {
    '跑步': '#ffecec',
    '步行': '#f0f9eb',
    '骑行': '#fdf6ec',
    '游泳': '#ecf5ff',
    '力量训练': '#f4f4f5',
    '篮球': '#fef0f0',
    '足球': '#f0f9eb',
    '瑜伽': '#f5effb',
    '健身操': '#fcefff',
    '舞蹈': '#fef9ec'
  }
  return colorMap[typeName] || '#ecf5ff'
}

// 获取运动类型的文字颜色
const getTypeTextColor = (typeName) => {
  const colorMap = {
    '跑步': '#f56c6c',
    '步行': '#67c23a',
    '骑行': '#e6a23c',
    '游泳': '#409eff',
    '力量训练': '#909399',
    '篮球': '#f56c6c',
    '足球': '#67c23a',
    '瑜伽': '#a97ade',
    '健身操': '#c599ec',
    '舞蹈': '#e6a23c'
  }
  return colorMap[typeName] || '#409eff'
}

// 获取运动类型的边框颜色
const getTypeBorderColor = (typeName) => {
  const colorMap = {
    '跑步': '#fbc4c4',
    '步行': '#c2e7b0',
    '骑行': '#f3d19e',
    '游泳': '#a0cfff',
    '力量训练': '#c8c9cc',
    '篮球': '#fbc4c4',
    '足球': '#c2e7b0',
    '瑜伽': '#c8a3e7',
    '健身操': '#d8b8f0',
    '舞蹈': '#f3d19e'
  }
  return colorMap[typeName] || '#a0cfff'
}
</script>

<style scoped>
.exercise-record-card {
  width: 100%;
}

.record-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-right {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: flex-end;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-title h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  padding: 12px;
  background: var(--el-color-primary-light-9);
  border-radius: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.stat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.stat-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.stat-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--el-color-primary);
  border-radius: 50%;
  color: white;
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.stat-value-area {
  display: flex;
  align-items: baseline;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-color-primary);
  margin-right: 4px;
}

.stat-unit {
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.stat-trend {
  margin-top: 4px;
}

.trend-info {
  display: flex;
  align-items: center;
  font-size: 12px;
  margin-top: 4px;
  color: var(--el-text-color-secondary);
}

.trend-up {
  color: #67C23A;
}

.trend-down {
  color: #F56C6C;
}

/* 批量操作工具栏 */
.batch-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  margin-bottom: 12px;
  border-bottom: 1px solid var(--el-border-color-light);
}

.selected-count {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

/* 视图切换器 */
.view-switcher {
  display: flex;
  align-items: center;
}

.view-label {
  margin-left: 4px;
}

/* 记录列表 - 列表视图 */
.records-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.record-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px;
  background: var(--el-bg-color);
  border-radius: 6px;
  border: 1px solid var(--el-border-color-light);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

/* 强度指示条 */
.intensity-indicator {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
}

.intensity-high .intensity-indicator {
  background-color: #f56c6c;
}

.intensity-medium .intensity-indicator {
  background-color: #e6a23c;
}

.intensity-low .intensity-indicator {
  background-color: #67c23a;
}

/* 根据强度添加不同的左边框 */
.intensity-high {
  border-left: 4px solid #f56c6c;
}

.intensity-medium {
  border-left: 4px solid #e6a23c;
}

.intensity-low {
  border-left: 4px solid #67c23a;
}

.record-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.record-checkbox {
  padding-top: 2px;
}

.record-date {
  min-width: 100px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  font-size: 14px;
}

.record-content {
  flex: 1;
  position: relative;
}

.record-primary-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.record-types {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.type-tag {
  margin-right: 3px;
  margin-bottom: 3px;
}

.record-details {
  display: flex;
  gap: 12px;
  margin-bottom: 6px;
  flex-wrap: wrap;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 3px;
  color: var(--el-text-color-regular);
  font-size: 13px;
}

.record-note {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-top: 6px;
  margin-left: 16px;
  padding-left: 8px;
  border-left: 2px solid var(--el-border-color);
}

.expand-toggle {
  position: absolute;
  right: 0;
  bottom: 0;
  cursor: pointer;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  padding: 2px;
}

.expand-toggle:hover {
  color: var(--el-color-primary);
}

.record-actions {
  display: flex;
  gap: 4px;
  align-items: flex-start;
}

.record-actions .el-button {
  padding: 2px 4px;
  font-size: 12px;
  height: auto;
}

/* 卡片视图样式 */
.records-grid {
  margin-bottom: 16px;
}

.exercise-card {
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

.exercise-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-custom-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-date {
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.card-types {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.card-metrics {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.metric-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px;
  background: var(--el-fill-color-light);
  border-radius: 6px;
}

.metric-icon {
  color: var(--el-color-primary);
  font-size: 18px;
  margin-bottom: 4px;
}

.metric-value {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.metric-label {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.card-note {
  margin-bottom: 12px;
  padding: 8px;
  background: var(--el-fill-color-lighter);
  border-radius: 4px;
  font-size: 13px;
}

.note-label {
  font-weight: 500;
  margin-bottom: 4px;
  color: var(--el-text-color-regular);
}

.note-content {
  color: var(--el-text-color-secondary);
  word-break: break-all;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 响应式布局优化 */
@media screen and (max-width: 768px) {
  .stats-overview {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .card-header {
    flex-direction: column;
    align-items: stretch;
  }

  .header-right {
    width: 100%;
    align-items: stretch;
  }

  .filter-section {
    flex-direction: column;
    width: 100%;
  }

  .date-range-picker {
    width: 100%;
  }

  .action-buttons {
    justify-content: flex-end;
  }

  .toolbar-left {
    flex-wrap: wrap;
  }
  
  .record-item {
    flex-wrap: wrap;
  }
  
  .record-date {
    min-width: auto;
    width: 100px;
  }
  
  .view-label {
    display: none;
  }
}
</style> 