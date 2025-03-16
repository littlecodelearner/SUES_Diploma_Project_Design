<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete, Food } from '@element-plus/icons-vue'
import { listDietFoodsDetailsByPage } from '@/api/food'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  formPath: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

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
      foodOptions.value = allFoods.value
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
    foodOptions.value = allFoods.value
    return
  }
  
  foodOptions.value = allFoods.value.filter(food => 
    food.foodName.toLowerCase().includes(query.toLowerCase()) ||
    food.foodType.toLowerCase().includes(query.toLowerCase())
  )
}

// 添加食物项
const addFoodItem = () => {
  const foodList = [...props.modelValue]
  foodList.push({ foodId: null, quantity: null })
  emit('update:modelValue', foodList)
}

// 移除食物项
const removeFoodItem = (index) => {
  const foodList = [...props.modelValue]
  foodList.splice(index, 1)
  emit('update:modelValue', foodList)
}

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

// 在组件挂载时获取所有食物数据
onMounted(() => {
  fetchAllFoods()
})

// 获取完整的表单路径
const getFullProp = (field) => {
  return props.formPath ? `${props.formPath}.${field}` : field
}
</script>

<template>
  <div class="food-list">
    <div class="section-header">
      <h3 class="section-title">
        <el-icon><Food /></el-icon>
        食物清单
        <span class="required-indicator">*</span>
      </h3>
      <el-button
        type="primary"
        :icon="Plus"
        @click="addFoodItem"
        class="custom-button"
      >
        添加食物
      </el-button>
    </div>

    <!-- 必填提示 -->
    <div class="required-notice">
      <el-alert
        title="请至少添加一种食物，否则无法保存记录"
        type="warning"
        :closable="false"
        show-icon
      />
    </div>

    <div 
      v-for="(food, foodIndex) in modelValue" 
      :key="foodIndex"
      class="food-item"
    >
      <div class="food-item-header">
        <span class="food-index">食物 {{ foodIndex + 1 }}</span>
        <el-button
          v-if="modelValue.length > 1"
          type="danger"
          :icon="Delete"
          circle
          @click="removeFoodItem(foodIndex)"
        />
      </div>

      <el-row :gutter="20">
        <el-col :span="14">
          <el-form-item
            :prop="getFullProp(`${foodIndex}.foodId`)"
            :rules="[
              { required: true, message: '请选择食物', trigger: 'change' }
            ]"
          >
            <el-select
              v-model="food.foodId"
              placeholder="搜索并选择食物"
              filterable
              :loading="foodsLoading"
              :filter-method="searchFoods"
              :reserve-keyword="true"
              class="food-select"
            >
              <el-option
                v-for="item in foodOptions"
                :key="item.foodId"
                :label="item.foodName"
                :value="item.foodId"
              >
                <div class="food-option">
                  <span class="food-name">{{ item.foodName }}</span>
                  <span class="food-info">
                    <el-tag size="small" :type="getFoodTypeTag(item.foodType)">
                      {{ item.foodType }}
                    </el-tag>
                    <span class="calories">{{ item.calories }}kcal/100g</span>
                  </span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        
        <el-col :span="10">
          <el-form-item
            :prop="getFullProp(`${foodIndex}.quantity`)"
            :rules="[
              { required: true, message: '请输入食物重量', trigger: 'blur' },
              { type: 'number', min: 1, message: '重量必须大于0', trigger: 'blur' }
            ]"
          >
            <el-input-number
              v-model="food.quantity"
              :min="1"
              :max="5000"
              :step="10"
              placeholder="食物重量"
              class="quantity-input"
            >
              <template #suffix>克</template>
            </el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<style scoped>
.food-list {
  margin-top: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 16px;
  color: var(--el-text-color-primary);
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

/* 必填指示器和提示样式 */
.required-indicator {
  color: var(--el-color-danger);
  margin-left: 4px;
  font-size: 16px;
}

.required-notice {
  margin-bottom: 15px;
}

:deep(.el-select),
:deep(.el-input-number) {
  width: 100%;
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
</style> 