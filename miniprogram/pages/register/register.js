const api = require('../../utils/api')

Page({
  data: {
    form: { username: '', password: '', confirmPwd: '', realName: '', phone: '', email: '' },
    roleOptions: [
      { label: '普通教师', value: 'TEACHER' },
      { label: '学院科研主管', value: 'COLLEGE_ADMIN' },
      { label: '学校科研主管', value: 'SCHOOL_ADMIN' },
      { label: '财务主管', value: 'FINANCE_ADMIN' }
    ],
    roleIndex: 0,
    colleges: [],
    collegeIndex: -1,
    loading: false
  },
  onLoad() { this.loadColleges() },
  async loadColleges() {
    try {
      const res = await api.getColleges()
      this.setData({ colleges: res.data })
    } catch (e) { console.error(e) }
  },
  onInput(e) {
    this.setData({ [`form.${e.currentTarget.dataset.field}`]: e.detail.value })
  },
  onRoleChange(e) {
    this.setData({ roleIndex: e.detail.value })
  },
  onCollegeChange(e) {
    this.setData({ collegeIndex: e.detail.value })
  },
  async handleRegister() {
    const { form, roleOptions, roleIndex, colleges, collegeIndex } = this.data
    if (!form.username) return wx.showToast({ title: '请输入账号', icon: 'none' })
    if (!form.password) return wx.showToast({ title: '请输入密码', icon: 'none' })
    if (form.password !== form.confirmPwd) return wx.showToast({ title: '两次密码不一致', icon: 'none' })
    if (!form.realName) return wx.showToast({ title: '请输入姓名', icon: 'none' })

    this.setData({ loading: true })
    try {
      await api.register({
        username: form.username,
        password: form.password,
        realName: form.realName,
        role: roleOptions[roleIndex].value,
        collegeId: collegeIndex >= 0 ? colleges[collegeIndex].id : null,
        phone: form.phone,
        email: form.email
      })
      wx.showToast({ title: '注册成功，请等待审核', icon: 'none', duration: 2000 })
      setTimeout(() => wx.navigateBack(), 2000)
    } catch (err) {
      console.error(err)
    } finally {
      this.setData({ loading: false })
    }
  },
  goLogin() {
    wx.navigateBack()
  }
})
