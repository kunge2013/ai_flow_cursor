<template>
  <div class="node-properties">
    <h4>ScriptNodeProperties配置</h4>
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
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

interface Props {
  node?: any
  edge?: any
  context: any
}

const props = defineProps<Props>()
const emit = defineEmits<{
  update: [data: any]
}>()

const localNode = ref({ ...props.node })

watch(() => props.node, (newNode) => {
  if (newNode) localNode.value = { ...newNode }
}, { deep: true })

function onUpdate() {
  emit('update', localNode.value)
}
</script>

<style scoped>
.node-properties h4 {
  margin: 0 0 16px 0;
  color: #303133;
}
</style>
