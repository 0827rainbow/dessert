import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false, title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false, title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页', requiresAuth: true }
      },
      {
        path: 'dessert-list',
        name: 'DessertList',
        component: () => import('@/views/DessertList.vue'),
        meta: { title: '甜点商城', requiresAuth: true }
      },
      {
        path: 'dessert-detail/:id',
        name: 'DessertDetail',
        component: () => import('@/views/DessertDetail.vue'),
        meta: { title: '商品详情', requiresAuth: true }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/Cart.vue'),
        meta: { title: '购物车', requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/Orders.vue'),
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/Category.vue'),
        meta: { title: '分类管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'dessert',
        name: 'Dessert',
        component: () => import('@/views/Dessert.vue'),
        meta: { title: '甜点管理', requiresAuth: true, requiresAdmin: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '404', requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `甜点管理系统 - ${to.meta.title}` : '甜点管理系统'

  // 获取登录信息
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : {}

  console.log('=== 路由守卫 ===')
  console.log('目标路径:', to.path)
  console.log('是否有 token:', !!token)

  // 1. 如果需要登录但未登录，跳转到登录页
  if (to.meta.requiresAuth && !token) {
    console.log('需要登录但未登录，跳转到登录页')
    if (to.path !== '/login') {
      next('/login')
    } else {
      next()
    }
    return
  }

  // 2. 如果需要管理员权限但不是管理员，跳转到商城首页
  if (to.meta.requiresAdmin && user.role !== 1) {
    console.log('需要管理员权限，跳转到甜点商城')
    if (to.path !== '/dessert-list') {
      next('/dessert-list')
    } else {
      next()
    }
    return
  }

  // 3. 已登录访问登录页或注册页，跳转到首页
  if ((to.path === '/login' || to.path === '/register') && token) {
    console.log('已登录访问登录/注册页，跳转到首页')
    // 直接跳转到首页，不需要额外判断
    next('/')
    return
  }

  // 4. 正常放行
  console.log('允许访问')
  next()
})

export default router
