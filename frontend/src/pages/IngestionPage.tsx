import React, { useState, useEffect } from 'react';
import { scanApi } from '../api/scanApi';
import { ScanJob } from '../types';
import { PageHeader } from '../components/layout/PageHeader';
import {
  UploadCloud,
  FileText,
  Globe,
  Bot,
  Play,
  CheckCircle2,
  RefreshCw,
  Clock,
  AlertCircle,
  FileUp,
} from 'lucide-react';

export const IngestionPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'MANUAL' | 'UPLOAD' | 'URL' | 'SANDBOX'>('SANDBOX');
  const [scanJobs, setScanJobs] = useState<ScanJob[]>([]);
  const [loadingJobs, setLoadingJobs] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  // Manual Form
  const [productName, setProductName] = useState('');
  const [declaredMrp, setDeclaredMrp] = useState('');
  const [listingPrice, setListingPrice] = useState('');
  const [countryOfOrigin, setCountryOfOrigin] = useState('India');
  const [description, setDescription] = useState('');
  const [manualImage, setManualImage] = useState<File | null>(null);

  // Upload Form
  const [uploadFile, setUploadFile] = useState<File | null>(null);
  const [uploadType, setUploadType] = useState<'CSV' | 'JSON'>('CSV');

  // URL Form
  const [urlInput, setUrlInput] = useState('https://retailbharat.in/p/organic-atta-5kg');

  // Sandbox Form
  const [sandboxBatch, setSandboxBatch] = useState(5);
  const [sandboxPlatform, setSandboxPlatform] = useState('RetailBharat');

  const loadJobs = async () => {
    try {
      setLoadingJobs(true);
      const jobs = await scanApi.getScanJobs();
      setScanJobs(jobs);
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingJobs(false);
    }
  };

  useEffect(() => {
    loadJobs();
    const interval = setInterval(loadJobs, 4000);
    return () => clearInterval(interval);
  }, []);

  const handleManualSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setMessage(null);
    try {
      const job = await scanApi.createManualScan({
        productName,
        declaredMrp: declaredMrp ? parseFloat(declaredMrp) : undefined,
        listingPrice: listingPrice ? parseFloat(listingPrice) : undefined,
        countryOfOrigin,
        description,
        imageFile: manualImage || undefined,
      });
      setMessage({ type: 'success', text: `Scan Job #${job.id} initiated successfully!` });
      setProductName('');
      setDeclaredMrp('');
      setListingPrice('');
      setDescription('');
      setManualImage(null);
      loadJobs();
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Manual ingestion failed.' });
    } finally {
      setSubmitting(false);
    }
  };

  const handleUploadSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!uploadFile) return;
    setSubmitting(true);
    setMessage(null);
    try {
      const job = await scanApi.uploadFileScan(uploadFile, uploadType);
      setMessage({ type: 'success', text: `Batch Scan Job #${job.id} queued successfully!` });
      setUploadFile(null);
      loadJobs();
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Batch upload failed.' });
    } finally {
      setSubmitting(false);
    }
  };

  const handleUrlSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setMessage(null);
    try {
      const job = await scanApi.triggerUrlScan(urlInput);
      setMessage({ type: 'success', text: `URL Ingestion Scan Job #${job.id} initiated!` });
      loadJobs();
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'URL ingestion failed.' });
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
        text: `Sandbox simulated crawl initiated (Job #${job.id}, ${sandboxBatch} products)!`,
      });
      loadJobs();
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Sandbox crawl failed.' });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-5 pb-16">
      <PageHeader
        title="Commodity Ingestion & Pipeline Triggers"
        description="Trigger e-commerce web crawlers, upload batch catalogs, submit listing URLs, or register physical package specimens."
        breadcrumbs={[{ label: 'Compliance' }, { label: 'Ingestion Pipeline' }]}
      />

      {message && (
        <div
          className={`flex items-center gap-2 p-3 rounded-lg text-xs font-semibold ${
            message.type === 'success'
              ? 'bg-emerald-50 text-emerald-900 border border-emerald-200'
              : 'bg-rose-50 text-rose-900 border border-rose-200'
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

      {/* Ingestion Channels & Pipeline Monitor */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-5">
        {/* Left Column: Input Forms */}
        <div className="lg:col-span-7 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 p-4 space-y-4">
          {/* Channel Tabs */}
          <div className="grid grid-cols-4 gap-1.5 border-b border-slate-100 dark:border-slate-800 pb-3">
            <button
              onClick={() => setActiveTab('SANDBOX')}
              className={`p-2 rounded-md text-xs font-semibold flex items-center justify-center gap-1.5 transition ${
                activeTab === 'SANDBOX'
                  ? 'bg-blue-900 text-white dark:bg-blue-800'
                  : 'bg-slate-50 text-slate-600 hover:bg-slate-100 dark:bg-slate-800 dark:text-slate-400'
              }`}
            >
              <Bot className="w-3.5 h-3.5" />
              <span>Sandbox</span>
            </button>

            <button
              onClick={() => setActiveTab('URL')}
              className={`p-2 rounded-md text-xs font-semibold flex items-center justify-center gap-1.5 transition ${
                activeTab === 'URL'
                  ? 'bg-blue-900 text-white dark:bg-blue-800'
                  : 'bg-slate-50 text-slate-600 hover:bg-slate-100 dark:bg-slate-800 dark:text-slate-400'
              }`}
            >
              <Globe className="w-3.5 h-3.5" />
              <span>Product URL</span>
            </button>

            <button
              onClick={() => setActiveTab('UPLOAD')}
              className={`p-2 rounded-md text-xs font-semibold flex items-center justify-center gap-1.5 transition ${
                activeTab === 'UPLOAD'
                  ? 'bg-blue-900 text-white dark:bg-blue-800'
                  : 'bg-slate-50 text-slate-600 hover:bg-slate-100 dark:bg-slate-800 dark:text-slate-400'
              }`}
            >
              <FileUp className="w-3.5 h-3.5" />
              <span>Bulk Upload</span>
            </button>

            <button
              onClick={() => setActiveTab('MANUAL')}
              className={`p-2 rounded-md text-xs font-semibold flex items-center justify-center gap-1.5 transition ${
                activeTab === 'MANUAL'
                  ? 'bg-blue-900 text-white dark:bg-blue-800'
                  : 'bg-slate-50 text-slate-600 hover:bg-slate-100 dark:bg-slate-800 dark:text-slate-400'
              }`}
            >
              <FileText className="w-3.5 h-3.5" />
              <span>Manual Entry</span>
            </button>
          </div>

          {/* TAB 1: Sandbox Crawler */}
          {activeTab === 'SANDBOX' && (
            <form onSubmit={handleSandboxSubmit} className="space-y-4">
              <div className="p-3 rounded-md bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700 text-xs text-slate-700 dark:text-slate-300">
                <strong>Simulated Crawler:</strong> Triggers the pipeline across sample e-commerce marketplace commodities to verify OCR and Rule Engine execution.
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                    Marketplace Platform
                  </label>
                  <select
                    value={sandboxPlatform}
                    onChange={(e) => setSandboxPlatform(e.target.value)}
                    className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                  >
                    <option value="RetailBharat">RetailBharat (Mock Platform)</option>
                    <option value="QuickMart">QuickMart India</option>
                    <option value="KiranaDirect">KiranaDirect Wholesale</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                    Commodity Batch Size
                  </label>
                  <input
                    type="number"
                    min={1}
                    max={20}
                    value={sandboxBatch}
                    onChange={(e) => setSandboxBatch(parseInt(e.target.value) || 5)}
                    className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                  />
                </div>
              </div>

              <button
                type="submit"
                disabled={submitting}
                className="w-full py-2 bg-blue-900 hover:bg-blue-800 text-white font-semibold text-xs rounded-md shadow-2xs flex items-center justify-center gap-2 transition disabled:opacity-50"
              >
                <Play className="w-3.5 h-3.5" />
                <span>{submitting ? 'Initiating Crawler...' : 'Execute Sandbox Sweep'}</span>
              </button>
            </form>
          )}

          {/* TAB 2: URL Ingestion */}
          {activeTab === 'URL' && (
            <form onSubmit={handleUrlSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Product Listing URL *
                </label>
                <input
                  type="url"
                  required
                  value={urlInput}
                  onChange={(e) => setUrlInput(e.target.value)}
                  className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 font-mono focus:outline-none focus:ring-1 focus:ring-blue-700"
                  placeholder="https://example.in/p/item-123"
                />
              </div>

              <button
                type="submit"
                disabled={submitting}
                className="w-full py-2 bg-blue-900 hover:bg-blue-800 text-white font-semibold text-xs rounded-md shadow-2xs flex items-center justify-center gap-2 transition disabled:opacity-50"
              >
                <Play className="w-3.5 h-3.5" />
                <span>{submitting ? 'Acquiring Listing...' : 'Acquire & Scan Listing'}</span>
              </button>
            </form>
          )}

          {/* TAB 3: File Upload */}
          {activeTab === 'UPLOAD' && (
            <form onSubmit={handleUploadSubmit} className="space-y-4">
              <div className="flex gap-4">
                <label className="flex items-center gap-1.5 text-xs font-medium text-slate-700 dark:text-slate-300 cursor-pointer">
                  <input
                    type="radio"
                    checked={uploadType === 'CSV'}
                    onChange={() => setUploadType('CSV')}
                    className="text-blue-800 focus:ring-blue-700"
                  />
                  <span>CSV File</span>
                </label>
                <label className="flex items-center gap-1.5 text-xs font-medium text-slate-700 dark:text-slate-300 cursor-pointer">
                  <input
                    type="radio"
                    checked={uploadType === 'JSON'}
                    onChange={() => setUploadType('JSON')}
                    className="text-blue-800 focus:ring-blue-700"
                  />
                  <span>JSON File</span>
                </label>
              </div>

              <div className="border border-dashed border-slate-300 dark:border-slate-700 rounded-md p-6 text-center hover:border-blue-700 transition">
                <input
                  type="file"
                  accept=".csv,.json"
                  onChange={(e) => setUploadFile(e.target.files?.[0] || null)}
                  className="hidden"
                  id="ingestion-file-upload"
                />
                <label htmlFor="ingestion-file-upload" className="cursor-pointer space-y-2 block">
                  <UploadCloud className="w-8 h-8 text-blue-800 mx-auto" />
                  <div className="text-xs font-semibold text-slate-700 dark:text-slate-300">
                    {uploadFile ? uploadFile.name : `Select ${uploadType} catalog file`}
                  </div>
                  <div className="text-[11px] text-slate-400">
                    Supports .csv or .json formatted catalog files (Max 25MB)
                  </div>
                </label>
              </div>

              <button
                type="submit"
                disabled={submitting || !uploadFile}
                className="w-full py-2 bg-blue-900 hover:bg-blue-800 text-white font-semibold text-xs rounded-md shadow-2xs flex items-center justify-center gap-2 transition disabled:opacity-50"
              >
                <UploadCloud className="w-3.5 h-3.5" />
                <span>{submitting ? 'Uploading Catalog...' : 'Upload & Start Batch Scan'}</span>
              </button>
            </form>
          )}

          {/* TAB 4: Manual Entry */}
          {activeTab === 'MANUAL' && (
            <form onSubmit={handleManualSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Commodity Name *
                </label>
                <input
                  type="text"
                  required
                  value={productName}
                  onChange={(e) => setProductName(e.target.value)}
                  className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                  placeholder="e.g. Swarna Atta 5kg"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                    Declared MRP (₹)
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    value={declaredMrp}
                    onChange={(e) => setDeclaredMrp(e.target.value)}
                    className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 font-mono focus:outline-none focus:ring-1 focus:ring-blue-700"
                    placeholder="320.00"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                    Marketplace Listing Price (₹)
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    value={listingPrice}
                    onChange={(e) => setListingPrice(e.target.value)}
                    className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 font-mono focus:outline-none focus:ring-1 focus:ring-blue-700"
                    placeholder="299.00"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Country of Origin
                </label>
                <input
                  type="text"
                  value={countryOfOrigin}
                  onChange={(e) => setCountryOfOrigin(e.target.value)}
                  className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Physical Package Label Image
                </label>
                <input
                  type="file"
                  accept="image/*"
                  onChange={(e) => setManualImage(e.target.files?.[0] || null)}
                  className="w-full p-1.5 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800"
                />
              </div>

              <button
                type="submit"
                disabled={submitting}
                className="w-full py-2 bg-blue-900 hover:bg-blue-800 text-white font-semibold text-xs rounded-md shadow-2xs flex items-center justify-center gap-2 transition disabled:opacity-50"
              >
                <Play className="w-3.5 h-3.5" />
                <span>{submitting ? 'Registering...' : 'Register Specimen & Audit'}</span>
              </button>
            </form>
          )}
        </div>

        {/* Right Column: Trackable Pipeline Jobs */}
        <div className="lg:col-span-5 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 p-4 space-y-3">
          <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-2">
            <h3 className="text-xs font-bold text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
              <Clock className="w-3.5 h-3.5 text-blue-800" />
              <span>Active Pipeline Jobs</span>
            </h3>
            <button
              onClick={loadJobs}
              className="p-1 text-slate-400 hover:text-slate-600"
              title="Refresh"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${loadingJobs ? 'animate-spin' : ''}`} />
            </button>
          </div>

          <div className="space-y-2.5 overflow-y-auto max-h-[460px] pr-1">
            {scanJobs.slice(0, 8).map((job) => (
              <div
                key={job.id}
                className="p-3 rounded-md border border-slate-200 dark:border-slate-800 bg-slate-50/60 dark:bg-slate-800/40 space-y-1.5"
              >
                <div className="flex items-center justify-between text-xs">
                  <span className="font-semibold text-slate-800 dark:text-slate-200">
                    Job #{job.id} ({job.jobType || 'SCAN'})
                  </span>
                  <span className="text-[10px] font-mono font-bold uppercase px-1.5 py-0.5 rounded border bg-blue-50 text-blue-800 border-blue-200">
                    {job.status}
                  </span>
                </div>

                <div className="w-full bg-slate-200 dark:bg-slate-700 h-1.5 rounded-full overflow-hidden">
                  <div
                    className="h-full bg-blue-800 transition-all duration-300"
                    style={{ width: `${job.progressPct || 0}%` }}
                  />
                </div>

                <div className="flex justify-between text-[10px] text-slate-500 font-mono">
                  <span>Processed: {job.processedItems || 0}/{job.totalItems || 1}</span>
                  <span>{job.progressPct || 0}%</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
