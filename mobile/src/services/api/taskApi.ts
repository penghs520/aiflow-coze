import api from './api';
import { Task } from '../../types/task';

interface CreateTaskRequest {
  workflowId: string;
  parameters: Record<string, any>;
  settings?: {
    priority?: string;
    notifyWhenComplete?: boolean;
  };
}

class TaskApi {
  // 提交任务
  async createTask(data: CreateTaskRequest): Promise<Task> {
    return api.post('/tasks', data);
  }

  // 获取任务列表
  async getTasks(params?: {
    page?: number;
    size?: number;
    status?: string;
  }): Promise<{ data: Task[]; total: number }> {
    return api.get('/tasks', params);
  }

  // 获取任务详情
  async getTaskDetail(id: string): Promise<Task> {
    return api.get(`/tasks/${id}`);
  }

  // 取消任务
  async cancelTask(id: string): Promise<{ success: boolean }> {
    return api.post(`/tasks/${id}/cancel`);
  }

  // 重新运行任务
  async retryTask(id: string): Promise<Task> {
    return api.post(`/tasks/${id}/retry`);
  }
}

export const taskApi = new TaskApi();
export default taskApi;