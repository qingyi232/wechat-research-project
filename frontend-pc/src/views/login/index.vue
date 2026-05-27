<template>
  <div class="login-page">
    <div class="login-bg">
      <div class="bg-circle c1"></div>
      <div class="bg-circle c2"></div>
      <div class="bg-circle c3"></div>
    </div>
    <div class="login-card">
      <div class="login-header">
        <div class="logo-icon">
          <el-icon :size="36" color="#2d8f7b"><School /></el-icon>
        </div>
        <h1>高校科研项目管理系统</h1>
        <p>University Research Project Management System</p>
      </div>
      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" :rules="loginRules" ref="loginRef" @keyup.enter="handleLogin">
            <el-form-item prop="username">
              <el-input v-model="loginForm.username" placeholder="请输入账号" prefix-icon="User" size="large" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" @click="handleLogin" size="large" style="width:100%">登 录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="注册" name="register">
          <el-form :model="regForm" :rules="regRules" ref="regRef">
            <el-form-item prop="username">
              <el-input v-model="regForm.username" placeholder="设置登录账号" prefix-icon="User" size="large" />
            </el-form-item>
            <el-form-item prop="realName">
              <el-input v-model="regForm.realName" placeholder="真实姓名" prefix-icon="Postcard" size="large" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="regForm.password" type="password" placeholder="设置密码" prefix-icon="Lock" size="large" show-password />
            </el-form-item>
            <el-form-item prop="role">
              <el-select v-model="regForm.role" placeholder="选择角色" size="large" style="width:100%">
                <el-option label="高校普通教师" value="TEACHER" />
                <el-option label="学院科研主管" value="COLLEGE_ADMIN" />
                <el-option label="学校科研主管" value="SCHOOL_ADMIN" />
                <el-option label="财务主管" value="FINANCE_ADMIN" />
              </el-select>
            </el-form-item>
            <el-form-item prop="collegeId">
              <el-select v-model="regForm.collegeId" placeholder="选择所属学院" size="large" style="width:100%" clearable>
                <el-option v-for="c in colleges" :key="c.id" :label="c.collegeName" :value="c.id" />
              </el-select>
            </el-form-item>
            <el-form-item prop="phone">
              <el-input v-model="regForm.phone" placeholder="联系电话" prefix-icon="Phone" size="large" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" @click="handleRegister" size="large" style="width:100%">注 册</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <div class="demo-accounts">
        <p>演示账号（密码均为 123456）</p>
        <div class="account-tags">
          <el-tag @click="fillDemo('admin')" effect="plain">管理员</el-tag>
          <el-tag @click="fillDemo('teacher1')" effect="plain" type="success">教师</el-tag>
          <el-tag @click="fillDemo('school_admin')" effect="plain" type="warning">科研主管</el-tag>
          <el-tag @click="fillDemo('finance_admin')" effect="plain" type="danger">财务主管</el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi, userApi } from '../../api'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('login')
const loading = ref(false)
const loginRef = ref()
const regRef = ref()
const colleges = ref([])

const loginForm = ref({ username: '', password: '' })
const regForm = ref({ username: '', realName: '', password: '', role: '', collegeId: null, phone: '' })

const loginRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const regRules = {
  username: [{ required: true, message: '请设置账号', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请设置密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const fillDemo = (username) => {
  loginForm.value = { username, password: '123456' }
  activeTab.value = 'login'
}

const handleLogin = async () => {
  await loginRef.value.validate()
  loading.value = true
  try {
    const res = await authApi.login(loginForm.value)
    userStore.setUser(res.data, res.data.token)
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  await regRef.value.validate()
  loading.value = true
  try {
    await authApi.register(regForm.value)
    ElMessage.success('注册成功，请等待管理员审核')
    activeTab.value = 'login'
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    const res = await userApi.colleges()
    colleges.value = res.data
  } catch {}
})
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8f5f0 0%, #f0f4f2 50%, #faf6ee 100%);
  position: relative;
  overflow: hidden;
}
.login-bg { position: absolute; inset: 0; }
.bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.12;
}
.c1 { width: 500px; height: 500px; background: #2d8f7b; top: -120px; right: -80px; }
.c2 { width: 350px; height: 350px; background: #e8a838; bottom: -60px; left: -60px; }
.c3 { width: 200px; height: 200px; background: #2d8f7b; bottom: 20%; right: 15%; }
.login-card {
  width: 440px;
  background: #fff;
  border-radius: 16px;
  padding: 40px 36px 28px;
  box-shadow: 0 12px 40px rgba(0,0,0,0.08);
  position: relative;
  z-index: 1;
}
.login-header {
  text-align: center;
  margin-bottom: 28px;
}
.logo-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: #e8f5f0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}
.login-header h1 {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 6px;
}
.login-header p {
  font-size: 14px;
  color: #95a5a6;
  letter-spacing: 0.5px;
}
.login-tabs :deep(.el-tabs__item) { font-size: 17px; }
.demo-accounts {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #eee;
  text-align: center;
}
.demo-accounts p { font-size: 14px; color: #999; margin-bottom: 10px; }
.account-tags { display: flex; gap: 8px; justify-content: center; flex-wrap: wrap; }
.account-tags .el-tag { cursor: pointer; }
</style>
