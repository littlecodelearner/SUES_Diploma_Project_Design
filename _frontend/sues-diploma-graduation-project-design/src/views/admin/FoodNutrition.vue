<template>
  <div 
    class="food-management" 
    :class="{ 
      'dark-mode': isDarkMode, 
      'light-mode': !isDarkMode 
    }"
  >
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6" v-for="(stat, index) in statistics" :key="index">
        <el-card :body-style="{ padding: '20px' }" class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: stat.color }">
              <el-icon><component :is="stat.icon" /></el-icon>
          </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
        </div>
        </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 在统计卡片下方添加数据分析图表 -->
    <el-row :gutter="20" class="chart-section">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>食物分类分布</span>
      </div>
          </template>
          <div id="foodTypesPieChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>营养成分分布</span>
    </div>
          </template>
          <div id="nutritionBarChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索和过滤区域 -->
        <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="食物名称">
          <el-input
            v-model="searchForm.foodName"
            placeholder="搜索食物名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="食物分类">
          <el-select
            v-model="searchForm.foodType"
            placeholder="选择分类"
            clearable
            filterable
          >
            <el-option
              v-for="type in foodTypes"
              :key="type"
              :label="type"
              :value="type"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
        </div>

    <!-- 操作按钮区域 -->
        <div class="action-section">
      <el-button-group>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>添加食物
        </el-button>
        <el-button 
          type="warning" 
          :disabled="!selectedFoods.length" 
          @click="handleBatchEdit"
        >
          <el-icon><Edit /></el-icon>批量修改
          </el-button>
          <el-button
            type="danger"
          :disabled="!selectedFoods.length" 
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>批量删除
          </el-button>
        <el-button type="success" @click="exportData">
          <el-icon><Download /></el-icon>导出数据
        </el-button>
        <el-button type="warning" @click="showImportDialog">
          <el-icon><Upload /></el-icon>批量导入
        </el-button>
      </el-button-group>
      <el-button-group>
        <el-tooltip content="切换视图模式">
          <el-button @click="toggleViewMode">
            <el-icon><component :is="viewMode === 'grid' ? List : Grid" /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="刷新数据">
          <el-button @click="refreshData">
            <el-icon><Refresh /></el-icon>
          </el-button>
        </el-tooltip>
      </el-button-group>
      </div>

    <!-- 加载状态 -->
    <div v-loading="loading" class="content-area">
      <!-- 网格视图 -->
        <template v-if="viewMode === 'grid'">
          <div class="food-grid">
            <el-card
            v-for="food in foodList"
              :key="food.foodId"
              class="food-card"
            :body-style="{ padding: '0px' }"
            >
              <div class="food-card-header">
              <h3>{{ food.foodName }}</h3>
                <el-tag size="small" :type="getTagType(food.foodType)">
                  {{ food.foodType }}
                </el-tag>
              </div>
              <div class="food-card-content">
                <div class="nutrition-chart" :id="'chart-' + food.foodId"></div>
              <div class="nutrition-info">
                  <div class="nutrition-item">
                    <span class="label">热量:</span>
                  <span class="value">{{ food.calories }}kcal/100g</span>
                  </div>
                  <div class="nutrition-item">
                    <span class="label">蛋白质:</span>
                    <span class="value">{{ food.protein }}g/100g</span>
                  </div>
                  <div class="nutrition-item">
                    <span class="label">脂肪:</span>
                    <span class="value">{{ food.fat }}g/100g</span>
                  </div>
                  <div class="nutrition-item">
                  <span class="label">碳水化合物:</span>
                    <span class="value">{{ food.carbohydrates }}g/100g</span>
                  </div>
                  <div class="nutrition-item">
                    <span class="label">水分:</span>
                    <span class="value">{{ food.water }}ml/100g</span>
                  </div>
                </div>
              </div>
            <div class="food-card-actions">
                <el-button-group>
                <el-button type="primary" link @click="handleEdit(food)">
                  <el-icon><Edit /></el-icon>编辑
                  </el-button>
                <el-button type="danger" link @click="handleDelete(food)">
                  <el-icon><Delete /></el-icon>删除
                  </el-button>
                </el-button-group>
              </div>
            </el-card>
          </div>
        </template>

      <!-- 表格视图 -->
        <template v-else>
          <el-table
          :data="foodList"
            style="width: 100%"
            :class="{ 'dark-mode': isDarkMode, 'light-mode': !isDarkMode }"
          @selection-change="handleSelectionChange"
          :row-style="{ height: 'auto', padding: '10px 0' }"
          :cell-style="{ padding: '15px 0' }"
          border
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="foodName" label="食物名称" min-width="120" />
          <el-table-column prop="foodType" label="分类" width="120">
              <template #default="{ row }">
              <el-tag :type="getTagType(row.foodType)">{{ row.foodType }}</el-tag>
              </template>
            </el-table-column>
          <el-table-column prop="calories" label="热量(kcal/100g)" width="150" />
          <el-table-column label="营养成分" min-width="320">
              <template #default="{ row }">
                <div class="nutrition-text-display">
                  <div class="nutrition-item">
                    <span class="nutrition-label">蛋白质:</span>
                    <span class="nutrition-value">{{ row.protein }}g/100g</span>
                  </div>
                  <div class="nutrition-item">
                    <span class="nutrition-label">脂肪:</span>
                    <span class="nutrition-value">{{ row.fat }}g/100g</span>
                  </div>
                  <div class="nutrition-item">
                    <span class="nutrition-label">碳水化合物:</span>
                    <span class="nutrition-value">{{ row.carbohydrates }}g/100g</span>
                  </div>
                  <div class="nutrition-item">
                    <span class="nutrition-label">水分:</span>
                    <span class="nutrition-value">{{ row.water }}ml/100g</span>
                  </div>
                </div>
              </template>
            </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button-group>
                <el-button type="primary" link @click="handleEdit(row)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button type="danger" link @click="handleDelete(row)">
                  <el-icon><Delete /></el-icon>
                </el-button>
                </el-button-group>
              </template>
            </el-table-column>
          </el-table>
        </template>

      <!-- 分页器 -->
        <div class="pagination-container">
          <el-pagination
          v-model:current-page="searchForm.current"
          v-model:page-size="searchForm.size"
            :page-sizes="[15, 30, 50]"
          :total="total"
            layout="total, sizes, first, prev, pager, next, last, jumper"
            @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          />
      </div>
    </div>

    <!-- 在template最后添加对话框组件 -->
    <food-dialog
      custom-class="food-dialog-custom"
      v-model:visible="dialogVisible"
      :food-types="foodTypes"
      :edit-data="editingFood"
      @success="handleDialogSuccess"
    />

    <!-- 添加导入对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="批量导入食物数据"
      width="500px"
    >
      <div class="import-dialog-content">
        <el-upload
          class="upload-demo"
          drag
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          accept=".xlsx,.xls,.csv"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持 .xlsx, .xls, .csv 格式文件，请确保文件格式正确
            </div>
          </template>
        </el-upload>
        <div class="template-download">
          <el-button type="primary" link @click="downloadTemplate">
            下载导入模板
          </el-button>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="importDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleImport" :loading="importing">
            开始导入
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick, inject, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Edit, List, Grid, Refresh, Download, Upload, UploadFilled } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import * as XLSX from 'xlsx'
import { 
  listFoodsByPage, 
  getFoodTypes, 
  addFoods, 
  updateFoods, 
  deleteFoods 
} from '@/api/food'
import FoodDialog from '@/components/food/FoodDialog.vue'

// 注入主题状态
const isDarkMode = inject('isDarkMode', ref(false))

// 主题相关的计算属性
const currentTheme = computed(() => isDarkMode.value ? 'dark' : 'light')

// 图表主题配置
const getChartThemeOption = (theme) => {
  const darkTheme = {
    backgroundColor: '#1f2937', // 深色背景
    textStyle: {
      color: '#e5e7eb' // 浅色文字
    },
    title: {
      textStyle: {
        color: '#e5e7eb'
      }
    },
    legend: {
      textStyle: {
        color: '#e5e7eb'
      }
    },
    tooltip: {
      backgroundColor: '#374151',
      borderColor: '#4b5563',
      textStyle: {
        color: '#e5e7eb'
      }
    }
  }

  const lightTheme = {
    backgroundColor: '#ffffff', // 浅色背景
    textStyle: {
      color: '#1f2937' // 深色文字
    }
  }

  return theme === 'dark' ? darkTheme : lightTheme
}

// 标签颜色适配
const getTagType = (foodType) => {
  const tagMappings = {
    '蔬菜': 'success',
    '水果': 'warning',
    '肉类': 'danger',
    '谷物': 'primary',
    '奶制品': 'info'
  }
  return tagMappings[foodType] || 'info'
}

// 状态定义
const loading = ref(false)
const viewMode = ref('grid')
const foodList = ref([])
const foodTypes = ref([])
const selectedFoods = ref([])
const total = ref(0)
const allFoodList = ref([])

// 搜索表单
const searchForm = ref({
  current: 1,
  size: 15,
  isAsc: true,
  foodName: '',
  foodType: ''
})

// 统计数据
const statistics = computed(() => [
  {
    label: '食物总数',
    value: total.value,
    icon: 'Food',
    color: '#409EFF'
  },
  {
    label: '分类数量',
    value: foodTypes.value.length,
    icon: 'Collection',
    color: '#67C23A'
  },
  {
    label: '已选择',
    value: selectedFoods.value.length,
    icon: 'Select',
    color: '#E6A23C'
  }
])

// 对话框相关状态
const dialogVisible = ref(false)
const editingFood = ref(null)

// 新增状态
const importDialogVisible = ref(false)
const importing = ref(false)
const importFile = ref(null)

// 方法定义
const fetchFoodTypes = async () => {
  try {
    const res = await getFoodTypes()
    console.log('获取食物分类响应:', res)
    if (res.code === 200) {
      foodTypes.value = res.data || [];
    } else {
      ElMessage.error(res.message || '获取食物分类失败')
    }
  } catch (error) {
    console.error('获取食物分类失败:', error)
    ElMessage.error('获取食物分类失败')
  }
}

const fetchFoodList = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm.value,
      current: Math.min(Math.max(1, searchForm.value.current), 1000),
      size: Math.min(Math.max(15, searchForm.value.size), 50)
    }
    
    console.log('请求参数:', params)
    const res = await listFoodsByPage(params)
    console.log('API响应:', res)
    
    if (res.code === 200) {
      console.log('解析的数据列表:', res.data.dataList)
      console.log('解析的总数:', res.data.total)
      
      foodList.value = res.data.dataList || []
      total.value = res.data.total || 0
      
      console.log('设置后的foodList:', foodList.value)
      console.log('设置后的total:', total.value)
      
      // 初始化所有图表
      nextTick(() => {
        // 清除旧的图表实例
        const charts = document.querySelectorAll('.nutrition-chart')
        charts.forEach(chart => {
          const instance = echarts.getInstanceByDom(chart)
          if (instance) {
            instance.dispose()
          }
        })
        
        // 初始化新的图表
        foodList.value.forEach(food => {
          initNutritionChart(food)
        })
        initCharts()
      })
    } else {
      console.error('响应异常:', res)
      ElMessage.error(res.message || '获取食物列表失败')
    }
  } catch (error) {
    console.error('获取食物列表失败:', error)
    ElMessage.error('获取食物列表失败')
  } finally {
    loading.value = false
  }
}

// 初始化营养成分图表
const initNutritionChart = (food) => {
  const chartDom = document.getElementById(`chart-${food.foodId}`)
  if (!chartDom) {
    console.warn(`未找到图表容器: chart-${food.foodId}`)
    return
  }
  
  try {
    const chart = echarts.init(chartDom)
    const option = {
      ...getChartThemeOption(currentTheme.value),
      radar: {
        indicator: [
          { name: '蛋白质', max: Math.max(100, food.protein || 0) },
          { name: '脂肪', max: Math.max(100, food.fat || 0) },
          { name: '碳水', max: Math.max(100, food.carbohydrates || 0) },
          { name: '水分', max: Math.max(100, food.water || 0) }
        ],
        axisName: {
          color: currentTheme.value === 'dark' ? '#e5e7eb' : '#1f2937'
        },
        splitLine: {
          lineStyle: {
            color: currentTheme.value === 'dark' ? 'rgba(229, 231, 235, 0.2)' : 'rgba(31, 41, 55, 0.2)'
          }
        },
        splitArea: {
          areaStyle: {
            color: currentTheme.value === 'dark' ? ['rgba(55, 65, 81, 0.1)', 'rgba(55, 65, 81, 0.3)'] : ['rgba(255, 255, 255, 0.1)', 'rgba(255, 255, 255, 0.3)']
          }
        }
      },
      series: [{
        type: 'radar',
        data: [{
          value: [
            food.protein || 0,
            food.fat || 0,
            food.carbohydrates || 0,
            food.water || 0
          ],
          itemStyle: {
            color: currentTheme.value === 'dark' ? 'rgba(96, 165, 250, 0.7)' : 'rgba(59, 130, 246, 0.7)'
          },
          lineStyle: {
            color: currentTheme.value === 'dark' ? 'rgba(96, 165, 250, 0.8)' : 'rgba(59, 130, 246, 0.8)'
          }
        }]
      }]
    }
    chart.setOption(option)
  } catch (error) {
    console.error('初始化营养成分图表失败:', error)
  }
}

// 其他工具方法
const getNutritionTooltip = (food) => {
  return `
    蛋白质: ${food.protein}g/100g
    脂肪: ${food.fat}g/100g
    碳水化合物: ${food.carbohydrates}g/100g
    水分: ${food.water}ml/100g
  `
}

const getNutritionPercentage = (value) => {
  return Math.min(value, 100)
}

// 事件处理方法
const handleSearch = () => {
  searchForm.value.current = 1
  fetchFoodList()
}

const resetSearch = () => {
  searchForm.value = {
  current: 1,
  size: 15,
  isAsc: true,
  foodName: '',
  foodType: ''
  }
  fetchFoodList()
}

const handleSelectionChange = (selection) => {
  selectedFoods.value = selection
}

const handleSizeChange = (size) => {
  searchForm.value.size = size
  fetchFoodList()
}

const handleCurrentChange = (current) => {
  searchForm.value.current = current
  fetchFoodList()
}

const toggleViewMode = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
  
  // 在视图更新后重新初始化图表
  nextTick(() => {
    // 重新应用主题样式
    const foodManagementEl = document.querySelector('.food-management')
    if (foodManagementEl) {
      if (isDarkMode.value) {
        foodManagementEl.classList.add('dark-mode')
        foodManagementEl.classList.remove('light-mode')
      } else {
        foodManagementEl.classList.remove('dark-mode')
        foodManagementEl.classList.add('light-mode')
      }
    }
    
    // 重新初始化图表
    if (viewMode.value === 'grid') {
      foodList.value.forEach(food => {
        initNutritionChart(food)
      })
    }
    
    // 重新初始化统计图表
    initCharts()
  })
}

const refreshData = () => {
  fetchFoodList()
  fetchAllFoodListForChart()
}

// 显示添加对话框
const showAddDialog = () => {
  editingFood.value = null
  dialogVisible.value = true
}

// 显示编辑对话框
const handleEdit = (food) => {
  editingFood.value = { ...food }
  dialogVisible.value = true
}

// 处理对话框提交
const handleDialogSuccess = async (foods) => {
  try {
    loading.value = true
    console.log('提交的食物数据:', foods)
    
    // 检查并收集新的食物类型
    const newFoodTypes = new Set();
    foods.forEach(food => {
      // 确保食物类型是经过trim的字符串并且不为空
      if (food.foodType && typeof food.foodType === 'string') {
        const trimmedType = food.foodType.trim();
        if (trimmedType && !foodTypes.value.includes(trimmedType)) {
          newFoodTypes.add(trimmedType);
          // 确保当前food对象使用的是trim后的类型
          food.foodType = trimmedType;
        }
      }
    });
    
    if (Array.isArray(editingFood.value)) {
      // 批量修改模式
      const updateData = foods.map((food, index) => ({
        ...food,
        foodId: editingFood.value[index].foodId
      }))
      console.log('批量修改数据:', updateData)
      const res = await updateFoods(updateData)
      if (res.code === 200) {
        ElMessage.success('批量修改成功')
        // 如果有新的食物类型，添加到列表并刷新
        if (newFoodTypes.size > 0) {
          foodTypes.value = [...foodTypes.value, ...newFoodTypes];
          console.log('新增食物类型:', newFoodTypes);
          console.log('更新后的食物类型列表:', foodTypes.value);
        }
        fetchFoodList()
      } else {
        ElMessage.error(res.message || '批量修改失败')
      }
    } else if (editingFood.value) {
      // 单个修改模式
      const updateData = [{ ...foods[0], foodId: editingFood.value.foodId }]
      console.log('单个修改数据:', updateData)
      const res = await updateFoods(updateData)
      if (res.code === 200) {
        ElMessage.success('修改成功')
        // 如果有新的食物类型，添加到列表并刷新
        if (newFoodTypes.size > 0) {
          foodTypes.value = [...foodTypes.value, ...newFoodTypes];
          console.log('新增食物类型:', newFoodTypes);
          console.log('更新后的食物类型列表:', foodTypes.value);
        }
        fetchFoodList()
      } else {
        ElMessage.error(res.message || '修改失败')
      }
    } else {
      // 批量添加模式
      console.log('批量添加数据:', foods)
      // 确保所有必填字段都有值
      const validFoods = foods.filter(food => food.foodName && food.foodType)
      if (validFoods.length === 0) {
        ElMessage.error('没有有效的食物数据')
        return
      }
      const res = await addFoods(validFoods)
      if (res.code === 200) {
        ElMessage.success('批量添加成功')
        // 如果有新的食物类型，添加到列表并刷新
        if (newFoodTypes.size > 0) {
          foodTypes.value = [...foodTypes.value, ...newFoodTypes];
          console.log('新增食物类型:', newFoodTypes);
          console.log('更新后的食物类型列表:', foodTypes.value);
        }
        fetchFoodList()
      } else {
        ElMessage.error(res.message || '添加失败')
      }
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error(error.message || '操作失败')
  } finally {
    loading.value = false
    dialogVisible.value = false
  }
}

// 删除单个食物
const handleDelete = (food) => {
  ElMessageBox.confirm(
    `确定要删除食物"${food.foodName}"吗？此操作不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      loading.value = true
      const res = await deleteFoods([food.foodId])
    if (res.code === 200) {
      ElMessage.success('删除成功')
        fetchFoodList()
      } else {
        ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (!selectedFoods.value.length) {
    ElMessage.warning('请选择要删除的食物')
    return
  }

  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedFoods.value.length} 个食物吗？此操作不可恢复！`,
    '批量删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      loading.value = true
      const foodIds = selectedFoods.value.map(food => food.foodId)
      const res = await deleteFoods(foodIds)
      if (res.code === 200) {
        ElMessage.success('批量删除成功')
        selectedFoods.value = []
        fetchFoodList()
      } else {
        throw new Error(res.message || '批量删除失败')
      }
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error(error.message || '批量删除失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 添加批量修改方法
const handleBatchEdit = () => {
  if (!selectedFoods.value.length) {
    ElMessage.warning('请选择要修改的食物')
    return
  }

  editingFood.value = [...selectedFoods.value]
  dialogVisible.value = true
}

// 图表初始化函数
const initCharts = () => {
  initFoodTypesPieChart()
  initNutritionBarChart()
}

// 食物分类饼图
const initFoodTypesPieChart = () => {
  const chartDom = document.getElementById('foodTypesPieChart')
  if (!chartDom) {
    console.warn('未找到食物分类饼图容器')
    return
  }
  
  try {
    const chart = echarts.init(chartDom)
    // 通过 allFoodList 统计每个食物分类的数量
    let typeStats = {};
    allFoodList.value.forEach(food => {
      if (food.foodType) {
        typeStats[food.foodType] = (typeStats[food.foodType] || 0) + 1;
      }
    });

    let pieData = Object.entries(typeStats).map(([name, value]) => ({ name, value }));
    if (pieData.length === 0) {
      if (foodTypes.value.length > 0) {
        pieData = foodTypes.value.map(type => ({ name: type, value: 0 }));
      } else {
        pieData = [{ name: '暂无数据', value: 0 }];
      }
    }
    
    const option = {
      ...getChartThemeOption(currentTheme.value),
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: 10,
        top: 'center',
        textStyle: {
          color: currentTheme.value === 'dark' ? '#ffffff' : '#1f2937'
        }
      },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '16',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: pieData
      }]
    }
    
    chart.setOption(option)
  } catch (error) {
    console.error('初始化食物分类饼图失败:', error)
  }
}

// 营养成分柱状图
const initNutritionBarChart = () => {
  const chartDom = document.getElementById('nutritionBarChart')
  if (!chartDom) {
    console.warn('未找到营养成分柱状图容器')
    return
  }
  
  try {
    const chart = echarts.init(chartDom)
    const avgNutrition = {
      protein: 0,
      fat: 0,
      carbohydrates: 0,
      water: 0
    }
    
    let validFoodCount = 0
    foodList.value.forEach(food => {
      if (food.protein || food.fat || food.carbohydrates || food.water) {
        avgNutrition.protein += food.protein || 0
        avgNutrition.fat += food.fat || 0
        avgNutrition.carbohydrates += food.carbohydrates || 0
        avgNutrition.water += food.water || 0
        validFoodCount++
      }
    })
    
    if (validFoodCount > 0) {
      Object.keys(avgNutrition).forEach(key => {
        avgNutrition[key] = +(avgNutrition[key] / validFoodCount).toFixed(1)
      })
    }
    
    const option = {
      ...getChartThemeOption(currentTheme.value),
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '10%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: ['蛋白质', '脂肪', '碳水化合物', '水分']
      },
      yAxis: {
        type: 'value',
        name: '平均含量 (g/100g)',
        nameGap: 40,
        nameTextStyle: {
          fontSize: 14,
          color: currentTheme.value === 'dark' ? '#e5e7eb' : '#000000'
        }
      },
      series: [{
        name: '平均含量',
        type: 'bar',
        data: [
          avgNutrition.protein,
          avgNutrition.fat,
          avgNutrition.carbohydrates,
          avgNutrition.water
        ],
        itemStyle: {
          color: function(params) {
            const colors = ['#67C23A', '#E6A23C', '#409EFF', '#909399']
            return colors[params.dataIndex]
          }
        }
      }]
    }
    
    chart.setOption(option)
  } catch (error) {
    console.error('初始化营养成分柱状图失败:', error)
  }
}

// 新增 fetchAllFoodListForChart 方法，用于获取所有食物数据
const fetchAllFoodListForChart = async () => {
  try {
    // 传入一个足够大的 size 参数以获取所有记录，并传递空搜索条件以确保获取全部数据
    const res = await listFoodsByPage({ current: 1, size: 10000, foodName: '', foodType: '' })
    console.log('获取所有食物数据用于图表:', res)
    if (res.code === 200) {
      allFoodList.value = res.data.dataList || []
      nextTick(() => {
        initFoodTypesPieChart()
      })
    } else {
      ElMessage.error(res.message || '获取全部食物数据失败')
    }
  } catch (error) {
    console.error('获取全部食物数据失败:', error)
    ElMessage.error('获取全部食物数据失败')
  }
}

// 监听主题变化，重新渲染图表和更新样式
watch(isDarkMode, (newMode) => {
  nextTick(() => {
    // 重新初始化统计图表
    initCharts()
    
    // 重新初始化每个食物的营养图表
    foodList.value.forEach(food => {
      initNutritionChart(food)
    })
    
    // 动态切换全局样式类
    const foodManagementEl = document.querySelector('.food-management')
    if (foodManagementEl) {
      if (newMode) {
        foodManagementEl.classList.add('dark-mode')
        foodManagementEl.classList.remove('light-mode')
      } else {
        foodManagementEl.classList.remove('dark-mode')
        foodManagementEl.classList.add('light-mode')
      }
    }
  })
}, { immediate: true })

// 导出数据
const exportData = () => {
  const data = [
    ['食物名称', '分类', '热量(kcal/100g)', '蛋白质(g/100g)', '脂肪(g/100g)', '碳水化合物(g/100g)', '水分(ml/100g)']
  ]
  
  foodList.value.forEach(food => {
    data.push([
      food.foodName,
      food.foodType,
      food.calories,
      food.protein,
      food.fat,
      food.carbohydrates,
      food.water
    ])
  })
  
  const ws = XLSX.utils.aoa_to_sheet(data)
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, '食物数据')
  
  // 生成文件并下载
  XLSX.writeFile(wb, `食物数据_${new Date().toLocaleDateString()}.xlsx`)
}

// 显示导入对话框
const showImportDialog = () => {
  importDialogVisible.value = true
}

// 处理文件选择
const handleFileChange = (file) => {
  importFile.value = file.raw
}

// 下载导入模板
const downloadTemplate = () => {
  const template = [
    ['食物名称', '分类', '热量(kcal/100g)', '蛋白质(g/100g)', '脂肪(g/100g)', '碳水化合物(g/100g)', '水分(ml/100g)'],
    ['示例食物', '主食', 100, 2.5, 0.5, 23, 73]
  ]
  
  const ws = XLSX.utils.aoa_to_sheet(template)
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, '导入模板')
  XLSX.writeFile(wb, '食物数据导入模板.xlsx')
}

// 处理导入
const handleImport = async () => {
  if (!importFile.value) {
    ElMessage.warning('请先选择要导入的文件')
    return
  }
  
  importing.value = true
  try {
    const reader = new FileReader()
    reader.onload = async (e) => {
      const data = new Uint8Array(e.target.result)
      const workbook = XLSX.read(data, { type: 'array' })
      const worksheet = workbook.Sheets[workbook.SheetNames[0]]
      const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 })
      
      // 移除表头
      jsonData.shift()
      
      // 转换数据格式
      const foods = jsonData.map(row => ({
        foodName: row[0],
        foodType: row[1],
        calories: parseFloat(row[2]),
        protein: parseFloat(row[3]),
        fat: parseFloat(row[4]),
        carbohydrates: parseFloat(row[5]),
        water: parseFloat(row[6])
      }))
      
      // 批量添加
      const res = await addFoods(foods)
      if (res.code === 200) {
        ElMessage.success('导入成功')
        importDialogVisible.value = false
        fetchFoodList()
      } else {
        ElMessage.error(res.message || '导入失败，请检查文件格式是否正确')
      }
    }
    reader.readAsArrayBuffer(importFile.value)
  } catch (error) {
    console.error('导入失败:', error)
    ElMessage.error('导入失败，请检查文件格式是否正确')
  } finally {
    importing.value = false
  }
}

// 生命周期钩子
onMounted(() => {
  fetchFoodTypes()
  fetchFoodList()
  fetchAllFoodListForChart()
})
</script>

<style lang="scss" scoped>
.food-management {
  padding: 20px;

  .statistics-cards {
    margin-bottom: 20px;
    
    .stat-card {
      .stat-content {
  display: flex;
        align-items: center;

        .stat-icon {
          width: 48px;
          height: 48px;
          border-radius: 8px;
  display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16px;
          
          .el-icon {
            font-size: 24px;
            color: white;
          }
        }
        
        .stat-info {
          .stat-value {
            font-size: 24px;
            font-weight: bold;
            line-height: 1.2;
          }
          
          .stat-label {
            color: #909399;
            font-size: 14px;
          }
        }
      }
    }
  }

  .search-section {
    background: white;
    padding: 20px;
    border-radius: 4px;
    margin-bottom: 20px;
    
    .el-form {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      gap: 20px;
    }
  }

  .action-section {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  }

  .content-area {
    background: white;
    padding: 20px;
    border-radius: 4px;

    .food-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 20px;
      margin-bottom: 20px;

      .food-card {
        .food-card-header {
          padding: 15px;
          border-bottom: 1px solid #EBEEF5;
  display: flex;
          justify-content: space-between;
          align-items: center;

          h3 {
            margin: 0;
            font-size: 16px;
          }
        }

        .food-card-content {
          padding: 15px;

          .nutrition-chart {
            height: 200px;
            margin-bottom: 15px;
          }

          .nutrition-info {
            .nutrition-item {
  display: flex;
              justify-content: space-between;
              margin-bottom: 8px;

              .label {
                color: #909399;
              }

              .value {
                font-weight: 500;
              }
            }
          }
        }

        .food-card-actions {
          padding: 10px 15px;
          border-top: 1px solid #EBEEF5;
          display: flex;
          justify-content: flex-end;
        }
      }
    }
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }

  .nutrition-progress {
    .el-progress {
      margin-bottom: 5px;

      &:last-child {
        margin-bottom: 0;
      }
    }
  }

  .chart-section {
    margin-bottom: 20px;
    
    .chart-card {
      height: 400px;
      
      .chart-header {
  display: flex;
        justify-content: space-between;
        align-items: center;
      }
      
      .chart-container {
        height: 320px;
      }
    }
  }

  .import-dialog-content {
    .el-upload {
  width: 100%;
    }
    
    .template-download {
      margin-top: 16px;
      text-align: center;
    }
  }
}

/* 浅色模式 */
.food-management {
  transition: all 0.3s ease;
  background-color: #f0f2f5;
  color: #1f2937;
}



/* 深色模式 */
.food-management.dark-mode {
  background-color: #111827;
  color: #e5e7eb;
}

.food-management.dark-mode .stat-card {
  background-color: #1f2937;
  color: #e5e7eb;
  border: 1px solid #374151;
}

.food-management.dark-mode .chart-card {
  background-color: #1f2937;
  color: #e5e7eb;
  border: 1px solid #374151;
}

.food-management.dark-mode .el-input,
.food-management.dark-mode .el-select {
  background-color: #374151;
  color: #e5e7eb;
}

.food-management.dark-mode .el-table {
  background-color: #1f2937;
  color: #e5e7eb;
}

.food-management.dark-mode .el-table__header {
  background-color: #374151;
  color: #e5e7eb;
}

.food-management.dark-mode .el-pagination {
  background-color: #1f2937;
  color: #e5e7eb;
}

/* 搜索区域主题适配 */
.food-management.dark-mode .search-section {
  background-color: #1f2937;
  border: 1px solid #374151;
  
  .el-form-item__label {
    color: #e5e7eb;
  }
  
  .el-input__inner {
    background-color: #374151;
    border-color: #4b5563;
    color: #e5e7eb;
  }
  
  .el-input__inner::placeholder {
    color: #9ca3af;
  }
  
  .el-select__inner {
    background-color: #374151;
    border-color: #4b5563;
    color: #e5e7eb;
  }
  
  .el-button--primary {
    background-color: #2563eb;
    border-color: #2563eb;
  }
  
  .el-button--default {
    background-color: #374151;
    border-color: #4b5563;
    color: #e5e7eb;
  }
}

/* 操作按钮区域主题适配 */
.food-management.dark-mode .action-section {
  .el-button-group {
    .el-button {
      background-color: #374151;
      border-color: #4b5563;
      color: #e5e7eb;
      
      &:hover {
        background-color: #4b5563;
      }
      
      &.el-button--primary {
        background-color: #2563eb;
        border-color: #2563eb;
      }
      
      &.el-button--warning {
        background-color: #d97706;
        border-color: #d97706;
      }
      
      &.el-button--danger {
        background-color: #dc2626;
        border-color: #dc2626;
      }
      
      &.el-button--success {
        background-color: #16a34a;
        border-color: #16a34a;
      }
    }
  }
}

/* 食物卡片主题适配 */
.food-management.dark-mode .food-card {
  background-color: #1f2937;
  border: 1px solid #374151;
  color: #e5e7eb;
  
  .food-card-header {
    border-bottom-color: #374151;
    
    h3 {
      color: #e5e7eb;
    }
  }
  
  .nutrition-item {
    .label {
      color: #9ca3af;
    }
    
    .value {
      color: #e5e7eb;
    }
  }
  
  .food-card-actions {
    border-top-color: #374151;
    
    .el-button {
      color: #e5e7eb;
      
      &:hover {
        color: #3b82f6;
      }
    }
  }
}

/* 修改夜间模式下分页器样式 */
.food-management.dark-mode .pagination-container {
  :deep(.el-pagination) {
    background-color: #1f2937;
    color: #e5e7eb;
  }
  :deep(.el-pager li) {
    background-color: #374151;
    color: #ffffff;
    border-color: #4b5563;
  }
  :deep(.el-pager li.active) {
    background-color: #2563eb;
    color: white;
  }
  :deep(.el-pager li:hover) {
    background-color: #4b5563;
    color: white;
  }

  /* 为 'Go to' 文本设置亮白色 */
  :deep(.el-pagination__jump) {
    color: #ffffff !important;
  }

  /* 翻页箭头按钮修改，更新选择器，匹配同时具有 el-pagination__btn 和 el-pagination__prev / el-pagination__next */
  :deep(.el-pagination__btn.el-pagination__prev),
  :deep(.el-pagination__btn.el-pagination__next) {
    background-color: #374151 !important;
    border: 1px solid #4b5563 !important;
    color: #565050 !important;
    border-radius: 4px !important;
    margin: 0 4px !important;
  }
  :deep(.el-pagination__btn.el-pagination__prev:hover),
  :deep(.el-pagination__btn.el-pagination__next:hover) {
    background-color: #2563eb !important;
    border-color: #2563eb !important;
    color: white !important;
  }
  :deep(.el-pagination__btn.el-pagination__prev i),
  :deep(.el-pagination__btn.el-pagination__next i) {
    color: inherit !important;
  }
  /* 为内部 SVG 图标设置 fill 颜色 */
  :deep(.el-pagination__btn.el-pagination__prev svg),
  :deep(.el-pagination__btn.el-pagination__next svg) {
    fill: currentColor !important;
  }
  /* 确保禁用状态下的翻页箭头按钮也应用自定义样式 */
  :deep(.el-pagination__btn.el-pagination__prev.is-disabled),
  :deep(.el-pagination__btn.el-pagination__next.is-disabled) {
    background-color: #374151 !important;
    border: 1px solid #4b5563 !important;
    color: #565050 !important;
    opacity: 1 !important;
  }
  
  /* 新增：为翻页的第一和最后按钮设置样式 */
  :deep(.el-pagination__btn.el-pagination__btn--first),
  :deep(.el-pagination__btn.el-pagination__btn--last) {
    background-color: #374151 !important;
    border: 1px solid #4b5563 !important;
    color: #565050 !important;
    border-radius: 4px !important;
    margin: 0 4px !important;
  }
  :deep(.el-pagination__btn.el-pagination__btn--first:hover),
  :deep(.el-pagination__btn.el-pagination__btn--last:hover) {
    background-color: #2563eb !important;
    border-color: #2563eb !important;
    color: white !important;
  }
  :deep(.el-pagination__btn.el-pagination__btn--first i),
  :deep(.el-pagination__btn.el-pagination__btn--last i) {
    color: inherit !important;
  }
  :deep(.el-pagination__btn.el-pagination__btn--first svg),
  :deep(.el-pagination__btn.el-pagination__btn--last svg) {
    fill: currentColor !important;
  }
  :deep(.el-pagination__btn.el-pagination__btn--first.is-disabled),
  :deep(.el-pagination__btn.el-pagination__btn--last.is-disabled) {
    background-color: #374151 !important;
    border: 1px solid #4b5563 !important;
    color: #565050 !important;
    opacity: 1 !important;
  }
  
  /* 新增：设置分页器 Total 文本在夜间模式下为亮白色 */
  :deep(.el-pagination__total) {
    color: #ffffff !important;
  }
}

/* 对话框主题适配 */
.food-management.dark-mode .el-dialog {
  background-color: #1f2937;
  color: #ffffff;
  
  .el-dialog__header {
    background-color: #374151;
    border-bottom: 1px solid #4b5563;
    
    .el-dialog__title {
      color: #e5e7eb;
    }
  }
  
  .el-dialog__body {
    background-color: #1f2937;
    
    .el-form-item__label {
      color: #e5e7eb;
    }
    
    .el-input__inner,
    .el-select__inner {
      background-color: #374151;
      border-color: #4b5563;
      color: #e5e7eb;
    }
  }
  
  .el-dialog__footer {
    background-color: #374151;
    border-top: 1px solid #4b5563;
    
    .el-button {
      background-color: #374151;
      border-color: #4b5563;
      color: #e5e7eb;
      
      &.el-button--primary {
        background-color: #2563eb;
        border-color: #2563eb;
      }
    }
  }
}

/* 标签主题适配 */
.food-management.dark-mode .el-tag {
  background-color: rgba(59, 130, 246, 0.2);
  color: #93c5fd;
  border-color: rgba(59, 130, 246, 0.4);
  
  &.el-tag--success {
    background-color: rgba(34, 197, 94, 0.2);
    color: #6ee7b7;
    border-color: rgba(34, 197, 94, 0.4);
  }
  
  &.el-tag--warning {
    background-color: rgba(245, 158, 11, 0.2);
    color: #fcd34d;
    border-color: rgba(245, 158, 11, 0.4);
  }
  
  &.el-tag--danger {
    background-color: rgba(239, 68, 68, 0.2);
    color: #fca5a5;
    border-color: rgba(239, 68, 68, 0.4);
  }
}

/* 进度条主题适配 */
.food-management.dark-mode .nutrition-progress {
  .el-progress {
    .el-progress-bar__outer {
      background-color: #374151;
    }
    
    .el-progress-bar__inner {
      background-color: #2563eb;
    }
    
    .el-progress__text {
      color: #e5e7eb;
    }
  }
}

/* 视图模式主题适配 */
.food-management.dark-mode .content-area {
  background-color: #111827; // 深色背景
  border: 1px solid #374151;
}

.food-management.dark-mode .content-area .food-grid {
  background-color: #111827; // 深色背景
}

.food-management.dark-mode .content-area .el-table {
  background-color: #111827 !important;
  color: #e5e7eb !important;
  border-color: #374151 !important;

  /* 表头样式 */
  .el-table__header {
    background-color: #1f2937 !important;
    color: #e5e7eb !important;

    th {
      background-color: #1f2937 !important;
      color: #e5e7eb !important;
      border-color: #374151 !important;

      &.is-sortable:hover {
        background-color: #374151 !important;
      }
    }
  }

  /* 表格行样式 - 默认和悬停状态 */
  .el-table__row {
    background-color: #111827 !important;
    border-color: #374151 !important;
    color: #e5e7eb !important;

    /* 默认状态下的样式 */
    &:not(:hover) {
      background-color: #111827 !important;
      color: #e5e7eb !important;
    }

    /* 悬停状态下的样式 */
    &:hover {
      background-color: #1f2937 !important;
      color: #e5e7eb !important;
    }

    td {
      background-color: #111827 !important;
      border-color: #374151 !important;
      color: #e5e7eb !important;
    }
  }

  /* 确保所有行都有一致的样式 */
  .el-table__row--striped {
    &:nth-child(even) {
      background-color: #111827 !important;
    }
    &:nth-child(odd) {
      background-color: #111827 !important;
    }
  }

  /* 单选/多选列 */
  .el-table-column--selection {
    .cell {
      background-color: #1f2937 !important;

      .el-checkbox {
        .el-checkbox__input {
          .el-checkbox__inner {
            background-color: #374151 !important;
            border-color: #4b5563 !important;

            &::after {
              border-color: #e5e7eb !important;
            }
          }

          &.is-checked {
            .el-checkbox__inner {
              background-color: #2563eb !important;
              border-color: #2563eb !important;
            }
          }
        }
      }
    }
  }

  /* 操作列按钮 */
  .el-table-column--action {
    .el-button {
      color: #e5e7eb !important;

      &:hover {
        color: #3b82f6 !important;
      }
    }
  }

  /* 分页器 */
  .el-table__pagination {
    background-color: #1f2937;
    color: #e5e7eb;
  }
}

/* 列表视图标签样式 */
.food-management.dark-mode .el-table .el-tag {
  background-color: rgba(59, 130, 246, 0.2) !important;
  color: #93c5fd !important;
  border-color: rgba(59, 130, 246, 0.4) !important;

  &.el-tag--success {
    background-color: rgba(34, 197, 94, 0.2) !important;
    color: #6ee7b7 !important;
    border-color: rgba(34, 197, 94, 0.4) !important;
  }

  &.el-tag--warning {
    background-color: rgba(245, 158, 11, 0.2) !important;
    color: #fcd34d !important;
    border-color: rgba(245, 158, 11, 0.4) !important;
  }

  &.el-tag--danger {
    background-color: rgba(239, 68, 68, 0.2) !important;
    color: #fca5a5 !important;
    border-color: rgba(239, 68, 68, 0.4) !important;
  }
}

/* 列表视图营养成分进度条 */
.food-management.dark-mode .nutrition-progress {
  .el-progress {
    .el-progress-bar__outer {
      background-color: #374151 !important;
    }

    .el-progress-bar__inner {
      &.el-progress-bar__inner--success {
        background-color: #16a34a !important;
      }

      &.el-progress-bar__inner--warning {
        background-color: #d97706 !important;
      }

      &.el-progress-bar__inner--primary {
        background-color: #2563eb !important;
      }
    }

    .el-progress__text {
      color: #e5e7eb !important;
    }
  }
}

/* 深度夜间主题列表视图样式 */
.food-management.dark-mode {
  /* 全局表格样式覆盖 */
  .el-table {
    --el-table-bg-color: #111827 !important;
    --el-table-header-bg-color: #1f2937 !important;
    --el-table-row-hover-bg-color: #1f2937 !important;
    --el-table-border-color: #374151 !important;
    --el-table-text-color: #e5e7eb !important;
    --el-table-header-text-color: #e5e7eb !important;

    background-color: #111827 !important;
    color: #e5e7eb !important;
    border-color: #374151 !important;

    /* 表头样式 */
    .el-table__header {
      background-color: #1f2937 !important;
      color: #e5e7eb !important;

      th {
        background-color: #1f2937 !important;
        color: #e5e7eb !important;
        border-color: #374151 !important;

        &.is-sortable:hover {
          background-color: #374151 !important;
        }
      }
    }

    /* 表格行样式 - 默认和悬停状态 */
    .el-table__row {
      background-color: #111827 !important;
      border-color: #374151 !important;
      color: #e5e7eb !important;

      /* 默认状态下的样式 */
      &:not(:hover) {
        background-color: #111827 !important;
        color: #e5e7eb !important;
      }

      /* 悬停状态下的样式 */
      &:hover {
        background-color: #1f2937 !important;
        color: #e5e7eb !important;
      }

      td {
        background-color: #111827 !important;
        border-color: #374151 !important;
        color: #e5e7eb !important;
      }
    }

    /* 确保所有行都有一致的样式 */
    .el-table__row--striped {
      &:nth-child(even) {
        background-color: #111827 !important;
      }
      &:nth-child(odd) {
        background-color: #111827 !important;
      }
    }

    /* 单选/多选列 */
    .el-table-column--selection {
      .cell {
        background-color: #1f2937 !important;

        .el-checkbox {
          .el-checkbox__input {
            .el-checkbox__inner {
              background-color: #374151 !important;
              border-color: #4b5563 !important;

              &::after {
                border-color: #e5e7eb !important;
              }
            }

            &.is-checked {
              .el-checkbox__inner {
                background-color: #2563eb !important;
                border-color: #2563eb !important;
              }
            }
          }
        }
      }
    }
  }

  /* 标签样式 */
  .el-table .el-tag {
    background-color: rgba(59, 130, 246, 0.2) !important;
    color: #93c5fd !important;
    border-color: rgba(59, 130, 246, 0.4) !important;

    &.el-tag--success {
      background-color: rgba(34, 197, 94, 0.2) !important;
      color: #6ee7b7 !important;
      border-color: rgba(34, 197, 94, 0.4) !important;
    }

    &.el-tag--warning {
      background-color: rgba(245, 158, 11, 0.2) !important;
      color: #fcd34d !important;
      border-color: rgba(245, 158, 11, 0.4) !important;
    }

    &.el-tag--danger {
      background-color: rgba(239, 68, 68, 0.2) !important;
      color: #fca5a5 !important;
      border-color: rgba(239, 68, 68, 0.4) !important;
    }
  }

  /* 营养成分进度条 */
  .nutrition-progress .el-progress {
    .el-progress-bar__outer {
      background-color: #374151 !important;
    }

    .el-progress-bar__inner {
      &.el-progress-bar__inner--success {
        background-color: #16a34a !important;
      }

      &.el-progress-bar__inner--warning {
        background-color: #d97706 !important;
      }

      &.el-progress-bar__inner--primary {
        background-color: #2563eb !important;
      }
    }

    .el-progress__text {
      color: #e5e7eb !important;
    }
  }

  /* 操作列按钮 */
  .el-table-column--action .el-button {
    color: #e5e7eb !important;

    &:hover {
      color: #3b82f6 !important;
    }
  }
}

/* 全局 Element Plus 夜间主题覆盖 */
body.dark-mode {
  .el-table {
    background-color: #111827 !important;
    color: #e5e7eb !important;
  }

  .el-table__header {
    background-color: #1f2937 !important;
    color: #e5e7eb !important;
  }

  .el-table__row {
    background-color: #111827 !important;
    color: #e5e7eb !important;

    &:hover {
      background-color: #1f2937 !important;
    }
  }

  .el-tag {
    background-color: rgba(59, 130, 246, 0.2) !important;
    color: #93c5fd !important;
    border-color: rgba(59, 130, 246, 0.4) !important;
  }
}

/* 针对列表视图的夜间和日间主题调整 */
:deep(.dark-mode .el-table th),
:deep(.dark-mode .el-table td) {
  background-color: #1f2937 !important;
  color: #e5e7eb !important;
}

:deep(.light-mode .el-table th),
:deep(.light-mode .el-table td) {
  background-color: #ffffff !important;
  color: #1f2937 !important;
}

/* 根据修改后的示例代码，修正列表视图行数据的夜间/日间主题样式 */
:deep(.dark-mode .el-table__row) {
  background-color: #111827 !important;
  color: #e5e7eb !important;
}
:deep(.dark-mode .el-table__row:hover) {
  background-color: #1f2937 !important;
}
:deep(.light-mode .el-table__row) {
  background-color: #ffffff !important;
  color: #1f2937 !important;
}
:deep(.light-mode .el-table__row:hover) {
  background-color: #f5f5f5 !important;
}

/* 保留或根据需要调整 el-tag 的样式 */
:deep(.el-tag) {
  background-color: rgba(59, 130, 246, 0.2) !important;
  color: #93c5fd !important;
  border-color: rgba(59, 130, 246, 0.4) !important;
}

/* 新增: 将营养进度条文字颜色设置为亮白色，并增加表格行单元格的上下内边距 */
:deep(.nutrition-progress .el-progress__text) {
  color: #ffffff !important;
}

:deep(.el-table__cell) {
  padding-top: 20px !important;
  padding-bottom: 20px !important;
}

/* 新增: 调整搜索区域标签颜色及下拉框宽度 */
.food-management.dark-mode .search-section .el-form-item__label {
  color: #ffffff !important;
}

.search-section .el-select {
  min-width: 200px !important;
}

/* 在style块的末尾新增全局 CSS 规则，用于夜间模式下调整 food-dialog 的宽度和层级 */
:global(.food-dialog-custom) .el-dialog {
  width: 400px !important;
}
:global(.food-dialog-custom) .el-dialog__wrapper {
  z-index: 9999 !important;
}

/* 确保下拉菜单能够正确显示在对话框上层 */
:global(.el-select__popper) {
  z-index: 10000 !important;
}

/* 针对对话框中的下拉菜单进行特殊处理 */
:global(.el-select-dropdown) {
  z-index: 10000 !important;
  position: absolute !important;
}

/* 确保下拉菜单的定位容器允许溢出 */
:global(.el-dialog__body) {
  overflow: visible !important;
}

/* 确保表单项容器允许溢出 */
:global(.el-form-item__content) {
  overflow: visible !important;
}

/* 新增: 非夜间模式下调整食物营养信息数据列表中文本颜色 */
.food-management.light-mode :deep(.nutrition-progress .el-progress__text) {
  color: #000000 !important;
}

.food-management.light-mode :deep(.el-table .el-tag) {
  color: #1f2937 !important;
}

/* 添加新的营养成分文本显示样式 */
.nutrition-text-display {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 8px 0;
  
  .nutrition-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px 8px;
    border-radius: 4px;
    background-color: rgba(0, 0, 0, 0.02);
    
    &:nth-child(1) {
      background-color: rgba(103, 194, 58, 0.1);
    }
    
    &:nth-child(2) {
      background-color: rgba(230, 162, 60, 0.1);
    }
    
    &:nth-child(3) {
      background-color: rgba(64, 158, 255, 0.1);
    }
    
    &:nth-child(4) {
      background-color: rgba(144, 147, 153, 0.1);
    }
    
    .nutrition-label {
      font-weight: 500;
      color: var(--el-text-color-secondary);
      margin-right: 8px;
    }
    
    .nutrition-value {
      font-weight: 600;
    }
  }
}

/* 深色模式下的营养成分文本样式 */
.dark-mode {
  .nutrition-text-display {
    .nutrition-item {
      background-color: rgba(255, 255, 255, 0.05);
      
      &:nth-child(1) {
        background-color: rgba(103, 194, 58, 0.15);
      }
      
      &:nth-child(2) {
        background-color: rgba(230, 162, 60, 0.15);
      }
      
      &:nth-child(3) {
        background-color: rgba(64, 158, 255, 0.15);
      }
      
      &:nth-child(4) {
        background-color: rgba(144, 147, 153, 0.15);
      }
      
      .nutrition-label {
        color: rgba(229, 231, 235, 0.8);
      }
      
      .nutrition-value {
        color: #ffffff;
      }
    }
  }
}
</style> 