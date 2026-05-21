<template>
  <div class="container">
    <h2>购物车</h2>
    <el-card v-if="cartList.length" class="cart-card">
      <el-table :data="cartList">
        <el-table-column label="商品" width="300">
          <template #default="{ row }">
            <div class="product">
              <img :src="baseUrl + row.dessertPhoto" class="product-img" />
              <span>{{ row.dessertName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价">
          <template #default="{ row }">¥{{ row.dessertPrice }}</template>
        </el-table-column>
        <el-table-column label="数量" width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" :max="99" size="small" @change="updateQty(row)" />
          </template>
        </el-table-column>
        <el-table-column label="小计">
          <template #default="{ row }">¥{{ (row.dessertPrice * row.quantity).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button type="danger" text @click="remove(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="summary">
        <div class="total">总计：<span>¥{{ total }}</span></div>
        <el-button type="primary" size="large" :loading="checkoutLoading" @click="checkout">去结算</el-button>
      </div>
    </el-card>
    <el-empty v-else description="购物车空空如也">
      <el-button type="primary" @click="$router.push('/dessert-list')">去逛逛</el-button>
    </el-empty>

    <!-- 收货地址对话框 -->
    <el-dialog v-model="addressDialogVisible" title="填写收货信息" width="500px">
      <el-form :model="checkoutForm" :rules="addressRules" ref="checkoutFormRef" label-width="100px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="checkoutForm.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="checkoutForm.receiverPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="收货地址" prop="address">
          <el-input v-model="checkoutForm.address" type="textarea" rows="2" placeholder="请输入详细收货地址" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="checkoutForm.remark" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitOrder" :loading="checkoutLoading">提交订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cartApi } from '@/api/cart'
import { orderApi } from '@/api/order'

interface CartItem {
  id: number
  dessertName: string
  dessertPhoto: string
  dessertPrice: number
  quantity: number
}

const router = useRouter()
const baseUrl = 'http://localhost:8080'
const cartList = ref<CartItem[]>([])
const checkoutLoading = ref(false)
const addressDialogVisible = ref(false)
const checkoutFormRef = ref()

const checkoutForm = ref({
  receiverName: '',
  receiverPhone: '',
  address: '',
  remark: ''
})

const addressRules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  address: [{ required: true, message: '请输入收货地址', trigger: 'blur' }]
}

const total = computed(() => {
  return cartList.value.reduce((sum, item) => sum + item.dessertPrice * item.quantity, 0).toFixed(2)
})

const fetchCart = async () => {
  try {
    const res: any = await cartApi.get()
    cartList.value = res.data || []
  } catch {
    ElMessage.error('获取购物车失败')
  }
}

const updateQty = async (item: CartItem) => {
  try {
    await cartApi.updateQuantity(item.id, item.quantity)
    await fetchCart()
    window.dispatchEvent(new Event('cart-updated'))
  } catch {
    ElMessage.error('更新失败')
  }
}

const remove = async (id: number) => {
  try {
    await cartApi.remove(id)
    await fetchCart()
    window.dispatchEvent(new Event('cart-updated'))
    ElMessage.success('删除成功')
  } catch {
    ElMessage.error('删除失败')
  }
}

const checkout = () => {
  if (cartList.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  addressDialogVisible.value = true
}

const submitOrder = async () => {
  try {
    await checkoutFormRef.value?.validate()
    addressDialogVisible.value = false
    checkoutLoading.value = true

    const res: any = await orderApi.createFromCart({
      address: checkoutForm.value.address,
      receiverName: checkoutForm.value.receiverName,
      receiverPhone: checkoutForm.value.receiverPhone,
      remark: checkoutForm.value.remark
    })

    if (res.code === 200) {
      ElMessage.success('订单创建成功')
      cartList.value = []
      window.dispatchEvent(new Event('cart-updated'))
      router.push('/orders')
    } else {
      ElMessage.error(res.message || '创建订单失败')
    }
  } catch (error: any) {
    console.error('创建订单失败:', error)
    ElMessage.error(error?.message || '创建订单失败')
  } finally {
    checkoutLoading.value = false
  }
}

onMounted(fetchCart)
</script>

<style scoped>
.container { max-width: 1000px; margin: 0 auto; padding: 0 20px; }
h2 { margin-bottom: 20px; }
.product { display: flex; align-items: center; gap: 12px; }
.product-img { width: 60px; height: 60px; object-fit: cover; border-radius: 8px; }
.summary { margin-top: 20px; padding-top: 20px; border-top: 1px solid #eee; display: flex; justify-content: flex-end; align-items: center; gap: 20px; }
.total { font-size: 18px; }
.total span { font-size: 24px; color: #ff6b6b; font-weight: bold; }
</style>
