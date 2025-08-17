<template>
  <el-container class="layout">
    <el-aside :width="asideWidth" class="sidebar">
      <div class="logo">AI Flow</div>
      <el-menu :default-active="active" @select="onSelect" router background-color="#0f233a" text-color="#c0c4cc" active-text-color="#ffffff" class="menu">
        <el-sub-menu index="1">
          <template #title>
            <el-icon><Grid /></el-icon>
            <span>AI 应用管理</span>
          </template>
          <el-menu-item index="/apps">应用列表</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/kb">
          <el-icon><Collection /></el-icon>
          <span>AI 知识库</span>
        </el-menu-item>
        <el-menu-item index="/flows">
          <el-icon><Share /></el-icon>
          <span>AI 流程设计</span>
        </el-menu-item>
        <el-menu-item index="/models">
          <el-icon><Cpu /></el-icon>
          <span>AI 模型配置</span>
        </el-menu-item>
        <el-menu-item index="/ocr">
          <el-icon><Document /></el-icon>
          <span>OCR 识别</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="content">
      <el-header class="header">
        <div class="header-title">AI 流程配置工具</div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Grid, Collection, Share, Cpu, Document } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const active = computed(() => route.path)

// Slimmer fixed width to match reference
const asideWidth = '200px'

function onSelect(index: string) {
  if (index.startsWith('/')) {
    router.push(index)
  }
}
</script>

<style scoped>
.layout {
  height: 100vh;
}
.sidebar {
  background: #0f233a;
  color: #c0c4cc;
  display: flex;
  flex-direction: column;
}
.logo {
  height: 56px;
  display: flex;
  align-items: center;
  padding-left: 16px;
  font-weight: 700;
  color: #ffffff;
}
.menu :deep(.el-sub-menu__title),
.menu :deep(.el-menu-item) {
  height: 44px;
  line-height: 44px;
}
.header {
  background: #ffffff;
  border-bottom: 1px solid var(--el-border-color-lighter);
}
.header-title {
  font-size: 16px;
  font-weight: 600;
}
.el-aside {
  border-right: none;
}
.content {
  background: #f5f7fa;
}
</style> 