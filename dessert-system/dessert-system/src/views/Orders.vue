<template>
  <div class="container">
    <h2>📦 我的订单</h2>

    <!-- 订单状态切换 -->
    <div class="tabs">
      <el-button
        v-for="item in statusList"
        :key="item.value"
        :type="currentStatus === item.value ? 'primary' : 'default'"
        @click="switchStatus(item.value)"
      >
        {{ item.label }}
      </el-button>
      <el-button type="success" plain @click="fetchOrders" :loading="loading">
        <el-icon><Refresh /></el-icon> 刷新
      </el-button>
    </div>

    <!-- 订单列表 -->
    <div v-if="filteredOrders.length > 0" class="order-list">
      <el-card v-for="order in filteredOrders" :key="order.id" class="order-card">
        <div class="order-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <span class="order-status" :style="{ color: getStatusColor(order.status) }">
            {{ getStatusText(order.status) }}
          </span>
        </div>

        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <div class="item-info">
              <div class="item-name">{{ item.dessertName }}</div>
              <div class="item-price">¥{{ item.price }} × {{ item.quantity }}</div>
            </div>
            <div class="item-subtotal">¥{{ ((item.price || 0) * (item.quantity || 0)).toFixed(2) }}</div>
          </div>
        </div>

        <div class="order-footer">
          <div class="order-total">
            共 {{ order.items?.length || 0 }} 件商品，实付：¥{{ (order.actualAmount || order.totalAmount || 0).toFixed(2) }}
          </div>
          <div class="order-actions">
            <el-button v-if="order.status === 1" type="primary" size="small" @click="payOrder(order.id)">立即支付</el-button>
            <el-button v-if="order.status === 1" size="small" @click="cancelOrder(order.id)">取消订单</el-button>
            <el-button v-if="order.status === 4" size="small" @click="reviewOrder(order)">评价</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <el-empty v-else description="暂无订单" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api/order'

interface OrderItem {
  id: number
  dessertId: number
  dessertName: string
  price: number
  quantity: number
  subtotal: number
}

interface Order {
  id: number
  orderNo: string
  userId: number
  totalAmount: number
  actualAmount: number
  status: number
  address: string
  receiverName: string
  receiverPhone: string
  createTime: string
  items: OrderItem[]
}

const orders = ref<Order[]>([])
const currentStatus = ref<number | null>(null)
const loading = ref(false)

const statusList = [
  { label: '全部', value: null },
  { label: '待付款', value: 1 },
  { label: '已完成', value: 4 },
  { label: '已取消', value: 5 }
]

// 根据状态筛选订单
const filteredOrders = computed(() => {
  if (currentStatus.value === null) {
    return orders.value
  }
  return orders.value.filter(order => order.status === currentStatus.value)
})

// 获取状态文本
const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    1: '待付款',
    2: '已付款',
    3: '配送中',
    4: '已完成',
    5: '已取消'
  }
  return map[status] || '未知'
}

// 获取状态颜色
const getStatusColor = (status: number) => {
  const map: Record<number, string> = {
    1: '#f56c6c',  // 待付款 - 红色
    2: '#409eff',  // 已付款 - 蓝色
    3: '#e6a23c',  // 配送中 - 橙色
    4: '#67c23a',  // 已完成 - 绿色
    5: '#909399'   // 已取消 - 灰色
  }
  return map[status] || '#909399'
}

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const res: any = await orderApi.getList()
    console.log('获取到的订单响应:', res)

    if (res.code === 200) {
      // 根据后端实际返回结构调整
      if (Array.isArray(res.data)) {
        orders.value = res.data
      } else if (res.rows && Array.isArray(res.rows)) {
        orders.value = res.rows
      } else if (res.list && Array.isArray(res.list)) {
        orders.value = res.list
      } else {
        orders.value = []
      }
      console.log('解析后的订单数量:', orders.value.length)
    } else {
      ElMessage.error(res.message || '获取订单失败')
    }
  } catch (error) {
    console.error('获取订单失败:', error)
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

// 切换状态
const switchStatus = (status: number | null) => {
  currentStatus.value = status
}

// 支付订单
const payOrder = (orderId: number) => {
  ElMessageBox.confirm('确认支付该订单吗？', '提示', { type: 'info' }).then(async () => {
    try {
      const res: any = await orderApi.pay(orderId)
      if (res.code === 200) {
        ElMessage.success('支付成功')
        await fetchOrders()
      } else {
        ElMessage.error(res.message || '支付失败')
      }
    } catch {
      ElMessage.error('支付失败')
    }
  }).catch(() => {})
}

// 取消订单
const cancelOrder = (orderId: number) => {
  ElMessageBox.confirm('确认取消该订单吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      const res: any = await orderApi.cancel(orderId)
      if (res.code === 200) {
        ElMessage.success('订单已取消')
        await fetchOrders()
      } else {
        ElMessage.error(res.message || '取消失败')
      }
    } catch {
      ElMessage.error('取消失败')
    }
  }).catch(() => {})
}

// 评价订单
const reviewOrder = (order: Order) => {
  ElMessage.info('评价功能开发中')
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 20px;
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  align-items: center;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  padding: 0;
  border-radius: 12px;
  overflow: hidden;
}

.order-header {
  padding: 15px 20px;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
}

.order-no {
  color: #666;
  font-size: 14px;
}

.order-status {
  font-weight: 500;
}

.order-items {
  padding: 15px 20px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.order-item:last-child {
  border-bottom: none;
}

.item-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.item-price {
  font-size: 12px;
  color: #999;
}

.item-subtotal {
  font-weight: 500;
  color: #ff6b6b;
}

.order-footer {
  padding: 15px 20px;
  background: #fafafa;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #eee;
}

.order-total {
  font-size: 14px;
  color: #666;
}

.order-actions {
  display: flex;
  gap: 10px;
}
</style>
