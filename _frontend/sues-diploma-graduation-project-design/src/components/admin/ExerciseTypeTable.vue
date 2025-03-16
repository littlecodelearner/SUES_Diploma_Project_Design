<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Edit, Delete, RefreshRight } from '@element-plus/icons-vue'
import { deleteExerciseTypes, updateExerciseType } from '@/api/exerciseType'
import { formatDate } from '@/utils/date'

// 定义props
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
    default: () => ({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })
  },
  theme: {
    type: String,
    default: 'light'
  },
  tableConfig: {
    type: Object,
    default: () => ({
      size: 'default',
      stripe: true,
      border: false
    })
  }
})

// 定义事件
const emit = defineEmits(['page-change', 'size-change', 'refresh'])

// 选中的行
const selectedRows = ref([])

// 编辑对话框
const editDialogVisible = ref(false)
const editingData = reactive({
  id: '',
  name: '',
  caloriesPerHour: 0,
  description: ''
})

// 搜索文本
const searchText = ref('')

// 排序配置
const sortConfig = reactive({
  prop: '',
  order: ''
})

// 过滤后的数据
const filteredData = computed(() => {
  let result = [...props.data]
  
  // 搜索过滤
  if (searchText.value) {
    const searchLower = searchText.value.toLowerCase()
    result = result.filter(item => 
      item.name.toLowerCase().includes(searchLower) || 
      (item.description && item.description.toLowerCase().includes(searchLower))
    )
  }
  
  // 排序
  if (sortConfig.prop && sortConfig.order) {
    result.sort((a, b) => {
      const aValue = a[sortConfig.prop]
      const bValue = b[sortConfig.prop]
      
      if (typeof aValue === 'number' && typeof bValue === 'number') {
        return sortConfig.order === 'ascending' ? aValue - bValue : bValue - aValue
      } else {
        const aStr = String(aValue || '')
        const bStr = String(bValue || '')
        return sortConfig.order === 'ascending' 
          ? aStr.localeCompare(bStr) 
          : bStr.localeCompare(aStr)
      }
    })
  }
  
  return result
})

// 处理搜索
const handleSearch = () => {
  // 搜索时重置页码
  emit('page-change', 1)
}

// 处理排序变化
const handleSortChange = ({ prop, order }) => {
  sortConfig.prop = prop
  sortConfig.order = order
}

// 处理页码变化
const handleCurrentChange = (page) => {
  emit('page-change', page)
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  emit('size-change', size)
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 处理批量编辑
const handleBatchEdit = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要编辑的运动类型')
    return
  }
  
  if (selectedRows.value.length > 1) {
    ElMessage.warning('一次只能编辑一个运动类型')
    return
  }
  
  const row = selectedRows.value[0]
  editingData.id = row.id
  editingData.name = row.name
  editingData.caloriesPerHour = row.caloriesPerHour
  editingData.description = row.description || ''
  
  editDialogVisible.value = true
}

// 保存编辑
const saveEdit = async () => {
  try {
    await updateExerciseType({
      id: editingData.id,
      name: editingData.name,
      caloriesPerHour: editingData.caloriesPerHour,
      description: editingData.description
    })
    
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    emit('refresh')
  } catch (error) {
    console.error('更新失败:', error)
    ElMessage.error('更新失败')
  }
}

// 关闭编辑对话框
const closeEditDialog = () => {
  editDialogVisible.value = false
}

// 处理批量删除
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要删除的运动类型')
    return
  }
  
  const deleteList = selectedRows.value.map(row => ({
    id: row.id,
    name: row.name
  }))
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${deleteList.length} 个运动类型吗？此操作不可逆`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const ids = deleteList.map(item => item.id)
      await deleteExerciseTypes(ids)
      ElMessage.success('删除成功')
      emit('refresh')
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 处理单行编辑
const handleEdit = (row) => {
  editingData.id = row.id
  editingData.name = row.name
  editingData.caloriesPerHour = row.caloriesPerHour
  editingData.description = row.description || ''
  
  editDialogVisible.value = true
}

// 处理单行删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除运动类型"${row.name}"吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const deleteList = [{
      id: row.id,
      name: row.name
    }]

    const ids = deleteList.map(item => item.id)
    await deleteExerciseTypes(ids)
    ElMessage.success(`成功删除运动类型：${row.name}`)
    emit('refresh')
  } catch (error) {
    if (error === 'cancel') return
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}
</script>

<template>
  <div class="exercise-type-table" :class="{ 'dark': theme === 'dark' }">
    <div class="table-toolbar">
      <div class="search-section">
        <el-input
          v-model="searchText"
          placeholder="搜索运动类型..."
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
      
      <div class="action-buttons">
        <el-button type="primary" @click="handleBatchEdit" :disabled="selectedRows.length !== 1">
          <el-icon><Edit /></el-icon>编辑
        </el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">
          <el-icon><Delete /></el-icon>删除
        </el-button>
        <el-button @click="$emit('refresh')">
          <el-icon><RefreshRight /></el-icon>刷新
        </el-button>
      </div>
    </div>
    
    <el-table
      v-loading="loading"
      :data="filteredData"
      :stripe="tableConfig.stripe"
      :border="tableConfig.border"
      :size="tableConfig.size"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
      class="user-table"
    >
      <el-table-column type="selection" width="55" />
      
      <el-table-column prop="id" label="ID" width="80" sortable />
      
      <el-table-column prop="name" label="运动类型名称" sortable>
        <template #default="{ row }">
          <span class="name-cell">{{ row.name }}</span>
        </template>
      </el-table-column>
      
      <el-table-column prop="caloriesPerHour" label="消耗卡路里(每小时)" sortable>
        <template #default="{ row }">
          <span class="calories-cell">{{ row.caloriesPerHour }}</span>
        </template>
      </el-table-column>
      
      <el-table-column prop="description" label="描述">
        <template #default="{ row }">
          <span class="description-cell">{{ row.description || '无' }}</span>
        </template>
      </el-table-column>
      
      <el-table-column prop="createdAt" label="创建时间" sortable>
        <template #default="{ row }">
          <span class="time-cell">{{ formatDate(row.createdAt) }}</span>
        </template>
      </el-table-column>
      
      <el-table-column prop="updatedAt" label="更新时间" sortable>
        <template #default="{ row }">
          <span class="time-cell">{{ formatDate(row.updatedAt) }}</span>
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <div class="action-cell">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        background
      />
    </div>
    
    <!-- 编辑对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑运动类型"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form label-width="120px">
        <el-form-item label="运动类型名称" required>
          <el-input v-model="editingData.name" placeholder="请输入运动类型名称" />
        </el-form-item>
        
        <el-form-item label="消耗卡路里" required>
          <el-input-number
            v-model="editingData.caloriesPerHour"
            :min="0"
            :step="10"
            placeholder="每小时消耗卡路里"
          />
        </el-form-item>
        
        <el-form-item label="描述">
          <el-input
            v-model="editingData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="closeEditDialog">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.exercise-type-table {
  --animation-duration: 0.3s;
  --hover-transform: translateY(-2px);
  --hover-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  
  background: #FFFFFF;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all var(--animation-duration) ease;
}

.dark .exercise-type-table {
  background: #2D3748;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
}

.table-toolbar {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  border-bottom: 1px solid #E5E7EB;
}

.dark .table-toolbar {
  border-bottom: 1px solid #374151;
}

.search-section {
  flex: 1;
  max-width: 300px;
}

.search-input {
  transition: all 0.3s ease;
}

.search-input:focus-within {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.table-container {
  padding: 0 16px;
}

.exercise-name {
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.exercise-name:hover {
  color: #2B5BA1;
  transform: translateX(4px);
}

.dark .exercise-name:hover {
  color: #3B82F6;
}

.exercise-name .el-icon {
  color: #38B2AC;
}

.dark .exercise-name .el-icon {
  color: #10B981;
}

.pagination-container {
  padding: 16px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #E5E7EB;
}

.dark .pagination-container {
  border-top: 1px solid #374151;
}

.edit-form {
  max-height: 60vh;
  overflow-y: auto;
  padding: 0 20px;
}

.edit-item {
  margin-bottom: 16px;
  position: relative;
}

.edit-input-group {
  display: flex;
  gap: 8px;
  align-items: center;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .table-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-section {
    max-width: none;
  }

  .action-buttons {
    justify-content: flex-end;
  }
}

/* 动画效果 */
.el-table__row {
  transition: all 0.3s ease;
}

.el-table__row:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
}

.delete-button {
  position: relative;
  overflow: hidden;
}

.delete-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.2),
    transparent
  );
  transition: 0.5s;
}

.delete-button:hover::before {
  left: 100%;
}

/* 优化按钮动画效果 */
.el-button {
  transition: all var(--animation-duration) cubic-bezier(0.4, 0, 0.2, 1);
}

.el-button:hover {
  transform: var(--hover-transform);
}

.el-button:active {
  transform: scale(0.95);
}

/* 优化表格行动画 */
.el-table__row {
  transition: all var(--animation-duration) cubic-bezier(0.4, 0, 0.2, 1);
}

.el-table__row:hover {
  transform: var(--hover-transform);
  box-shadow: var(--hover-shadow);
  position: relative;
  z-index: 1;
}

/* 优化移动端体验 */
@media screen and (max-width: 768px) {
  .exercise-type-table {
    --animation-duration: 0.2s;
    --hover-transform: none;
    --hover-shadow: none;
  }

  .table-toolbar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
    padding: 12px;
  }

  .search-section {
    max-width: none;
  }

  .action-buttons {
    justify-content: space-between;
  }

  .el-table {
    font-size: 14px;
  }

  .el-table__row:hover {
    background-color: var(--el-table-row-hover-bg-color);
  }

  .exercise-name {
    font-size: 14px;
  }

  .pagination-container {
    padding: 12px;
  }
}

/* 深色模式优化 */
.dark .el-table {
  --el-table-border-color: #374151;
  --el-table-header-bg-color: #3B82F6;
  --el-table-row-hover-bg-color: #2D3748;
  color: #E5E7EB;
}

.dark .el-table th {
  background-color: #1F2937;
  color: #F0F2F5;
}

.dark .el-table td {
  border-bottom-color: #374151;
}
</style> 