import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 白名单接口（不需要验证）
const whiteList = ['/user/login', '/user/register']

request.interceptors.request.use(
  (config) => {
    console.log('请求地址:', config.url)

    const isWhiteList = whiteList.some(url => config.url?.includes(url))

    if (!isWhiteList) {
      // Session 方式，不需要手动添加 token
      // withCredentials: true 会自动携带 Cookie
    }

    return config
  },
  (error) => Promise.reject(error)
)

request.interceptors.response.use(
  (response) => {
    console.log('响应:', response.data)
    return response.data
  },
  (error) => {
    console.error('请求错误:', error)

    if (error.response?.status === 401 || error.response?.status === 403) {
      const isLoginUrl = error.config?.url?.includes('/user/login')
      if (!isLoginUrl) {
        localStorage.removeItem('user')
        // 不自动跳转，让页面自己处理
      }
    }
    return Promise.reject(error)
  }
)

export default request
