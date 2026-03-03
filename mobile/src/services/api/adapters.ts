// 任务状态映射：后端数字 <-> 前端字符串
export const taskStatusToBackend = (status: string): number => {
  const map: Record<string, number> = {
    'pending': 0,
    'queued': 1,
    'processing': 2,
    'completed': 3,
    'failed': 4,
    'cancelled': 5
  };
  return map[status] ?? 0;
};

export const taskStatusFromBackend = (status: number): string => {
  const map: Record<number, string> = {
    0: 'pending',
    1: 'queued',
    2: 'processing',
    3: 'completed',
    4: 'failed',
    5: 'cancelled'
  };
  return map[status] ?? 'pending';
};

// Workflow 数据转换
export const adaptWorkflowFromBackend = (raw: any): any => {
  return {
    id: raw.id,
    name: raw.name,
    description: raw.description,
    category: raw.category,
    tags: typeof raw.tags === 'string' ? JSON.parse(raw.tags || '[]') : (raw.tags || []),
    coverUrl: raw.coverUrl,
    previewVideoUrl: raw.previewVideoUrl,
    basePoints: raw.basePoints,
    status: raw.status === 1 ? 'active' : 'inactive',
    parameters: typeof raw.parameterDefinition === 'string'
      ? JSON.parse(raw.parameterDefinition || '[]')
      : (raw.parameterDefinition || []),
    statistics: {
      usageCount: raw.usageCount || 0,
      averageRating: parseFloat(raw.averageRating) || 0,
      favoriteCount: raw.favoriteCount || 0
    }
  };
};

// Task 数据转换
export const adaptTaskFromBackend = (raw: any): any => {
  return {
    id: raw.id,
    workflowId: raw.workflowId,
    workflowName: raw.workflowName || '未知工作流',
    userId: raw.userId?.toString(),
    status: taskStatusFromBackend(raw.status),
    progress: raw.progress || 0,
    estimatedPoints: raw.estimatedPoints,
    actualPoints: raw.actualPoints,
    parameters: typeof raw.parameters === 'string'
      ? JSON.parse(raw.parameters || '{}')
      : (raw.parameters || {}),
    result: typeof raw.result === 'string'
      ? JSON.parse(raw.result || '{}')
      : raw.result,
    errorMessage: raw.errorMessage,
    timestamps: {
      createdAt: raw.createdAt,
      startedAt: raw.startedAt,
      completedAt: raw.completedAt
    },
    logs: raw.logs || []
  };
};
