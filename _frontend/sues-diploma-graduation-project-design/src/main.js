import {createApp} from 'vue'
import {createPinia} from 'pinia'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import './styles/global.css'
import './styles/button-spacing.css'
import './styles/global-variables.css'
import './styles/dialog-fixes.css'
import {lazyLoad} from './utils/lazyLoadImage'

const app = createApp(App)
const pinia = createPinia()

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册图片懒加载指令
app.directive('lazy', lazyLoad)

// 全局错误处理
app.config.errorHandler = (err, vm, info) => {
  console.error('全局错误:', err)
  console.error('错误信息:', info)
}

// 先使用 pinia
app.use(pinia)
// 再使用 router
app.use(router)
app.use(ElementPlus)

app.mount('#app')
