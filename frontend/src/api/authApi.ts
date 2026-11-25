import api from './client';
import {
  AuthResponse,
  User,
  AuthConfig,
  OtpRequestResponse,
  RegisterRequest,
  ResetPasswordRequest,
  ChangePasswordRequest,
  UpdateProfileRequest
} from '../types';

export const authApi = {
  login: async (username: string, password: string, rememberMe = false): Promise<AuthResponse> => {
    const res = await api.post<any>('/auth/login', { username, password, rememberMe });
    const payload = res.data?.data || res.data;
    return payload;
  },

  googleDemoLogin: async (email: string, fullName?: string, role?: string): Promise<AuthResponse> => {
    const res = await api.post<any>('/auth/google/demo', { email, fullName, role });
    const payload = res.data?.data || res.data;
    return payload;
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const res = await api.post<any>('/auth/register', data);
    const payload = res.data?.data || res.data;
    return payload;
  },

  verifyEmail: async (token: string): Promise<AuthResponse> => {
    const res = await api.post<any>(`/auth/verify-email?token=${encodeURIComponent(token)}`);
    const payload = res.data?.data || res.data;
    return payload;
  },

  resendVerification: async (email: string): Promise<{ message: string }> => {
    const res = await api.post<any>('/auth/resend-verification', { email });
    const payload = res.data?.data || res.data;
    return payload;
  },

  forgotPassword: async (email: string): Promise<{ message: string }> => {
    const res = await api.post<any>('/auth/forgot-password', { email });
    const payload = res.data?.data || res.data;
    return payload;
  },

  resetPassword: async (data: ResetPasswordRequest): Promise<{ message: string }> => {
    const res = await api.post<any>('/auth/reset-password', data);
    const payload = res.data?.data || res.data;
    return payload;
  },

  changePassword: async (data: ChangePasswordRequest): Promise<void> => {
    await api.post<any>('/auth/change-password', data);
  },

  updateProfile: async (data: UpdateProfileRequest): Promise<User> => {
    const res = await api.put<any>('/auth/profile', data);
    const payload = res.data?.data || res.data;
    return payload;
  },

  getAuthConfig: async (): Promise<AuthConfig> => {
    const res = await api.get<any>('/auth/config');
    const payload = res.data?.data || res.data;
    return payload;
  },

  requestOtp: async (identifier: string): Promise<OtpRequestResponse> => {
    const res = await api.post<any>('/auth/otp/request', { identifier });
    const payload = res.data?.data || res.data;
    return payload;
  },

  verifyOtp: async (identifier: string, code: string): Promise<AuthResponse> => {
    const res = await api.post<any>('/auth/otp/verify', { identifier, code });
    const payload = res.data?.data || res.data;
    return payload;
  },

  getCurrentUser: async (): Promise<User> => {
    const res = await api.get<any>('/auth/me');
    const payload = res.data?.data || res.data;
    return payload;
  },

  logout: async (): Promise<void> => {
    try {
      await api.post('/auth/logout');
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
  },
};
