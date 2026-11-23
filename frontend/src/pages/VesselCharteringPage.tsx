import React, { useEffect, useState } from 'react';
import {
  Anchor,
  CheckCircle2,
  AlertTriangle,
  Clock,
  ShieldCheck,
  Ship,
  Sliders,
  DollarSign,
  ArrowRight,
  Info,
  Waves,
  Ruler,
} from 'lucide-react';
import { freightApi } from '../api/freightApi';
import { MaritimePort, VesselClass } from '../types';

export const VesselCharteringPage: React.FC = () => {
  const [ports, setPorts] = useState<MaritimePort[]>([]);
  const [vessels, setVessels] = useState<VesselClass[]>([]);
  const [selectedPortCode, setSelectedPortCode] = useState<string>('INPRT');
  const [selectedVesselCode, setSelectedVesselCode] = useState<string>('PANAMAX');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);
        const [pList, vList] = await Promise.all([freightApi.getPorts(), freightApi.getVessels()]);
        setPorts(pList);
        setVessels(vList);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const currentPort = ports.find((p) => p.portCode === selectedPortCode) || ports[0];
  const currentVessel = vessels.find((v) => v.classCode === selectedVesselCode) || vessels[0];

  const draftDifference = currentPort && currentVessel ? currentPort.maxDraftMeters - currentVessel.maxDraftMeters : 0;
  const isDraftFeasible = draftDifference >= -0.3; // Within tide margin
  const isDwtFeasible = currentPort && currentVessel ? currentVessel.minDwt <= currentPort.maxDwt : true;

  const portLoa = currentPort?.maxLoaMeters ?? 280.0;
  const vesselLoa = currentVessel?.typicalLoaMeters ?? 225.0;
  const isLoaFeasible = vesselLoa <= portLoa;

  const portBeam = currentPort?.maxBeamMeters ?? 45.0;
  const vesselBeam = currentVessel?.typicalBeamMeters ?? 32.2;
  const isBeamFeasible = vesselBeam <= portBeam;

  const isFullyFeasible = isDraftFeasible && isDwtFeasible && isLoaFeasible && isBeamFeasible;
  const isHaldia = selectedPortCode === 'INHAL' || selectedPortCode === 'INHLD';

  const dailyDemurrageUsd = currentVessel?.classCode === 'CAPESIZE' ? 28000
    : currentVessel?.classCode === 'PANAMAX' ? 22000
    : currentVessel?.classCode === 'SUPRAMAX' ? 16500 : 12000;

  return (
    <div className="p-6 space-y-6 max-w-[1600px] mx-auto">
      {/* Title */}
      <div className="pb-4 border-b border-slate-200 dark:border-slate-800">
        <div className="flex items-center gap-2">
          <span className="px-2.5 py-0.5 text-[11px] font-bold rounded-full bg-indigo-100 text-indigo-800 dark:bg-indigo-900/60 dark:text-indigo-300">
            VESSEL CHARTERING OPTIMIZER
          </span>
          <span className="text-xs text-slate-500">Draft, LOA & Beam Compatibility Verification</span>
        </div>
        <h1 className="text-2xl font-bold text-slate-900 dark:text-slate-100 mt-1">
          Optimal Vessel Class Matching & Port Navigational Envelope Verification
        </h1>
        <p className="text-sm text-slate-500 dark:text-slate-400">
          Prevents catastrophic demurrage, lighterage delays, and deadfreight penalties by verifying draft, LOA, beam, and lock-gate constraints across East Coast Indian ports
        </p>
      </div>

      {/* Interactive Port-Vessel Compatibility Tester */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="p-6 bg-white dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-xl shadow-sm space-y-5">
          <h2 className="text-sm font-bold text-slate-900 dark:text-slate-100 uppercase tracking-wider flex items-center gap-2">
            <Sliders className="w-4 h-4 text-blue-700" />
            Charter Feasibility Simulator
          </h2>

          <div className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Destination Port (East Coast India)
              </label>
              <select
                value={selectedPortCode}
                onChange={(e) => setSelectedPortCode(e.target.value)}
                className="w-full text-xs font-medium p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 rounded-lg text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-blue-600 outline-none"
              >
                {ports.map((p) => (
                  <option key={p.portCode} value={p.portCode}>
                    {p.portName} (Draft: {p.maxDraftMeters}m | LOA: {p.maxLoaMeters || 280}m | DWT: {p.maxDwt.toLocaleString()})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Candidate Vessel Category
              </label>
              <select
                value={selectedVesselCode}
                onChange={(e) => setSelectedVesselCode(e.target.value)}
                className="w-full text-xs font-medium p-2.5 bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 rounded-lg text-slate-900 dark:text-slate-100 focus:ring-2 focus:ring-blue-600 outline-none"
              >
                {vessels.map((v) => (
                  <option key={v.classCode} value={v.classCode}>
                    {v.className} (Draft: {v.maxDraftMeters}m | LOA: {v.typicalLoaMeters || 225}m | {v.minDwt.toLocaleString()} - {v.maxDwt.toLocaleString()} DWT)
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="pt-3 border-t border-slate-100 dark:border-slate-800">
            <a
              href="/freight/procurement"
              className="w-full flex items-center justify-center gap-2 py-2.5 text-xs font-bold text-white bg-blue-700 hover:bg-blue-800 rounded-lg transition-colors shadow-sm"
            >
              Apply to Bulk Procurement Order
              <ArrowRight className="w-3.5 h-3.5" />
            </a>
          </div>
        </div>

        {/* Live Compatibility Verdict Card */}
        <div className="lg:col-span-2 p-6 bg-white dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-xl shadow-sm space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between pb-4 border-b border-slate-100 dark:border-slate-800 gap-3">
            <div>
              <div className="text-xs font-bold text-slate-400 uppercase tracking-wider">
                COMPATIBILITY & BERTH DISPATCH DETERMINATION
              </div>
              <div className="text-lg font-bold text-slate-900 dark:text-slate-100 mt-0.5">
                {currentVessel?.className} → {currentPort?.portName}
              </div>
            </div>

            <span
              className={`px-3 py-1 rounded-full text-xs font-bold flex items-center gap-1.5 ${
                isFullyFeasible
                  ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-950/60 dark:text-emerald-300'
                  : 'bg-rose-100 text-rose-800 dark:bg-rose-950/60 dark:text-rose-300'
              }`}
            >
              {isFullyFeasible ? (
                <>
                  <CheckCircle2 className="w-4 h-4" />
                  BERTH DISPATCH APPROVED
                </>
              ) : (
                <>
                  <AlertTriangle className="w-4 h-4" />
                  RESTRICTION DETECTED — PROTOCOL ENGAGED
                </>
              )}
            </span>
          </div>

          {/* Navigational Envelope: Draft, LOA, Beam, DWT */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-3 text-xs">
            <div className={`p-3 rounded-lg border ${isDraftFeasible ? 'bg-slate-50 dark:bg-slate-800 border-slate-200 dark:border-slate-700' : 'bg-rose-50 dark:bg-rose-950/40 border-rose-200 dark:border-rose-800'}`}>
              <div className="flex justify-between items-center text-slate-500">
                <span>Channel Draft:</span>
                <span className={`text-[10px] font-bold ${isDraftFeasible ? 'text-emerald-600' : 'text-rose-600'}`}>
                  {isDraftFeasible ? 'PASS' : 'EXCEEDED'}
                </span>
              </div>
              <div className="text-base font-bold text-slate-900 dark:text-slate-100 mt-1">
                {currentVessel?.maxDraftMeters}m / {currentPort?.maxDraftMeters}m
              </div>
              <div className="text-[10px] text-slate-400">Vessel vs Port Draft</div>
            </div>

            <div className={`p-3 rounded-lg border ${isLoaFeasible ? 'bg-slate-50 dark:bg-slate-800 border-slate-200 dark:border-slate-700' : 'bg-rose-50 dark:bg-rose-950/40 border-rose-200 dark:border-rose-800'}`}>
              <div className="flex justify-between items-center text-slate-500">
                <span>Max LOA (Length):</span>
                <span className={`text-[10px] font-bold ${isLoaFeasible ? 'text-emerald-600' : 'text-rose-600'}`}>
                  {isLoaFeasible ? 'PASS' : 'EXCEEDED'}
                </span>
              </div>
              <div className="text-base font-bold text-slate-900 dark:text-slate-100 mt-1">
                {vesselLoa}m / {portLoa}m
              </div>
              <div className="text-[10px] text-slate-400">Lock / Berth Envelope</div>
            </div>

            <div className={`p-3 rounded-lg border ${isBeamFeasible ? 'bg-slate-50 dark:bg-slate-800 border-slate-200 dark:border-slate-700' : 'bg-rose-50 dark:bg-rose-950/40 border-rose-200 dark:border-rose-800'}`}>
              <div className="flex justify-between items-center text-slate-500">
                <span>Max Beam (Width):</span>
                <span className={`text-[10px] font-bold ${isBeamFeasible ? 'text-emerald-600' : 'text-rose-600'}`}>
                  {isBeamFeasible ? 'PASS' : 'EXCEEDED'}
                </span>
              </div>
              <div className="text-base font-bold text-slate-900 dark:text-slate-100 mt-1">
                {vesselBeam}m / {portBeam}m
              </div>
              <div className="text-[10px] text-slate-400">Lock Gate Clearance</div>
            </div>

            <div className="p-3 bg-slate-50 dark:bg-slate-800 rounded-lg border border-slate-200 dark:border-slate-700">
              <span className="text-slate-500">Daily Demurrage:</span>
              <div className="text-base font-bold text-slate-900 dark:text-slate-100 mt-1">
                ${dailyDemurrageUsd.toLocaleString()} / day
              </div>
              <div className="text-[10px] text-amber-600">Berth wait: {currentPort?.averageBerthWaitDays} days</div>
            </div>
          </div>

          {/* Operational Feasibility Notice */}
          <div
            className={`p-4 rounded-xl border text-xs leading-relaxed ${
              isFullyFeasible
                ? 'bg-emerald-50 dark:bg-emerald-950/40 border-emerald-200 dark:border-emerald-800 text-emerald-900 dark:text-emerald-200'
                : 'bg-amber-50 dark:bg-amber-950/40 border-amber-200 dark:border-amber-800 text-amber-900 dark:text-amber-200'
            }`}
          >
            {isFullyFeasible ? (
              <p>
                <strong>Optimal Logistics Match:</strong> {currentVessel?.className} (Draft {currentVessel?.maxDraftMeters}m, LOA {vesselLoa}m, Beam {vesselBeam}m) fully satisfies navigation channel depth and jetty displacement specifications at {currentPort?.portName}. Direct discharge alongside berth is feasible without requiring outer-anchorage lightering barges.
              </p>
            ) : isHaldia ? (
              <p>
                <strong>Haldia Shallow-Draft Protocol:</strong> Haldia Dock Complex has a channel depth limit of 11.0m and lock gate constraints (LOA 230m, Beam 32.4m). {currentVessel?.className} cannot berth directly at full laden displacement. Lighterage of ~25,000–30,000 MT into daughter barges at Sagar Roads / Sandheads anchorage is mandatory, adding ~$4.80/MT transshipment surcharge. <em>Recommendation: Divert to deepwater Dhamra Port (INDHM - 18.0m draft) with South Eastern Railway rake evacuation direct to Durgapur/Bokaro steel plants.</em>
              </p>
            ) : (
              <p>
                <strong>Navigation Bottleneck:</strong> The laden draft of {currentVessel?.className} ({currentVessel?.maxDraftMeters}m) exceeds the maximum permissible draft at {currentPort?.portName} ({currentPort?.maxDraftMeters}m) by {Math.abs(draftDifference).toFixed(1)}m. Direct entry risks grounding. Transshipment via floating crane barges at outer anchorage or diverting to deepwater alternative ports (Dhamra Port INDHM or Gangavaram INGGV) is required.
              </p>
            )}
          </div>
        </div>
      </div>

      {/* East Coast Master Port Specifications Table */}
      <div className="bg-white dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-xl shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
          <h2 className="text-sm font-bold text-slate-900 dark:text-slate-100 uppercase tracking-wider">
            East Coast of India Major Bulk Terminal Technical Matrix
          </h2>
          <span className="text-xs text-slate-500">Ministry of Steel Bulk Cargo Gateways</span>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-600 dark:text-slate-400 border-b border-slate-200 dark:border-slate-800 uppercase font-semibold">
              <tr>
                <th className="py-3 px-4">Port Code</th>
                <th className="py-3 px-4">Terminal Name</th>
                <th className="py-3 px-4">Draft</th>
                <th className="py-3 px-4">Max LOA</th>
                <th className="py-3 px-4">Max Beam</th>
                <th className="py-3 px-4">Max DWT</th>
                <th className="py-3 px-4">Avg Wait</th>
                <th className="py-3 px-4">Congestion</th>
                <th className="py-3 px-4">Lighterage</th>
                <th className="py-3 px-4 text-right">Tariff / Ton</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800 text-slate-700 dark:text-slate-300">
              {ports.map((p) => (
                <tr key={p.portCode} className="hover:bg-slate-50/60 dark:hover:bg-slate-800/40">
                  <td className="py-3 px-4 font-mono font-bold text-blue-700 dark:text-blue-400">{p.portCode}</td>
                  <td className="py-3 px-4 font-bold text-slate-900 dark:text-slate-100">{p.portName}</td>
                  <td className="py-3 px-4 font-semibold text-slate-800 dark:text-slate-200">{p.maxDraftMeters} m</td>
                  <td className="py-3 px-4 font-mono">{p.maxLoaMeters || 280} m</td>
                  <td className="py-3 px-4 font-mono">{p.maxBeamMeters || 45} m</td>
                  <td className="py-3 px-4 font-mono">{p.maxDwt.toLocaleString()} MT</td>
                  <td className="py-3 px-4 font-mono text-amber-600 font-semibold">{p.averageBerthWaitDays} Days</td>
                  <td className="py-3 px-4">
                    <span
                      className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                        p.congestionIndex < 0.35
                          ? 'bg-emerald-100 text-emerald-800'
                          : p.congestionIndex < 0.55
                          ? 'bg-amber-100 text-amber-800'
                          : 'bg-rose-100 text-rose-800'
                      }`}
                    >
                      {(p.congestionIndex * 100).toFixed(0)}%
                    </span>
                  </td>
                  <td className="py-3 px-4">
                    <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${p.lighterageSupported ? 'bg-amber-100 text-amber-800' : 'bg-slate-100 text-slate-600'}`}>
                      {p.lighterageSupported ? 'YES (Sandheads)' : 'Direct'}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-right font-mono font-bold text-slate-900 dark:text-slate-100">
                    ${p.portTariffPerTon.toFixed(2)}
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
