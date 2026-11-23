import React, { useState, useEffect } from 'react';
import { scanApi } from '../api/scanApi';
import { ScanJob, ScanJobStatus } from '../types';
import { PageHeader } from '../components/layout/PageHeader';
import {
  History,
  CheckCircle2,
  AlertCircle,
  Clock,
  RefreshCw,
  Search,
  ExternalLink,
  Filter,
} from 'lucide-react';
import { Link } from 'react-router-dom';

export const ScanHistoryPage: React.FC = () => {
  const [jobs, setJobs] = useState<ScanJob[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [searchTerm, setSearchTerm] = useState('');

  const loadJobs = async () => {
    try {
      setLoading(true);
      const data = await scanApi.getScanJobs();
      setJobs(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadJobs();
    const interval = setInterval(loadJobs, 5000);
    return () => clearInterval(interval);
  }, []);

  const filteredJobs = jobs.filter((job) => {
    const matchesStatus =
      statusFilter === 'ALL'
        ? true
        : statusFilter === 'IN_PROGRESS'
        ? job.status === 'PROCESSING' || job.status === 'OCR_PROCESSING' || job.status === 'VALIDATING' || job.status === 'QUEUED'
        : job.status === statusFilter;

    const matchesSearch =
      searchTerm.trim() === ''
        ? true
        : String(job.id).includes(searchTerm.trim()) ||
          (job.jobType && job.jobType.toLowerCase().includes(searchTerm.toLowerCase())) ||
          (job.productId && String(job.productId).includes(searchTerm.trim())) ||
          (job.batchId && job.batchId.toLowerCase().includes(searchTerm.toLowerCase()));

    return matchesStatus && matchesSearch;
  });

  const getStatusBadge = (status: ScanJobStatus) => {
    switch (status) {
      case 'COMPLETED':
        return 'bg-emerald-50 text-emerald-800 border-emerald-200 dark:bg-emerald-950/40 dark:text-emerald-300 dark:border-emerald-800';
      case 'FAILED':
        return 'bg-rose-50 text-rose-800 border-rose-200 dark:bg-rose-950/40 dark:text-rose-300 dark:border-rose-800';
      case 'PROCESSING':
      case 'OCR_PROCESSING':
      case 'VALIDATING':
        return 'bg-blue-50 text-blue-800 border-blue-200 dark:bg-blue-950/40 dark:text-blue-300 dark:border-blue-800 animate-pulse';
      case 'QUEUED':
        return 'bg-amber-50 text-amber-800 border-amber-200 dark:bg-amber-950/40 dark:text-amber-300 dark:border-amber-800';
      default:
        return 'bg-slate-100 text-slate-800 border-slate-200 dark:bg-slate-800 dark:text-slate-300';
    }
  };

  return (
    <div className="space-y-5 pb-16">
      <PageHeader
        title="Statutory Scan History & Audit Trail"
        description="Chronological record of automated compliance scans, batch catalog processing, OCR extractions, and rule determinations."
        breadcrumbs={[{ label: 'Compliance' }, { label: 'Scan History' }]}
        action={
          <button
            onClick={loadJobs}
            disabled={loading}
            className="px-3 py-1.5 border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 rounded-md text-slate-700 dark:text-slate-300 hover:bg-slate-50 text-xs font-semibold flex items-center gap-1.5 transition"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin text-blue-800' : ''}`} />
            <span>Refresh</span>
          </button>
        }
      />

      {/* Filter and Search Bar */}
      <div className="p-3 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 flex flex-wrap items-center justify-between gap-3 text-xs">
        <div className="flex items-center gap-3 flex-wrap">
          <div className="flex items-center gap-1.5 font-semibold text-slate-500">
            <Filter className="w-3.5 h-3.5" />
            <span>Filter Status:</span>
          </div>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-2.5 py-1.5 rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 font-medium text-slate-700 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-blue-700"
          >
            <option value="ALL">All Statuses ({jobs.length})</option>
            <option value="IN_PROGRESS">In Progress / Processing</option>
            <option value="COMPLETED">Completed</option>
            <option value="FAILED">Failed</option>
            <option value="QUEUED">Queued</option>
          </select>
        </div>

        <div className="relative w-full sm:w-64">
          <Search className="w-3.5 h-3.5 absolute left-2.5 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            placeholder="Search by Job ID, Type..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-8 pr-3 py-1.5 rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-1 focus:ring-blue-700"
          />
        </div>
      </div>

      {/* Main Jobs Table */}
      <div className="bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 overflow-hidden">
        {loading && jobs.length === 0 ? (
          <div className="p-12 text-center text-xs text-slate-500 flex items-center justify-center gap-2">
            <RefreshCw className="w-4 h-4 animate-spin text-blue-800" />
            <span>Loading statutory scan logs...</span>
          </div>
        ) : filteredJobs.length === 0 ? (
          <div className="p-12 text-center text-xs text-slate-500">
            No scan history matches your filter criteria.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-600 uppercase tracking-wider font-semibold border-b border-slate-200 dark:border-slate-800">
                <tr>
                  <th className="py-2.5 px-3">Job ID</th>
                  <th className="py-2.5 px-3">Job Type</th>
                  <th className="py-2.5 px-3">Status</th>
                  <th className="py-2.5 px-3">Progress</th>
                  <th className="py-2.5 px-3">Items (Proc/Total)</th>
                  <th className="py-2.5 px-3">Target Product</th>
                  <th className="py-2.5 px-3">Created At</th>
                  <th className="py-2.5 px-3 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {filteredJobs.map((job) => (
                  <tr key={job.id} className="hover:bg-slate-50/70 dark:hover:bg-slate-800/40 transition">
                    <td className="py-2.5 px-3 font-mono font-semibold text-slate-900 dark:text-slate-100">
                      #{job.id}
                    </td>

                    <td className="py-2.5 px-3">
                      <span className="font-mono text-[11px] font-semibold text-blue-900 dark:text-blue-400">
                        {job.jobType || 'SINGLE'}
                      </span>
                    </td>

                    <td className="py-2.5 px-3">
                      {(() => {
                        const effectiveStatus = (job.progressPct >= 100 && job.status !== 'FAILED') ? 'COMPLETED' : job.status;
                        return (
                          <span
                            className={`inline-flex items-center gap-1 px-1.5 py-0.5 rounded text-[10px] font-mono font-bold uppercase border ${getStatusBadge(
                              effectiveStatus
                            )}`}
                          >
                            {effectiveStatus === 'COMPLETED' ? (
                              <CheckCircle2 className="w-3 h-3 text-emerald-600" />
                            ) : effectiveStatus === 'FAILED' ? (
                              <AlertCircle className="w-3 h-3 text-rose-600" />
                            ) : (
                              <Clock className="w-3 h-3 text-blue-700" />
                            )}
                            <span>{effectiveStatus}</span>
                          </span>
                        );
                      })()}
                    </td>

                    <td className="py-2.5 px-3 w-40">
                      <div className="space-y-1">
                        <div className="flex justify-between text-[10px] text-slate-500 font-mono">
                          <span>{job.progressPct || 0}%</span>
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
                            style={{ width: `${job.progressPct || 0}%` }}
                          />
                        </div>
                      </div>
                    </td>

                    <td className="py-2.5 px-3 font-mono text-slate-600 dark:text-slate-400">
                      {job.processedItems || 0} / {job.totalItems || 1}
                    </td>

                    <td className="py-2.5 px-3">
                      {job.productId ? (
                        <Link
                          to={`/products/${job.productId}`}
                          className="font-mono text-blue-900 dark:text-blue-400 hover:underline font-semibold"
                        >
                          PRD-{job.productId}
                        </Link>
                      ) : (
                        <span className="text-slate-400 font-mono text-[11px]">Batch Stream</span>
                      )}
                    </td>

                    <td className="py-2.5 px-3 font-mono text-slate-500 whitespace-nowrap">
                      {job.createdAt ? job.createdAt.replace('T', ' ').substring(0, 19) : '-'}
                    </td>

                    <td className="py-2.5 px-3 text-right">
                      {job.productId ? (
                        <Link
                          to={`/products/${job.productId}`}
                          className="px-2 py-1 rounded bg-slate-100 hover:bg-slate-200 text-slate-700 font-medium text-[11px] inline-flex items-center gap-1 transition"
                        >
                          <span>Inspect</span>
                          <ExternalLink className="w-3 h-3" />
                        </Link>
                      ) : (
                        <span className="text-[11px] text-slate-400 font-mono">-</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
