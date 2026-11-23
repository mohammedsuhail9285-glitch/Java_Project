import api from './client';
import {
  FreightOverview,
  MaritimePort,
  VesselClass,
  TradeRoute,
  FreightForecast,
  ProcurementPlan,
  CharterOptimizationRequest,
  CharterOptimizationResponse,
  ModelAccuracyMetrics,
} from '../types';

export const freightApi = {
  getOverview: async (): Promise<FreightOverview> => {
    const res = await api.get('/freight/overview');
    return res.data;
  },

  getPorts: async (): Promise<MaritimePort[]> => {
    const res = await api.get('/freight/ports');
    return res.data;
  },

  getVessels: async (): Promise<VesselClass[]> => {
    const res = await api.get('/freight/vessels');
    return res.data;
  },

  getRoutes: async (): Promise<TradeRoute[]> => {
    const res = await api.get('/freight/routes');
    return res.data;
  },

  getForecasts: async (): Promise<FreightForecast[]> => {
    const res = await api.get('/freight/forecasts');
    return res.data;
  },

  getPlans: async (): Promise<ProcurementPlan[]> => {
    const res = await api.get('/freight/plans');
    return res.data;
  },

  getPlanByRef: async (ref: string): Promise<ProcurementPlan> => {
    const res = await api.get(`/freight/plans/${ref}`);
    return res.data;
  },

  optimizeCharter: async (
    request: CharterOptimizationRequest
  ): Promise<CharterOptimizationResponse> => {
    const res = await api.post('/freight/optimize', request);
    return res.data;
  },

  downloadPlanPdf: (id: number): string => {
    return `/api/v1/freight/plans/${id}/pdf`;
  },

  downloadExcelWorkbook: (): string => {
    return '/api/v1/freight/export/excel';
  },

  downloadCsvReport: (): string => {
    return '/api/v1/freight/export/csv';
  },

  getModelAccuracy: async (): Promise<ModelAccuracyMetrics> => {
    const res = await api.get('/freight/model/accuracy');
    return res.data;
  },
};


