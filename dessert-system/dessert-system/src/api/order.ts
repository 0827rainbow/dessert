import request from './request'

export const orderApi = {
  // 获取订单列表
  getList() {
    return request.get('/order/list')
  },

  // 从购物车创建订单（需要传递收货地址）
  createFromCart(data: {
    address: string;
    receiverName: string;
    receiverPhone: string;
    remark?: string;
  }) {
    return request.post('/order/createFromCart', data)
  },

  // 直接购买
  createDirect(data: {
    dessertId: number;
    quantity: number;
    address: string;
    receiverName: string;
    receiverPhone: string;
    remark?: string;
  }) {
    return request.post('/order/createDirect', data)
  },

  // 支付订单
  pay(orderId: number) {
    return request.post(`/order/pay/${orderId}`)
  },

  // 取消订单
  cancel(orderId: number) {
    return request.post(`/order/cancel/${orderId}`)
  }
}
