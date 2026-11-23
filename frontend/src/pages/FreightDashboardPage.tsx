import React, { useEffect, useState } from 'react';
import {
  Ship,
  TrendingDown,
  TrendingUp,
  Anchor,
  FileSpreadsheet,
  FileText,
  Clock,
  AlertCircle,
  CheckCircle2,
  Waves,
  DollarSign,
  ArrowRight,
  ShieldCheck,
  RefreshCw,
} from 'lucide-react';
import { freightApi } from '../api/freightApi';
import { FreightOverview, ProcurementPlan, MaritimePort } from '../types';

export const FreightDashboardPage: React.FC = () => {
  const [overview, setOverview] = useState<FreightOverview | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await freightApi.getOverview();
      setOverview(data);
    } catch (err: any) {
      setError(err?.message || 'Failed to load freight intelligence data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] space-y-3">
        <RefreshCw className="w-8 h-8 text-blue-600 animate-spin" />
        <p className="text-sm text-slate-500 font-medium">Fetching live Baltic Dry Index & East Coast port telemetry...</p>
      </div>
    );
  }

  if (error || !overview) {
    return (
      <div className="p-6">
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg text-red-700 flex items-center gap-3">
          <AlertCircle className="w-5 h-5 shrink-0" />
          <span>{error || 'Unable to connect to Maritime Freight intelligence core.'}</span>
        </div>
      </div>
    );
  }

  const { marketData, totalRoutes, totalPorts, activeProcurementPlans, totalOptimizedSavingsInr, eastCoastPorts, recentPlans } = overview;

  return (
    <div className="p-6 space-y-6 max-w-[1600px] mx-auto">
      {/* Title & Master Actions */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-4 border-b border-slate-200 dark:border-slate-800">
        <div>
          <div className="flex items-center gap-2">
            <span className="px-2.5 py-0.5 text-[11px] font-bold rounded-full bg-teal-100 text-teal-800 dark:bg-teal-900/60 dark:text-teal-300">
              SIH26006 • MINISTRY OF STEEL
            </span>
            <span className="text-xs text-slate-500">Overseas Bulk Cargo Procurement & Vessel Chartering</span>
          </div>
          <h1 className="text-2xl font-bold text-slate-900 dark:text-slate-100 mt-1">
            Intelligent Maritime Freight Decision Support System
          </h1>
          <p className="text-sm text-slate-500 dark:text-slate-400">
            Real-time Baltic Dry rate forecasting, draft constraint validation, and landed cost optimization for East Coast Indian ports
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={() => window.open(freightApi.downloadCsvReport(), '_blank')}
            className="flex items-center gap-2 px-3 py-2 text-xs font-semibold text-slate-700 dark:text-slate-200 bg-white dark:bg-slate-800 border border-slate-300 dark:border-slate-700 rounded-lg hover:bg-slate-50 dark:hover:bg-slate-750 transition-colors shadow-sm"
          >
            <FileText className="w-4 h-4 text-blue-600" />
            Export CSV
          </button>
          <button
            onClick={() => window.open(freightApi.downloadExcelWorkbook(), '_blank')}
            className="flex items-center gap-2 px-3 py-2 text-xs font-semibold text-slate-700 dark:text-slate-200 bg-white dark:bg-slate-800 border border-slate-300 dark:border-slate-700 rounded-lg hover:bg-slate-50 dark:hover:bg-slate-750 transition-colors shadow-sm"
          >
            <FileSpreadsheet className="w-4 h-4 text-emerald-600" />
            Export Master (XLSX)
          </button>
          <a
            href="/freight/procurement"
            className="flex items-center gap-2 px-3.5 py-2 text-xs font-semibold text-white bg-blue-700 hover:bg-blue-800 rounded-lg transition-colors shadow-sm"
          >
            <Anchor className="w-4 h-4" />
            New Bulk Procurement Plan
          </a>
        </div>
      </div>

      {/* Market Telemetry Tickers */}
      <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-3">
        <div className="p-3.5 bg-slate-900 text-white rounded-lg shadow-sm">
          <div className="text-[11px] font-medium text-slate-400 uppercase tracking-wider">Baltic Dry Index (BDI)</div>
          <div className="text-2xl font-bold mt-1 flex items-baseline gap-2">
            {marketData?.bdi || 1890}
            <span className="text-xs font-semibold text-emerald-400 flex items-center">
              <TrendingUp className="w-3.5 h-3.5" /> +24 pts
            </span>
          </div>
          <div className="text-[10px] text-slate-400 mt-1">Global Dry-Bulk Benchmark</div>
        </div>

        <div className="p-3.5 bg-white dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-lg shadow-sm">
          <div className="text-[11px] font-medium text-slate-500 uppercase tracking-wider">Capesize (BCI)</div>
          <div className="text-xl font-bold text-slate-900 dark:text-slate-100 mt-1 flex items-baseline gap-2">
            {marketData?.bci || 2920}
            <span className="text-xs font-semibold text-emerald-600 flex items-center">
              <TrendingUp className="w-3.5 h-3.5" /> +1.8%
            </span>
          </div>
          <div className="text-[10px] text-slate-400 mt-1">120k-200k DWT (Iron Ore)</div>
        </div>

        <div className="p-3.5 bg-white dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-lg shadow-sm">
          <div className="text-[11px] font-medium text-slate-500 uppercase tracking-wider">Panamax (BPI)</div>
          <div className="text-xl font-bold text-slate-900 dark:text-slate-100 mt-1 flex items-baseline gap-2">
            {marketData?.bpi || 1640}
            <span className="text-xs font-semibold text-rose-600 flex items-center">
              <TrendingDown className="w-3.5 h-3.5" /> -3.4%
            </span>
          </div>
          <div className="text-[10px] text-slate-400 mt-1">70k-85k DWT (Coking Coal)</div>
        </div>

        <div className="p-3.5 bg-white dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-lg shadow-sm">
          <div className="text-[11px] font-medium text-slate-500 uppercase tracking-wider">Supramax (BSI)</div>
          <div className="text-xl font-bold text-slate-900 dark:text-slate-100 mt-1 flex items-baseline gap-2">
            {marketData?.bsi || 1310}
            <span className="text-xs font-semibold text-emerald-600 flex items-center">
              <TrendingUp className="w-3.5 h-3.5" /> +3.7%
            </span>
          </div>
          <div className="text-[10px] text-slate-400 mt-1">50k-65k DWT (Thermal Coal)</div>
        </div>

        <div className="p-3.5 bg-white dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-lg shadow-sm">
          <div className="text-[11px] font-medium text-slate-500 uppercase tracking-wider">Bunker Fuel (VLSFO)</div>
          <div className="text-xl font-bold text-slate-900 dark:text-slate-100 mt-1">
            ${marketData?.bunkerVlsfoUsd || 592.50}
          </div>
          <div className="text-[10px] text-slate-400 mt-1">Singapore Hub ($/MT)</div>
        </div>

        <div className="p-3.5 bg-white dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-lg shadow-sm">
          <div className="text-[11px] font-medium text-slate-500 uppercase tracking-wider">USD / INR Peg</div>
          <div className="text-xl font-bold text-slate-900 dark:text-slate-100 mt-1">
            ₹{marketData?.usdInrRate || 83.65}
          </div>
          <div className="text-[10px] text-slate-400 mt-1">Customs Clearance Peg</div>
        </div>
      </div>

      {/* High-Level Impact Banner */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="p-4 bg-gradient-to-br from-blue-900 to-indigo-950 text-white rounded-xl shadow-md">
          <div className="flex items-center justify-between text-blue-200 text-xs font-medium">
            <span>TOTAL OPTIMIZED PROCUREMENT SAVINGS</span>
            <DollarSign className="w-4 h-4 text-emerald-400" />
          </div>
          <div className="text-3xl font-extrabold mt-2 text-white">
            ₹{(totalOptimizedSavingsInr / 10000000).toFixed(2)} Crore
          </div>
          <p className="text-xs text-blue-200/80 mt-1">
            Captured across {activeProcurementPlans} active raw material orders via forward rate timing & vessel matching
          </p>
        </div>

        <div className="p-4 bg-white dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-xl shadow-sm">
          <div className="flex items-center justify-between text-slate-500 text-xs font-medium">
            <span>ACTIVE STRATEGY SPLIT</span>
            <ShieldCheck className="w-4 h-4 text-blue-600" />
          </div>
          <div className="flex items-center gap-3 mt-3">
            <div className="flex-1 text-center p-2 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 rounded-lg">
              <div className="text-xs font-bold text-emerald-700 dark:text-emerald-300">CHARTER NOW</div>
              <div className="text-lg font-bold text-emerald-800 dark:text-emerald-200">1 Order</div>
            </div>
            <div className="flex-1 text-center p-2 bg-amber-50 dark:bg-amber-950/40 border border-amber-200 dark:border-amber-800 rounded-lg">
              <div className="text-xs font-bold text-amber-700 dark:text-amber-300">HOLD / WAIT</div>
              <div className="text-lg font-bold text-amber-800 dark:text-amber-200">1 Order</div>
            </div>
            <div className="flex-1 text-center p-2 bg-blue-50 dark:bg-blue-950/40 border border-blue-200 dark:border-blue-800 rounded-lg">
              <div className="text-xs font-bold text-blue-700 dark:text-blue-300">SPLIT COA</div>
              <div className="text-lg font-bold text-blue-800 dark:text-blue-200">1 Order</div>
            </div>
          </div>
        </div>

        <div className="p-4 bg-white dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-xl shadow-sm">
          <div className="flex items-center justify-between text-slate-500 text-xs font-medium">
            <span>STRATEGIC TRADE CORRIDORS</span>
            <Waves className="w-4 h-4 text-teal-600" />
          </div>
          <div className="text-2xl font-bold text-slate-900 dark:text-slate-100 mt-2">
            {totalRoutes} Monitored Routes
          </div>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
            Overseas origins: Australia, Indonesia, South Africa, Mozambique, USA into {totalPorts} East Coast terminals
          </p>
        </div>
      </div>

      {/* East Coast Port Turnaround & Draft Constraints Grid */}
      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <h2 className="text-base font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
            <Anchor className="w-4 h-4 text-blue-700" />
            East Coast of India Port Constraints & Berth Congestion Index
          </h2>
          <span className="text-xs text-slate-500">Live operational telemetry for draft and vessel dispatch clearance</span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-3">
          {eastCoastPorts?.map((port) => (
            <div
              key={port.portCode}
              className="p-3.5 bg-white dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-xl shadow-sm space-y-2.5"
            >
              <div className="flex items-start justify-between">
                <div>
                  <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300">
                    {port.portCode}
                  </span>
                  <div className="text-xs font-bold text-slate-900 dark:text-slate-100 mt-1 truncate max-w-[170px]" title={port.portName}>
                    {port.portName}
                  </div>
                </div>
                <span
                  className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                    port.congestionIndex < 0.35
                      ? 'bg-emerald-100 text-emerald-800'
                      : port.congestionIndex < 0.55
                      ? 'bg-amber-100 text-amber-800'
                      : 'bg-rose-100 text-rose-800'
                  }`}
                >
                  {port.congestionIndex < 0.35 ? 'NORMAL' : port.congestionIndex < 0.55 ? 'MODERATE' : 'CONGESTED'}
                </span>
              </div>

              <div className="space-y-1.5 text-xs">
                <div className="flex justify-between text-slate-600 dark:text-slate-400">
                  <span>Max Draft:</span>
                  <span className="font-bold text-slate-900 dark:text-slate-200">{port.maxDraftMeters} m</span>
                </div>
                <div className="flex justify-between text-slate-600 dark:text-slate-400">
                  <span>Max DWT:</span>
                  <span className="font-semibold text-slate-800 dark:text-slate-200">{port.maxDwt.toLocaleString()} MT</span>
                </div>
                <div className="flex justify-between text-slate-600 dark:text-slate-400">
                  <span>Discharge Rate:</span>
                  <span className="font-semibold text-slate-800 dark:text-slate-200">{port.dischargeRateTpd.toLocaleString()} TPD</span>
                </div>
                <div className="flex justify-between text-slate-600 dark:text-slate-400">
                  <span>Berth Wait:</span>
                  <span className="font-semibold text-amber-600 dark:text-amber-400 flex items-center gap-1">
                    <Clock className="w-3 h-3" /> {port.averageBerthWaitDays} Days
                  </span>
                </div>
              </div>

              {/* Draft Capacity Gauge */}
              <div className="pt-2 border-t border-slate-100 dark:border-slate-800">
                <div className="flex justify-between text-[10px] text-slate-400 mb-1">
                  <span>Channel Depth</span>
                  <span>{((port.maxDraftMeters / 18.5) * 100).toFixed(0)}% Capesize ready</span>
                </div>
                <div className="w-full h-1.5 bg-slate-100 dark:bg-slate-700 rounded-full overflow-hidden">
                  <div
                    className={`h-full rounded-full ${
                      port.maxDraftMeters >= 16.0 ? 'bg-blue-600' : port.maxDraftMeters >= 13.0 ? 'bg-teal-500' : 'bg-amber-500'
                    }`}
                    style={{ width: `${(port.maxDraftMeters / 18.5) * 100}%` }}
                  />
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Active Raw Material Procurement Plans Table */}
      <div className="bg-white dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-xl shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
          <div>
            <h2 className="text-base font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
              <Ship className="w-4 h-4 text-blue-700" />
              Optimized Bulk Cargo Procurement & Chartering Plans
            </h2>
            <p className="text-xs text-slate-500 mt-0.5">
              Steel & Power sector shipments with predictive landed cost breakdown and chartering determinations
            </p>
          </div>
          <span className="text-xs font-semibold px-2.5 py-1 bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 rounded-md">
            {recentPlans?.length || 0} Orders
          </span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-600 dark:text-slate-400 border-b border-slate-200 dark:border-slate-800 uppercase font-semibold">
              <tr>
                <th className="py-3 px-4">Plan Ref</th>
                <th className="py-3 px-4">Commodity & Volume</th>
                <th className="py-3 px-4">Route & Terminal</th>
                <th className="py-3 px-4">Vessel Class</th>
                <th className="py-3 px-4">Decision</th>
                <th className="py-3 px-4">Freight Rate</th>
                <th className="py-3 px-4">Total Landed Cost</th>
                <th className="py-3 px-4">Cost Savings</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800 text-slate-700 dark:text-slate-300">
              {recentPlans?.map((plan) => (
                <tr key={plan.id} className="hover:bg-slate-50/60 dark:hover:bg-slate-800/40 transition-colors">
                  <td className="py-3 px-4 font-mono font-bold text-blue-700 dark:text-blue-400">
                    {plan.planReference}
                  </td>
                  <td className="py-3 px-4">
                    <div className="font-semibold text-slate-900 dark:text-slate-100">{plan.commodity}</div>
                    <div className="text-[11px] text-slate-500">{plan.quantityTons.toLocaleString()} Metric Tons</div>
                  </td>
                  <td className="py-3 px-4">
                    <div className="font-medium text-slate-800 dark:text-slate-200">
                      {plan.originCountry} → {plan.destinationPort?.portName || 'East Coast'}
                    </div>
                    <div className="text-[11px] text-slate-400">Laycan: {plan.targetLaycanStart} to {plan.targetLaycanEnd}</div>
                  </td>
                  <td className="py-3 px-4">
                    <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-slate-100 dark:bg-slate-800 text-slate-800 dark:text-slate-200">
                      {plan.recommendedVesselClass}
                    </span>
                  </td>
                  <td className="py-3 px-4">
                    <span
                      className={`px-2.5 py-1 rounded-full text-[10px] font-bold inline-flex items-center gap-1 ${
                        plan.charterDecision === 'CHARTER_NOW'
                          ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-950/60 dark:text-emerald-300'
                          : plan.charterDecision === 'HOLD_WAIT'
                          ? 'bg-amber-100 text-amber-800 dark:bg-amber-950/60 dark:text-amber-300'
                          : 'bg-blue-100 text-blue-800 dark:bg-blue-950/60 dark:text-blue-300'
                      }`}
                    >
                      {plan.charterDecision === 'CHARTER_NOW' && <CheckCircle2 className="w-3 h-3" />}
                      {plan.charterDecision === 'HOLD_WAIT' && <Clock className="w-3 h-3" />}
                      {plan.charterDecision === 'SPLIT_COA' && <Waves className="w-3 h-3" />}
                      {plan.charterDecision.replace('_', ' ')}
                    </span>
                  </td>
                  <td className="py-3 px-4 font-mono font-semibold">
                    ${plan.predictedFreightUsdTon.toFixed(2)} / MT
                  </td>
                  <td className="py-3 px-4 font-mono">
                    <div className="font-bold text-slate-900 dark:text-slate-100">
                      ₹{plan.totalLandedCostInrTon.toLocaleString('en-IN', { maximumFractionDigits: 0 })} / MT
                    </div>
                    <div className="text-[11px] text-slate-500">${plan.totalLandedCostUsdTon.toFixed(2)} USD</div>
                  </td>
                  <td className="py-3 px-4 font-mono font-bold text-emerald-700 dark:text-emerald-400">
                    +₹{(plan.estimatedCostSavingsInr / 100000).toFixed(1)} Lakh
                  </td>
                  <td className="py-3 px-4 text-right">
                    <button
                      onClick={() => window.open(freightApi.downloadPlanPdf(plan.id), '_blank')}
                      className="inline-flex items-center gap-1.5 px-2.5 py-1 text-[11px] font-semibold text-blue-700 dark:text-blue-400 bg-blue-50 dark:bg-blue-950/50 hover:bg-blue-100 dark:hover:bg-blue-900/60 rounded border border-blue-200 dark:border-blue-800 transition-colors"
                      title="Download PDF Procurement Dossier"
                    >
                      <FileText className="w-3.5 h-3.5" />
                      Dossier
                    </button>
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

