import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    component: () => import('../layouts/AppLayout.vue'),
    children: [
      { path: '', redirect: '/apps' },
      { path: 'apps', name: 'apps', component: () => import('../views/apps/AppsList.vue') },
      { path: 'apps/:id/edit', name: 'appEdit', component: () => import('../views/apps/AppEditor.vue'), props: true },
      { path: 'kb', name: 'kb', component: () => import('../views/kb/Index.vue') },
      { path: 'flows', name: 'flows', component: () => import('../views/flows/Index.vue') },
      { path: 'models', name: 'models', component: () => import('../views/models/Index.vue') },
      { path: 'ocr', name: 'ocr', component: () => import('../views/ocr/Index.vue') },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 