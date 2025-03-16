<script setup>
import { ref, computed, watch } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import ExerciseTypeSelect from '@/components/common/ExerciseTypeSelect.vue'
import { createExerciseRecords, updateExerciseRecords } from '@/api/exercise'
import { useUserStore } from '@/store/user'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  mode: {
    type: String,
    default: 'add', // 'add' 或 'edit' 或 'batchEdit'
    validator: (value) => ['add', 'edit', 'batchEdit'].includes(value)
  },
  initialData: {
    type: Object,
    default: null
  },
  selectedRecords: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

// 表单数据
const formData = ref({
  records: []
})

// 表单验证规则
const rules = {
  'records.*.exerciseTypeIdList': [
    { required: true, message: '请选择运动类型', trigger: 'change' }
  ],
  'records.*.exerciseDate': [
    { required: true, message: '请选择运动日期', trigger: 'change' }
  ],
  'records.*.duration': [
    { required: true, message: '请输入运动时长', trigger: 'blur' }
  ]
}

// 对话框显示状态
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 对话框标题
const dialogTitle = computed(() => {
  switch (props.mode) {
    case 'edit':
      return '修改运动记录'
    case 'batchEdit':
      return '批量修改运动记录'
    default:
      return '批量添加运动记录'
  }
})

// 添加新记录
const addNewRecord = () => {
  formData.value.records.push({
    userId: userStore.userId,
    exerciseTypeIdList: [],
    duration: 30,
    distance: 0,
    caloriesBurned: 0,
    heartRate: 0,
    exerciseNote: '',
    exerciseDate: new Date().toISOString().replace('Z', '+08:00')
  })
}

// 删除记录
const removeRecord = (index) => {
  formData.value.records.splice(index, 1)
}

// 初始化表单数据
const initFormData = () => {
  formData.value = {
    records: []
  }
  
  if (props.mode === 'edit' && props.initialData) {
    // 单条记录编辑
    formData.value.records = [{
      exerciseRecordId: props.initialData.exerciseRecordId,
      userId: userStore.userId,
      exerciseTypeIdList: props.initialData.exerciseTypesList?.map(type => type.exerciseTypeId) || [],
      duration: props.initialData.duration || 30,
      distance: props.initialData.distance || 0,
      caloriesBurned: props.initialData.caloriesBurned || 0,
      heartRate: props.initialData.heartRate || 0,
      exerciseNote: props.initialData.exerciseNote || '',
      exerciseDate: props.initialData.exerciseDate || new Date().toISOString().replace('Z', '+08:00')
    }]
  } else if (props.mode === 'batchEdit' && Array.isArray(props.initialData)) {
    // 批量记录编辑
    formData.value.records = props.initialData.map(record => ({
      exerciseRecordId: record.exerciseRecordId,
      userId: userStore.userId,
      exerciseTypeIdList: record.exerciseTypesList?.map(type => type.exerciseTypeId) || [],
      duration: record.duration || 30,
      distance: record.distance || 0,
      caloriesBurned: record.caloriesBurned || 0,
      heartRate: record.heartRate || 0,
      exerciseNote: record.exerciseNote || '',
      exerciseDate: record.exerciseDate || new Date().toISOString().replace('Z', '+08:00')
    }))
  } else {
    // 新增记录
    addNewRecord()
  }
}

// 监听对话框显示状态变化
watch(() => dialogVisible.value, (val) => {
  if (val) {
    initFormData()
  }
})

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (formData.value.records.length === 0) {
      ElMessage.warning('请至少添加一条运动记录')
      return
    }
    
    loading.value = true
    
    // 根据模式决定是创建还是更新
    if (props.mode === 'add') {
      const response = await createExerciseRecords(formData.value.records)
      if (response.code === 200) {
        ElMessage.success('运动记录添加成功')
        emit('success')
        dialogVisible.value = false
      } else {
        ElMessage.error(response.message || '添加失败')
      }
    } else {
      // 编辑模式（单条或批量）
      emit('success', formData.value.records)
      dialogVisible.value = false
    }
  } catch (error) {
    console.error('表单提交失败:', error)
    ElMessage.error('提交失败，请检查表单内容')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="800px"
    destroy-on-close
    :close-on-click-modal="false"
    class="exercise-record-form-dialog"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      class="exercise-form"
    >
      <!-- 添加记录按钮（仅在新增模式下显示） -->
      <div v-if="mode === 'add'" class="form-actions">
        <el-button type="primary" :icon="Plus" @click="addNewRecord">
          添加记录
        </el-button>
      </div>

      <!-- 记录列表 -->
      <div v-for="(record, index) in formData.records" :key="index" class="record-item">
        <div class="record-header">
          <h4>{{ mode === 'add' ? `记录 ${index + 1}` : '运动记录' }}</h4>
          <el-button
            v-if="mode === 'add' && formData.records.length > 1"
            type="danger"
            :icon="Delete"
            circle
            @click="removeRecord(index)"
          />
        </div>

        <el-form-item
          :prop="`records.${index}.exerciseTypeIdList`"
          label="运动类型"
        >
          <exercise-type-select
            v-model="record.exerciseTypeIdList"
            multiple
            :placeholder="mode === 'batchEdit' ? '不修改请留空' : '请选择运动类型'"
            :clearable="mode === 'batchEdit'"
          />
        </el-form-item>

        <el-form-item
          :prop="`records.${index}.exerciseDate`"
          label="运动日期"
        >
          <el-date-picker
            v-model="record.exerciseDate"
            type="datetime"
            :placeholder="mode === 'batchEdit' ? '不修改请留空' : '请选择运动日期'"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss.SSSZ"
            :clearable="mode === 'batchEdit'"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item
              :prop="`records.${index}.duration`"
              label="运动时长"
            >
              <el-input-number
                v-model="record.duration"
                :min="0"
                :max="1440"
                :step="5"
                :placeholder="mode === 'batchEdit' ? '不修改请留空' : '请输入运动时长'"
                :clearable="mode === 'batchEdit'"
              >
                <template #append>分钟</template>
              </el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              :prop="`records.${index}.distance`"
              label="运动距离"
            >
              <el-input-number
                v-model="record.distance"
                :min="0"
                :max="1000"
                :step="0.1"
                :precision="2"
                :placeholder="mode === 'batchEdit' ? '不修改请留空' : '请输入运动距离'"
                :clearable="mode === 'batchEdit'"
              >
                <template #append>公里</template>
              </el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item
              :prop="`records.${index}.caloriesBurned`"
              label="消耗热量"
            >
              <el-input-number
                v-model="record.caloriesBurned"
                :min="0"
                :max="10000"
                :step="10"
                :placeholder="mode === 'batchEdit' ? '不修改请留空' : '请输入消耗热量'"
                :clearable="mode === 'batchEdit'"
              >
                <template #append>千卡</template>
              </el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              :prop="`records.${index}.heartRate`"
              label="平均心率"
            >
              <el-input-number
                v-model="record.heartRate"
                :min="0"
                :max="220"
                :step="1"
                :placeholder="mode === 'batchEdit' ? '不修改请留空' : '请输入平均心率'"
                :clearable="mode === 'batchEdit'"
              >
                <template #append>bpm</template>
              </el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item
          :prop="`records.${index}.exerciseNote`"
          label="运动备注"
        >
          <el-input
            v-model="record.exerciseNote"
            type="textarea"
            :rows="3"
            :placeholder="mode === 'batchEdit' ? '不修改请留空' : '请输入运动备注'"
            :clearable="mode === 'batchEdit'"
          />
        </el-form-item>
      </div>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="loading"
          @click="handleSubmit"
        >
          {{ mode === 'add' ? '添加' : '保存' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.exercise-form {
  max-height: calc(80vh - 150px);
  overflow-y: auto;
  padding-right: 16px;
}

.form-actions {
  margin-bottom: 20px;
}

.record-item {
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  background: var(--el-bg-color-page);
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.record-header h4 {
  margin: 0;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

/* 自定义滚动条样式 */
.exercise-form::-webkit-scrollbar {
  width: 6px;
}

.exercise-form::-webkit-scrollbar-thumb {
  background: var(--el-color-primary-light-5);
  border-radius: 3px;
}

.exercise-form::-webkit-scrollbar-track {
  background: transparent;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .el-row {
    margin-left: 0 !important;
    margin-right: 0 !important;
  }

  .el-col {
    padding-left: 0 !important;
    padding-right: 0 !important;
  }
}
</style> 