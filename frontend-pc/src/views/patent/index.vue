<template>
  <div class="page-container">
    <div class="page-header">
      <h2>专利管理</h2>
      <el-button type="primary" @click="showForm = true" v-if="userStore.role === 'TEACHER'"><el-icon><Plus /></el-icon>申请盖章</el-button>
    </div>
    <div class="filter-bar">
      <el-row :gutter="16">
        <el-col :span="6"><el-input v-model="query.keyword" placeholder="搜索专利名称/专利号" clearable @keyup.enter="fetchData" /></el-col>
        <el-col :span="4">
          <el-select v-model="query.status" placeholder="状态" clearable @change="fetchData">
            <el-option label="待盖章" value="PENDING_SEAL" /><el-option label="已盖章" value="SEALED" />
            <el-option label="实质审查中" value="UNDER_REVIEW" /><el-option label="已授权" value="AUTHORIZED" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
        </el-col>
        <el-col :span="3"><el-button type="primary" @click="fetchData">查询</el-button></el-col>
      </el-row>
    </div>
    <div class="data-table">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="patentName" label="专利名称" min-width="220" show-overflow-tooltip />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">{{ { INVENTION:'发明',UTILITY:'实用新型',DESIGN:'外观' }[row.patentType] }}</template>
        </el-table-column>
        <el-table-column prop="patentNo" label="专利号" width="160" />
        <el-table-column prop="inventorName" label="发明人" width="90" />
        <el-table-column prop="applyDate" label="申请日期" width="110" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status==='AUTHORIZED'?'success':row.status==='REJECTED'?'danger':'warning'" size="small">
              {{ { DRAFT:'草稿',PENDING_SEAL:'待盖章',SEALED:'已盖章',UNDER_REVIEW:'实质审查',AUTHORIZED:'已授权',REJECTED:'已驳回' }[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="userStore.role === 'SCHOOL_ADMIN'">
              <el-button link type="success" v-if="row.status==='PENDING_SEAL'" @click="handleAction(row.id,'SEAL')">盖章</el-button>
              <el-button link type="primary" v-if="row.status==='SEALED'" @click="handleAction(row.id,'UNDER_REVIEW')">转实质审查</el-button>
              <el-button link type="success" v-if="row.status==='UNDER_REVIEW'" @click="handleAction(row.id,'AUTHORIZE')">授权通过</el-button>
              <el-button link type="danger" v-if="['PENDING_SEAL','UNDER_REVIEW'].includes(row.status)" @click="handleAction(row.id,'REJECT')">驳回</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total" layout="total, sizes, prev, pager, next" @change="fetchData" />
      </div>
    </div>

    <el-dialog v-model="showForm" title="专利盖章申请" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="专利名称"><el-input v-model="form.patentName" /></el-form-item>
        <el-form-item label="专利类型">
          <el-select v-model="form.patentType" style="width:100%">
            <el-option label="发明专利" value="INVENTION" /><el-option label="实用新型" value="UTILITY" /><el-option label="外观设计" value="DESIGN" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请号"><el-input v-model="form.applicationNo" /></el-form-item>
        <el-form-item label="合作发明人"><el-input v-model="form.coInventors" placeholder="多个用逗号分隔" /></el-form-item>
        <el-form-item label="关联项目">
          <el-select v-model="form.projectId" placeholder="选择项目(选填)" clearable style="width:100%">
            <el-option v-for="p in myProjects" :key="p.id" :label="p.projectName" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请日期"><el-date-picker v-model="form.applyDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button type="primary" @click="submitPatent">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { patentApi, projectApi } from '../../api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = ref({ page: 1, size: 10, keyword: '', status: '' })
const showForm = ref(false)
const myProjects = ref([])
const form = ref({ patentName: '', patentType: 'INVENTION', applicationNo: '', coInventors: '', projectId: null, applyDate: '', inventorId: userStore.userId })

const fetchData = async () => {
  loading.value = true
  const params = { ...query.value }
  if (userStore.role === 'TEACHER') params.inventorId = userStore.userId
  try { const res = await patentApi.list(params); list.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}

const submitPatent = async () => {
  await patentApi.submit(form.value)
  ElMessage.success('申请已提交'); showForm.value = false; fetchData()
}

const handleAction = async (id, action) => {
  await patentApi.review({ id, action, comment: action === 'REJECT' ? '审核不通过' : '操作成功' })
  ElMessage.success('操作成功'); fetchData()
}

onMounted(async () => {
  fetchData()
  if (userStore.role === 'TEACHER') {
    const res = await projectApi.list({ leaderId: userStore.userId, size: 100 })
    myProjects.value = res.data.records
  }
})
</script>
