const api = require('../../../utils/api')
const app = getApp()

Page({
  data: {
    projects: [],
    currentTab: '',
    keyword: '',
    loading: false,
    userInfo: {},
    statusMap: {
      DRAFT: '草稿', PENDING_SEAL: '待盖章', SEALED: '已盖章', PENDING_REVIEW: '待评审',
      REVIEWED: '已评审', PENDING_APPROVAL: '待审批', APPROVED: '已立项',
      IN_PROGRESS: '进行中', PENDING_COMPLETION: '待结题', COMPLETED: '已结题', REJECTED: '已驳回'
    },
    statusClass: {
      APPROVED: 'tag-success', COMPLETED: 'tag-success', SEALED: 'tag-success',
      REJECTED: 'tag-danger', DRAFT: 'tag-info',
      PENDING_APPROVAL: 'tag-warning', PENDING_REVIEW: 'tag-warning', PENDING_SEAL: 'tag-warning', PENDING_COMPLETION: 'tag-warning'
    }
  },
  onShow() {
    this.setData({ userInfo: app.globalData.userInfo || {} })
    this.loadData()
  },
  async loadData() {
    this.setData({ loading: true })
    const params = { page: 1, size: 50 }
    if (this.data.currentTab) params.category = this.data.currentTab
    if (this.data.keyword) params.keyword = this.data.keyword
    if (this.data.userInfo.role === 'TEACHER') params.leaderId = this.data.userInfo.id
    try {
      const res = await api.getProjects(params)
      this.setData({ projects: res.data.records })
    } finally {
      this.setData({ loading: false })
    }
  },
  switchTab(e) {
    this.setData({ currentTab: e.currentTarget.dataset.tab })
    this.loadData()
  },
  onSearch(e) {
    this.setData({ keyword: e.detail.value })
    this.loadData()
  },
  goDetail(e) {
    wx.navigateTo({ url: `/pages/project/detail/detail?id=${e.currentTarget.dataset.id}` })
  },
  goApply() {
    wx.navigateTo({ url: '/pages/project/apply/apply' })
  }
})
