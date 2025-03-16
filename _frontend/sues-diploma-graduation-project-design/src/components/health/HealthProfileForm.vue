<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  // 初始表单数据
  initialData: {
    type: Object,
    default: () => ({
      medicalHistory: '',
      allergyHistory: '',
      exerciseHabits: '',
      healthGoals: ''
    })
  },
  // 是否处于加载状态
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit', 'cancel'])

// 表单数据
const formData = ref({
  medicalHistory: props.initialData.medicalHistory,
  allergyHistory: props.initialData.allergyHistory,
  exerciseHabits: props.initialData.exerciseHabits,
  healthGoals: props.initialData.healthGoals
})

// 表单引用
const formRef = ref(null)

// 表单验证规则
const rules = {
  medicalHistory: [
    { max: 500, message: '疾病史不能超过500个字符', trigger: 'blur' }
  ],
  allergyHistory: [
    { max: 500, message: '过敏史不能超过500个字符', trigger: 'blur' }
  ],
  exerciseHabits: [
    { max: 500, message: '运动习惯不能超过500个字符', trigger: 'blur' }
  ],
  healthGoals: [
    { max: 500, message: '健康目标不能超过500个字符', trigger: 'blur' }
  ]
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    emit('submit', { ...formData.value })
  } catch (error) {
    ElMessage.error('请检查表单填写是否正确')
  }
}

// 处理取消
const handleCancel = () => {
  emit('cancel')
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 暴露方法给父组件
defineExpose({
  resetForm
})
</script>

<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="rules"
    label-width="100px"
    class="health-profile-form"
    v-loading="loading"
  >
    <el-form-item label="疾病史" prop="medicalHistory">
      <el-input
        v-model="formData.medicalHistory"
        type="textarea"
        :rows="4"
        placeholder="请描述您的疾病史（如有）"
      />
    </el-form-item>

    <el-form-item label="过敏史" prop="allergyHistory">
      <el-input
        v-model="formData.allergyHistory"
        type="textarea"
        :rows="4"
        placeholder="请描述您的过敏史（如有）"
      />
    </el-form-item>

    <el-form-item label="运动习惯" prop="exerciseHabits">
      <el-input
        v-model="formData.exerciseHabits"
        type="textarea"
        :rows="4"
        placeholder="请描述您的运动习惯"
      />
    </el-form-item>

    <el-form-item label="健康目标" prop="healthGoals">
      <el-input
        v-model="formData.healthGoals"
        type="textarea"
        :rows="4"
        placeholder="请设定您的健康目标"
      />
    </el-form-item>

    <el-form-item class="form-buttons">
      <el-button type="primary" @click="handleSubmit" :loading="loading">
        保存
      </el-button>
      <el-button @click="handleCancel">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<style scoped>
.health-profile-form {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.form-buttons {
  margin-top: 30px;
  text-align: center;
}

:deep(.el-form-item__label) {
  font-weight: bold;
}

:deep(.el-textarea__inner) {
  font-family: inherit;
}
</style> 