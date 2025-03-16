<template>
  <el-dialog
    v-model="dialogVisible"
    title="批量添加运动类型"
    width="500px"
    :close-on-click-modal="false"
    @closed="handleDialogClosed"
    class="add-exercise-type-dialog"
    :class="{ 'dark': isDarkMode }"
  >
    <div class="dialog-content">
      <el-form ref="formRef" :model="form" :rules="rules">
        <div class="types-list" :class="{ 'has-items': form.types.length > 0 }">
          <transition-group name="type-item">
            <div
              v-for="(type, index) in form.types"
              :key="index"
              class="type-item"
            >
              <el-form-item
                :prop="'types.' + index + '.name'"
                :rules="typeRules"
              >
                <div class="type-input-group">
                  <div class="type-index">{{ index + 1 }}.</div>
                  <el-input
                    v-model="type.name"
                    placeholder="请输入运动类型名称"
                    :maxlength="20"
                    clearable
                    @keyup.enter="handleEnterPress(index)"
                  >
                    <template #append>
                      <el-button
                        type="danger"
                        :icon="Delete"
                        @click="removeType(index)"
                      />
                    </template>
                  </el-input>
                </div>
              </el-form-item>
            </div>
          </transition-group>
        </div>

        <div class="add-type" v-if="form.types.length < 50">
          <el-button
            type="primary"
            :icon="Plus"
            @click="addType"
            class="add-button"
          >
            添加运动类型
          </el-button>
          <div class="type-count" :class="{ 'near-limit': form.types.length >= 40 }">
            {{ form.types.length }}/50
          </div>
        </div>
      </el-form>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :loading="loading" 
          @click="handleSubmit"
          :disabled="form.types.length === 0"
        >
          确认添加
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped>
.add-exercise-type-dialog {
  --dialog-padding: clamp(16px, 3vw, 24px);
  --item-gap: clamp(8px, 2vw, 16px);
  --animation-duration: 0.3s;
  --animation-timing: cubic-bezier(0.4, 0, 0.2, 1);
  --border-radius: 8px;
  --shadow-color: rgba(0, 0, 0, 0.1);
}

.dialog-content {
  padding: var(--dialog-padding) 0;
  max-height: 60vh;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: var(--el-scrollbar-bg-color) transparent;
}

.types-list {
  padding: 0 var(--dialog-padding);
  min-height: 60px;
  transition: all var(--animation-duration) var(--animation-timing);
  border-radius: var(--border-radius);
}

.types-list.has-items {
  padding: var(--item-gap) var(--dialog-padding);
  background: var(--el-fill-color-light);
  box-shadow: inset 0 2px 4px var(--shadow-color);
}

.type-item {
  margin-bottom: var(--item-gap);
  transition: all var(--animation-duration) var(--animation-timing);
}

.type-item:last-child {
  margin-bottom: 0;
}

.type-input-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.type-index {
  width: 24px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  text-align: right;
  transition: color var(--animation-duration) var(--animation-timing);
}

.add-type {
  padding: var(--dialog-padding);
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top: 1px solid var(--el-border-color-lighter);
  margin-top: var(--dialog-padding);
  transition: all var(--animation-duration) var(--animation-timing);
}

.add-button {
  position: relative;
  overflow: hidden;
  transition: all var(--animation-duration) var(--animation-timing);
}

.add-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px var(--shadow-color);
}

.add-button:active {
  transform: scale(0.95);
}

.type-count {
  color: var(--el-text-color-secondary);
  font-size: 14px;
  transition: all var(--animation-duration) var(--animation-timing);
  display: flex;
  align-items: center;
  gap: 8px;
}

.type-count.near-limit {
  color: var(--el-color-warning);
  font-weight: bold;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.6; }
  100% { opacity: 1; }
}

/* 动画效果优化 */
.type-item-enter-active,
.type-item-leave-active {
  transition: all var(--animation-duration) var(--animation-timing);
}

.type-item-enter-from {
  opacity: 0;
  transform: translateX(-20px) scale(0.9);
}

.type-item-leave-to {
  opacity: 0;
  transform: translateX(20px) scale(0.9);
}

/* 深色模式优化 */
.dark {
  --shadow-color: rgba(0, 0, 0, 0.3);
  
  .types-list.has-items {
    background: var(--el-bg-color);
  }

  .type-index {
    color: var(--el-text-color-regular);
  }

  .type-count {
    color: var(--el-text-color-regular);
  }

  .el-input {
    --el-input-bg-color: var(--el-bg-color-overlay);
    --el-input-border-color: var(--el-border-color-light);
    --el-input-hover-border-color: var(--el-border-color);
  }
}

/* 响应式设计优化 */
@media screen and (max-width: 768px) {
  .add-exercise-type-dialog {
    --animation-duration: 0.2s;
  }

  .type-input-group {
    gap: 4px;
  }

  .type-index {
    font-size: 12px;
  }

  .add-type {
    flex-direction: column;
    gap: var(--item-gap);
  }

  .add-button {
    width: 100%;
  }

  .type-count {
    width: 100%;
    justify-content: center;
  }

  .el-dialog {
    width: 90% !important;
    margin: 5vh auto !important;
  }
}

/* 自定义滚动条优化 */
.dialog-content::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.dialog-content::-webkit-scrollbar-thumb {
  background: var(--el-scrollbar-bg-color);
  border-radius: 3px;
}

.dialog-content::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 3px;
}

.dialog-content::-webkit-scrollbar-corner {
  background: transparent;
}
</style>

<script setup>
import {computed, ref} from 'vue'
import {ElMessage} from 'element-plus'
import {Delete, Plus} from '@element-plus/icons-vue'
import {addExerciseTypes} from '@/api/exerciseType'

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  theme: {
    type: String,
    default: 'light'
  }
})

const emit = defineEmits(['update:visible', 'success'])

// 深色模式
const isDarkMode = computed(() => props.theme === 'dark')

// 对话框可见性
const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

// 表单数据
const formRef = ref(null)
const form = ref({
  types: [{ name: '' }]
})

// 加载状态
const loading = ref(false)

// 单个类型的校验规则
const typeRules = [
  { required: true, message: '请输入运动类型名称', trigger: 'blur' },
  { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
]

// 添加新的运动类型输入框
const addType = () => {
  if (form.value.types.length >= 50) {
    ElMessage.warning('最多只能添加50个运动类型')
    return
  }
  form.value.types.push({ name: '' })
}

// 移除运动类型输入框
const removeType = (index) => {
  form.value.types.splice(index, 1)
  // 确保至少保留一个输入框
  if (form.value.types.length === 0) {
    form.value.types.push({ name: '' })
  }
}

// 处理回车键按下
const handleEnterPress = (index) => {
  // 如果是最后一个输入框，且内容不为空，则添加新的输入框
  if (index === form.value.types.length - 1 && 
      form.value.types[index].name.trim() !== '' &&
      form.value.types.length < 50) {
    addType()
  }
}

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 过滤掉空的输入
    const validTypes = form.value.types
      .map(type => type.name.trim())
      .filter(name => name !== '')

    if (validTypes.length === 0) {
      ElMessage.warning('请至少输入一个运动类型')
      return
    }
    
    loading.value = true
    const response = await addExerciseTypes(validTypes)
    
    if (response.data.code === 200) {
      ElMessage.success('添加成功')
      dialogVisible.value = false
      emit('success')
    } else {
      ElMessage.error(response.data.message || '添加失败')
    }
  } catch (error) {
    console.error('添加运动类型失败:', error)
    ElMessage.error('添加失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理对话框关闭
const handleDialogClosed = () => {
  form.value.types = [{ name: '' }]
  if (formRef.value) {
    formRef.value.resetFields()
  }
}
</script> 