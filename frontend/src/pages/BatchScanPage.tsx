import React, { useState, useEffect } from 'react';
import { scanApi } from '../api/scanApi';
import { ScanJob } from '../types';
import { PageHeader } from '../components/layout/PageHeader';
import {
  FileUp,
  Bot,
  Play,
  CheckCircle2,
  AlertCircle,
  Clock,
  RefreshCw,
  UploadCloud,
  FileSpreadsheet,
  Download,
  ListPlus,
  ExternalLink,
} from 'lucide-react';
import { Link } from 'react-router-dom';

export const BatchScanPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'FILE' | 'URLS' | 'SANDBOX'>('FILE');
  const [scanJobs, setScanJobs] = useState<ScanJob[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  // File Upload State
  const [uploadFile, setUploadFile] = useState<File | null>(null);
  const [uploadType, setUploadType] = useState<'CSV' | 'JSON'>('CSV');

  // Multiple URLs State
  const [multipleUrls, setMultipleUrls] = useState<string>(
    'https://retailbharat.in/p/swarna-atta-5kg\nhttps://apexglobalkart.com/p/vedika-facewash-150\nhttps://westernimports.biz/p/apex-sheetmask-pack5'
  );

  // Sandbox State
  const [sandboxBatch, setSandboxBatch] = useState(5);
  const [sandboxPlatform, setSandboxPlatform] = useState('RetailBharat');

  const loadJobs = async () => {
    try {
      const jobs = await scanApi.getScanJobs();
      setScanJobs(jobs);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadJobs();
    const interval = setInterval(loadJobs, 4000);
    return () => clearInterval(interval);
  }, []);

  const handleFileUpload = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!uploadFile) {
      setMessage({ type: 'error', text: 'Please select a CSV or JSON catalog file.' });
      return;
    }
    if (uploadFile.size > 25 * 1024 * 1024) {
      setMessage({ type: 'error', text: 'File size exceeds maximum batch upload limit of 25MB.' });
      return;
    }
    const lowerName = uploadFile.name.toLowerCase();
    if (uploadType === 'CSV' && !lowerName.endsWith('.csv')) {
      setMessage({ type: 'error', text: 'Selected file is not a valid CSV (.csv) document.' });
      return;
    }
    if (uploadType === 'JSON' && !lowerName.endsWith('.json')) {
      setMessage({ type: 'error', text: 'Selected file is not a valid JSON (.json) document.' });
      return;
    }

    setSubmitting(true);
    setMessage(null);
    try {
      const job = await scanApi.uploadFileScan(uploadFile, uploadType);
      setMessage({ type: 'success', text: `Batch Scan Job #${job.id} queued successfully (${uploadFile.name}).` });
      setUploadFile(null);
      loadJobs();
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Batch file upload failed.' });
    } finally {
      setSubmitting(false);
    }
  };

  const handleMultipleUrls = async (e: React.FormEvent) => {
    e.preventDefault();
    const urls = multipleUrls
      .split('\n')
      .map((u) => u.trim())
      .filter((u) => u.length > 0);

    if (urls.length === 0) {
      setMessage({ type: 'error', text: 'Please enter at least one product URL.' });
      return;
    }

    setSubmitting(true);
    setMessage(null);
    let successCount = 0;
    try {
      for (const url of urls) {
        await scanApi.triggerUrlScan(url);
        successCount++;
      }
      setMessage({ type: 'success', text: `Successfully triggered ${successCount} scan jobs for specified URLs.` });
      loadJobs();
    } catch (err: any) {
      setMessage({ type: 'error', text: `Failed after queueing ${successCount} URLs: ${err.message}` });
    } finally {
      setSubmitting(false);
    }
  };

  const handleSandboxSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setMessage(null);
    try {
      const job = await scanApi.triggerSandboxCrawler(sandboxBatch, sandboxPlatform);
      setMessage({
        type: 'success',
        text: `Sandbox simulated crawl job #${job.id} initiated (${sandboxBatch} commodities across ${sandboxPlatform}).`,
      });
      loadJobs();
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Sandbox crawl failed.' });
    } finally {
      setSubmitting(false);
    }
  };

  const handleDownloadCsvTemplate = () => {
    const csvContent =
      'name,declared_mrp,listing_price,country_of_origin,brand,seller,category,description\n' +
      'Swarna Shakti Atta 5kg,320.00,299.00,India,Swarna Shakti Foods,RetailBharat,Packaged Food & Staples,100% stone ground whole wheat flour\n' +
      'Vedika Face Wash 150ml,185.00,160.00,India,Vedika Herbal Care,Apex Global Traders,Personal Care,Herbal neem foaming face wash\n' +
      'Himalayan Honey 500g,299.00,349.00,India,Himalayan Nectar,RetailBharat,Food,Raw unprocessed honey';

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'lmpc_batch_catalog_template.csv');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="space-y-5 pb-16">
      <PageHeader
        title="Batch Compliance Pipeline"
        description="Ingest bulk marketplace catalogs, submit lists of product URLs, or execute scheduled crawler sweeps."
        breadcrumbs={[{ label: 'Compliance' }, { label: 'Batch Scan' }]}
      />

      {/* Alert Banner */}
      {message && (
        <div
          className={`flex items-center gap-2 p-3 rounded-lg text-xs font-semibold ${
            message.type === 'success'
              ? 'bg-emerald-50 text-emerald-900 border border-emerald-200 dark:bg-emerald-950/40 dark:text-emerald-300'
              : 'bg-rose-50 text-rose-900 border border-rose-200 dark:bg-rose-950/40 dark:text-rose-300'
          }`}
        >
          {message.type === 'success' ? (
            <CheckCircle2 className="w-4 h-4 shrink-0 text-emerald-600" />
          ) : (
            <AlertCircle className="w-4 h-4 shrink-0 text-rose-600" />
          )}
          <span>{message.text}</span>
        </div>
      )}

      {/* Main Grid: Form + Live Job Queue */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-5">
        {/* Left Column: Batch Form */}
        <div className="lg:col-span-7 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 p-4 space-y-4">
          {/* Segmented Controls */}
          <div className="grid grid-cols-3 gap-2 border-b border-slate-100 dark:border-slate-800 pb-3">
            <button
              onClick={() => setActiveTab('FILE')}
              className={`p-2 rounded-md text-xs font-semibold flex items-center justify-center gap-1.5 transition ${
                activeTab === 'FILE'
                  ? 'bg-blue-900 text-white dark:bg-blue-800'
                  : 'bg-slate-50 text-slate-600 hover:bg-slate-100 dark:bg-slate-800 dark:text-slate-400'
              }`}
            >
              <FileUp className="w-3.5 h-3.5" />
              <span>Catalog File</span>
            </button>

            <button
              onClick={() => setActiveTab('URLS')}
              className={`p-2 rounded-md text-xs font-semibold flex items-center justify-center gap-1.5 transition ${
                activeTab === 'URLS'
                  ? 'bg-blue-900 text-white dark:bg-blue-800'
                  : 'bg-slate-50 text-slate-600 hover:bg-slate-100 dark:bg-slate-800 dark:text-slate-400'
              }`}
            >
              <ListPlus className="w-3.5 h-3.5" />
              <span>Multiple URLs</span>
            </button>

            <button
              onClick={() => setActiveTab('SANDBOX')}
              className={`p-2 rounded-md text-xs font-semibold flex items-center justify-center gap-1.5 transition ${
                activeTab === 'SANDBOX'
                  ? 'bg-blue-900 text-white dark:bg-blue-800'
                  : 'bg-slate-50 text-slate-600 hover:bg-slate-100 dark:bg-slate-800 dark:text-slate-400'
              }`}
            >
              <Bot className="w-3.5 h-3.5" />
              <span>Sandbox Crawler</span>
            </button>
          </div>

          {/* TAB 1: CSV / JSON Upload */}
          {activeTab === 'FILE' && (
            <form onSubmit={handleFileUpload} className="space-y-4">
              <div className="flex items-center justify-between text-xs">
                <div className="flex gap-4">
                  <label className="flex items-center gap-1.5 font-medium text-slate-700 dark:text-slate-300 cursor-pointer">
                    <input
                      type="radio"
                      checked={uploadType === 'CSV'}
                      onChange={() => setUploadType('CSV')}
                      className="text-blue-800 focus:ring-blue-700"
                    />
                    <span>CSV Format</span>
                  </label>
                  <label className="flex items-center gap-1.5 font-medium text-slate-700 dark:text-slate-300 cursor-pointer">
                    <input
                      type="radio"
                      checked={uploadType === 'JSON'}
                      onChange={() => setUploadType('JSON')}
                      className="text-blue-800 focus:ring-blue-700"
                    />
                    <span>JSON Format</span>
                  </label>
                </div>

                <button
                  type="button"
                  onClick={handleDownloadCsvTemplate}
                  className="text-blue-900 hover:underline flex items-center gap-1 font-semibold"
                >
                  <Download className="w-3 h-3" />
                  <span>Download CSV Template</span>
                </button>
              </div>

              <div className="border border-dashed border-slate-300 dark:border-slate-700 rounded-md p-6 text-center hover:border-blue-700 transition">
                <input
                  type="file"
                  accept=".csv,.json"
                  onChange={(e) => setUploadFile(e.target.files?.[0] || null)}
                  className="hidden"
                  id="batch-file-input"
                />
                <label htmlFor="batch-file-input" className="cursor-pointer space-y-2 block">
                  <UploadCloud className="w-8 h-8 text-blue-800 mx-auto" />
                  <div className="text-xs font-semibold text-slate-700 dark:text-slate-300">
                    {uploadFile ? uploadFile.name : `Select ${uploadType} catalog file to upload`}
                  </div>
                  <div className="text-[11px] text-slate-400">
                    Headers: name, declared_mrp, listing_price, country_of_origin, brand, seller, category (Max 25MB)
                  </div>
                </label>
              </div>

              <button
                type="submit"
                disabled={submitting || !uploadFile}
                className="w-full py-2 bg-blue-900 hover:bg-blue-800 text-white font-semibold text-xs rounded-md shadow-2xs flex items-center justify-center gap-2 transition disabled:opacity-50"
              >
                <UploadCloud className="w-3.5 h-3.5" />
                <span>{submitting ? 'Uploading & Queuing...' : 'Upload & Launch Batch Job'}</span>
              </button>
            </form>
          )}

          {/* TAB 2: Multiple URLs */}
          {activeTab === 'URLS' && (
            <form onSubmit={handleMultipleUrls} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Product Listing URLs (One per line)
                </label>
                <textarea
                  rows={6}
                  required
                  value={multipleUrls}
                  onChange={(e) => setMultipleUrls(e.target.value)}
                  className="w-full p-2.5 rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-xs dark:text-slate-100 font-mono focus:outline-none focus:ring-1 focus:ring-blue-700"
                  placeholder="https://retailbharat.in/p/item-1&#10;https://apexglobalkart.com/p/item-2"
                />
              </div>

              <button
                type="submit"
                disabled={submitting}
                className="w-full py-2 bg-blue-900 hover:bg-blue-800 text-white font-semibold text-xs rounded-md shadow-2xs flex items-center justify-center gap-2 transition disabled:opacity-50"
              >
                <Play className="w-3.5 h-3.5" />
                <span>{submitting ? 'Submitting Batch URLs...' : 'Submit URL Batch & Start Scans'}</span>
              </button>
            </form>
          )}

          {/* TAB 3: Sandbox Crawler Simulation */}
          {activeTab === 'SANDBOX' && (
            <form onSubmit={handleSandboxSubmit} className="space-y-4">
              <div className="p-3 bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700 rounded-md text-xs text-slate-700 dark:text-slate-300">
                <strong>Simulated Crawl:</strong> Simulates an automated web crawler across e-commerce product listings with real label images for statutory compliance testing.
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                    Marketplace Platform
                  </label>
                  <select
                    value={sandboxPlatform}
                    onChange={(e) => setSandboxPlatform(e.target.value)}
                    className="w-full p-2 rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-xs dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                  >
                    <option value="RetailBharat">RetailBharat (Mock Platform)</option>
                    <option value="QuickMart">QuickMart India</option>
                    <option value="KiranaDirect">KiranaDirect Wholesale</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                    Batch Size (Commodities)
                  </label>
                  <input
                    type="number"
                    min={1}
                    max={25}
                    value={sandboxBatch}
                    onChange={(e) => setSandboxBatch(parseInt(e.target.value) || 5)}
                    className="w-full p-2 rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-xs dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                  />
                </div>
              </div>

              <button
                type="submit"
                disabled={submitting}
                className="w-full py-2 bg-blue-900 hover:bg-blue-800 text-white font-semibold text-xs rounded-md shadow-2xs flex items-center justify-center gap-2 transition disabled:opacity-50"
              >
                <Play className="w-3.5 h-3.5" />
                <span>{submitting ? 'Initiating Crawler...' : 'Execute Sandbox Crawl Sweep'}</span>
              </button>
            </form>
          )}
        </div>

        {/* Right Column: Trackable Batch Jobs Queue */}
        <div className="lg:col-span-5 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 p-4 flex flex-col justify-between space-y-3">
          <div>
            <div className="flex items-center justify-between mb-1">
              <h3 className="text-xs font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
                <Clock className="w-3.5 h-3.5 text-blue-800" />
                <span>Trackable Batch Scan Jobs</span>
              </h3>
              <button
                onClick={loadJobs}
                className="p-1 text-slate-400 hover:text-slate-600"
                title="Refresh jobs"
              >
                <RefreshCw className="w-3.5 h-3.5" />
              </button>
            </div>
            <p className="text-[11px] text-slate-500 mb-3">
              Per-item progression through Data Acquisition, OCR, Extraction, and Rule Engine.
            </p>

            <div className="space-y-2.5 overflow-y-auto max-h-[420px] pr-1">
              {scanJobs.map((job) => (
                <div
                  key={job.id}
                  className="p-3 rounded-md border border-slate-200 dark:border-slate-800 bg-slate-50/60 dark:bg-slate-800/40 space-y-2"
                >
                  <div className="flex items-center justify-between text-xs">
                    <span className="font-semibold text-slate-800 dark:text-slate-200">
                      Scan Job #{job.id}
                    </span>
                    <span
                      className={`text-[10px] font-mono font-bold uppercase px-1.5 py-0.5 rounded border ${
                        job.status === 'COMPLETED'
                          ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
                          : job.status === 'FAILED'
                          ? 'bg-rose-50 text-rose-800 border-rose-200'
                          : 'bg-blue-50 text-blue-800 border-blue-200'
                      }`}
                    >
                      {job.status}
                    </span>
                  </div>

                  <div className="w-full bg-slate-200 dark:bg-slate-700 h-1.5 rounded-full overflow-hidden">
                    <div
                      className={`h-full transition-all duration-300 ${
                        job.status === 'COMPLETED'
                          ? 'bg-emerald-600'
                          : job.status === 'FAILED'
                          ? 'bg-rose-600'
                          : 'bg-blue-800'
                      }`}
                      style={{ width: `${job.progressPct}%` }}
                    />
                  </div>

                  <div className="flex items-center justify-between text-[10px] text-slate-500 font-mono">
                    <span>Type: {job.jobType || 'BATCH'}</span>
                    <span>Items: {job.processedItems}/{job.totalItems}</span>
                  </div>

                  {job.errorMessage && (
                    <div className="text-[10px] text-rose-600 dark:text-rose-400 font-mono truncate">
                      Error: {job.errorMessage}
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>

          <div className="pt-3 border-t border-slate-100 dark:border-slate-800 text-center">
            <Link
              to="/scan-history"
              className="text-xs font-semibold text-blue-900 hover:underline dark:text-blue-400 inline-flex items-center gap-1"
            >
              <span>View Full Scan History & Logs</span>
              <ExternalLink className="w-3 h-3" />
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};
