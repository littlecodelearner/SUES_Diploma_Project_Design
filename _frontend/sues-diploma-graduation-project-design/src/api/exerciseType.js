import request from '@/utils/request'
import { ApiPath, getApiBaseUrl } from '@/config/api.config'

/**
 * 批量分页查看运动类型
 * @param {object} params 查询参数
 * @returns {Promise}
 */
export function listExerciseTypesByPage(params) {
  return request.post('/exerciseTypes/listByPage', {
    current: params.current || 1,
    size: params.size || 15,
    isAsc: params.isAsc ?? true,
    startDateTime: params.startDateTime,
    endDateTime: params.endDateTime,
    name: params.name
  })
}

/**
 * 批量添加运动类型
 * @param {string[]} exerciseNameList 运动类型名称列表
 * @returns {Promise}
 */
export function addExerciseTypes(exerciseNameList) {
  return request.post('/exerciseTypes', {
    exerciseNameList
  })
}

/**
 * 批量修改运动类型
 * @param {Array<{exerciseTypeId: number, newExerciseTypeName: string}>} updateList 更新列表
 * @returns {Promise}
 */
export function updateExerciseTypes(updateList) {
  return request.put('/exerciseTypes', updateList)
}

/**
 * 批量删除运动类型
 * @param {number[]} exerciseTypeIdList 运动类型ID列表
 * @returns {Promise}
 */
export function deleteExerciseTypes(exerciseTypeIdList) {
  return request.delete('/exerciseTypes', {
    data: { exerciseTypeIdList }
  })
}

// 兼容旧版API的函数
export const getExerciseTypes = ({ page, limit, name }) => {
  return listExerciseTypesByPage({
    current: page,
    size: limit,
    name
  })
}

export const updateExerciseType = (exerciseType) => {
  return updateExerciseTypes([{
    exerciseTypeId: exerciseType.id,
    newExerciseTypeName: exerciseType.name
  }])
}

// 获取所有运动类型（不分页）- 如果后端没有提供此接口，可以通过分页接口模拟
export const getAllExerciseTypes = () => {
  return listExerciseTypesByPage({
    current: 1,
    size: 1000
  })
} 