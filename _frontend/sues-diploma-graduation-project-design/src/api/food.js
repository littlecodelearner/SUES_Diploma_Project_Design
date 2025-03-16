import request from '@/utils/request'

/**
 * 分页获取食物数据列表
 * @param {Object} params 查询参数对象
 * @returns {Promise}
 */
export function listFoodsByPage(params) {
  return request.post('/diet-foods/listByPage', {
    current: Math.min(Math.max(1, params.current || 1), 1000),
    size: Math.min(Math.max(15, params.size || 15), 50),
    isAsc: params.isAsc ?? true,
    foodName: params.foodName || '',
    foodType: params.foodType || ''
  })
}

/**
 * 获取所有食物分类
 * @returns {Promise}
 */
export function getFoodTypes() {
  return request.get('/diet-foods/foodTypes')
}

/**
 * 批量添加食物
 * @param {Array} foods 食物数据数组
 * @returns {Promise}
 */
export function addFoods(foods) {
  return request.post('/diet-foods', foods)
}

/**
 * 批量更新食物
 * @param {Array} foods 食物数据数组
 * @returns {Promise}
 */
export function updateFoods(foods) {
  return request.put('/diet-foods', foods)
}

/**
 * 批量删除食物
 * @param {Array} foodIds 食物ID数组
 * @returns {Promise}
 */
export function deleteFoods(foodIds) {
  return request.delete('/diet-foods', {
    data: { foodIdList: foodIds }
  })
}

/**
 * 获取所有食物数据
 * @returns {Promise<{code: number, message: string, data: Array}>}
 */
export function listDietFoodsDetailsByPage() {
  return request.get('/diet-foods')
} 