import request from './request'

export const authApi = {
  login: data => request.post('/api/auth/login', data),
  register: data => request.post('/api/auth/register', data)
}

export const userApi = {
  getInfo: () => request.get('/api/user/info'),
  getDetail: id => request.get(`/api/user/detail/${id}`),
  list: params => request.get('/api/user/list', { params }),
  update: data => request.put('/api/user/update', data),
  audit: (id, status) => request.put(`/api/user/audit/${id}`, { status }),
  resetPwd: id => request.put(`/api/user/resetPwd/${id}`),
  changePwd: data => request.put('/api/user/changePwd', data),
  delete: id => request.delete(`/api/user/${id}`),
  colleges: () => request.get('/api/user/colleges'),
  teachers: params => request.get('/api/user/teachers', { params })
}

export const projectApi = {
  list: params => request.get('/api/project/list', { params }),
  detail: id => request.get(`/api/project/detail/${id}`),
  members: id => request.get(`/api/project/members/${id}`),
  approvals: id => request.get(`/api/project/approvals/${id}`),
  apply: data => request.post('/api/project/apply', data),
  update: data => request.put('/api/project/update', data),
  approve: data => request.post('/api/project/approve', data),
  submitCompletion: id => request.post(`/api/project/submitCompletion/${id}`),
  statistics: params => request.get('/api/project/statistics', { params }),
  delete: id => request.delete(`/api/project/${id}`)
}

export const budgetApi = {
  list: params => request.get('/api/budget/list', { params }),
  detail: id => request.get(`/api/budget/detail/${id}`),
  submit: data => request.post('/api/budget/submit', data),
  update: data => request.put('/api/budget/update', data),
  review: data => request.post('/api/budget/review', data),
  seal: id => request.post(`/api/budget/seal/${id}`),
  finalApprove: id => request.post(`/api/budget/finalApprove/${id}`)
}

export const settlementApi = {
  list: params => request.get('/api/settlement/list', { params }),
  detail: id => request.get(`/api/settlement/detail/${id}`),
  submit: data => request.post('/api/settlement/submit', data),
  review: data => request.post('/api/settlement/review', data),
  seal: id => request.post(`/api/settlement/seal/${id}`)
}

export const paperApi = {
  list: params => request.get('/api/paper/list', { params }),
  detail: id => request.get(`/api/paper/detail/${id}`),
  submit: data => request.post('/api/paper/submit', data),
  update: data => request.put('/api/paper/update', data),
  review: data => request.post('/api/paper/review', data),
  delete: id => request.delete(`/api/paper/${id}`)
}

export const patentApi = {
  list: params => request.get('/api/patent/list', { params }),
  detail: id => request.get(`/api/patent/detail/${id}`),
  submit: data => request.post('/api/patent/submit', data),
  review: data => request.post('/api/patent/review', data),
  delete: id => request.delete(`/api/patent/${id}`)
}

export const noticeApi = {
  list: params => request.get('/api/notice/list', { params }),
  unread: () => request.get('/api/notice/unread'),
  markRead: id => request.put(`/api/notice/read/${id}`),
  send: data => request.post('/api/notice/send', data),
  delete: id => request.delete(`/api/notice/${id}`)
}

export const statisticsApi = {
  dashboard: params => request.get('/api/statistics/dashboard', { params })
}

export const taskBookApi = {
  getByProject: projectId => request.get(`/api/taskbook/byProject/${projectId}`),
  submit: data => request.post('/api/taskbook/submit', data),
  update: data => request.put('/api/taskbook/update', data),
  review: (id, data) => request.post(`/api/taskbook/review/${id}`, data)
}
