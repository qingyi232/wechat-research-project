const api = require('../../utils/api')
const app = getApp()

Page({
  data: {
    username: '',
    password: '',
    loading: false
  },
  onUsernameInput(e) { this.setData({ username: e.detail.value }) },
  onPasswordInput(e) { this.setData({ password: e.detail.value }) },
  fillDemo(e) {
    this.setData({ username: e.currentTarget.dataset.user, password: '123456' })
  },
  goRegister() {
    wx.navigateTo({ url: '/pages/register/register' })
  },
  async handleLogin() {
    const { username, password } = this.data
    if (!username || !password) {
      return wx.showToast({ title: '请输入账号和密码', icon: 'none' })
    }
    this.setData({ loading: true })
    try {
      const res = await api.login({ username, password })
      app.globalData.userInfo = res.data
      app.globalData.token = res.data.token
      wx.setStorageSync('token', res.data.token)
      wx.setStorageSync('userInfo', res.data)
      wx.showToast({ title: '登录成功', icon: 'success' })
      wx.switchTab({ url: '/pages/index/index' })
    } catch (err) {
      console.error(err)
    } finally {
      this.setData({ loading: false })
    }
  }
})
