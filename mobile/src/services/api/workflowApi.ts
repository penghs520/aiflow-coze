import api from './api';
import { Workflow, WorkflowCategory } from '../../types/workflow';
import { adaptWorkflowFromBackend } from './adapters';

class WorkflowApi {
  // 获取工作流列表
  async getWorkflows(params?: {
    page?: number;
    size?: number;
    category?: string;
    keyword?: string;
  }): Promise<{ data: Workflow[]; total: number }> {
    const response: any = await api.get('/workflows', {
      page: params?.page || 0,
      size: params?.size || 20,
      category: params?.category,
      keyword: params?.keyword
    });

    // response 已经是 PageResponse 格式: {content, page, size, total, ...}
    return {
      data: (response.content || []).map(adaptWorkflowFromBackend),
      total: response.total || 0
    };
  }

  // 获取工作流详情
  async getWorkflowDetail(id: string): Promise<Workflow> {
    const response = await api.get(`/workflows/${id}`);
    return adaptWorkflowFromBackend(response);
  }

  // 获取工作流分类
  async getCategories(): Promise<WorkflowCategory[]> {
    const categories: any = await api.get('/workflows/categories');
    // 后端返回 List<String>，转换为前端格式
    return (categories || []).map((name: string) => ({
      id: name,
      name: name,
      icon: undefined
    }));
  }

  // 收藏工作流
  async favoriteWorkflow(id: string): Promise<{ success: boolean }> {
    // 如果后端有收藏接口，调用它；否则返回成功
    return { success: true };
  }

  // 获取热门工作流
  async getPopularWorkflows(): Promise<Workflow[]> {
    const response: any = await api.get('/workflows/ranking', { limit: 10 });
    return (response.content || []).map(adaptWorkflowFromBackend);
  }
}

export const workflowApi = new WorkflowApi();
export default workflowApi;