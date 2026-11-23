import React, { useState } from 'react';
import { reportApi } from '../api/reportApi';
import { PageHeader } from '../components/layout/PageHeader';
import {
  FileSpreadsheet,
  FileText,
  Download,
  ShieldCheck,
  FileCheck,
} from 'lucide-react';

export const ReportsPage: React.FC = () => {
  const [downloadingExcel, setDownloadingExcel] = useState(false);
  const [downloadingCsv, setDownloadingCsv] = useState(false);
  const [sampleProductId, setSampleProductId] = useState('1');
  const [downloadingPdf, setDownloadingPdf] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  const handleExcel = async () => {
    try {
      setErrorMsg(null);
      setSuccessMsg(null);
      setDownloadingExcel(true);
      await reportApi.downloadExcel();
      setSuccessMsg('Excel compliance report exported successfully.');
    } catch (err: any) {
      console.error(err);
      setErrorMsg('Failed to generate Excel report. Please ensure backend service is running.');
    } finally {
      setDownloadingExcel(false);
    }
  };

  const handleCsv = async () => {
    try {
      setErrorMsg(null);
      setSuccessMsg(null);
      setDownloadingCsv(true);
      await reportApi.downloadCsv();
      setSuccessMsg('CSV violations report exported successfully.');
    } catch (err: any) {
      console.error(err);
      setErrorMsg('Failed to generate CSV export.');
    } finally {
      setDownloadingCsv(false);
    }
  };

  const handlePdf = async () => {
    try {
      setErrorMsg(null);
      setSuccessMsg(null);
      setDownloadingPdf(true);
      await reportApi.downloadProductPdf(Number(sampleProductId), `Product-${sampleProductId}`);
      setSuccessMsg(`PDF dossier for Product #${sampleProductId} exported successfully.`);
    } catch (err: any) {
      console.error(err);
      setErrorMsg(err.response?.status === 404
        ? `Product #${sampleProductId} not found. Please enter a valid product ID.`
        : 'Failed to generate PDF dossier.');
    } finally {
      setDownloadingPdf(false);
    }
  };

  return (
    <div className="space-y-5 pb-16">
      <PageHeader
        title="Compliance Reports & Regulatory Exports"
        description="Generate formal statutory inspection dossiers, complete catalog audit workbooks, and raw discrepancy feeds."
        breadcrumbs={[{ label: 'Reporting' }, { label: 'Reports' }]}
      />

      {errorMsg && (
        <div className="p-3 rounded-lg bg-rose-50 text-rose-800 border border-rose-200 dark:bg-rose-950/40 dark:text-rose-300 text-xs flex items-center justify-between">
          <span>{errorMsg}</span>
          <button onClick={() => setErrorMsg(null)} className="text-xs font-semibold hover:underline">Dismiss</button>
        </div>
      )}
      {successMsg && (
        <div className="p-3 rounded-lg bg-emerald-50 text-emerald-800 border border-emerald-200 dark:bg-emerald-950/40 dark:text-emerald-300 text-xs flex items-center justify-between">
          <span>{successMsg}</span>
          <button onClick={() => setSuccessMsg(null)} className="text-xs font-semibold hover:underline">Dismiss</button>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        {/* PDF Individual Product Dossier */}
        <div className="p-4 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 flex flex-col justify-between space-y-4">
          <div className="space-y-2">
            <div className="w-8 h-8 rounded-md bg-blue-50 dark:bg-blue-950 text-blue-800 flex items-center justify-center">
              <FileText className="w-4 h-4" />
            </div>
            <h3 className="text-xs font-bold text-slate-900 dark:text-slate-100">
              Statutory Inspection Dossier (PDF)
            </h3>
            <p className="text-[11px] text-slate-500 leading-relaxed">
              Official regulatory document with physical label crop evidence, OCR confidence scores, Rule 6 citation, and statutory disclaimer.
            </p>

            <div className="pt-2">
              <label className="block text-[11px] font-semibold text-slate-600 dark:text-slate-400 mb-1">
                Target Product ID
              </label>
              <input
                type="number"
                value={sampleProductId}
                onChange={(e) => setSampleProductId(e.target.value)}
                className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700"
              />
            </div>
          </div>

          <button
            onClick={handlePdf}
            disabled={downloadingPdf || !sampleProductId}
            className="w-full py-2 bg-blue-900 hover:bg-blue-800 text-white font-semibold text-xs rounded-md shadow-2xs flex items-center justify-center gap-1.5 transition disabled:opacity-50"
          >
            <Download className="w-3.5 h-3.5" />
            <span>{downloadingPdf ? 'Compiling PDF...' : 'Download Dossier PDF'}</span>
          </button>
        </div>

        {/* Excel Multi-Sheet Report */}
        <div className="p-4 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 flex flex-col justify-between space-y-4">
          <div className="space-y-2">
            <div className="w-8 h-8 rounded-md bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 flex items-center justify-center">
              <FileSpreadsheet className="w-4 h-4" />
            </div>
            <h3 className="text-xs font-bold text-slate-900 dark:text-slate-100">
              Full Compliance Audit (XLSX)
            </h3>
            <p className="text-[11px] text-slate-500 leading-relaxed">
              Multi-tab spreadsheet covering executive summary metrics, product catalog status, flagged violations, and seller risk ratings.
            </p>
          </div>

          <button
            onClick={handleExcel}
            disabled={downloadingExcel}
            className="w-full py-2 bg-blue-900 hover:bg-blue-800 text-white font-semibold text-xs rounded-md shadow-2xs flex items-center justify-center gap-1.5 transition disabled:opacity-50"
          >
            <Download className="w-3.5 h-3.5" />
            <span>{downloadingExcel ? 'Generating Workbook...' : 'Export Excel Workbook'}</span>
          </button>
        </div>

        {/* CSV Export */}
        <div className="p-4 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 flex flex-col justify-between space-y-4">
          <div className="space-y-2">
            <div className="w-8 h-8 rounded-md bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 flex items-center justify-center">
              <FileCheck className="w-4 h-4" />
            </div>
            <h3 className="text-xs font-bold text-slate-900 dark:text-slate-100">
              Violations Data Stream (CSV)
            </h3>
            <p className="text-[11px] text-slate-500 leading-relaxed">
              Flat, lightweight CSV export of non-compliance findings formatted for ingestion into external government reporting databases.
            </p>
          </div>

          <button
            onClick={handleCsv}
            disabled={downloadingCsv}
            className="w-full py-2 bg-slate-800 hover:bg-slate-900 text-white font-semibold text-xs rounded-md shadow-2xs flex items-center justify-center gap-1.5 transition disabled:opacity-50"
          >
            <Download className="w-3.5 h-3.5" />
            <span>{downloadingCsv ? 'Exporting CSV...' : 'Download Flat CSV'}</span>
          </button>
        </div>
      </div>
    </div>
  );
};
