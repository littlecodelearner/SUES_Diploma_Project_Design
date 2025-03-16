<template>
  <el-dialog
    v-model="dialogVisible"
    title="编辑健康目标"
    width="600px"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      class="goal-form"
    >
      <el-form-item label="目标计划" prop="targetPlan">
        <el-input
          v-model="formData.targetPlan"
          type="textarea"
          :rows="4"
          placeholder="请描述您的健康目标计划"
        />
      </el-form-item>

      <el-form-item label="目标日期" prop="targetDate">
        <el-date-picker
          v-model="formData.targetDate"
          type="datetime"
          placeholder="选择目标达成日期"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DDTHH:mm:ss.SSS"
          :disabled-date="disablePastDates"
        />
      </el-form-item>

      <el-form-item label="运动类型" prop="exerciseTypeIdList">
        <el-select
          v-model="formData.exerciseTypeIdList"
          multiple
          filterable
          placeholder="选择关联的运动类型"
          style="width: 100%"
          clearable
          :loading="loading"
        >
          <el-option
            v-for="type in exerciseTypes"
            :key="type.exerciseTypeId"
            :label="type.exerciseName"
            :value="type.exerciseTypeId"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="完成状态" prop="isFinished">
        <el-switch
          v-model="formData.isFinished"
          :active-value="1"
          :inactive-value="0"
          active-text="已完成"
          inactive-text="进行中"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">
          保存
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { updateHealthGoal } from '@/api/healthGoal'
import { formatDate } from '@/utils/date'

const props = defineProps({
  exerciseTypes: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['success', 'cancel'])

const dialogVisible = ref(false)
const loading = ref(false)
const formRef = ref(null)

// 表单数据
const formData = reactive({
  goalId: null,
  targetPlan: '',
  targetDate: '',
  isFinished: 0,
  exerciseTypeIdList: []
})

// 表单验证规则
const rules = {
  targetPlan: [
    { required: true, message: '请输入目标计划', trigger: 'blur' },
    { max: 1000, message: '目标计划长度不能超过1000个字符', trigger: 'blur' }
  ],
  targetDate: [
    { required: true, message: '请选择目标达成日期', trigger: 'change' }
  ]
}

// 禁用过去的日期
const disablePastDates = (time) => {
  return time.getTime() < Date.now() - 8.64e7 // 禁用今天之前的日期
}

// 打开编辑表单
const open = (goal) => {
  dialogVisible.value = true
  // 初始化表单数据
  formData.goalId = goal.goalId
  formData.targetPlan = goal.targetPlan
  formData.targetDate = goal.targetDate
  formData.isFinished = goal.isFinished
  formData.exerciseTypeIdList = goal.exerciseTypesList?.map(type => type.exerciseTypeId) || []
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    const response = await updateHealthGoal({
      ...formData,
      targetDate: formatDate(formData.targetDate)
    })

    if (response.code === 200) {
      ElMessage.success('更新成功')
      dialogVisible.value = false
      emit('success')
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('更新健康目标失败:', error)
    ElMessage.error('更新失败，请检查表单内容')
  } finally {
    loading.value = false
  }
}

// 取消编辑
const handleCancel = () => {
  dialogVisible.value = false
  emit('cancel')
}

// 暴露方法给父组件
defineExpose({
  open
})
</script>

<style scoped>
.goal-form {
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
}
</style> 