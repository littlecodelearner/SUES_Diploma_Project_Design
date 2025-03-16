<template>
  <div class="health-goal-batch-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      class="goal-form"
    >
      <!-- 目标列表 -->
      <div v-for="(goal, index) in formData.goals" :key="index" class="goal-item">
        <div class="goal-item-header">
          <h3>目标 #{{ index + 1 }}</h3>
          <el-button
            v-if="formData.goals.length > 1"
            type="danger"
            :icon="Delete"
            circle
            @click="removeGoal(index)"
          />
        </div>

        <el-form-item
          :label="'目标计划'"
          :prop="'goals.' + index + '.targetPlan'"
          :rules="rules.targetPlan"
        >
          <el-input
            v-model="goal.targetPlan"
            type="textarea"
            :rows="3"
            placeholder="请输入您的健康目标计划"
          />
        </el-form-item>

        <el-form-item
          :label="'目标日期'"
          :prop="'goals.' + index + '.targetDate'"
          :rules="rules.targetDate"
        >
          <div class="date-picker-container">
            <div class="date-picker-header">
              <span class="date-picker-title">选择目标达成日期</span>
              <el-button
                type="primary"
                link
                :icon="Calendar"
                class="today-button"
                @click="setTodayDate(index)"
              >
                选择今天
              </el-button>
            </div>
            <div class="date-picker-tip">
              <el-icon><InfoFilled /></el-icon>
              <span>请选择一个未来的日期作为目标达成日期，这将帮助您更好地规划健康目标</span>
            </div>
            <el-date-picker
              v-model="goal.targetDate"
              type="date"
              placeholder="选择目标达成日期"
              :disabled-date="disabledDate"
              value-format="YYYY-MM-DD"
              class="date-picker"
            />
          </div>
        </el-form-item>

        <el-form-item
          :label="'关联运动'"
          :prop="'goals.' + index + '.exerciseTypeIdList'"
          :rules="rules.exerciseTypeIdList"
        >
          <div class="exercise-select-container">
            <div class="exercise-select-tip">
              <el-icon><InfoFilled /></el-icon>
              <span>选择与目标相关的运动类型，这将帮助系统更好地追踪您的目标完成情况</span>
            </div>
            <exercise-type-select
              v-model="goal.exerciseTypeIdList"
              placeholder="请输入运动类型名称搜索"
            />
          </div>
        </el-form-item>

        <!-- 编辑模式下的状态选择 -->
        <template v-if="editMode">
          <el-form-item
            :label="'目标状态'"
            :prop="'goals.' + index + '.isFinished'"
          >
            <el-radio-group v-model="goal.isFinished">
              <el-radio label="0">进行中</el-radio>
              <el-radio label="1">已完成</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item
            :label="'是否放弃'"
            :prop="'goals.' + index + '.isAbandoned'"
          >
            <el-radio-group v-model="goal.isAbandoned">
              <el-radio label="0">继续坚持</el-radio>
              <el-radio label="1">放弃目标</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>
      </div>

      <!-- 添加目标按钮 -->
      <div v-if="!editMode" class="form-actions">
        <el-button
          type="primary"
          :icon="Plus"
          @click="addGoal"
        >
          添加目标
        </el-button>
      </div>

      <!-- 表单操作按钮 -->
      <div class="form-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          {{ editMode ? '确认修改' : '确认添加' }}
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { createHealthGoalInBatch, updateHealthGoalInBatch } from '@/api/healthGoal'
import { Plus, Delete, Calendar, InfoFilled } from '@element-plus/icons-vue'
import ExerciseTypeSelect from '@/components/common/ExerciseTypeSelect.vue'

const props = defineProps({
  userId: {
    type: Number,
    required: true
  },
  editMode: {
    type: Boolean,
    default: false
  },
  initialData: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['success', 'cancel'])

const formRef = ref(null)
const loading = ref(false)

// 表单数据
const formData = ref({
  goals: [
    {
      targetPlan: '',
      targetDate: '',
      exerciseTypeIdList: [],
      isFinished: '0',
      isAbandoned: '0'
    }
  ]
})

// 表单验证规则
const rules = {
  targetPlan: [
    { required: true, message: '请输入目标计划', trigger: 'blur' },
    { min: 5, max: 500, message: '目标计划长度在 5 到 500 个字符', trigger: 'blur' }
  ],
  targetDate: [
    { required: true, message: '请选择目标日期', trigger: 'change' }
  ],
  exerciseTypeIdList: [
    { required: true, message: '请选择关联运动', trigger: 'change' }
  ]
}

// 设置今天日期
const setTodayDate = (index) => {
  const today = new Date()
  const formattedDate = today.toISOString().split('T')[0]
  formData.value.goals[index].targetDate = formattedDate
}

// 添加目标
const addGoal = () => {
  formData.value.goals.push({
    targetPlan: '',
    targetDate: '',
    exerciseTypeIdList: [],
    isFinished: '0',
    isAbandoned: '0'
  })
}

// 移除目标
const removeGoal = (index) => {
  formData.value.goals.splice(index, 1)
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 监听 initialData 变化，更新表单数据
onMounted(() => {
  if (props.editMode && props.initialData?.length > 0) {
    formData.value.goals = props.initialData.map(goal => ({
      ...goal,
      isFinished: goal.isFinished || '0',
      isAbandoned: goal.isAbandoned || '0'
    }))
  }
})

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    loading.value = true
    
    const batchData = formData.value.goals.map(goal => ({
      userId: props.userId,
      targetPlan: goal.targetPlan,
      targetDate: goal.targetDate,
      exerciseTypeIdList: goal.exerciseTypeIdList,
      ...(props.editMode ? {
        goalId: goal.goalId,
        isFinished: goal.isFinished,
        isAbandoned: goal.isAbandoned
      } : {})
    }))
    
    const response = props.editMode
      ? await updateHealthGoalInBatch(batchData)
      : await createHealthGoalInBatch(batchData)
    
    if (response.code === 200) {
      ElMessage.success(props.editMode ? '健康目标修改成功' : '健康目标添加成功')
      emit('success')
    } else {
      ElMessage.error(response.message || (props.editMode ? '修改失败' : '添加失败'))
    }
  } catch (error) {
    console.error('提交表单失败:', error)
    ElMessage.error('请检查表单填写是否正确')
  } finally {
    loading.value = false
  }
}

// 取消操作
const handleCancel = () => {
  emit('cancel')
}
</script>

<style scoped>
.health-goal-batch-form {
  padding: 20px;
}

.goal-form {
  max-width: 800px;
  margin: 0 auto;
}

.goal-item {
  background: var(--el-fill-color-light);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.goal-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.goal-item-header h3 {
  margin: 0;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.date-picker-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.date-picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.date-picker-title {
  font-size: 14px;
  color: var(--el-text-color-regular);
}

.date-picker-tip {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  background-color: var(--el-color-info-light-9);
  padding: 4px 8px;
  border-radius: 4px;
}

.date-picker {
  width: 100%;
}

.exercise-select-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.exercise-select-tip {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  background-color: var(--el-color-info-light-9);
  padding: 4px 8px;
  border-radius: 4px;
}

.form-actions {
  display: flex;
  justify-content: center;
  margin: 24px 0;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid var(--el-border-color-lighter);
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input__wrapper),
:deep(.el-textarea__wrapper) {
  box-shadow: 0 0 0 1px var(--el-border-color) inset;
}

:deep(.el-input__wrapper:hover),
:deep(.el-textarea__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--el-border-color-hover) inset;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-textarea__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--el-color-primary) inset;
}

.today-button {
  color: white;
  font-size: 14px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.today-button:hover {
  color: white;
  opacity: 0.8;
}

.today-button .el-icon {
  margin-right: 4px;
  font-size: 14px;
}

/* 编辑模式下的状态选择样式 */
:deep(.el-radio-group) {
  display: flex;
  gap: 24px;
}

:deep(.el-radio) {
  margin-right: 0;
}

:deep(.el-radio__label) {
  font-size: 14px;
  color: var(--el-text-color-regular);
}

:deep(.el-radio.is-checked .el-radio__label) {
  color: var(--el-color-primary);
}
</style> 