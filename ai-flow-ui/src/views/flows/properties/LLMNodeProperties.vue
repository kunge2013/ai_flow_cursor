<template>
  <div class="node-properties">
    <h4>LLM节点配置</h4>
    <el-form label-width="80px">
      <el-form-item label="节点名称">
        <el-input v-model="localNode.properties.name" @input="onUpdate" />
      </el-form-item>
      <el-form-item label="选择模型">
        <el-select v-model="localNode.properties.model" @change="onUpdate">
          <el-option label="gpt-4o-mini" value="gpt-4o-mini" />
          <el-option label="qwen2.5-7b-instruct" value="qwen2.5-7b-instruct" />
        </el-select>
      </el-form-item>
      <el-form-item label="输入变量">
        <el-input 
          v-model="localNode.properties.input" 
          type="textarea" 
          :rows="3" 
          @input="onUpdate"
        />
      </el-form-item>
      <el-form-item label="输出变量">
        <el-input v-model="localNode.properties.output" @input="onUpdate" />
      </el-form-item>
      <el-form-item label="温度">
        <el-slider 
          v-model="localNode.properties.temperature" 
          :min="0" 
          :max="2" 
          :step="0.1"
          @change="onUpdate"
        />
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
</script>

<style scoped>
.node-properties h4 {
  margin: 0 0 16px 0;
  color: #303133;
}
</style>
