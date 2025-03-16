<script setup>
import {ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Delete, Edit} from '@element-plus/icons-vue'
import {formatDate} from '@/utils/date'
import {deleteDietRecordInBulk} from '@/api/diet'

const props = defineProps({
  loading: {
    type: Boolean,
    default: false
  },
  data: {
    type: Array,
    default: () => []
  },
  pagination: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['page-change', 'size-change', 'edit', 'refresh'])

// 多选相关
const selectedRows = ref([])

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要删除的记录')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 条饮食记录吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        draggable: true,
        icon: Delete
      }
    )

    const response = await deleteDietRecordInBulk({
      dietIdList: selectedRows.value.map(row => row.dietId)
    })

    if (response.code === 200) {
      ElMessage.success('删除成功')
      selectedRows.value = []
      emit('refresh') // 刷新列表
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

// 餐食类型映射
const mealTypeMap = {
  'BREAKFAST': '早餐',
  'LUNCH': '午餐',
  'DINNER': '晚餐',
  'SNACK': '加餐'
}

// 处理分页变化
const handlePageChange = (page) => {
  emit('page-change', page)
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  emit('size-change', size)
}

// 处理编辑
const handleEdit = (row) => {
  emit('edit', row)
}

// 格式化餐食类型
const formatMealType = (type) => {
  return mealTypeMap[type] || type
}
</script>

<template>
  <div class="diet-record-table">
    <!-- 批量操作工具栏 -->
    <div class="table-toolbar" v-if="data.length">
      <el-button
        type="danger"
        :disabled="!selectedRows.length"
        @click="handleBatchDelete"
        :icon="Delete"
      >
        批量删除
      </el-button>
      <span class="selection-info" v-if="selectedRows.length">
        已选择 {{ selectedRows.length }} 项
      </span>
    </div>

    <el-table
      :data="data"
      :loading="loading"
      style="width: 100%"
      border
      @selection-change="handleSelectionChange"
    >
      <!-- 添加多选列 -->
      <el-table-column
        type="selection"
        width="55"
        align="center"
      />

      <el-table-column
        prop="foodName"
        label="食物名称"
        min-width="120"
      />
      
      <el-table-column
        prop="mealType"
        label="餐食类型"
        width="100"
      >
        <template #default="{ row }">
          {{ formatMealType(row.mealType) }}
        </template>
      </el-table-column>

      <el-table-column
        prop="calories"
        label="卡路里"
        width="100"
        align="right"
      >
        <template #default="{ row }">
          {{ row.calories }} kcal
        </template>
      </el-table-column>

      <el-table-column
        label="营养成分"
        align="center"
        width="280"
      >
        <template #default="{ row }">
          <el-tooltip
            effect="dark"
            placement="top"
          >
            <template #content>
              <div class="nutrition-tooltip">
                <p>蛋白质: {{ row.protein }}g</p>
                <p>脂肪: {{ row.fat }}g</p>
                <p>碳水: {{ row.carbohydrates }}g</p>
                <p>水分: {{ row.water }}ml</p>
              </div>
            </template>
            <div class="nutrition-info">
              <span>蛋白质 {{ row.protein }}g</span>
              <span>脂肪 {{ row.fat }}g</span>
              <span>碳水 {{ row.carbohydrates }}g</span>
            </div>
          </el-tooltip>
        </template>
      </el-table-column>

      <el-table-column
        prop="recordDate"
        label="记录时间"
        width="180"
        sortable
      >
        <template #default="{ row }">
          {{ formatDate(row.recordDate) }}
        </template>
      </el-table-column>

      <el-table-column
        label="操作"
        width="120"
        fixed="right"
      >
        <template #default="{ row }">
          <el-button
            type="primary"
            :icon="Edit"
            circle
            @click="handleEdit(row)"
          />
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页器 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<style scoped>
.diet-record-table {
  margin: 16px 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.nutrition-info {
  display: flex;
  gap: 8px;
  font-size: 0.9em;
  color: var(--el-text-color-secondary);
}

.nutrition-tooltip {
  text-align: left;
  line-height: 1.5;
}

.nutrition-tooltip p {
  margin: 4px 0;
  white-space: nowrap;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .nutrition-info {
    flex-direction: column;
    gap: 4px;
    font-size: 0.8em;
  }
}

/* 添加工具栏样式 */
.table-toolbar {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.selection-info {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .table-toolbar {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }
  
  .selection-info {
    text-align: center;
  }
}
</style> 