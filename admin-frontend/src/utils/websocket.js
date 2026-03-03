import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'
import { getToken } from './auth'

class WebSocketService {
  constructor() {
    this.client = null
    this.connected = false
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
    this.reconnectDelay = 3000
    this.subscriptions = new Map()
  }

  /**
   * 连接 WebSocket
   */
  connect() {
    if (this.connected) {
      console.log('WebSocket 已连接')
      return Promise.resolve()
    }

    return new Promise((resolve, reject) => {
      const token = getToken()
      if (!token) {
        reject(new Error('未找到认证 token'))
        return
      }

      // 创建 SockJS 实例
      const socket = new SockJS('/api/ws')

      // 创建 STOMP 客户端
      this.client = new Client({
        webSocketFactory: () => socket,
        connectHeaders: {
          Authorization: `Bearer ${token}`
        },
        debug: (str) => {
          // 仅在开发环境输出调试信息
          if (import.meta.env.DEV) {
            console.log('[WebSocket Debug]', str)
          }
        },
        reconnectDelay: this.reconnectDelay,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          console.log('WebSocket 连接成功')
          this.connected = true
          this.reconnectAttempts = 0
          resolve()
        },
        onStompError: (frame) => {
          console.error('WebSocket STOMP 错误:', frame)
          this.connected = false
          reject(new Error(frame.headers.message))
        },
        onWebSocketClose: () => {
          console.log('WebSocket 连接关闭')
          this.connected = false
          this.handleReconnect()
        },
        onWebSocketError: (error) => {
          console.error('WebSocket 错误:', error)
          this.connected = false
        }
      })

      // 激活连接
      this.client.activate()
    })
  }

  /**
   * 订阅主题
   */
  subscribe(destination, callback) {
    if (!this.connected) {
      console.warn('WebSocket 未连接，无法订阅')
      return null
    }

    const subscription = this.client.subscribe(destination, (message) => {
      try {
        const data = JSON.parse(message.body)
        callback(data)
      } catch (error) {
        console.error('解析 WebSocket 消息失败:', error)
      }
    })

    this.subscriptions.set(destination, subscription)
    console.log(`订阅主题: ${destination}`)
    return subscription
  }

  /**
   * 取消订阅
   */
  unsubscribe(destination) {
    const subscription = this.subscriptions.get(destination)
    if (subscription) {
      subscription.unsubscribe()
      this.subscriptions.delete(destination)
      console.log(`取消订阅: ${destination}`)
    }
  }

  /**
   * 断开连接
   */
  disconnect() {
    if (this.client) {
      // 取消所有订阅
      this.subscriptions.forEach((subscription) => {
        subscription.unsubscribe()
      })
      this.subscriptions.clear()

      // 断开连接
      this.client.deactivate()
      this.connected = false
      console.log('WebSocket 已断开')
    }
  }

  /**
   * 处理重连
   */
  handleReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++
      console.log(`尝试重连 WebSocket (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`)

      setTimeout(() => {
        this.connect().catch((error) => {
          console.error('重连失败:', error)
        })
      }, this.reconnectDelay * this.reconnectAttempts)
    } else {
      console.error('WebSocket 重连次数已达上限')
    }
  }

  /**
   * 检查连接状态
   */
  isConnected() {
    return this.connected
  }
}

// 导出单例
export default new WebSocketService()
