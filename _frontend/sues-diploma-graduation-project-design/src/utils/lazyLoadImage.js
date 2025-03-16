export const lazyLoad = {
  mounted(el) {
    const imageObserver = new IntersectionObserver((entries, observer) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const img = entry.target
          const src = img.getAttribute('data-src')
          if (src) {
            img.src = src
            img.removeAttribute('data-src')
          }
          observer.unobserve(img)
        }
      })
    }, {
      rootMargin: '50px 0px'
    })

    if (el.getAttribute('data-src')) {
      imageObserver.observe(el)
    }
  }
}

// 使用示例：
// <img v-lazy data-src="图片路径" src="占位图路径" /> 