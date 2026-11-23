import api from './client';

export interface AiMonitoringData {
  aiServiceOnline: boolean;
  activeEngine: string;
  tesseractAvailable: boolean;
  aiServiceUrl: string;
  totalOcrRuns: number;
  avgOcrConfidence: number;
  engineDistribution: Record<string, number>;
  nlpModel: string;
  visionModel: string;
  status: string;
  totalReviews?: number;
  confirmedViolations?: number;
  dismissedFalsePositives?: number;
  aiCorrectionRate?: number;
  regulatorAgreementRate?: number;
}

export const aiMonitoringApi = {
  getMonitoringMetrics: async (): Promise<AiMonitoringData> => {
    const res = await api.get<AiMonitoringData>('/ai/monitoring');
    return res.data;
  },
};
