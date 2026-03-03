export interface TaskLog {
  timestamp: string;
  level: string;
  message: string;
}

export interface TaskResult {
  outputVideoUrl?: string;
  outputImageUrl?: string;
  thumbnailUrl?: string;
  duration?: string;
  resolution?: string;
  fileSize?: string;
}

export interface TaskTimestamps {
  createdAt: string;
  startedAt?: string;
  completedAt?: string;
}

export interface Task {
  id: string;
  workflowId: string;
  workflowName: string;
  userId: string;
  status: string;
  progress: number;
  estimatedPoints: number;
  actualPoints?: number;
  parameters: Record<string, any>;
  result?: TaskResult;
  errorMessage?: string;
  timestamps: TaskTimestamps;
  logs?: TaskLog[];
}

export type TaskStatus = 'pending' | 'queued' | 'processing' | 'completed' | 'failed' | 'cancelled';