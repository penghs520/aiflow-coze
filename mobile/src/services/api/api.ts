import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { API_BASE_URL, STORAGE_KEYS } from '../../utils/constants';
import AsyncStorage from '@react-native-async-storage/async-storage';

class ApiService {
  private instance: AxiosInstance;

  constructor() {
    this.instance = axios.create({
      baseURL: API_BASE_URL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    // 请求拦截器
    this.instance.interceptors.request.use(
      async (config) => {
        const token = await AsyncStorage.getItem(STORAGE_KEYS.USER_TOKEN);
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // 响应拦截器
    this.instance.interceptors.response.use(
      (response: AxiosResponse) => {
        // 后端返回 Result<T> 格式: {code, message, data, timestamp}
        // 自动解包 data 字段
        if (response.data && typeof response.data === 'object' && 'data' in response.data) {
          return response.data.data;
        }
        return response.data;
      },
      async (error) => {
        const originalRequest = error.config;
        
        // 处理401错误（Token过期）
        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;
          
          try {
            const refreshToken = await AsyncStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);
            if (refreshToken) {
              const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
                refreshToken,
              });
              
              const { token, refreshToken: newRefreshToken } = response.data.data;
              await AsyncStorage.setItem(STORAGE_KEYS.USER_TOKEN, token);
              await AsyncStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, newRefreshToken);
              
              originalRequest.headers.Authorization = `Bearer ${token}`;
              return this.instance(originalRequest);
            }
          } catch (refreshError) {
            // 刷新Token失败，跳转到登录页
            await AsyncStorage.removeItem(STORAGE_KEYS.USER_TOKEN);
            await AsyncStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
            // 这里可以添加导航到登录页的逻辑
          }
        }
        
        // 处理其他错误
        const errorMessage = error.response?.data?.message || '网络请求失败';
        return Promise.reject(new Error(errorMessage));
      }
    );
  }

  // GET请求
  async get<T>(url: string, params?: any): Promise<T> {
    return this.instance.get(url, { params });
  }

  // POST请求
  async post<T>(url: string, data?: any): Promise<T> {
    return this.instance.post(url, data);
  }

  // PUT请求
  async put<T>(url: string, data?: any): Promise<T> {
    return this.instance.put(url, data);
  }

  // DELETE请求
  async delete<T>(url: string, params?: any): Promise<T> {
    return this.instance.delete(url, { params });
  }

  // 上传文件
  async upload<T>(url: string, formData: FormData): Promise<T> {
    return this.instance.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }
}

export const api = new ApiService();
export default api;