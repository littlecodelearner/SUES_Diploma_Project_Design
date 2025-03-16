<template>
  <el-dialog
    v-model="dialogVisible"
    title="更新运动记录"
    width="600px"
    :close-on-click-modal="false"
    :lock-scroll="false"
    @closed="handleDialogClosed"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      class="exercise-form"
    >
      <el-form-item label="运动类型" prop="exerciseTypeIdList">
        <el-select
          v-model="formData.exerciseTypeIdList"
          multiple
          filterable
          placeholder="请选择运动类型"
          class="w-full"
        >
          <el-option
            v-for="item in props.exerciseTypes"
            :key="item.exerciseTypeId"
            :label="item.exerciseName"
            :value="item.exerciseTypeId"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="运动日期" prop="exerciseDate">
        <el-date-picker
          v-model="formData.exerciseDate"
          type="datetime"
          placeholder="选择运动日期时间"
          :format="DATE_FORMATS.DISPLAY_DATETIME"
          :value-format="DATE_FORMATS.API_DATETIME"
          class="w-full"
        />
      </el-form-item>

      <el-form-item label="运动时长" prop="duration">
        <el-input-number
          v-model="formData.duration"
          :min="VALIDATION_RULES.EXERCISE_DURATION.MIN"
          :max="VALIDATION_RULES.EXERCISE_DURATION.MAX"
          placeholder="请输入运动时长（分钟）"
          class="w-full"
        />
      </el-form-item>

      <el-form-item label="运动距离" prop="distance">
        <el-input-number
          v-model="formData.distance"
          :min="VALIDATION_RULES.EXERCISE_DISTANCE.MIN"
          :max="VALIDATION_RULES.EXERCISE_DISTANCE.MAX"
          :precision="VALIDATION_RULES.EXERCISE_DISTANCE.PRECISION"
          :step="0.1"
          placeholder="请输入运动距离（公里）"
          class="w-full"
        />
      </el-form-item>

      <el-form-item label="消耗卡路里" prop="caloriesBurned">
        <el-input-number
          v-model="formData.caloriesBurned"
          :min="VALIDATION_RULES.CALORIES.MIN"
          :max="VALIDATION_RULES.CALORIES.MAX"
          :precision="VALIDATION_RULES.CALORIES.PRECISION"
          :step="1"
          placeholder="请输入消耗的卡路里（kcal）"
          class="w-full"
        />
      </el-form-item>

      <el-form-item label="平均心率" prop="heartRate">
        <el-input-number
          v-model="formData.heartRate"
          :min="VALIDATION_RULES.HEART_RATE.MIN"
          :max="VALIDATION_RULES.HEART_RATE.MAX"
          :precision="0"
          :step="1"
          placeholder="请输入平均心率（bpm）"
          class="w-full"
        />
      </el-form-item>

      <el-form-item label="运动备注" prop="exerciseNote">
        <el-input
          v-model="formData.exerciseNote"
          type="textarea"
          :rows="3"
          placeholder="请输入运动备注、随笔或详细内容"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">
          更新
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { updateExerciseRecord } from '@/api/exercise'
import { DATE_FORMATS, VALIDATION_RULES } from '@/config/constants'

const props = defineProps({
  exerciseTypes: {
    type: Array,
    required: true
  },
  recordData: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update-success'])

const dialogVisible = ref(false)
const loading = ref(false)
const formRef = ref(null)

// 表单数据
const formData = reactive({
  exerciseRecordId: null,
  exerciseTypeIdList: [],
  exerciseDate: '',
  duration: null,
  distance: null,
  caloriesBurned: null,
  heartRate: null,
  exerciseNote: ''
})

// 表单验证规则
const rules = {
  exerciseTypeIdList: [
    { required: true, message: '请选择运动类型', trigger: 'change' },
    { type: 'array', min: 1, message: '至少选择一个运动类型', trigger: 'change' }
  ],
  exerciseDate: [
    { required: true, message: '请选择运动日期时间', trigger: 'change' }
  ],
  duration: [
    { required: true, message: '请输入运动时长', trigger: 'blur' }
  ]
}

// 打开对话框时初始化表单数据
const open = (record) => {
  dialogVisible.value = true
  // 确保运动类型ID列表是数组形式
  const exerciseTypeIdList = record.exerciseTypesList?.map(type => type.exerciseTypeId) || []
  
  // 初始化表单数据
  Object.assign(formData, {
    exerciseRecordId: record.exerciseRecordId,
    exerciseTypeIdList: exerciseTypeIdList,
    exerciseDate: record.exerciseDate,
    duration: record.duration,
    distance: record.distance,
    caloriesBurned: record.caloriesBurned,
    heartRate: record.heartRate,
    exerciseNote: record.exerciseNote
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    loading.value = true
    const response = await updateExerciseRecord(formData)
    
    if (response.code === 200) {
      ElMessage.success('运动记录更新成功')
      dialogVisible.value = false
      emit('update-success')
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('更新运动记录失败:', error)
    ElMessage.error('更新失败，请重试')
  } finally {
    loading.value = false
  }
}

// 关闭对话框时重置表单
const handleDialogClosed = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 暴露方法给父组件
defineExpose({
  open
})
</script>

<style scoped>
.exercise-form {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 16px;
}

.exercise-form :deep(.el-form-item__content) {
  width: calc(100% - 100px);
}

.w-full {
  width: 100%;
}

/* 自定义滚动条样式 */
.exercise-form::-webkit-scrollbar {
  width: 6px;
}

.exercise-form::-webkit-scrollbar-thumb {
  background-color: var(--el-border-color-lighter);
  border-radius: 3px;
}

.exercise-form::-webkit-scrollbar-track {
  background-color: transparent;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 确保对话框内容可以滚动但不影响背景 */
:deep(.el-dialog) {
  display: flex;
  flex-direction: column;
  margin: 0 !important;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  max-height: 90vh;
  max-width: 90vw;
}

:deep(.el-dialog__body) {
  flex: 1;
  overflow: auto;
}
</style> 