import api from './api';
import { User, LoginRequest, SmsCodeRequest, LoginResponse, PointRecord } from '../../types/user';

class UserApi {
  // 发送验证码
  async sendSmsCode(data: SmsCodeRequest): Promise<{ success: boolean }> {
    return api.post('/auth/sms-code', data);
  }

  // 登录/注册
  async login(data: LoginRequest): Promise<LoginResponse> {
    return api.post('/auth/login', data);
  }

  // 获取用户信息
  async getProfile(): Promise<User> {
    return api.get('/users/profile');
  }

  // 更新用户信息
  async updateProfile(data: Partial<User>): Promise<User> {
    return api.put('/users/profile', data);
  }

  // 获取资源点记录
  async getPointHistory(params?: {
    page?: number;
    size?: number;
  }): Promise<{ data: PointRecord[]; total: number }> {
    return api.get('/users/points/history', params);
  }
}

export const userApi = new UserApi();
export default userApi;