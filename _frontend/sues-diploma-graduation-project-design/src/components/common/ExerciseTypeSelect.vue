<template>
  <div class="exercise-type-select">
    <el-select
      v-model="selectedValue"
      multiple
      filterable
      :loading="loading"
      :placeholder="placeholder"
      class="exercise-select"
      @search="handleSearch"
      @change="handleChange"
    >
      <el-option
        v-for="type in filteredExerciseTypes"
        :key="type.exerciseTypeId"
        :label="type.exerciseName"
        :value="type.exerciseTypeId"
      />
      <template #empty>
        <div class="select-empty">
          <el-icon><Search /></el-icon>
          <span>{{ searchQuery ? '暂无匹配的运动类型' : '暂无数据' }}</span>
        </div>
      </template>
    </el-select>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { useExerciseTypeSelect } from '@/composables/useExerciseTypeSelect'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  placeholder: {
    type: String,
    default: '请选择运动类型'
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

// 使用组合式函数
const {
  exerciseTypes,
  loading,
  searchQuery,
  filteredExerciseTypes,
  fetchExerciseTypes,
  handleSearch,
  handleSelectionChange
} = useExerciseTypeSelect()

// 计算属性：用于双向绑定
const selectedValue = computed({
  get: () => props.modelValue,
  set: (value) => {
    emit('update:modelValue', value)
    handleSelectionChange(value)
  }
})

// 处理选择变化
const handleChange = (value) => {
  emit('change', value)
}

// 组件挂载时获取数据
onMounted(() => {
  fetchExerciseTypes()
})
</script>

<style scoped>
.exercise-type-select {
  width: 100%;
}

.exercise-select {
  width: 100%;
}

.select-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  color: var(--el-text-color-secondary);
}

.select-empty .el-icon {
  font-size: 16px;
}

:deep(.el-select-dropdown__wrap) {
  max-height: 300px;
}

:deep(.el-select-dropdown__list) {
  padding: 0;
  margin: 0;
}

:deep(.el-select-dropdown__item) {
  padding: 8px 12px;
}

:deep(.el-select-dropdown__item.selected) {
  color: var(--el-color-primary);
  font-weight: bold;
}

:deep(.el-select-dropdown__item:hover) {
  background-color: var(--el-color-primary-light-9);
}

:deep(.el-select__tags) {
  max-width: calc(100% - 30px);
}

:deep(.el-select__tags-text) {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style> 