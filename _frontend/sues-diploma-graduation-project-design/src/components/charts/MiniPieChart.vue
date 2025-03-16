<template>
  <div ref="chartRef" class="mini-pie-chart"></div>
</template>

<script setup>
import {onMounted, ref, watch} from 'vue'
import * as echarts from 'echarts/core'
import {PieChart} from 'echarts/charts'
import {LegendComponent} from 'echarts/components'
import {SVGRenderer} from 'echarts/renderers'

echarts.use([PieChart, LegendComponent, SVGRenderer])

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
    legend: {
      orient: 'vertical',
      right: 0,
      top: 'center',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: {
        fontSize: 12
      }
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 4
      },
      label: {
        show: false
      },
      emphasis: {
        label: {
          show: false
        }
      },
      data: props.data.map(item => ({
        name: item.name,
        value: item.value,
        itemStyle: {
          color: item.name === '男' ? '#409eff' : '#ff69b4'
        }
      }))
    }]
  }
  
  chart.setOption(option)
}

watch(() => props.data, () => {
  if (chart) {
    chart.setOption({
      series: [{
        data: props.data.map(item => ({
          name: item.name,
          value: item.value,
          itemStyle: {
            color: item.name === '男' ? '#409eff' : '#ff69b4'
          }
        }))
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
.mini-pie-chart {
  width: 100%;
  height: 100%;
}
</style> 