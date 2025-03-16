<script setup>
import {computed, inject, nextTick, onMounted, reactive, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Delete, Download, Refresh, Search, Setting, View} from '@element-plus/icons-vue'
import {deleteUsersInBulk, listUsersDetailsByPage} from '@/api/admin'
import {useFullscreen, useWindowSize} from '@vueuse/core'
import MiniLineChart from '@/components/charts/MiniLineChart.vue'
import MiniPieChart from '@/components/charts/MiniPieChart.vue'
import MiniBarChart from '@/components/charts/MiniBarChart.vue'

// 注入主题状态
const isDarkMode = inject('isDarkMode')

// 全屏控制
const { isFullscreen, toggle: toggleFullscreen } = useFullscreen()

// 窗口大小响应
const { width } = useWindowSize()
const isMobile = computed(() => width.value < 768)

// 性能优化：防抖处理
const debounce = (fn, delay) => {
  let timer = null
  return (...args) => {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => fn.apply(this, args), delay)
  }
}

// 处理搜索（使用防抖）
const debouncedSearch = debounce(() => {
  pagination.current = 1
  fetchUsers()
}, 300)

// 处理搜索输入
const handleSearchInput = () => {
  debouncedSearch()
}

// 处理搜索按钮点击
const handleSearchClick = () => {
  debouncedSearch()
}

// 优化表格配置
const tableConfig = ref({
  stripe: false,
  border: false,
  size: 'default',
  highlightCurrentRow: true
})

// 动画配置
const animationConfig = ref({
  duration: 500,
  easing: 'cubic-bezier(0.4, 0, 0.2, 1)',
  delay: {
    header: 0,
    content: 300,
    table: 600
  }
})

// 表格加载优化
const loadingConfig = ref({
  text: '加载中...',
  spinner: 'el-icon-loading',
  background: 'rgba(255, 255, 255, 0.8)'
})

// 动画状态
const animationState = ref({
  headerVisible: false,
  contentVisible: false,
  tableLoaded: false
})

// 组件注册
const components = {
  MiniLineChart,
  MiniPieChart,
  MiniBarChart
}

// 表格数据
const tableData = ref([])
const loading = ref(false)
const noDataText = ref('暂无数据')
const tableRef = ref(null)

// 分页参数
const pagination = reactive({
  current: 1,
  size: 15,
  total: 0,
  pages: 0,
  hasPrevious: false,
  hasNext: false
})

// 搜索参数
const searchForm = ref({
  searchText: '',
  searchFields: ['userId', 'username', 'fullName', 'gender', 'email', 'phoneNumber', 'height', 'weight', 'createdAt', 'updatedAt']
})

// 排序参数
const sortConfig = ref({
  field: '',
  order: ''
})

// 选中的用户
const selectedUsers = ref([])

// 计算当前页数据
const processedData = computed(() => {
  let result = [...tableData.value]

  if (searchForm.value.searchText) {
    const searchText = searchForm.value.searchText.toLowerCase()
    result = result.filter(item => {
      return searchForm.value.searchFields.some(field => {
        const value = item[field]
        if (value === null || value === undefined) return false
        
        if (field === 'userId') {
          return value.toString().includes(searchText)
        }
        if (field === 'height' || field === 'weight') {
          return value.toString().includes(searchText)
        }
        if (field === 'createdAt' || field === 'updatedAt') {
          return formatDate(value).toLowerCase().includes(searchText)
        }
        return value.toString().toLowerCase().includes(searchText)
      })
    })
  }

  if (sortConfig.value.field && sortConfig.value.order) {
    result.sort((a, b) => {
      let aValue = a[sortConfig.value.field]
      let bValue = b[sortConfig.value.field]

      if (aValue === null || aValue === undefined) return 1
      if (bValue === null || bValue === undefined) return -1

      if (sortConfig.value.field === 'createdAt' || sortConfig.value.field === 'updatedAt') {
        aValue = new Date(aValue).getTime()
        bValue = new Date(bValue).getTime()
      }

      if (sortConfig.value.order === 'ascending') {
        return aValue > bValue ? 1 : -1
      } else {
        return aValue < bValue ? 1 : -1
      }
    })
  }
  
  return result
})

// 统计数据
const monthlyGrowth = ref(0)
const monthlyNewUsers = ref(0)
const monthlyTrend = ref([])
const genderRatio = ref([
  { name: '男', value: 0 },
  { name: '女', value: 0 }
])
const weeklyActivity = ref([0, 0, 0, 0, 0, 0, 0])

// 获取用户列表
const fetchUsers = async () => {
  try {
    console.log('开始获取用户数据...')
    console.log('当前分页状态:', JSON.stringify(pagination))
    loading.value = true
    
    // 确保分页参数是有效的数字
    let current = parseInt(pagination.current) || 1
    let size = parseInt(pagination.size) || 15
    
    // 限制范围
    if (current < 1) current = 1
    if (current > 1000) current = 1000
    if (size < 15) size = 15
    if (size > 50) size = 50
    
    // 更新分页参数
    pagination.current = current
    pagination.size = size
    
    // 构建请求参数
    const params = { current, size }
    
    // 只有当这些值存在时才添加到请求中
    if (filterForm.value.username) {
      params.username = filterForm.value.username
    }
    
    if (filterForm.value.dateRange && filterForm.value.dateRange.length === 2) {
      params.startDateTime = filterForm.value.dateRange[0]
      params.endDateTime = filterForm.value.dateRange[1]
    }
    
    // 只有当排序方式有明确设置时才添加
    if (filterForm.value.isAsc !== undefined) {
      params.isAsc = filterForm.value.isAsc
    }
    
    console.log('请求参数:', params)
    
    const response = await listUsersDetailsByPage(params)
    console.log('API响应:', response)
    
    if (response.code === 200) {
      console.log('数据获取成功，开始处理数据...')
      // 确保response.data存在
      const data = response.data || {}
      console.log('原始数据:', data)

      // 初始化默认值
      const defaultData = {
        dataList: [],
        total: 0,
        pages: 1,
        current: 1,
        size: 15,
        hasPrevious: false,
        hasNext: false
      }

      // 合并数据，确保所有字段都有值
      const {
        dataList = defaultData.dataList,
        total = defaultData.total,
        pages = defaultData.pages,
        current = defaultData.current,
        size = defaultData.size,
        hasPrevious = defaultData.hasPrevious,
        hasNext = defaultData.hasNext
      } = data

      // 更新表格数据
      tableData.value = Array.isArray(dataList) ? dataList : []
      console.log('处理后的tableData:', tableData.value)

      // 更新分页信息 - 使用parseInt确保是数字
      pagination.total = parseInt(total) || 0
      pagination.pages = parseInt(pages) || 1
      pagination.current = parseInt(current) || 1
      pagination.size = parseInt(size) || 15
      pagination.hasPrevious = !!hasPrevious
      pagination.hasNext = !!hasNext
      
      console.log('更新后的分页信息:', JSON.stringify(pagination))

      // 更新统计数据
      if (Array.isArray(dataList) && dataList.length > 0) {
        // 性别比例
        const maleCount = dataList.filter(user => user.gender === '男').length
        const femaleCount = dataList.filter(user => user.gender === '女').length
        genderRatio.value = [
          { name: '男', value: maleCount },
          { name: '女', value: femaleCount }
        ]
        console.log('更新性别比例:', genderRatio.value)
        
        // 计算月度增长率
        // 获取当前月份的用户数和上个月的用户数
        const now = new Date()
        const currentMonth = now.getMonth()
        const currentYear = now.getFullYear()
        
        // 计算当前月注册的用户数
        const currentMonthUsers = dataList.filter(user => {
          const createdDate = new Date(user.createdAt)
          return createdDate.getMonth() === currentMonth && 
                 createdDate.getFullYear() === currentYear
        }).length
        
        // 计算上个月注册的用户数
        const lastMonth = currentMonth === 0 ? 11 : currentMonth - 1
        const lastMonthYear = currentMonth === 0 ? currentYear - 1 : currentYear
        const lastMonthUsers = dataList.filter(user => {
          const createdDate = new Date(user.createdAt)
          return createdDate.getMonth() === lastMonth && 
                 createdDate.getFullYear() === lastMonthYear
        }).length
        
        // 计算增长率
        if (lastMonthUsers > 0) {
          monthlyGrowth.value = Math.round(((currentMonthUsers - lastMonthUsers) / lastMonthUsers) * 100)
        } else if (currentMonthUsers > 0) {
          monthlyGrowth.value = 100 // 如果上月为0，本月有用户，则增长率为100%
        } else {
          monthlyGrowth.value = 0
        }
        
        // 设置月度新增用户数
        monthlyNewUsers.value = currentMonthUsers
        
        // 计算月度趋势（过去30天每天的新增用户数）
        const last30Days = []
        for (let i = 0; i < 30; i++) {
          const date = new Date()
          date.setDate(date.getDate() - i)
          date.setHours(0, 0, 0, 0)
          
          const nextDay = new Date(date)
          nextDay.setDate(nextDay.getDate() + 1)
          
          const count = dataList.filter(user => {
            const createdDate = new Date(user.createdAt)
            return createdDate >= date && createdDate < nextDay
          }).length
          
          last30Days.unshift(count) // 添加到数组开头，保持时间顺序
        }
        monthlyTrend.value = last30Days
        
        // 计算周活跃度（过去7天每天的活跃用户数，这里用更新时间作为活跃指标）
        const last7Days = []
        for (let i = 0; i < 7; i++) {
          const date = new Date()
          date.setDate(date.getDate() - i)
          date.setHours(0, 0, 0, 0)
          
          const nextDay = new Date(date)
          nextDay.setDate(nextDay.getDate() + 1)
          
          const count = dataList.filter(user => {
            const updatedDate = new Date(user.updatedAt)
            return updatedDate >= date && updatedDate < nextDay
          }).length
          
          last7Days.unshift(count) // 添加到数组开头，保持时间顺序
        }
        weeklyActivity.value = last7Days
      }

      ElMessage.success('数据加载成功')
    } else if (response.code === 400) {
      console.warn('请求参数错误:', response.msg)
      ElMessage.error(response.msg || '请求参数错误，请检查输入')
      tableData.value = []
    } else {
      console.error('获取数据失败:', response)
      ElMessage.error(response.msg || '获取用户数据失败')
      tableData.value = []
    }
  } catch (error) {
    console.error('获取用户数据时发生错误:', error)
    ElMessage.error('获取用户数据时发生错误')
    tableData.value = []
  } finally {
    loading.value = false
    console.log('数据获取流程结束')
  }
}

// 手动修改页码
const manuallySetPage = (page) => {
  console.log('手动设置页码:', page)
  pagination.current = parseInt(page)
  fetchUsers()
}

// 手动修改每页条数
const manuallySetPageSize = (size) => {
  console.log('手动设置每页条数:', size)
  pagination.size = parseInt(size)
  pagination.current = 1
  fetchUsers()
}

// 处理搜索
const handleSearch = () => {
  pagination.current = 1
  fetchUsers()
}

// 重置搜索和排序
const resetSearch = () => {
  searchForm.value.searchText = ''
  sortConfig.value.field = ''
  sortConfig.value.order = ''
  pagination.current = 1
  fetchUsers()
  ElMessage.success('已重置搜索和排序')
}

// 处理排序
const handleSortChange = ({ prop, order }) => {
  sortConfig.value.field = prop
  sortConfig.value.order = order
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  try {
    return new Date(dateString).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (error) {
    return dateString
  }
}

// 格式化数值
const formatNumber = (value, unit = '', decimals = 1) => {
  if (value === null || value === undefined) return '-'
  try {
    return `${Number(value).toFixed(decimals)}${unit}`
  } catch (error) {
    return `${value}${unit}`
  }
}

// 批量删除用户
const handleBatchDelete = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请至少选择一个用户')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedUsers.value.length} 个用户吗？此操作不可恢复！`,
      '批量删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    loading.value = true
    const userIds = selectedUsers.value.map(user => user.userId)
    
    // 调用批量删除API
    const response = await deleteUsersInBulk(userIds)

    // 根据新的接口文档处理响应
    switch (response.code) {
      case 200: // 删除成功
        ElMessage.success('用户删除成功')
        selectedUsers.value = []
        await fetchUsers()
        break
      case 1000: // 部分用户不存在
        ElMessageBox.alert(
          `<div class="delete-result-container">
            <div class="summary">
              <p><strong>删除结果:</strong></p>
              <p>部分用户不存在</p>
            </div>
            <div class="failed-details">
              <p><strong>详细信息:</strong></p>
              <p>${response.message || '未知错误'}</p>
            </div>
          </div>`,
          '删除提示',
          {
            dangerouslyUseHTMLString: true,
            confirmButtonText: '确定',
            type: 'warning'
          }
        )
        selectedUsers.value = []
        await fetchUsers()
        break
      case 1045: // 删除用户账号失败
        ElMessageBox.alert(
          `<div class="delete-result-container">
            <div class="summary">
              <p><strong>删除失败:</strong></p>
              <p>删除用户账号失败</p>
            </div>
            <div class="failed-details">
              <p><strong>错误信息:</strong></p>
              <p>${response.message || '未知错误'}</p>
            </div>
          </div>`,
          '删除失败',
          {
            dangerouslyUseHTMLString: true,
            confirmButtonText: '确定',
            type: 'error'
          }
        )
        break
      case 500: // 服务器错误
        ElMessage.error(response.message || '服务器错误，删除失败')
        break
      default:
        ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error === 'cancel') return
    console.error('批量删除出错:', error)
    ElMessage.error('批量删除操作失败，请重试')
  } finally {
    loading.value = false
  }
}

// 单个用户删除
const deleteUser = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${row.username}" 吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    loading.value = true
    
    // 调用删除API
    const response = await deleteUsersInBulk([row.userId])

    // 根据新的接口文档处理响应
    switch (response.code) {
      case 200: // 删除成功
        ElMessage.success(`用户 "${row.username}" 删除成功`)
        await fetchUsers()
        break
      case 1000: // 用户不存在
        ElMessageBox.alert(
          `<div class="delete-result-container">
            <div class="summary">
              <p><strong>删除结果:</strong></p>
              <p>用户不存在</p>
            </div>
            <div class="failed-details">
              <p><strong>详细信息:</strong></p>
              <p>${response.message || '未知错误'}</p>
            </div>
          </div>`,
          '删除提示',
          {
            dangerouslyUseHTMLString: true,
            confirmButtonText: '确定',
            type: 'warning'
          }
        )
        await fetchUsers()
        break
      case 1045: // 删除用户账号失败
        ElMessageBox.alert(
          `<div class="delete-result-container">
            <div class="summary">
              <p><strong>删除失败:</strong></p>
              <p>删除用户账号失败</p>
            </div>
            <div class="failed-details">
              <p><strong>错误信息:</strong></p>
              <p>${response.message || '未知错误'}</p>
            </div>
          </div>`,
          '删除失败',
          {
            dangerouslyUseHTMLString: true,
            confirmButtonText: '确定',
            type: 'error'
          }
        )
        break
      case 500: // 服务器错误
        ElMessage.error(response.message || '服务器错误，删除失败')
        break
      default:
        ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error === 'cancel') return
    console.error('删除用户失败:', error)
    ElMessage.error('删除用户失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 处理表格行点击
const handleRowClick = (row, column, event) => {
  // 只在点击复选框时触发选择
  if (column && column.type === 'selection') {
    const index = selectedUsers.value.findIndex(user => user.userId === row.userId)
    if (index === -1) {
      tableRef.value?.toggleRowSelection(row, true)
    } else {
      tableRef.value?.toggleRowSelection(row, false)
    }
  }
}

// 切换表格大小
const toggleTableSize = () => {
  const sizes = ['default', 'large', 'small']
  const currentIndex = sizes.indexOf(tableConfig.value.size)
  tableConfig.value.size = sizes[(currentIndex + 1) % sizes.length]
}

// 切换表格条纹
const toggleTableStripe = () => {
  tableConfig.value.stripe = !tableConfig.value.stripe
}

// 切换表格边框
const toggleTableBorder = () => {
  tableConfig.value.border = !tableConfig.value.border
}

// 导出数据
const exportData = () => {
  const data = processedData.value
  const headers = [
    'ID', '用户名', '姓名', '性别', '邮箱', 
    '电话', '身高(cm)', '体重(kg)', '创建时间', '更新时间'
  ]
  const csvContent = [
    headers.join(','),
    ...data.map(row => [
      row.userId,
      row.username,
      row.fullName,
      row.gender,
      row.email,
      row.phoneNumber,
      row.height,
      row.weight,
      formatDate(row.createdAt),
      formatDate(row.updatedAt)
    ].join(','))
  ].join('\n')

  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `用户数据_${new Date().toLocaleDateString()}.csv`
  link.click()
  URL.revokeObjectURL(link.href)
}

// 初始化动画
const initializeAnimations = async () => {
  await nextTick()
  const { duration, easing, delay } = animationConfig.value
  
  // 设置CSS变量
  const root = document.documentElement
  root.style.setProperty('--animation-duration', `${duration}ms`)
  root.style.setProperty('--animation-easing', easing)
  
  // 按序显示各个部分
  setTimeout(() => {
    animationState.value.headerVisible = true
  }, delay.header)
  
  setTimeout(() => {
    animationState.value.contentVisible = true
  }, delay.content)
  
  setTimeout(() => {
    animationState.value.tableLoaded = true
  }, delay.table)
}

// 过滤表单
const filterForm = ref({
  username: '',
  dateRange: null,
  isAsc: true
})

// 处理过滤
const handleFilter = () => {
  pagination.current = 1
  fetchUsers()
}

// 重置过滤
const resetFilter = () => {
  filterForm.value = {
    username: '',
    dateRange: null,
    isAsc: true
  }
  pagination.current = 1
  fetchUsers()
}

// 刷新数据
const refreshData = () => {
  fetchUsers()
}

// 表格列配置
const columns = [
  { prop: 'userId', label: '用户ID', width: '100', sortable: true },
  { prop: 'username', label: '用户名', width: '120' },
  { prop: 'fullName', label: '姓名', width: '120' },
  { prop: 'gender', label: '性别', width: '80' },
  { prop: 'email', label: '邮箱', width: '180' },
  { prop: 'phoneNumber', label: '电话', width: '140' },
  { prop: 'height', label: '身高(cm)', width: '100', sortable: true },
  { prop: 'weight', label: '体重(kg)', width: '100', sortable: true },
  { prop: 'createdAt', label: '创建时间', width: '180', sortable: true },
  { prop: 'updatedAt', label: '更新时间', width: '180', sortable: true }
]

// 查看用户详情
const viewUserDetail = (row) => {
  ElMessageBox.alert(
    `<div style="max-height: 300px; overflow-y: auto;">
      <p><strong>用户ID:</strong> ${row.userId}</p>
      <p><strong>用户名:</strong> ${row.username}</p>
      <p><strong>姓名:</strong> ${row.fullName || '-'}</p>
      <p><strong>性别:</strong> ${row.gender || '-'}</p>
      <p><strong>出生日期:</strong> ${row.birthDate ? formatDate(row.birthDate) : '-'}</p>
      <p><strong>身高:</strong> ${row.height ? `${row.height} cm` : '-'}</p>
      <p><strong>体重:</strong> ${row.weight ? `${row.weight} kg` : '-'}</p>
      <p><strong>邮箱:</strong> ${row.email || '-'}</p>
      <p><strong>电话:</strong> ${row.phoneNumber || '-'}</p>
      <p><strong>注册时间:</strong> ${formatDate(row.createdAt)}</p>
      <p><strong>更新时间:</strong> ${formatDate(row.updatedAt)}</p>
    </div>`,
    '用户详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  )
}

// 编辑用户信息
const editUser = (row) => {
  ElMessage.info('编辑用户功能正在开发中')
  // 这里可以实现编辑用户的逻辑，例如打开编辑对话框
}

// 显示分页信息
const showPaginationInfo = () => {
  ElMessageBox.alert(
    `<div style="max-height: 300px; overflow-y: auto;">
      <p><strong>当前分页状态:</strong></p>
      <p><strong>总页数:</strong> ${pagination.pages}</p>
      <p><strong>当前页码:</strong> ${pagination.current}</p>
      <p><strong>每页条数:</strong> ${pagination.size}</p>
      <p><strong>总记录数:</strong> ${pagination.total}</p>
    </div>`,
    '分页信息',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  )
}

// 添加行样式类函数
const getRowClassName = ({ row, rowIndex }) => {
  return isDarkMode.value ? 'dark-mode-row' : 'light-mode-row'
}

onMounted(() => {
  // 确保分页参数是数字类型
  pagination.current = 1
  pagination.size = 15
  pagination.total = 0
  pagination.pages = 0
  
  fetchUsers()
  initializeAnimations()
})
</script>

<template>
  <div class="admin-dashboard" :class="{ 'dark': isDarkMode }">
    <!-- 数据概览卡片 -->
    <div class="overview-cards" v-loading="loading">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="overview-card">
            <template #header>
              <div class="card-header">
                <span>总用户数</span>
                <el-tag type="info" effect="plain">实时</el-tag>
              </div>
            </template>
            <div class="card-content">
              <div class="number">{{ pagination.total || 0 }}</div>
              <div class="trend">
                <span>较上月</span>
                <span :class="['trend-value', monthlyGrowth >= 0 ? 'positive' : 'negative']">
                  {{ monthlyGrowth >= 0 ? '+' : '' }}{{ monthlyGrowth }}%
                </span>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="overview-card">
            <template #header>
              <div class="card-header">
                <span>本月新增</span>
                <el-tag type="success" effect="plain">月度</el-tag>
              </div>
            </template>
            <div class="card-content">
              <div class="number">{{ monthlyNewUsers }}</div>
              <div class="chart-container" :class="{'dark-theme-chart': currentTheme === 'dark'}">
                <mini-line-chart :data="monthlyTrend" />
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="overview-card">
            <template #header>
              <div class="card-header">
                <span>性别比例</span>
                <el-tag type="warning" effect="plain">统计</el-tag>
              </div>
            </template>
            <div class="card-content">
              <div class="chart-container" :class="{'dark-theme-chart': currentTheme === 'dark'}">
                <mini-pie-chart :data="genderRatio" />
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="overview-card">
            <template #header>
              <div class="card-header">
                <span>活跃度</span>
                <el-tag type="primary" effect="plain">周度</el-tag>
              </div>
            </template>
            <div class="card-content">
              <div class="chart-container" :class="{'dark-theme-chart': currentTheme === 'dark'}">
                <mini-bar-chart :data="weeklyActivity" />
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 高级筛选器 -->
    <el-card class="filter-card" shadow="never">
      <div class="filter-container">
        <el-form :model="filterForm" label-width="100px" :inline="true">
          <el-form-item label="用户名">
            <el-input
              v-model="filterForm.username"
              placeholder="请输入用户名"
              clearable
              @clear="handleFilter"
            />
          </el-form-item>
          <el-form-item label="时间范围">
            <el-date-picker
              v-model="filterForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD'T'HH:mm:ss.SSSXXX"
              @change="handleFilter"
            />
          </el-form-item>
          <el-form-item label="排序方式">
            <el-select 
              v-model="filterForm.isAsc" 
              placeholder="请选择" 
              @change="handleFilter"
              style="width: 120px;"
              :popper-class="isDarkMode ? 'dark-select-dropdown' : ''"
            >
              <el-option label="升序" :value="true" />
              <el-option label="降序" :value="false" />
            </el-select>
          </el-form-item>
          <el-form-item class="filter-buttons">
            <el-button 
              type="primary" 
              @click="handleFilter"
              :icon="Search"
              class="search-btn"
            >
              查询
            </el-button>
            <el-button 
              @click="resetFilter"
              :icon="Refresh"
              class="reset-btn"
            >
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="table-header">
          <div class="left">
            <span class="title">用户列表</span>
            <el-tag type="info" effect="plain">{{ pagination.total }} 条记录</el-tag>
        </div>
          <div class="right">
            <el-button-group>
              <el-tooltip content="刷新数据">
                <el-button :icon="Refresh" circle @click="refreshData" />
              </el-tooltip>
              <el-tooltip content="导出数据">
                <el-button :icon="Download" circle @click="exportData" />
              </el-tooltip>
              <el-tooltip content="批量删除">
                <el-button 
                  :icon="Delete" 
                  circle 
                  type="danger" 
                  @click="handleBatchDelete"
                  :disabled="selectedUsers.length === 0"
                />
              </el-tooltip>
            </el-button-group>
      </div>
        </div>
      </template>

        <el-table
          ref="tableRef"
          :data="tableData"
          style="width: 100%"
          :border="false"
          :stripe="false"
          :highlight-current-row="false"
          :row-class-name="getRowClassName"
          @selection-change="handleSelectionChange"
          @sort-change="handleSortChange"
          @row-click="handleRowClick"
          v-loading="loading"
          :empty-text="noDataText"
        >
        <el-table-column type="selection" width="55" align="center" fixed="left" />
        <el-table-column type="index" width="50" align="center" fixed="left" :index="(index) => (pagination.current - 1) * pagination.size + index + 1" />
        
        <template v-for="column in columns" :key="column.prop">
          <el-table-column
            :prop="column.prop"
            :label="column.label"
            :width="column.width"
            :sortable="column.sortable ? 'custom' : false"
            align="center"
          >
            <template #default="{ row }">
              <template v-if="column.prop === 'gender'">
              <el-tag :type="row.gender === '男' ? 'primary' : 'success'" size="small">
                {{ row.gender }}
              </el-tag>
            </template>
              <template v-else-if="column.prop === 'createdAt' || column.prop === 'updatedAt'">
                {{ formatDate(row[column.prop]) }}
            </template>
              <template v-else>
                {{ row[column.prop] ?? '-' }}
              </template>
            </template>
          </el-table-column>
        </template>

        <el-table-column fixed="right" label="操作" width="120" align="center">
            <template #default="{ row }">
            <el-button-group>
              <el-tooltip content="查看详情">
                <el-button :icon="View" circle size="small" @click="viewUserDetail(row)" />
              </el-tooltip>
              <el-tooltip content="删除用户">
                <el-button :icon="Delete" circle size="small" type="danger" @click="deleteUser(row)" />
              </el-tooltip>
            </el-button-group>
            </template>
          </el-table-column>
      </el-table>

      <!-- 分页器 -->
      <div class="pagination-container">
        <el-button 
          type="primary" 
          :icon="Refresh" 
          circle 
          size="small" 
          @click="refreshData" 
          :loading="loading"
          style="margin-right: 10px;"
          title="刷新数据"
        />
        <el-button 
          type="info" 
          :icon="Setting" 
          circle 
          size="small" 
          @click="showPaginationInfo" 
          style="margin-right: 10px;"
          title="查看分页信息"
        />
        
        <!-- 自定义分页控件 -->
        <div class="custom-pagination">
          <span class="pagination-info">共 {{ pagination.total }} 条记录，每页</span>
          <el-select 
            v-model="pagination.size" 
            @change="manuallySetPageSize" 
            style="width: 80px; margin: 0 5px;"
            :disabled="loading"
          >
            <el-option :value="15" label="15条" />
            <el-option :value="30" label="30条" />
            <el-option :value="50" label="50条" />
          </el-select>
          
          <span class="pagination-info">条，当前第</span>
          
          <el-button 
            type="primary" 
            size="small" 
            :disabled="pagination.current <= 1 || loading" 
            @click="manuallySetPage(pagination.current - 1)"
            style="margin: 0 5px;"
          >
            上一页
          </el-button>
          
          <el-input 
            v-model="pagination.current" 
            style="width: 60px; margin: 0 5px;" 
            @change="manuallySetPage(pagination.current)"
            :disabled="loading"
          />
          
          <span class="pagination-info">/ {{ pagination.pages }} 页</span>
          
          <el-button 
            type="primary" 
            size="small" 
            :disabled="pagination.current >= pagination.pages || loading" 
            @click="manuallySetPage(pagination.current + 1)"
            style="margin: 0 5px;"
          >
            下一页
          </el-button>
          
          <el-button 
            type="primary" 
            size="small" 
            @click="manuallySetPage(pagination.current)"
            :disabled="loading"
          >
            跳转
          </el-button>
      </div>
    </div>
    </el-card>
  </div>
</template>

<style scoped>
.admin-dashboard {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 160px);
  color: #1F2937;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

/* 夜间模式样式 */
.admin-dashboard.dark {
  background: linear-gradient(135deg, #0d0e15, #090b13);
  color: #ffffff;
  position: relative;
  height: auto;
  overflow: hidden;
  padding-bottom: 20px;
  margin-bottom: 0;
}

/* 添加霓虹效果背景 */
.admin-dashboard.dark::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: 
    radial-gradient(circle, rgba(0, 255, 255, 0.1), transparent 40%),
    radial-gradient(circle at 70% 30%, rgba(255, 20, 147, 0.08), transparent 35%);
  animation: neonFlow 20s linear infinite;
  z-index: -1;
  bottom: 0;
}

@keyframes neonFlow {
  0% { transform: translate(0, 0) rotate(0deg); }
  50% { transform: translate(10%, 10%) rotate(180deg); }
  100% { transform: translate(0, 0) rotate(360deg); }
}

/* 夜间模式下的卡片样式 */
.admin-dashboard.dark .overview-card {
  background: rgba(10, 10, 20, 0.9);
  border-color: rgba(255, 255, 255, 0.1);
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.1);
}

.admin-dashboard.dark .overview-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 0 30px rgba(0, 255, 255, 0.2);
}

.admin-dashboard.dark .card-header {
  color: #ffffff;
}

.admin-dashboard.dark .number {
  color: #00ffff;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}

.admin-dashboard.dark .trend {
  color: rgba(255, 255, 255, 0.7);
}

.admin-dashboard.dark .trend-value {
  margin-left: 8px;
  font-weight: bold;
}

.admin-dashboard.dark .trend-value.positive {
  color: #67c23a;
}

.admin-dashboard.dark .trend-value.negative {
  color: #f56c6c;
}

.admin-dashboard.dark .chart-container {
  height: 100%;
  width: 100%;
}

.admin-dashboard.dark .filter-card {
  background: rgba(13, 14, 21, 0.9);
  border-color: rgba(0, 255, 255, 0.1);
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.admin-dashboard.dark .filter-card:hover {
  box-shadow: 0 0 25px rgba(0, 255, 255, 0.15);
}

.admin-dashboard.dark .table-header .title {
  color: #00ffff;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.3);
}

.admin-dashboard.dark :deep(.el-card__header) {
  border-bottom-color: rgba(0, 255, 255, 0.1);
  background: rgba(17, 19, 29, 0.95);
}

.admin-dashboard.dark :deep(.el-input__wrapper) {
  background-color: rgba(17, 19, 29, 0.95);
  box-shadow: 0 0 0 1px rgba(0, 255, 255, 0.2) inset;
}

.admin-dashboard.dark :deep(.el-input__inner) {
  color: #e2e8f0;
}

.admin-dashboard.dark :deep(.el-input__placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

.admin-dashboard.dark :deep(.el-button) {
  background: rgba(0, 255, 255, 0.1);
  border-color: rgba(0, 255, 255, 0.2);
  color: #e2e8f0;
}

.admin-dashboard.dark :deep(.el-button:hover) {
  background: rgba(0, 255, 255, 0.15);
  border-color: rgba(0, 255, 255, 0.3);
  box-shadow: 0 0 15px rgba(0, 255, 255, 0.2);
}

.admin-dashboard.dark :deep(.el-button--primary) {
  background: rgba(0, 255, 255, 0.2);
  border-color: rgba(0, 255, 255, 0.3);
  color: #00ffff;
}

.admin-dashboard.dark :deep(.el-button--primary:hover) {
  background: rgba(0, 255, 255, 0.3);
  border-color: rgba(0, 255, 255, 0.4);
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.3);
}

.admin-dashboard.dark :deep(.el-tag) {
  background-color: rgba(0, 255, 255, 0.1);
  border-color: rgba(0, 255, 255, 0.2);
  color: #00ffff;
}

.admin-dashboard.dark .pagination-info {
  color: rgba(255, 255, 255, 0.7);
}

.admin-dashboard.dark :deep(.el-form-item__label) {
  color: #e2e8f0;
}

.admin-dashboard.dark :deep(.el-select .el-input__wrapper) {
  background-color: rgba(17, 19, 29, 0.95);
  box-shadow: 0 0 0 1px rgba(0, 255, 255, 0.2) inset;
  border-radius: 4px;
}

.admin-dashboard.dark :deep(.el-select:hover .el-input__wrapper) {
  box-shadow: 0 0 0 1px rgba(0, 255, 255, 0.4) inset;
}

.admin-dashboard.dark :deep(.el-select .el-input__inner) {
  color: #e2e8f0;
}

.admin-dashboard.dark :deep(.dark-select-dropdown) {
  background-color: rgba(17, 19, 29, 0.95) !important;
  border: 1px solid rgba(0, 255, 255, 0.2) !important;
  backdrop-filter: blur(10px);

  .el-select-dropdown__item {
    color: #e2e8f0;
    
    &:hover, &.hover {
      background-color: rgba(0, 255, 255, 0.1);
    }
    
    &.selected {
      background-color: rgba(0, 255, 255, 0.2);
      color: #00ffff;
    }
  }
}

.admin-dashboard.dark :deep(.el-select-dropdown__item.selected) {
  background-color: rgba(0, 255, 255, 0.2);
  color: #00ffff;
}

.filter-buttons {
  margin-left: 20px;
}

.filter-buttons :deep(.search-btn) {
  margin-right: 10px;
  min-width: 100px;
  transition: all 0.3s;
}

.filter-buttons :deep(.reset-btn) {
  min-width: 100px;
  transition: all 0.3s;
}

.filter-buttons :deep(.search-btn) {
  background: rgba(0, 255, 255, 0.15);
  border-color: rgba(0, 255, 255, 0.3);
  color: #00ffff;
}

.filter-buttons :deep(.search-btn:hover) {
  background: rgba(0, 255, 255, 0.25);
  border-color: rgba(0, 255, 255, 0.4);
  box-shadow: 0 0 15px rgba(0, 255, 255, 0.3);
  transform: translateY(-1px);
}

.filter-buttons :deep(.reset-btn) {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
  color: #e2e8f0;
}

.filter-buttons :deep(.reset-btn:hover) {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 0 15px rgba(255, 255, 255, 0.1);
  transform: translateY(-1px);
}

.delete-result-container {
  max-height: 300px;
  overflow-y: auto;
}

.delete-result-container .summary {
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.delete-result-container .total {
  color: #409EFF;
  font-weight: bold;
}

.delete-result-container .success {
  color: #67c23a;
  font-weight: bold;
}

.delete-result-container .failed {
  color: #f56c6c;
  font-weight: bold;
}

.delete-result-container .failed-details {
  margin-top: 10px;
}

.delete-result-container .failed-details ul {
  list-style-type: none;
  padding-left: 0;
}

.delete-result-container .failed-details li {
  margin-bottom: 5px;
  padding: 5px;
  background-color: #fff1f0;
  border-radius: 4px;
  color: #f56c6c;
}

/* 夜间模式下的表格样式 */
.admin-dashboard.dark :deep(.el-table) {
  /* 基础样式设置 */
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-hover-bg-color: transparent;
  --el-table-border-color: rgba(255, 255, 255, 0.1);
  --el-table-header-bg-color: rgba(17, 19, 29, 0.95);
  --el-table-header-text-color: #00ffff;
  --el-table-text-color: #e2e8f0;
  
  /* 移除所有默认的表格样式 */
  --el-table-fixed-right-column-bg-color: transparent;
  --el-table-fixed-left-column-bg-color: transparent;
  --el-table-fixed-right-column-border-color: transparent;
  --el-table-fixed-left-column-border-color: transparent;
  --el-table-row-hover-bg-color: transparent;
  --el-table-current-row-bg-color: transparent;
  --el-table-striped-bg-color: transparent;
  
  background-color: transparent;
  
  /* 表格基础结构 */
  &::before,
  .el-table__inner-wrapper::before {
    display: none;
  }
  
  /* 表头样式 */
  .el-table__header-wrapper {
    th.el-table__cell {
      background-color: var(--el-table-header-bg-color);
      color: var(--el-table-header-text-color);
      border-bottom: 1px solid rgba(0, 255, 255, 0.2);
      font-weight: 500;
    }
  }
  
  /* 表体样式 */
  .el-table__body-wrapper {
    background-color: transparent;
    
    tr.el-table__row {
      background-color: transparent;
      transition: background-color 0.3s ease;
      
      td.el-table__cell {
        background-color: transparent;
        border-color: rgba(255, 255, 255, 0.05);
        color: var(--el-table-text-color);
      }
      
      /* 悬浮效果 */
      &:hover > td.el-table__cell {
        background-color: rgba(0, 0, 0, 0.3) !important;
      }
      
      /* 选中效果 */
      &.current-row > td.el-table__cell {
        background-color: transparent !important;
      }
      
      /* 条纹效果 */
      &.el-table__row--striped > td.el-table__cell {
        background-color: transparent !important;
      }
    }
  }
  
  /* 固定列样式 */
  .el-table__fixed-right,
  .el-table__fixed-left {
    background-color: transparent;
    &::before {
      display: none;
    }
    
    .el-table__fixed-body-wrapper {
      background-color: transparent;
    }
  }
}

/* 夜间模式下的表格卡片样式 */
.admin-dashboard.dark .table-card {
  background: rgba(13, 14, 21, 0.9);
  border-color: rgba(0, 255, 255, 0.1);
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.admin-dashboard.dark .table-card:hover {
  box-shadow: 0 0 25px rgba(0, 255, 255, 0.15);
}

/* 常规卡片和统计信息样式 */
.overview-cards {
  margin-bottom: 20px;
}

.overview-card {
  height: 180px;
  transition: all 0.3s;
  background-color: #FFFFFF;
  border-color: #E5E7EB;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.overview-card:hover {
  transform: translateY(-5px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #1F2937;
}

.card-content {
  height: 100px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.trend {
  font-size: 14px;
  color: #909399;
}

.trend-value {
  margin-left: 8px;
  font-weight: bold;
}

.trend-value.positive {
  color: #67c23a;
}

.trend-value.negative {
  color: #f56c6c;
}

.chart-container {
  height: 100%;
  width: 100%;
}

/* 筛选卡片样式 */
.filter-card {
  margin-bottom: 20px;
  background-color: #FFFFFF;
  border-color: #E5E7EB;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.filter-container {
  padding: 20px 0;
  
  :deep(.el-form) {
    display: flex;
    flex-wrap: wrap;
    align-items: flex-start;
    gap: 15px;
  }

  :deep(.el-form-item) {
    margin-bottom: 0;
    margin-right: 20px;
  }

  :deep(.el-form-item__label) {
    font-weight: 500;
  }
}

/* 表格卡片样式 */
.table-card {
  margin-bottom: 20px;
  background-color: #FFFFFF;
  border-color: #E5E7EB;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-header .title {
  font-size: 16px;
  font-weight: bold;
  margin-right: 10px;
  color: #1F2937;
}

/* 分页器样式 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  flex-wrap: wrap;
  align-items: center;
}

.custom-pagination {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin: 5px 0;
}

.pagination-info {
  color: #606266;
  font-size: 14px;
  margin: 0 5px;
}

@media screen and (max-width: 768px) {
  .pagination-container {
    justify-content: center;
  }
  
  .custom-pagination {
    justify-content: center;
    margin-top: 10px;
  }
}

/* 响应式适配 */
@media screen and (max-width: 768px) {
  .overview-cards {
    :deep(.el-col) {
      width: 100%;
      margin-bottom: 20px;
    }
  }

  .filter-container {
    :deep(.el-form-item) {
      margin-bottom: 10px;
    }
  }
}

/* 表格样式 */
:deep(.el-table) {
  --el-table-header-bg-color: #F9FAFB;
  --el-table-border-color: #E5E7EB;
  --el-table-bg-color: #FFFFFF;
  --el-table-tr-bg-color: #FFFFFF;
  --el-table-hover-bg-color: #F9FAFB;
  color: #1F2937;
  transition: all 0.3s ease;
  background-color: #FFFFFF;
}

:deep(.el-table th) {
  background-color: #F9FAFB;
  color: #1F2937;
}

:deep(.el-table td) {
  border-bottom-color: #E5E7EB;
  color: #1F2937;
}

:deep(.el-table--border), 
:deep(.el-table--group) {
  border-color: #E5E7EB;
}

:deep(.el-table tr) {
  background-color: #FFFFFF;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: #f9fafb;
}

/* 表单元素样式 */
:deep(.el-input__wrapper) {
  background-color: #FFFFFF;
  box-shadow: 0 0 0 1px #DCDFE6 inset;
}

:deep(.el-input__inner) {
  color: #606266;
}

:deep(.el-input__placeholder) {
  color: #A0AEC0;
}

/* 按钮样式 */
:deep(.el-button) {
  background-color: #FFFFFF;
  border-color: #DCDFE6;
  color: #606266;
  transition: all 0.3s ease;
}

:deep(.el-button:not(.el-button--primary):not(.el-button--success):not(.el-button--warning):not(.el-button--danger):not(.el-button--info):hover) {
  background-color: #F3F4F6;
  border-color: #E5E7EB;
  color: #1F2937;
}

/* 日期相关样式 */
:deep(.el-date-table td) {
  color: #1F2937;
}

:deep(.el-date-table td.available:hover) {
  color: #3B82F6;
}

:deep(.el-date-table td.current:not(.disabled)) {
  color: #FFFFFF;
}

:deep(.el-picker-panel) {
  background-color: #FFFFFF;
  border-color: #E5E7EB;
}

:deep(.el-picker-panel__content) {
  color: #1F2937;
}

:deep(.el-date-picker__header-label) {
  color: #1F2937;
}

:deep(.el-date-picker__time-header) {
  border-bottom-color: #E5E7EB;
}

/* 夜间模式下的图表主题适配 */
:deep(.dark-theme-chart) {
  filter: invert(0.8) hue-rotate(180deg);
}

/* 夜间模式下的饼图文字样式 */
.admin-dashboard.dark :deep(.mini-pie-chart text) {
  fill: #00ffff !important;
  text-shadow: 0 0 8px rgba(0, 255, 255, 0.4);
}
</style> 