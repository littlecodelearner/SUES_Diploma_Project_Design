import {defineAsyncComponent} from 'vue'
import {ElLoading} from 'element-plus'

/**
 * 组件懒加载工具函数
 * @param {Function} componentImport 组件导入函数
 * @returns {Promise} 返回组件的Promise
 */
export const lazyLoad = (componentImport) => {
  return defineAsyncComponent({
    loader: componentImport,
    loadingComponent: {
      template: '<div class="component-loading">加载中...</div>'
    },
    delay: 200,
    timeout: 10000
  })
}

/**
 * 路由组件懒加载
 * @param {string} viewPath 视图组件路径
 * @returns {Function} 返回异步组件加载函数
 */
export const lazyLoadView = (viewPath) => {
  return () => import(`@/views/${viewPath}.vue`)
}

export const lazyLoadComponent = (componentPath) => {
  return defineAsyncComponent({
    loader: () => import(componentPath),
    loadingComponent: {
      template: '<div style="height: 200px;"></div>'
    },
    delay: 200,
    onError(error, retry, fail, attempts) {
      if (attempts <= 3) {
        retry()
      } else {
        console.error('组件加载失败:', error)
        fail()
      }
    }
  })
}

export const lazyLoadWithLoading = (componentPath) => {
  let loadingInstance
  return defineAsyncComponent({
    loader: () => {
      return new Promise((resolve, reject) => {
        loadingInstance = ElLoading.service({
          fullscreen: true,
          text: '加载中...'
        })
        import(componentPath)
          .then(component => {
            loadingInstance.close()
            resolve(component)
          })
          .catch(error => {
            loadingInstance.close()
            reject(error)
          })
      })
    },
    delay: 0,
    timeout: 30000
  })
} 