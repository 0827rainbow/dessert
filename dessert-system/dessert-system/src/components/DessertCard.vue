<template>
  <div class="dessert-card" @click="goDetail">
    <div class="card-img">
      <img :src="imageUrl" :alt="dessert.name" />
      <span v-if="dessert.isRecommend" class="tag hot">🔥 推荐</span>
      <span v-if="dessert.status === 0" class="tag off">已下架</span>
    </div>
    <div class="card-info">
      <h3>{{ dessert.name }}</h3>
      <p>{{ dessert.descp }}</p>
      <div class="price">
        <span class="current">¥{{ dessert.price }}</span>
        <span v-if="dessert.originalPrice" class="original">¥{{ dessert.originalPrice }}</span>
      </div>
      <div class="card-footer">
        <span>已售 {{ dessert.sales || 0 }}</span>
        <el-button type="primary" size="small" @click.stop="addCart">加入购物车</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps<{ dessert: any }>()
const emit = defineEmits(['add-cart'])
const router = useRouter()
const baseUrl = 'http://localhost:8080'

const imageUrl = computed(() => {
  if (props.dessert.photoUrl) return baseUrl + props.dessert.photoUrl
  return 'https://picsum.photos/200/200?random=1'
})

const goDetail = () => router.push(`/dessert-detail/${props.dessert.id}`)
const addCart = () => emit('add-cart', props.dessert)
</script>

<style scoped>
.dessert-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  transition: all 0.3s;
  cursor: pointer;
}
.dessert-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.12);
}
.card-img {
  position: relative;
  height: 200px;
  overflow: hidden;
}
.card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.tag {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
}
.tag.hot { background: #ff6b6b; }
.tag.off { background: #999; }
.card-info { padding: 16px; }
.card-info h3 { font-size: 16px; margin-bottom: 8px; }
.card-info p { font-size: 12px; color: #999; margin-bottom: 12px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.price { display: flex; align-items: baseline; gap: 8px; margin-bottom: 12px; }
.current { font-size: 18px; font-weight: bold; color: #ff6b6b; }
.original { font-size: 12px; color: #999; text-decoration: line-through; }
.card-footer { display: flex; justify-content: space-between; align-items: center; font-size: 12px; color: #999; }
</style>
