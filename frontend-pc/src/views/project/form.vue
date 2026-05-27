<template>
  <div class="page-container">
    <div class="page-header"><h2>申报项目</h2></div>
    <div class="form-card">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" style="max-width:800px">
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="form.projectName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目类别" prop="projectCategory">
          <el-radio-group v-model="form.projectCategory">
            <el-radio value="HORIZONTAL">横向项目</el-radio>
            <el-radio value="VERTICAL">纵向项目</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="项目类型" prop="projectType">
          <el-select v-model="form.projectType" placeholder="请选择" style="width:100%">
            <template v-if="form.projectCategory === 'HORIZONTAL'">
              <el-option label="招标项目" value="BIDDING" />
              <el-option label="委托项目" value="COMMISSION" />
            </template>
            <template v-else>
              <el-option label="院级" value="COLLEGE" />
              <el-option label="校级" value="SCHOOL" />
              <el-option label="省级" value="PROVINCIAL" />
              <el-option label="国家级" value="NATIONAL" />
            </template>
          </el-select>
        </el-form-item>
        <el-form-item label="项目来源"><el-input v-model="form.projectSource" placeholder="资助来源/招标单位" /></el-form-item>
        <el-form-item label="经费金额(万)"><el-input-number v-model="form.fundingAmount" :min="0" :precision="2" style="width:200px" /></el-form-item>
        <el-form-item label="项目成员">
          <el-select v-model="form.memberIds" multiple filterable placeholder="选择项目参与人" style="width:100%">
            <el-option v-for="t in teachers" :key="t.id" :label="t.realName" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目描述"><el-input v-model="form.description" type="textarea" :rows="4" placeholder="请简要描述项目内容" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">提交申报</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { projectApi, userApi } from '../../api'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const formRef = ref()
const teachers = ref([])

const form = ref({
  projectName: '', projectCategory: 'VERTICAL', projectType: '',
  projectSource: '', fundingAmount: 0, description: '',
  leaderId: userStore.userId, collegeId: userStore.collegeId, memberIds: []
})

const rules = {
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  projectCategory: [{ required: true, message: '请选择类别', trigger: 'change' }],
  projectType: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const submitForm = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await projectApi.apply(form.value)
    ElMessage.success('项目申报成功')
    router.push('/project')
  } finally { loading.value = false }
}

onMounted(async () => {
  const res = await userApi.teachers({})
  teachers.value = res.data.filter(t => t.id !== userStore.userId)
})
</script>

<style scoped>
.form-card {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
</style>
