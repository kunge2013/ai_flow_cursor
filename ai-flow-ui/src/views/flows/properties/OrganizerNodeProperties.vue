<template>
  <div class="node-properties">
    <h4>组织器节点配置</h4>
    <el-form label-width="80px">
      <el-form-item label="节点名称">
        <el-input v-model="localNode.properties.name" @input="onUpdate" />
      </el-form-item>
      
      <el-form-item label="组织类型">
        <el-select v-model="localNode.properties.organizeType" @change="onUpdate">
          <el-option label="分类整理" value="classify" />
          <el-option label="排序整理" value="sort" />
          <el-option label="分组整理" value="group" />
          <el-option label="过滤整理" value="filter" />
          <el-option label="合并整理" value="merge" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="输入数据">
        <el-input 
          v-model="localNode.properties.inputData" 
          type="textarea" 
          :rows="3" 
          placeholder="输入要整理的数据或变量名"
          @input="onUpdate"
        />
      </el-form-item>
      
      <el-form-item label="整理规则">
        <el-input 
          v-model="localNode.properties.organizeRules" 
          type="textarea" 
          :rows="4" 
          placeholder="定义整理规则，如：按类别分组、按时间排序等"
          @input="onUpdate"
        />
      </el-form-item>
      
      <el-form-item label="输出变量">
        <el-input v-model="localNode.properties.output" @input="onUpdate" />
      </el-form-item>
      
      <el-form-item label="输出格式">
        <el-select v-model="localNode.properties.outputFormat" @change="onUpdate">
          <el-option label="JSON" value="json" />
          <el-option label="数组" value="array" />
          <el-option label="对象" value="object" />
          <el-option label="字符串" value="string" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="描述">
        <el-input 
          v-model="localNode.properties.description" 
          type="textarea" 
          :rows="2" 
          @input="onUpdate"
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
