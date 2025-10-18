import type LogicFlow from '@logicflow/core'
import { StartNode, StartNodeModel } from './StartNode'
import { LLMNode, LLMNodeModel } from './LLMNode'
import { ClassifierNode, ClassifierNodeModel } from './ClassifierNode'
import { KnowledgeBaseNode, KnowledgeBaseNodeModel } from './KnowledgeBaseNode'
import { ConditionNode, ConditionNodeModel } from './ConditionNode'
import { ScriptNode, ScriptNodeModel } from './ScriptNode'
import { HttpNode, HttpNodeModel } from './HttpNode'
import { EndNode, EndNodeModel } from './EndNode'
import { OrganizerNode, OrganizerNodeModel } from './OrganizerNode'

export interface NodeRegistry {
  type: string
  model: any
  view: any
}

export const nodeRegistries: NodeRegistry[] = [
  { type: 'start', model: StartNodeModel, view: StartNode },
  { type: 'llm', model: LLMNodeModel, view: LLMNode },
  { type: 'classifier', model: ClassifierNodeModel, view: ClassifierNode },
  { type: 'knowledge-base', model: KnowledgeBaseNodeModel, view: KnowledgeBaseNode },
  { type: 'condition', model: ConditionNodeModel, view: ConditionNode },
  { type: 'script', model: ScriptNodeModel, view: ScriptNode },
  { type: 'http', model: HttpNodeModel, view: HttpNode },
  { type: 'organizer', model: OrganizerNodeModel, view: OrganizerNode },
  { type: 'end', model: EndNodeModel, view: EndNode }
]

export function registerCustomNodes(lf: LogicFlow) {
  nodeRegistries.forEach(registry => {
    lf.register(registry)
  })
}

// 导出所有节点
export * from './StartNode'
export * from './LLMNode'
export * from './ClassifierNode'
export * from './KnowledgeBaseNode'
export * from './ConditionNode'
export * from './ScriptNode'
export * from './HttpNode'
export * from './OrganizerNode'
export * from './EndNode'
