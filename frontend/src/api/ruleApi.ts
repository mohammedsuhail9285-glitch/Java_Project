import api from './client';
import { Rule } from '../types';

export const ruleApi = {
  getRules: async (): Promise<Rule[]> => {
    const res = await api.get<any>('/rules');
    return res.data?.data || res.data || [];
  },

  getRuleById: async (id: number): Promise<Rule> => {
    const res = await api.get<any>(`/rules/${id}`);
    return res.data?.data || res.data;
  },

  updateRule: async (id: number, rule: Partial<Rule>): Promise<Rule> => {
    const res = await api.put<any>(`/rules/${id}`, rule);
    return res.data?.data || res.data;
  },

  createRule: async (rule: Partial<Rule>): Promise<Rule> => {
    const res = await api.post<any>('/rules', rule);
    return res.data?.data || res.data;
  },

  simulateRule: async (rule: Partial<Rule>, productId?: number, sampleSize = 10): Promise<any> => {
    const res = await api.post('/rules/simulator/run', {
      rule,
      productId,
      sampleSize,
    });
    return res.data;
  },
};
