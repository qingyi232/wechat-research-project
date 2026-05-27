<template>
  <div class="page-container">
    <div class="page-header"><h2>项目任务书管理</h2></div>
    <div class="filter-bar">
      <el-row :gutter="16">
        <el-col :span="8">
          <el-select v-model="selectedProjectId" placeholder="选择项目查看任务书" filterable style="width:100%" @change="loadTaskBook">
            <el-option v-for="p in projects" :key="p.id" :label="p.projectName" :value="p.id" />
          </el-select>
        </el-col>
      </el-row>
    </div>

    <div class="form-card" v-if="taskBook || showCreateForm">
      <el-form :model="form" label-width="110px" v-if="taskBook || showCreateForm">
        <el-form-item label="研究目标">
          <el-input v-model="form.objectives" type="textarea" :rows="3" :disabled="!editable" />
        </el-form-item>
        <el-form-item label="研究内容">
          <el-input v-model="form.researchContent" type="textarea" :rows="4" :disabled="!editable" />
        </el-form-item>
        <el-form-item label="预期成果">
          <el-input v-model="form.expectedResults" type="textarea" :rows="3" :disabled="!editable" />
        </el-form-item>
        <el-form-item label="研究计划">
          <el-input v-model="form.schedule" type="textarea" :rows="3" :disabled="!editable" />
        </el-form-item>
        <el-form-item label="状态" v-if="taskBook">
          <el-tag :type="statusType(form.status)">{{ statusMap[form.status] }}</el-tag>
          <span v-if="form.reviewComment" style="margin-left:12px;color:#999">审核意见：{{ form.reviewComment }}</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm" v-if="editable">{{ taskBook ? '修改提交' : '提交任务书' }}</el-button>
          <el-button type="success" @click="handleReview('APPROVE')" v-if="canReview && form.status === 'PENDING'">审核通过</el-button>
          <el-button type="danger" @click="handleReview('REJECT')" v-if="canReview && form.status === 'PENDING'">驳回</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="data-table" v-else-if="selectedProjectId">
      <div class="empty-hint">
        <p>该项目尚未填写任务书</p>
        <el-button type="primary" @click="showCreateForm = true" v-if="userStore.role === 'TEACHER'">填写任务书</el-button>
      </div>
    </div>
    <div class="data-table" v-else>
      <div class="empty-hint"><p>请先选择一个项目</p></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { taskBookApi, projectApi } from '../../api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const projects = ref([])
const selectedProjectId = ref(null)
const taskBook = ref(null)
const showCreateForm = ref(false)
const form = ref({ objectives: '', researchContent: '', expectedResults: '', schedule: '', status: '' })
const statusMap = { DRAFT: '草稿', PENDING: '待审核', APPROVED: '已审核', REJECTED: '已驳回' }

const editable = computed(() => {
  if (userStore.role !== 'TEACHER') return false
  if (!taskBook.value) return true
  return ['DRAFT', 'REJECTED'].includes(form.value.status)
})
const canReview = computed(() => userStore.role === 'SCHOOL_ADMIN')
const statusType = (s) => s === 'APPROVED' ? 'success' : s === 'REJECTED' ? 'danger' : 'warning'

const loadTaskBook = async () => {
  if (!selectedProjectId.value) return
  showCreateForm.value = false
  try {
    const res = await taskBookApi.getByProject(selectedProjectId.value)
    if (res.data) {
      taskBook.value = res.data
      form.value = { ...res.data }
    } else {
      taskBook.value = null
      form.value = { objectives: '', researchContent: '', expectedResults: '', schedule: '', status: '' }
    }
  } catch { taskBook.value = null }
}

const submitForm = async () => {
  if (taskBook.value) {
    form.value.status = 'PENDING'
    await taskBookApi.update(form.value)
  } else {
    await taskBookApi.submit({ ...form.value, projectId: selectedProjectId.value })
  }
  ElMessage.success('提交成功')
  loadTaskBook()
}

const handleReview = async (action) => {
  await taskBookApi.review(taskBook.value.id, { action, comment: action === 'APPROVE' ? '任务书合格' : '请修改后重新提交' })
  ElMessage.success('操作成功')
  loadTaskBook()
}

onMounted(async () => {
  const params = userStore.role === 'TEACHER' ? { leaderId: userStore.userId, size: 100 } : { size: 100 }
  const res = await projectApi.list(params)
  projects.value = res.data.records
})
</script>

<style scoped>
.form-card { background: #fff; border-radius: 12px; padding: 32px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.empty-hint { text-align: center; padding: 60px 0; color: #999; }
.empty-hint p { margin-bottom: 16px; }
</style>
