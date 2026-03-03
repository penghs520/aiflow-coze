export interface WorkflowParameter {
  key: string;
  name: string;
  type: string;
  required: boolean;
  default?: any;
  options?: string[];
  constraints?: {
    maxSize?: string;
    formats?: string[];
  };
}

export interface WorkflowStatistics {
  usageCount: number;
  averageRating: number;
  favoriteCount: number;
}

export interface Workflow {
  id: string;
  name: string;
  description: string;
  category: string;
  tags: string[];
  coverUrl: string;
  previewVideoUrl?: string;
  basePoints: number;
  status: string;
  parameters: WorkflowParameter[];
  statistics: WorkflowStatistics;
}

export interface WorkflowCategory {
  id: string;
  name: string;
  icon?: string;
}