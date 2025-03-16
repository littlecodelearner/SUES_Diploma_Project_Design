<template>
  <div class="pagination-info">
    <el-tooltip
      effect="dark"
      placement="top"
      :content="tooltipContent"
    >
      <el-icon class="info-icon"><InfoFilled /></el-icon>
    </el-tooltip>
    <span class="pagination-text">{{ textContent }}</span>
  </div>
</template>

<script>
import {InfoFilled} from '@element-plus/icons-vue'

export default {
  name: 'PaginationInfo',
  components: {
    InfoFilled
  },
  props: {
    tooltipContent: {
      type: String,
      default: '提示：由于一条记录可能包含多个关联数据，系统可能显示的实际记录数量少于预期。'
    },
    textContent: {
      type: String,
      default: ''
    }
  }
}
</script>

<style scoped>
.pagination-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.info-icon {
  color: var(--el-color-info);
  cursor: help;
}

.pagination-text {
  display: none; /* Hide text by default, only show icon */
}
</style> 