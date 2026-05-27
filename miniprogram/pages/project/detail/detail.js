const api = require('../../../utils/api')
const app = getApp()

Page({
  data: {
    project: null,
    canOperate: false,
    needApproval: false,
    canComplete: false,
    approveLabel: '审核通过',
    statusMap: {
      DRAFT: '草稿', PENDING_SEAL: '待盖章', SEALED: '已盖章', PENDING_REVIEW: '待评审',
      REVIEWED: '已评审', PENDING_APPROVAL: '待审批', APPROVED: '已立项',
      IN_PROGRESS: '进行中', PENDING_COMPLETION: '待结题',
      COMPLETED: '已结题', REJECTED: '已驳回'
    }
  },
  onLoad(options) { this.loadDetail(options.id) },
  async loadDetail(id) {
    const res = await api.getProjectDetail(id)
    const p = res.data
    const user = app.globalData.userInfo
    const role = user.role
    let needApproval = false
    let approveLabel = '审核通过'

    if (p.status === 'PENDING_REVIEW' && p.projectCategory === 'VERTICAL' && p.projectType === 'COLLEGE' && role === 'COLLEGE_ADMIN') {
      needApproval = true
      approveLabel = '学院评审通过'
    } else if (p.status === 'PENDING_REVIEW' && role === 'SCHOOL_ADMIN') {
      needApproval = true
      approveLabel = '评审通过'
    } else if (p.status === 'PENDING_SEAL' && role === 'SCHOOL_ADMIN') {
      needApproval = true
      approveLabel = '盖章通过'
    } else if (p.status === 'SEALED' && role === 'SCHOOL_ADMIN') {
      needApproval = true
      approveLabel = '审核通过'
    } else if (p.status === 'PENDING_APPROVAL' && role === 'SCHOOL_ADMIN') {
      needApproval = true
      approveLabel = '立项通过'
    } else if (p.status === 'PENDING_COMPLETION' && role === 'SCHOOL_ADMIN') {
      needApproval = true
      approveLabel = '结题通过'
    }

    const canComplete = role === 'TEACHER' && p.status === 'APPROVED' && p.leaderId === user.id
    this.setData({ project: p, canOperate: needApproval || canComplete, needApproval, canComplete, approveLabel })
  },
  async handleApprove(e) {
    const action = e.currentTarget.dataset.action
    await api.approveProject({ projectId: this.data.project.id, action, comment: action === 'APPROVE' ? '同意' : '不符合要求' })
    wx.showToast({ title: '操作成功' })
    this.loadDetail(this.data.project.id)
  },
  async submitCompletion() {
    await api.submitCompletion(this.data.project.id)
    wx.showToast({ title: '结题报告已提交' })
    this.loadDetail(this.data.project.id)
  }
})
