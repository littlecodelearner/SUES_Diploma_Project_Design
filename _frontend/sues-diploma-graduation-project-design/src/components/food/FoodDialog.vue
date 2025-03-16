<template>
  <el-dialog
    :title="getDialogTitle"
    v-model="dialogVisible"
    width="80%"
    :z-index="99999"
    :close-on-click-modal="false"
    :append-to-body="false"
    @close="handleClose"
    :custom-class="customClass"
  >
    <div class="batch-form-header">
      <template v-if="!isEdit">
        <el-button type="primary" @click="addFoodItem">
          <el-icon><Plus /></el-icon>添加一行
        </el-button>
        <el-button type="danger" @click="removeAllItems">
          <el-icon><Delete /></el-icon>清空所有
        </el-button>
      </template>
    </div>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      class="food-form"
    >
      <div v-for="(item, index) in formData.foods" :key="index" class="food-item">
        <div class="food-item-header">
          <span class="item-title">食物 #{{ index + 1 }}</span>
          <el-button type="danger" link @click="removeFoodItem(index)">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>

        <el-form-item
          :label="'食物名称'"
          :prop="'foods.' + index + '.foodName'"
          :rules="rules.foodName"
        >
          <el-input v-model="item.foodName" placeholder="请输入食物名称" />
        </el-form-item>
        
        <el-form-item
          :label="'食物分类'"
          :prop="'foods.' + index + '.foodType'"
          :rules="rules.foodType"
        >
          <el-select 
            v-model="item.foodType" 
            placeholder="请选择或输入食物分类" 
            style="width: 100%"
            allow-create
            default-first-option
            filterable
            :reserve-keyword="true"
            :teleported="false"
            :popper-class="'select-dropdown-' + index"
            @visible-change="(visible) => handleSelectVisibleChange(visible, index)"
            @create="value => handleCreateOption(value, index)"
            :popper-options="{
              modifiers: [
                {
                  name: 'computeStyles',
                  options: {
                    adaptive: false,
                    gpuAcceleration: false
                  }
                }
              ],
              strategy: 'fixed'
            }">
            <template #prefix>
              <el-tooltip 
                content="您可以选择已有分类或直接输入新的分类名称" 
                placement="top"
                effect="light">
                <el-icon><InfoFilled /></el-icon>
              </el-tooltip>
            </template>
            <el-option
              v-for="type in props.foodTypes"
              :key="type"
              :label="type"
              :value="type"
            />
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item
              :label="'热量'"
              :prop="'foods.' + index + '.calories'"
              :rules="rules.calories"
            >
              <el-input-number
                v-model="item.calories"
                :min="0"
                :precision="1"
                :step="0.1"
                style="width: 100%"
              >
                <template #suffix>kcal/100g</template>
              </el-input-number>
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item
              :label="'蛋白质'"
              :prop="'foods.' + index + '.protein'"
            >
              <el-input-number
                v-model="item.protein"
                :min="0"
                :precision="1"
                :step="0.1"
                style="width: 100%"
              >
                <template #suffix>g/100g</template>
              </el-input-number>
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item
              :label="'脂肪'"
              :prop="'foods.' + index + '.fat'"
            >
              <el-input-number
                v-model="item.fat"
                :min="0"
                :precision="1"
                :step="0.1"
                style="width: 100%"
              >
                <template #suffix>g/100g</template>
              </el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item
              :label="'碳水化合物'"
              :prop="'foods.' + index + '.carbohydrates'"
            >
              <el-input-number
                v-model="item.carbohydrates"
                :min="0"
                :precision="1"
                :step="0.1"
                style="width: 100%"
              >
                <template #suffix>g/100g</template>
              </el-input-number>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item
              :label="'水分'"
              :prop="'foods.' + index + '.water'"
            >
              <el-input-number
                v-model="item.water"
                :min="0"
                :precision="1"
                :step="0.1"
                style="width: 100%"
              >
                <template #suffix>ml/100g</template>
              </el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
    </el-form>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          {{ isEdit ? '保存' : '添加' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete, InfoFilled } from '@element-plus/icons-vue'

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  foodTypes: {
    type: Array,
    required: true
  },
  editData: {
    type: [Object, Array],
    default: null
  },
  customClass: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:visible', 'success'])

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const isEdit = computed(() => !!props.editData)

const formRef = ref(null)
const loading = ref(false)

// 表单数据
const formData = ref({
  foods: []
})

// 表单验证规则
const rules = {
  foodName: [
    { required: true, message: '请输入食物名称', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  foodType: [
    { required: true, message: '请选择或输入食物分类', trigger: 'change' }
  ],
  calories: [
    { required: true, message: '请输入卡路里值', trigger: 'blur' },
    { type: 'number', min: 0, message: '卡路里不能小于0', trigger: 'blur' }
  ],
  'foods': {
    type: 'array',
    required: true,
    trigger: 'submit',
    validator: (rule, value, callback) => {
      if (!value || value.length === 0) {
        callback(new Error('请至少添加一个食物'))
        return
      }
      
      const errors = []
      value.forEach((food, index) => {
        if (!food.foodName?.trim()) {
          errors.push(`第${index + 1}行：请输入食物名称`)
        }
        if (!food.foodType?.trim()) {
          errors.push(`第${index + 1}行：请选择或输入食物分类`)
        }
        if (typeof food.calories !== 'number' || food.calories < 0) {
          errors.push(`第${index + 1}行：请输入正确的卡路里值`)
        }
        // 其他营养成分字段验证（允许为0或正数）
        const nutritionFields = ['protein', 'fat', 'carbohydrates', 'water']
        nutritionFields.forEach(field => {
          if (typeof food[field] !== 'number' || food[field] < 0) {
            food[field] = 0
          }
        })
      })
      
      if (errors.length > 0) {
        callback(new Error(errors.join('\n')))
      } else {
        callback()
      }
    }
  }
}

// 创建新的食物项，设置默认值
const createFoodItem = () => ({
  foodName: '',
  foodType: '',
  calories: 0.00,
  protein: 0.00,
  fat: 0.00,
  carbohydrates: 0.00,
  water: 0.00
})

// 添加食物项
const addFoodItem = () => {
  formData.value.foods.push(createFoodItem())
}

// 移除食物项
const removeFoodItem = (index) => {
  formData.value.foods.splice(index, 1)
}

// 移除所有食物项
const removeAllItems = () => {
  formData.value.foods = []
}

// 初始化表单数据
const initFormData = () => {
  formData.value.foods = []
  if (props.editData) {
    if (Array.isArray(props.editData)) {
      formData.value.foods = props.editData.map(food => ({ ...food }))
    } else {
      formData.value.foods = [{ ...props.editData }]
    }
  } else {
    addFoodItem() // 默认添加一行
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  if (formData.value.foods.length === 0) {
    ElMessage.warning('请至少添加一个食物')
    return
  }
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    // 过滤掉空值并格式化数据
    const submitData = formData.value.foods.map(food => {
      // 确保食物类型是trim过的以去除空白
      const foodType = typeof food.foodType === 'string' ? food.foodType.trim() : food.foodType;
      
      const formattedFood = {
        foodName: food.foodName.trim(),
        foodType: foodType, // 使用处理后的食物类型
        calories: Number(food.calories) || 0,
        protein: Number(food.protein) || 0,
        fat: Number(food.fat) || 0,
        carbohydrates: Number(food.carbohydrates) || 0,
        water: Number(food.water) || 0
      }

      // 在编辑模式下，确保保留原有的foodId
      if (isEdit.value && food.foodId) {
        formattedFood.foodId = food.foodId
      }

      return formattedFood
    })
    
    // 触发成功事件，将表单数据传递给父组件
    emit('success', submitData)
    dialogVisible.value = false
  } catch (error) {
    console.error('表单验证失败:', error)
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    loading.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  formRef.value?.resetFields()
  formData.value.foods = []
  dialogVisible.value = false
}

// 监听visible变化
watch(
  () => props.visible,
  (val) => {
    if (val) {
      initFormData()
    }
  }
)

// 在 script setup 部分修改计算属性
const getDialogTitle = computed(() => {
  if (!isEdit.value) {
    return '批量添加食物'
  }
  // 判断是否是数组来确定是批量修改还是单个修改
  return Array.isArray(props.editData) ? '批量修改食物' : '修改食物'
})

// 处理选择器可见性变化
const handleSelectVisibleChange = (visible, index) => {
  // 当下拉菜单关闭时，触发验证以确保值被接受
  if (!visible && formRef.value) {
    formRef.value.validateField(`foods.${index}.foodType`).catch(err => {
      console.error('食物分类验证错误:', err);
    });
  }
}

// 处理创建新选项
const handleCreateOption = (value, index) => {
  // 当创建新选项时，直接将其赋值给对应的食物
  if (value && typeof value === 'string') {
    const trimmedValue = value.trim();
    if (trimmedValue) {
      formData.value.foods[index].foodType = trimmedValue;
      // 立即验证以确保自定义值被接受
      nextTick(() => {
        formRef.value?.validateField(`foods.${index}.foodType`).catch(err => {
          console.error('食物分类验证错误:', err);
        });
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.food-form {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 20px;
  
  :deep(.el-form-item__content) {
    flex-wrap: nowrap;
  }
  
  /* 确保下拉菜单能正确显示 */
  :deep(.el-select__popper) {
    z-index: 2100 !important;
  }
  
  /* 优化选择器样式 */
  :deep(.el-select) {
    width: 100%;
    
    .el-select__tags {
      max-width: calc(100% - 30px);
    }
  }
}

.batch-form-header {
  margin-bottom: 20px;
  display: flex;
  gap: 12px;
}

.food-item {
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 20px;
  background-color: #f8f9fa;
  
  .food-item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .item-title {
      font-size: 16px;
      font-weight: bold;
      color: #606266;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style> 