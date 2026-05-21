<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="logo">🍰</div>
        <h1>甜点管理系统</h1>
        <p>欢迎回来，请登录您的账号</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            prefix-icon="User"
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-button
          type="primary"
          size="large"
          :loading="loading"
          @click="handleLogin"
          class="login-btn"
        >
          登录
        </el-button>

        <div class="login-footer">
          <span class="demo" @click="fillDemo">演示账号</span>
          <router-link to="/register" class="register">注册账号</router-link>
        </div>
      </el-form>

      <div class="demo-users">
        <el-tag size="small" @click="fillAdmin">管理员: admin / admin123</el-tag>
        <el-tag size="small" @click="fillUser">普通用户: user1 / 123456</el-tag>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api/user'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const fillAdmin = () => {
  form.username = 'admin'
  form.password = 'admin123'
}

const fillUser = () => {
  form.username = 'user1'
  form.password = '123456'
}

const fillDemo = () => {
  fillAdmin()
}

const handleLogin = async () => {
  try {
    await formRef.value?.validate()

    loading.value = true

    console.log('=== 开始登录 ===')
    console.log('用户名:', form.username)

    const res: any = await userApi.login(form)

    console.log('=== 登录响应完整数据 ===')
    console.log('响应:', res)
    console.log('code:', res.code)
    console.log('message:', res.message)
    console.log('data:', res.data)

    if (res.code === 200) {
      // 检查数据结构
      if (res.data) {
        console.log('token:', res.data.token)
        console.log('user:', res.data.user)

        // 保存用户信息
        localStorage.setItem('user', JSON.stringify(res.data.user))

        // 如果后端返回 token，也保存
        if (res.data.token) {
          localStorage.setItem('token', res.data.token)
        }

        ElMessage.success('登录成功')

        console.log('准备跳转到首页...')
        // 跳转到首页
        await router.push('/')
        console.log('跳转完成')
      } else {
        console.error('响应中没有 data 字段')
        ElMessage.error('登录响应数据格式错误')
      }
    } else {
      console.error('登录失败:', res.message)
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error: any) {
    console.error('=== 登录错误 ===')
    console.error('错误对象:', error)
    console.error('错误信息:', error.message)
    console.error('响应数据:', error.response?.data)
    console.error('状态码:', error.response?.status)

    const errorMsg = error.response?.data?.message || error.message || '登录失败'
    ElMessage.error(errorMsg)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-card {
  width: 450px;
  background: white;
  border-radius: 32px;
  padding: 48px 40px;
  box-shadow: 0 40px 60px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  font-size: 56px;
  margin-bottom: 16px;
}

.login-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
}

.login-header p {
  color: #999;
  font-size: 14px;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  margin-top: 8px;
}

.login-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 24px;
  font-size: 14px;
}

.demo {
  color: #999;
  cursor: pointer;
}

.demo:hover {
  color: #667eea;
}

.register {
  color: #667eea;
  text-decoration: none;
}

.demo-users {
  margin-top: 30px;
  padding-top: 24px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.demo-users .el-tag {
  cursor: pointer;
}
</style>
