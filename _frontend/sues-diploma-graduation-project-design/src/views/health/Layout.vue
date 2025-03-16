<template>
  <div class="health-layout">
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-active="$route.path"
          router
          class="health-menu"
        >
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <span>返回仪表板</span>
          </el-menu-item>
          <el-menu-item index="/health/create" v-if="!hasHealthProfile">
            <el-icon><Plus /></el-icon>
            <span>创建健康档案</span>
          </el-menu-item>
          <el-menu-item index="/health/edit" v-if="hasHealthProfile">
            <el-icon><Edit /></el-icon>
            <span>编辑健康档案</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import {computed} from 'vue'
import {Edit, House, Plus} from '@element-plus/icons-vue'
import {useUserStore} from '@/store/user'

const userStore = useUserStore()
const hasHealthProfile = computed(() => userStore.healthProfile !== null)
</script>

<style scoped>
.health-layout {
  height: 100vh;
  background-color: var(--el-bg-color);
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: var(--el-menu-bg-color);
  border-right: 1px solid var(--el-border-color-light);
}

.health-menu {
  height: 100%;
  border-right: none;
}

.el-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.el-main {
  padding: 20px;
  background-color: var(--el-bg-color-page);
}
</style> 