import React, { useState, useEffect } from 'react';
import { aiMonitoringApi, AiMonitoringData } from '../api/aiMonitoringApi';
import { PageHeader } from '../components/layout/PageHeader';
import {
  Activity,
  Cpu,
  CheckCircle2,
  AlertTriangle,
  RefreshCw,
  Server,
  Zap,
} from 'lucide-react';

export const AiMonitoringPage: React.FC = () => {
  const [data, setData] = useState<AiMonitoringData | null>(null);
  const [loading, setLoading] = useState(true);

  const loadMetrics = async () => {
    try {
      setLoading(true);
      const metrics = await aiMonitoringApi.getMonitoringMetrics();
      setData(metrics);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMetrics();
  }, []);

  if (loading || !data) {
    return (
      <div className="flex items-center justify-center min-h-[50vh]">
        <RefreshCw className="w-5 h-5 text-blue-800 animate-spin" />
      </div>
    );
  }

  return (
    <div className="space-y-5 pb-16">
      <PageHeader
        title="AI & OCR System Telemetry"
        description="Operational health of the OCR inference pipeline, neural extraction confidence, and regulatory override statistics."
        breadcrumbs={[{ label: 'Administration' }, { label: 'AI Telemetry' }]}
        action={
          <button
            onClick={loadMetrics}
            className="px-3 py-1.5 border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 rounded-md text-slate-700 dark:text-slate-300 hover:bg-slate-50 text-xs font-semibold flex items-center gap-1.5 transition"
            title="Refresh metrics"
          >
            <RefreshCw className="w-3.5 h-3.5 text-blue-800" />
            <span>Refresh</span>
          </button>
        }
      />

      {/* Health Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="p-4 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900">
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-semibold uppercase tracking-wider text-slate-500">
              AI Microservice
            </span>
            <Server className="w-3.5 h-3.5 text-blue-800" />
          </div>
          <div className="mt-2 flex items-center gap-2">
            <span
              className={`w-2 h-2 rounded-full ${
                data.aiServiceOnline ? 'bg-emerald-600' : 'bg-amber-500'
              }`}
            />
            <span className="text-sm font-bold text-slate-900 dark:text-slate-100">
              {data.aiServiceOnline ? 'ONLINE (Port 8000)' : 'STANDALONE FALLBACK'}
            </span>
          </div>
          <div className="text-[10px] text-slate-400 mt-1 font-mono">
            {data.aiServiceUrl}
          </div>
        </div>

        <div className="p-4 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900">
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-semibold uppercase tracking-wider text-slate-500">
              Active OCR Engine
            </span>
            <Cpu className="w-3.5 h-3.5 text-blue-800" />
          </div>
          <div className="mt-2 text-sm font-bold font-mono text-slate-900 dark:text-slate-100">
            {data.activeEngine}
          </div>
          <div className="text-[10px] text-slate-400 mt-1">
            System Tesseract: {data.tesseractAvailable ? 'Available' : 'Fallback Engaged'}
          </div>
        </div>

        <div className="p-4 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900">
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-semibold uppercase tracking-wider text-slate-500">
              Average OCR Confidence
            </span>
            <Zap className="w-3.5 h-3.5 text-amber-600" />
          </div>
          <div className="mt-2 text-xl font-bold font-mono text-slate-900 dark:text-slate-100">
            {data.avgOcrConfidence}%
          </div>
          <div className="text-[10px] text-slate-400 mt-1 font-mono">
            Across {data.totalOcrRuns} statutory scans
          </div>
        </div>
      </div>

      {/* Reviewer Override Trail & Feedback */}
      <div className="bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 p-4 space-y-3">
        <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-2.5">
          <div>
            <h3 className="text-xs font-bold text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
              <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
              <span>Human Regulator Override Metrics</span>
            </h3>
            <p className="text-[11px] text-slate-500">
              Corroborating officer confirmations vs automated false-positive dismissals.
            </p>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-4 gap-3 pt-1">
          <div className="p-3 rounded-md bg-slate-50 dark:bg-slate-800/50 border border-slate-200/80 dark:border-slate-800">
            <div className="text-[10px] font-semibold text-slate-500 uppercase tracking-wider">
              Total Human Reviews
            </div>
            <div className="text-lg font-bold font-mono text-slate-900 dark:text-slate-100 mt-0.5">
              {data.totalReviews ?? 0}
            </div>
          </div>

          <div className="p-3 rounded-md bg-slate-50 dark:bg-slate-800/50 border border-slate-200/80 dark:border-slate-800">
            <div className="text-[10px] font-semibold text-slate-500 uppercase tracking-wider">
              Confirmed Violations
            </div>
            <div className="text-lg font-bold font-mono text-rose-700 dark:text-rose-400 mt-0.5">
              {data.confirmedViolations ?? 0}
            </div>
          </div>

          <div className="p-3 rounded-md bg-slate-50 dark:bg-slate-800/50 border border-slate-200/80 dark:border-slate-800">
            <div className="text-[10px] font-semibold text-slate-500 uppercase tracking-wider">
              Dismissed (False Positives)
            </div>
            <div className="text-lg font-bold font-mono text-slate-700 dark:text-slate-300 mt-0.5">
              {data.dismissedFalsePositives ?? 0}
            </div>
          </div>

          <div className="p-3 rounded-md bg-slate-50 dark:bg-slate-800/50 border border-slate-200/80 dark:border-slate-800">
            <div className="text-[10px] font-semibold text-slate-500 uppercase tracking-wider">
              Precision Alignment
            </div>
            <div className="text-lg font-bold font-mono text-blue-900 dark:text-blue-400 mt-0.5">
              {data.confirmedViolations && data.totalReviews
                ? `${Math.round((data.confirmedViolations / data.totalReviews) * 100)}%`
                : '100%'}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
