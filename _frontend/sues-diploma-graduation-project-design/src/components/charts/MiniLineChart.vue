<template>
  <div ref="chartRef" class="mini-line-chart"></div>
</template>

<script setup>
import {onMounted, ref, watch} from 'vue'
import * as echarts from 'echarts/core'
import {LineChart} from 'echarts/charts'
import {GridComponent} from 'echarts/components'
import {SVGRenderer} from 'echarts/renderers'

echarts.use([LineChart, GridComponent, SVGRenderer])

const props = defineProps({
  data: {
    type: Array,
    required: true
  }
})

const chartRef = ref(null)
let chart = null

const initChart = () => {
  if (!chartRef.value) return
  
  chart = echarts.init(chartRef.value, null, {
    renderer: 'svg',
    width: 'auto',
    height: 'auto'
  })
  
  const option = {
    grid: {
      top: 5,
      right: 5,
      bottom: 5,
      left: 5,
      containLabel: false
    },
    xAxis: {
      type: 'category',
      show: false,
      boundaryGap: false
    },
    yAxis: {
      type: 'value',
      show: false
    },
    series: [{
      data: props.data,
      type: 'line',
      smooth: true,
      symbol: 'none',
      lineStyle: {
        color: '#67c23a',
        width: 2
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(103, 194, 58, 0.2)' },
          { offset: 1, color: 'rgba(103, 194, 58, 0)' }
        ])
      }
    }]
  }
  
  chart.setOption(option)
}

watch(() => props.data, () => {
  if (chart) {
    chart.setOption({
      series: [{
        data: props.data
      }]
    })
  }
}, { deep: true })

onMounted(() => {
  initChart()
  
  window.addEventListener('resize', () => {
    chart?.resize()
  })
})
</script>

<style scoped>
.mini-line-chart {
  width: 100%;
  height: 100%;
}
</style> 