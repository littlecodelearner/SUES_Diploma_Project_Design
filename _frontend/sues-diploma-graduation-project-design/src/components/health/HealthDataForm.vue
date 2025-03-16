<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { addHealthData, updateHealthData } from '@/api/healthData'
import { InfoFilled } from '@element-plus/icons-vue'

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
    type: Object,
    default: () => null
  }
})

const emit = defineEmits(['success', 'cancel'])

const formRef = ref(null)
const loading = ref(false)
const activeStep = ref(0)

// 表单数据
const formData = reactive({
  heartRate: null,
  weight: null,
  height: null,
  measurementDate: null
})

// 如果是编辑模式，初始化表单数据
if (props.editMode && props.initialData) {
  formData.heartRate = props.initialData.heartRate
  formData.weight = props.initialData.weight
  formData.height = props.initialData.height
  // 处理日期：确保使用正确的格式
  formData.measurementDate = props.initialData.measurementDate
} else {
  // 新增模式：设置默认日期为今天
  const today = new Date()
  today.setHours(16, 0, 0, 0) // 设置为 UTC 16:00 (对应东八区 00:00)
  formData.measurementDate = today.toISOString()
}

// 计算BMI值
const calculatedBMI = computed(() => {
  if (!formData.weight || !formData.height) return null
  
  // BMI = 体重(kg) / (身高(m) * 身高(m))
  const height = formData.height / 100 // 转换为米
  return formData.weight / (height * height)
})

// BMI状态判断
const bmiStatus = computed(() => {
  if (!calculatedBMI.value) return { text: '未知', type: 'info' }
  
  if (calculatedBMI.value < 18.5) return { text: '偏瘦', type: 'warning' }
  if (calculatedBMI.value >= 18.5 && calculatedBMI.value <= 23.9) return { text: '健康', type: 'success' }
  if (calculatedBMI.value >= 24.0 && calculatedBMI.value <= 27.9) return { text: '过重', type: 'warning' }
  return { text: '肥胖', type: 'danger' }
})

// 表单校验规则
const rules = {
  heartRate: [
    { required: true, message: '请输入心率', trigger: 'blur' },
    { type: 'number', message: '心率必须是数字' },
    { validator: (rule, value) => {
      if (value && (value < 0 || value > 300 || !Number.isInteger(value))) {
        return Promise.reject('心率必须是0-300之间的整数')
      }
      return Promise.resolve()
    }, trigger: 'blur' }
  ],
  weight: [
    { required: true, message: '请输入体重', trigger: 'blur' },
    { type: 'number', message: '体重必须是数字' },
    { validator: (rule, value) => {
      if (value && (value < 0 || value > 500)) {
        return Promise.reject('体重必须在0-500kg之间')
      }
      return Promise.resolve()
    }, trigger: 'blur' }
  ],
  height: [
    { required: false, message: '请输入身高', trigger: 'blur' },
    { type: 'number', message: '身高必须是数字' },
    { validator: (rule, value) => {
      if (value && (value < 0 || value > 300)) {
        return Promise.reject('身高必须在0-300cm之间')
      }
      return Promise.resolve()
    }, trigger: 'blur' }
  ],
  measurementDate: [
    { required: true, message: '请选择测量日期', trigger: 'change' }
  ]
}

// 添加日期禁用函数
const disableFutureDate = (time) => {
  // 获取当前日期的零点时间
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  // 获取传入时间的零点时间
  const compareDate = new Date(time)
  compareDate.setHours(0, 0, 0, 0)
  
  // 比较日期部分
  return compareDate.getTime() > today.getTime()
}

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    // 准备提交数据
    const submitData = {
      userId: props.userId, // 确保包含用户ID
      heartRate: formData.heartRate,
      weight: formData.weight,
      height: formData.height,
      measurementDate: formData.measurementDate
    }

    if (props.editMode) {
      // 编辑模式：使用批量修改接口
      submitData.healthDataId = props.initialData.healthDataId
      const response = await updateHealthData(submitData)
      
      if (response.code === 200) {
        ElMessage.success('修改成功')
        emit('success')
      } else {
        ElMessage.error(response.message || '修改失败')
      }
    } else {
      // 添加模式：使用单条添加接口
      const response = await addHealthData(submitData)
      
      if (response.code === 200) {
        ElMessage.success('添加成功')
        emit('success')
      } else {
        ElMessage.error(response.message || '添加失败')
      }
    }
  } catch (error) {
    console.error(props.editMode ? '修改健康数据失败:' : '添加健康数据失败:', error)
    ElMessage.error(props.editMode ? '修改失败，请检查数据后重试' : '添加失败，请检查数据后重试')
  } finally {
    loading.value = false
  }
}

// 处理取消
const handleCancel = () => {
  emit('cancel')
}

// 处理下一步
const handleNext = async () => {
  if (activeStep.value < 3) {
    activeStep.value++
  } else {
    await handleSubmit()
  }
}

// 处理上一步
const handlePrev = () => {
  if (activeStep.value > 0) {
    activeStep.value--
  }
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}
</script>

<template>
  <div class="health-data-form">
    <el-steps 
      v-if="!editMode" 
      :active="activeStep" 
      finish-status="success" 
      class="form-steps"
    >
      <el-step title="心率数据" description="请输入当前心率(bpm)" />
      <el-step title="身高数据" description="请输入当前身高(cm)" />
      <el-step title="体重数据" description="请输入当前体重(kg)" />
      <el-step title="确认信息" description="确认并提交数据" />
    </el-steps>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-position="top"
      class="data-form"
      v-loading="loading"
    >
      <!-- 步骤1：心率 -->
      <div v-show="!editMode && activeStep === 0 || editMode" class="step-content">
        <el-form-item label="心率 (bpm)" prop="heartRate">
          <el-input-number
            v-model="formData.heartRate"
            :min="0"
            :max="300"
            :step="1"
            controls-position="right"
            placeholder="请输入心率"
            class="full-width"
          />
          <div class="hint-text">正常心率范围: 60-100 bpm</div>
        </el-form-item>
      </div>

      <!-- 步骤2：身高 -->
      <div v-show="!editMode && activeStep === 1 || editMode" class="step-content">
        <el-form-item label="身高 (cm)" prop="height">
          <el-input-number
            v-model="formData.height"
            :min="0"
            :max="300"
            :step="0.1"
            :precision="1"
            controls-position="right"
            placeholder="请输入身高"
            class="full-width"
          />
        </el-form-item>
      </div>

      <!-- 步骤3：体重 -->
      <div v-show="!editMode && activeStep === 2 || editMode" class="step-content">
        <el-form-item label="体重 (kg)" prop="weight">
          <el-input-number
            v-model="formData.weight"
            :min="0"
            :max="500"
            :precision="2"
            :step="0.1"
            controls-position="right"
            placeholder="请输入体重"
            class="full-width"
          />
        </el-form-item>
      </div>

      <!-- 步骤4：确认信息 -->
      <div v-show="!editMode && activeStep === 3 || editMode" class="step-content">
        <el-form-item label="测量日期（只能选择今天以及今天之前的日期）" prop="measurementDate">
          <el-date-picker
            v-model="formData.measurementDate"
            type="date"
            placeholder="选择测量日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DDT16:00:00.000Z"
            :shortcuts="[
              {
                text: '今天',
                value: (() => {
                  const today = new Date()
                  today.setHours(16, 0, 0, 0)
                  return today
                })()
              }
            ]"
            :disabled-date="disableFutureDate"
            class="date-picker"
            clearable
          >
            <template #default="cell">
              <div class="el-date-picker-cell">
                {{ cell.text }}
                <el-tooltip
                  content="系统将记录选择日期的00:00时刻"
                  placement="top"
                  :show-after="500"
                >
                  <el-icon class="info-icon"><InfoFilled /></el-icon>
                </el-tooltip>
              </div>
            </template>
          </el-date-picker>
        </el-form-item>

        <!-- BMI 计算结果 -->
        <div v-if="calculatedBMI" class="bmi-result">
          <div class="bmi-title">BMI 计算结果</div>
          <div class="bmi-value">
            {{ calculatedBMI.toFixed(1) }}
            <el-tag size="small" :type="bmiStatus.type">
              {{ bmiStatus.text }}
            </el-tag>
          </div>
          <div class="bmi-hint">
            BMI参考范围: 偏瘦 < 18.5 | 健康 18.5-23.9 | 过重 24.0-27.9 | 肥胖 ≥ 28.0
          </div>
        </div>

        <div v-if="!editMode" class="data-summary">
          <h4>数据确认</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="心率">
              {{ formData.heartRate }} bpm
            </el-descriptions-item>
            <el-descriptions-item label="身高">
              {{ formData.height }} cm
            </el-descriptions-item>
            <el-descriptions-item label="体重">
              {{ formData.weight }} kg
            </el-descriptions-item>
            <el-descriptions-item label="测量时间">
              {{ formData.measurementDate }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>

      <!-- 按钮区域 -->
      <div class="form-actions">
        <el-button @click="handleCancel">取消</el-button>
        <template v-if="!editMode">
          <el-button 
            v-if="activeStep > 0" 
            @click="handlePrev"
          >
            上一步
          </el-button>
          <el-button 
            type="primary" 
            @click="handleNext"
            :loading="loading"
          >
            {{ activeStep === 3 ? '提交' : '下一步' }}
          </el-button>
        </template>
        <el-button 
          v-else 
          type="primary" 
          @click="handleSubmit"
          :loading="loading"
        >
          保存修改
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<style scoped>
.health-data-form {
  padding: 20px;
}

.form-steps {
  margin-bottom: 40px;
}

.step-content {
  margin-bottom: 20px;
}

.full-width {
  width: 100%;
}

.hint-text {
  margin-top: 5px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.data-summary {
  margin-top: 20px;
  margin-bottom: 20px;
}

.data-summary h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: var(--el-color-primary);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}

.bmi-result {
  background-color: var(--el-color-primary-light-9);
  border-radius: 8px;
  padding: 15px;
  margin: 20px 0;
  text-align: center;
}

.bmi-title {
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--el-color-primary);
}

.bmi-value {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.bmi-hint {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 8px;
}

.info-icon {
  font-size: 14px;
  color: var(--el-color-info);
  margin-left: 4px;
  cursor: help;
}

.el-date-picker-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}
</style> 