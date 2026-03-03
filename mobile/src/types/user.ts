export interface User {
  id: string;
  phone: string;
  nickname: string;
  avatarUrl?: string;
  pointsBalance: number;
  status: number;
  lastLoginAt?: string;
  createdAt: string;
}

export interface LoginRequest {
  phone: string;
  code: string;
}

export interface SmsCodeRequest {
  phone: string;
  type: string;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  user: User;
}

export interface PointRecord {
  id: string;
  changeType: number;
  changeAmount: number;
  balanceAfter: number;
  relatedId?: string;
  description: string;
  createdAt: string;
}