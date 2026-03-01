import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/utils/auth'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    const res = response.data

    // 统一响应格式处理
    if (res.code !== undefined && res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      if (res.code === 401) {
        removeToken()
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }

    // 返回data字段的数据
    return res.data
  },
  error => {
    const message = error.response?.data?.message || error.message || '网络错误'
    ElMessage.error(message)

    // 401未授权，跳转登录页
    if (error.response?.status === 401) {
      removeToken()
      router.push('/login')
    }

    return Promise.reject(error)
  }
)

export default request
