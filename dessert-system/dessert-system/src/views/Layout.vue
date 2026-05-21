<template>
  <div class="layout">
    <header class="header">
      <div class="header-container">
        <div class="logo" @click="$router.push('/')">
          <span>🍰</span>
          <span>甜点管理系统</span>
        </div>
        <nav class="nav">
          <router-link to="/">🏠 首页</router-link>
          <router-link to="/dessert-list">🍰 甜点商城</router-link>
          <router-link to="/cart" class="cart-link">
            🛒 购物车
            <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
          </router-link>
          <router-link to="/orders">📦 我的订单</router-link>

          <!-- 仅管理员可见 -->
          <template v-if="isAdmin">
            <span class="divider"></span>
            <router-link to="/category">📁 分类管理</router-link>
            <router-link to="/dessert">🍮 甜点管理</router-link>
          </template>
        </nav>
        <div class="user">
          <el-dropdown>
            <span class="user-name">
              <el-icon><User /></el-icon>
              {{ username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>
                  <span v-if="isAdmin" class="admin-badge">👑 管理员</span>
                  <span v-else class="user-badge">👤 普通用户</span>
                </el-dropdown-item>
                <el-dropdown-item divided @click="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>
    <main class="main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <footer class="footer">
      <p>© 2026 甜点管理系统 | 用心制作每一份甜蜜</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cartApi } from '@/api/cart'

const router = useRouter()
const cartCount = ref(0)

// 从 localStorage 获取用户信息（响应式）
const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const username = computed(() => user.value.nickname || user.value.username || '用户')
const isAdmin = computed(() => user.value.role === 1)

// 获取购物车数量
const fetchCartCount = async () => {
  try {
    const res: any = await cartApi.getCount()
    cartCount.value = res.data?.count || 0
  } catch (error) {
    console.error('获取购物车数量失败', error)
  }
}

// 监听购物车更新事件
const handleCartUpdated = () => {
  fetchCartCount()
}

// 监听 localStorage 变化（当其他页面更新用户信息时）
const handleStorageChange = () => {
  const newUser = JSON.parse(localStorage.getItem('user') || '{}')
  user.value = newUser
}

// 退出登录
const logout = () => {
  ElMessageBox.confirm('确定退出登录吗？', '退出确认', {
    confirmButtonText: '确定退出',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    ElMessage.success('已安全退出')
    router.push('/login')
  }).catch(() => {})
}

onMounted(() => {
  fetchCartCount()
  window.addEventListener('cart-updated', handleCartUpdated)
  window.addEventListener('storage', handleStorageChange)
})

onUnmounted(() => {
  window.removeEventListener('cart-updated', handleCartUpdated)
  window.removeEventListener('storage', handleStorageChange)
})
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #2c3e50, #1a252f);
  color: white;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 30px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.3s;
}

.logo:hover {
  opacity: 0.8;
}

.nav {
  display: flex;
  gap: 8px;
  align-items: center;
}

.nav a {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 40px;
  transition: all 0.3s;
  font-size: 14px;
  position: relative;
}

.nav a:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav a.router-link-active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

/* 购物车徽章样式 */
.cart-link {
  position: relative;
}

.cart-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #ff6b6b;
  color: white;
  font-size: 10px;
  font-weight: bold;
  min-width: 16px;
  height: 16px;
  line-height: 16px;
  text-align: center;
  border-radius: 10px;
  padding: 0 4px;
}

.divider {
  width: 1px;
  height: 24px;
  background: rgba(255, 255, 255, 0.2);
  margin: 0 8px;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 40px;
  transition: background 0.3s;
  font-size: 14px;
}

.user-name:hover {
  background: rgba(255, 255, 255, 0.1);
}

.admin-badge {
  color: #ffd700;
  font-size: 12px;
}

.user-badge {
  color: #90ee90;
  font-size: 12px;
}

.main {
  flex: 1;
  padding: 30px 0;
  background: #f5f7fa;
}

.footer {
  background: #2c3e50;
  color: rgba(255, 255, 255, 0.6);
  text-align: center;
  padding: 24px;
  font-size: 14px;
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 16px;
  }

  .logo span:last-child {
    display: none;
  }

  .nav a span {
    display: none;
  }

  .nav a {
    padding: 8px 12px;
  }
}
</style>
