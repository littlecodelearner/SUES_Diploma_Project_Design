<script setup>
import {computed, onMounted, ref, watch} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/store/user'
import {getHealthProfile} from '@/api/health'
import {getHealthDataList} from '@/api/healthData'
import {listPaginatedDietRecordsByTimeRange} from '@/api/diet'
import {deleteExerciseRecords, getExerciseRecords, updateExerciseRecords} from '@/api/exercise'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  Aim,
  ArrowDown,
  Bowl,
  Calendar,
  DataLine,
  Delete,
  Document,
  Flag,
  Histogram,
  Lock,
  MapLocation,
  Plus,
  Setting,
  SwitchButton,
  Timer,
  TrendCharts,
  User
} from '@element-plus/icons-vue'
import HealthDataCardView from '@/components/health/HealthDataCardView.vue'
import DietRecordForm from '@/components/diet/DietRecordForm.vue'
import DietNutritionSummary from '@/components/diet/DietNutritionSummary.vue'
import ExerciseRecordForm from '@/components/exercise/ExerciseRecordForm.vue'
import HealthGoalList from '@/components/health/HealthGoalList.vue'
import WelcomeGuide from '@/components/WelcomeGuide.vue'
import HealthGoalBatchForm from '@/components/health/HealthGoalBatchForm.vue'
import ExerciseRecordCard from '@/components/exercise/ExerciseRecordCard.vue'
import {formatDate, formatDateWithChineseTimezone} from '@/utils/date'
import DietRecordList from '@/components/diet/DietRecordList.vue'
import HealthDataTrend from '@/components/health/HealthDataTrend.vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const activeTab = ref('profile')

// 检查登录状态
const checkAuth = () => {
  if (!userStore.isAuthenticated) {
    ElMessage({
      type: 'warning',
      dangerouslyUseHTMLString: true,
      message: `
        <div class="message-container warning-message">
          <div class="message-icon">
            <span class="icon">🔒</span>
            <div class="icon-background"></div>
          </div>
          
          <div class="message-content">
            <div class="message-header">
              <h3>需要登录</h3>
              <p>访问仪表板需要先登录您的账号</p>
            </div>
            
            <div class="message-body">
              <div class="info-item">
                <span class="info-icon">💡</span>
                <span>即将为您跳转到登录页面...</span>
              </div>
            </div>
          </div>
        </div>
      `,
      duration: 3000,
      showClose: true,
      customClass: 'global-message warning'
    })

    // 延迟跳转到登录页面
    setTimeout(() => {
      router.push({
        path: '/login',
        query: { redirect: router.currentRoute.value.fullPath }
      })
    }, 2000)
    return false
  }
  return true
}

// 健康档案数据
const healthData = ref(null)
const recentExercises = ref([])

// 获取健康档案数据
const fetchHealthProfile = async () => {
  if (!userId.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    loading.value = true
    const response = await getHealthProfile(userId.value)
    
    if (response.code === 200) {
      healthData.value = response.data
      // 更新 store 中的健康档案数据
      userStore.setHealthProfile(response.data)
    } else if (response.code === 1005) {
      ElMessage({
        type: 'warning',
        message: '⚠️ 未找到健康档案',
        duration: 2000
      })
      router.push('/health/create')
    } else {
      ElMessage.error('获取健康档案失败，请稍后重试')
    }
  } catch (error) {
    console.error('获取健康档案失败:', error)
    if (error.response?.status === 404) {
      ElMessage({
        type: 'warning',
        message: '⚠️ 未找到健康档案',
        duration: 2000
      })
      router.push('/health/create')
    } else {
      ElMessage.error('获取健康档案失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 健康数据相关
const healthDataLoading = ref(false)
const healthDataList = ref([])
const healthDataPagination = ref({
  currentPage: 1,
  pageSize: 15,
  total: 0
})

// 添加日期筛选的响应式引用
const dateRange = ref([])

// 添加用户ID的响应式引用
const userId = computed(() => {
  return Number(localStorage.getItem('userId')) || null
})

// 统计数据
const statsData = ref({
  today: {
    duration: 0,
    distance: 0,
    count: 0,
    avgHeartRate: 0
  },
  week: {
    duration: 0,
    distance: 0,
    count: 0,
    avgHeartRate: 0
  }
})

// 计算统计数据
const calculateStats = (records) => {
  if (!records || !records.length) return

  console.log('开始计算统计数据，记录数:', records.length)
  
  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const weekStart = new Date(today)
  weekStart.setDate(today.getDate() - today.getDay()) // 获取本周开始日期

  console.log('今日日期:', formatDate(today), '本周开始日期:', formatDate(weekStart))

  // 今日记录
  const todayRecords = records.filter(record => {
    try {
      if (!record.exerciseDate) {
        console.warn('记录没有日期:', record)
        return false
      }
      
      // 确保日期是有效的
      const recordDate = new Date(record.exerciseDate)
      if (isNaN(recordDate.getTime())) {
        console.warn('无效的日期格式:', record.exerciseDate)
        return false
      }
      
      // 用于调试
      console.log('记录日期:', formatDate(recordDate), 
                 '是否为今日:', recordDate.getFullYear() === today.getFullYear() && 
                                recordDate.getMonth() === today.getMonth() && 
                                recordDate.getDate() === today.getDate())
      
      // 比较日期部分，忽略时间部分
      return recordDate.getFullYear() === today.getFullYear() && 
             recordDate.getMonth() === today.getMonth() && 
             recordDate.getDate() === today.getDate()
    } catch (e) {
      console.error('日期解析错误:', e, record.exerciseDate)
      return false
    }
  })

  // 本周记录
  const weekRecords = records.filter(record => {
    try {
      if (!record.exerciseDate) return false
      
      const recordDate = new Date(record.exerciseDate)
      if (isNaN(recordDate.getTime())) {
        return false
      }
      
      return recordDate >= weekStart
    } catch (e) {
      console.error('日期解析错误:', e, record.exerciseDate)
      return false
    }
  })

  console.log('今日记录数:', todayRecords.length, '本周记录数:', weekRecords.length)

  // 计算今日统计
  statsData.value.today = {
    duration: todayRecords.reduce((sum, record) => sum + (record.duration || 0), 0),
    distance: todayRecords.reduce((sum, record) => sum + (record.distance || 0), 0),
    count: todayRecords.length,
    avgHeartRate: todayRecords.length ? 
      Math.round(todayRecords.reduce((sum, record) => sum + (record.heartRate || 0), 0) / todayRecords.length) : 0
  }

  // 计算本周统计
  statsData.value.week = {
    duration: weekRecords.reduce((sum, record) => sum + (record.duration || 0), 0),
    distance: weekRecords.reduce((sum, record) => sum + (record.distance || 0), 0),
    count: weekRecords.length,
    avgHeartRate: weekRecords.length ? 
      Math.round(weekRecords.reduce((sum, record) => sum + (record.heartRate || 0), 0) / weekRecords.length) : 0
  }
  
  console.log('计算完成的统计数据:', JSON.stringify(statsData.value, null, 2))
}

// 更新快速统计数据的计算属性
const quickStats = computed(() => [
  {
    title: '今日运动时长',
    value: statsData.value.today.duration || '0',
    unit: '分钟',
    icon: Timer,
    color: '#409EFF'
  },
  {
    title: '累计运动距离',
    value: (statsData.value.week.distance || 0).toFixed(2),
    unit: '公里',
    icon: MapLocation,
    color: '#67C23A'
  },
  {
    title: '本周运动次数',
    value: statsData.value.week.count || '0',
    unit: '次',
    icon: Calendar,
    color: '#E6A23C'
  },
  {
    title: '平均心率',
    value: statsData.value.today.avgHeartRate || '0',
    unit: 'bpm',
    icon: TrendCharts,
    color: '#F56C6C'
  }
])

// 运动记录相关
const exerciseRecords = ref([])
const exerciseTotal = ref(0)
const exerciseLoading = ref(false)
const exercisePagination = ref({
  current: 1,
  size: 15
})
const showExerciseForm = ref(false)
const editingExerciseRecord = ref(null)
const selectedExerciseRecords = ref([])
const exerciseFormMode = ref('add') // 'add', 'edit', 或 'batchEdit'

// 运动记录日期范围
const exerciseDateRange = ref(null)

// 处理运动记录日期范围变化
const handleExerciseRecordDateRangeChange = async (range) => {
  console.log('运动记录日期范围变化:', range)
  exerciseDateRange.value = range
  exercisePagination.value.current = 1 // 重置页码
  await fetchExerciseRecords() // 重新获取数据
}

// 获取运动记录列表
const fetchExerciseRecords = async () => {
  if (!userStore.userId) {
    console.warn('用户ID不存在，无法获取运动记录')
    return
  }

  try {
    exerciseLoading.value = true
    console.log('====== 开始获取运动记录 ======')
    console.log('当前分页状态:', JSON.stringify(exercisePagination.value))
    
    // 构建基本请求参数
    const params = {
      userId: userStore.userId,
      current: exercisePagination.value.current,
      size: exercisePagination.value.size,
      isAsc: false // 最新的记录显示在前面
    }
    
    // 添加日期范围参数
    if (exerciseDateRange.value && Array.isArray(exerciseDateRange.value) && exerciseDateRange.value.length === 2) {
      const [startDate, endDate] = exerciseDateRange.value
      params.startDateTime = formatDateWithChineseTimezone(startDate)
      params.endDateTime = formatDateWithChineseTimezone(endDate)
      console.log('添加日期过滤 - 开始:', formatDate(startDate), '结束:', formatDate(endDate))
    } else {
      console.log('未设置日期过滤，将获取所有历史记录')
    }
    
    console.log('API请求参数:', JSON.stringify(params, null, 2))
    
    const response = await getExerciseRecords(params)
    
    if (response.code === 200) {
      // 确保数据是数组形式
      const dataList = Array.isArray(response.data.dataList) ? response.data.dataList : []
      exerciseRecords.value = dataList
      exerciseTotal.value = response.data.total || 0
      
      console.log(`成功获取运动记录: ${dataList.length}条数据，总记录数: ${exerciseTotal.value}`)
      
      // 检测多运动类型记录情况
      if (dataList.length > 0) {
        const uniqueRecordIds = new Set(dataList.map(record => record.exerciseRecordId))
        if (uniqueRecordIds.size < dataList.length) {
          const difference = dataList.length - uniqueRecordIds.size
          console.warn(`⚠️ 注意: API返回了${dataList.length}行数据，但只包含${uniqueRecordIds.size}条唯一记录，有${difference}条记录包含多个运动类型`)
          console.warn('这可能导致分页显示的记录数少于请求的数量，这是由数据结构决定的')
        }
        
        // 每条记录日志
        console.log('首条记录:', JSON.stringify(dataList[0], null, 2))
        console.log('末条记录:', JSON.stringify(dataList[dataList.length - 1], null, 2))
        
        // 检查每条记录的运动类型
        const hasEmptyTypes = dataList.some(record => 
          !record.exerciseTypesList || record.exerciseTypesList.length === 0
        )
        
        if (hasEmptyTypes) {
          console.log('警告: 部分记录没有运动类型')
        }
        
        // 确保统计数据计算正确
        calculateStats(dataList)
      } else {
        console.log('未获取到运动记录数据')
        // 重置统计数据
        statsData.value = {
          today: { duration: 0, distance: 0, count: 0, avgHeartRate: 0 },
          week: { duration: 0, distance: 0, count: 0, avgHeartRate: 0 }
        }
      }
      console.log('计算后的统计数据:', JSON.stringify(statsData.value, null, 2))
    } else if (response.code === 1010) {
      console.log('API返回1010代码 - 没有找到记录')
      // 没有找到记录
      exerciseRecords.value = []
      exerciseTotal.value = 0
      // 重置统计数据
      statsData.value = {
        today: { duration: 0, distance: 0, count: 0, avgHeartRate: 0 },
        week: { duration: 0, distance: 0, count: 0, avgHeartRate: 0 }
      }
      
      ElMessage({
        type: 'info',
        message: '💡 暂无运动记录\n✨ 开始记录您的运动数据吧！',
        duration: 5000,
        showClose: true
      })
    } else {
      console.error('API错误:', response.message || '获取运动记录失败')
      ElMessage.error(response.message || '获取运动记录失败')
    }
  } catch (error) {
    console.error('获取运动记录失败:', error)
    ElMessage.error('获取运动记录失败，请稍后重试')
    exerciseRecords.value = []
    exerciseTotal.value = 0
  } finally {
    console.log('====== 结束获取运动记录 ======')
    exerciseLoading.value = false
  }
}

// 处理编辑运动记录
const handleEditExercise = (record) => {
  editingExerciseRecord.value = record
  exerciseFormMode.value = 'edit'
  showExerciseForm.value = true
}

// 处理批量修改运动记录
const handleBatchEditExercise = () => {
  if (selectedExerciseRecords.value.length === 0) {
    ElMessage.warning('请先选择要修改的运动记录')
    return
  }
  // 找出选中的记录对象
  const selectedRecordObjects = exerciseRecords.value.filter(
    record => selectedExerciseRecords.value.includes(record.exerciseRecordId)
  )
  editingExerciseRecord.value = selectedRecordObjects
  exerciseFormMode.value = 'batchEdit'
  showExerciseForm.value = true
}

// 处理批量删除运动记录
const handleBatchDeleteExercise = async () => {
  if (selectedExerciseRecords.value.length === 0) {
    ElMessage.warning('请先选择要删除的运动记录')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除所选的 ${selectedExerciseRecords.value.length} 条运动记录吗？此操作不可恢复。`,
      '批量删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        draggable: true
      }
    )

    exerciseLoading.value = true
    const response = await deleteExerciseRecords(selectedExerciseRecords.value)

    if (response.code === 200) {
      ElMessage.success(`成功删除 ${selectedExerciseRecords.value.length} 条运动记录`)
      selectedExerciseRecords.value = []
      await fetchExerciseRecords()
    } else if (response.code === 1030) {
      ElMessage.warning('部分或全部记录已不存在，请刷新页面')
      selectedExerciseRecords.value = []
      await fetchExerciseRecords()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除运动记录失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  } finally {
    exerciseLoading.value = false
  }
}

// 处理运动记录表单成功提交
const handleExerciseFormSuccess = async (updatedRecords) => {
  try {
    exerciseLoading.value = true
    
    // 将更新的记录转换为API所需的格式
    const recordsToUpdate = Array.isArray(updatedRecords) ? updatedRecords : [updatedRecords]
    
    const response = await updateExerciseRecords(recordsToUpdate)
    
    if (response.code === 200) {
      ElMessage.success(
        recordsToUpdate.length > 1 ? 
        `成功更新 ${recordsToUpdate.length} 条运动记录` : 
        '运动记录更新成功'
      )
      // 重置状态
      showExerciseForm.value = false
      editingExerciseRecord.value = null
      selectedExerciseRecords.value = []
      exerciseFormMode.value = 'add'
      // 刷新记录列表
      await fetchExerciseRecords()
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('更新运动记录失败:', error)
    ElMessage.error('更新失败，请稍后重试')
  } finally {
    exerciseLoading.value = false
  }
}

// 处理运动记录选择变化
const handleExerciseSelectionChange = (selection) => {
  selectedExerciseRecords.value = selection
}

// 处理运动记录删除
const handleDeleteExercise = async (record) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条运动记录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    exerciseLoading.value = true
    const response = await deleteExerciseRecords([record.exerciseRecordId])
    
    if (response.code === 200) {
      ElMessage.success('删除成功')
      await fetchExerciseRecords()
    } else if (response.code === 1030) {
      ElMessage.warning('记录已不存在，请刷新页面')
      await fetchExerciseRecords()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除运动记录失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  } finally {
    exerciseLoading.value = false
  }
}

// 处理分页变化
const handleExercisePageChange = (page) => {
  exercisePagination.value.current = page
  fetchExerciseRecords()
}

// 处理每页数量变化
const handleExerciseSizeChange = (size) => {
  exercisePagination.value.size = size
  exercisePagination.value.current = 1
  fetchExerciseRecords()
}

// 监听用户ID变化
watch(() => userStore.userId, (newId) => {
  if (newId) {
    fetchExerciseRecords()
  }
})

// 饮食记录相关
const dietRecordListRef = ref(null)
const showDietRecordForm = ref(false)
const editingDietRecord = ref(null)
const dietRecordLoading = ref(false)
const dietRecordList = ref([])
const dietRecordTotal = ref(0)
const dietRecordPagination = ref({
  current: 1,
  size: 15
})
const selectedDietRecords = ref([])

// 处理修改饮食记录
const handleEditDietRecord = (record) => {
  editingDietRecord.value = { ...record }
  showDietRecordForm.value = true
}

// 处理批量修改饮食记录
const handleBatchEditDietRecord = (records) => {
  editingDietRecord.value = records
  showDietRecordForm.value = true
}

// 处理饮食记录选择变化
const handleDietRecordSelectionChange = (selection) => {
  selectedDietRecords.value = selection
}

// 处理饮食记录操作成功
const handleDietRecordSuccess = () => {
  showDietRecordForm.value = false
  editingDietRecord.value = null
  selectedDietRecords.value = []
  ElMessage.success('操作成功')
  fetchDietRecordList()
}

// 处理分页大小变化
const handleDietRecordSizeChange = (size) => {
  dietRecordPagination.value.size = size
  dietRecordPagination.value.current = 1 // 重置到第一页
  fetchDietRecordList()
}

// 处理页码变化
const handleDietRecordPageChange = (page) => {
  dietRecordPagination.value.current = page
  fetchDietRecordList()
}

// 获取饮食记录列表
const fetchDietRecordList = async () => {
  if (!userStore.userId) {
    console.warn('用户ID不存在，无法获取饮食记录')
    return
  }
  
  try {
    dietRecordLoading.value = true
    console.log('====== 开始获取饮食记录 ======')
    console.log('当前分页状态:', JSON.stringify(dietRecordPagination.value))
    
    const listRef = dietRecordListRef.value
    const filters = listRef?.getFilters() || {}
    
    // 构建请求参数
    const params = {
      userId: userStore.userId,
      current: dietRecordPagination.value.current,
      size: dietRecordPagination.value.size,
      startDateTime: filters.dateRange?.[0] || null,
      endDateTime: filters.dateRange?.[1] || null,
      mealType: filters.mealType?.trim() || null,
      isAsc: false
    }
    
    console.log('API请求参数:', JSON.stringify(params, null, 2))
    
    const response = await listPaginatedDietRecordsByTimeRange(params)
    
    if (response.code === 200) {
      console.log(`成功获取饮食记录: ${response.data.dataList.length}条数据，总记录数: ${response.data.total}`)
      // 更新分页信息
      dietRecordPagination.value = {
        current: response.data.current,
        size: response.data.size
      }
      dietRecordList.value = response.data.dataList
      dietRecordTotal.value = response.data.total
    } else if (response.code === 1010) {
      dietRecordList.value = []
      dietRecordTotal.value = 0
      
      ElMessage({
        type: 'info',
        message: '💡 暂无饮食记录\n✨ 开始记录您的饮食习惯吧！',
        duration: 5000,
        showClose: true
      })
    } else {
      console.error('API错误:', response.message || '获取饮食记录失败')
      ElMessage.error(response.message || '获取饮食记录失败')
    }
  } catch (error) {
    console.error('获取饮食记录失败:', error)
    ElMessage.error('获取饮食记录失败，请稍后重试')
    dietRecordList.value = []
    dietRecordTotal.value = 0
  } finally {
    console.log('====== 结束获取饮食记录 ======')
    dietRecordLoading.value = false
  }
}

// 健康目标表单控制
const showHealthGoalForm = ref(false)
const healthGoalListRef = ref(null)

// 添加处理健康目标创建成功的方法
const handleHealthGoalSuccess = async () => {
  showHealthGoalForm.value = false
  try {
    // 调用健康目标列表组件的刷新方法
    await healthGoalListRef.value?.fetchGoalList()
    ElMessage({
      type: 'success',
      message: '🎉 健康目标设置成功！开启您的健康之旅吧！',
      duration: 3000
    })
  } catch (error) {
    console.error('刷新健康目标列表失败:', error)
    ElMessage.error('刷新数据失败，请手动刷新页面')
  }
}

// 处理目标列表更新
const handleGoalListUpdate = async () => {
  try {
    // 调用健康目标列表组件的刷新方法
    await healthGoalListRef.value?.fetchGoalList()
  } catch (error) {
    console.error('刷新健康目标列表失败:', error)
    ElMessage.error('刷新数据失败，请手动刷新页面')
  }
}

// 添加欢迎引导相关的状态
const showWelcomeGuide = ref(false)

// 处理欢迎引导关闭
const handleWelcomeGuideClose = () => {
  showWelcomeGuide.value = false
  localStorage.setItem('welcomeGuideShown', 'true')
}

// 在组件挂载时检查登录状态
onMounted(async () => {
  if (!checkAuth()) return
  
  // 检查是否需要显示欢迎引导
  const welcomeGuideShown = localStorage.getItem('welcomeGuideShown')
  if (!welcomeGuideShown) {
    showWelcomeGuide.value = true
  }
  
  try {
    await Promise.all([
      fetchHealthProfile(),
      fetchHealthDataList(),
      fetchDietRecordList(),
      fetchExerciseRecords()
    ])
  } catch (error) {
    console.error('初始化数据失败:', error)
    ElMessage.error('加载数据失败，请刷新页面重试')
  }
})

// 获取健康数据列表
const fetchHealthDataList = async () => {
  if (!userStore.userId) return
  
  try {
    healthDataLoading.value = true
    const response = await getHealthDataList({
      userId: userStore.userId,
      current: healthDataPagination.value.currentPage,
      size: healthDataPagination.value.pageSize,
      startDateTime: dateRange.value?.[0] ? new Date(dateRange.value[0]).toISOString().replace('Z', '+08:00') : null,
      endDateTime: dateRange.value?.[1] ? new Date(dateRange.value[1]).toISOString().replace('Z', '+08:00') : null,
      isAsc: false // 默认按时间降序，最新数据在前
    })

    if (response.code === 200) {
      healthDataList.value = response.data.dataList.map(item => ({
        ...item,
        bmi: item.weight && item.height ? +(item.weight / Math.pow(item.height / 100, 2)).toFixed(1) : null
      }))
      healthDataPagination.value.total = response.data.total
    } else {
      ElMessage({
        type: 'info',
        message: '💡 暂无健康数据记录\n✨ 建议您开始记录健康数据，以便更好地管理身体健康',
        duration: 5000,
        showClose: true
      })
    }
  } catch (error) {
    console.error('获取健康数据失败:', error)
    ElMessage({
      type: 'info',
      message: '💡 获取健康数据遇到问题\n✨ 建议您刷新页面或稍后重试',
      duration: 5000,
      showClose: true
    })
  } finally {
    healthDataLoading.value = false
  }
}

// 处理健康数据分页变化
const handleHealthDataPageChange = (page) => {
  healthDataPagination.value.currentPage = page
  fetchHealthDataList()
}

// 处理健康数据每页条数变化
const handleHealthDataSizeChange = (size) => {
  healthDataPagination.value.pageSize = size
  healthDataPagination.value.currentPage = 1
  fetchHealthDataList()
}

// 处理运动记录日期范围变化
const handleExerciseDateRangeChange = async (range) => {
  console.log('日期范围变化:', range)
  
  if (!range) {
    // 重置日期范围
    dateRange.value = null
  } else {
    // 更新日期范围，确保使用正确的时区格式
    const [start, end] = range
    dateRange.value = [
      formatDateWithChineseTimezone(start),
      formatDateWithChineseTimezone(end)
    ]
  }
  
  // 重置页码并立即获取数据
  exercisePagination.value.current = 1
  await fetchExerciseRecords()
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'change-password':
      router.push('/user/change-password')
      break
    case 'settings':
      ElMessage({
        type: 'info',
        message: '系统设置功能正在开发中，敬请期待！',
        duration: 3000
      })
      break
    case 'logout':
      ElMessageBox.confirm(
        '确定要退出登录吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        userStore.clearUserInfo()
        router.push('/login')
      }).catch(() => {})
      break
  }
}

// 处理日期范围变化
const handleDateRangeChange = (range) => {
  if (!range) {
    dateRange.value = null
    return
  }

  try {
    // 设置开始时间为当天的开始（00:00:00）
    const startDate = new Date(range[0])
    startDate.setHours(0, 0, 0, 0)

    // 设置结束时间为当天的结束（23:59:59.999）
    const endDate = new Date(range[1])
    endDate.setHours(23, 59, 59, 999)

    // 使用formatDateWithChineseTimezone格式化日期
    const formattedStartDate = formatDateWithChineseTimezone(startDate)
    const formattedEndDate = formatDateWithChineseTimezone(endDate)

    // 更新查询参数
    const params = {
      userId: userStore.userId,
      current: 1,
      size: 15,
      isAsc: false,
      startDateTime: formattedStartDate,
      endDateTime: formattedEndDate,
      mealType: ''
    }

    // 更新日期范围
    dateRange.value = [formattedStartDate, formattedEndDate]

    // 刷新数据
    fetchDietRecordList(params)
  } catch (error) {
    console.error('处理日期范围变化时出错:', error)
    ElMessage.error('日期格式处理失败，请重试')
  }
}

// 添加新函数：处理添加饮食记录
const handleAddDietRecord = () => {
  // 清除已选中的记录和编辑状态
  editingDietRecord.value = null
  selectedDietRecords.value = []
  showDietRecordForm.value = true
}
</script>

<template>
  <div class="dashboard-container">

    <!-- 添加装饰元素 -->
    <div class="corner-decoration"></div>

    <!-- 添加欢迎引导组件 -->
    <welcome-guide
      v-if="showWelcomeGuide"
      @close="handleWelcomeGuideClose"
    />
    
    <!-- 顶部导航栏 -->
    <el-header class="dashboard-header" height="64">
      <div class="header-left">
        <div class="logo-container">
          <img src="@/assets/logo.png" alt="Logo" class="logo-image">
          <h2>日常运动健康管理系统</h2>
        </div>
      </div>
      <div class="header-right">
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-info">
            <el-avatar :size="32" class="user-avatar">
              {{ healthData?.fullName?.[0] || 'わたし' }}
            </el-avatar>
            <span class="user-name">{{ healthData?.fullName || '我的账号' }}</span>
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu class="custom-dropdown">
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                个人资料
              </el-dropdown-item>
              <el-dropdown-item command="change-password">
                <el-icon><Lock /></el-icon>
                修改密码
              </el-dropdown-item>
              <el-dropdown-item command="settings">
                <el-icon><Setting /></el-icon>
                系统设置
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 主要内容区 -->
    <el-main class="dashboard-main">
      <!-- 顶部三个卡片的容器 -->
      <div class="top-cards-container">
        <!-- 健康档案卡片 -->
        <div class="grid-item profile-section">
          <el-card v-loading="loading" class="profile-card dashboard-card">
            <template #header>
              <div class="card-header">
                <div class="header-title">
                  <el-icon><Document /></el-icon>
                  <h2>我的健康档案</h2>
                </div>
                <div class="header-actions">
                  <el-button
                    v-if="healthData"
                    type="danger"
                    size="small"
                    :icon="Delete"
                    class="action-btn delete-btn"
                    @click="handleDeleteProfile"
                  >
                    删除档案
                  </el-button>
                  <el-button
                    type="primary"
                    size="small"
                    class="action-btn"
                    @click="handleEditProfile"
                  >
                    {{ healthData ? '编辑档案' : '创建档案' }}
                  </el-button>
                </div>
              </div>
            </template>

            <div class="health-profile compact-view" v-loading="loading">
              <el-empty
                v-if="!healthData"
                description="您还没有创建健康档案"
              >
                <el-button type="primary" @click="handleEditProfile">
                  立即创建
                </el-button>
              </el-empty>

              <el-descriptions
                v-else
                :column="1"
                border
                class="profile-descriptions"
              >
                <el-descriptions-item label="疾病史">
                  <div class="description-content">
                    {{ healthData.medicalHistory || '无' }}
                  </div>
                </el-descriptions-item>
                <el-descriptions-item label="过敏史">
                  <div class="description-content">
                    {{ healthData.allergyHistory || '无' }}
                  </div>
                </el-descriptions-item>
                <el-descriptions-item label="运动习惯">
                  <div class="description-content">
                    {{ healthData.exerciseHabits || '暂无记录' }}
                  </div>
                </el-descriptions-item>
                <el-descriptions-item label="健康目标">
                  <div class="description-content">
                    {{ healthData.healthGoals || '暂未设置' }}
                  </div>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-card>
        </div>

        <!-- 健康数据趋势图 -->
        <div class="grid-item chart-section">
          <el-card class="dashboard-card trend-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <div class="header-title">
                  <el-icon><TrendCharts /></el-icon>
                  <h2>健康数据趋势</h2>
                </div>
              </div>
            </template>
            
            <health-data-trend
              :user-id="userStore.userId"
              class="health-chart"
            />
          </el-card>
        </div>

        <!-- 营养分析卡片 -->
        <div class="grid-item nutrition-section">
          <el-card class="dashboard-card nutrition-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <div class="header-title">
                  <el-icon><Histogram /></el-icon>
                  <h2>营养分析</h2>
                </div>
                <div class="header-actions">
                  <el-tooltip content="查看详细营养报告" placement="top">
                    <el-button
                      type="primary"
                      :icon="DataLine"
                      size="small"
                      link
                    >
                      详细报告
                    </el-button>
                  </el-tooltip>
                </div>
              </div>
            </template>
            
            <diet-nutrition-summary
              :user-id="userStore.userId"
              :date-range="dateRange"
              class="nutrition-summary optimized"
            />
          </el-card>
        </div>
      </div>

      <div class="dashboard-grid">
        <!-- 健康数据记录卡片 -->
        <div class="grid-item health-data-section">
          <el-card class="dashboard-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <div class="header-title">
                  <el-icon><DataLine /></el-icon>
                  <h2>健康数据记录</h2>
                </div>
              </div>
            </template>
            
            <health-data-card-view
              :data="healthDataList"
              :loading="healthDataLoading"
              :pagination="healthDataPagination"
              :user-id="userId"
              @page-change="handleHealthDataPageChange"
              @size-change="handleHealthDataSizeChange"
              @refresh="fetchHealthDataList"
            />
          </el-card>
        </div>

        <!-- 健康目标计划卡片 -->
        <div class="grid-item health-goal-section">
          <el-card class="dashboard-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <div class="header-title">
                  <el-icon><Aim /></el-icon>
                  <h2>健康目标计划</h2>
                </div>
                <div class="header-actions">
                  <el-button
                    type="primary"
                    :icon="Plus"
                    @click="showHealthGoalForm = true"
                  >
                    设置目标
                  </el-button>
                </div>
              </div>
            </template>
            
            <div v-if="userId" class="health-goal-content">
              <health-goal-list 
                :user-id="userId" 
                ref="healthGoalListRef"
                @update-list="handleGoalListUpdate"
              />
            </div>
            <div v-else class="health-goal-placeholder">
              <el-empty
                description="暂无健康目标计划"
                :image-size="120"
              >
                <template #image>
                  <el-icon class="empty-icon"><Flag /></el-icon>
                </template>
                <template #description>
                  <p class="empty-text">设置您的健康目标，开启健康生活新篇章</p>
                </template>
                <el-button
                  type="primary"
                  @click="showHealthGoalForm = true"
                >
                  立即设置
                </el-button>
              </el-empty>
            </div>
          </el-card>
        </div>

        <!-- 运动记录卡片 -->
        <div class="grid-item exercise-section">
          <exercise-record-card
            :records="exerciseRecords"
            :stats="statsData"
            :total="exerciseTotal"
            :loading="exerciseLoading"
            @add="showExerciseForm = true"
            @edit="handleEditExercise"
            @delete="handleDeleteExercise"
            @selection-change="handleExerciseSelectionChange"
            @page-change="handleExercisePageChange"
            @size-change="handleExerciseSizeChange"
            @batch-edit="handleBatchEditExercise"
            @batch-delete="handleBatchDeleteExercise"
            @date-range-change="handleExerciseRecordDateRangeChange"
          />
        </div>

        <!-- 饮食记录卡片 - 现在占据更大空间 -->
        <div class="grid-item diet-section">
          <el-card class="dashboard-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <div class="header-title">
                  <el-icon><Bowl /></el-icon>
                  <h2>饮食记录</h2>
                </div>
              </div>
            </template>
            
            <template v-if="userId">
              <diet-record-list
                ref="dietRecordListRef"
                :loading="dietRecordLoading"
                :records="dietRecordList"
                :total="dietRecordTotal"
                v-model:current-page="dietRecordPagination.current"
                v-model:page-size="dietRecordPagination.size"
                :page-sizes="[15, 25, 35, 50]"
                @refresh="fetchDietRecordList"
                @add="handleAddDietRecord"
                @edit="handleEditDietRecord"
                @batch-edit="handleBatchEditDietRecord"
                @selection-change="handleDietRecordSelectionChange"
                @size-change="handleDietRecordSizeChange"
                @page-change="handleDietRecordPageChange"
              />
            </template>
            <template v-else>
              <el-empty
                description="开始记录您的饮食习惯"
              >
                <el-button type="primary" @click="handleAddDietRecord">
                  添加记录
                </el-button>
              </el-empty>
            </template>
          </el-card>
        </div>
      </div>
    </el-main>

    <!-- 弹窗组件 -->
    <exercise-record-form
      v-model="showExerciseForm"
      :mode="exerciseFormMode"
      :initial-data="editingExerciseRecord"
      :selected-records="selectedExerciseRecords"
      @success="handleExerciseFormSuccess"
    />

    <!-- 饮食记录表单弹窗 -->
    <el-dialog
      v-model="showDietRecordForm"
      :title="Array.isArray(editingDietRecord) ? '批量修改饮食记录' : (editingDietRecord ? '修改饮食记录' : '添加饮食记录')"
      width="800px"
      class="custom-dialog diet-record-dialog"
      destroy-on-close
      :modal-append-to-body="true"
      :append-to-body="true"
      :close-on-click-modal="false"
      align-center
    >
      <diet-record-form
        v-if="showDietRecordForm"
        :user-id="userStore.userId"
        :edit-mode="!!editingDietRecord"
        :initial-data="editingDietRecord"
        @success="handleDietRecordSuccess"
        @cancel="showDietRecordForm = false"
      />
    </el-dialog>

    <!-- 健康目标表单弹窗 -->
    <el-dialog
      v-model="showHealthGoalForm"
      title="设置健康目标"
      width="800px"
      destroy-on-close
      :modal-append-to-body="true"
      :append-to-body="true"
      :close-on-click-modal="false"
      class="custom-dialog goal-form-dialog"
      align-center
    >
      <health-goal-batch-form
        :user-id="userId"
        @success="handleHealthGoalSuccess"
        @cancel="showHealthGoalForm = false"
      />
    </el-dialog>
  </div>
</template>

<style scoped>
.dashboard-container {
  min-height: 100vh;
  background-color: var(--el-bg-color-page);
  transition: background-color 0.3s ease;
}

/* 顶部导航栏样式 */
.dashboard-header {
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.95), rgba(15, 15, 35, 0.95));
  backdrop-filter: blur(10px);
  box-shadow: 
    0 4px 20px rgba(0, 0, 0, 0.2),
    0 0 30px rgba(255, 105, 180, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  transition: all 0.3s ease;
  border-bottom: 2px solid transparent;
  border-image: linear-gradient(
    90deg,
    var(--anime-primary),
    var(--street-primary),
    var(--anime-secondary)
  ) 1;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
}

.logo-container::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--anime-primary), transparent);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.logo-container:hover::after {
  transform: scaleX(1);
}

.logo-image {
  width: 36px;
  height: 36px;
  object-fit: contain;
  filter: drop-shadow(0 0 8px var(--anime-primary));
  transition: transform 0.3s ease;
}

.logo-container:hover .logo-image {
  transform: scale(1.1) rotate(5deg);
}

.header-left h2 {
  margin: 0;
  font-family: "华文行楷", "楷体", cursive;
  font-size: 1.8rem;
  font-weight: 600;
  background: linear-gradient(
    45deg,
    var(--anime-primary),
    var(--street-primary),
    var(--anime-secondary)
  );
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 10px rgba(255, 105, 180, 0.2);
  letter-spacing: 2px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 20px;
  background: linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.05),
    rgba(255, 255, 255, 0.1)
  );
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.user-info:hover {
  background: linear-gradient(
    45deg,
    rgba(255, 105, 180, 0.1),
    rgba(135, 206, 235, 0.1)
  );
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-avatar {
  background: linear-gradient(45deg, var(--anime-primary), var(--street-primary));
  color: white;
  font-weight: 600;
  border: 2px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.user-info:hover .user-avatar {
  transform: rotate(5deg);
  box-shadow: 0 0 15px var(--anime-primary);
}

.user-name {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}

.user-info:hover .user-name {
  color: white;
  text-shadow: 0 0 10px var(--anime-primary);
}

/* 自定义下拉菜单样式 */
.custom-dropdown {
  background: rgba(26, 26, 46, 0.95) !important;
  backdrop-filter: blur(10px) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 12px !important;
  overflow: hidden !important;
}

.custom-dropdown .el-dropdown-menu__item {
  color: rgba(255, 255, 255, 0.8) !important;
  transition: all 0.3s ease !important;
}

.custom-dropdown .el-dropdown-menu__item:hover {
  background: linear-gradient(
    45deg,
    rgba(255, 105, 180, 0.1),
    rgba(135, 206, 235, 0.1)
  ) !important;
  color: white !important;
}

.custom-dropdown .el-dropdown-menu__item .el-icon {
  color: var(--anime-primary) !important;
  margin-right: 8px !important;
}

/* 主要内容区网格布局 */
.dashboard-main {
  padding: 88px 24px 24px;
  max-width: 1440px;
  margin: 0 auto;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 24px;
  margin: 0 auto;
}

/* 调整网格布局 */
.profile-section {
  grid-column: span 4;
  min-height: 500px; /* 增加最小高度 */
}

.chart-section {
  grid-column: span 8;
  min-height: 500px; /* 增加最小高度 */
}

.nutrition-section {
  grid-column: span 12;
}

.health-data-section {
  grid-column: span 12;
}

.exercise-section {
  grid-column: span 12;
}

.diet-section {
  grid-column: span 12;
}

/* 响应式布局 */
@media screen and (max-width: 1400px) {
  .profile-section,
  .chart-section,
  .nutrition-section {
    grid-column: span 4;
  }

  .health-data-section,
  .exercise-section,
  .diet-section {
    grid-column: span 12;
  }
}

@media screen and (max-width: 1200px) {
  .profile-section,
  .chart-section,
  .nutrition-section,
  .health-data-section,
  .exercise-section,
  .diet-section {
    grid-column: span 12;
  }
}

@media screen and (max-width: 768px) {
  .dashboard-main {
    padding: 88px 16px 16px;
  }

  .quick-stats .el-col {
    width: 100%;
    margin-bottom: 16px;
  }

  .header-left h2 {
    font-size: 1.2rem;
  }

  .date-range-picker {
    width: 100%;
  }

  .header-actions {
    flex-direction: column;
    align-items: stretch;
  }
}

/* 卡片样式优化 */
.dashboard-card {
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  border: none;
  height: 100%;
  background: var(--el-bg-color);
}

.dashboard-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-title .el-icon {
  font-size: 1.2rem;
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
  padding: 8px;
  border-radius: 8px;
}

.header-title h3 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

/* 描述列表样式优化 */
.profile-descriptions {
  margin-top: 16px;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
  color: var(--el-text-color-regular);
  background-color: var(--el-fill-color-lighter);
  padding: 12px 16px;
}

:deep(.el-descriptions__content) {
  padding: 12px 16px;
}

.description-content {
  color: var(--el-text-color-primary);
  line-height: 1.6;
  white-space: pre-wrap;
}

/* 图表区域样式 */
.health-chart {
  height: 400px;
  margin-top: 16px;
}

/* 表格区域样式 */
.data-table,
.diet-table {
  margin-top: 16px;
}

/* 自定义弹窗样式 */
:deep(.custom-dialog .el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.custom-dialog .el-dialog__header) {
  margin: 0;
  padding: 20px 24px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  background: var(--el-bg-color);
}

:deep(.custom-dialog .el-dialog__body) {
  padding: 24px;
  background: var(--el-bg-color-page);
}

/* 按钮样式优化 */
.action-btn {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.delete-btn {
  background: var(--el-color-danger-light-9);
  border-color: var(--el-color-danger-light-7);
  color: var(--el-color-danger);
}

.delete-btn:hover {
  background: var(--el-color-danger-light-8);
  border-color: var(--el-color-danger-light-6);
}

/* 下拉菜单样式 */
:deep(.custom-dropdown .el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
}

:deep(.custom-dropdown .el-dropdown-menu__item .el-icon) {
  font-size: 1.1em;
}

/* 添加快速统计卡片样式 */
.quick-stats {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 16px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 4px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.stat-card:hover .stat-icon {
  transform: scale(1.1);
}

.stat-icon .el-icon {
  font-size: 28px;
  transition: all 0.3s ease;
}

.stat-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-title {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-bottom: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--el-text-color-primary);
  line-height: 1.2;
  margin-bottom: 4px;
}

.stat-unit {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-left: 4px;
  font-weight: normal;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--el-color-danger);
}

.stat-trend.trend-up {
  color: var(--el-color-success);
}

/* 优化表格容器 */
.table-container {
  max-height: 600px;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: var(--el-color-primary-light-5) transparent;
}

.table-container::-webkit-scrollbar {
  width: 6px;
}

.table-container::-webkit-scrollbar-track {
  background: transparent;
}

.table-container::-webkit-scrollbar-thumb {
  background-color: var(--el-color-primary-light-5);
  border-radius: 3px;
}

/* 渐变按钮 */
.gradient-button {
  background: linear-gradient(45deg, var(--el-color-primary), var(--el-color-primary-light-3));
  border: none;
  padding: 12px 24px;
  transition: all 0.3s ease;
}

.gradient-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 卡片动画效果 */
.dashboard-card {
  animation: fadeInUp 0.6s ease-out;
  animation-fill-mode: both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 优化响应式布局 */
@media screen and (max-width: 1400px) {
  .quick-stats .el-col {
    width: 50%;
    margin-bottom: 24px;
  }
}

@media screen and (max-width: 768px) {
  .quick-stats .el-col {
    width: 100%;
  }

  .stat-card {
    margin-bottom: 16px;
  }

  .stat-content {
    padding: 16px;
  }

  .stat-icon {
    width: 48px;
    height: 48px;
  }

  .stat-value {
    font-size: 24px;
  }
}

/* 暗色主题适配 */
:root[data-theme="dark"] .stat-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.05), rgba(255, 255, 255, 0.02));
  border-color: rgba(255, 255, 255, 0.05);
}

/* 添加更多动画效果 */
.dashboard-card,
.stat-card,
.gradient-button {
  will-change: transform;
}

/* 优化表格样式 */
:deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

:deep(.el-table th) {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  font-weight: 600;
}

:deep(.el-table tr:hover) td {
  background-color: var(--el-color-primary-light-9) !important;
}

/* 优化分页器样式 */
:deep(.el-pagination) {
  margin-top: 20px;
  padding: 16px;
  background: var(--el-bg-color);
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 优化图表样式 */
.health-chart {
  height: 400px;
  margin-top: 16px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 优化加载状态显示 */
.stat-card.is-loading {
  position: relative;
}

.stat-card.is-loading::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  z-index: 1;
  border-radius: 16px;
}

/* 优化营养分析卡片内部布局 */
:deep(.nutrition-summary) {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 12px;
  height: 100%;
}

:deep(.nutrition-summary .nutrition-info) {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

:deep(.nutrition-summary .nutrition-item) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--el-fill-color-light);
  padding: 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.nutrition-summary .nutrition-item:hover) {
  transform: translateX(4px);
  background: var(--el-color-primary-light-9);
}

:deep(.nutrition-summary .nutrition-item-label) {
  font-size: 14px;
  color: var(--el-text-color-regular);
}

:deep(.nutrition-summary .nutrition-item-value) {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

:deep(.nutrition-summary .nutrition-charts) {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

:deep(.nutrition-summary .chart-container) {
  background: var(--el-fill-color-blank);
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 优化饮食记录表格 */
.diet-section .table-container {
  height: calc(100vh - 300px);
  min-height: 500px;
  overflow: hidden;
  border-radius: 12px;
  background: var(--el-bg-color);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

:deep(.diet-table) {
  height: 100%;
}

:deep(.diet-table .el-table__inner-wrapper) {
  height: 100%;
}

:deep(.diet-table .el-table__body-wrapper) {
  height: calc(100% - 40px);
  overflow-y: auto;
}

/* 优化表格滚动条 */
:deep(.el-table__body-wrapper::-webkit-scrollbar) {
  width: 6px;
  height: 6px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background: var(--el-color-primary-light-5);
  border-radius: 3px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-track) {
  background: transparent;
}

/* 优化表格内容显示 */
:deep(.diet-table .el-table__row) {
  transition: all 0.3s ease;
}

:deep(.diet-table .el-table__row:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

:deep(.diet-table .el-table__cell) {
  padding: 12px;
  transition: all 0.3s ease;
}

/* 确保弹出层不被遮挡 */
:deep(.el-table__fixed) {
  z-index: 10;
}

:deep(.el-table__fixed-right) {
  z-index: 10;
}

:deep(.el-popper) {
  z-index: 3000;
}

/* 添加卡片内容过渡动画 */
.card-content {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 优化空状态显示 */
:deep(.el-empty) {
  padding: 40px 0;
}

:deep(.el-empty__description) {
  margin-top: 16px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

/* 添加加载状态动画 */
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

/* 优化表格操作按钮 */
:deep(.diet-table .operation-column) {
  .el-button {
    padding: 4px 8px;
    font-size: 12px;
  }
  
  .el-button + .el-button {
    margin-left: 8px;
  }
}

/* 优化表格分页器 */
:deep(.el-pagination) {
  padding: 16px;
  background: var(--el-bg-color);
  border-radius: 0 0 12px 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 16px;
}

/* 添加工具提示 */
:deep(.el-tooltip__trigger) {
  cursor: help;
}

/* 优化表格加载状态 */
:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(4px);
}

/* 优化表格展开行 */
:deep(.el-table__expand-icon) {
  transition: transform 0.3s ease-in-out;
}

:deep(.el-table__expand-icon--expanded) {
  transform: rotate(90deg);
}

/* 添加表格行激活状态 */
:deep(.el-table__row.active-row) {
  background-color: var(--el-color-primary-light-9);
}

/* 优化表格列宽调整 */
:deep(.el-table__column-resize-proxy) {
  background-color: var(--el-color-primary);
}

/* 优化表格筛选和排序 */
:deep(.el-table__column-filter-trigger) {
  opacity: 0;
  transition: opacity 0.3s ease;
}

:deep(.el-table__cell:hover .el-table__column-filter-trigger) {
  opacity: 1;
}

/* 添加表格行展开/收起动画 */
:deep(.el-table__expanded-cell) {
  animation: expandRow 0.3s ease-out;
}

@keyframes expandRow {
  from {
    opacity: 0;
    transform: scaleY(0);
  }
  to {
    opacity: 1;
    transform: scaleY(1);
  }
}

/* 优化表格选择列 */
:deep(.el-table__column-selection) {
  .el-checkbox__inner {
    border-radius: 4px;
  }
}

/* 优化表格固定列阴影 */
:deep(.el-table__fixed-right-patch) {
  background-color: var(--el-bg-color);
}

:deep(.el-table--enable-row-hover) {
  .el-table__body tr:hover > td.el-table__cell {
    background-color: var(--el-color-primary-light-9);
  }
}

/* 调整网格布局 */
.health-goal-section {
  grid-column: span 12;
}

.health-goal-content {
  min-height: 300px;
  padding: 16px;
}

.health-goal-placeholder {
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
}

.empty-icon {
  font-size: 60px;
  color: var(--el-color-primary);
  opacity: 0.8;
}

.empty-text {
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin: 8px 0;
  text-align: center;
}

/* 响应式布局 */
@media screen and (max-width: 1400px) {
  .health-goal-section {
    grid-column: span 12;
  }
}

@media screen and (max-width: 768px) {
  .health-goal-section {
    grid-column: span 12;
  }
  
  .health-goal-placeholder {
    padding: 20px;
  }
}

/* 左下角装饰元素 */
.corner-decoration {
  content: '';
  position: fixed;
  bottom: 35px; /* 稍微抬高位置 */
  left: 35px;
  width: 180px;
  height: 180px;
  background: url('/images/anime-decoration-2.png') no-repeat;
  background-size: contain;
  pointer-events: none;
  z-index: 2;
  transform-origin: bottom center;
  filter: drop-shadow(0 0 15px var(--anime-glow));
  opacity: 0.85;
  /* 组合多个动画效果 */
  animation: 
    bounce 2s ease-in-out infinite,
    wiggle 3s ease-in-out infinite,
    morphShape 60s linear infinite;
}

/* 添加中国风装饰元素 */
.corner-decoration::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 120px;
  height: 120px;
  background: url('/images/chinese-decoration.png') no-repeat;
  background-size: contain;
  transform: translate(-50%, -50%);
  opacity: 0.6;
  mix-blend-mode: screen;
  animation: rotateAnimation 15s linear infinite;
}

/* 添加光效装饰 */
.corner-decoration::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(
    circle at center,
    var(--anime-glow) 0%,
    transparent 70%
  );
  mix-blend-mode: screen;
  opacity: 0.5;
  animation: pulseAnimation 4s ease-in-out infinite;
}

/* 弹跳动画 */
@keyframes bounce {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-20px) scale(1.05);
  }
}

/* 摇摆动画 */
@keyframes wiggle {
  0%, 100% {
    transform: rotate(-45deg);
  }
  50% {
    transform: rotate(45deg);
  }
}

/* 形状变化动画 */
@keyframes morphShape {
  0%, 100% {
    border-radius: 50%;
  }
  20% {
    border-radius: 0;
    transform: rotate(45deg);
  }
  40% {
    border-radius: 30% 70% 70% 30% / 30% 30% 70% 70%;
  }
  60% {
    border-radius: 30% 70% 70% 30% / 70% 30% 70% 30%;
  }
  80% {
    border-radius: 60%;
  }
}

/* 旋转动画 */
@keyframes rotateAnimation {
  from {
    transform: translate(-50%, -50%) rotate(0deg);
  }
  to {
    transform: translate(-50%, -50%) rotate(360deg);
  }
}

/* 脉冲动画 */
@keyframes pulseAnimation {
  0%, 100% {
    opacity: 0.3;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(1.1);
  }
}

/* 漂浮动画 */
@keyframes floatAnimation {
  0%, 100% {
    transform: translateY(0) rotate(-5deg);
  }
  50% {
    transform: translateY(-15px) rotate(5deg);
  }
}

/* 弹窗样式优化 */
:deep(.custom-dialog) {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 !important;
}

:deep(.custom-dialog .el-dialog) {
  margin: 0 auto !important;
  position: relative;
  max-width: 90%;
  margin-top: 15vh !important;
}

:deep(.goal-form-dialog .el-dialog__body) {
  padding: 20px 24px;
  max-height: 70vh;
  overflow-y: auto;
}

:deep(.el-dialog__header) {
  padding: 20px 24px;
  margin-right: 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

:deep(.el-dialog__headerbtn) {
  top: 20px;
  right: 20px;
}

:deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
}

/* 确保弹窗内容可滚动 */
:deep(.el-dialog__body) {
  overflow-y: auto;
  max-height: calc(90vh - 150px);
}

/* 添加滚动条样式 */
:deep(.el-dialog__body::-webkit-scrollbar) {
  width: 6px;
}

:deep(.el-dialog__body::-webkit-scrollbar-thumb) {
  background: var(--el-color-primary-light-5);
  border-radius: 3px;
}

:deep(.el-dialog__body::-webkit-scrollbar-track) {
  background: transparent;
}

/* 优化弹窗样式 */
:deep(.diet-record-dialog) {
  .el-dialog {
    margin: 0 auto !important;
    position: relative;
    max-width: 90%;
    margin-top: 15vh !important;
    border-radius: 16px;
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
    background: var(--el-bg-color);
  }

  .el-dialog__header {
    padding: 20px 24px;
    margin-right: 0;
    border-bottom: 1px solid var(--el-border-color-lighter);
    background: var(--el-bg-color);
  }

  .el-dialog__title {
    font-size: 18px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }

  .el-dialog__headerbtn {
    top: 20px;
    right: 20px;
  }

  .el-dialog__body {
    padding: 24px;
    max-height: 70vh;
    overflow-y: auto;
    background: var(--el-bg-color-page);
  }

  .el-dialog__body::-webkit-scrollbar {
    width: 6px;
  }

  .el-dialog__body::-webkit-scrollbar-thumb {
    background: var(--el-color-primary-light-5);
    border-radius: 3px;
  }

  .el-dialog__body::-webkit-scrollbar-track {
    background: transparent;
  }
}

/* 弹窗动画效果 */
:deep(.el-dialog) {
  transform: translateY(-20px);
  transition: transform 0.3s ease-out;
}

:deep(.el-dialog.el-dialog--center) {
  transform: translateY(0);
}

:deep(.el-overlay) {
  backdrop-filter: blur(5px);
  transition: backdrop-filter 0.3s ease;
}

:deep(.el-overlay-dialog) {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  margin: 0 !important;
}

/* 添加新的样式 */
.top-cards-container {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

/* 优化卡片布局 */
.profile-section {
  grid-column: span 4;
  min-height:500px; /* 增加最小高度 */
}

.chart-section {
  grid-column: span 8;
  min-height: 500px; /* 增加最小高度 */
}

/* 健康档案卡片样式优化 */
.profile-card {
  display: flex;
  flex-direction: column;
  height: 620px; /* 增加固定高度 */
}

.health-profile.compact-view {
  height: calc(100% - 60px); /* 减去header高度 */
  overflow-y: auto;
}

.profile-descriptions {
  margin-top: 0;
}

:deep(.profile-descriptions .el-descriptions__label) {
  width: 70px;
  padding: 6px 10px; /* 减小内边距 */
  font-size: 0.9em; /* 稍微减小字体 */
}

:deep(.profile-descriptions .el-descriptions__content) {
  padding: 6px 10px; /* 减小内边距 */
}

.description-content {
  font-size: 0.9em;
  line-height: 1.4;
  max-height: 48px; /* 减小内容区域高度 */
}

/* 卡片头部样式优化 */
.card-header {
  padding: 12px 16px;
  margin: -12px -16px 8px; /* 减小下边距 */
}

.header-title h3 {
  font-size: 15px; /* 稍微减小标题字体 */
}

/* 响应式布局优化 */
@media screen and (max-width: 1200px) {
  .profile-section,
  .chart-section {
    min-height: 420px; /* 保持一致的高度 */
  }
  
  .health-chart.optimized {
    min-height: 340px;
  }
}

@media screen and (max-width: 768px) {
  .profile-section,
  .chart-section {
    min-height: 400px; /* 在移动端稍微减小高度 */
  }
  
  .health-chart.optimized {
    min-height: 320px;
  }
}

.nutrition-section {
  grid-column: span 12;
  min-height: 420px;
}

/* 健康数据趋势图样式优化 */
.trend-card {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 620px; /* 增加固定高度 */
}

.health-chart {
  flex: 1;
  height: 500px; /* 增加图表高度 */
  min-height: 500px;
  margin: 0;
  padding: 0; /* 移除内边距 */
  overflow: hidden; /* 防止内容溢出 */
}

/* 确保图表容器内的文本不会溢出 */
:deep(.health-chart .el-card__body) {
  overflow: hidden;
  padding: 0; /* 移除内边距 */
}

:deep(.health-chart .echarts) {
  width: 100% !important;
  height: 100% !important;
  margin: 0 auto;
  padding: 0; /* 完全移除左右内边距 */
}

:deep(.health-chart .echarts-container) {
  margin: 0;
  padding: 0;
  width: 100% !important;
  height: 100% !important;
}

:deep(.health-chart .echarts-tooltip) {
  max-width: 90%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 优化图表标签显示 */
:deep(.health-chart.optimized .el-card__body > div) {
  margin: 0;
  padding: 0;
  width: 100% !important;
  height: 100% !important;
}

:deep(.health-chart.optimized canvas) {
  margin: 0 !important;
  padding: 0 !important;
  width: 100% !important;
  height: 100% !important;
}

/* 营养分析卡片样式优化 */
.nutrition-card {
  display: flex;
  flex-direction: column;
}

.nutrition-summary.optimized {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

:deep(.nutrition-summary .nutrition-info) {
  margin-bottom: 16px;
}

:deep(.nutrition-summary .nutrition-item) {
  padding: 8px 12px;
  margin-bottom: 8px;
  border-radius: 6px;
  background: var(--el-fill-color-light);
  transition: all 0.3s ease;
}

:deep(.nutrition-summary .nutrition-item:hover) {
  transform: translateX(4px);
  background: var(--el-color-primary-light-9);
}

/* 优化卡片头部样式 */
.card-header {
  padding: 12px 16px;
  margin: -12px -16px 12px;
  border-bottom: 1px solid var(--el-border-color-light);
  background: var(--el-bg-color);
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-title h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.header-actions {
  margin-left: auto;
  display: flex;
  gap: 8px;
}

.header-actions.compact {
  .el-date-editor {
    width: 240px;
  }
}

/* 响应式布局优化 */
@media screen and (max-width: 1400px) {
  .profile-section {
    grid-column: span 4;
  }
  
  .chart-section {
    grid-column: span 5;
  }
  
  .nutrition-section {
    grid-column: span 3;
  }
}

@media screen and (max-width: 1200px) {
  .profile-section,
  .chart-section,
  .nutrition-section {
    grid-column: span 12;
    min-height: 500px;
  }
  
  .health-chart.optimized {
    min-height: 500px;
  }
}

@media screen and (max-width: 768px) {
  .top-cards-container {
    gap: 16px;
  }
  
  .profile-section,
  .chart-section,
  .nutrition-section {
    min-height: 600px;
  }
  
  .health-chart.optimized {
    min-height: 600px;
  }
  
  .header-actions.compact {
    .el-date-editor {
      width: 100%;
    }
  }
}

/* 滚动条美化 */
.description-content::-webkit-scrollbar,
.health-profile::-webkit-scrollbar,
.nutrition-summary::-webkit-scrollbar {
  width: 4px;
}

.description-content::-webkit-scrollbar-thumb,
.health-profile::-webkit-scrollbar-thumb,
.nutrition-summary::-webkit-scrollbar-thumb {
  background-color: var(--el-border-color);
  border-radius: 2px;
}

.description-content::-webkit-scrollbar-track,
.health-profile::-webkit-scrollbar-track,
.nutrition-summary::-webkit-scrollbar-track {
  background-color: var(--el-fill-color-light);
  border-radius: 2px;
}

/* 卡片内容区域优化 */
:deep(.el-card__body) {
  padding: 16px;
  height: calc(100% - 56px); /* 减去header高度 */
  display: flex;
  flex-direction: column;
}

/* 添加阴影和过渡效果 */
.dashboard-card {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.dashboard-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

/* 优化空状态显示 */
:deep(.el-empty) {
  padding: 20px 0;
  margin: auto 0;
}

/* 优化按钮样式 */
.action-btn {
  padding: 6px 12px;
  font-size: 13px;
}

.delete-btn {
  background: var(--el-color-danger-light-9);
  border-color: var(--el-color-danger-light-7);
  color: var(--el-color-danger);
}

.delete-btn:hover {
  background: var(--el-color-danger-light-8);
  border-color: var(--el-color-danger-light-6);
}
</style>
