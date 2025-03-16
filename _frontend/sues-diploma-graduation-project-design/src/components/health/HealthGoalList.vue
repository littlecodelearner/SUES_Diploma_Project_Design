<template>
  <div class="health-goal-list" v-loading="loading">
    <!-- 批量操作按钮 -->
    <div v-if="healthGoals.length > 0" class="batch-actions">
      <div class="filter-section">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :shortcuts="dateShortcuts"
          @change="handleDateRangeChange"
          class="date-range-picker"
        />
        <el-button
          type="primary"
          link
          :icon="Refresh"
          @click="resetFilters"
        >
          重置筛选
        </el-button>
      </div>
      <div class="action-buttons">
        <el-button
          type="danger"
          :icon="Delete"
          :disabled="!selectedGoals.length"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
        <el-button
          type="primary"
          :icon="Edit"
          :disabled="!selectedGoals.length"
          @click="handleBatchEdit"
        >
          批量修改
        </el-button>
      </div>
    </div>

    <!-- 目标列表 -->
    <div class="goal-cards">
      <el-empty
        v-if="!loading && healthGoals.length === 0"
        description="暂无健康目标计划"
        :image-size="120"
      >
        <template #image>
          <el-icon class="empty-icon"><Flag /></el-icon>
        </template>
        <template #description>
          <p class="empty-text">设置您的健康目标，开启健康生活新篇章</p>
        </template>
      </el-empty>

      <div v-else class="goal-grid">
        <el-card
          v-for="goal in healthGoals"
          :key="goal.goalId"
          class="goal-card"
          shadow="hover"
        >
          <div class="goal-header">
            <div class="goal-actions">
              <el-checkbox
                v-model="selectedGoals"
                :label="goal.goalId"
                class="goal-checkbox"
              />
              <div class="goal-status" :style="{ color: getGoalStatusStyle(goal).color }">
                <el-icon><component :is="getGoalStatusStyle(goal).icon" /></el-icon>
                <span>{{ getGoalStatusStyle(goal).text }}</span>
              </div>
            </div>
            <div class="action-buttons">
              <el-button
                type="primary"
                link
                :icon="Edit"
                size="small"
                class="action-button"
                @click="handleEdit(goal)"
              >
                编辑
              </el-button>
              <el-button
                type="danger"
                link
                :icon="Delete"
                size="small"
                class="action-button"
                @click="handleDelete(goal)"
              >
                删除
              </el-button>
            </div>
          </div>

          <div class="goal-content">
            <h3 class="goal-title">{{ goal.targetPlan }}</h3>
            <div class="goal-date">
              <el-icon><Calendar /></el-icon>
              <span>目标日期：{{ formatDate(goal.targetDate) }}</span>
            </div>
            
            <!-- 关联的运动类型 -->
            <div v-if="goal.exerciseTypesList?.length" class="exercise-types">
              <div class="exercise-types-title">关联运动：</div>
              <div class="exercise-types-list">
                <el-tag
                  v-for="type in goal.exerciseTypesList"
                  :key="type.exerciseTypeId"
                  size="small"
                  class="exercise-type-tag"
                >
                  {{ type.exerciseName }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 分页器 -->
    <div v-if="pagination.total > 0">
      <scrollable-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[15, 20, 30, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        tooltip-content="提示：由于一条健康目标可能关联多个运动类型，系统可能显示的实际记录数量少于预期。例如：如果请求30条记录，但某条目标关联多个运动类型，实际可能只显示较少的不同记录。"
        text-content="查看更多记录"
        scroll-target=".health-goal-list"
        :top-offset="100"
      />
    </div>

    <!-- 编辑目标对话框 -->
    <el-dialog
      v-model="showEditDialog"
      :title="editingGoal ? '修改健康目标' : '批量修改健康目标'"
      width="800px"
      destroy-on-close
      :modal-append-to-body="true"
      :append-to-body="true"
      :close-on-click-modal="false"
      class="custom-dialog goal-form-dialog"
      align-center
    >
      <health-goal-batch-form
        v-if="showEditDialog"
        :user-id="userId"
        :edit-mode="true"
        :initial-data="editingGoal ? [editingGoal] : selectedGoalsData"
        @success="handleEditSuccess"
        @cancel="showEditDialog = false"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listPaginatedHealthGoalsByTimeRange, deleteHealthGoals, updateHealthGoalInBatch } from '@/api/healthGoal'
import { listExerciseTypesByPage } from '@/api/exerciseType'
import { useUserStore } from '@/store/user'
import { Calendar, Timer, Flag, Check, Close, Delete, Edit, Refresh } from '@element-plus/icons-vue'
import HealthGoalBatchForm from './HealthGoalBatchForm.vue'
import ScrollablePagination from '@/components/common/ScrollablePagination.vue'

const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['update-list'])

const userStore = useUserStore()
const loading = ref(false)
const healthGoals = ref([])
const selectedGoals = ref([])
const showEditDialog = ref(false)
const editingGoal = ref(null)

// 计算选中的目标数据
const selectedGoalsData = computed(() => {
  return healthGoals.value.filter(goal => selectedGoals.value.includes(goal.goalId))
})

const pagination = ref({
  current: 1,
  size: 15,
  total: 0,
  pages: 0
})

// 日期范围选择
const dateRange = ref([])
const dateShortcuts = [
  {
    text: '最近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    }
  },
  {
    text: '最近一个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    }
  },
  {
    text: '最近三个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    }
  }
]

// 获取健康目标列表
const fetchGoalList = async () => {
  if (!props.userId) return
  
  try {
    loading.value = true
    const requestParams = {
      userId: props.userId,
      current: pagination.value.current,
      size: pagination.value.size,
      isAsc: false // 最新的目标显示在前面
    }

    // 添加日期范围参数
    if (dateRange.value?.[0]) {
      requestParams.startDateTime = new Date(dateRange.value[0]).toISOString()
    }
    if (dateRange.value?.[1]) {
      requestParams.endDateTime = new Date(dateRange.value[1]).toISOString()
    }

    const response = await listPaginatedHealthGoalsByTimeRange(requestParams)

    if (response.code === 200) {
      // 确保状态字段是字符串类型
      healthGoals.value = (response.data.dataList || []).map(goal => ({
        ...goal,
        isFinished: String(goal.isFinished),
        isAbandoned: String(goal.isAbandoned)
      }))
      pagination.value = {
        current: response.data.current,
        size: response.data.size,
        total: response.data.total,
        pages: response.data.pages
      }
    } else {
      ElMessage.error(response.message || '获取健康目标列表失败')
    }
  } catch (error) {
    console.error('获取健康目标列表失败:', error)
    ElMessage.error('获取健康目标列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理单个目标删除
const handleDelete = async (goal) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个健康目标吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await deleteHealthGoals([goal.goalId])

    if (response.code === 200) {
      ElMessage.success('健康目标删除成功')
      fetchGoalList()
      emit('update-list')
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除健康目标失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

// 处理批量删除
const handleBatchDelete = async () => {
  if (!selectedGoals.value.length) return

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedGoals.value.length} 个健康目标吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await deleteHealthGoals(selectedGoals.value)

    if (response.code === 200) {
      ElMessage.success('健康目标批量删除成功')
      selectedGoals.value = []
      fetchGoalList()
      emit('update-list')
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除健康目标失败:', error)
      ElMessage.error('批量删除失败，请稍后重试')
    }
  }
}

// 处理单个目标编辑
const handleEdit = (goal) => {
  editingGoal.value = goal
  showEditDialog.value = true
}

// 处理批量编辑
const handleBatchEdit = () => {
  if (!selectedGoals.value.length) return
  editingGoal.value = null
  showEditDialog.value = true
}

// 处理编辑成功
const handleEditSuccess = async () => {
  showEditDialog.value = false
  editingGoal.value = null
  selectedGoals.value = []
  await fetchGoalList() // 确保等待数据刷新完成
  emit('update-list')
}

// 处理页码变化
const handlePageChange = (page) => {
  pagination.value.current = page
  fetchGoalList()
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  pagination.value.size = Math.max(15, size)
  pagination.value.current = 1
  fetchGoalList()
}

// 获取目标状态样式
const getGoalStatusStyle = (goal) => {
  if (goal.isAbandoned === '1') {
    return {
      color: '#F56C6C',
      icon: Close,
      text: '已放弃'
    }
  }
  if (goal.isFinished === '1') {
    return {
      color: '#67C23A',
      icon: Check,
      text: '已完成'
    }
  }
  return {
    color: '#409EFF',
    icon: Timer,
    text: '进行中'
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 处理日期范围变化
const handleDateRangeChange = () => {
  pagination.value.current = 1 // 重置页码
  fetchGoalList()
}

// 重置筛选条件
const resetFilters = () => {
  dateRange.value = []
  pagination.value.current = 1
  fetchGoalList()
}

// 组件挂载时获取数据
onMounted(() => {
  fetchGoalList()
})

// 暴露方法给父组件
defineExpose({
  fetchGoalList
})
</script>

<style scoped>
.health-goal-list {
  min-height: 200px;
}

.goal-cards {
  margin-bottom: 20px;
}

.goal-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 10px;
}

.goal-card {
  transition: all 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
}

.goal-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.goal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.goal-status {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  font-weight: 500;
}

.goal-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

.goal-checkbox {
  margin-right: 4px;
}

.goal-date {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin: 12px 0;
}

.goal-content {
  padding: 8px 0;
}

.goal-title {
  margin: 0 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  line-height: 1.5;
}

.exercise-types {
  margin-top: 16px;
}

.exercise-types-title {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-bottom: 8px;
}

.exercise-types-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.exercise-type-tag {
  background-color: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-7);
  color: var(--el-color-primary);
}

/* 已移至ScrollablePagination组件中
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
*/

/* 分页提示相关样式已移至 PaginationInfo 组件中 */

.empty-icon {
  font-size: 60px;
  color: var(--el-color-primary);
  opacity: 0.8;
}

.empty-text {
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin: 8px 0;
}

.batch-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px;
  background: var(--el-bg-color);
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.filter-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.date-range-picker {
  width: 320px;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

@media screen and (max-width: 768px) {
  .goal-grid {
    grid-template-columns: 1fr;
  }

  .batch-actions {
    flex-direction: column;
    gap: 12px;
  }

  .filter-section {
    width: 100%;
    flex-direction: column;
    align-items: stretch;
  }

  .date-range-picker {
    width: 100%;
  }

  .action-buttons {
    width: 100%;
    justify-content: flex-end;
  }
}

/* 自定义对话框样式 */
:deep(.goal-form-dialog) {
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.goal-form-dialog .el-dialog) {
  margin: 0 auto !important;
  max-height: 90vh;
  overflow-y: auto;
}

:deep(.goal-form-dialog .el-dialog__body) {
  padding: 20px;
  max-height: calc(90vh - 120px);
  overflow-y: auto;
}

/* 调整勾选框大小 */
:deep(.goal-checkbox .el-checkbox__inner) {
  width: 18px;
  height: 18px;
  border-radius: 4px;
}

:deep(.goal-checkbox .el-checkbox__inner::after) {
  height: 9px;
  left: 6px;
  top: 2px;
}

/* 调整按钮大小和样式 */
.action-button {
  padding: 4px 8px !important;
  font-size: 13px !important;
  height: 28px !important;
}

.action-button .el-icon {
  font-size: 13px !important;
  margin-right: 3px !important;
}

/* 调整状态样式 */
.goal-status {
  font-size: 14px;
  margin-left: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.goal-status .el-icon {
  font-size: 14px;
}
</style> 