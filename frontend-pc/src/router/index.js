import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', component: () => import('../views/login/index.vue'), meta: { title: '登录' } },
  {
    path: '/',
    component: () => import('../layout/index.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: () => import('../views/dashboard/index.vue'), meta: { title: '首页概览' } },
      { path: 'user', component: () => import('../views/user/index.vue'), meta: { title: '用户管理' } },
      { path: 'project', component: () => import('../views/project/index.vue'), meta: { title: '项目管理' } },
      { path: 'project/add', component: () => import('../views/project/form.vue'), meta: { title: '申报项目' } },
      { path: 'budget', component: () => import('../views/budget/index.vue'), meta: { title: '经费预算' } },
      { path: 'settlement', component: () => import('../views/settlement/index.vue'), meta: { title: '经费结算' } },
      { path: 'paper', component: () => import('../views/paper/index.vue'), meta: { title: '论文专著' } },
      { path: 'patent', component: () => import('../views/patent/index.vue'), meta: { title: '专利管理' } },
      { path: 'taskbook', component: () => import('../views/taskbook/index.vue'), meta: { title: '任务书管理' } },
      { path: 'notice', component: () => import('../views/notice/index.vue'), meta: { title: '消息通知' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = (to.meta.title || '高校科研项目管理系统') + ' - 科研管理'
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
