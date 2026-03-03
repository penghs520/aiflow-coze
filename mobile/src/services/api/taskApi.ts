import api from './api';
import { Task } from '../../types/task';
import { adaptTaskFromBackend, taskStatusToBackend } from './adapters';

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
    const response = await api.post('/tasks', {
      workflowId: data.workflowId,
      parameters: data.parameters,
      settings: data.settings
    });
    return adaptTaskFromBackend(response);
  }

  // 获取任务列表
  async getTasks(params?: {
    page?: number;
    size?: number;
    status?: string;
  }): Promise<{ data: Task[]; total: number }> {
    const requestParams: any = {
      page: params?.page || 0,
      size: params?.size || 20
    };

    // 状态转换：字符串 -> 数字
    if (params?.status && params.status !== 'all') {
      requestParams.status = taskStatusToBackend(params.status);
    }

    const response: any = await api.get('/tasks', requestParams);

    return {
      data: (response.content || []).map(adaptTaskFromBackend),
      total: response.total || 0
    };
  }

  // 获取任务详情
  async getTaskDetail(id: string): Promise<Task> {
    const response = await api.get(`/tasks/${id}`);
    return adaptTaskFromBackend(response);
  }

  // 取消任务
  async cancelTask(id: string): Promise<{ success: boolean }> {
    await api.post(`/tasks/${id}/cancel`);
    return { success: true };
  }

  // 重新运行任务
  async retryTask(id: string): Promise<Task> {
    await api.post(`/tasks/${id}/retry`);
    // 重试后重新获取任务详情
    return this.getTaskDetail(id);
  }
}

export const taskApi = new TaskApi();
export default taskApi;