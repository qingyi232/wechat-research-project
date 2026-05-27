const api = require('../../../utils/api')
const app = getApp()

Page({
  data: {
    list: [],
    userInfo: {},
    statusMap: { DRAFT: '草稿', PENDING: '待审核', APPROVED: '已审核', SEALED: '已盖章', REJECTED: '已驳回' },
    showForm: false,
    myProjects: [],
    projectIndex: -1,
    form: { projectId: '', equipmentFee: '', materialFee: '', travelFee: '', meetingFee: '', laborFee: '', consultFee: '', otherFee: '', remark: '' }
  },
  onShow() {
    this.setData({ userInfo: app.globalData.userInfo || {} })
    this.loadData()
  },
  async loadData() {
    const params = { page: 1, size: 50 }
    const user = app.globalData.userInfo
    if (user.role === 'TEACHER') params.leaderId = user.id
    const res = await api.getBudgets(params)
    this.setData({ list: res.data.records })
  },
  toggleForm() {
    if (!this.data.showForm) this.loadProjects()
    this.setData({ showForm: !this.data.showForm })
  },
  async loadProjects() {
    const user = app.globalData.userInfo
    const res = await api.getProjects({ leaderId: user.id, size: 100 })
    this.setData({ myProjects: res.data.records })
  },
  onProjectChange(e) {
    const idx = e.detail.value
    this.setData({ projectIndex: idx, 'form.projectId': this.data.myProjects[idx].id })
  },
  onInput(e) {
    this.setData({ [`form.${e.currentTarget.dataset.field}`]: e.detail.value })
  },
  showDetail(e) {
    const item = e.currentTarget.dataset.item
    const details = `设备费: ${item.equipmentFee||0}万\n材料费: ${item.materialFee||0}万\n差旅费: ${item.travelFee||0}万\n会议费: ${item.meetingFee||0}万\n劳务费: ${item.laborFee||0}万\n咨询费: ${item.consultFee||0}万\n其他: ${item.otherFee||0}万\n合计: ${item.totalAmount}万\n备注: ${item.remark||'无'}`
    wx.showModal({ title: item.projectName, content: details, showCancel: false })
  },
  async handleReview(e) {
    const { id, action } = e.currentTarget.dataset
    const actionText = action === 'APPROVE' ? '通过' : '驳回'
    const { confirm } = await wx.showModal({ title: '确认', content: `确定${actionText}此预算？` })
    if (!confirm) return
    try {
      await api.reviewBudget({ id, action, comment: actionText })
      wx.showToast({ title: '操作成功' })
      this.loadData()
    } catch (e) { wx.showToast({ title: e.message || '操作失败', icon: 'none' }) }
  },
  async handleSeal(e) {
    const id = e.currentTarget.dataset.id
    const { confirm } = await wx.showModal({ title: '确认', content: '确定为此预算盖章？' })
    if (!confirm) return
    try {
      await api.sealBudget(id)
      wx.showToast({ title: '盖章成功' })
      this.loadData()
    } catch (e) { wx.showToast({ title: e.message || '操作失败', icon: 'none' }) }
  },
  async submitForm() {
    if (!this.data.form.projectId) return wx.showToast({ title: '请选择项目', icon: 'none' })
    const f = this.data.form
    const total = (parseFloat(f.equipmentFee) || 0) + (parseFloat(f.materialFee) || 0) + (parseFloat(f.travelFee) || 0) +
      (parseFloat(f.meetingFee) || 0) + (parseFloat(f.laborFee) || 0) + (parseFloat(f.consultFee) || 0) + (parseFloat(f.otherFee) || 0)
    await api.submitBudget({
      projectId: f.projectId,
      equipmentFee: parseFloat(f.equipmentFee) || 0,
      materialFee: parseFloat(f.materialFee) || 0,
      travelFee: parseFloat(f.travelFee) || 0,
      meetingFee: parseFloat(f.meetingFee) || 0,
      laborFee: parseFloat(f.laborFee) || 0,
      consultFee: parseFloat(f.consultFee) || 0,
      otherFee: parseFloat(f.otherFee) || 0,
      totalAmount: total,
      remark: f.remark
    })
    wx.showToast({ title: '预算提交成功' })
    this.setData({
      showForm: false,
      form: { projectId: '', equipmentFee: '', materialFee: '', travelFee: '', meetingFee: '', laborFee: '', consultFee: '', otherFee: '', remark: '' },
      projectIndex: -1
    })
    this.loadData()
  }
})
