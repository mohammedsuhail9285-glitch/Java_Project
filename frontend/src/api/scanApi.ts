import api from './client';
import { ScanJob } from '../types';

export const scanApi = {
  getScanJobs: async (): Promise<ScanJob[]> => {
    try {
      const res = await api.get<any>('/scan-jobs');
      const payload = res.data?.data || res.data;
      if (Array.isArray(payload)) return payload;
      if (payload?.content && Array.isArray(payload.content)) return payload.content;
      return [];
    } catch {
      return [];
    }
  },

  getScanJobById: async (id: number): Promise<ScanJob> => {
    const res = await api.get<any>(`/scan-jobs/${id}`);
    return res.data?.data || res.data;
  },

  createManualScan: async (payload: {
    productName: string;
    brandId?: number;
    sellerId?: number;
    categoryId?: number;
    declaredMrp?: number;
    listingPrice?: number;
    description?: string;
    countryOfOrigin?: string;
    productUrl?: string;
    imageFile?: File;
  }): Promise<ScanJob> => {
    const formData = new FormData();
    formData.append('productName', payload.productName);
    if (payload.brandId) formData.append('brandId', String(payload.brandId));
    if (payload.sellerId) formData.append('sellerId', String(payload.sellerId));
    if (payload.categoryId) formData.append('categoryId', String(payload.categoryId));
    if (payload.declaredMrp) formData.append('declaredMrp', String(payload.declaredMrp));
    if (payload.listingPrice) formData.append('listingPrice', String(payload.listingPrice));
    if (payload.description) formData.append('description', payload.description);
    if (payload.countryOfOrigin) formData.append('countryOfOrigin', payload.countryOfOrigin);
    if (payload.productUrl) formData.append('productUrl', payload.productUrl);
    if (payload.imageFile) formData.append('file', payload.imageFile);

    const res = await api.post<any>('/ingestion/manual', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data?.data || res.data;
  },

  uploadFileScan: async (file: File, fileType: 'CSV' | 'JSON'): Promise<ScanJob> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('type', fileType);

    const res = await api.post<any>('/ingestion/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data?.data || res.data;
  },

  triggerUrlScan: async (url: string): Promise<ScanJob> => {
    const res = await api.post<any>('/ingestion/url', { url });
    return res.data?.data || res.data;
  },

  triggerDualScan: async (payload: {
    labelImage?: File;
    listingScreenshot?: File;
    listingText?: string;
    productName?: string;
    productUrl?: string;
    declaredMrp?: number;
    listingPrice?: number;
    countryOfOrigin?: string;
  }): Promise<ScanJob> => {
    const formData = new FormData();
    if (payload.labelImage) formData.append('labelImage', payload.labelImage);
    if (payload.listingScreenshot) formData.append('listingScreenshot', payload.listingScreenshot);
    if (payload.listingText) formData.append('listingText', payload.listingText);
    if (payload.productName) formData.append('productName', payload.productName);
    if (payload.productUrl) formData.append('productUrl', payload.productUrl);
    if (payload.declaredMrp) formData.append('declaredMrp', String(payload.declaredMrp));
    if (payload.listingPrice) formData.append('listingPrice', String(payload.listingPrice));
    if (payload.countryOfOrigin) formData.append('countryOfOrigin', payload.countryOfOrigin);

    const res = await api.post<any>('/ingestion/dual-scan', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data?.data || res.data;
  },

  uploadImageScan: async (imageFile: File, productName?: string): Promise<ScanJob> => {
    const formData = new FormData();
    formData.append('image', imageFile);
    if (productName) formData.append('productName', productName);

    const res = await api.post<any>('/ingestion/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data?.data || res.data;
  },

  triggerSandboxCrawler: async (batchSize = 5, platform = 'RetailBharat'): Promise<ScanJob> => {
    const res = await api.post<any>('/ingestion/crawl-sandbox', {
      batchSize,
      platform,
    });
    return res.data?.data || res.data;
  },
};
