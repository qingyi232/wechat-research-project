<template>
  <div class="page-container">
    <div class="page-header">
      <h2>消息通知</h2>
      <el-button type="primary" @click="showSendForm = true" v-if="['SYSTEM_ADMIN','SCHOOL_ADMIN'].includes(userStore.role)">
        <el-icon><Plus /></el-icon>发送通知
      </el-button>
    </div>
    <div class="data-table">
      <el-table :data="list" stripe v-loading="loading" @row-click="handleRead">
        <el-table-column width="50">
          <template #default="{ row }">
            <el-badge is-dot :hidden="row.isRead === 1" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="{ SYSTEM:'',PROJECT:'success',BUDGET:'warning',ACHIEVEMENT:'' }[row.type]">
              {{ { SYSTEM:'系统',PROJECT:'项目',BUDGET:'经费',ACHIEVEMENT:'成果' }[row.type] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="createTime" label="时间" width="160" />
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total" layout="total, prev, pager, next" @change="fetchData" />
      </div>
    </div>

    <el-dialog v-model="showSendForm" title="发送通知" width="550px">
      <el-form :model="sendForm" label-width="80px">
        <el-form-item label="通知类型">
          <el-select v-model="sendForm.type" style="width:100%">
            <el-option label="系统通知" value="SYSTEM" />
            <el-option label="项目通知" value="PROJECT" />
            <el-option label="经费通知" value="BUDGET" />
            <el-option label="成果通知" value="ACHIEVEMENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="接收人">
          <el-select v-model="sendForm.receiverId" placeholder="选择接收人" filterable style="width:100%">
            <el-option v-for="t in teachers" :key="t.id" :label="t.realName + ' (' + t.username + ')'" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="sendForm.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="sendForm.content" type="textarea" :rows="4" placeholder="请输入通知内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSendForm = false">取消</el-button>
        <el-button type="primary" @click="handleSend">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { noticeApi, userApi } from '../../api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = ref({ page: 1, size: 20 })
const showSendForm = ref(false)
const sendForm = ref({ title: '', content: '', type: 'SYSTEM', receiverId: null })
const teachers = ref([])

const fetchData = async () => {
  loading.value = true
  try { const res = await noticeApi.list(query.value); list.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}

const handleRead = async (row) => {
  if (row.isRead === 0) {
    await noticeApi.markRead(row.id)
    row.isRead = 1
  }
}

const handleSend = async () => {
  if (!sendForm.value.title) return ElMessage.warning('请输入标题')
  if (!sendForm.value.receiverId) return ElMessage.warning('请选择接收人')
  await noticeApi.send(sendForm.value)
  ElMessage.success('通知发送成功')
  showSendForm.value = false
  sendForm.value = { title: '', content: '', type: 'SYSTEM', receiverId: null }
  fetchData()
}

onMounted(async () => {
  fetchData()
  if (['SYSTEM_ADMIN', 'SCHOOL_ADMIN'].includes(userStore.role)) {
    try {
      const res = await userApi.list({ size: 200, status: 1 })
      teachers.value = res.data.records
    } catch {}
  }
})
</script>
