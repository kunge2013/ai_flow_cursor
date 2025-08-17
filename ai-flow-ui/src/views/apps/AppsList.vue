<template>
  <div class="page">
    <el-card class="mb-12">
      <el-form :inline="true" :model="query">
        <el-form-item label="应用名称">
          <el-input v-model="query.name" placeholder="请输入应用名称" clearable />
        </el-form-item>
        <el-form-item label="应用类型">
          <el-select v-model="query.appType" placeholder="全部" clearable style="width: 160px">
            <el-option label="简单应用" value="simple" />
            <el-option label="高级编排应用" value="advanced" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">查询</el-button>
          <el-button @click="onReset">重置</el-button>
          <el-button type="success" @click="openCreate">创建应用</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="filtered" style="width: 100%">
        <el-table-column label="图标" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.iconUrl" :size="40">{{ row.name?.[0] }}</el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="应用名称" min-width="160" />
        <el-table-column prop="description" label="描述" min-width="240" />
        <el-table-column prop="appType" label="类型" width="140">
          <template #default="{ row }">
            <el-tag :type="row.appType === 'simple' ? 'success' : 'warning'">
              {{ row.appType === 'simple' ? '简单应用' : '高级编排应用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="primary" link @click="edit(row.id)">编辑</el-button>
            <el-button type="danger" link @click="remove(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="createVisible" title="创建应用" width="560px" @closed="onCreateClosed">
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="100px">
        <el-form-item label="应用名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入应用名称" />
        </el-form-item>
        <el-form-item label="应用描述" prop="description">
          <el-input v-model="createForm.description" type="textarea" :rows="3" placeholder="请输入应用描述" />
        </el-form-item>
        <el-form-item label="应用类型" prop="appType">
          <el-segmented v-model="createForm.appType" :options="appTypeOptions" />
        </el-form-item>
        <el-form-item label="图标">
          <el-upload :auto-upload="false" :show-file-list="false" :on-change="onCreateIcon">
            <img v-if="createForm.iconUrl" :src="createForm.iconUrl" class="avatar" />
            <el-button v-else>上传图标</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="onCreateConfirm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAppsStore, type AiApp } from '../../stores/apps'
import type { FormInstance, FormRules, UploadFile, UploadFiles } from 'element-plus'

const router = useRouter()
const appsStore = useAppsStore()

const query = reactive<{ name: string; appType: '' | AiApp['appType'] }>({ name: '', appType: '' })

const filtered = computed(() => {
  return appsStore.apps.filter(app => {
    const nameOk = query.name ? app.name.includes(query.name) : true
    const typeOk = query.appType ? app.appType === query.appType : true
    return nameOk && typeOk
  })
})

const appTypeOptions = [
  { label: '简单应用', value: 'simple' },
  { label: '高级编排应用', value: 'advanced' },
]

const createVisible = ref(false)
const createFormRef = ref<FormInstance>()
const createForm = reactive<Pick<AiApp, 'name' | 'description' | 'appType' | 'iconUrl'>>({
  name: '',
  description: '',
  appType: 'simple',
  iconUrl: ''
})
const createRules = reactive<FormRules<AiApp>>({
  name: [
    { required: true, message: '请输入应用名称', trigger: 'blur' },
    { min: 2, max: 30, message: '长度为 2-30 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入应用描述', trigger: 'blur' }
  ],
  appType: [
    { required: true, message: '请选择应用类型', trigger: 'change' }
  ]
})

function openCreate() {
  createVisible.value = true
}
async function onCreateConfirm() {
  await createFormRef.value?.validate()
  const created = appsStore.createApp({
    name: createForm.name,
    description: createForm.description,
    appType: createForm.appType,
    iconUrl: createForm.iconUrl,
    model: 'gpt-4o-mini',
    prompt: '',
    openingRemark: ''
  })
  createVisible.value = false
  router.push(`/apps/${created.id}/edit`)
}
function onCreateClosed() {
  createForm.name = ''
  createForm.description = ''
  createForm.appType = 'simple'
  createForm.iconUrl = ''
}
function onCreateIcon(file: UploadFile, _files: UploadFiles) {
  if (file.raw) {
    const reader = new FileReader()
    reader.onload = () => {
      createForm.iconUrl = String(reader.result || '')
    }
    reader.readAsDataURL(file.raw)
  }
}

function onSearch() {}
function onReset() {
  query.name = ''
  query.appType = ''
}
function edit(id: string) {
  router.push(`/apps/${id}/edit`)
}
function remove(id: string) {
  appsStore.removeApp(id)
}
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.mb-12 {
  margin-bottom: 12px;
}
.avatar {
  width: 48px;
  height: 48px;
  border-radius: 6px;
}
</style> 