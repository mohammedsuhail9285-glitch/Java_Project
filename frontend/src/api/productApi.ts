import api from './client';
import { Product, ExtractedField, OCRResult } from '../types';

export interface ProductFilterParams {
  category?: string;
  sellerId?: number;
  status?: string;
  search?: string;
  page?: number;
  size?: number;
}

export const productApi = {
  getProducts: async (params?: ProductFilterParams): Promise<{ content: Product[]; totalElements: number; totalPages: number }> => {
    const res = await api.get('/products', {
      params: {
        keyword: params?.search,
        status: params?.status,
        page: params?.page || 0,
        size: params?.size || 50,
      },
    });

    // Handle ApiResponse<Page<ProductDetailDto>> or plain Page
    const payload = res.data?.data || res.data;
    if (Array.isArray(payload)) {
      return { content: payload, totalElements: payload.length, totalPages: 1 };
    }
    return {
      content: payload?.content || [],
      totalElements: payload?.totalElements || 0,
      totalPages: payload?.totalPages || 1,
    };
  },

  getProductById: async (id: number): Promise<Product> => {
    const res = await api.get(`/products/${id}`);
    const data = res.data?.data || res.data;
    return data;
  },

  getExtractedFields: async (productId: number): Promise<ExtractedField[]> => {
    const res = await api.get<ExtractedField[]>(`/products/${productId}/fields`);
    return res.data || [];
  },

  getOcrResults: async (productId: number): Promise<OCRResult[]> => {
    const res = await api.get<OCRResult[]>(`/products/${productId}/ocr`);
    return res.data || [];
  },
};
