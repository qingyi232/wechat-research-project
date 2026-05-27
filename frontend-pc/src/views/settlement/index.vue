<template>
  <div class="page-container">
    <div class="page-header">
      <h2>经费结算管理</h2>
      <el-button type="primary" @click="showForm = true" v-if="userStore.role === 'TEACHER'"><el-icon><Plus /></el-icon>编制结算</el-button>
    </div>
    <div class="filter-bar">
      <el-row :gutter="16">
        <el-col :span="6"><el-input v-model="query.keyword" placeholder="搜索项目名称" clearable @keyup.enter="fetchData" /></el-col>
        <el-col :span="4">
          <el-select v-model="query.status" placeholder="状态" clearable @change="fetchData">
            <el-option label="待审核" value="PENDING" /><el-option label="已审核" value="APPROVED" />
            <el-option label="已盖章" value="SEALED" /><el-option label="已驳回" value="REJECTED" />
          </el-select>
        </el-col>
        <el-col :span="3"><el-button type="primary" @click="fetchData">查询</el-button></el-col>
      </el-row>
    </div>
    <div class="data-table">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="leaderName" label="负责人" width="90" />
        <el-table-column prop="totalAmount" label="结算总额(万)" width="110" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status==='SEALED'?'success':row.status==='REJECTED'?'danger':'warning'" size="small">
              {{ { DRAFT:'草稿',PENDING:'待审核',APPROVED:'已审核',SEALED:'已盖章',REJECTED:'已驳回' }[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="success" v-if="userStore.role==='SCHOOL_ADMIN' && row.status==='PENDING'" @click="handleReview(row.id,'APPROVE')">审核通过</el-button>
            <el-button link type="danger" v-if="userStore.role==='SCHOOL_ADMIN' && row.status==='PENDING'" @click="handleReview(row.id,'REJECT')">驳回</el-button>
            <el-button link type="warning" v-if="userStore.role==='FINANCE_ADMIN' && row.status==='APPROVED'" @click="handleSeal(row.id)">盖章</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total" layout="total, sizes, prev, pager, next" @change="fetchData" />
      </div>
    </div>

    <el-dialog v-model="showForm" title="编制结算表" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="关联项目">
          <el-select v-model="form.projectId" placeholder="选择已结题项目" style="width:100%">
            <el-option v-for="p in completedProjects" :key="p.id" :label="p.projectName" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="设备费(万)"><el-input-number v-model="form.equipmentFee" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="材料费(万)"><el-input-number v-model="form.materialFee" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="差旅费(万)"><el-input-number v-model="form.travelFee" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="会议费(万)"><el-input-number v-model="form.meetingFee" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="劳务费(万)"><el-input-number v-model="form.laborFee" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="咨询费(万)"><el-input-number v-model="form.consultFee" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="其他(万)"><el-input-number v-model="form.otherFee" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button type="primary" @click="submitSettlement">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { settlementApi, projectApi } from '../../api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = ref({ page: 1, size: 10, keyword: '', status: '' })
const showForm = ref(false)
const completedProjects = ref([])
const form = ref({ projectId: null, equipmentFee: 0, materialFee: 0, travelFee: 0, meetingFee: 0, laborFee: 0, consultFee: 0, otherFee: 0, remark: '' })

const fetchData = async () => {
  loading.value = true
  const params = { ...query.value }
  if (userStore.role === 'TEACHER') params.leaderId = userStore.userId
  try { const res = await settlementApi.list(params); list.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}

const submitSettlement = async () => {
  const total = form.value.equipmentFee + form.value.materialFee + form.value.travelFee + form.value.meetingFee + form.value.laborFee + form.value.consultFee + form.value.otherFee
  await settlementApi.submit({ ...form.value, totalAmount: total })
  ElMessage.success('结算表提交成功'); showForm.value = false; fetchData()
}

const handleReview = async (id, action) => {
  await settlementApi.review({ id, action, comment: action === 'APPROVE' ? '审核通过' : '请修改' })
  ElMessage.success('操作成功'); fetchData()
}

const handleSeal = async (id) => { await settlementApi.seal(id); ElMessage.success('盖章完成'); fetchData() }

onMounted(async () => {
  fetchData()
  if (userStore.role === 'TEACHER') {
    const res = await projectApi.list({ leaderId: userStore.userId, status: 'COMPLETED', size: 100 })
    completedProjects.value = res.data.records
  }
})
</script>
