import api from './client';
import { Violation } from '../types';

export interface ViolationFilterParams {
  status?: string;
  severity?: string;
  ruleId?: string;
  productId?: number;
  page?: number;
  size?: number;
}

export const violationApi = {
  getViolations: async (params?: ViolationFilterParams): Promise<{ content: Violation[]; totalElements: number; totalPages: number }> => {
    const res = await api.get('/violations', { params });
    if (Array.isArray(res.data)) {
      return { content: res.data, totalElements: res.data.length, totalPages: 1 };
    }
    return {
      content: res.data.content || [],
      totalElements: res.data.totalElements || 0,
      totalPages: res.data.totalPages || 1,
    };
  },

  getViolationById: async (id: number): Promise<Violation> => {
    const res = await api.get<Violation>(`/violations/${id}`);
    return res.data;
  },

  getViolationsByProduct: async (productId: number): Promise<Violation[]> => {
    const res = await api.get<Violation[]>(`/violations/product/${productId}`);
    return res.data;
  },

  submitReview: async (id: number, action: 'CONFIRM' | 'DISMISS' | 'RESOLVE' | 'REQUEST_INFO', comment: string, remediationAction?: string): Promise<Violation> => {
    const res = await api.post<Violation>(`/violations/${id}/review`, {
      action,
      comment,
      remediationAction,
    });
    return res.data;
  },
};
