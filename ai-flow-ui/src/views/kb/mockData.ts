export interface MockKnowledgeBase {
  id: string
  name: string
  description: string
  tags: string[]
  vectorModel: string
  status: boolean
  createdAt: string
  updatedAt: string
}

export const mockKbList: MockKnowledgeBase[] = [
  {
    id: '1',
    name: '产品知识库',
    description: '包含公司所有产品的详细信息和常见问题解答',
    tags: ['产品', 'FAQ', '技术支持'],
    vectorModel: 'openai',
    status: true,
    createdAt: '2024-01-15T10:00:00Z',
    updatedAt: '2024-01-20T14:30:00Z'
  },
  {
    id: '2',
    name: '技术文档库',
    description: '技术架构、API文档、开发指南等技术相关内容',
    tags: ['技术', 'API', '开发'],
    vectorModel: 'zhipu',
    status: true,
    createdAt: '2024-01-10T09:00:00Z',
    updatedAt: '2024-01-18T16:45:00Z'
  },
  {
    id: '3',
    name: '客户服务知识库',
    description: '客户服务流程、常见问题、解决方案等',
    tags: ['客服', '问题', '解决方案'],
    vectorModel: 'openai',
    status: false,
    createdAt: '2024-01-05T11:00:00Z',
    updatedAt: '2024-01-12T10:20:00Z'
  },
  {
    id: '4',
    name: '销售培训资料库',
    description: '销售技巧、产品介绍、竞品分析等销售相关材料',
    tags: ['销售', '培训', '竞品'],
    vectorModel: 'zhipu',
    status: true,
    createdAt: '2024-01-08T13:00:00Z',
    updatedAt: '2024-01-16T15:10:00Z'
  }
] 