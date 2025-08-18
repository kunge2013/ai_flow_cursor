<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>{{ isEdit ? '编辑应用' : '创建应用' }}</span>
        <div class="actions">
          <el-button type="primary" @click="onSubmit">保存</el-button>
          <el-button @click="onCancel">关闭</el-button>
        </div>
      </div>
    </template>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="max-width: 860px">
      <el-form-item label="应用名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入应用名称" />
      </el-form-item>

      <el-form-item label="应用描述" prop="description">
        <el-input v-model="form.description" placeholder="请输入应用描述" type="textarea" :rows="3" />
      </el-form-item>

      <el-form-item label="上传应用图标">
        <el-upload
          class="avatar-uploader"
          :auto-upload="false"
          :show-file-list="false"
          :on-change="onIconChange"
        >
          <img v-if="form.iconUrl" :src="form.iconUrl" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>

      <el-form-item label="应用类型" prop="appType">
        <el-segmented v-model="form.appType" :options="appTypeOptions" />
      </el-form-item>

      <el-form-item label="选择模型">
        <el-select v-model="form.model" placeholder="请选择模型" filterable style="width: 240px">
          <el-option label="gpt-4o-mini" value="gpt-4o-mini" />
          <el-option label="gpt-4.1" value="gpt-4.1" />
          <el-option label="qwen2.5-7b-instruct" value="qwen2.5-7b-instruct" />
        </el-select>
      </el-form-item>

      <el-form-item label="提示词">
        <el-input v-model="form.prompt" type="textarea" :rows="4" placeholder="请输入提示词" />
      </el-form-item>

      <el-form-item label="开场白">
        <el-input v-model="form.openingRemark" type="textarea" :rows="3" placeholder="请输入开场白" />
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { computed, reactive, ref, onMounted } from 'vue'
import type { FormInstance, FormRules, UploadFile, UploadFiles } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getApp, createApp, updateApp, type AiAppUpsertRequest } from '../../api/apps'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => Boolean(route.params.id))

const formRef = ref<FormInstance>()
const form = reactive<AiAppUpsertRequest>({
  name: '',
  description: '',
  iconUrl: '',
  appType: 'simple',
  model: 'gpt-4o-mini',
  prompt: '',
  openingRemark: ''
})

const rules = reactive<FormRules<AiAppUpsertRequest>>({
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

const appTypeOptions = [
  { label: '简单应用', value: 'simple' },
  { label: '高级编排应用', value: 'advanced' }
]

onMounted(async () => {
  if (isEdit.value) {
    const id = String(route.params.id)
    const data = await getApp(id)
    form.name = data.name
    form.description = data.description || ''
    form.iconUrl = data.iconUrl || ''
    form.appType = data.appType as any
    form.model = data.model || 'gpt-4o-mini'
    form.prompt = data.prompt || ''
    form.openingRemark = data.openingRemark || ''
  }
})

function onIconChange(file: UploadFile, _files: UploadFiles) {
  if (file.raw) {
    const reader = new FileReader()
    reader.onload = () => {
      form.iconUrl = String(reader.result || '')
    }
    reader.readAsDataURL(file.raw)
  }
}

async function onSubmit() {
  await formRef.value?.validate()
  if (isEdit.value) {
    const id = String(route.params.id)
    await updateApp(id, { ...form })
    ElMessage.success('已保存')
    router.push('/apps')
  } else {
    await createApp({ ...form })
    ElMessage.success('已创建')
    router.push('/apps')
  }
}

function onCancel() {
  router.push('/apps')
}
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.avatar-uploader .avatar {
  width: 80px;
  height: 80px;
  display: block;
  border-radius: 6px;
}
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  text-align: center;
}
</style> 