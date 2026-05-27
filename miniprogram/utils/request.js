const app = getApp()

const TIMEOUT_MS = 10000

const request = (options) => {
  return new Promise((resolve, reject) => {
    wx.request({
      url: app.globalData.baseUrl + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      timeout: TIMEOUT_MS,
      header: {
        'Content-Type': 'application/json',
        'Authorization': app.globalData.token ? `Bearer ${app.globalData.token}` : ''
      },
      success(res) {
        if (res.statusCode === 401) {
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          wx.redirectTo({ url: '/pages/login/login' })
          return reject({ msg: '登录已过期' })
        }
        if (res.statusCode >= 500) {
          wx.showToast({ title: '服务异常，请稍后重试', icon: 'none' })
          return reject({ msg: '服务异常' })
        }
        if (res.data && res.data.code === 200) {
          resolve(res.data)
        } else {
          wx.showToast({ title: (res.data && res.data.msg) || '请求失败', icon: 'none' })
          reject(res.data || { msg: '请求失败' })
        }
      },
      fail(err) {
        const msg = err && err.errMsg ? err.errMsg : ''
        let toast = '网络异常'
        if (msg.indexOf('timeout') >= 0) toast = '请求超时，请检查后端服务'
        else if (msg.indexOf('fail') >= 0) toast = '无法连接到服务器'
        wx.showToast({ title: toast, icon: 'none', duration: 2000 })
        reject(err)
      }
    })
  })
}

module.exports = { request }
