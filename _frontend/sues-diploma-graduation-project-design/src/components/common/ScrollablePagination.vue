<template>
  <div class="scrollable-pagination-container" ref="paginationContainer">
    <div class="pagination-wrapper">
      <pagination-info
        v-if="showInfo"
        :tooltip-content="tooltipContent"
        :text-content="textContent" 
      />
      <el-pagination
        v-bind="$attrs"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import {onMounted, ref} from 'vue'
import PaginationInfo from './PaginationInfo.vue'
import {scrollToElement} from '@/utils/scrollHelper'

export default {
  name: 'ScrollablePagination',
  components: {
    PaginationInfo
  },
  props: {
    // Whether to show pagination info
    showInfo: {
      type: Boolean,
      default: true
    },
    // Tooltip content
    tooltipContent: {
      type: String,
      default: '提示：由于一条记录可能包含多个关联数据，系统可能显示的实际记录数量少于预期。'
    },
    // Text content
    textContent: {
      type: String,
      default: ''
    },
    // Scroll target selector, defaults to component itself
    scrollTarget: {
      type: String,
      default: null
    },
    // Top offset
    topOffset: {
      type: Number,
      default: 80 // Default offset to avoid content being hidden by navigation bar
    }
  },
  emits: ['size-change', 'current-change'],
  setup(props, { emit }) {
    // Store component reference for scrolling
    const paginationContainer = ref(null)

    // Handle page size change
    const handleSizeChange = (size) => {
      emit('size-change', size)
      scrollToTarget()
    }

    // Handle page number change
    const handleCurrentChange = (current) => {
      emit('current-change', current)
      scrollToTarget()
    }

    // Scroll to target position
    const scrollToTarget = async () => {
      try {
        // If a scroll target selector is provided, use it first
        if (props.scrollTarget) {
          await scrollToElement(props.scrollTarget, { offset: props.topOffset })
        } 
        // Otherwise scroll to the component itself
        else if (paginationContainer.value) {
          // We're actually scrolling to the parent element (card/table), not the pagination component itself
          const parentElement = paginationContainer.value.parentElement
          if (parentElement) {
            await scrollToElement(parentElement, { offset: props.topOffset })
          } else {
            await scrollToElement(paginationContainer.value, { offset: props.topOffset })
          }
        }
      } catch (error) {
        console.error('Pagination scroll failed:', error)
      }
    }

    // Ensure reference is available after mounting
    onMounted(() => {
      console.log('Scrollable pagination component mounted')
    })

    return {
      paginationContainer,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.scrollable-pagination-container {
  width: 100%;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
