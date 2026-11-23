import api from './client';

export const reportApi = {
  downloadProductPdf: async (productId: number, productName?: string) => {
    const res = await api.get(`/reports/pdf/product/${productId}`, {
      responseType: 'blob',
    });
    const blob = new Blob([res.data], { type: 'application/pdf' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `LMPC-Compliance-Report-${productName ? productName.replace(/[^a-zA-Z0-9]/g, '_') : productId}.pdf`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  },

  downloadExcel: async () => {
    const res = await api.get('/reports/excel', {
      responseType: 'blob',
    });
    const blob = new Blob([res.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'RegTech-Sentinel-Violations-Report.xlsx';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  },

  downloadCsv: async () => {
    const res = await api.get('/reports/csv', {
      responseType: 'blob',
    });
    const blob = new Blob([res.data], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'RegTech-Sentinel-Violations.csv';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  },
};
