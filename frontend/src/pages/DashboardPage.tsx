import React, { useState, useEffect } from 'react';
import { dashboardApi } from '../api/dashboardApi';
import { scanApi } from '../api/scanApi';
import { violationApi } from '../api/violationApi';
import { reportApi } from '../api/reportApi';
import { DashboardSummary, ScanJob, Violation } from '../types';
import { StatCard } from '../components/common/StatCard';
import { StatusBadge } from '../components/common/StatusBadge';
import { SeverityBadge } from '../components/common/SeverityBadge';
import { PageHeader } from '../components/layout/PageHeader';
import {
  Package,
  CheckCircle,
  AlertOctagon,
  HelpCircle,
  ShieldAlert,
  ArrowUpRight,
  TrendingUp,
  FileSpreadsheet,
  UploadCloud,
  Play,
  RefreshCw,
  Clock,
  Scale,
  DollarSign,
  Tag,
  Factory,
  Globe,
  Mail,
  Calendar,
  AlertTriangle,
  LayoutDashboard,
} from 'lucide-react';
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  BarChart,
  Bar,
  Cell,
} from 'recharts';
import { useNavigate } from 'react-router-dom';

export const DashboardPage: React.FC = () => {
  const [data, setData] = useState<DashboardSummary | null>(null);
  const [scanJobs, setScanJobs] = useState<ScanJob[]>([]);
  const [recentViolations, setRecentViolations] = useState<Violation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [exporting, setExporting] = useState(false);
  const navigate = useNavigate();

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      const [summaryRes, jobsRes, viosRes] = await Promise.all([
        dashboardApi.getSummary().catch((err) => {
          console.error('Failed to load summary', err);
          return null;
        }),
        scanApi.getScanJobs().catch((err) => {
          console.warn('Failed to load scan jobs', err);
          return [];
        }),
        violationApi.getViolations({ size: 6, page: 0 }).catch((err) => {
          console.warn('Failed to load recent violations', err);
          return { content: [], totalElements: 0, totalPages: 1 };
        }),
      ]);

      if (summaryRes) {
        setData(summaryRes);
      } else {
        setError('Unable to compile regulatory summary. Please verify backend service.');
      }
      setScanJobs(jobsRes || []);
      setRecentViolations(viosRes.content || []);
    } catch (err: any) {
      console.error('Dashboard pipeline error', err);
      setError(err?.message || 'Error compiling compliance analytics');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleExportExcel = async () => {
    try {
      setExporting(true);
      await reportApi.downloadExcel();
    } catch (err) {
      console.error(err);
    } finally {
      setExporting(false);
    }
  };

  const handleQuickSandboxCrawl = async () => {
    try {
      await scanApi.triggerSandboxCrawler(5, 'RetailBharat');
      loadData();
      navigate('/single-scan');
    } catch (err) {
      console.error(err);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[50vh]">
        <div className="flex flex-col items-center gap-2">
          <RefreshCw className="w-6 h-6 text-blue-800 animate-spin" />
          <span className="text-xs font-medium text-slate-500">
            Compiling regulatory compliance metrics...
          </span>
        </div>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[50vh] p-6 text-center space-y-3">
        <div className="w-10 h-10 rounded bg-rose-50 text-rose-700 flex items-center justify-center border border-rose-200">
          <ShieldAlert className="w-5 h-5" />
        </div>
        <h3 className="text-sm font-bold text-slate-900 dark:text-slate-100">
          Analytics Pipeline Unavailable
        </h3>
        <p className="text-xs text-slate-500 max-w-sm">
          {error || 'The regulatory analytics service returned an empty payload.'}
        </p>
        <button
          onClick={loadData}
          className="px-3.5 py-1.5 bg-blue-900 text-white rounded text-xs font-semibold hover:bg-blue-800 transition flex items-center gap-1.5"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          Retry Pipeline
        </button>
      </div>
    );
  }

  // Handle empty state gracefully
  if (data.totalProducts === 0) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[50vh] p-8 text-center space-y-3 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800">
        <div className="w-12 h-12 rounded bg-slate-100 dark:bg-slate-800 text-slate-600 flex items-center justify-center border border-slate-200 dark:border-slate-700">
          <Package className="w-6 h-6" />
        </div>
        <div className="space-y-1">
          <h3 className="text-sm font-bold text-slate-900 dark:text-slate-100">
            No E-Commerce Products Scanned Yet
          </h3>
          <p className="text-xs text-slate-500 max-w-md">
            The legal metrology regulatory engine is ready. Ingest marketplace listings or run a sandbox crawl to evaluate products against LMPC Rules 2011.
          </p>
        </div>
        <div className="flex items-center gap-2 pt-2">
          <button
            onClick={handleQuickSandboxCrawl}
            className="px-3.5 py-2 bg-blue-900 text-white rounded text-xs font-semibold hover:bg-blue-800 transition flex items-center gap-1.5"
          >
            <Play className="w-3.5 h-3.5" />
            Launch Sandbox Crawler (5 Products)
          </button>
          <button
            onClick={() => navigate('/single-scan')}
            className="px-3.5 py-2 bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-200 rounded text-xs font-semibold hover:bg-slate-200 dark:hover:bg-slate-700 transition flex items-center gap-1.5"
          >
            <UploadCloud className="w-3.5 h-3.5" />
            Start Single Scan
          </button>
        </div>
      </div>
    );
  }

  // Category Risk chart data
  const categoryChartData = (data.categoryRisks || []).map((c) => ({
    name: c.category,
    nonCompliant: c.nonCompliantCount,
    total: c.totalProducts,
    rate: c.complianceRate,
  }));

  const CATEGORY_COLORS = ['#b91c1c', '#c2410c', '#b45309', '#1d4ed8', '#4338ca', '#15803d'];

  // Violation Distribution breakdown
  const dist = data.violationDistribution || {};
  const violationDistItems = [
    { label: 'MRP Declarations', count: dist['MRP Declarations'] ?? 0, rule: 'LM003, LM008' },
    { label: 'Net Quantity', count: dist['Net Quantity'] ?? 0, rule: 'LM002, LM007' },
    { label: 'Manufacturer / Packer', count: dist['Manufacturer / Packer'] ?? 0, rule: 'LM001' },
    { label: 'Country of Origin', count: dist['Country of Origin'] ?? 0, rule: 'LM004' },
    { label: 'Consumer Care', count: dist['Consumer Care'] ?? 0, rule: 'LM005' },
    { label: 'Date of Mfg / Expiry', count: dist['Date of Mfg / Expiry'] ?? 0, rule: 'LM009' },
    { label: 'Standard Unit of Measure', count: dist['Standard Unit of Measure'] ?? 0, rule: 'LM006' },
    { label: 'E-Commerce Marketplace Parity', count: dist['E-Commerce Marketplace Parity'] ?? 0, rule: 'LM010' },
  ];

  return (
    <div className="space-y-5 pb-16">
      {/* Top Page Header */}
      <PageHeader
        title="Compliance Overview"
        badge={
          <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-semibold bg-amber-50 text-amber-800 dark:bg-amber-950/60 dark:text-amber-300 border border-amber-300 dark:border-amber-800 tracking-wide">
            <span className="w-1.5 h-1.5 rounded-full bg-amber-500 animate-pulse"></span>
            Demo Data (~200 SKUs)
          </span>
        }
        description="Automated legal metrology verification metrics across e-commerce marketplaces under the Legal Metrology Act, 2009."
        icon={LayoutDashboard}
        actions={
          <div className="flex items-center gap-2">
            <button
              onClick={() => navigate('/single-scan')}
              className="px-3 py-1.5 rounded bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold shadow-2xs flex items-center gap-1.5 transition"
            >
              <Play className="w-3.5 h-3.5" />
              New Scan
            </button>
            <button
              onClick={() => navigate('/batch-scan')}
              className="px-3 py-1.5 rounded bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-slate-700 dark:text-slate-200 text-xs font-semibold hover:bg-slate-50 transition flex items-center gap-1.5"
            >
              <UploadCloud className="w-3.5 h-3.5" />
              Batch Upload
            </button>
            <button
              onClick={handleExportExcel}
              disabled={exporting}
              className="px-3 py-1.5 rounded bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-slate-700 dark:text-slate-200 text-xs font-semibold hover:bg-slate-50 transition flex items-center gap-1.5 disabled:opacity-50"
            >
              <FileSpreadsheet className="w-3.5 h-3.5 text-emerald-700" />
              {exporting ? 'Exporting...' : 'Export XLSX'}
            </button>
          </div>
        }
      />

      {/* ROW 1: 5 KEY METRICS (Step 6 / Dashboard Redesign Requirement) */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-3.5">
        <StatCard
          title="Products Scanned"
          value={data.totalProducts}
          subtitle="Monitored catalog SKUs"
          icon={<Package className="w-3.5 h-3.5" />}
          colorScheme="slate"
        />

        <StatCard
          title="Compliance Rate"
          value={`${data.overallComplianceRate}%`}
          subtitle={`${data.compliantProducts} compliant listings`}
          icon={<CheckCircle className="w-3.5 h-3.5" />}
          colorScheme="emerald"
        />

        <StatCard
          title="Non-Compliant"
          value={data.potentialNonCompliantProducts}
          subtitle="Flagged deviations"
          icon={<AlertOctagon className="w-3.5 h-3.5" />}
          colorScheme="rose"
        />

        <StatCard
          title="Open Violations"
          value={data.totalViolations}
          subtitle={`${data.criticalViolations} critical deviations`}
          icon={<ShieldAlert className="w-3.5 h-3.5" />}
          colorScheme="rose"
        />

        <StatCard
          title="Requires Review"
          value={data.requiresReviewProducts}
          subtitle={data.pendingScanProducts ? `${data.requiresReviewProducts} review • ${data.pendingScanProducts} pending` : "Pending inspector review"}
          icon={<HelpCircle className="w-3.5 h-3.5" />}
          colorScheme="amber"
        />
      </div>

      {/* ROW 2: CHARTS (Compliance Trend & Category Risk) */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-4">
        {/* Compliance Trend Chart */}
        <div className="lg:col-span-8 p-4 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 space-y-3">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-xs font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300 flex items-center gap-1.5">
                <TrendingUp className="w-3.5 h-3.5 text-blue-700 dark:text-blue-400" />
                Statutory Compliance Trajectory (7-Day Rolling)
              </h3>
              <p className="text-[11px] text-slate-500">
                Adherence percentage to Legal Metrology Packaged Commodities Rules over time.
              </p>
            </div>
            <span className="text-[11px] font-mono font-bold text-emerald-800 dark:text-emerald-300 bg-emerald-50 dark:bg-emerald-950/40 px-2 py-0.5 rounded border border-emerald-200 dark:border-emerald-800">
              Current: {data.overallComplianceRate}%
            </span>
          </div>

          <div className="h-60 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={data.complianceTrends} margin={{ top: 8, right: 8, left: -24, bottom: 0 }}>
                <defs>
                  <linearGradient id="compGradientDash" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#1e40af" stopOpacity={0.15} />
                    <stop offset="95%" stopColor="#1e40af" stopOpacity={0.0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                <XAxis dataKey="date" tick={{ fontSize: 10, fill: '#64748b' }} tickLine={false} axisLine={{ stroke: '#cbd5e1' }} />
                <YAxis domain={[0, 100]} tick={{ fontSize: 10, fill: '#64748b' }} tickLine={false} axisLine={false} />
                <Tooltip
                  formatter={(val: any) => [`${val}%`, 'Compliance Rate']}
                  contentStyle={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderRadius: '4px', color: '#fff', fontSize: '11px' }}
                />
                <Area type="monotone" dataKey="complianceRate" stroke="#1d4ed8" strokeWidth={2} fillOpacity={1} fill="url(#compGradientDash)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Category Risk Bar Chart */}
        <div className="lg:col-span-4 p-4 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between mb-0.5">
              <h3 className="text-xs font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">
                Violations by Category
              </h3>
            </div>
            <p className="text-[11px] text-slate-500 mb-2">
              Potential deviations per commodity category.
            </p>

            <div className="h-64 w-full">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={categoryChartData} layout="vertical" margin={{ top: 0, right: 10, left: 10, bottom: 0 }}>
                  <XAxis type="number" hide />
                  <YAxis
                    dataKey="name"
                    type="category"
                    width={150}
                    interval={0}
                    tickFormatter={(name: string) => (name && name.length > 20 ? name.substring(0, 18) + '...' : name)}
                    tick={{ fontSize: 10, fill: '#64748b' }}
                    tickLine={false}
                    axisLine={false}
                  />
                  <Tooltip
                    formatter={(val: any) => [`${val} Potential Violations`, 'Flagged Items']}
                    contentStyle={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderRadius: '4px', color: '#fff', fontSize: '11px' }}
                  />
                  <Bar dataKey="nonCompliant" radius={[0, 2, 2, 0]}>
                    {categoryChartData.map((_, index) => (
                      <Cell key={`dash-cell-${index}`} fill={CATEGORY_COLORS[index % CATEGORY_COLORS.length]} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="pt-2 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between text-[11px] text-slate-500">
            <span>Categories Monitored: <strong>{categoryChartData.length}</strong></span>
            <button
              onClick={() => navigate('/products')}
              className="text-blue-700 dark:text-blue-400 font-semibold hover:underline flex items-center gap-0.5"
            >
              <span>Catalog</span> <ArrowUpRight className="w-3 h-3" />
            </button>
          </div>
        </div>
      </div>

      {/* ROW 3: VIOLATION DISTRIBUTION BY MANDATORY DECLARATION */}
      <div className="bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 p-4 space-y-3">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-xs font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300 flex items-center gap-1.5">
              <Scale className="w-3.5 h-3.5 text-blue-700 dark:text-blue-400" />
              Statutory Violation Distribution
            </h3>
            <p className="text-[11px] text-slate-500">
              Breakdown of deviations across mandatory declaration requirements under Rule 6 of LMPC Rules, 2011.
            </p>
          </div>
          <button
            onClick={() => navigate('/violations')}
            className="text-xs text-blue-700 dark:text-blue-400 font-semibold hover:underline flex items-center gap-0.5"
          >
            <span>View All</span> <ArrowUpRight className="w-3 h-3" />
          </button>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-8 gap-2.5">
          {violationDistItems.map((item, idx) => (
            <div
              key={idx}
              className="p-2.5 rounded border border-slate-200 dark:border-slate-800 bg-slate-50/60 dark:bg-slate-800/40 flex flex-col justify-between"
            >
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-semibold text-slate-700 dark:text-slate-300 truncate">{item.label}</span>
                <span className="font-mono text-xs font-bold text-slate-900 dark:text-slate-100">{item.count}</span>
              </div>
              <div className="text-[9px] text-slate-400 font-mono mt-1">{item.rule}</div>
            </div>
          ))}
        </div>
      </div>

      {/* ROW 4: RECENT COMPLIANCE SCANS TABLE */}
      <div className="bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 overflow-hidden">
        <div className="p-3.5 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
          <div>
            <h3 className="text-xs font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">
              Recent Compliance Scans
            </h3>
            <p className="text-[11px] text-slate-500">
              Latest automated optical character recognition & rule verification jobs executed.
            </p>
          </div>
          <button
            onClick={() => navigate('/scan-history')}
            className="text-xs font-semibold text-blue-700 dark:text-blue-400 hover:underline flex items-center gap-0.5"
          >
            <span>View All Scans</span>
            <ArrowUpRight className="w-3 h-3" />
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-500 uppercase tracking-wider font-semibold border-b border-slate-200 dark:border-slate-800">
              <tr>
                <th className="py-2.5 px-3">Scan ID</th>
                <th className="py-2.5 px-3">Product / Target</th>
                <th className="py-2.5 px-3">Source</th>
                <th className="py-2.5 px-3">Date</th>
                <th className="py-2.5 px-3">Progress</th>
                <th className="py-2.5 px-3">Status</th>
                <th className="py-2.5 px-3 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
              {scanJobs.length === 0 ? (
                <tr>
                  <td colSpan={7} className="py-6 text-center text-slate-400">
                    No scans have been performed yet.
                  </td>
                </tr>
              ) : (
                scanJobs.slice(0, 5).map((job) => (
                  <tr key={job.id} className="hover:bg-slate-50/75 dark:hover:bg-slate-800/40 transition">
                    <td className="py-2 px-3 font-mono font-semibold text-slate-800 dark:text-slate-200">
                      #{job.id}
                    </td>
                    <td className="py-2 px-3 font-medium text-slate-800 dark:text-slate-200">
                      {job.productId ? `Product #${job.productId}` : (job.batchId || 'Batch Ingestion')}
                    </td>
                    <td className="py-2 px-3 font-mono text-slate-500">
                      {job.jobType || 'SINGLE_SCAN'}
                    </td>
                    <td className="py-2 px-3 text-slate-500">
                      {job.createdAt ? new Date(job.createdAt).toLocaleString([], { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) : 'Just now'}
                    </td>
                    <td className="py-2 px-3">
                      <div className="flex items-center gap-1.5">
                        <div className="w-16 h-1 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                          <div
                            className={`h-full ${job.status === 'COMPLETED' ? 'bg-emerald-600' : job.status === 'FAILED' ? 'bg-rose-600' : 'bg-blue-600'}`}
                            style={{ width: `${job.progressPct}%` }}
                          />
                        </div>
                        <span className="font-mono text-[10px] text-slate-500">{job.progressPct}%</span>
                      </div>
                    </td>
                    <td className="py-2 px-3">
                      <StatusBadge status={job.progressPct >= 100 && job.status !== 'FAILED' ? 'COMPLETED' : job.status} size="sm" />
                    </td>
                    <td className="py-2 px-3 text-right">
                      {job.productId ? (
                        <button
                          onClick={() => navigate(`/products/${job.productId}`)}
                          className="text-blue-700 dark:text-blue-400 hover:underline font-semibold text-[11px]"
                        >
                          View Result →
                        </button>
                      ) : (
                        <span className="text-slate-400 text-[11px]">-</span>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* ROW 5: RECENT VIOLATIONS TABLE */}
      <div className="bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 overflow-hidden">
        <div className="p-3.5 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
          <div>
            <h3 className="text-xs font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">
              Recent Violations
            </h3>
            <p className="text-[11px] text-slate-500">
              Flagged statutory discrepancies awaiting or undergoing official regulatory review.
            </p>
          </div>
          <button
            onClick={() => navigate('/violations')}
            className="text-xs font-semibold text-blue-700 dark:text-blue-400 hover:underline flex items-center gap-0.5"
          >
            <span>View All Violations</span>
            <ArrowUpRight className="w-3 h-3" />
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-500 uppercase tracking-wider font-semibold border-b border-slate-200 dark:border-slate-800">
              <tr>
                <th className="py-2.5 px-3">Ref ID</th>
                <th className="py-2.5 px-3">Product</th>
                <th className="py-2.5 px-3">Rule</th>
                <th className="py-2.5 px-3">Severity</th>
                <th className="py-2.5 px-3">Detected</th>
                <th className="py-2.5 px-3">Status</th>
                <th className="py-2.5 px-3">Date</th>
                <th className="py-2.5 px-3 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
              {recentViolations.length === 0 ? (
                <tr>
                  <td colSpan={8} className="py-6 text-center text-slate-400">
                    No violations found in the compliance registry.
                  </td>
                </tr>
              ) : (
                recentViolations.slice(0, 5).map((v) => (
                  <tr key={v.id} className="hover:bg-slate-50/75 dark:hover:bg-slate-800/40 transition">
                    <td className="py-2 px-3 font-mono text-slate-500">
                      {v.violationId}
                    </td>
                    <td className="py-2 px-3">
                      <button
                        onClick={() => navigate(`/products/${v.productId}`)}
                        className="font-medium text-slate-900 dark:text-slate-100 hover:text-blue-700 dark:hover:text-blue-400 text-left max-w-xs truncate block"
                      >
                        {v.productName || `Product #${v.productId}`}
                      </button>
                    </td>
                    <td className="py-2 px-3 font-mono font-semibold text-slate-800 dark:text-slate-200">
                      {v.ruleId}
                    </td>
                    <td className="py-2 px-3">
                      <SeverityBadge severity={v.severity} />
                    </td>
                    <td className="py-2 px-3 font-mono max-w-xs truncate text-slate-600 dark:text-slate-300">
                      {v.detectedValue}
                    </td>
                    <td className="py-2 px-3">
                      <StatusBadge status={v.status} size="sm" />
                    </td>
                    <td className="py-2 px-3 text-slate-500">
                      {v.timestamp ? new Date(v.timestamp).toLocaleDateString([], { month: 'short', day: 'numeric' }) : 'Recent'}
                    </td>
                    <td className="py-2 px-3 text-right">
                      <button
                        onClick={() => navigate(`/products/${v.productId}`)}
                        className="text-blue-700 dark:text-blue-400 hover:underline font-semibold text-[11px]"
                      >
                        Inspect →
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
