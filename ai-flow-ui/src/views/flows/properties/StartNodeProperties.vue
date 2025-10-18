<template>
  <div class="node-properties">
    <h4>开始节点配置</h4>
    <el-form label-width="80px">
      <el-form-item label="节点名称">
        <el-input v-model="localNode.properties.name" @input="onUpdate" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input 
          v-model="localNode.properties.description" 
          type="textarea" 
          :rows="3" 
          @input="onUpdate"
        />
      </el-form-item>
      <el-form-item label="流程变量">
        <div class="variables-list">
          <div 
            v-for="(variable, index) in localNode.properties.variables" 
            :key="index"
            class="variable-item"
          >
            <el-input 
              v-model="variable.name" 
              placeholder="变量名"
              @input="onUpdate"
            />
            <el-input 
              v-model="variable.value" 
              placeholder="默认值"
              @input="onUpdate"
            />
            <el-button 
              type="danger" 
              size="small" 
              @click="removeVariable(index)"
              icon="Delete"
            />
          </div>
          <el-button 
            type="primary" 
            size="small" 
            @click="addVariable"
            icon="Plus"
          >
            添加变量
          </el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

interface Props {
  node: any
  context: any
}

const props = defineProps<Props>()
const emit = defineEmits<{
  update: [data: any]
}>()

const localNode = ref({ ...props.node })

watch(() => props.node, (newNode) => {
  localNode.value = { ...newNode }
}, { deep: true })

function onUpdate() {
  emit('update', localNode.value)
}

function addVariable() {
  if (!localNode.value.properties.variables) {
    localNode.value.properties.variables = []
  }
  localNode.value.properties.variables.push({ name: '', value: '' })
  onUpdate()
}

function removeVariable(index: number) {
  localNode.value.properties.variables.splice(index, 1)
  onUpdate()
}
</script>

<style scoped>
.node-properties h4 {
  margin: 0 0 16px 0;
  color: #303133;
}

.variables-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.variable-item {
  display: flex;
  gap: 8px;
  align-items: center;
}
</style>
