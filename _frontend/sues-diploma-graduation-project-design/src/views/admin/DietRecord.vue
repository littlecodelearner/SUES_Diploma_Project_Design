<template>
  <!-- 饮食记录列表卡片 -->
  <el-card class="diet-record-card">
    <template #header>
      <div class="card-header">
        <span class="card-title">饮食记录管理</span>
        <div class="header-actions">
          <!-- 搜索和操作按钮 -->
          // ... existing code ...
        </div>
      </div>
    </template>
    
    <!-- 饮食记录表格 -->
    <el-table
      v-loading="loading"
      :data="recordList"
      style="width: 100%"
      border
      stripe
    >
      // ... existing table columns ...
    </el-table>
    
    <!-- 替换原有分页器为增强版ScrollablePagination -->
    <scrollable-pagination
      v-model:current-page="searchForm.current"
      v-model:page-size="searchForm.size"
      :total="total"
      :page-sizes="[15, 20, 30, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :show-info="true"
      :tooltip-content="'提示：饮食记录可能受到多种筛选条件影响，实际显示的记录数可能少于总数。'"
      :text-content="'总共 ' + total + ' 条记录'"
      :scroll-target="'.diet-record-card'"
      :top-offset="100"
    />
    
    <!-- 饮食记录编辑对话框 -->
    // ... existing code ...
  </el-card>
</template>

<script setup>
import {reactive, ref} from 'vue'
import {ElMessage} from 'element-plus'
// 导入增强版分页组件
import ScrollablePagination from '@/components/common/ScrollablePagination.vue'
// ... existing code ...

// 分页搜索表单
const searchForm = reactive({
  keyword: '',
  userId: '',
  dateRange: [],
  current: 1,
  size: 15,
  // ... existing code ...
})

// 记录列表和总数
const recordList = ref([])
const total = ref(0)
const loading = ref(false)

// 处理页码变化
const handleCurrentChange = (page) => {
  searchForm.current = page
  fetchRecordList()
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  searchForm.size = size
  searchForm.current = 1 // 重置页码到第一页
  fetchRecordList()
}

// 获取饮食记录列表数据
const fetchRecordList = async () => {
  loading.value = true
  try {
    // 预处理参数，确保参数合法
    const params = {
      ...searchForm,
      current: Math.min(Math.max(1, searchForm.current), 1000),
      size: Math.min(Math.max(15, searchForm.size), 50)
    }
    
    // 如果有日期范围，转换为开始日期和结束日期
    if (params.dateRange && params.dateRange.length === 2) {
      params.startDate = params.dateRange[0]
      params.endDate = params.dateRange[1]
      delete params.dateRange
    }
    
    console.log('请求参数:', params)
    const res = await listDietRecordsByPage(params)
    
    if (res.code === 200) {
      recordList.value = res.data.dataList || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取饮食记录失败')
    }
  } catch (error) {
    console.error('获取饮食记录失败:', error)
    ElMessage.error('获取饮食记录失败')
  } finally {
    loading.value = false
  }
}

// ... existing code ...
</script>

<style scoped>


/* 确保卡片有合适的定位上下文以便滚动定位 */
.diet-record-card {
  position: relative;
  margin-bottom: 20px;
}
</style> 