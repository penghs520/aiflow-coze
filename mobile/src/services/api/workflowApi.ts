import api from './api';
import { Workflow, WorkflowCategory } from '../../types/workflow';

class WorkflowApi {
  // 获取工作流列表
  async getWorkflows(params?: {
    page?: number;
    size?: number;
    category?: string;
    sort?: string;
  }): Promise<{ data: Workflow[]; total: number }> {
    return api.get('/workflows', params);
  }

  // 获取工作流详情
  async getWorkflowDetail(id: string): Promise<Workflow> {
    return api.get(`/workflows/${id}`);
  }

  // 获取工作流分类
  async getCategories(): Promise<WorkflowCategory[]> {
    return api.get('/workflows/categories');
  }

  // 收藏工作流
  async favoriteWorkflow(id: string): Promise<{ success: boolean }> {
    return api.post(`/workflows/${id}/favorite`);
  }

  // 获取热门工作流
  async getPopularWorkflows(): Promise<Workflow[]> {
    return api.get('/workflows/ranking');
  }
}

export const workflowApi = new WorkflowApi();
export default workflowApi;