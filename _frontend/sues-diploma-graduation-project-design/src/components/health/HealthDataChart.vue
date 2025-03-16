<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DataZoomComponent,
  ToolboxComponent
} from 'echarts/components'
import * as echarts from 'echarts/core'
import { ElMessage } from 'element-plus'
import { getHealthDataList, getHealthDataTrend } from '@/api/healthData'

// 注册必需的组件
use([
  CanvasRenderer,
  LineChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DataZoomComponent,
  ToolboxComponent
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

// 检查浏览器是否支持 Canvas
const canUseCanvas = () => {
  const canvas = document.createElement('canvas')
  return !!(canvas.getContext && canvas.getContext('2d'))
}

const chartRef = ref(null)
let chart = null
const loading = ref(false)
const useServerRendering = ref(false)
const serverRenderedImage = ref('')

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  
  chart = echarts.init(chartRef.value)
  chart.setOption({
    title: {
      text: '健康数据趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['心率', '体重', 'BMI'],
      top: 30
    },
    toolbox: {
      feature: {
        dataZoom: {
          yAxisIndex: 'none'
        },
        restore: {},
        saveAsImage: {}
      },
      right: 20
    },
    grid: {
      left: '10%',
      right: '10%',
      bottom: '10%',
      containLabel: true
    },
    dataZoom: [
      {
        type: 'inside',
        start: 0,
        end: 100
      },
      {
        start: 0,
        end: 100
      }
    ],
    xAxis: {
      type: 'time',
      boundaryGap: false
    },
    yAxis: [
      {
        name: '心率(bpm)',
        type: 'value',
        splitLine: {
          show: false
        }
      },
      {
        name: '体重(kg)/BMI',
        type: 'value',
        splitLine: {
          show: false
        }
      }
    ],
    series: [
      {
        name: '心率',
        type: 'line',
        smooth: true,
        yAxisIndex: 0,
        data: [],
        itemStyle: {
          color: '#FF6B6B'
        }
      },
      {
        name: '体重',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: [],
        itemStyle: {
          color: '#4ECDC4'
        }
      },
      {
        name: 'BMI',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: [],
        itemStyle: {
          color: '#45B7D1'
        }
      }
    ]
  })
  
  // 处理窗口大小变化
  window.addEventListener('resize', handleResize)
}

// 更新图表数据
const updateChartData = async () => {
  if (!props.userId) return
  
  try {
    loading.value = true

    // 检查是否需要使用服务端渲染
    if (!canUseCanvas()) {
      useServerRendering.value = true
      const response = await getHealthDataTrend({
        userId: props.userId,
        offset: 0,
        size: 1000,
        startDate: props.dateRange?.[0] ? new Date(props.dateRange[0]).toISOString() : null,
        endDate: props.dateRange?.[1] ? new Date(props.dateRange[1]).toISOString() : null
      })
      
      if (response.code === 200) {
        serverRenderedImage.value = 'data:image/png;base64,' + response.data
      } else {
        ElMessage.error('获取趋势图失败')
      }
      return
    }

    // 使用 ECharts 渲染
    const response = await getHealthDataList({
      userId: props.userId,
      offset: 0,
      size: 1000,
      startDate: props.dateRange?.[0] ? new Date(props.dateRange[0]).toISOString() : null,
      endDate: props.dateRange?.[1] ? new Date(props.dateRange[1]).toISOString() : null
    })
    
    if (response.code === 200 && response.data) {
      const data = response.data
      const heartRateData = []
      const weightData = []
      const bmiData = []
      
      data.forEach(item => {
        const time = new Date(item.measurementDate).getTime()
        heartRateData.push([time, item.heartRate])
        weightData.push([time, item.weight])
        bmiData.push([time, item.bmi])
      })
      
      chart?.setOption({
        series: [
          { data: heartRateData },
          { data: weightData },
          { data: bmiData }
        ]
      })
    }
  } catch (error) {
    console.error('获取健康数据趋势失败:', error)
    ElMessage.error('获取趋势数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理窗口大小变化
const handleResize = () => {
  chart?.resize()
}

// 监听日期范围变化
watch(() => props.dateRange, () => {
  updateChartData()
}, { deep: true })

onMounted(() => {
  initChart()
  updateChartData()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>

<template>
  <div class="health-data-chart">
    <!-- 服务端渲染的图表 -->
    <div v-if="useServerRendering" class="server-rendered-chart">
      <img 
        v-if="serverRenderedImage"
        :src="serverRenderedImage"
        alt="健康数据趋势图"
        class="trend-chart-image"
      />
      <el-empty v-else description="暂无趋势数据" />
    </div>
    
    <!-- 客户端渲染的图表 -->
    <div 
      v-else
      ref="chartRef" 
      class="chart-container"
      :class="{ loading }"
    />
  </div>
</template>

<style scoped>
.health-data-chart {
  width: 100%;
  height: 100%;
  min-height: 400px;
}

.chart-container {
  width: 100%;
  height: 100%;
  min-height: 400px;
}

.loading {
  opacity: 0.6;
  cursor: wait;
}

.server-rendered-chart {
  width: 100%;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.trend-chart-image {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
}

@media screen and (max-width: 768px) {
  .health-data-chart {
    min-height: 300px;
  }
  
  .chart-container {
    min-height: 300px;
  }

  .server-rendered-chart {
    min-height: 300px;
  }
}
</style> 