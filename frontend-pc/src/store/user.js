import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const token = ref(localStorage.getItem('token') || '')

  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => userInfo.value?.role || '')
  const userId = computed(() => userInfo.value?.id)
  const collegeId = computed(() => userInfo.value?.collegeId)

  function setUser(info, t) {
    userInfo.value = info
    token.value = t
    localStorage.setItem('userInfo', JSON.stringify(info))
    localStorage.setItem('token', t)
  }

  function logout() {
    userInfo.value = null
    token.value = ''
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
  }

  return { userInfo, token, isLoggedIn, role, userId, collegeId, setUser, logout }
})
