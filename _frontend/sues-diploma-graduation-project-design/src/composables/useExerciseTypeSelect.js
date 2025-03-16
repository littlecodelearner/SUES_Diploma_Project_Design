import {computed, ref} from 'vue'
import {ElMessage} from 'element-plus'
import request from '@/utils/request'

/**
 * 运动类型选择器的组合式函数
 * @returns {Object} 返回运动类型选择器所需的状态和方法
 */
export function useExerciseTypeSelect() {
  // 状态
  const exerciseTypes = ref([])
  const loading = ref(false)
  const selectedTypes = ref([])
  const searchQuery = ref('')

  // 获取所有运动类型
  const fetchExerciseTypes = async () => {
    try {
      loading.value = true
      const response = await request({
        url: '/exerciseTypes',
        method: 'get'
      })
      
      if (response.code === 200) {
        exerciseTypes.value = response.data || []
      } else {
        ElMessage.error(response.message || '获取运动类型列表失败')
      }
    } catch (error) {
      console.error('获取运动类型列表失败:', error)
      ElMessage.error('获取运动类型列表失败')
    } finally {
      loading.value = false
    }
  }

  // 过滤运动类型
  const filteredExerciseTypes = computed(() => {
    if (!searchQuery.value) return exerciseTypes.value
    
    const query = searchQuery.value.toLowerCase()
    return exerciseTypes.value.filter(type => 
      type.exerciseName.toLowerCase().includes(query)
    )
  })

  // 处理搜索
  const handleSearch = (query) => {
    searchQuery.value = query
  }

  // 处理选择变化
  const handleSelectionChange = (selection) => {
    selectedTypes.value = selection
  }

  // 重置选择器状态
  const reset = () => {
    selectedTypes.value = []
    searchQuery.value = ''
  }

  return {
    // 状态
    exerciseTypes,
    loading,
    selectedTypes,
    searchQuery,
    filteredExerciseTypes,
    
    // 方法
    fetchExerciseTypes,
    handleSearch,
    handleSelectionChange,
    reset
  }
} 