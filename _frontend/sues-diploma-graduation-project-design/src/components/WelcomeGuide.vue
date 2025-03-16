<template>
  <el-dialog
    v-model="dialogVisible"
    title="欢迎使用日常运动健康管理系统"
    width="60%"
    :close-on-click-modal="false"
    :show-close="false"
  >
    <div class="welcome-content">
      <el-steps :active="activeStep" finish-status="success" align-center>
        <el-step title="创建健康档案" description="记录您的基本健康信息" />
        <el-step title="设置健康目标" description="制定您的健康计划" />
        <el-step title="开始记录" description="记录您的运动和饮食数据" />
      </el-steps>

      <div class="step-content" v-if="activeStep === 0">
        <h3>🏥 创建健康档案</h3>
        <p>首先，让我们创建您的健康档案，这将帮助系统更好地为您提供个性化建议。</p>
        <ul>
          <li>记录基本健康信息</li>
          <li>填写运动习惯</li>
          <li>注明特殊健康状况（如有）</li>
        </ul>
      </div>

      <div class="step-content" v-else-if="activeStep === 1">
        <h3>🎯 设置健康目标</h3>
        <p>接下来，设置您想要达到的健康目标，这将帮助您保持动力。</p>
        <ul>
          <li>制定运动计划</li>
          <li>设置体重目标</li>
          <li>规划饮食习惯</li>
        </ul>
      </div>

      <div class="step-content" v-else-if="activeStep === 2">
        <h3>📝 开始记录</h3>
        <p>最后，开始记录您的日常运动和饮食数据。</p>
        <ul>
          <li>记录运动数据</li>
          <li>记录饮食情况</li>
          <li>追踪健康指标</li>
        </ul>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button v-if="activeStep > 0" @click="activeStep--" >上一步</el-button>
        <el-button
          type="primary"
          @click="handleNext"
        >
          {{ activeStep === 2 ? '开始使用' : '下一步' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import {ref} from 'vue'
import {useRouter} from 'vue-router'

const router = useRouter()
const dialogVisible = ref(true)
const activeStep = ref(0)

const emit = defineEmits(['close'])

const handleNext = () => {
  if (activeStep.value < 2) {
    activeStep.value++
  } else {
    dialogVisible.value = false
    emit('close')
    // 根据步骤引导用户到相应页面
    if (!localStorage.getItem('hasHealthProfile')) {
      router.push('/health/create')
    }
  }
}
</script>

<style scoped>
.welcome-content {
  padding: 20px 0;
}

.step-content {
  margin-top: 40px;
  text-align: center;
}

.step-content h3 {
  margin-bottom: 20px;
  color: var(--el-color-primary);
}

.step-content p {
  margin-bottom: 20px;
  color: var(--el-text-color-regular);
}

.step-content ul {
  display: inline-block;
  text-align: left;
  margin: 0;
  padding: 0;
  list-style-position: inside;
}

.step-content li {
  margin: 10px 0;
  color: var(--el-text-color-secondary);
}

.dialog-footer {
  text-align: right;
}
</style> 