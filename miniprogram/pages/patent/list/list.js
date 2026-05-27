const api = require('../../../utils/api')
const app = getApp()

Page({
  data: {
    list: [],
    userInfo: {},
    typeMap: { INVENTION: '发明', UTILITY: '实用新型', DESIGN: '外观' },
    statusMap: { DRAFT: '草稿', PENDING_SEAL: '待盖章', SEALED: '已盖章', UNDER_REVIEW: '实质审查', AUTHORIZED: '已授权', REJECTED: '已驳回' },
    showForm: false,
    patentTypeOptions: [{ label: '发明专利', value: 'INVENTION' }, { label: '实用新型', value: 'UTILITY' }, { label: '外观设计', value: 'DESIGN' }],
    patentTypeIndex: 0,
    form: { patentName: '', applicationNo: '', coInventors: '', applyDate: '' }
  },
  onShow() {
    this.setData({ userInfo: app.globalData.userInfo || {} })
    this.loadData()
  },
  async loadData() {
    const params = { page: 1, size: 50 }
    const user = app.globalData.userInfo
    if (user.role === 'TEACHER') params.inventorId = user.id
    const res = await api.getPatents(params)
    this.setData({ list: res.data.records })
  },
  toggleForm() {
    this.setData({ showForm: !this.data.showForm })
  },
  onInput(e) {
    this.setData({ [`form.${e.currentTarget.dataset.field}`]: e.detail.value })
  },
  onPatentTypeChange(e) {
    this.setData({ patentTypeIndex: e.detail.value })
  },
  onDateChange(e) {
    this.setData({ 'form.applyDate': e.detail.value })
  },
  showDetail(e) {
    const item = e.currentTarget.dataset.item
    const info = `类型: ${this.data.typeMap[item.patentType]||''}\n发明人: ${item.inventorName||''}\n合作发明人: ${item.coInventors||'无'}\n申请号: ${item.applicationNo||''}\n专利号: ${item.patentNo||'待授权'}\n申请日期: ${item.applyDate||''}\n授权日期: ${item.authDate||'未授权'}`
    wx.showModal({ title: item.patentName, content: info, showCancel: false })
  },
  async handleReview(e) {
    const { id, action } = e.currentTarget.dataset
    const textMap = { SEAL: '盖章', REJECT: '驳回', AUTHORIZE: '授权通过' }
    const { confirm } = await wx.showModal({ title: '确认', content: `确定${textMap[action]}此专利？` })
    if (!confirm) return
    try {
      await api.reviewPatent({ id, action, comment: textMap[action] })
      wx.showToast({ title: '操作成功' })
      this.loadData()
    } catch (e) { wx.showToast({ title: e.message || '操作失败', icon: 'none' }) }
  },
  async submitForm() {
    if (!this.data.form.patentName) return wx.showToast({ title: '请输入专利名称', icon: 'none' })
    const user = app.globalData.userInfo
    await api.submitPatent({
      patentName: this.data.form.patentName,
      patentType: this.data.patentTypeOptions[this.data.patentTypeIndex].value,
      applicationNo: this.data.form.applicationNo,
      inventorId: user.id,
      coInventors: this.data.form.coInventors,
      applyDate: this.data.form.applyDate
    })
    wx.showToast({ title: '申请已提交' })
    this.setData({
      showForm: false,
      form: { patentName: '', applicationNo: '', coInventors: '', applyDate: '' },
      patentTypeIndex: 0
    })
    this.loadData()
  }
})
