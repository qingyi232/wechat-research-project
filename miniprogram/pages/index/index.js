const api = require('../../utils/api')
const app = getApp()

Page({
  data: {
    userInfo: {},
    stats: { project: {}, achievement: {}, teacherCount: 0 },
    roleMap: {
      TEACHER: '普通教师', COLLEGE_ADMIN: '学院科研主管',
      SCHOOL_ADMIN: '学校科研主管', FINANCE_ADMIN: '财务主管', SYSTEM_ADMIN: '系统管理员'
    }
  },
  onShow() {
    const userInfo = app.globalData.userInfo
    if (!userInfo) {
      wx.redirectTo({ url: '/pages/login/login' })
      return
    }
    this.setData({ userInfo })
    this.loadStats()
  },
  async loadStats() {
    try {
      const res = await api.getDashboard({})
      this.setData({ stats: res.data })
    } catch (e) { console.error(e) }
  },
  goTo(e) {
    wx.navigateTo({ url: e.currentTarget.dataset.url })
  },
  switchToProject() {
    wx.switchTab({ url: '/pages/project/list/list' })
  }
})
