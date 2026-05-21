<template>
  <div class="container">
    <h2>📁 分类管理</h2>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="queryParams.name" placeholder="分类名称" prefix-icon="Search" style="width: 200px" clearable @keyup.enter="handleQuery" />
      <el-input v-model="queryParams.descp" placeholder="分类描述" prefix-icon="Document" style="width: 200px" clearable @keyup.enter="handleQuery" />
      <el-button type="primary" @click="handleQuery">查询</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <!-- 操作按钮 -->
    <div class="toolbar">
      <el-button type="danger" @click="handleDeleteBatch" :disabled="selectedIds.length === 0">
        <el-icon><Delete /></el-icon> 批量删除
      </el-button>
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon> 新增分类
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="list" v-loading="loading" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="name" label="分类名称" min-width="150" />
      <el-table-column prop="descp" label="分类描述" min-width="250" />
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEditDialog(row)">
            <el-icon><Edit /></el-icon> 编辑
          </el-button>
          <el-button type="danger" link @click="handleDelete(row.id)">
            <el-icon><Delete /></el-icon> 删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        :current-page="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        :total="total"
        layout="total, prev, pager, next, jumper"
        @current-change="handleCurrentPageChange"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="450px" @close="closeDialog">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类描述" prop="descp">
          <el-input v-model="form.descp" type="textarea" rows="3" placeholder="请输入分类描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
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

interface Category {
  id?: number
  name: string
  descp: string
}

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  name: '',
  descp: ''
})

const list = ref<Category[]>([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref<number[]>([])

const dialogVisible = ref(false)
const dialogTitle = ref('新增分类')
const isEdit = ref(false)
const formRef = ref()
const form = ref<Category>({ name: '', descp: '' })

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res: any = await CategoryService.list(queryParams.value)
    list.value = res.rows || []
    total.value = res.total || 0
  } catch {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

const handleCurrentPageChange = (page: number) => {
  queryParams.value.pageNum = page
  getList()
}

const handleQuery = () => {
  queryParams.value.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.value.name = ''
  queryParams.value.descp = ''
  handleQuery()
}

const handleSelectionChange = (selection: Category[]) => {
  selectedIds.value = selection.map(item => item.id!)
}

const openAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '新增分类'
  form.value = { name: '', descp: '' }
  dialogVisible.value = true
}

const openEditDialog = (row: Category) => {
  isEdit.value = true
  dialogTitle.value = '编辑分类'
  form.value = { ...row }
  dialogVisible.value = true
}

const closeDialog = () => {
  formRef.value?.resetFields()
}

const save = async () => {
  try {
    await formRef.value?.validate()
    if (isEdit.value) {
      await CategoryService.edit(form.value)
      ElMessage.success('编辑成功')
    } else {
      await CategoryService.add(form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (id: number) => {
  ElMessageBox.confirm('确定删除该分类吗？', '提示', { type: 'warning' }).then(async () => {
    await CategoryService.del(id)
    ElMessage.success('删除成功')
    getList()
  }).catch(() => {})
}

const handleDeleteBatch = () => {
  if (selectedIds.value.length === 0) return
  ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 个分类吗？`, '提示', { type: 'warning' }).then(async () => {
    await CategoryService.del(selectedIds.value.join(','))
    ElMessage.success('删除成功')
    getList()
  }).catch(() => {})
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
h2 { margin-bottom: 20px; color: #333; }
.search-bar { background: white; padding: 20px; border-radius: 12px; margin-bottom: 20px; display: flex; gap: 12px; flex-wrap: wrap; align-items: center; }
.toolbar { margin-bottom: 20px; display: flex; gap: 12px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
