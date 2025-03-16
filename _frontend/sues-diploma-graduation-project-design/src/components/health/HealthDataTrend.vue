<template>
  <div class="health-data-trend">
    <div v-loading="loading" class="chart-container">
      <div v-if="error" class="error-container">
        <el-icon class="error-icon"><Warning /></el-icon>
        <p class="error-message">{{ error }}</p>
        <p class="error-detail">{{ detailedError }}</p>
      </div>
      <div v-else-if="!hasData && !loading" class="empty-container">
        <el-icon class="empty-icon"><DataLine /></el-icon>
        <p class="empty-message">暂无健康数据记录</p>
      </div>
      <div v-else class="chart-wrapper" id="healthDataTrendChart"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getAllHealthData } from '@/api/health'
import { Warning, DataLine } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  userId: {
    type: [String, Number],
    required: true
  }
})

const chartRef = ref(null)
const loading = ref(false)
const error = ref(null)
const detailedError = ref('')
const hasData = ref(false)
let chart = null

// 初始化图表
const initChart = () => {
  // 使用固定ID而不是ref
  const chartDom = document.getElementById('healthDataTrendChart')
  if (!chartDom) {
    console.error('找不到图表DOM元素')
    return false
  }
  
  try {
    // 强制设置容器尺寸
    chartDom.style.width = '100%'
    chartDom.style.height = '500px'

    // 确保先销毁旧实例
    if (chart) {
      chart.dispose()
      chart = null
    }
    
    // 创建新图表实例
    chart = echarts.init(chartDom)
    
    const option = {
      title: {
        text: '健康数据趋势',
        left: 'center',
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross',
          label: {
            backgroundColor: '#6a7985'
          }
        },
        formatter: function(params) {
          let result = params[0].axisValue + '<br/>'
          params.forEach(param => {
            const value = param.value
            // 对BMI进行截断，最多显示1位小数
            const displayValue = param.seriesName === 'BMI' ? parseFloat(value).toFixed(1) : value
            const unit = param.seriesName === '心率' ? ' bpm' : 
                        param.seriesName === '体重' ? ' kg' : ''
            result += `${param.seriesName}: ${displayValue}${unit}<br/>`
          })
          return result
        }
      },
      legend: {
        data: ['心率', '体重', 'BMI'],
        top: 30,
        left: 'center',
        itemGap: 20
      },
      grid: {
        left: '3%',
        right: '5%',
        bottom: '15%',
        top: '15%',
        containLabel: true
      },
      dataZoom: [
        {
          type: 'slider',
          show: true,
          bottom: 20,
          height: 30,
          start: 0,
          end: 100,
          borderColor: 'transparent',
          fillerColor: 'rgba(70,130,180,0.2)',
          handleStyle: {
            color: '#4682b4'
          }
        }
      ],
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: [],
        axisLabel: {
          interval: 0,
          rotate: 45,
          fontSize: 14
        }
      },
      yAxis: [
        {
          type: 'value',
          name: '心率(bpm)',
          position: 'left',
          axisLabel: {
            formatter: '{value} bpm'
          }
        },
        {
          type: 'value',
          name: '体重(kg)',
          position: 'right',
          axisLabel: {
            formatter: '{value} kg'
          },
          splitLine: {
            show: false
          }
        },
        {
          type: 'value',
          name: 'BMI',
          position: 'right',
          offset: 80, // 保持原有偏移量
          min: function(value) {
            return Math.max(0, Math.floor(value.min * 0.8));
          },
          max: function(value) {
            return Math.ceil(value.max * 1.2);
          },
          axisLabel: {
            formatter: function(value) {
              return parseFloat(value).toFixed(1); // 格式化BMI只显示1位小数
            }
          },
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
          data: [],
          itemStyle: {
            color: '#FF6B6B'
          },
          yAxisIndex: 0
        },
        {
          name: '体重',
          type: 'line',
          smooth: true,
          data: [],
          itemStyle: {
            color: '#4ECDC4'
          },
          yAxisIndex: 1
        },
        {
          name: 'BMI',
          type: 'line',
          smooth: true,
          data: [],
          itemStyle: {
            color: '#45B7D1'
          },
          yAxisIndex: 2,
          // 对BMI数据进行格式化处理
          symbolSize: 8,
          emphasis: {
            focus: 'series',
            itemStyle: {
              borderWidth: 5
            }
          }
        }
      ]
    }
    
    chart.setOption(option)
    window.addEventListener('resize', handleResize)
    
    return true
  } catch (err) {
    console.error('图表初始化失败:', err)
    error.value = '图表初始化失败，请刷新页面重试'
    detailedError.value = err.message || '未知错误'
    return false
  }
}

// 更新图表数据
const updateChartData = async () => {
  if (!props.userId) {
    error.value = '用户ID不存在'
    return
  }

  loading.value = true
  error.value = null
  detailedError.value = ''
  hasData.value = false
  
  try {
    const response = await getAllHealthData(props.userId)
    console.log('健康数据响应:', response)
    
    // 数据验证
    if (!response) {
      throw new Error('响应为空')
    }
    
    // 直接提取数据数组
    const dataArray = response.data || []
    
    if (!Array.isArray(dataArray) || dataArray.length === 0) {
      console.log('没有健康数据记录')
      hasData.value = false
      return
    }
    
    // 数据存在，开始处理
    const data = [...dataArray]
    data.sort((a, b) => new Date(a.measurementDate).getTime() - new Date(b.measurementDate).getTime())
    
    const dates = []
    const heartRates = []
    const weights = []
    const bmis = []
    
    data.forEach(item => {
      // 日期格式化
      const date = new Date(item.measurementDate)
      const formattedDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
      dates.push(formattedDate)
      
      // 确保所有数据都是数字
      heartRates.push(parseFloat(item.heartRate || 0))
      weights.push(parseFloat(item.weight || 0))
      bmis.push(parseFloat(item.bmi || 0))
    })
    
    console.log('处理后的数据:', { dates, heartRates, weights, bmis })
    
    // 初始化图表(如果需要)，并设置数据
    if (!chart) {
      const initSuccess = initChart()
      if (!initSuccess) {
        throw new Error('图表初始化失败')
      }
    }
    
    if (chart) {
      chart.setOption({
        xAxis: {
          data: dates,
          axisLabel: {
            interval: Math.max(0, Math.floor(dates.length / 8)),
            rotate: 45
          }
        },
        series: [
          {
            name: '心率',
            data: heartRates
          },
          {
            name: '体重',
            data: weights
          },
          {
            name: 'BMI',
            // 对BMI值进行格式化，确保只保留1位小数
            data: bmis.map(value => parseFloat(value).toFixed(1))
          }
        ]
      })
      hasData.value = true
      
      // 强制重新渲染图表
      setTimeout(() => {
        if (chart) {
          chart.resize()
        }
      }, 100)
    } else {
      throw new Error('图表实例不存在')
    }
  } catch (err) {
    console.error('获取或处理健康数据失败:', err)
    error.value = '获取健康数据失败，请稍后重试'
    detailedError.value = err.message || '未知错误'
    hasData.value = false
  } finally {
    loading.value = false
  }
}

// 监听窗口大小变化
const handleResize = () => {
  if (chart) {
    try {
      chart.resize()
    } catch (err) {
      console.warn('图表调整大小失败:', err)
    }
  }
}

// 监听用户ID变化
watch(() => props.userId, (newId) => {
  if (newId) {
    updateChartData()
  }
})

// 页面加载完成后初始化图表
onMounted(() => {
  // 使用setTimeout等待DOM完全渲染
  setTimeout(() => {
    // 先初始化图表
    initChart()
    // 然后加载数据
    updateChartData()
  }, 500) // 增加延迟时间
})

// 组件卸载时释放资源
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (chart) {
    chart.dispose()
    chart = null
  }
})
</script>

<style scoped>
.health-data-trend {
  width: 100%;
  height: 100%;
  min-height: 480px;
  display: flex;
}

.chart-container {
  width: 100%;
  height: 100%;
  min-height: 480px;
  position: relative;
  padding-top: 0;
}

.chart-wrapper {
  width: 100%;
  height: 480px;
  min-height: 480px;
  position: relative;
}

.error-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 32px;
  text-align: center;
  width: 100%;
  height: 100%;
  min-height: 480px;
}

.error-icon,
.empty-icon {
  font-size: 48px;
  color: var(--el-color-danger);
}

.empty-icon {
  color: var(--el-color-info);
}

.error-message,
.empty-message {
  margin: 0;
  font-size: 16px;
  color: var(--el-text-color-secondary);
}

.error-detail {
  font-size: 12px;
  color: var(--el-color-danger);
  max-width: 80%;
  word-break: break-word;
}

:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(4px);
}

:deep(.el-loading-spinner) {
  .circular {
    animation: rotate 2s linear infinite;
  }
  
  .path {
    stroke-dasharray: 90, 150;
    stroke-dashoffset: 0;
    stroke-width: 2;
    stroke: var(--el-color-primary);
    stroke-linecap: round;
    animation: dash 1.5s ease-in-out infinite;
  }
}

@keyframes rotate {
  100% {
    transform: rotate(360deg);
  }
}

@keyframes dash {
  0% {
    stroke-dasharray: 1, 150;
    stroke-dashoffset: 0;
  }
  50% {
    stroke-dasharray: 90, 150;
    stroke-dashoffset: -35;
  }
  100% {
    stroke-dasharray: 90, 150;
    stroke-dashoffset: -124;
  }
}
</style>