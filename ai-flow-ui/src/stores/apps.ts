import { defineStore } from 'pinia'

export type AppType = 'simple' | 'advanced'

export interface AiApp {
  id: string
  name: string
  description: string
  iconUrl?: string
  appType: AppType
  model?: string
  prompt?: string
  openingRemark?: string
  createdAt: number
}

interface AppsState {
  apps: AiApp[]
}

const STORAGE_KEY = 'ai_flow_apps'

function generateId(): string {
  return Math.random().toString(36).slice(2, 10)
}

function loadFromStorage(): AiApp[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? (JSON.parse(raw) as AiApp[]) : []
  } catch {
    return []
  }
}

function saveToStorage(apps: AiApp[]): void {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(apps))
  } catch {
    // ignore
  }
}

export const useAppsStore = defineStore('apps', {
  state: (): AppsState => ({
    apps: loadFromStorage()
  }),
  getters: {
    findById: (state) => (id: string) => state.apps.find(a => a.id === id)
  },
  actions: {
    createApp(payload: Omit<AiApp, 'id' | 'createdAt'>): AiApp {
      const app: AiApp = { id: generateId(), createdAt: Date.now(), ...payload }
      this.apps.unshift(app)
      saveToStorage(this.apps)
      return app
    },
    updateApp(id: string, update: Partial<AiApp>): void {
      const idx = this.apps.findIndex(a => a.id === id)
      if (idx !== -1) {
        this.apps[idx] = { ...this.apps[idx], ...update }
        saveToStorage(this.apps)
      }
    },
    removeApp(id: string): void {
      this.apps = this.apps.filter(a => a.id !== id)
      saveToStorage(this.apps)
    }
  }
}) 