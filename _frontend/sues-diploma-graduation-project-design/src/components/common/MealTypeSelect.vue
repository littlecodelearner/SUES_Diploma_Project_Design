<template>
  <el-select
    v-model="selectedValue"
    :placeholder="placeholder"
    :clearable="clearable"
    :disabled="disabled"
    :size="size"
    @change="handleChange"
    @clear="handleClear"
    class="meal-type-select"
  >
    <el-option
      v-for="option in mealTypeOptions"
      :key="option.value"
      :label="option.label"
      :value="option.value"
    />
  </el-select>
</template>

<script setup>
import {computed, ref} from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '选择进餐类型'
  },
  clearable: {
    type: Boolean,
    default: true
  },
  disabled: {
    type: Boolean,
    default: false
  },
  size: {
    type: String,
    default: 'default'
  }
})

const emit = defineEmits(['update:modelValue', 'change', 'clear'])

// 当前选中的值
const selectedValue = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 进餐类型数据 - 使用硬编码选项
const mealTypeOptions = ref([
  { label: '早餐', value: 'BREAKFAST' },
  { label: '午餐', value: 'LUNCH' },
  { label: '下午茶', value: 'AFTERNOONTEA' },
  { label: '晚餐', value: 'DINNER' },
  { label: '零食', value: 'SNACK' },
  { label: '加餐', value: 'EXTRAS' },
  { label: '夜宵', value: 'NIGHTSNACK' },
  { label: '其他', value: 'OTHER' }
])

// 标签类型映射
const typesMap = ref({
  typeTagMap: {
    'BREAKFAST': 'success',
    'LUNCH': 'warning',
    'AFTERNOONTEA': 'info',
    'DINNER': 'danger',
    'SNACK': 'info',
    'EXTRAS': 'warning',
    'NIGHTSNACK': 'danger',
    'OTHER': '' 
  }
})

// 获取进餐类型显示标签
const getMealTypeLabel = (type) => {
  const option = mealTypeOptions.value.find(opt => opt.value === type)
  return option ? option.label : type
}

// 处理选择变化
const handleChange = (value) => {
  emit('change', value)
}

// 处理清除
const handleClear = () => {
  emit('clear')
}

// 导出获取标签类型的方法，方便外部使用
defineExpose({
  getMealTypeTag: (type) => typesMap.value.typeTagMap[type] || '',
  getMealTypeLabel,
  refresh: () => {} // 空函数，保留API兼容性
})
</script>

<style scoped>
.meal-type-select {
  width: 100%;
}
</style> 