<template>
  <dashboard-decorator>
    <dashboard />
  </dashboard-decorator>
</template>

<script>
import Dashboard from '@/views/Dashboard.vue'
import DashboardDecorator from '@/components/DashboardDecorator.vue'

export default {
  name: 'DashboardWithDecorator',
  components: {
    Dashboard,
    DashboardDecorator
  }
}
</script> 