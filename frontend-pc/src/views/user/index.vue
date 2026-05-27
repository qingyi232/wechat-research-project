<template>
  <div class="page-container">
    <div class="page-header"><h2>用户管理</h2></div>
    <div class="filter-bar">
      <el-row :gutter="16">
        <el-col :span="5"><el-input v-model="query.keyword" placeholder="搜索姓名/账号" clearable prefix-icon="Search" @keyup.enter="fetchData" /></el-col>
        <el-col :span="4">
          <el-select v-model="query.role" placeholder="角色" clearable @change="fetchData">
            <el-option label="普通教师" value="TEACHER" /><el-option label="学院科研主管" value="COLLEGE_ADMIN" />
            <el-option label="学校科研主管" value="SCHOOL_ADMIN" /><el-option label="财务主管" value="FINANCE_ADMIN" />
            <el-option label="系统管理员" value="SYSTEM_ADMIN" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="query.status" placeholder="状态" clearable @change="fetchData">
            <el-option label="待审核" :value="0" /><el-option label="正常" :value="1" /><el-option label="禁用" :value="2" />
          </el-select>
        </el-col>
        <el-col :span="3"><el-button type="primary" @click="fetchData">查询</el-button></el-col>
      </el-row>
    </div>
    <div class="data-table">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="username" label="账号" width="120" />
        <el-table-column label="角色" width="120">
          <template #default="{ row }"><el-tag size="small">{{ roleMap[row.role] }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="collegeName" label="学院" width="160" show-overflow-tooltip />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="title" label="职称" width="90" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'warning' : 'danger'" size="small">
              {{ ['待审核','正常','禁用'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="success" v-if="row.status === 0" @click="handleAudit(row.id, 1)">通过</el-button>
            <el-button link type="warning" v-if="row.status === 1" @click="handleAudit(row.id, 2)">禁用</el-button>
            <el-button link type="primary" v-if="row.status === 2" @click="handleAudit(row.id, 1)">启用</el-button>
            <el-button link type="info" @click="handleReset(row.id)">重置密码</el-button>
            <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.size"
          :total="total" layout="total, sizes, prev, pager, next" @change="fetchData" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '../../api'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = ref({ page: 1, size: 10, keyword: '', role: '', status: null })
const roleMap = { TEACHER: '普通教师', COLLEGE_ADMIN: '学院科研主管', SCHOOL_ADMIN: '学校科研主管', FINANCE_ADMIN: '财务主管', SYSTEM_ADMIN: '系统管理员' }

const fetchData = async () => {
  loading.value = true
  try { const res = await userApi.list(query.value); list.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}

const handleAudit = async (id, status) => { await userApi.audit(id, status); ElMessage.success('操作成功'); fetchData() }
const handleReset = async (id) => {
  await ElMessageBox.confirm('确认将密码重置为 123456？', '提示')
  await userApi.resetPwd(id); ElMessage.success('密码已重置')
}
const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该用户？', '警告', { type: 'warning' })
  await userApi.delete(id); ElMessage.success('删除成功'); fetchData()
}

onMounted(fetchData)
</script>
