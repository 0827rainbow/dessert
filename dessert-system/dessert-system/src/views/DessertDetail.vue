<template>
  <div class="container" v-loading="loading">
    <div v-if="dessert.id" class="detail-content">
      <div class="detail-left">
        <div class="main-image">
          <img :src="imageUrl" :alt="dessert.name" />
        </div>
      </div>
      <div class="detail-right">
        <h1 class="product-name">{{ dessert.name }}</h1>
        <div class="price-section">
          <div class="price">
            <span class="label">价格</span>
            <span class="current">¥{{ dessert.price }}</span>
            <span v-if="dessert.original_price" class="original">¥{{ dessert.original_price }}</span>
          </div>
          <div class="sales-info">
            <span>月销 {{ dessert.sales || 0 }} 件</span>
            <span>库存 {{ dessert.stock || 0 }} 件</span>
          </div>
        </div>
        <div class="info-item">
          <span class="label">分类</span>
          <span>{{ dessert.categoryName }}</span>
        </div>
        <div class="info-item">
          <span class="label">描述</span>
          <span>{{ dessert.descp }}</span>
        </div>
        <div class="quantity-section">
          <span class="label">数量</span>
          <el-input-number v-model="quantity" :min="1" :max="Math.max(1, dessert.stock || 99)" size="large" />
        </div>
        <div class="action-buttons">
          <el-button v-if="dessert.stock > 0" size="large" @click="addToCart" :loading="cartLoading">加入购物车</el-button>
          <el-button v-if="dessert.stock > 0" type="primary" size="large" @click="buyNow">立即购买</el-button>
          <el-button v-else type="info" size="large" disabled>商品已售罄</el-button>
        </div>
      </div>
    </div>
    <el-empty v-else description="商品不存在" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dessertApi } from '@/api/dessert'
import { cartApi } from '@/api/cart'

const route = useRoute()
const router = useRouter()
const baseUrl = 'http://localhost:8080'

const dessert = ref<any>({})
const loading = ref(false)
const quantity = ref(1)
const cartLoading = ref(false)

const imageUrl = computed(() => {
  if (dessert.value.photoUrl) return baseUrl + dessert.value.photoUrl
  return 'https://picsum.photos/400/400?random=1'
})

const fetchDetail = async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    const res: any = await dessertApi.getById(id)
    dessert.value = res.data || {}
  } catch {
    ElMessage.error('获取商品详情失败')
  } finally {
    loading.value = false
  }
}

const addToCart = async () => {
  if (dessert.value.stock === 0) {
    ElMessage.warning('商品已售罄')
    return
  }
  cartLoading.value = true
  try {
    await cartApi.add(dessert.value.id, quantity.value)
    ElMessage.success('已添加到购物车')
    window.dispatchEvent(new Event('cart-updated'))
  } catch {
    ElMessage.error('添加失败')
  } finally {
    cartLoading.value = false
  }
}

const buyNow = () => {
  ElMessageBox.confirm('确认购买该商品吗？', '提示').then(async () => {
    ElMessage.success('订单创建成功，请去订单页面查看')
    router.push('/orders')
  })
}

onMounted(fetchDetail)
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.detail-content {
  display: flex;
  gap: 40px;
  background: white;
  padding: 30px;
  border-radius: 16px;
}

.detail-left {
  flex: 1;
}

.main-image {
  width: 100%;
  height: 400px;
  border-radius: 12px;
  overflow: hidden;
  background: #f5f5f5;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-right {
  flex: 1;
}

.product-name {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

.price-section {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.price {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 12px;
}

.label {
  color: #999;
  font-size: 14px;
  width: 60px;
}

.current {
  font-size: 28px;
  font-weight: bold;
  color: #ff6b6b;
}

.original {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
}

.sales-info {
  display: flex;
  gap: 20px;
  color: #999;
  font-size: 14px;
}

.info-item {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.quantity-section {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.action-buttons {
  display: flex;
  gap: 20px;
  margin-top: 30px;
}

.action-buttons .el-button {
  flex: 1;
  height: 48px;
  font-size: 16px;
}
</style>
