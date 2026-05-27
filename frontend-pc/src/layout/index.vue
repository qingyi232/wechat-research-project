<template>
  <el-container class="app-layout">
    <el-aside width="220px" class="app-aside">
      <div class="aside-logo">
        <el-icon :size="24" color="#2d8f7b"><School /></el-icon>
        <span>科研管理系统</span>
      </div>
      <el-menu :default-active="route.path" router unique-opened>
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon><span>首页概览</span>
        </el-menu-item>

        <el-sub-menu index="project-group" v-if="role !== 'FINANCE_ADMIN'">
          <template #title><el-icon><FolderOpened /></el-icon><span>项目管理</span></template>
          <el-menu-item index="/project">项目列表</el-menu-item>
          <el-menu-item index="/project/add" v-if="role === 'TEACHER'">申报项目</el-menu-item>
          <el-menu-item index="/taskbook">任务书管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="fund-group">
          <template #title><el-icon><Money /></el-icon><span>经费管理</span></template>
          <el-menu-item index="/budget">经费预算</el-menu-item>
          <el-menu-item index="/settlement">经费结算</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="achievement-group" v-if="role !== 'FINANCE_ADMIN'">
          <template #title><el-icon><Trophy /></el-icon><span>科研成果</span></template>
          <el-menu-item index="/paper">论文专著</el-menu-item>
          <el-menu-item index="/patent">专利管理</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/notice">
          <el-icon><Bell /></el-icon>
          <template #title>
            <span>消息通知</span>
            <el-badge :value="unreadCount" :hidden="!unreadCount" :max="99" class="notice-badge" />
          </template>
        </el-menu-item>

        <el-menu-item index="/user" v-if="role === 'SYSTEM_ADMIN'">
          <el-icon><UserFilled /></el-icon><span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="app-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" style="background:#2d8f7b">{{ userStore.userInfo?.realName?.[0] }}</el-avatar>
              <span class="user-name">{{ userStore.userInfo?.realName }}</span>
              <span class="user-role">{{ roleLabel }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="changePwd">修改密码</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>

  <el-dialog v-model="pwdVisible" title="修改密码" width="400px">
    <el-form :model="pwdForm" label-width="80px">
      <el-form-item label="原密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
      <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="pwdVisible = false">取消</el-button>
      <el-button type="primary" @click="submitPwd">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'
import { noticeApi, userApi } from '../api'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const role = computed(() => userStore.role)
const unreadCount = ref(0)
const pwdVisible = ref(false)
const pwdForm = ref({ oldPassword: '', newPassword: '' })

const roleMap = { TEACHER: '普通教师', COLLEGE_ADMIN: '学院科研主管', SCHOOL_ADMIN: '学校科研主管', FINANCE_ADMIN: '财务主管', SYSTEM_ADMIN: '系统管理员' }
const roleLabel = computed(() => roleMap[role.value] || '')

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (cmd === 'changePwd') {
    pwdForm.value = { oldPassword: '', newPassword: '' }
    pwdVisible.value = true
  }
}

const submitPwd = async () => {
  await userApi.changePwd(pwdForm.value)
  ElMessage.success('密码修改成功')
  pwdVisible.value = false
}

const fetchUnread = async () => {
  try {
    const res = await noticeApi.unread()
    unreadCount.value = res.data
  } catch {}
}

onMounted(() => {
  fetchUnread()
  setInterval(fetchUnread, 30000)
})
</script>

<style scoped>
.app-layout { height: 100vh; }
.app-aside {
  background: #1a2332;
  overflow-y: auto;
}
.aside-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid #243447;
}
.aside-logo span {
  font-size: 18px;
  font-weight: 600;
  color: #e8f5f0;
}
.app-aside .el-menu {
  border-right: none;
  background: transparent;
}
.app-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  padding: 0 24px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}
.user-name { font-size: 16px; font-weight: 500; color: #2c3e50; }
.user-role { font-size: 14px; color: #95a5a6; }
.app-main {
  background: #f5f7f6;
  padding: 20px;
  overflow-y: auto;
}
.notice-badge { margin-left: 8px; }
</style>
