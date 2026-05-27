const api = require('../../utils/api')

Page({
  data: {
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
    loading: false
  },
  onInput(e) {
    this.setData({ [e.currentTarget.dataset.field]: e.detail.value })
  },
  async submit() {
    const { oldPassword, newPassword, confirmPassword } = this.data
    if (!oldPassword || !newPassword || !confirmPassword) {
      return wx.showToast({ title: '请填写完整', icon: 'none' })
    }
    if (newPassword.length < 6) {
      return wx.showToast({ title: '新密码至少6位', icon: 'none' })
    }
    if (newPassword !== confirmPassword) {
      return wx.showToast({ title: '两次密码不一致', icon: 'none' })
    }
    this.setData({ loading: true })
    try {
      await api.changePassword({ oldPassword, newPassword })
      wx.showToast({ title: '修改成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 1500)
    } catch (e) {
      wx.showToast({ title: e.message || '修改失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  }
})
