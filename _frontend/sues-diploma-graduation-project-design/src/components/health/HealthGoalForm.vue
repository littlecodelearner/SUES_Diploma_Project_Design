<template>
  <div class="health-goal-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      class="goal-form"
    >
      <el-form-item
        label="目标计划"
        prop="targetPlan"
      >
        <el-input
          v-model="formData.targetPlan"
          type="textarea"
          :rows="4"
          placeholder="请描述您的健康目标计划，例如：'三个月内减重5公斤，同时提升心肺功能'"
        />
      </el-form-item>

      <el-form-item
        label="目标日期"
        prop="targetDate"
      >
        <el-date-picker
          v-model="formData.targetDate"
          type="datetime"
          placeholder="选择目标达成日期"
          :disabled-date="disablePastDates"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DDTHH:mm:ss.SSS"
        />
      </el-form-item>

      <el-form-item
        label="运动类型"
        prop="exerciseTypeIdList"
      >
        <el-select
          v-model="formData.exerciseTypeIdList"
          multiple
          filterable
          placeholder="选择关联的运动类型（可选）"
          style="width: 100%"
          clearable
          :loading="loading"
        >
          <el-option
            v-for="type in exerciseTypes"
            :key="type.exerciseTypeId"
            :label="type.exerciseName"
            :value="type.exerciseTypeId"
          >
            <span style="float: left">{{ type.exerciseName }}</span>
          </el-option>
        </el-select>
        <div class="form-tip">选择与您目标相关的运动类型，这将帮助我们为您推荐合适的运动计划（可选）</div>
      </el-form-item>

      <el-form-item>
        <div class="form-buttons">
          <el-button @click="$emit('cancel')">取消</el-button>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleSubmit"
          >
            创建目标
          </el-button>
        </div>
      </el-form-item>
    </el-form>

    <!-- 目标设置指南 -->
    <div class="goal-guide">
      <h4>设置健康目标的小贴士：</h4>
      <ul>
        <li>设定具体且可衡量的目标</li>
        <li>选择合理的时间范围</li>
        <li>将大目标分解为小目标</li>
        <li>确保目标具有挑战性但可实现</li>
        <li>选择适合自己的运动类型</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { createHealthGoal } from '@/api/healthGoal'
import { getExerciseTypeList } from '@/api/exercise'

const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['success', 'cancel'])

// 表单引用
const formRef = ref(null)

// 加载状态
const loading = ref(false)

// 运动类型列表
const exerciseTypes = ref([])

// 表单数据
const formData = reactive({
  userId: props.userId,
  targetPlan: '',
  targetDate: '',
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

// 获取运动类型列表
const fetchExerciseTypes = async () => {
  try {
    loading.value = true
    const response = await getExerciseTypeList()
    if (response.code === 200) {
      exerciseTypes.value = response.data || []
    } else {
      ElMessage.error(response.message || '获取运动类型列表失败')
    }
  } catch (error) {
    console.error('获取运动类型列表失败:', error)
    ElMessage.error('获取运动类型列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 禁用过去的日期
const disablePastDates = (time) => {
  return time.getTime() < Date.now() - 8.64e7 // 禁用今天之前的日期
}

// 格式化日期为后端支持的格式
const formatDate = (date) => {
  if (!date) return null
  
  // 如果已经是字符串格式，检查是否符合支持的格式
  if (typeof date === 'string') {
    // 检查是否符合任一支持的格式
    const isValidFormat = [
      /^\d{4}-\d{2}-\d{2}$/,  // yyyy-MM-dd
      /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}[+-]\d{2}:\d{2}$/,  // yyyy-MM-dd'T'HH:mm:ss.SSSX
      /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}$/,  // yyyy-MM-dd'T'HH:mm:ss.SSS
      /^[A-Za-z]{3}, \d{2} [A-Za-z]{3} \d{4} \d{2}:\d{2}:\d{2} [A-Z]{3}$/  // EEE, dd MMM yyyy HH:mm:ss zzz
    ].some(regex => regex.test(date))

    if (isValidFormat) return date
  }
  
  // 如果是日期对象或其他格式，转换为 "yyyy-MM-dd'T'HH:mm:ss.SSS" 格式
  const d = new Date(date)
  return d.toISOString().split('Z')[0] // 移除末尾的 'Z' 以符合格式3
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    
    loading.value = true
    // 构造提交数据，格式化日期
    const submitData = {
      ...formData,
      targetDate: formatDate(formData.targetDate)
    }
    
    const response = await createHealthGoal(submitData)
    
    if (response.code === 200) {
      ElMessage.success('健康目标创建成功！')
      emit('success')
    } else {
      ElMessage.error(response.message || '创建失败，请重试')
    }
  } catch (error) {
    console.error('创建健康目标失败:', error)
    ElMessage.error('创建失败，请检查表单内容')
  } finally {
    loading.value = false
  }
}

// 在组件挂载时获取运动类型列表
onMounted(() => {
  fetchExerciseTypes()
})
</script>

<style scoped>
.health-goal-form {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  padding: 20px;
}

.goal-form {
  background: var(--el-bg-color);
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.goal-guide {
  background: var(--el-color-primary-light-9);
  padding: 24px;
  border-radius: 8px;
  height: fit-content;
}

.goal-guide h4 {
  color: var(--el-color-primary);
  margin-top: 0;
  margin-bottom: 16px;
}

.goal-guide ul {
  padding-left: 20px;
  margin: 0;
}

.goal-guide li {
  color: var(--el-text-color-regular);
  margin-bottom: 12px;
  line-height: 1.5;
}

.form-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
  line-height: 1.4;
}

.form-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

@media screen and (max-width: 768px) {
  .health-goal-form {
    grid-template-columns: 1fr;
  }
  
  .goal-form {
    padding: 16px;
  }
  
  .goal-guide {
    padding: 16px;
  }
}
</style> 