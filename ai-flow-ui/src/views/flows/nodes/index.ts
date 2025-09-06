import type LogicFlow from '@logicflow/core'
import { LLMNodeModel, LLMNodeView } from './LLMNode'
import { ClassifierNodeModel, ClassifierNodeView } from './ClassifierNode'

export interface NodeRegistry {
  type: string
  model: any
  view: any
}

export const nodeRegistries: NodeRegistry[] = [
  { type: 'llm', model: LLMNodeModel, view: LLMNodeView },
  { type: 'classifier', model: ClassifierNodeModel, view: ClassifierNodeView }
]

export function registerNodes(lf: LogicFlow) {
  nodeRegistries.forEach(registry => {
    lf.register(registry)
  })
}

export { LLMNodeModel, LLMNodeView } from './LLMNode'
export { ClassifierNodeModel, ClassifierNodeView } from './ClassifierNode'
