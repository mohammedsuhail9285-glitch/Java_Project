import React from 'react';
import { Severity } from '../../types';
import { AlertTriangle, AlertCircle, Info, ShieldAlert } from 'lucide-react';

interface SeverityBadgeProps {
  severity: Severity | string;
}

export const SeverityBadge: React.FC<SeverityBadgeProps> = ({ severity }) => {
  const sev = (severity || 'MINOR').toUpperCase();

  if (sev === 'CRITICAL') {
    return (
      <span className="inline-flex items-center gap-1 px-1.5 py-0.5 rounded text-[11px] font-bold uppercase tracking-wider bg-rose-50 text-rose-800 dark:bg-rose-950/60 dark:text-rose-300 border border-rose-300 dark:border-rose-800">
        <ShieldAlert className="w-3 h-3 text-rose-600" />
        Critical
      </span>
    );
  }

  if (sev === 'MAJOR') {
    return (
      <span className="inline-flex items-center gap-1 px-1.5 py-0.5 rounded text-[11px] font-bold uppercase tracking-wider bg-amber-50 text-amber-800 dark:bg-amber-950/60 dark:text-amber-300 border border-amber-300 dark:border-amber-800">
        <AlertTriangle className="w-3 h-3 text-amber-600" />
        Major
      </span>
    );
  }

  if (sev === 'MINOR') {
    return (
      <span className="inline-flex items-center gap-1 px-1.5 py-0.5 rounded text-[11px] font-semibold uppercase tracking-wider bg-blue-50 text-blue-800 dark:bg-blue-950/60 dark:text-blue-300 border border-blue-200 dark:border-blue-800">
        <AlertCircle className="w-3 h-3 text-blue-600" />
        Minor
      </span>
    );
  }

  return (
    <span className="inline-flex items-center gap-1 px-1.5 py-0.5 rounded text-[11px] font-medium uppercase tracking-wider bg-slate-50 text-slate-700 dark:bg-slate-800 dark:text-slate-300 border border-slate-200 dark:border-slate-700">
      <Info className="w-3 h-3 text-slate-500" />
      Info
    </span>
  );
};

export default SeverityBadge;
