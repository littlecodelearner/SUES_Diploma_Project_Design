<script setup>
import {computed, nextTick, onMounted, onUnmounted, ref, watch} from 'vue'
import {ElMessage} from 'element-plus'
import {calculateNutritionIntake} from '@/api/diet'
import * as echarts from 'echarts/core'
import {use} from 'echarts/core'
import {CanvasRenderer} from 'echarts/renderers'
import {BarChart, PieChart} from 'echarts/charts'
import {GridComponent, LegendComponent, TitleComponent, TooltipComponent} from 'echarts/components'
import {Bowl, Calendar, DataLine, Refresh, Timer} from '@element-plus/icons-vue'
import {formatDateWithChineseTimezone} from '@/utils/date'

// 注册必需的组件
use([
  CanvasRenderer,
  PieChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const props = defineProps({
  userId: {
    type: Number,
    required: true
  },
  dateRange: {
    type: Array,
    default: () => []
  }
})

// 基础状态
const loading = ref(false)
const nutritionData = ref({
  totalCalories: 0,
  totalProtein: 0,
  totalFat: 0,
  totalCarbohydrates: 0,
  totalWater: 0
})

// 图表相关
const pieChartRef = ref(null)
const barChartRef = ref(null)
let pieChart = null
let barChart = null
const chartMode = ref('pie')

// 日期筛选相关
const localDateRange = ref(null)
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
      start.setMonth(start.getMonth() - 1)
      return [start, end]
    }
  },
  {
    text: '最近三月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setMonth(start.getMonth() - 3)
      return [start, end]
    }
  }
]

// 重置日期筛选
const resetDateFilter = () => {
  localDateRange.value = null
  fetchNutritionData()
}

// 处理日期变化
const handleDateChange = () => {
  fetchNutritionData()
}

// 初始化饼图
const initPieChart = () => {
  if (!pieChartRef.value) return
  
  pieChart = echarts.init(pieChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}g ({d}%)',
      position: 'top'
    },
    legend: {
      orient: 'horizontal',
      bottom: '5%',
      left: 'center'
    },
    series: [
      {
        name: '营养成分',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '14',
            fontWeight: 'bold'
          }
        },
        data: []
      }
    ]
  }
  
  pieChart.setOption(option)
}

// 初始化柱状图
const initBarChart = () => {
  if (!barChartRef.value) return
  
  barChart = echarts.init(barChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['蛋白质', '脂肪', '碳水化合物']
    },
    yAxis: {
      type: 'value',
      name: '克(g)'
    },
    series: [
      {
        name: '营养成分',
        type: 'bar',
        data: [],
        itemStyle: {
          borderRadius: [5, 5, 0, 0]
        }
      }
    ]
  }
  
  barChart.setOption(option)
}

// 更新图表数据
const updateCharts = () => {
  const { totalProtein, totalFat, totalCarbohydrates } = nutritionData.value
  
  // 更新饼图
  if (pieChart) {
    pieChart.setOption({
      series: [{
        data: [
          { value: totalProtein, name: '蛋白质', itemStyle: { color: '#91CC75' } },
          { value: totalFat, name: '脂肪', itemStyle: { color: '#FAC858' } },
          { value: totalCarbohydrates, name: '碳水化合物', itemStyle: { color: '#5470C6' } }
        ]
      }]
    })
  }
  
  // 更新柱状图
  if (barChart) {
    barChart.setOption({
      series: [{
        data: [
          { value: totalProtein, itemStyle: { color: '#91CC75' } },
          { value: totalFat, itemStyle: { color: '#FAC858' } },
          { value: totalCarbohydrates, itemStyle: { color: '#5470C6' } }
        ]
      }]
    })
  }
}

// 获取营养分析数据
const fetchNutritionData = async () => {
  if (!props.userId) {
    ElMessage.warning('用户ID不存在，无法获取营养分析数据')
    return
  }

  try {
    loading.value = true
    const params = {
      userId: props.userId,
      current: 1,
      size: 15,
      isAsc: false
    }

    if (localDateRange.value?.length === 2) {
      params.startDateTime = formatDateWithChineseTimezone(localDateRange.value[0])
      params.endDateTime = formatDateWithChineseTimezone(localDateRange.value[1])
    }

    const response = await calculateNutritionIntake(params)

    if (response.code === 200) {
      nutritionData.value = response.data
      updateCharts()
    } else {
      ElMessage.error(response.message || '获取营养分析数据失败')
    }
  } catch (error) {
    console.error('获取营养分析数据失败:', error)
    ElMessage.error('获取营养分析数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 计算营养项目
const nutritionItems = computed(() => [
  {
    label: '总热量',
    value: formatNumber(nutritionData.value.totalCalories),
    unit: 'kcal',
    icon: Bowl,
    iconClass: 'calories'
  },
  {
    label: '蛋白质',
    value: formatNumber(nutritionData.value.totalProtein),
    unit: 'g',
    icon: Timer,
    iconClass: 'protein'
  },
  {
    label: '脂肪',
    value: formatNumber(nutritionData.value.totalFat),
    unit: 'g',
    icon: DataLine,
    iconClass: 'fat'
  },
  {
    label: '碳水化合物',
    value: formatNumber(nutritionData.value.totalCarbohydrates),
    unit: 'g',
    icon: Calendar,
    iconClass: 'carbs'
  }
])

// 格式化数字
const formatNumber = (value) => {
  if (!value && value !== 0) return '-'
  return Number(value).toFixed(1)
}

// 监听日期范围变化
watch(() => props.dateRange, (newRange) => {
  if (newRange && newRange.length === 2) {
    localDateRange.value = newRange
    fetchNutritionData()
  }
}, { immediate: true })

// 监听图表模式切换
watch(chartMode, async () => {
  await nextTick()
  pieChart?.resize()
  barChart?.resize()
})

// 组件挂载时初始化
onMounted(() => {
  initPieChart()
  initBarChart()
  fetchNutritionData()
  window.addEventListener('resize', () => {
    pieChart?.resize()
    barChart?.resize()
  })
})

// 组件卸载时清理
onUnmounted(() => {
  window.removeEventListener('resize', () => {
    pieChart?.resize()
    barChart?.resize()
  })
  pieChart?.dispose()
  barChart?.dispose()
})
</script>

<template>
  <div class="nutrition-summary">
    <!-- 日期筛选器 -->
    <div class="filter-section">
      <div class="date-filter">
        <el-date-picker
          v-model="localDateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :shortcuts="dateShortcuts"
          value-format="YYYY-MM-DD"
          @change="handleDateChange"
        />
        <el-button 
          :icon="Refresh"
          circle
          @click="resetDateFilter"
          :disabled="!localDateRange"
        />
      </div>
    </div>

    <!-- 营养数据卡片 -->
    <div class="nutrition-cards">
      <el-card
        v-for="(item, index) in nutritionItems"
        :key="index"
        class="nutrition-card"
        :class="item.iconClass"
        shadow="hover"
      >
        <template #header>
          <div class="card-header">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </div>
        </template>
        <div class="card-value">
          {{ item.value }}
          <span class="unit">{{ item.unit }}</span>
        </div>
      </el-card>
    </div>

    <!-- 图表展示区域 -->
    <div class="charts-section">
      <div class="chart-controls">
        <el-radio-group v-model="chartMode" size="large">
          <el-radio-button label="pie">营养比例</el-radio-button>
          <el-radio-button label="bar">营养分布</el-radio-button>
        </el-radio-group>
      </div>
      
      <div class="chart-container">
        <div
          v-show="chartMode === 'pie'"
          ref="pieChartRef"
          class="chart"
        />
        <div
          v-show="chartMode === 'bar'"
          ref="barChartRef"
          class="chart"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.nutrition-summary {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-section {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.date-filter {
  display: flex;
  gap: 8px;
  align-items: center;
}

.nutrition-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.nutrition-card {
  transition: all 0.3s ease;
}

.nutrition-card:hover {
  transform: translateY(-5px);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: var(--el-color-primary);
}

.unit {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-left: 4px;
}

.charts-section {
  background: var(--el-bg-color);
  border-radius: 8px;
  padding: 20px;
  height: 400px;
  display: flex;
  flex-direction: column;
}

.chart-controls {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}

.chart-container {
  flex: 1;
  position: relative;
}

.chart {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

/* 营养卡片主题色 */
.nutrition-card.calories :deep(.el-card__header) {
  background: linear-gradient(135deg, #FF9A9E 0%, #FAD0C4 100%);
  color: white;
}

.nutrition-card.protein :deep(.el-card__header) {
  background: linear-gradient(135deg, #91CC75 0%, #76B947 100%);
  color: white;
}

.nutrition-card.fat :deep(.el-card__header) {
  background: linear-gradient(135deg, #FAC858 0%, #F3B141 100%);
  color: white;
}

.nutrition-card.carbs :deep(.el-card__header) {
  background: linear-gradient(135deg, #5470C6 0%, #3F51B5 100%);
  color: white;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .nutrition-summary {
    padding: 10px;
  }
  
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .date-filter {
    width: 100%;
  }
  
  .charts-section {
    height: 300px;
  }
}
</style> 