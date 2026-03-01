import { defineStore } from 'pinia'
import { login as loginApi, getAdminInfo } from '@/api/auth'
import { setToken, removeToken } from '@/utils/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: null,
    adminInfo: null
  }),

  actions: {
    async login(username, password) {
      const data = await loginApi({ username, password })
      this.token = data.token
      setToken(data.token)
      return data
    },

    async fetchAdminInfo() {
      const data = await getAdminInfo()
      this.adminInfo = data
      return data
    },

    logout() {
      this.token = null
      this.adminInfo = null
      removeToken()
    }
  }
})
