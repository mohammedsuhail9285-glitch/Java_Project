import React, { useState, useEffect } from 'react';
import {
  Layers,
  Calculator,
  Ship,
  Anchor,
  FileText,
  FileSpreadsheet,
  CheckCircle2,
  Clock,
  Waves,
  AlertTriangle,
  ArrowRight,
  TrendingDown,
  TrendingUp,
  DollarSign,
  RefreshCw,
  Download,
  ShieldAlert,
  Compass,
} from 'lucide-react';
import { freightApi } from '../api/freightApi';
import { MaritimePort, CharterOptimizationRequest, CharterOptimizationResponse, ProcurementPlan } from '../types';

export const BulkProcurementPage: React.FC = () => {
  const [ports, setPorts] = useState<MaritimePort[]>([]);
  const [plans, setPlans] = useState<ProcurementPlan[]>([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState<CharterOptimizationResponse | null>(null);

  // Form state
  const [commodity, setCommodity] = useState('Premium Hard Coking Coal (CSR 68%)');
  const [quantityTons, setQuantityTons] = useState<number>(75000);
  const [originCountry, setOriginCountry] = useState('Australia');
  const [destinationPortCode, setDestinationPortCode] = useState('INPRT');
  const [fobPriceUsd, setFobPriceUsd] = useState<number>(240.0);
  const [contractType, setContractType] = useState<string>('SPOT');
  const [laycanStart, setLaycanStart] = useState('2026-12-05');
  const [laycanEnd, setLaycanEnd] = useState('2026-12-15');

  const loadData = async () => {
    try {
      setLoading(true);
      const [pList, planList] = await Promise.all([freightApi.getPorts(), freightApi.getPlans()]);
      setPorts(pList);
      setPlans(planList);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleOptimize = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setSubmitting(true);
      const req: CharterOptimizationRequest = {
        commodity,
        quantityTons,
        originCountry,
        destinationPortCode,
        targetLaycanStart: laycanStart,
        targetLaycanEnd: laycanEnd,
        fobPriceUsd,
        contractType,
      };
      const res = await freightApi.optimizeCharter(req);
      setResult(res);
      const updatedPlans = await freightApi.getPlans();
      setPlans(updatedPlans);
    } catch (err) {
      console.error('Error optimizing charter:', err);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="p-6 space-y-6 max-w-[1600px] mx-auto">
      {/* Title */}
      <div className="pb-4 border-b border-slate-200 dark:border-slate-800">
        <div className="flex items-center gap-2">
          <span className="px-2.5 py-0.5 text-[11px] font-bold rounded-full bg-teal-100 text-teal-800 dark:bg-teal-900/60 dark:text-teal-300">
            STRATEGY SIMULATOR • SIH26006
          </span>
          <span className="text-xs text-slate-500">Ministry of Steel Procurement DSS</span>
        </div>
        <h1 className="text-2xl font-bold text-slate-900 dark:text-slate-100 mt-1">
          Bulk Cargo Procurement & Landed Cost Optimization Simulator
        </h1>
        <p className="text-sm text-slate-500 dark:text-slate-400">
          Simulate global raw material imports, evaluate Spot vs COA multi-voyage contracts, verify port navigational envelopes, and generate statutory procurement dossiers
        </p>
      </div>

      {/* Simulator Form & Real-Time Output */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Form Panel */}
        <div className="lg:col-span-5 p-6 bg-white dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-xl shadow-sm space-y-5">
          <h2 className="text-sm font-bold text-slate-900 dark:text-slate-100 uppercase tracking-wider flex items-center gap-2">
            <Calculator className="w-4 h-4 text-blue-700" />
            Procurement Order Parameters
          </h2>

          <form onSubmit={handleOptimize} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Raw Material Commodity
              </label>
              <select
                value={commodity}
                onChange={(e) => setCommodity(e.target.value)}
                className="w-full text-xs font-medium p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 rounded-lg text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-blue-600 outline-none"
              >
                <option value="Premium Hard Coking Coal (CSR 68%)">Premium Hard Coking Coal (CSR 68%)</option>
                <option value="PCI Pulverized Coal Injection">PCI Pulverized Coal Injection</option>
                <option value="High-CV Indonesian Thermal Coal (5800 kcal)">High-CV Indonesian Thermal Coal (5800 kcal)</option>
                <option value="Pellet Feed Iron Ore (Fe 65%)">Pellet Feed Iron Ore (Fe 65%)</option>
                <option value="Imported High-Grade Limestone">Imported High-Grade Limestone</option>
              </select>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Cargo Parcel Size (MT)
                </label>
                <input
                  type="number"
                  value={quantityTons}
                  onChange={(e) => setQuantityTons(Number(e.target.value))}
                  className="w-full text-xs font-medium p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 rounded-lg text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-blue-600 outline-none"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  FOB Base Price ($/MT)
                </label>
                <input
                  type="number"
                  step="0.01"
                  value={fobPriceUsd}
                  onChange={(e) => setFobPriceUsd(Number(e.target.value))}
                  className="w-full text-xs font-medium p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 rounded-lg text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-blue-600 outline-none"
                  required
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Origin Country / Port
                </label>
                <select
                  value={originCountry}
                  onChange={(e) => setOriginCountry(e.target.value)}
                  className="w-full text-xs font-medium p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 rounded-lg text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-blue-600 outline-none"
                >
                  <option value="Australia">Australia (Hay Point / Gladstone)</option>
                  <option value="Indonesia">Indonesia (Balikpapan)</option>
                  <option value="South Africa">South Africa (Richards Bay)</option>
                  <option value="Mozambique">Mozambique (Maputo)</option>
                  <option value="USA">USA (Hampton Roads)</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Destination East Coast Port
                </label>
                <select
                  value={destinationPortCode}
                  onChange={(e) => setDestinationPortCode(e.target.value)}
                  className="w-full text-xs font-medium p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 rounded-lg text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-blue-600 outline-none"
                >
                  {ports.map((p) => (
                    <option key={p.portCode} value={p.portCode}>
                      {p.portName} ({p.maxDraftMeters}m draft | {p.maxDwt.toLocaleString()} DWT)
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Contract Type Selection (Spot vs Multi-Voyage COA) */}
            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Charter Contract Structure
              </label>
              <select
                value={contractType}
                onChange={(e) => setContractType(e.target.value)}
                className="w-full text-xs font-medium p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 rounded-lg text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-blue-600 outline-none"
              >
                <option value="SPOT">SPOT Charter (Single Voyage Prompt Market)</option>
                <option value="COA_3_VOYAGE">COA 3-Voyage Contract of Affreightment (5.0% Volume Discount)</option>
                <option value="COA_6_VOYAGE">COA 6-Voyage Long-Term Contract of Affreightment (7.5% Volume Discount)</option>
              </select>
              <p className="text-[11px] text-slate-500 mt-1">
                COA contracts secure dedicated laycan slots, hedge Baltic index surges, and reduce berth demurrage risks.
              </p>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Target Laycan Start
                </label>
                <input
                  type="date"
                  value={laycanStart}
                  onChange={(e) => setLaycanStart(e.target.value)}
                  className="w-full text-xs font-medium p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 rounded-lg text-slate-900 dark:text-slate-100 outline-none"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Target Laycan End
                </label>
                <input
                  type="date"
                  value={laycanEnd}
                  onChange={(e) => setLaycanEnd(e.target.value)}
                  className="w-full text-xs font-medium p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 rounded-lg text-slate-900 dark:text-slate-100 outline-none"
                  required
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={submitting}
              className="w-full flex items-center justify-center gap-2 py-3 text-xs font-bold text-white bg-blue-700 hover:bg-blue-800 rounded-lg transition-colors shadow-sm disabled:opacity-50"
            >
              {submitting ? (
                <>
                  <RefreshCw className="w-4 h-4 animate-spin" />
                  Running Neural Freight Optimization...
                </>
              ) : (
                <>
                  <Ship className="w-4 h-4" />
                  Execute Charter & Landed Cost Optimization
                </>
              )}
            </button>
          </form>
        </div>

        {/* Results Panel */}
        <div className="lg:col-span-7 space-y-6">
          {result ? (
            <div className="p-6 bg-white dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-xl shadow-sm space-y-6">
              {/* Early Warning Alert Banner if Active */}
              {result.earlyWarningMessage && (
                <div className="p-3 bg-amber-50 dark:bg-amber-950/40 border border-amber-200 dark:border-amber-800 rounded-lg flex items-center gap-2.5 text-xs text-amber-900 dark:text-amber-200">
                  <ShieldAlert className="w-5 h-5 text-amber-600 flex-shrink-0" />
                  <div>
                    <span className="font-bold">EARLY WARNING ALERT: </span>
                    {result.earlyWarningMessage}
                  </div>
                </div>
              )}

              {/* Decision Header */}
              <div className="flex flex-col md:flex-row md:items-center justify-between pb-4 border-b border-slate-100 dark:border-slate-800 gap-3">
                <div>
                  <span className="text-[11px] font-mono font-bold text-blue-700 dark:text-blue-400">
                    {result.planReference}
                  </span>
                  <div className="text-xl font-bold text-slate-900 dark:text-slate-100 mt-0.5">
                    Recommended Vessel: {result.recommendedVesselClass}
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <span
                    className={`px-3 py-1.5 rounded-full text-xs font-bold flex items-center gap-1.5 ${
                      result.charterDecision === 'CHARTER_NOW'
                        ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-950/60 dark:text-emerald-300'
                        : result.charterDecision === 'HOLD_WAIT'
                        ? 'bg-amber-100 text-amber-800 dark:bg-amber-950/60 dark:text-amber-300'
                        : 'bg-blue-100 text-blue-800 dark:bg-blue-950/60 dark:text-blue-300'
                    }`}
                  >
                    {result.charterDecision === 'CHARTER_NOW' && <CheckCircle2 className="w-4 h-4" />}
                    {result.charterDecision === 'HOLD_WAIT' && <Clock className="w-4 h-4" />}
                    {result.charterDecision === 'SPLIT_COA' && <Waves className="w-4 h-4" />}
                    DECISION: {result.charterDecision.replace('_', ' ')}
                  </span>
                </div>
              </div>

              {/* Economic Savings & Landed Cost Grid */}
              <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                <div className="p-3.5 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 rounded-lg">
                  <div className="text-[11px] font-bold text-emerald-800 dark:text-emerald-300">TOTAL PROJECTED SAVINGS</div>
                  <div className="text-xl font-extrabold text-emerald-900 dark:text-emerald-100 mt-1">
                    ₹{(result.estimatedCostSavingsInr / 100000).toFixed(1)} Lakh
                  </div>
                  <div className="text-[10px] text-emerald-700/80 mt-0.5">Captured via Rate Timing</div>
                </div>

                <div className="p-3.5 bg-slate-50 dark:bg-slate-800 rounded-lg">
                  <div className="text-[11px] font-medium text-slate-500">TOTAL LANDED COST (INR)</div>
                  <div className="text-xl font-bold text-slate-900 dark:text-slate-100 mt-1">
                    ₹{result.totalLandedCostInrTon.toLocaleString('en-IN', { maximumFractionDigits: 0 })} / MT
                  </div>
                  <div className="text-[10px] text-slate-400 mt-0.5">${result.totalLandedCostUsdTon.toFixed(2)} USD / MT</div>
                </div>

                <div className="p-3.5 bg-slate-50 dark:bg-slate-800 rounded-lg">
                  <div className="text-[11px] font-medium text-slate-500">PREDICTED FREIGHT</div>
                  <div className="text-xl font-bold text-slate-900 dark:text-slate-100 mt-1">
                    ${result.predictedFreightUsdTon.toFixed(2)} / MT
                  </div>
                  <div className="text-[10px] text-slate-400 mt-0.5">{result.confidenceInterval95 || `Spot: $${result.spotRateUsd.toFixed(2)}/MT`}</div>
                </div>
              </div>

              {/* Spot vs COA Multi-Voyage Contract Comparison */}
              {result.coaNetSavingsInr != null && result.coaNetSavingsInr > 0 && (
                <div className="p-4 bg-indigo-50/70 dark:bg-indigo-950/40 border border-indigo-200 dark:border-indigo-800 rounded-xl space-y-3">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-bold text-indigo-900 dark:text-indigo-200 uppercase tracking-wider flex items-center gap-1.5">
                      <Waves className="w-4 h-4 text-indigo-600" />
                      Spot vs. Multi-Voyage COA Analysis ({result.contractType})
                    </span>
                    <span className="text-xs font-extrabold text-indigo-700 dark:text-indigo-300">
                      COA Discount: {result.coaDiscountPct?.toFixed(1)}%
                    </span>
                  </div>
                  <div className="grid grid-cols-3 gap-2 text-xs">
                    <div className="p-2.5 bg-white dark:bg-slate-800 rounded border border-indigo-100 dark:border-indigo-900">
                      <span className="text-slate-500 text-[10px]">Spot Landed Cost</span>
                      <div className="font-bold text-slate-900 dark:text-slate-100">
                        ₹{result.spotTotalLandedInrTon?.toLocaleString('en-IN', { maximumFractionDigits: 0 })} / MT
                      </div>
                    </div>
                    <div className="p-2.5 bg-white dark:bg-slate-800 rounded border border-indigo-100 dark:border-indigo-900">
                      <span className="text-slate-500 text-[10px]">COA Landed Cost</span>
                      <div className="font-bold text-indigo-700 dark:text-indigo-300">
                        ₹{result.coaTotalLandedInrTon?.toLocaleString('en-IN', { maximumFractionDigits: 0 })} / MT
                      </div>
                    </div>
                    <div className="p-2.5 bg-white dark:bg-slate-800 rounded border border-indigo-100 dark:border-indigo-900">
                      <span className="text-slate-500 text-[10px]">COA Net Benefit</span>
                      <div className="font-bold text-emerald-600 dark:text-emerald-400">
                        +₹{((result.coaNetSavingsInr || 0) / 100000).toFixed(1)} Lakh
                      </div>
                    </div>
                  </div>
                  <p className="text-[11px] text-indigo-800 dark:text-indigo-300 leading-relaxed">
                    {result.coaSuitabilityRecommendation}
                  </p>
                </div>
              )}

              {/* Haldia Shallow-Draft & Sandheads Lighterage Protocol */}
              {result.lighterageRequired && (
                <div className="p-4 bg-amber-50/70 dark:bg-amber-950/40 border border-amber-200 dark:border-amber-800 rounded-xl space-y-2">
                  <div className="flex items-center gap-2 text-xs font-bold text-amber-900 dark:text-amber-200 uppercase tracking-wider">
                    <AlertTriangle className="w-4 h-4 text-amber-600" />
                    Haldia Shallow-Draft & Sagar/Sandheads Lighterage Protocol Engaged
                  </div>
                  <p className="text-xs text-amber-800 dark:text-amber-300 leading-relaxed">
                    {result.lighterageOperationsNotes}
                  </p>
                  <div className="grid grid-cols-3 gap-2 text-[11px] pt-1">
                    <div className="p-2 bg-white/80 dark:bg-slate-800 rounded">
                      <span className="text-slate-500 text-[10px]">Anchorage Zone:</span>
                      <div className="font-bold text-slate-900 dark:text-slate-100">{result.lighterageAnchorage}</div>
                    </div>
                    <div className="p-2 bg-white/80 dark:bg-slate-800 rounded">
                      <span className="text-slate-500 text-[10px]">Lightered Quantity:</span>
                      <div className="font-bold text-slate-900 dark:text-slate-100">{result.lighteredQuantityTons?.toLocaleString()} MT</div>
                    </div>
                    <div className="p-2 bg-white/80 dark:bg-slate-800 rounded">
                      <span className="text-slate-500 text-[10px]">Transshipment Surcharge:</span>
                      <div className="font-bold text-slate-900 dark:text-slate-100">${result.lighterageCostUsdTon?.toFixed(2)} / MT</div>
                    </div>
                  </div>
                </div>
              )}

              {/* Demurrage & Alternative Port Diversion */}
              {(result.estimatedBerthWaitDays || result.alternativePortRecommendation) && (
                <div className="p-4 bg-slate-50 dark:bg-slate-800/80 rounded-xl space-y-2 text-xs">
                  <div className="flex items-center justify-between">
                    <span className="font-bold text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
                      <Clock className="w-4 h-4 text-slate-600" />
                      Idle-Time Management & Congestion Risk
                    </span>
                    <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                      result.demurrageRiskLevel === 'CRITICAL' ? 'bg-rose-100 text-rose-800' :
                      result.demurrageRiskLevel === 'HIGH' ? 'bg-amber-100 text-amber-800' :
                      result.demurrageRiskLevel === 'MODERATE' ? 'bg-yellow-100 text-yellow-800' :
                      'bg-emerald-100 text-emerald-800'
                    }`}>
                      DEMURRAGE RISK: {result.demurrageRiskLevel}
                    </span>
                  </div>
                  <div className="grid grid-cols-2 gap-2 text-[11px]">
                    <div>Est. Berth Wait: <span className="font-bold text-slate-900 dark:text-slate-100">{result.estimatedBerthWaitDays?.toFixed(1)} Days</span></div>
                    <div>Daily Demurrage Rate: <span className="font-bold text-slate-900 dark:text-slate-100">${result.dailyDemurrageRateUsd?.toLocaleString()}/day</span></div>
                  </div>
                  {result.alternativePortRecommendation && (
                    <div className="pt-2 border-t border-slate-200 dark:border-slate-700 text-[11px] text-blue-700 dark:text-blue-300">
                      <span className="font-bold">Alternative Port Recommendation: </span>
                      {result.alternativePortRecommendation}
                    </div>
                  )}
                </div>
              )}

              {/* Landed Cost Waterfall Breakdown */}
              <div className="space-y-2">
                <div className="text-xs font-bold text-slate-900 dark:text-slate-100 uppercase tracking-wider">
                  Comprehensive Landed Cost Waterfall Components ($/MT)
                </div>
                <div className="grid grid-cols-2 md:grid-cols-5 gap-2 text-xs">
                  <div className="p-2.5 bg-slate-100 dark:bg-slate-800 rounded">
                    <span className="text-slate-400 text-[10px]">FOB Cargo Base</span>
                    <div className="font-bold text-slate-900 dark:text-slate-100">${fobPriceUsd.toFixed(2)}</div>
                  </div>
                  <div className="p-2.5 bg-blue-50 dark:bg-blue-950/40 rounded border border-blue-200 dark:border-blue-800">
                    <span className="text-blue-600 dark:text-blue-400 text-[10px]">Ocean Freight</span>
                    <div className="font-bold text-blue-900 dark:text-blue-100">${result.predictedFreightUsdTon.toFixed(2)}</div>
                  </div>
                  <div className="p-2.5 bg-slate-100 dark:bg-slate-800 rounded">
                    <span className="text-slate-400 text-[10px]">Bunker Surcharge (BAF)</span>
                    <div className="font-bold text-slate-900 dark:text-slate-100">${result.bunkerAdjustmentUsdTon.toFixed(2)}</div>
                  </div>
                  <div className="p-2.5 bg-slate-100 dark:bg-slate-800 rounded">
                    <span className="text-slate-400 text-[10px]">Port Tariffs</span>
                    <div className="font-bold text-slate-900 dark:text-slate-100">${result.portChargesUsdTon.toFixed(2)}</div>
                  </div>
                  <div className="p-2.5 bg-slate-100 dark:bg-slate-800 rounded">
                    <span className="text-slate-400 text-[10px]">Demurrage Risk</span>
                    <div className="font-bold text-slate-900 dark:text-slate-100">${result.demurrageRiskUsdTon.toFixed(2)}</div>
                  </div>
                </div>
              </div>

              {/* Rationale & Technical Verification */}
              <div className="p-4 bg-slate-50 dark:bg-slate-800/80 rounded-xl space-y-2 text-xs">
                <div className="font-bold text-slate-900 dark:text-slate-100">
                  AI Decision Rationale & Navigation Safety:
                </div>
                <p className="text-slate-600 dark:text-slate-300 leading-relaxed">
                  {result.decisionRationale}
                </p>
                <div className="text-[11px] font-semibold text-blue-700 dark:text-blue-400 pt-1 border-t border-slate-200 dark:border-slate-700">
                  {result.portCompatibilityNotice}
                </div>
              </div>
            </div>
          ) : (
            <div className="p-12 bg-white dark:bg-slate-850 border border-dashed border-slate-300 dark:border-slate-700 rounded-xl text-center space-y-3">
              <Anchor className="w-12 h-12 text-slate-300 dark:text-slate-600 mx-auto" />
              <h3 className="text-base font-bold text-slate-700 dark:text-slate-300">
                Ready for Procurement Optimization
              </h3>
              <p className="text-xs text-slate-500 max-w-md mx-auto">
                Fill in cargo parcel volume, overseas load port, and target East Coast discharge terminal to compute rate trajectories, Spot vs COA economics, and charter determinations.
              </p>
            </div>
          )}
        </div>
      </div>

      {/* Historical Procurement Dossiers Table with Full Exports */}
      <div className="bg-white dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-xl shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-200 dark:border-slate-800 flex flex-wrap items-center justify-between gap-3">
          <h2 className="text-sm font-bold text-slate-900 dark:text-slate-100 uppercase tracking-wider">
            Generated Bulk Cargo Procurement Dossiers
          </h2>
          <div className="flex items-center gap-2">
            <button
              onClick={() => window.open(freightApi.downloadCsvReport(), '_blank')}
              className="flex items-center gap-1.5 text-xs font-semibold text-blue-700 dark:text-blue-400 bg-blue-50 dark:bg-blue-950/40 px-2.5 py-1 rounded border border-blue-200 dark:border-blue-800 hover:bg-blue-100"
            >
              <Download className="w-3.5 h-3.5" />
              Export CSV
            </button>
            <button
              onClick={() => window.open(freightApi.downloadExcelWorkbook(), '_blank')}
              className="flex items-center gap-1.5 text-xs font-semibold text-emerald-700 dark:text-emerald-400 bg-emerald-50 dark:bg-emerald-950/40 px-2.5 py-1 rounded border border-emerald-200 dark:border-emerald-800 hover:bg-emerald-100"
            >
              <FileSpreadsheet className="w-3.5 h-3.5" />
              Export Master Excel (XLSX)
            </button>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-600 dark:text-slate-400 border-b border-slate-200 dark:border-slate-800 uppercase font-semibold">
              <tr>
                <th className="py-3 px-4">Plan Ref</th>
                <th className="py-3 px-4">Commodity</th>
                <th className="py-3 px-4">Quantity</th>
                <th className="py-3 px-4">Terminal</th>
                <th className="py-3 px-4">Contract</th>
                <th className="py-3 px-4">Decision</th>
                <th className="py-3 px-4">Freight Rate</th>
                <th className="py-3 px-4">Landed Cost</th>
                <th className="py-3 px-4 text-right">Dossier</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800 text-slate-700 dark:text-slate-300">
              {plans.map((p) => (
                <tr key={p.id} className="hover:bg-slate-50/60 dark:hover:bg-slate-800/40">
                  <td className="py-3 px-4 font-mono font-bold text-blue-700 dark:text-blue-400">{p.planReference}</td>
                  <td className="py-3 px-4 font-semibold text-slate-900 dark:text-slate-100">{p.commodity}</td>
                  <td className="py-3 px-4 font-mono">{p.quantityTons.toLocaleString()} MT</td>
                  <td className="py-3 px-4">{p.destinationPort?.portName || 'East Coast'}</td>
                  <td className="py-3 px-4 font-mono text-[10px]">{p.contractType || 'SPOT'}</td>
                  <td className="py-3 px-4">
                    <span
                      className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                        p.charterDecision === 'CHARTER_NOW'
                          ? 'bg-emerald-100 text-emerald-800'
                          : p.charterDecision === 'HOLD_WAIT'
                          ? 'bg-amber-100 text-amber-800'
                          : 'bg-blue-100 text-blue-800'
                      }`}
                    >
                      {p.charterDecision.replace('_', ' ')}
                    </span>
                  </td>
                  <td className="py-3 px-4 font-mono font-semibold">${p.predictedFreightUsdTon.toFixed(2)}</td>
                  <td className="py-3 px-4 font-mono font-bold text-slate-900 dark:text-slate-100">
                    ₹{p.totalLandedCostInrTon.toLocaleString('en-IN', { maximumFractionDigits: 0 })} / MT
                  </td>
                  <td className="py-3 px-4 text-right">
                    <button
                      onClick={() => window.open(freightApi.downloadPlanPdf(p.id), '_blank')}
                      className="inline-flex items-center gap-1 px-2.5 py-1 text-[11px] font-semibold text-blue-700 dark:text-blue-400 bg-blue-50 dark:bg-blue-950/50 hover:bg-blue-100 rounded border border-blue-200 dark:border-blue-800"
                    >
                      <FileText className="w-3.5 h-3.5" />
                      PDF Dossier
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
