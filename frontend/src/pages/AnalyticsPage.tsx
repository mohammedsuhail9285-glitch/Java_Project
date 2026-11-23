import React, { useState, useEffect } from 'react';
import { dashboardApi } from '../api/dashboardApi';
import { violationApi } from '../api/violationApi';
import { DashboardSummary, Violation } from '../types';
import { StatCard } from '../components/common/StatCard';
import { PageHeader } from '../components/layout/PageHeader';
import {
  BarChart3,
  TrendingUp,
  AlertTriangle,
  Package,
  CheckCircle,
  AlertOctagon,
  ShieldAlert,
  RefreshCw,
  Building,
  Scale,
  PieChart as PieChartIcon,
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
  PieChart,
  Pie,
  Legend,
} from 'recharts';
import { useNavigate } from 'react-router-dom';

export const AnalyticsPage: React.FC = () => {
  const [data, setData] = useState<DashboardSummary | null>(null);
  const [violations, setViolations] = useState<Violation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');
  const [timeWindow, setTimeWindow] = useState<'7D' | '30D'>('7D');
  const navigate = useNavigate();

  const loadAnalytics = async () => {
    try {
      setLoading(true);
      setError(null);
      const [summaryRes, vioRes] = await Promise.all([
        dashboardApi.getSummary().catch((e) => {
          console.error(e);
          return null;
        }),
        violationApi.getViolations({ size: 100 }).catch((e) => {
          console.error(e);
          return { content: [], totalElements: 0, totalPages: 1 };
        }),
      ]);

      if (summaryRes) {
        setData(summaryRes);
      } else {
        setError('Unable to load analytics summary from backend service.');
      }
      setViolations(vioRes.content || []);
    } catch (err: any) {
      setError(err?.message || 'Error compiling regulatory analytics');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAnalytics();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[50vh]">
        <div className="flex flex-col items-center gap-3">
          <RefreshCw className="w-8 h-8 text-blue-600 animate-spin" />
          <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">
            Compiling Regulatory Analytics & Risk Matrices...
          </span>
        </div>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[50vh] p-6 text-center space-y-4">
        <div className="w-12 h-12 rounded-full bg-rose-50 dark:bg-rose-950/40 text-rose-600 flex items-center justify-center">
          <ShieldAlert className="w-6 h-6" />
        </div>
        <h3 className="text-base font-bold text-slate-900 dark:text-slate-100">
          Analytics Pipeline Unavailable
        </h3>
        <p className="text-xs text-slate-500 max-w-sm">
          {error || 'Unable to retrieve regulatory metrics.'}
        </p>
        <button
          onClick={loadAnalytics}
          className="px-4 py-2 bg-blue-900 text-white rounded-lg text-xs font-semibold hover:bg-blue-800 transition flex items-center gap-1.5"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          Retry
        </button>
      </div>
    );
  }

  // Derive Violations by Rule from real violation data
  const ruleCounts: Record<string, { count: number; name: string; severity: string }> = {};
  violations.forEach((v) => {
    const id = v.ruleId || 'UNKNOWN';
    if (!ruleCounts[id]) {
      ruleCounts[id] = {
        count: 0,
        name: v.ruleName || id,
        severity: v.severity,
      };
    }
    ruleCounts[id].count += 1;
  });

  const ruleChartData = Object.entries(ruleCounts)
    .map(([ruleId, info]) => ({
      ruleId,
      name: `${ruleId}: ${info.name}`,
      count: info.count,
      severity: info.severity,
    }))
    .sort((a, b) => b.count - a.count);

  // Category chart data
  const filteredCategories = selectedCategory === 'ALL'
    ? data.categoryRisks || []
    : (data.categoryRisks || []).filter((c) => c.category === selectedCategory);

  const categoryChartData = filteredCategories.map((c) => ({
    name: c.category,
    nonCompliant: c.nonCompliantCount,
    total: c.totalProducts,
    rate: c.complianceRate,
  }));

  // Severity Distribution Data for Pie Chart
  const severityData = [
    { name: 'Critical', value: data.criticalViolations || 0, color: '#ef4444' },
    { name: 'Major', value: data.majorViolations || 0, color: '#f97316' },
    { name: 'Minor', value: data.minorViolations || 0, color: '#3b82f6' },
  ].filter((s) => s.value > 0);

  const CATEGORY_COLORS = ['#ef4444', '#f97316', '#eab308', '#3b82f6', '#8b5cf6', '#10b981'];

  return (
    <div className="space-y-6 pb-16">
      {/* Header */}
      <PageHeader
        title="Regulatory Compliance Intelligence & Risk Analytics"
        description="Comprehensive statutory trend analyses, category risk distributions, seller non-compliance patterns, and rule failure density."
        icon={BarChart3}
        breadcrumbs={[
          { label: 'Overview', to: '/' },
          { label: 'Intelligence' },
          { label: 'Analytics' },
        ]}
        actions={
          <div className="flex items-center gap-2">
            <div className="flex items-center bg-slate-100 dark:bg-slate-800 p-0.5 rounded-lg border border-slate-200 dark:border-slate-700">
              <button
                onClick={() => setTimeWindow('7D')}
                className={`px-2.5 py-1 text-xs font-semibold rounded-md transition ${
                  timeWindow === '7D'
                    ? 'bg-blue-900 text-white shadow-xs dark:bg-blue-600'
                    : 'text-slate-600 dark:text-slate-300 hover:text-slate-900'
                }`}
              >
                7-Day Rolling
              </button>
              <button
                onClick={() => setTimeWindow('30D')}
                className={`px-2.5 py-1 text-xs font-semibold rounded-md transition ${
                  timeWindow === '30D'
                    ? 'bg-blue-900 text-white shadow-xs dark:bg-blue-600'
                    : 'text-slate-600 dark:text-slate-300 hover:text-slate-900'
                }`}
              >
                30-Day View
              </button>
            </div>

            <button
              onClick={loadAnalytics}
              className="p-2 border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 rounded-lg hover:bg-slate-50 dark:hover:bg-slate-700 text-slate-600 dark:text-slate-300"
              title="Refresh Analytics"
            >
              <RefreshCw className="w-4 h-4" />
            </button>
          </div>
        }
      />

      {/* Row 1: KPI Stat Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Products Monitored"
          value={data.totalProducts}
          subtitle="Real catalog commodities"
          icon={<Package className="w-5 h-5" />}
          colorScheme="blue"
        />

        <StatCard
          title="Overall Compliance Rate"
          value={`${data.overallComplianceRate}%`}
          subtitle={`${data.compliantProducts} compliant listings`}
          icon={<CheckCircle className="w-5 h-5" />}
          colorScheme="emerald"
        />

        <StatCard
          title="Total Detected Violations"
          value={data.totalViolations}
          subtitle={`${data.criticalViolations} critical deviations`}
          icon={<AlertOctagon className="w-5 h-5" />}
          colorScheme="rose"
        />

        <StatCard
          title="High-Risk Sellers"
          value={(data.sellerRisks || []).filter((s) => s.riskLevel === 'CRITICAL' || s.riskLevel === 'HIGH').length}
          subtitle={`Across ${(data.sellerRisks || []).length} active vendors`}
          icon={<Building className="w-5 h-5" />}
          colorScheme="amber"
        />
      </div>

      {/* Row 2: Compliance Trajectory + Severity Breakdown */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Trajectory Area Chart */}
        <div className="lg:col-span-8 p-5 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-sm font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
                <TrendingUp className="w-4 h-4 text-blue-600" />
                Statutory Compliance Trajectory Over Time
              </h3>
              <p className="text-xs text-slate-500">
                Rolling compliance percentage across evaluated e-commerce declarations.
              </p>
            </div>
            <span className="text-xs font-mono font-bold text-emerald-600 bg-emerald-50 dark:bg-emerald-950/40 px-2 py-0.5 rounded border border-emerald-200 dark:border-emerald-800">
              Latest: {data.overallComplianceRate}%
            </span>
          </div>

          <div className="h-72 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={data.complianceTrends} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <defs>
                  <linearGradient id="analyticsCompGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#2563eb" stopOpacity={0.35} />
                    <stop offset="95%" stopColor="#2563eb" stopOpacity={0.0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                <XAxis dataKey="date" tick={{ fontSize: 11 }} tickLine={false} axisLine={false} />
                <YAxis domain={[50, 100]} tick={{ fontSize: 11 }} tickLine={false} axisLine={false} />
                <Tooltip
                  formatter={(val: any) => [`${val}%`, 'Compliance Rate']}
                  contentStyle={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderRadius: '8px', color: '#fff', fontSize: '12px' }}
                />
                <Area type="monotone" dataKey="complianceRate" stroke="#2563eb" strokeWidth={2.5} fillOpacity={1} fill="url(#analyticsCompGradient)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Severity Distribution Donut Chart */}
        <div className="lg:col-span-4 p-5 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm flex flex-col justify-between">
          <div>
            <h3 className="text-sm font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2 mb-1">
              <PieChartIcon className="w-4 h-4 text-rose-600" />
              Violation Severity Distribution
            </h3>
            <p className="text-xs text-slate-500 mb-3">
              Proportion of flagged statutory deviations by severity level.
            </p>

            <div className="h-56 w-full flex items-center justify-center">
              {severityData.length === 0 ? (
                <div className="text-xs text-slate-400">No violations recorded</div>
              ) : (
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={severityData}
                      dataKey="value"
                      nameKey="name"
                      cx="50%"
                      cy="50%"
                      innerRadius={50}
                      outerRadius={75}
                      paddingAngle={4}
                    >
                      {severityData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip
                      formatter={(val: any) => [val, 'Violations']}
                      contentStyle={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderRadius: '8px', color: '#fff', fontSize: '11px' }}
                    />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              )}
            </div>
          </div>

          <div className="pt-3 border-t border-slate-100 dark:border-slate-800 text-[11px] text-slate-500 flex justify-between">
            <span>Critical Discrepancies: <strong>{data.criticalViolations}</strong></span>
            <span>Total: <strong>{data.totalViolations}</strong></span>
          </div>
        </div>
      </div>

      {/* Row 3: Violations by Rule + Violations by Category */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Violations by Statutory Rule */}
        <div className="lg:col-span-6 p-5 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-sm font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
                <Scale className="w-4 h-4 text-blue-600" />
                Violations by Legal Metrology Rule
              </h3>
              <p className="text-xs text-slate-500">
                Frequency of non-compliance flags across configured statutory rules.
              </p>
            </div>
            <button
              onClick={() => navigate('/rules')}
              className="text-xs font-semibold text-blue-600 hover:underline"
            >
              View Rules →
            </button>
          </div>

          <div className="h-64 w-full">
            {ruleChartData.length === 0 ? (
              <div className="h-full flex items-center justify-center text-xs text-slate-400">
                No rule violation records found.
              </div>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={ruleChartData} margin={{ top: 10, right: 10, left: -20, bottom: 20 }}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                  <XAxis dataKey="ruleId" tick={{ fontSize: 10 }} />
                  <YAxis tick={{ fontSize: 10 }} allowDecimals={false} />
                  <Tooltip
                    formatter={(val: any, _name: any, item: any) => [`${val} violations`, item.payload.name]}
                    contentStyle={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderRadius: '8px', color: '#fff', fontSize: '11px' }}
                  />
                  <Bar dataKey="count" fill="#3b82f6" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            )}
          </div>
        </div>

        {/* Violations by Category */}
        <div className="lg:col-span-6 p-5 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-sm font-bold text-slate-900 dark:text-slate-100">
                Non-Compliance Density by Product Category
              </h3>
              <p className="text-xs text-slate-500">
                Identifies highest-risk commodity groups in active catalog.
              </p>
            </div>
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              className="text-xs border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 rounded px-2 py-1 text-slate-700 dark:text-slate-300"
            >
              <option value="ALL">All Categories</option>
              {(data.categoryRisks || []).map((c) => (
                <option key={c.category} value={c.category}>
                  {c.category}
                </option>
              ))}
            </select>
          </div>

          <div className="h-64 w-full">
            {categoryChartData.length === 0 ? (
              <div className="h-full flex items-center justify-center text-xs text-slate-400">
                No category data available.
              </div>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={categoryChartData} layout="vertical" margin={{ top: 5, right: 20, left: 20, bottom: 5 }}>
                  <XAxis type="number" tick={{ fontSize: 10 }} />
                  <YAxis
                    dataKey="name"
                    type="category"
                    width={150}
                    interval={0}
                    tickFormatter={(name: string) => (name && name.length > 20 ? name.substring(0, 18) + '...' : name)}
                    tick={{ fontSize: 10 }}
                    tickLine={false}
                    axisLine={false}
                  />
                  <Tooltip
                    formatter={(val: any) => [`${val} Potential Violations`, 'Flagged Items']}
                    contentStyle={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderRadius: '8px', color: '#fff', fontSize: '11px' }}
                  />
                  <Bar dataKey="nonCompliant" radius={[0, 4, 4, 0]}>
                    {categoryChartData.map((_, index) => (
                      <Cell key={`cat-cell-${index}`} fill={CATEGORY_COLORS[index % CATEGORY_COLORS.length]} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            )}
          </div>
        </div>
      </div>

      {/* Row 4: Seller & Vendor Compliance Risk Table */}
      <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm p-5 space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-sm font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
              <Building className="w-4 h-4 text-blue-600" />
              Vendor & Marketplace Compliance Ranking
            </h3>
            <p className="text-xs text-slate-500">
              Compliance performance broken down across sellers and regional e-commerce entities.
            </p>
          </div>
          <button
            onClick={() => navigate('/violations')}
            className="text-xs font-semibold text-blue-600 hover:underline"
          >
            Review Violations Inbox →
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-500 uppercase tracking-wider font-semibold border-b border-slate-100 dark:border-slate-800">
              <tr>
                <th className="py-2.5 px-3">Vendor / Entity Name</th>
                <th className="py-2.5 px-3">Platform / Hub</th>
                <th className="py-2.5 px-3">Catalog Size</th>
                <th className="py-2.5 px-3">Flagged Non-Compliance</th>
                <th className="py-2.5 px-3">Compliance Rate</th>
                <th className="py-2.5 px-3">Risk Assessment</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
              {(data.sellerRisks || []).map((seller, idx) => (
                <tr key={idx} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/40 transition">
                  <td className="py-2.5 px-3 font-semibold text-slate-800 dark:text-slate-200">
                    {seller.sellerName}
                  </td>
                  <td className="py-2.5 px-3 text-slate-500">
                    {seller.platform}
                  </td>
                  <td className="py-2.5 px-3 font-mono font-medium">
                    {seller.totalProducts} SKUs
                  </td>
                  <td className="py-2.5 px-3 font-bold text-rose-600 dark:text-rose-400 font-mono">
                    {seller.violationCount}
                  </td>
                  <td className="py-2.5 px-3">
                    <div className="flex items-center gap-2">
                      <div className="w-20 h-2 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                        <div
                          className={`h-full ${seller.complianceRate > 75 ? 'bg-emerald-500' : seller.complianceRate > 50 ? 'bg-amber-500' : 'bg-rose-500'}`}
                          style={{ width: `${seller.complianceRate}%` }}
                        />
                      </div>
                      <span className="font-mono text-xs font-semibold">{seller.complianceRate}%</span>
                    </div>
                  </td>
                  <td className="py-2.5 px-3">
                    <span className={`inline-flex px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider ${
                      seller.riskLevel === 'CRITICAL'
                        ? 'bg-red-100 text-red-800 dark:bg-red-950/60 dark:text-red-300'
                        : seller.riskLevel === 'HIGH'
                        ? 'bg-orange-100 text-orange-800 dark:bg-orange-950/60 dark:text-orange-300'
                        : 'bg-blue-100 text-blue-800 dark:bg-blue-950/60 dark:text-blue-300'
                    }`}>
                      {seller.riskLevel}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default AnalyticsPage;
