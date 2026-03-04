import { Platform } from 'react-native';

// 根据平台和环境变量设置 API 地址
const getApiBaseUrl = () => {
  if (!__DEV__) {
    return 'https://api.ai-workflow-platform.com/api';  // 生产环境
  }

  // 开发环境 - 使用局域网 IP 以支持 Expo Go 真机调试
  // 如果使用模拟器，请改为 10.0.2.2 (Android) 或 localhost (iOS)
  return 'http://192.168.2.4:8001/api';
};

export const API_BASE_URL = getApiBaseUrl();

export const STORAGE_KEYS = {
  USER_TOKEN: 'user_token',
  REFRESH_TOKEN: 'refresh_token',
  USER_INFO: 'user_info',
  THEME_MODE: 'theme_mode',
};

export const WORKFLOW_CATEGORIES = [
  { id: 'hot', name: '热门', icon: '🔥' },
  { id: 'self_media', name: '自媒体', icon: '📱' },
  { id: 'business', name: '商业宣传', icon: '💼' },
  { id: 'novel', name: '小说推文', icon: '📖' },
  { id: 'anime', name: '动漫', icon: '🎨' },
  { id: 'scifi', name: '科幻', icon: '🚀' },
  { id: 'portrait', name: '写真', icon: '📸' },
  { id: 'pet', name: '萌宠', icon: '🐾' },
];

export const TASK_STATUS = {
  PENDING: 'pending',
  QUEUED: 'queued',
  PROCESSING: 'processing',
  COMPLETED: 'completed',
  FAILED: 'failed',
  CANCELLED: 'cancelled',
};

export const TASK_STATUS_LABEL = {
  pending: '待提交',
  queued: '排队中',
  processing: '处理中',
  completed: '已完成',
  failed: '失败',
  cancelled: '已取消',
};

export const COLORS = {
  primary: '#007AFF',
  secondary: '#5856D6',
  success: '#34C759',
  warning: '#FF9500',
  error: '#FF3B30',
  background: '#121212',
  surface: '#1E1E1E',
  text: '#FFFFFF',
  textSecondary: '#8E8E93',
  border: '#38383A',
};