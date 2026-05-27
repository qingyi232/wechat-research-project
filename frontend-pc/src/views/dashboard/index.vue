<template>
  <div class="dashboard">
    <div class="welcome-bar">
      <div>
        <h2>欢迎回来，{{ userStore.userInfo?.realName }}</h2>
        <p>{{ roleMap[userStore.role] }} · {{ currentDate }}</p>
      </div>
      <div class="welcome-icon-wrap">
        <el-icon :size="64" color="#ffffff"><School /></el-icon>
      </div>
    </div>

    <el-row :gutter="16" class="stat-row">
      <el-col :span="6">
        <div class="stat-card" style="border-top:3px solid #2d8f7b">
          <div class="stat-icon" style="background:#e8f5f0"><el-icon :size="28" color="#2d8f7b"><FolderOpened /></el-icon></div>
          <div class="stat-value">{{ stats.project?.total || 0 }}</div>
          <div class="stat-label">项目总数</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="border-top:3px solid #e8a838">
          <div class="stat-icon" style="background:#fef5e7"><el-icon :size="28" color="#e8a838"><Money /></el-icon></div>
          <div class="stat-value">{{ stats.project?.inProgress || 0 }}</div>
          <div class="stat-label">在研项目</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="border-top:3px solid #27ae60">
          <div class="stat-icon" style="background:#eafaf1"><el-icon :size="28" color="#27ae60"><Trophy /></el-icon></div>
          <div class="stat-value">{{ stats.achievement?.total || 0 }}</div>
          <div class="stat-label">科研成果</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="border-top:3px solid #3498db">
          <div class="stat-icon" style="background:#ebf5fb"><el-icon :size="28" color="#3498db"><User /></el-icon></div>
          <div class="stat-value">{{ stats.teacherCount || 0 }}</div>
          <div class="stat-label">科研教师</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="12">
        <div class="panel">
          <div class="panel-header">
            <h3>项目分布</h3>
          </div>
          <div class="project-dist">
            <div class="dist-item">
              <div class="dist-bar">
                <div class="bar-fill" :style="{ width: barWidth('horizontal') + '%', background: '#2d8f7b' }"></div>
              </div>
              <span class="dist-label">横向项目</span>
              <span class="dist-num">{{ stats.project?.horizontal || 0 }}</span>
            </div>
            <div class="dist-item">
              <div class="dist-bar">
                <div class="bar-fill" :style="{ width: barWidth('vertical') + '%', background: '#e8a838' }"></div>
              </div>
              <span class="dist-label">纵向项目</span>
              <span class="dist-num">{{ stats.project?.vertical || 0 }}</span>
            </div>
            <div class="dist-item">
              <div class="dist-bar">
                <div class="bar-fill" :style="{ width: barWidth('completed') + '%', background: '#27ae60' }"></div>
              </div>
              <span class="dist-label">已结题</span>
              <span class="dist-num">{{ stats.project?.completed || 0 }}</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="panel">
          <div class="panel-header">
            <h3>科研成果统计</h3>
          </div>
          <div class="achievement-grid">
            <div class="ach-item">
              <el-icon :size="40" color="#2d8f7b"><Document /></el-icon>
              <div class="ach-num">{{ stats.achievement?.paperCount || 0 }}</div>
              <div class="ach-label">论文/专著</div>
            </div>
            <div class="ach-item">
              <el-icon :size="40" color="#e8a838"><Medal /></el-icon>
              <div class="ach-num">{{ stats.achievement?.patentCount || 0 }}</div>
              <div class="ach-label">授权专利</div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { statisticsApi } from '../../api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const stats = ref({})
const roleMap = { TEACHER: '普通教师', COLLEGE_ADMIN: '学院科研主管', SCHOOL_ADMIN: '学校科研主管', FINANCE_ADMIN: '财务主管', SYSTEM_ADMIN: '系统管理员' }
const currentDate = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })

const barWidth = (key) => {
  const total = stats.value.project?.total || 1
  return Math.round(((stats.value.project?.[key] || 0) / total) * 100)
}

onMounted(async () => {
  const params = {}
  if (userStore.role === 'COLLEGE_ADMIN') params.collegeId = userStore.collegeId
  const res = await statisticsApi.dashboard(params)
  stats.value = res.data
})
</script>

<style scoped>
.welcome-bar {
  background: linear-gradient(135deg, #2d8f7b, #3ab096);
  border-radius: 14px;
  padding: 28px 32px;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.welcome-bar h2 { font-size: 24px; margin-bottom: 6px; }
.welcome-bar p { opacity: 0.85; font-size: 16px; }
.welcome-icon-wrap {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  background: rgba(255,255,255,0.18);
  display: flex;
  align-items: center;
  justify-content: center;
}
.stat-row { margin-bottom: 20px; }
.stat-card { position: relative; padding-left: 88px; min-height: 100px; }
.stat-icon {
  position: absolute;
  left: 24px;
  top: 50%;
  transform: translateY(-50%);
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.panel {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.panel-header { margin-bottom: 20px; }
.panel-header h3 { font-size: 18px; font-weight: 600; }
.dist-item { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.dist-bar { flex: 1; height: 12px; background: #f0f2f5; border-radius: 6px; overflow: hidden; }
.bar-fill { height: 100%; border-radius: 6px; transition: width 0.6s ease; }
.dist-label { width: 80px; font-size: 16px; color: #666; }
.dist-num { font-weight: 600; font-size: 18px; min-width: 30px; text-align: right; }
.achievement-grid { display: flex; gap: 40px; justify-content: center; padding: 20px 0; }
.ach-item { text-align: center; }
.ach-num { font-size: 30px; font-weight: 700; color: #2c3e50; margin: 10px 0 4px; }
.ach-label { font-size: 16px; color: #999; }
</style>
