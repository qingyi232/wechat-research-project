const { request } = require('./request')

const api = {
  login: (data) => request({ url: '/api/auth/login', method: 'POST', data }),
  register: (data) => request({ url: '/api/auth/register', method: 'POST', data }),
  getUserInfo: () => request({ url: '/api/user/info' }),
  getColleges: () => request({ url: '/api/user/colleges' }),
  changePassword: (data) => request({ url: '/api/user/changePwd', method: 'PUT', data }),

  getProjects: (params) => request({ url: '/api/project/list', data: params }),
  getProjectDetail: (id) => request({ url: `/api/project/detail/${id}` }),
  applyProject: (data) => request({ url: '/api/project/apply', method: 'POST', data }),
  approveProject: (data) => request({ url: '/api/project/approve', method: 'POST', data }),
  submitCompletion: (id) => request({ url: `/api/project/submitCompletion/${id}`, method: 'POST' }),
  getProjectStats: (params) => request({ url: '/api/project/statistics', data: params }),

  getBudgets: (params) => request({ url: '/api/budget/list', data: params }),
  getBudgetDetail: (id) => request({ url: `/api/budget/detail/${id}` }),
  submitBudget: (data) => request({ url: '/api/budget/submit', method: 'POST', data }),
  reviewBudget: (data) => request({ url: '/api/budget/review', method: 'POST', data }),
  sealBudget: (id) => request({ url: `/api/budget/seal/${id}`, method: 'POST' }),

  getSettlements: (params) => request({ url: '/api/settlement/list', data: params }),
  getSettlementDetail: (id) => request({ url: `/api/settlement/detail/${id}` }),
  submitSettlement: (data) => request({ url: '/api/settlement/submit', method: 'POST', data }),
  reviewSettlement: (data) => request({ url: '/api/settlement/review', method: 'POST', data }),
  sealSettlement: (id) => request({ url: `/api/settlement/seal/${id}`, method: 'POST' }),

  getPapers: (params) => request({ url: '/api/paper/list', data: params }),
  submitPaper: (data) => request({ url: '/api/paper/submit', method: 'POST', data }),
  reviewPaper: (data) => request({ url: '/api/paper/review', method: 'POST', data }),

  getPatents: (params) => request({ url: '/api/patent/list', data: params }),
  submitPatent: (data) => request({ url: '/api/patent/submit', method: 'POST', data }),
  reviewPatent: (data) => request({ url: '/api/patent/review', method: 'POST', data }),

  getNotices: (params) => request({ url: '/api/notice/list', data: params }),
  getUnread: () => request({ url: '/api/notice/unread' }),
  markRead: (id) => request({ url: `/api/notice/read/${id}`, method: 'PUT' }),

  getDashboard: (params) => request({ url: '/api/statistics/dashboard', data: params })
}

module.exports = api
