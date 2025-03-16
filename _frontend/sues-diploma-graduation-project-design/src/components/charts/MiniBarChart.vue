<template>
  <div ref="chartRef" class="mini-bar-chart"></div>
</template>

<script setup>
import {onMounted, ref, watch} from 'vue'
import * as echarts from 'echarts/core'
import {BarChart} from 'echarts/charts'
import {GridComponent} from 'echarts/components'
import {SVGRenderer} from 'echarts/renderers'

echarts.use([BarChart, GridComponent, SVGRenderer])

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
      data: ['一', '二', '三', '四', '五', '六', '日']
    },
    yAxis: {
      type: 'value',
      show: false
    },
    series: [{
      data: props.data,
      type: 'bar',
      barWidth: '60%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#3b82f6' },
          { offset: 1, color: '#60a5fa' }
        ]),
        borderRadius: [4, 4, 0, 0]
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
.mini-bar-chart {
  width: 100%;
  height: 100%;
}
</style> 