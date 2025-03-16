<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { formatDateForBackend } from '@/utils/date'
import { addHealthDataBatch, updateHealthDataInBatch } from '@/api/healthData'
import { Plus, Delete, DocumentCopy, InfoFilled } from '@element-plus/icons-vue'

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

const formRef = ref([])
const loading = ref(false)

// 初始化一个空记录
const createEmptyRecord = () => ({
  userId: props.userId,
  heartRate: null,
  weight: null,
  height: null,
  measurementDate: new Date()
})

// 表单数据 - 记录列表
const records = ref([createEmptyRecord()])

// 表单校验规则
const rules = {
  heartRate: [
    { type: 'number', message: '心率必须是数字' },
    { validator: (rule, value) => {
      if (value && (value < 0 || value > 300)) {
        return Promise.reject('心率必须是0-300之间的数值')
      }
      return Promise.resolve()
    }, trigger: 'blur' }
  ],
  weight: [
    { type: 'number', message: '体重必须是数字' },
    { validator: (rule, value) => {
      if (value && (value < 0 || value > 500)) {
        return Promise.reject('体重必须在0-500kg之间')
      }
      return Promise.resolve()
    }, trigger: 'blur' }
  ],
  height: [
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

// 计算BMI值
const calculateBMI = (weight, height) => {
  if (!weight || !height) return null
  
  // BMI = 体重(kg) / (身高(m) * 身高(m))
  const heightInMeters = height / 100 // 转换为米
  return (weight / (heightInMeters * heightInMeters)).toFixed(1)
}

// 添加新记录
const addRecord = () => {
  records.value.push(createEmptyRecord())
}

// 复制记录
const duplicateRecord = (index) => {
  const record = { ...records.value[index] }
  
  // 调整测量日期为下一天
  if (record.measurementDate) {
    const newDate = new Date(record.measurementDate)
    newDate.setDate(newDate.getDate() + 1)
    record.measurementDate = newDate
  }
  
  records.value.splice(index + 1, 0, record)
}

// 删除记录
const removeRecord = (index) => {
  if (records.value.length > 1) {
    records.value.splice(index, 1)
  } else {
    ElMessage.warning('至少需要保留一条记录')
  }
}

// 初始化记录列表
onMounted(() => {
  if (props.editMode && props.initialData.length > 0) {
    records.value = props.initialData.map(item => ({
      ...item,
      measurementDate: new Date(item.measurementDate)
    }))
  }
})

// 批量提交健康数据
const handleSubmit = async () => {
  if (!formRef.value || !records.value.length) {
    ElMessage.warning('请至少添加一条记录')
    return
  }

  try {
    // 验证所有表单
    await Promise.all(formRef.value.map(form => form.validate()))
    
    // 准备提交数据
    const submitData = records.value.map(record => ({
      ...record,
      // 确保日期格式正确 (UTC 16:00 对应东八区 00:00)
      measurementDate: record.measurementDate
    }))

    // 根据模式选择接口
    const response = props.editMode
      ? await updateHealthDataInBatch(submitData)
      : await addHealthDataBatch(submitData)
    
    if (response.code === 200) {
      ElMessage.success(props.editMode ? '健康数据修改成功' : '健康数据添加成功')
      emit('success')
      // 重置表单
      resetForm()
    } else {
      ElMessage.error(response.message || (props.editMode ? '修改失败，请重试' : '添加失败，请重试'))
    }
  } catch (error) {
    console.error(props.editMode ? '修改健康数据时出错:' : '提交健康数据时出错:', error)
    ElMessage.error(props.editMode ? '表单验证失败，请检查输入' : '表单验证失败，请检查输入')
  }
}

// 处理取消
const handleCancel = () => {
  emit('cancel')
}

// 重置表单
const resetForm = () => {
  records.value = [createEmptyRecord()]
  formRef.value = []
}
</script>

<template>
  <div class="health-data-batch-form">
    <div class="batch-form-header">
      <h3>{{ editMode ? '批量修改健康数据' : '批量添加健康数据' }}</h3>
      <p class="hint-text">
        {{ editMode ? '一次性修改多条健康数据记录，提高操作效率' : '一次性添加多条健康数据记录，方便记录历史数据' }}
      </p>
      <el-alert type="info" :closable="false" show-icon class="info-alert">
        {{ editMode ? '修改完成后，更新的数据将立即显示在健康数据记录中' : '填写表单并提交，批量添加的数据将立即显示在健康数据记录中' }}
      </el-alert>
    </div>
    
    <div class="records-container" v-loading="loading">
      <div v-for="(record, index) in records" :key="record.healthDataId || index" class="record-item">
        <div class="record-header">
          <h4>记录 #{{ index + 1 }}</h4>
          <div class="record-actions">
            <template v-if="!editMode">
              <el-button 
                type="primary" 
                circle 
                size="small" 
                :icon="DocumentCopy"
                @click="duplicateRecord(index)"
                title="复制记录"
              />
            </template>
            <el-button 
              type="danger" 
              circle 
              size="small" 
              :icon="Delete"
              @click="removeRecord(index)"
              title="删除记录"
              :disabled="editMode || records.length === 1"
            />
          </div>
        </div>
        
        <el-form
          :ref="el => formRef ? (formRef[index] = el) : null"
          :model="record"
          :rules="rules"
          label-position="top"
          class="record-form"
        >
          <div class="form-row">
            <el-form-item 
              label="测量日期" 
              prop="measurementDate"
            >
              <el-date-picker
                v-model="record.measurementDate"
                type="date"
                placeholder="选择测量日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DDT16:00:00.000Z"
                :disabled-date="(time) => time > new Date()"
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
          </div>
          
          <div class="form-row">
            <el-form-item label="心率 (bpm)" prop="heartRate">
              <el-input-number
                v-model="record.heartRate"
                :min="0"
                :max="300"
                :step="1"
                controls-position="right"
                placeholder="请输入心率"
                class="number-input"
              />
            </el-form-item>
            
            <el-form-item label="身高 (cm)" prop="height">
              <el-input-number
                v-model="record.height"
                :min="0"
                :max="300"
                :step="0.1"
                :precision="1"
                controls-position="right"
                placeholder="请输入身高"
                class="number-input"
              />
            </el-form-item>
            
            <el-form-item label="体重 (kg)" prop="weight">
              <el-input-number
                v-model="record.weight"
                :min="0"
                :max="500"
                :precision="1"
                :step="0.1"
                controls-position="right"
                placeholder="请输入体重"
                class="number-input"
              />
            </el-form-item>
          </div>
          
          <div v-if="record.weight && record.height" class="bmi-display">
            <span class="bmi-label">计算BMI:</span>
            <span class="bmi-value">{{ calculateBMI(record.weight, record.height) }}</span>
          </div>
        </el-form>
        
        <el-divider v-if="index < records.length - 1" />
      </div>
    </div>
    
    <div v-if="!editMode" class="add-record-btn">
      <el-button 
        type="dashed" 
        :icon="Plus" 
        @click="addRecord"
        class="add-btn"
      >
        添加记录
      </el-button>
    </div>
    
    <div class="form-actions">
      <el-button @click="handleCancel">取消</el-button>
      <el-button 
        type="primary" 
        @click="handleSubmit"
        :loading="loading"
      >
        {{ editMode ? '保存修改' : '提交所有记录' }}
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.health-data-batch-form {
  padding: 20px;
  max-height: none;
  overflow-y: auto;
}

.batch-form-header {
  margin-bottom: 20px;
  text-align: center;
}

.batch-form-header h3 {
  margin: 0 0 8px;
  color: var(--el-color-primary);
  font-size: 20px;
}

.hint-text {
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin: 0;
  margin-bottom: 12px;
}

.info-alert {
  margin-bottom: 10px;
}

.info-alert :deep(.el-alert__content) {
  padding: 0 8px;
}

.records-container {
  margin-bottom: 20px;
}

.record-item {
  margin-bottom: 15px;
  padding: 15px;
  border-radius: 8px;
  background-color: var(--el-color-primary-light-9);
  transition: background-color 0.3s ease;
}

.record-item:hover {
  background-color: var(--el-color-primary-light-8);
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.record-header h4 {
  margin: 0;
  color: var(--el-color-primary);
}

.record-actions {
  display: flex;
  gap: 10px;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 10px;
}

.date-item {
  width: 100%;
}

.date-picker {
  width: 100%;
}

.number-input {
  width: 100%;
}

.el-form-item {
  flex: 1;
  min-width: 200px;
}

.bmi-display {
  background-color: var(--el-color-info-light-9);
  padding: 8px 12px;
  border-radius: 4px;
  margin-top: 10px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.bmi-label {
  font-weight: 500;
  color: var(--el-text-color-regular);
}

.bmi-value {
  font-weight: 600;
  color: var(--el-color-primary);
}

.add-record-btn {
  text-align: center;
  margin: 20px 0;
}

.add-btn {
  width: 200px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

@media (max-height: 800px) {
  .health-data-batch-form {
    padding: 15px;
  }
  
  .batch-form-header {
    margin-bottom: 15px;
  }
  
  .record-item {
    padding: 12px;
    margin-bottom: 12px;
  }
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
  }
  
  .el-form-item {
    width: 100%;
  }
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