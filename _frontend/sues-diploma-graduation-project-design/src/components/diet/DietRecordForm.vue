<script setup>
import {onMounted, ref, watch} from 'vue'
import {ElMessage} from 'element-plus'
import {Bowl, Delete, InfoFilled, Location, Plus} from '@element-plus/icons-vue'
import {addDietRecordsInBulk, updateDietRecordsInBulk} from '@/api/diet'
import {listDietFoodsDetailsByPage} from '@/api/food'
import {formatDateWithChineseTimezone} from '@/utils/date'
import FoodList from '@/components/food/FoodList.vue'

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
    type: [Object, Array],
    default: null
  }
})

const emit = defineEmits(['success', 'cancel'])

// 表单数据列表
const formDataList = ref([])

// 在 script setup 中修改日期时间格式
const formatISOString = (date) => {
  if (!date) return formatDateWithChineseTimezone(new Date())
  
  try {
    // 尝试处理传入的日期值
    let dateObj;
    
    // 如果是字符串，尝试解析
    if (typeof date === 'string') {
      // 检查是否包含格式化字符（yyyy, MM等），这表明日期未正确解析
      if (date.includes('yyyy') || date.includes('MM') || date.includes('dd')) {
        console.warn('日期包含未解析的格式化字符:', date)
        return formatDateWithChineseTimezone(new Date()) // 返回当前日期作为后备
      }
      
      // 否则尝试解析日期字符串
      dateObj = new Date(date)
      
      // 检查日期是否有效
      if (isNaN(dateObj.getTime())) {
        console.warn('无效的日期字符串:', date)
        return formatDateWithChineseTimezone(new Date())
      }
    } else if (date instanceof Date) {
      dateObj = date
    } else {
      console.warn('不支持的日期类型:', date)
      return formatDateWithChineseTimezone(new Date())
    }
    
    return formatDateWithChineseTimezone(dateObj)
  } catch (error) {
    console.error('格式化日期时出错:', error)
    return formatDateWithChineseTimezone(new Date())
  }
}

// 修改 initFormData 函数中的日期格式化
const initFormData = () => {
  if (props.editMode && props.initialData) {
    if (Array.isArray(props.initialData)) {
      // 批量编辑模式
      formDataList.value = props.initialData.map(record => ({
        ...record,
        mealTime: formatISOString(record.mealTime)
      }))
    } else {
      // 单条编辑模式
      formDataList.value = [{
        ...props.initialData,
        mealTime: formatISOString(props.initialData.mealTime)
      }]
    }
  } else {
    // 新建模式 - 默认一条记录
    formDataList.value = [{
      userId: props.userId,
      foodsQuantitiesDTOList: [{ foodId: null, quantity: null }],
      mealType: '',
      mealTime: formatISOString(new Date()),
      mealNote: '',
      mealPlace: ''
    }]
  }
}

// 食物选项相关
const allFoods = ref([])
const foodOptions = ref([])
const foodsLoading = ref(false)

// 获取所有食物数据
const fetchAllFoods = async () => {
  try {
    foodsLoading.value = true
    const response = await listDietFoodsDetailsByPage()
    
    if (response.code === 200) {
      allFoods.value = response.data || []
      foodOptions.value = allFoods.value // 初始显示所有食物
    } else {
      ElMessage.error('获取食物数据失败')
    }
  } catch (error) {
    console.error('获取食物数据失败:', error)
    ElMessage.error('获取食物数据失败，请重试')
  } finally {
    foodsLoading.value = false
  }
}

// 本地搜索食物
const searchFoods = (query) => {
  if (!query) {
    foodOptions.value = allFoods.value // 当搜索词为空时显示所有食物
    return
  }
  
  // 在本地进行模糊搜索
  foodOptions.value = allFoods.value.filter(food => 
    food.foodName.toLowerCase().includes(query.toLowerCase()) ||
    food.foodType.toLowerCase().includes(query.toLowerCase())
  )
}

// 在组件挂载时获取所有食物数据
onMounted(() => {
  fetchAllFoods()
})

// 餐食类型选项
const mealTypeOptions = [
  { label: '早餐', value: 'BREAKFAST' },
  { label: '午餐', value: 'LUNCH' },
  { label: '下午茶', value: 'AFTERNOONTEA' },
  { label: '晚餐', value: 'DINNER' },
  { label: '零食', value: 'SNACK' },
  { label: '加餐', value: 'EXTRAS' },
  { label: '夜宵', value: 'NIGHTSNACK' },
  { label: '其他', value: 'OTHER' }
]

const formRef = ref(null)
const loading = ref(false)

// 添加食物项
const addFoodItem = (recordIndex) => {
  formDataList.value[recordIndex].foodsQuantitiesDTOList.push({
    foodId: null,
    quantity: null
  })
}

// 移除食物项
const removeFoodItem = (recordIndex, foodIndex) => {
  formDataList.value[recordIndex].foodsQuantitiesDTOList.splice(foodIndex, 1)
}

// 添加新的饮食记录
const addNewRecord = () => {
  formDataList.value.push({
    userId: props.userId,
    foodsQuantitiesDTOList: [{ foodId: null, quantity: null }],
    mealType: '',
    mealTime: formatISOString(new Date()),
    mealNote: '',
    mealPlace: ''
  })
}

// 移除饮食记录
const removeRecord = (index) => {
  if (formDataList.value.length > 1) {
    formDataList.value.splice(index, 1)
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 检查每条记录是否都至少添加了一种食物
    const hasEmptyFoodsList = formDataList.value.some(record => {
      // 检查是否没有食物列表或食物列表为空
      if (!record.foodsQuantitiesDTOList || record.foodsQuantitiesDTOList.length === 0) {
        return true
      }
      
      // 检查是否有有效的食物项（同时有foodId和quantity）
      const hasValidFood = record.foodsQuantitiesDTOList.some(
        food => food.foodId && food.quantity
      )
      
      return !hasValidFood
    })
    
    if (hasEmptyFoodsList) {
      ElMessage.error('请至少添加一种食物到每条记录的食物清单中')
      return
    }
    
    loading.value = true
    const submitData = formDataList.value.map(record => ({
      ...record,
      userId: props.userId,
      // 确保日期格式正确
      mealTime: formatISOString(record.mealTime),
      // 确保食物列表格式正确
      foodsQuantitiesDTOList: record.foodsQuantitiesDTOList.map(food => ({
        foodId: food.foodId,
        quantity: food.quantity
      }))
    }))

    // 根据模式选择不同的 API
    const response = props.editMode
      ? await updateDietRecordsInBulk(submitData.map(record => {
          // 确保记录有效并包含 dietId
          if (!record || record.dietId === undefined) {
            console.warn('无效的记录或缺少dietId:', record)
            return {
              ...record,
              dietId: record?.dietId || 0 // 提供默认值
            }
          }
          return {
            ...record,
            dietId: record.dietId
          }
        }))
      : await addDietRecordsInBulk(submitData)
    
    if (response.code === 200) {
      ElMessage.success(
        props.editMode
          ? `成功修改 ${submitData.length} 条记录`
          : `成功添加 ${submitData.length} 条记录`
      )
      emit('success')
    } else {
      ElMessage.error(response.message || `${props.editMode ? '修改' : '添加'}失败`)
    }
  } catch (error) {
    console.error(`${props.editMode ? '修改' : '添加'}饮食记录失败:`, error)
    ElMessage.error(`${props.editMode ? '修改' : '添加'}失败，请检查表单并重试`)
  } finally {
    loading.value = false
  }
}

// 取消
const handleCancel = () => {
  emit('cancel')
}

// 监听编辑模式和初始数据变化
watch(
  () => [props.editMode, props.initialData],
  () => {
    initFormData()
  },
  { immediate: true }
)

defineExpose({
  resetForm: () => formRef.value?.resetFields()
})

// 获取食物类型标签样式
const getFoodTypeTag = (type) => {
  const typeMap = {
    '主食': 'warning',
    '肉类': 'danger',
    '蔬菜': 'success',
    '水果': 'success',
    '饮品': 'info',
    '零食': '',
    '其他': 'info'
  }
  return typeMap[type] || 'info'
}
</script>

<template>
  <el-form
    ref="formRef"
    :model="formDataList"
    label-width="100px"
    class="diet-record-form"
  >
    <!-- 修改顶部添加新记录按钮的容器 -->
    <div class="form-actions top-actions" v-if="!editMode">
      <div class="welcome-section">
        <div class="welcome-content">
          <div class="welcome-icon">
            <el-icon :size="32"><Bowl /></el-icon>
          </div>
          <div class="welcome-text">
            <h2>记录今天的饮食</h2>
            <p>记录每一餐，追踪营养摄入，保持健康生活</p>
          </div>
        </div>
        <el-button
          type="primary"
          :icon="Plus"
          @click="addNewRecord"
          class="custom-button add-record-btn"
        >
          添加新记录
        </el-button>
      </div>
    </div>

    <!-- 记录列表 -->
    <div 
      v-for="(record, recordIndex) in formDataList"
      :key="recordIndex"
      class="record-section"
    >
      <div class="record-header">
        <h3 class="record-title">记录 {{ recordIndex + 1 }}</h3>
        <el-button
          v-if="!editMode && formDataList.length > 1"
          type="danger"
          :icon="Delete"
          circle
          @click="removeRecord(recordIndex)"
        />
      </div>

      <!-- 基本信息部分 -->
      <div class="form-section basic-info">
        <h3 class="section-title">
          <el-icon><Bowl /></el-icon>
          基本信息
        </h3>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item
              :label="'用餐类型'"
              :prop="`${recordIndex}.mealType`"
              :rules="{ required: true, message: '请选择用餐类型', trigger: 'change' }"
            >
              <el-select
                v-model="record.mealType"
                placeholder="请选择用餐类型"
                class="form-select"
              >
                <el-option
                  v-for="option in mealTypeOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item
              :label="'用餐时间'"
              :prop="`${recordIndex}.mealTime`"
              :rules="{ required: true, message: '请选择用餐时间', trigger: 'change' }"
            >
              <el-date-picker
                v-model="record.mealTime"
                type="datetime"
                placeholder="选择用餐时间"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DDTHH:mm:ss.SSSZ"
                :default-time="new Date(2000, 1, 1, 12, 0, 0)"
                class="form-date-picker"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item :label="'用餐地点'" :prop="`${recordIndex}.mealPlace`">
              <el-input 
                v-model="record.mealPlace"
                placeholder="请输入用餐地点（选填）"
                maxlength="50"
                show-word-limit
              >
                <template #prefix>
                  <el-icon><Location /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item :label="'备注'" :prop="`${recordIndex}.mealNote`">
              <el-input 
                v-model="record.mealNote"
                type="textarea"
                placeholder="请输入备注信息（选填）"
                maxlength="500"
                show-word-limit
                :rows="3"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 使用新的FoodList组件 -->
      <food-list
        v-model="record.foodsQuantitiesDTOList"
        :form-path="`${recordIndex}.foodsQuantitiesDTOList`"
      />
    </div>

    <!-- 表单按钮 -->
    <div class="form-actions">
      <div class="form-actions-notice">
        <el-tag type="warning">
          <el-icon><InfoFilled /></el-icon>
          <span>请确保每条记录至少添加了一种食物，否则无法保存</span>
        </el-tag>
      </div>
      <div class="form-actions-buttons">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          :loading="loading"
          @click="handleSubmit"
        >
          {{ editMode ? '修改' : '保存' }}
        </el-button>
      </div>
    </div>
  </el-form>
</template>

<style scoped>
.diet-record-form {
  padding: 20px 0;
}

.form-section {
  background: var(--el-bg-color-page);
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 20px;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.section-title .el-icon {
  font-size: 18px;
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
  padding: 8px;
  border-radius: 6px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.food-item {
  background: var(--el-bg-color);
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  border: 1px solid var(--el-border-color-lighter);
  transition: all 0.3s ease;
}

.food-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.food-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.food-index {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.food-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
}

.food-name {
  font-weight: 500;
}

.food-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.calories {
  color: var(--el-text-color-secondary);
  font-size: 0.9em;
}

:deep(.el-select),
:deep(.el-date-picker),
:deep(.el-input-number) {
  width: 100%;
}

.form-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid var(--el-border-color-lighter);
}

.form-actions-notice {
  margin-bottom: 10px;
}

.form-actions-notice .el-tag {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 12px;
  font-size: 14px;
}

.form-actions-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  box-shadow: none;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select__wrapper:hover) {
  border-color: var(--el-color-primary);
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focus) {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 1px var(--el-color-primary-light-5);
}

/* 动画效果 */
.food-item {
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .el-row {
    margin: 0 !important;
  }
  
  .el-col {
    padding: 0 !important;
  }
  
  .food-item {
    padding: 15px;
  }
}

/* 重新设计顶部添加记录区域 */
.top-actions {
  margin-bottom: 24px;
  background: linear-gradient(135deg, var(--el-color-primary-light-8) 0%, var(--el-color-primary-light-9) 100%);
  border-radius: 12px;
  padding: 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  border: 1px solid var(--el-color-primary-light-5);
}

.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  position: relative;
}

.welcome-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.welcome-icon {
  background: var(--el-color-primary);
  color: white;
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.3);
  animation: float 3s ease-in-out infinite;
}

.welcome-text {
  color: var(--el-text-color-primary);
}

.welcome-text h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  background: linear-gradient(120deg, var(--el-color-primary) 0%, var(--el-color-primary-light-3) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.welcome-text p {
  margin: 0;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.add-record-btn {
  padding: 12px 24px;
  font-size: 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background: var(--el-color-primary);
  border: none;
  box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.3);
}

.add-record-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(var(--el-color-primary-rgb), 0.4);
}

.add-record-btn:active {
  transform: translateY(0);
}

@keyframes float {
  0% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-6px);
  }
  100% {
    transform: translateY(0px);
  }
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .welcome-section {
    flex-direction: column;
    gap: 20px;
    text-align: center;
    padding: 20px;
  }

  .welcome-content {
    flex-direction: column;
  }

  .welcome-text h2 {
    font-size: 20px;
  }

  .add-record-btn {
    width: 100%;
  }
}

/* 修改食用量输入框样式 */
.quantity-input {
  width: 100% !important;
}

.quantity-input :deep(.el-input__wrapper) {
  padding-right: 50px !important;
}

.quantity-input :deep(.el-input__inner) {
  font-size: 14px;
  text-align: left;
  padding-left: 12px;
}

.quantity-unit {
  position: absolute;
  right: 40px;
  color: var(--el-text-color-secondary);
}

/* 统一按钮样式 */
.custom-button,
.custom-button.el-button--primary.is-plain,
.el-button--primary {
  --el-button-text-color: #fff !important;
  --el-button-bg-color: var(--el-color-primary) !important;
  --el-button-border-color: var(--el-color-primary) !important;
  --el-button-hover-text-color: #fff !important;
  --el-button-hover-bg-color: var(--el-color-primary-light-3) !important;
  --el-button-hover-border-color: var(--el-color-primary-light-3) !important;
  --el-button-active-text-color: #fff !important;
}

/* 优化输入框控件样式 */
:deep(.el-input-number__decrease),
:deep(.el-input-number__increase) {
  border-color: var(--el-border-color);
  background-color: var(--el-fill-color-light);
}

:deep(.el-input-number__decrease:hover),
:deep(.el-input-number__increase:hover) {
  color: var(--el-color-primary);
}

/* 调整列间距 */
.food-item .el-col-10 {
  padding-left: 20px;
}

/* 确保所有主要按钮文字为白色 */
:deep(.el-button--primary) {
  color: #fff !important;
}

:deep(.el-button--primary:hover) {
  color: #fff !important;
}
</style> 