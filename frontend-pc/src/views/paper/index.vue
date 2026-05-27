<template>
  <div class="page-container">
    <div class="page-header">
      <h2>论文专著管理</h2>
      <el-button type="primary" @click="showForm = true" v-if="userStore.role === 'TEACHER'"><el-icon><Plus /></el-icon>录入成果</el-button>
    </div>
    <div class="filter-bar">
      <el-row :gutter="16">
        <el-col :span="6"><el-input v-model="query.keyword" placeholder="搜索标题/期刊" clearable @keyup.enter="fetchData" /></el-col>
        <el-col :span="4">
          <el-select v-model="query.type" placeholder="类型" clearable @change="fetchData">
            <el-option label="论文" value="PAPER" /><el-option label="专著" value="MONOGRAPH" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="query.status" placeholder="状态" clearable @change="fetchData">
            <el-option label="待审核" value="PENDING" /><el-option label="已通过" value="APPROVED" /><el-option label="已驳回" value="REJECTED" />
          </el-select>
        </el-col>
        <el-col :span="3"><el-button type="primary" @click="fetchData">查询</el-button></el-col>
      </el-row>
    </div>
    <div class="data-table">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="title" label="标题" min-width="250" show-overflow-tooltip />
        <el-table-column label="类型" width="80">
          <template #default="{ row }"><el-tag size="small" :type="row.type==='PAPER'?'':'warning'">{{ row.type==='PAPER'?'论文':'专著' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="authorName" label="作者" width="90" />
        <el-table-column prop="journalName" label="期刊/出版社" width="180" show-overflow-tooltip />
        <el-table-column prop="publishLevel" label="级别" width="80" />
        <el-table-column prop="publishDate" label="发表日期" width="110" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status==='APPROVED'?'success':row.status==='REJECTED'?'danger':'warning'" size="small">
              {{ { PENDING:'待审核',APPROVED:'已通过',REJECTED:'已驳回' }[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
            <el-button link type="success" v-if="canReview && row.status==='PENDING'" @click="handleReview(row.id,'APPROVE')">通过</el-button>
            <el-button link type="danger" v-if="canReview && row.status==='PENDING'" @click="handleReview(row.id,'REJECT')">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total" layout="total, sizes, prev, pager, next" @change="fetchData" />
      </div>
    </div>

    <el-dialog v-model="showForm" title="录入论文/专著" width="650px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="类型"><el-radio-group v-model="form.type"><el-radio value="PAPER">论文</el-radio><el-radio value="MONOGRAPH">专著</el-radio></el-radio-group></el-form-item>
        <el-form-item label="标题"><el-input v-model="form.title" placeholder="论文/专著标题" /></el-form-item>
        <el-form-item label="期刊/出版社"><el-input v-model="form.journalName" /></el-form-item>
        <el-form-item label="发表级别">
          <el-select v-model="form.publishLevel" placeholder="选择" style="width:100%">
            <el-option label="SCI" value="SCI" /><el-option label="EI" value="EI" /><el-option label="CSSCI" value="CSSCI" />
            <el-option label="核心期刊" value="核心" /><el-option label="普刊" value="普刊" /><el-option label="专著" value="专著" />
          </el-select>
        </el-form-item>
        <el-form-item label="合作作者"><el-input v-model="form.coAuthors" placeholder="多个用逗号分隔" /></el-form-item>
        <el-form-item label="关联项目">
          <el-select v-model="form.projectId" placeholder="选择项目(选填)" clearable style="width:100%">
            <el-option v-for="p in myProjects" :key="p.id" :label="p.projectName" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="DOI"><el-input v-model="form.doi" /></el-form-item>
        <el-form-item label="发表日期"><el-date-picker v-model="form.publishDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.abstractText" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="关键词"><el-input v-model="form.keywords" placeholder="用分号分隔" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button type="primary" @click="submitPaper">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="成果详情" width="650px">
      <el-descriptions :column="2" border v-if="currentItem">
        <el-descriptions-item label="标题" :span="2">{{ currentItem.title }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ currentItem.type==='PAPER'?'论文':'专著' }}</el-descriptions-item>
        <el-descriptions-item label="级别">{{ currentItem.publishLevel }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ currentItem.authorName }}</el-descriptions-item>
        <el-descriptions-item label="合作作者">{{ currentItem.coAuthors }}</el-descriptions-item>
        <el-descriptions-item label="期刊/出版社" :span="2">{{ currentItem.journalName }}</el-descriptions-item>
        <el-descriptions-item label="DOI">{{ currentItem.doi }}</el-descriptions-item>
        <el-descriptions-item label="发表日期">{{ currentItem.publishDate }}</el-descriptions-item>
        <el-descriptions-item label="摘要" :span="2">{{ currentItem.abstractText }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { paperApi, projectApi } from '../../api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = ref({ page: 1, size: 10, keyword: '', type: '', status: '' })
const showForm = ref(false)
const detailVisible = ref(false)
const currentItem = ref(null)
const myProjects = ref([])
const canReview = computed(() => userStore.role === 'SCHOOL_ADMIN')

const form = ref({ title: '', type: 'PAPER', journalName: '', publishLevel: '', coAuthors: '', projectId: null, doi: '', publishDate: '', abstractText: '', keywords: '', authorId: userStore.userId })

const fetchData = async () => {
  loading.value = true
  const params = { ...query.value }
  if (userStore.role === 'TEACHER') params.authorId = userStore.userId
  try { const res = await paperApi.list(params); list.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}

const viewDetail = (row) => { currentItem.value = row; detailVisible.value = true }

const submitPaper = async () => {
  await paperApi.submit(form.value)
  ElMessage.success('提交成功'); showForm.value = false; fetchData()
}

const handleReview = async (id, action) => {
  await paperApi.review({ id, action, comment: action === 'APPROVE' ? '审核通过' : '请修改后重新提交' })
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
