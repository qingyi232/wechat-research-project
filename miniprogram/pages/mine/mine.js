const app = getApp()

Page({
  data: {
    userInfo: {},
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
  },
  goTo(e) {
    wx.navigateTo({ url: e.currentTarget.dataset.url })
  },
  switchToProject() {
    wx.switchTab({ url: '/pages/project/list/list' })
  },
  switchToNotice() {
    wx.switchTab({ url: '/pages/notice/notice' })
  },
  handleLogout() {
    wx.showModal({
      title: '提示',
      content: '确认退出登录？',
      success: (res) => {
        if (res.confirm) {
          app.globalData.userInfo = null
          app.globalData.token = ''
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          wx.redirectTo({ url: '/pages/login/login' })
        }
      }
    })
  }
})
