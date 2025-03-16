<template>
  <div class="mini-trend-chart" :style="{height: height + 'px'}">
    <svg width="100%" :height="height">
      <path :d="pathData" :stroke="color" fill="none" stroke-width="1.5"></path>
      <path :d="areaPathData" :fill="fillColor" fill-opacity="0.1"></path>
    </svg>
  </div>
</template>

<script setup>
import {computed} from 'vue'

const props = defineProps({
  data: { 
    type: Array, 
    default: () => [0, 0, 0, 0, 0] 
  },
  color: { 
    type: String, 
    default: '#409EFF' 
  },
  height: { 
    type: Number, 
    default: 20 
  }
})

// 计算SVG路径数据
const pathData = computed(() => {
  if (!props.data.length) return ''
  
  const width = 100 // 宽度百分比
  const max = Math.max(...props.data, 1) // 防止除以零
  const min = Math.min(...props.data, 0)
  const range = max - min || 1 // 防止除以零
  
  // 计算每个点的x和y坐标
  const points = props.data.map((value, index) => {
    const x = (index / (props.data.length - 1 || 1)) * width
    const y = props.height - ((value - min) / range) * props.height
    return { x, y }
  })
  
  // 构建SVG路径
  return points.map((point, i) => 
    (i === 0 ? 'M' : 'L') + point.x + ',' + point.y
  ).join(' ')
})

// 计算填充区域的路径
const areaPathData = computed(() => {
  if (!props.data.length) return ''
  
  const linePoints = pathData.value
  const width = 100
  const lastX = width
  const lastY = props.height
  
  // 添加底部边界点来闭合区域
  return linePoints + ' L' + lastX + ',' + lastY + ' L0,' + lastY + ' Z'
})

// 计算填充颜色
const fillColor = computed(() => props.color)
</script>

<style scoped>
.mini-trend-chart {
  width: 100%;
  overflow: hidden;
  margin-top: 4px;
}
</style> 