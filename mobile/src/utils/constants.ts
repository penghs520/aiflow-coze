export const API_BASE_URL = 'https://api.ai-workflow-platform.com/api/v1';

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