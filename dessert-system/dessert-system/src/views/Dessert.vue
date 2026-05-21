<template>
  <div class="container">
    <h2>🍮 甜点管理</h2>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchForm.name" placeholder="甜点名称" style="width: 200px" clearable @keyup.enter="handleQuery" />
      <el-select v-model="searchForm.catId" placeholder="分类筛选" clearable style="width: 150px">
        <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-select v-model="searchForm.status" placeholder="状态筛选" clearable style="width: 120px">
        <el-option label="全部" :value="null" />
        <el-option label="上架" :value="1" />
        <el-option label="下架" :value="0" />
      </el-select>
      <el-button type="primary" @click="handleQuery">查询</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <!-- 操作按钮 -->
    <div class="toolbar">
      <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleDeleteBatch">批量删除</el-button>
      <el-button type="primary" @click="openAddDialog">新增甜点</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column label="图片" width="80" align="center">
        <template #default="{ row }">
          <img v-if="row.photoUrl" :src="imgUrl + row.photoUrl" class="table-img" />
          <span v-else>无图</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="甜点名称" min-width="120" />
      <el-table-column prop="categoryName" label="所属分类" width="100" />
      <el-table-column prop="price" label="价格" width="100" align="center">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" align="center" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="{ row }">
          <span :style="{ color: row.status === 1 ? '#67c23a' : '#f56c6c' }">
            {{ row.status === 1 ? '上架' : '下架' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        :current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next, jumper"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="甜点名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入甜点名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属分类" prop="catId">
              <el-select v-model="formData.catId" placeholder="请选择分类" style="width: 100%">
                <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input v-model="formData.price" type="number" placeholder="请输入价格">
                <template #prepend>¥</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input v-model="formData.stock" type="number" placeholder="请输入库存" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="甜点图片">
          <el-upload :action="uploadUrl" :on-success="handleUploadSuccess" :before-upload="beforeUpload">
            <el-button>上传图片</el-button>
          </el-upload>
          <img v-if="formData.photoUrl" :src="imgUrl + formData.photoUrl" style="width: 80px; margin-top: 10px" />
        </el-form-item>
        <el-form-item label="甜点描述">
          <el-input v-model="formData.descp" type="textarea" rows="3" placeholder="请输入甜点描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveData">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dessertApi } from '@/api/dessert'
import { CategoryService } from '@/api/CategoryService'

// ========== 权限验证（放在最顶部） ==========
const user = JSON.parse(localStorage.getItem('user') || '{}')
const isAdmin = computed(() => user.role === 1)

// 非管理员跳转
if (!isAdmin.value) {
  ElMessage.warning('您没有权限访问该页面')
  window.location.href = '/dessert-list'
}
// ========== 权限验证结束 ==========

const imgUrl = 'http://localhost:8080'
const uploadUrl = imgUrl + '/dessert/upload'

// 搜索表单
const searchForm = ref({
  name: '',
  catId: null,
  status: null
})

// 分页
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 数据
const tableData = ref([])
const loading = ref(false)
const selectedIds = ref([])
const categories = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增甜点')
const isEdit = ref(false)
const formRef = ref()
const formData = ref({
  id: 0,
  name: '',
  descp: '',
  photoUrl: '',
  price: 0,
  stock: 0,
  status: 1,
  catId: 0
})

// 验证规则
const rules = {
  name: [{ required: true, message: '请输入甜点名称', trigger: 'blur' }],
  catId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

// 获取分类列表
const getCategories = async () => {
  try {
    const res = await CategoryService.list({ pageNum: 1, pageSize: 100 })
    categories.value = res.rows || []
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

// 获取甜点列表
const getList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      name: searchForm.value.name || '',
      catId: searchForm.value.catId || '',
      status: searchForm.value.status || ''
    }
    const res = await dessertApi.getList(params)
    tableData.value = res.rows || []
    total.value = res.total || 0
  } catch (error) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

// 分页切换
const handlePageChange = (page) => {
  pageNum.value = page
  getList()
}

// 查询
const handleQuery = () => {
  pageNum.value = 1
  getList()
}

// 重置
const resetQuery = () => {
  searchForm.value = { name: '', catId: null, status: null }
  handleQuery()
}

// 多选
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 打开新增对话框
const openAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '新增甜点'
  formData.value = { id: 0, name: '', descp: '', photoUrl: '', price: 0, stock: 0, status: 1, catId: 0 }
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑甜点'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 图片上传前校验
const beforeUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isImage) {
    ElMessage.error('只能上传 JPG/PNG 格式图片')
    return false
  }
  return true
}

// 图片上传成功
const handleUploadSuccess = (res) => {
  formData.value.photoUrl = res.data || res
  ElMessage.success('图片上传成功')
}

// 保存数据
const saveData = async () => {
  try {
    await formRef.value?.validate()
    if (isEdit.value) {
      await dessertApi.update(formData.value)
      ElMessage.success('编辑成功')
    } else {
      await dessertApi.add(formData.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await getList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 删除单个
const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该甜点吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      await dessertApi.delete(id)
      ElMessage.success('删除成功')
      await getList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 批量删除
const handleDeleteBatch = () => {
  if (selectedIds.value.length === 0) return
  ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 个甜点吗？`, '提示', { type: 'warning' }).then(async () => {
    try {
      const ids = selectedIds.value.join(',')
      await dessertApi.delete(ids)
      ElMessage.success('删除成功')
      await getList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  getCategories()
  getList()
})
</script>

<style scoped>
.container { max-width: 1400px; margin: 0 auto; padding: 0 20px; }
h2 { margin-bottom: 20px; color: #333; }
.search-bar { background: white; padding: 20px; border-radius: 12px; margin-bottom: 20px; display: flex; gap: 12px; flex-wrap: wrap; align-items: center; }
.toolbar { margin-bottom: 20px; display: flex; gap: 12px; }
.table-img { width: 50px; height: 50px; object-fit: cover; border-radius: 8px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
