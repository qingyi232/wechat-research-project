<template>
  <div class="page-container">
    <div class="page-header">
      <h2>项目管理</h2>
      <el-button type="primary" @click="$router.push('/project/add')" v-if="userStore.role === 'TEACHER'">
        <el-icon><Plus /></el-icon>申报项目
      </el-button>
    </div>

    <div class="filter-bar">
      <el-row :gutter="16">
        <el-col :span="6">
          <el-input v-model="query.keyword" placeholder="搜索项目名称" clearable prefix-icon="Search" @clear="fetchData" @keyup.enter="fetchData" />
        </el-col>
        <el-col :span="4">
          <el-select v-model="query.category" placeholder="项目类别" clearable @change="fetchData">
            <el-option label="横向项目" value="HORIZONTAL" /><el-option label="纵向项目" value="VERTICAL" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="query.status" placeholder="项目状态" clearable @change="fetchData">
            <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-col>
        <el-col :span="3">
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-col>
      </el-row>
    </div>

    <div class="data-table">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
        <el-table-column label="类别" width="90">
          <template #default="{ row }">
            <el-tag :type="row.projectCategory === 'HORIZONTAL' ? 'success' : 'warning'" size="small">
              {{ row.projectCategory === 'HORIZONTAL' ? '横向' : '纵向' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="projectType" label="类型" width="90">
          <template #default="{ row }">{{ typeMap[row.projectType] || row.projectType }}</template>
        </el-table-column>
        <el-table-column prop="leaderName" label="负责人" width="90" />
        <el-table-column prop="collegeName" label="所属学院" width="140" show-overflow-tooltip />
        <el-table-column prop="fundingAmount" label="经费(万)" width="90" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyDate" label="申报日期" width="110" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
            <template v-if="canApproveRow(row)">
              <el-button link type="success" @click="handleApprove(row, 'APPROVE')">
                通过
              </el-button>
              <el-button link type="danger" @click="handleApprove(row, 'REJECT')">
                驳回
              </el-button>
            </template>
            <el-button link type="warning" @click="handleCompletion(row)"
              v-if="userStore.role === 'TEACHER' && row.status === 'APPROVED' && row.leaderId === userStore.userId">
              提交结题
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.size"
          :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next" @change="fetchData" />
      </div>
    </div>

    <el-dialog v-model="detailVisible" title="项目详情" width="680px">
      <el-descriptions :column="2" border v-if="currentProject">
        <el-descriptions-item label="项目名称" :span="2">{{ currentProject.projectName }}</el-descriptions-item>
        <el-descriptions-item label="项目类别">{{ currentProject.projectCategory === 'HORIZONTAL' ? '横向' : '纵向' }}</el-descriptions-item>
        <el-descriptions-item label="项目类型">{{ typeMap[currentProject.projectType] || currentProject.projectType }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ currentProject.leaderName }}</el-descriptions-item>
        <el-descriptions-item label="经费(万元)">{{ currentProject.fundingAmount }}</el-descriptions-item>
        <el-descriptions-item label="项目来源" :span="2">{{ currentProject.projectSource }}</el-descriptions-item>
        <el-descriptions-item label="申报日期">{{ currentProject.applyDate }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="statusType(currentProject.status)" size="small">{{ statusMap[currentProject.status] }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="项目描述" :span="2">{{ currentProject.description }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="approveVisible" :title="approveAction === 'APPROVE' ? '审批通过' : '驳回项目'" width="450px">
      <el-input v-model="approveComment" type="textarea" :rows="3" :placeholder="approveAction === 'APPROVE' ? '请输入审批意见（选填）' : '请输入驳回原因'" />
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button :type="approveAction === 'APPROVE' ? 'primary' : 'danger'" @click="submitApprove">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { projectApi } from '../../api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = ref({ page: 1, size: 10, keyword: '', category: '', status: '' })
const detailVisible = ref(false)
const currentProject = ref(null)
const approveVisible = ref(false)
const approveAction = ref('')
const approveComment = ref('')
const approveProjectId = ref(null)

const canApprove = computed(() => ['SCHOOL_ADMIN', 'COLLEGE_ADMIN'].includes(userStore.role))

const canApproveRow = (row) => {
  const role = userStore.role
  const s = row.status
  if (s === 'PENDING_REVIEW' && row.projectCategory === 'VERTICAL' && row.projectType === 'COLLEGE') {
    return role === 'COLLEGE_ADMIN'
  }
  if (['PENDING_SEAL', 'SEALED', 'PENDING_APPROVAL', 'PENDING_REVIEW', 'PENDING_COMPLETION'].includes(s)) {
    return role === 'SCHOOL_ADMIN'
  }
  return false
}

const statusOptions = [
  { label: '草稿', value: 'DRAFT' }, { label: '待审批', value: 'PENDING_APPROVAL' },
  { label: '待评审', value: 'PENDING_REVIEW' }, { label: '已立项', value: 'APPROVED' },
  { label: '已结题', value: 'COMPLETED' }, { label: '已驳回', value: 'REJECTED' }
]
const statusMap = { DRAFT: '草稿', PENDING_SEAL: '待盖章', SEALED: '已盖章', PENDING_REVIEW: '待评审', REVIEWED: '已评审', PENDING_APPROVAL: '待审批', APPROVED: '已立项', IN_PROGRESS: '进行中', PENDING_COMPLETION: '待结题', COMPLETED: '已结题', REJECTED: '已驳回' }
const typeMap = { BIDDING: '招标', COMMISSION: '委托', COLLEGE: '院级', SCHOOL: '校级', PROVINCIAL: '省级', NATIONAL: '国家级' }

const statusType = (s) => {
  if (['APPROVED', 'COMPLETED', 'SEALED', 'REVIEWED'].includes(s)) return 'success'
  if (['REJECTED'].includes(s)) return 'danger'
  if (['PENDING_APPROVAL', 'PENDING_REVIEW', 'PENDING_SEAL', 'PENDING_COMPLETION'].includes(s)) return 'warning'
  return 'info'
}

const fetchData = async () => {
  loading.value = true
  const params = { ...query.value }
  if (userStore.role === 'TEACHER') params.leaderId = userStore.userId
  if (userStore.role === 'COLLEGE_ADMIN') params.collegeId = userStore.collegeId
  try {
    const res = await projectApi.list(params)
    list.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

const resetQuery = () => { query.value = { page: 1, size: 10, keyword: '', category: '', status: '' }; fetchData() }
const viewDetail = (row) => { currentProject.value = row; detailVisible.value = true }
const handleApprove = (row, action) => { approveProjectId.value = row.id; approveAction.value = action; approveComment.value = ''; approveVisible.value = true }

const submitApprove = async () => {
  await projectApi.approve({ projectId: approveProjectId.value, action: approveAction.value, comment: approveComment.value })
  ElMessage.success('操作成功')
  approveVisible.value = false
  fetchData()
}

const handleCompletion = async (row) => {
  await ElMessageBox.confirm('确认提交结题报告？', '提示')
  await projectApi.submitCompletion(row.id)
  ElMessage.success('结题报告已提交')
  fetchData()
}

onMounted(fetchData)
</script>
