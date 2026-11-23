import api from './client';
import { User, Role } from '../types';

export interface CreateUserPayload {
  username: string;
  email: string;
  password: string;
  fullName: string;
  role: Role;
}

export const userApi = {
  getUsers: async (): Promise<User[]> => {
    const res = await api.get<any>('/users');
    return res.data?.data || res.data || [];
  },

  getUserById: async (id: number): Promise<User> => {
    const res = await api.get<any>(`/users/${id}`);
    return res.data?.data || res.data;
  },

  createUser: async (payload: CreateUserPayload): Promise<User> => {
    const res = await api.post<any>('/users', payload);
    return res.data?.data || res.data;
  },

  toggleStatus: async (id: number): Promise<User> => {
    const res = await api.patch<any>(`/users/${id}/toggle-status`);
    return res.data?.data || res.data;
  },

  updateRole: async (id: number, role: Role): Promise<User> => {
    const res = await api.patch<any>(`/users/${id}/role`, { role });
    return res.data?.data || res.data;
  },

  unlockUser: async (id: number): Promise<User> => {
    const res = await api.post<any>(`/users/${id}/unlock`);
    return res.data?.data || res.data;
  },
};
