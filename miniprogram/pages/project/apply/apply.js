const api = require('../../../utils/api')
const app = getApp()

Page({
  data: {
    form: { projectName: '', projectCategory: 'VERTICAL', projectType: 'COLLEGE', projectSource: '', fundingAmount: '', description: '' },
    categories: [{ label: '纵向项目', value: 'VERTICAL' }, { label: '横向项目', value: 'HORIZONTAL' }],
    categoryIndex: 0,
    typeOptions: [{ label: '院级', value: 'COLLEGE' }, { label: '校级', value: 'SCHOOL' }, { label: '省级', value: 'PROVINCIAL' }, { label: '国家级', value: 'NATIONAL' }],
    typeIndex: 0,
    loading: false
  },
  onInput(e) {
    this.setData({ [`form.${e.currentTarget.dataset.field}`]: e.detail.value })
  },
  onCategoryChange(e) {
    const idx = e.detail.value
    const cat = this.data.categories[idx].value
    const types = cat === 'HORIZONTAL'
      ? [{ label: '招标项目', value: 'BIDDING' }, { label: '委托项目', value: 'COMMISSION' }]
      : [{ label: '院级', value: 'COLLEGE' }, { label: '校级', value: 'SCHOOL' }, { label: '省级', value: 'PROVINCIAL' }, { label: '国家级', value: 'NATIONAL' }]
    this.setData({
      categoryIndex: idx,
      'form.projectCategory': cat,
      typeOptions: types,
      typeIndex: 0,
      'form.projectType': types[0].value
    })
  },
  onTypeChange(e) {
    const idx = e.detail.value
    this.setData({ typeIndex: idx, 'form.projectType': this.data.typeOptions[idx].value })
  },
  async submit() {
    if (!this.data.form.projectName) return wx.showToast({ title: '请输入项目名称', icon: 'none' })
    this.setData({ loading: true })
    const user = app.globalData.userInfo
    try {
      await api.applyProject({
        ...this.data.form,
        fundingAmount: parseFloat(this.data.form.fundingAmount) || 0,
        leaderId: user.id,
        collegeId: user.collegeId,
        memberIds: []
      })
      wx.showToast({ title: '申报成功' })
      setTimeout(() => wx.navigateBack(), 1500)
    } finally { this.setData({ loading: false }) }
  }
})
