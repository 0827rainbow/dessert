<template>
  <div class="layout">
    <header class="header">
      <div class="header-container">
        <div class="logo" @click="$router.push('/home')">
          <span>🍰</span>
          <span>甜点管理系统</span>
        </div>
        <nav class="nav">
          <router-link to="/home">🏠 首页</router-link>
          <router-link to="/dessert-list">🍰 甜点商城</router-link>
          <router-link to="/cart" class="cart-link">
            🛒 购物车
            <span v-if="cartCount" class="badge">{{ cartCount }}</span>
          </router-link>
          <router-link to="/orders">📦 我的订单</router-link>
          <template v-if="isAdmin">
            <span class="divider"></span>
            <router-link to="/category">📁 分类管理</router-link>
            <router-link to="/dessert-manage">🍮 甜点管理</router-link>
          </template>
        </nav>
        <div class="user">
          <el-dropdown>
            <span class="user-name">{{ username }} ▼</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>
    <main class="main"><router-view /></main>
    <footer class="footer">© 2026 甜点管理系统 | 用心制作每一份甜蜜</footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cartApi } from '@/api/cart'

const router = useRouter()
const cartCount = ref(0)
const user = JSON.parse(localStorage.getItem('user') || '{}')
const username = ref(user.nickname || user.username || '用户')
const isAdmin = computed(() => user.role === 1)

const fetchCartCount = async () => {
  try {
    const res = await cartApi.getCount()
    cartCount.value = res.data?.count || 0
  } catch {}
}

const logout = () => {
  ElMessageBox.confirm('确定退出登录？', '提示').then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    ElMessage.success('已退出')
    router.push('/login')
  }).catch(() => {})
}

onMounted(() => {
  fetchCartCount()
  window.addEventListener('cart-updated', fetchCartCount)
})
</script>

<style scoped>
.layout { min-height: 100vh; display: flex; flex-direction: column; }
.header { background: linear-gradient(135deg, #2c3e50, #1a252f); color: white; position: sticky; top: 0; z-index: 100; }
.header-container { max-width: 1400px; margin: 0 auto; padding: 0 30px; height: 64px; display: flex; align-items: center; justify-content: space-between; }
.logo { display: flex; align-items: center; gap: 10px; font-size: 20px; font-weight: 600; cursor: pointer; }
.nav { display: flex; gap: 8px; }
.nav a { color: rgba(255,255,255,0.8); text-decoration: none; padding: 8px 16px; border-radius: 40px; transition: all 0.3s; }
.nav a:hover, .nav a.router-link-active { background: linear-gradient(135deg, #667eea, #764ba2); color: white; }
.cart-link { position: relative; }
.badge { position: absolute; top: -4px; right: -4px; background: #ff6b6b; color: white; font-size: 10px; padding: 0 4px; border-radius: 10px; }
.divider { width: 1px; height: 24px; background: rgba(255,255,255,0.2); margin: 0 8px; }
.user-name { cursor: pointer; padding: 8px 12px; border-radius: 40px; }
.user-name:hover { background: rgba(255,255,255,0.1); }
.main { flex: 1; padding: 30px 0; background: #f5f7fa; }
.footer { background: #2c3e50; color: rgba(255,255,255,0.6); text-align: center; padding: 24px; font-size: 14px; }
</style>
