<template>
  <div class="exercise-type-management" :class="{ 'dark': isDarkMode }">
    <!-- 顶部操作栏 -->
    <div class="operation-bar">
      <el-row :gutter="20" class="mb-4">
        <el-col :span="16">
          <el-input
            v-model="searchForm.name"
            placeholder="搜索运动类型名称"
            clearable
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="8" class="text-right">
          <el-button-group>
            <el-button type="primary" @click="showAddDialog">
              <el-icon><Plus /></el-icon>添加
            </el-button>
            <el-button 
              type="warning" 
              :disabled="!selectedTypes.length" 
              @click="showBatchEditDialog"
            >
              <el-icon><Edit /></el-icon>批量修改
            </el-button>
            <el-button
              type="danger"
              :disabled="!selectedTypes.length" 
              @click="handleBatchDelete"
            >
              <el-icon><Delete /></el-icon>批量删除
            </el-button>
          </el-button-group>
        </el-col>
      </el-row>
    </div>

    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="stats-cards mb-4">
      <el-col :span="8">
        <el-card shadow="hover" class="stats-card total">
          <template #header>
            <div class="card-header">
              <span>总运动类型数</span>
              <el-tag type="info" effect="plain">Total</el-tag>
            </div>
          </template>
          <div class="card-body">
            <span class="number">{{ pagination.total }}</span>
            <el-icon class="icon"><DataLine /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stats-card selected">
          <template #header>
            <div class="card-header">
              <span>已选择数量</span>
              <el-tag type="warning" effect="plain">Selected</el-tag>
            </div>
          </template>
          <div class="card-body">
            <span class="number">{{ selectedTypes.length }}</span>
            <el-icon class="icon"><Select /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stats-card current">
          <template #header>
            <div class="card-header">
              <span>当前页数量</span>
              <el-tag type="success" effect="plain">Current</el-tag>
            </div>
          </template>
          <div class="card-body">
            <span class="number">{{ exerciseTypeList.length }}</span>
            <el-icon class="icon"><List /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 运动类型列表 -->
    <div v-loading="loading" class="exercise-type-list">
      <el-table
        :data="exerciseTypeList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        :header-cell-style="{
          background: isDarkMode ? '#1a1a2e' : 'var(--el-color-primary-light-9)',
          color: isDarkMode ? '#e5e7eb' : 'var(--el-text-color-primary)',
          borderBottom: isDarkMode ? '2px solid #2d3748' : 'none'
        }"
        :cell-style="{
          background: isDarkMode ? '#1f2937' : 'inherit',
          color: isDarkMode ? '#e5e7eb' : 'inherit',
          borderBottom: isDarkMode ? '1px solid #374151' : 'inherit'
        }"
        :row-class-name="isDarkMode ? 'dark-row' : ''"
        border
        stripe
        highlight-current-row
      >
        <el-table-column type="selection" width="55" fixed="left" />
        <el-table-column prop="exerciseTypeId" label="ID" width="80" fixed="left">
            <template #default="{ row }">
            <el-tag size="small" effect="plain">#{{ row.exerciseTypeId }}</el-tag>
            </template>
          </el-table-column>
        <el-table-column prop="exerciseName" label="运动类型名称" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">
            <div class="exercise-name-cell">
              <span class="name">{{ row.exerciseName }}</span>
              <div class="actions">
                <el-tooltip content="编辑" placement="top">
                  <el-button 
                    type="primary" 
                    link
                    @click.stop="handleEdit(row)"
                  >
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="删除" placement="top">
                  <el-button 
                    type="danger" 
                    link
                    @click.stop="handleDelete(row.exerciseTypeId)"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </div>
            </template>
          </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
            <template #default="{ row }">
              <el-button-group>
                <el-tooltip content="修改" placement="top">
                  <el-button
                    type="primary"
                    link
                    @click.stop="handleEdit(row)"
                >
                  <el-icon><Edit /></el-icon>
                </el-button>
                </el-tooltip>
                <el-tooltip content="删除" placement="top">
                  <el-button
                    type="danger"
                  link
                  @click.stop="handleDelete(row.exerciseTypeId)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
                </el-tooltip>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>

      <!-- 表格底部工具栏 -->
      <div class="table-footer">
        <div class="left">
          <el-tag type="info" effect="plain">
            已选择 {{ selectedTypes.length }} 项
          </el-tag>
          <el-button 
            type="primary" 
            link 
            :disabled="!selectedTypes.length"
            @click="showBatchEditDialog"
          >
            批量修改选中项
          </el-button>
          <el-button 
            type="danger" 
            link
            :disabled="!selectedTypes.length"
            @click="handleBatchDelete"
          >
            批量删除选中项
          </el-button>
        </div>
        <div class="right">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :page-sizes="[15, 30, 45, 50]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 添加运动类型对话框 -->
    <el-dialog
      v-model="addDialog.visible"
      title="添加运动类型"
      width="500px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="addFormRef"
        :model="addDialog.form"
        :rules="addDialog.rules"
        label-width="100px"
      >
        <div 
          v-for="(type, index) in addDialog.form.types" 
          :key="index"
          class="type-input-group"
        >
          <el-form-item
            :label="'运动类型' + (index + 1)"
            :prop="'types.' + index + '.name'"
            :rules="addDialog.rules.name"
          >
            <el-input 
              v-model="type.name"
              placeholder="请输入运动类型名称"
              @keyup.enter="handleEnterPress(index)"
            />
          </el-form-item>
          <el-button
            v-if="index > 0"
            type="danger"
            circle
            plain
            @click="removeType(index)"
          >
            <el-icon><Minus /></el-icon>
          </el-button>
  </div>
        <el-button 
          type="primary" 
          plain 
          class="add-type-btn"
          @click="addType"
        >
          <el-icon><Plus /></el-icon>添加更多运动类型
        </el-button>
      </el-form>
      <template #footer>
        <el-button @click="addDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleAddSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑运动类型对话框 -->
    <el-dialog
      v-model="editDialog.visible"
      title="修改运动类型"
      width="500px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="editFormRef"
        :model="editDialog.form"
        :rules="editDialog.rules"
        label-width="100px"
      >
        <el-form-item label="运动类型ID">
          <el-input v-model="editDialog.form.id" disabled />
        </el-form-item>
        <el-form-item label="运动类型名称" prop="name">
          <el-input 
            v-model="editDialog.form.name"
            placeholder="请输入新的运动类型名称"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量编辑对话框 -->
    <el-dialog
      v-model="batchEditDialog.visible"
      title="批量修改运动类型"
      width="800px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="batchEditFormRef"
        :model="batchEditDialog.form"
        :rules="batchEditDialog.rules"
        label-width="120px"
      >
        <el-table
          :data="batchEditDialog.form.types"
          style="width: 100%"
          size="small"
          border
        >
          <el-table-column label="ID" prop="exerciseTypeId" width="80">
            <template #default="{ row }">
              <el-tag size="small" effect="plain">#{{ row.exerciseTypeId }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="原名称" prop="oldName" min-width="150" />
          <el-table-column label="新名称" min-width="200">
            <template #default="{ row, $index }">
              <el-form-item
                :prop="'types.' + $index + '.newName'"
                :rules="batchEditDialog.rules.name"
                class="no-margin"
              >
                <el-input 
                  v-model="row.newName"
                  placeholder="请输入新的运动类型名称"
                />
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ row }">
              <el-button 
                type="danger" 
                link
                @click="removeBatchEditType(row.exerciseTypeId)"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <el-button @click="batchEditDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchEditSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, Plus, Edit, Delete, Minus, DataLine, 
  Select, List 
} from '@element-plus/icons-vue'
import { 
  listExerciseTypesByPage,
  addExerciseTypes,
  updateExerciseTypes,
  deleteExerciseTypes
} from '@/api/exerciseType'

// 加载状态
const loading = ref(false)

// 运动类型列表
const exerciseTypeList = ref([])

// 选中的运动类型ID列表
const selectedTypes = ref([])

// 搜索表单
const searchForm = reactive({
  name: ''
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 15,
  total: 0
})

// 添加对话框
const addDialog = reactive({
  visible: false,
  form: {
    types: [{ name: '' }]
  },
  rules: {
    name: [
      { required: true, message: '请输入运动类型名称', trigger: 'blur' },
      { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
    ]
  }
})

// 编辑对话框
const editDialog = reactive({
  visible: false,
  form: {
    id: '',
    name: ''
  },
  rules: {
    name: [
      { required: true, message: '请输入运动类型名称', trigger: 'blur' },
      { min: 1, max: 50, message: '请输入1-50个字符的名称', trigger: 'blur' }
    ]
  }
})

// 批量编辑对话框
const batchEditDialog = reactive({
  visible: false,
  form: {
    types: []
  },
  rules: {
    name: [
      { required: true, message: '请输入运动类型名称', trigger: 'blur' },
      { min: 1, max: 50, message: '请输入1-50个字符的名称', trigger: 'blur' }
    ]
  }
})

// 表单引用
const addFormRef = ref(null)
const editFormRef = ref(null)
const batchEditFormRef = ref(null)

// 添加主题相关的状态
const isDarkMode = inject('isDarkMode', ref(false))

// 获取运动类型列表
const fetchExerciseTypes = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      name: searchForm.name || undefined,
      isAsc: true
    }
    
    const res = await listExerciseTypesByPage(params)
    if (res.code === 200 && res.data) {
      exerciseTypeList.value = res.data.dataList
      pagination.total = res.data.total
    } else {
      ElMessage.error(res.message || '获取运动类型列表失败')
    }
  } catch (error) {
    console.error('获取运动类型列表失败:', error)
    ElMessage.error('获取运动类型列表失败')
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  pagination.current = 1
  fetchExerciseTypes()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.current = page
  fetchExerciseTypes()
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  fetchExerciseTypes()
}

// 显示添加对话框
const showAddDialog = () => {
  addDialog.form.types = [{ name: '' }]
  addDialog.visible = true
}

// 添加更多运动类型输入框
const addType = () => {
  if (addDialog.form.types.length >= 50) {
    ElMessage.warning('最多只能添加50个运动类型')
    return
  }
  addDialog.form.types.push({ name: '' })
}

// 移除运动类型输入框
const removeType = (index) => {
  addDialog.form.types.splice(index, 1)
}

// 处理回车键按下
const handleEnterPress = (index) => {
  if (index === addDialog.form.types.length - 1 && 
      addDialog.form.types[index].name.trim() !== '' &&
      addDialog.form.types.length < 50) {
    addType()
  }
}

// 提交添加表单
const handleAddSubmit = async () => {
  if (!addFormRef.value) return
  
  try {
    await addFormRef.value.validate()
    
    const validTypes = addDialog.form.types
      .map(type => type.name.trim())
      .filter(name => name !== '')

    if (validTypes.length === 0) {
      ElMessage.warning('请至少输入一个运动类型')
      return
    }
    
  loading.value = true
    const res = await addExerciseTypes(validTypes)
    
    if (res.code === 200) {
      ElMessage.success('添加成功')
      addDialog.visible = false
      fetchExerciseTypes()
    } else {
      ElMessage.error(res.message || '添加失败')
    }
  } catch (error) {
    console.error('添加运动类型失败:', error)
    ElMessage.error('添加失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理编辑
const handleEdit = (item) => {
  editDialog.form.id = item.exerciseTypeId
  editDialog.form.name = item.exerciseName
  editDialog.visible = true
}

// 提交编辑表单
const handleEditSubmit = async () => {
  if (!editFormRef.value) return
  
  try {
    await editFormRef.value.validate()
    
    const updateList = [{
      exerciseTypeId: editDialog.form.id,
      newExerciseTypeName: editDialog.form.name.trim()
    }]
    
    loading.value = true
    const res = await updateExerciseTypes(updateList)
    
    if (res.code === 200) {
      ElMessage.success('更新成功')
      editDialog.visible = false
  fetchExerciseTypes()
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (error) {
    console.error('更新失败:', error)
    ElMessage.error('更新失败')
  } finally {
    loading.value = false
  }
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedTypes.value = selection.map(item => item.exerciseTypeId)
}

// 处理删除
const handleDelete = async (typeId) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个运动类型吗？此操作不可恢复。',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
    )
    
    loading.value = true
    const res = await deleteExerciseTypes([typeId])
    
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchExerciseTypes()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
    } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  } finally {
    loading.value = false
  }
}

// 处理批量删除
const handleBatchDelete = async () => {
  if (selectedTypes.value.length === 0) {
    ElMessage.warning('请先选择要删除的运动类型')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedTypes.value.length} 个运动类型吗？此操作不可恢复。`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    loading.value = true
    const res = await deleteExerciseTypes(selectedTypes.value)
    
    if (res.code === 200) {
      ElMessage.success('批量删除成功')
      selectedTypes.value = []
      fetchExerciseTypes()
    } else {
      ElMessage.error(res.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  } finally {
    loading.value = false
  }
}

// 显示批量编辑对话框
const showBatchEditDialog = () => {
  if (selectedTypes.value.length === 0) {
    ElMessage.warning('请先选择要修改的运动类型')
    return
  }

  // 准备批量编辑的数据
  const selectedRows = exerciseTypeList.value.filter(
    item => selectedTypes.value.includes(item.exerciseTypeId)
  )
  
  batchEditDialog.form.types = selectedRows.map(item => ({
    exerciseTypeId: item.exerciseTypeId,
    oldName: item.exerciseName,
    newName: item.exerciseName
  }))
  
  batchEditDialog.visible = true
}

// 从批量编辑中移除类型
const removeBatchEditType = (typeId) => {
  const index = batchEditDialog.form.types.findIndex(
    item => item.exerciseTypeId === typeId
  )
  if (index > -1) {
    batchEditDialog.form.types.splice(index, 1)
  }
}

// 提交批量编辑
const handleBatchEditSubmit = async () => {
  if (!batchEditFormRef.value) return
  
  try {
    await batchEditFormRef.value.validate()
    
    const updateList = batchEditDialog.form.types
      .filter(type => type.oldName !== type.newName)
      .map(type => ({
        exerciseTypeId: type.exerciseTypeId,
        newExerciseTypeName: type.newName.trim()
      }))

    if (updateList.length === 0) {
      ElMessage.warning('没有需要更新的运动类型')
      return
    }
    
    loading.value = true
    const res = await updateExerciseTypes(updateList)
    
    if (res.code === 200) {
      ElMessage.success('批量更新成功')
      batchEditDialog.visible = false
      fetchExerciseTypes()
    } else {
      ElMessage.error(res.message || '批量更新失败')
    }
  } catch (error) {
    console.error('批量更新失败:', error)
    ElMessage.error('批量更新失败，请重试')
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(() => {
  fetchExerciseTypes()
})
</script>

<style lang="scss" scoped>
.exercise-type-management {
  padding: 20px;
  min-height: calc(100vh - 60px);
  background-color: var(--el-bg-color);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 20px;

  // 深色模式样式 - 赛博朋克2077和攻壳机动队风格
  &.dark {
    background: linear-gradient(135deg, #070b14 0%, #0f1620 100%);
    color: #e5e7eb;
    position: relative;
    overflow: hidden;

    // 背景网格效果 - 类似攻壳机动队的数据流
    &::before {
      content: "";
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: 
        linear-gradient(90deg, rgba(0, 255, 255, 0.03) 1px, transparent 1px),
        linear-gradient(0deg, rgba(0, 255, 255, 0.03) 1px, transparent 1px);
      background-size: 20px 20px;
      z-index: 0;
      pointer-events: none;
      animation: gridPulse 8s infinite linear;
    }

    // 霓虹数据流 - 赛博朋克2077风格
    &::after {
      content: "";
      position: absolute;
      top: -50%;
      left: -50%;
      width: 200%;
      height: 200%;
      background: 
        radial-gradient(circle, rgba(255, 0, 110, 0.1), transparent 30%),
        radial-gradient(circle at 70% 40%, rgba(0, 255, 255, 0.08), transparent 40%);
      animation: neonFlow 30s linear infinite;
      z-index: 0;
      pointer-events: none;
    }

    .operation-bar {
      position: relative;
      z-index: 1;
      backdrop-filter: blur(5px);
      border-radius: 8px;
      padding: 20px;
      background: rgba(13, 16, 33, 0.7);
      border: 1px solid rgba(0, 255, 255, 0.2);
      box-shadow: 0 0 20px rgba(0, 255, 255, 0.1);
      
      .el-input {
        --el-input-bg-color: rgba(19, 23, 40, 0.8);
        --el-input-border-color: rgba(0, 255, 255, 0.3);
        --el-input-hover-border-color: rgba(0, 255, 255, 0.5);
        --el-input-text-color: #e5e7eb;

        .el-input__wrapper {
          background-color: rgba(19, 23, 40, 0.8);
          box-shadow: 0 0 0 1px rgba(0, 255, 255, 0.3);
          backdrop-filter: blur(5px);
          transition: all 0.3s;

          &:hover {
            box-shadow: 0 0 0 1px rgba(0, 255, 255, 0.5),
                        0 0 10px rgba(0, 255, 255, 0.2);
          }

          &.is-focus {
            box-shadow: 0 0 0 1px rgb(0, 255, 255),
                        0 0 15px rgba(0, 255, 255, 0.3);
          }
        }

        .el-input__prefix {
          color: rgba(0, 255, 255, 0.7);
        }
      }

      .el-button {
        background-color: rgba(19, 23, 40, 0.8);
        border: 1px solid rgba(0, 255, 255, 0.3);
        color: #e5e7eb;
        transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
        position: relative;
        overflow: hidden;

        &::before {
          content: '';
          position: absolute;
          top: -2px;
          left: -2px;
          right: -2px;
          bottom: -2px;
          border-radius: inherit;
          background: linear-gradient(45deg, rgba(0, 255, 255, 0), rgba(0, 255, 255, 0.5), rgba(0, 255, 255, 0));
          z-index: -1;
          animation: borderGlow 3s linear infinite;
          opacity: 0;
          transition: opacity 0.3s;
        }

        &:not(:disabled):hover {
          background-color: rgba(28, 33, 55, 0.9);
          border-color: rgba(0, 255, 255, 0.7);
          color: #00ffff;
          transform: translateY(-2px);
          box-shadow: 0 5px 15px rgba(0, 255, 255, 0.3);

          &::before {
            opacity: 1;
          }

          .el-icon {
            animation: iconPulse 1.5s ease infinite;
          }
        }

        &:disabled {
          background-color: rgba(19, 23, 40, 0.5);
          border-color: rgba(49, 58, 85, 0.5);
          color: #6b7280;
        }

        &.el-button--primary {
          background: linear-gradient(135deg, rgba(0, 206, 209, 0.2), rgba(0, 150, 255, 0.2));
          border-color: rgba(0, 255, 255, 0.5);
          position: relative;
          
          &:not(:disabled):hover {
            background: linear-gradient(135deg, rgba(0, 206, 209, 0.3), rgba(0, 150, 255, 0.3));
            text-shadow: 0 0 8px rgba(0, 255, 255, 0.8);
          }
        }

        &.el-button--danger {
          background: linear-gradient(135deg, rgba(255, 0, 110, 0.2), rgba(255, 0, 55, 0.2));
          border-color: rgba(255, 0, 110, 0.5);
          
          &:not(:disabled):hover {
            background: linear-gradient(135deg, rgba(255, 0, 110, 0.3), rgba(255, 0, 55, 0.3));
            text-shadow: 0 0 8px rgba(255, 0, 110, 0.8);
          }
        }

        &.el-button--warning {
          background: linear-gradient(135deg, rgba(255, 165, 0, 0.2), rgba(255, 140, 0, 0.2));
          border-color: rgba(255, 165, 0, 0.5);
          
          &:not(:disabled):hover {
            background: linear-gradient(135deg, rgba(255, 165, 0, 0.3), rgba(255, 140, 0, 0.3));
            text-shadow: 0 0 8px rgba(255, 165, 0, 0.8);
          }
        }

        .el-icon {
          color: inherit;
          transition: all 0.3s;
        }
      }
    }

    .stats-cards {
      position: relative;
      z-index: 1;
      
      .stats-card {
        background: rgba(13, 16, 33, 0.7);
        border: 1px solid rgba(0, 255, 255, 0.2);
        backdrop-filter: blur(5px);
        box-shadow: 0 5px 20px rgba(0, 0, 0, 0.3);
        transition: all 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);
        border-radius: 8px;
        overflow: hidden;
        position: relative;

        &::after {
          content: "";
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          height: 1px;
          background: linear-gradient(90deg, transparent, rgba(0, 255, 255, 0.5), transparent);
          opacity: 0;
          transition: opacity 0.3s;
        }

        &:hover {
          transform: translateY(-5px) scale(1.02);
          box-shadow: 0 15px 30px rgba(0, 0, 0, 0.4), 
                      0 0 15px rgba(0, 255, 255, 0.2);
          
          &::after {
            opacity: 1;
          }
        }

        .card-header {
          border-bottom: 1px solid rgba(0, 255, 255, 0.1);
          padding-bottom: 12px;
          color: #e5e7eb;

          .el-tag {
            background-color: rgba(19, 23, 40, 0.8);
            border: 1px solid rgba(0, 255, 255, 0.3);
            color: #00ffff;
            font-weight: 500;
            letter-spacing: 0.5px;
          }
        }

        .card-body {
          .number {
            color: #00ffff;
            text-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
            font-weight: 700;
            font-size: 2rem;
            margin-right: 10px;
          }

          .icon {
            position: relative;
            border-radius: 50%;
            padding: 15px;
            font-size: 1.5rem;
          }
        }

        &.total .icon {
          background: rgba(59, 130, 246, 0.1);
          color: #3b82f6;
          box-shadow: 0 0 20px rgba(59, 130, 246, 0.3);
        }

        &.selected .icon {
          background: rgba(245, 158, 11, 0.1);
          color: #f59e0b;
          box-shadow: 0 0 20px rgba(245, 158, 11, 0.3);
        }

        &.current .icon {
          background: rgba(16, 185, 129, 0.1);
          color: #10b981;
          box-shadow: 0 0 20px rgba(16, 185, 129, 0.3);
        }
      }
    }

    .exercise-type-list {
      position: relative;
      z-index: 1;
      backdrop-filter: blur(5px);
      
      .el-table {
        background-color: rgba(13, 16, 33, 0.7) !important;
        border: 1px solid rgba(0, 255, 255, 0.2);
        border-radius: 8px;
        overflow: hidden;
        box-shadow: 0 5px 20px rgba(0, 0, 0, 0.3);

        // 设置表格内的分割线颜色
        &::before,
        &::after {
          background-color: rgba(0, 255, 255, 0.1) !important;
        }

        // 表头样式
        th {
          background-color: rgba(10, 13, 28, 0.95) !important;
          border-bottom: 1px solid rgba(0, 255, 255, 0.2) !important;
          font-weight: 600;
          color: #00ffff !important;
          text-shadow: 0 0 8px rgba(0, 255, 255, 0.3);
          letter-spacing: 0.5px;

          &.is-sortable:hover {
            background-color: rgba(19, 23, 40, 0.9) !important;
            color: #00ffff !important;
            text-shadow: 0 0 12px rgba(0, 255, 255, 0.6);
          }
        }

        // 表格行样式
        tr {
          background-color: rgba(13, 16, 33, 0.7) !important;
          transition: all 0.2s;
          border-bottom: 1px solid rgba(0, 255, 255, 0.05);

          &:hover > td {
            background-color: rgba(28, 33, 55, 0.8) !important;
          }

          &.el-table__row--striped {
            background-color: rgba(10, 13, 28, 0.8) !important;
            
            &:hover > td {
              background-color: rgba(28, 33, 55, 0.8) !important;
            }
          }

          // 选中行样式
          &.current-row > td {
            background-color: rgba(0, 255, 255, 0.1) !important;
            color: #00ffff !important;
            text-shadow: 0 0 8px rgba(0, 255, 255, 0.3);
            position: relative;
            
            &::before {
              content: "";
              position: absolute;
              left: 0;
              top: 0;
              bottom: 0;
              width: 2px;
              background: linear-gradient(to bottom, rgba(0, 255, 255, 0.5), rgba(0, 255, 255, 0.1));
            }
          }
        }

        // 单元格样式
        td {
          background-color: inherit !important;
          border-bottom: 1px solid rgba(0, 255, 255, 0.05) !important;
          color: #e5e7eb !important;
          font-weight: 400;

          .el-tag {
            background-color: rgba(19, 23, 40, 0.8);
            border: 1px solid rgba(0, 255, 255, 0.2);
            color: rgba(0, 255, 255, 0.8);
            font-weight: 500;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
            letter-spacing: 0.5px;
            transition: all 0.3s;
            
            &:hover {
              border-color: rgba(0, 255, 255, 0.5);
              color: #00ffff;
              text-shadow: 0 0 8px rgba(0, 255, 255, 0.3);
              box-shadow: 0 2px 12px rgba(0, 255, 255, 0.2);
            }
          }
        }

        // 表格内的按钮样式
        .el-button {
          transition: all 0.3s;
          background: transparent;
          border: none;
          
          &.el-button--primary {
            color: rgba(0, 255, 255, 0.8) !important;
            
            &:hover {
              color: #00ffff !important;
              text-shadow: 0 0 10px rgba(0, 255, 255, 0.8),
                          0 0 20px rgba(0, 255, 255, 0.4);
              transform: translateY(-2px) scale(1.1);
            }
          }

          &.el-button--danger {
            color: rgba(255, 0, 110, 0.8) !important;
            
            &:hover {
              color: #ff006e !important;
              text-shadow: 0 0 10px rgba(255, 0, 110, 0.8),
                          0 0 20px rgba(255, 0, 110, 0.4);
              transform: translateY(-2px) scale(1.1);
            }
          }
        }

        // 空数据状态样式
        .el-table__empty-block {
          background-color: rgba(13, 16, 33, 0.7);
          
          .el-table__empty-text {
            color: #e5e7eb;
            text-shadow: 0 0 8px rgba(255, 255, 255, 0.2);
          }
        }

        // 固定列样式
        .el-table__fixed,
        .el-table__fixed-right {
          background-color: rgba(13, 16, 33, 0.7);
          border-color: rgba(0, 255, 255, 0.2);

          &::before {
            background-color: rgba(0, 255, 255, 0.1);
          }
        }
      }

      // 表格底部工具栏样式（移到外层）
      .table-footer {
        background-color: rgba(13, 16, 33, 0.7) !important;
        border: 1px solid rgba(0, 255, 255, 0.2);
        border-top: none;
        padding: 16px;
        margin-top: -1px;
        border-radius: 0 0 8px 8px;
        box-shadow: 0 5px 20px rgba(0, 0, 0, 0.3);

        .left {
          display: flex;
          align-items: center;
          gap: 16px;

          .el-tag {
            background-color: rgba(19, 23, 40, 0.8) !important;
            border: 1px solid rgba(0, 255, 255, 0.2) !important;
            color: rgba(0, 255, 255, 0.8) !important;
            font-weight: 500;
            padding: 6px 12px;
            border-radius: 4px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
            letter-spacing: 0.5px;
          }

          .el-button {
            &.el-button--primary {
              background: transparent !important;
              border: none !important;
              color: rgba(0, 255, 255, 0.8) !important;
              font-weight: 500;
              padding: 4px 8px;
              transition: all 0.3s;
              
              &:hover {
                color: #00ffff !important;
                text-shadow: 0 0 10px rgba(0, 255, 255, 0.8),
                            0 0 20px rgba(0, 255, 255, 0.4);
                transform: translateY(-1px);
              }

              &:disabled {
                color: rgba(74, 85, 104, 0.8) !important;
                text-shadow: none;
                cursor: not-allowed;
              }
            }

            &.el-button--danger {
              background: transparent !important;
              border: none !important;
              color: rgba(255, 0, 110, 0.8) !important;
              font-weight: 500;
              padding: 4px 8px;
              transition: all 0.3s;
              
              &:hover {
                color: #ff006e !important;
                text-shadow: 0 0 10px rgba(255, 0, 110, 0.8),
                            0 0 20px rgba(255, 0, 110, 0.4);
                transform: translateY(-1px);
              }

              &:disabled {
                color: rgba(74, 85, 104, 0.8) !important;
                text-shadow: none;
                cursor: not-allowed;
              }
            }
          }
        }

        .right {
          .el-pagination {
            background: transparent !important;
            padding: 0;
            box-shadow: none;

            .el-pagination__total,
            .el-pagination__jump {
              color: #e5e7eb !important;
              text-shadow: 0 0 8px rgba(255, 255, 255, 0.2);
            }

            .el-input__wrapper {
              background-color: rgba(19, 23, 40, 0.8) !important;
              box-shadow: 0 0 0 1px rgba(0, 255, 255, 0.2) !important;
              transition: all 0.3s;

              &:hover {
                box-shadow: 0 0 0 1px rgba(0, 255, 255, 0.4) !important;
              }

              .el-input__inner {
                color: #e5e7eb !important;
              }
            }

            button {
              background-color: rgba(19, 23, 40, 0.8) !important;
              color: rgba(0, 255, 255, 0.8) !important;
              border: 1px solid rgba(0, 255, 255, 0.2);
              transition: all 0.3s;

              &:disabled {
                background-color: rgba(13, 16, 33, 0.5) !important;
                color: rgba(74, 85, 104, 0.8) !important;
                border-color: rgba(74, 85, 104, 0.2);
              }

              &:not(:disabled):hover {
                color: #00ffff !important;
                text-shadow: 0 0 8px rgba(0, 255, 255, 0.5);
                border-color: rgba(0, 255, 255, 0.5);
                transform: translateY(-1px);
              }
            }

            .el-pager li {
              background-color: rgba(19, 23, 40, 0.8) !important;
              color: rgba(0, 255, 255, 0.8) !important;
              border: 1px solid rgba(0, 255, 255, 0.2);
              transition: all 0.3s;

              &.active {
                background-color: rgba(0, 255, 255, 0.2) !important;
                color: #00ffff !important;
                border-color: rgba(0, 255, 255, 0.5);
                text-shadow: 0 0 8px rgba(0, 255, 255, 0.5);
              }

              &:not(.active):hover {
                color: #00ffff !important;
                text-shadow: 0 0 8px rgba(0, 255, 255, 0.5);
                border-color: rgba(0, 255, 255, 0.5);
                transform: translateY(-1px);
              }
            }
          }
        }
      }
    }

    // 对话框样式增强
    :deep(.el-dialog) {
      background: rgba(13, 16, 33, 0.85);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(0, 255, 255, 0.2);
      border-radius: 8px;
      box-shadow: 0 0 30px rgba(0, 0, 0, 0.5),
                  0 0 15px rgba(0, 255, 255, 0.2);
      overflow: hidden;
      
      .el-dialog__header {
        border-bottom: 1px solid rgba(0, 255, 255, 0.1);
        padding: 20px;
        margin: 0;
        
        .el-dialog__title {
          color: #00ffff;
          font-weight: 600;
          letter-spacing: 0.5px;
          text-shadow: 0 0 8px rgba(0, 255, 255, 0.3);
        }
        
        .el-dialog__headerbtn {
          .el-dialog__close {
            color: rgba(0, 255, 255, 0.8);
            
            &:hover {
              color: #ff006e;
              text-shadow: 0 0 8px rgba(255, 0, 110, 0.5);
            }
          }
        }
      }
      
      .el-dialog__body {
        color: #e5e7eb;
        padding: 20px;
      }
      
      .el-dialog__footer {
        border-top: 1px solid rgba(0, 255, 255, 0.1);
        padding: 15px 20px;
      }
      
      .el-form-item__label {
        color: rgba(0, 255, 255, 0.8);
        font-weight: 500;
        letter-spacing: 0.5px;
      }
      
      .el-form-item__error {
        color: #ff006e;
        text-shadow: 0 0 5px rgba(255, 0, 110, 0.3);
      }
      
      .el-input__wrapper {
        background-color: rgba(19, 23, 40, 0.8);
        box-shadow: 0 0 0 1px rgba(0, 255, 255, 0.2);
        
        &:hover {
          box-shadow: 0 0 0 1px rgba(0, 255, 255, 0.4);
        }
        
        &.is-focus {
          box-shadow: 0 0 0 1px rgba(0, 255, 255, 0.6),
                      0 0 10px rgba(0, 255, 255, 0.2);
        }
        
        .el-input__inner {
          color: #e5e7eb;
        }
      }
    }
  }
}

// 新增动画效果
@keyframes neonFlow {
  0% { transform: translate(0, 0) rotate(0deg); }
  50% { transform: translate(5%, 5%) rotate(180deg); }
  100% { transform: translate(0, 0) rotate(360deg); }
}

@keyframes gridPulse {
  0% { opacity: 0.5; }
  50% { opacity: 1; }
  100% { opacity: 0.5; }
}

@keyframes borderGlow {
  0% { background-position: 0% 0%; }
  100% { background-position: 200% 0%; }
}

@keyframes iconPulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); }
}

// 默认亮色主题样式优化
.stats-cards {
  .stats-card {
    transition: all 0.3s ease;
    border-radius: 8px;
    overflow: hidden;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
    }
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 15px;
      margin-bottom: 0;
      border-bottom: 1px solid var(--el-border-color-lighter);
      
      span {
        font-size: 16px;
        font-weight: 600;
      }
    }
    
    .card-body {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px 15px;
      
      .number {
        font-size: 28px;
        font-weight: bold;
      }
      
      .icon {
        font-size: 24px;
        padding: 15px;
        border-radius: 50%;
        transition: all 0.3s ease;
      }
    }
    
    &.total .icon {
      background-color: rgba(59, 130, 246, 0.1);
    }
    
    &.selected .icon {
      background-color: rgba(245, 158, 11, 0.1);
    }
    
    &.current .icon {
      background-color: rgba(16, 185, 129, 0.1);
    }
  }
}

.exercise-type-list {
  margin-bottom: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
  
  .exercise-name-cell {
    display: flex;
    align-items: center;
    justify-content: space-between;
    
    .name {
      font-size: 14px;
      font-weight: 500;
    }
    
    .actions {
      display: flex;
      gap: 8px;
      opacity: 0;
      transition: opacity 0.3s;
    }
    
    &:hover .actions {
      opacity: 1;
    }
  }
  
  :deep(.el-table__row) {
    cursor: pointer;
    transition: all 0.3s;
  }
  
  .table-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    background-color: var(--el-bg-color);
    border-top: 1px solid var(--el-border-color-lighter);
    
    .left {
      display: flex;
      align-items: center;
      gap: 16px;
    }
    
    .right {
      flex-shrink: 0;
    }
  }
}

.operation-bar {
  margin-bottom: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.type-input-group {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  
  .el-form-item {
    flex: 1;
  }
}

.add-type-btn {
  width: 100%;
  margin-top: 10px;
}

:deep(.no-margin) {
  margin-bottom: 0;
}

// 响应式优化
@media screen and (max-width: 768px) {
  .exercise-type-management {
    padding: 15px;
    gap: 15px;
  }
  
  .stats-cards .stats-card .card-body {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
    
    .number {
      font-size: 24px;
    }
  }
  
  .table-footer {
    flex-direction: column;
    gap: 15px;
    
    .left, .right {
      width: 100%;
    }
    
    .left {
      flex-wrap: wrap;
    }
  }
}
</style> 