const api = require('../../../utils/api')
const app = getApp()

Page({
  data: {
    list: [],
    userInfo: {},
    statusMap: { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已驳回' },
    showForm: false,
    typeOptions: [{ label: '论文', value: 'PAPER' }, { label: '专著', value: 'MONOGRAPH' }],
    typeIndex: 0,
    levelOptions: ['SCI', 'EI', 'CSSCI', '核心', '普刊', '专著'],
    levelIndex: 0,
    form: { title: '', journalName: '', doi: '', coAuthors: '', publishDate: '', abstractText: '', keywords: '' }
  },
  onShow() {
    this.setData({ userInfo: app.globalData.userInfo || {} })
    this.loadData()
  },
  async loadData() {
    const params = { page: 1, size: 50 }
    const user = app.globalData.userInfo
    if (user.role === 'TEACHER') params.authorId = user.id
    const res = await api.getPapers(params)
    this.setData({ list: res.data.records })
  },
  toggleForm() {
    this.setData({ showForm: !this.data.showForm })
  },
  onInput(e) {
    this.setData({ [`form.${e.currentTarget.dataset.field}`]: e.detail.value })
  },
  onTypeChange(e) {
    this.setData({ typeIndex: e.detail.value })
  },
  onLevelChange(e) {
    this.setData({ levelIndex: e.detail.value })
  },
  onDateChange(e) {
    this.setData({ 'form.publishDate': e.detail.value })
  },
  showDetail(e) {
    const item = e.currentTarget.dataset.item
    const info = `类型: ${item.type === 'PAPER' ? '论文' : '专著'}\n作者: ${item.authorName||''}\n期刊/出版社: ${item.journalName||''}\n发表级别: ${item.publishLevel||''}\nDOI: ${item.doi||'无'}\n合作作者: ${item.coAuthors||'无'}\n发表日期: ${item.publishDate||''}\n摘要: ${item.abstractText||'无'}\n关键词: ${item.keywords||'无'}`
    wx.showModal({ title: item.title, content: info, showCancel: false })
  },
  async handleReview(e) {
    const { id, action } = e.currentTarget.dataset
    const actionText = action === 'APPROVE' ? '通过' : '驳回'
    const { confirm } = await wx.showModal({ title: '确认', content: `确定${actionText}此论文/专著？` })
    if (!confirm) return
    try {
      await api.reviewPaper({ id, action, comment: actionText })
      wx.showToast({ title: '操作成功' })
      this.loadData()
    } catch (e) { wx.showToast({ title: e.message || '操作失败', icon: 'none' }) }
  },
  async submitForm() {
    if (!this.data.form.title) return wx.showToast({ title: '请输入标题', icon: 'none' })
    const user = app.globalData.userInfo
    await api.submitPaper({
      title: this.data.form.title,
      type: this.data.typeOptions[this.data.typeIndex].value,
      authorId: user.id,
      journalName: this.data.form.journalName,
      publishLevel: this.data.levelOptions[this.data.levelIndex],
      doi: this.data.form.doi,
      coAuthors: this.data.form.coAuthors,
      publishDate: this.data.form.publishDate,
      abstractText: this.data.form.abstractText,
      keywords: this.data.form.keywords
    })
    wx.showToast({ title: '提交成功' })
    this.setData({
      showForm: false,
      form: { title: '', journalName: '', doi: '', coAuthors: '', publishDate: '', abstractText: '', keywords: '' },
      typeIndex: 0, levelIndex: 0
    })
    this.loadData()
  }
})
