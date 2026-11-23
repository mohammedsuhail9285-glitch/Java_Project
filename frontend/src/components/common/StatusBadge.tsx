import React from 'react';
import { ComplianceStatus } from '../../types';
import { Check, X, AlertTriangle, Clock, ShieldCheck, HelpCircle } from 'lucide-react';

interface StatusBadgeProps {
  status: ComplianceStatus | string;
  size?: 'sm' | 'md';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status, size = 'md' }) => {
  const s = (status || 'PENDING_SCAN').toUpperCase().trim();
  const padding = size === 'sm' ? 'px-1.5 py-0.5 text-[11px]' : 'px-2 py-0.5 text-xs';

  if (s === 'COMPLETED' || s === 'SUCCESS') {
    return (
      <span className={`inline-flex items-center gap-1 font-semibold rounded bg-emerald-50 text-emerald-800 dark:bg-emerald-950/60 dark:text-emerald-300 border border-emerald-300 dark:border-emerald-800 ${padding}`}>
        <Check className="w-3 h-3 text-emerald-600 shrink-0" />
        <span>Completed</span>
      </span>
    );
  }

  if (s === 'PASSED_AUTOMATED_CHECKS' || s === 'COMPLIANT') {
    return (
      <span className={`inline-flex items-center gap-1 font-semibold rounded bg-emerald-50 text-emerald-800 dark:bg-emerald-950/60 dark:text-emerald-300 border border-emerald-300 dark:border-emerald-800 ${padding}`}>
        <Check className="w-3 h-3 text-emerald-600 shrink-0" />
        <span>Compliant</span>
      </span>
    );
  }

  if (s === 'POTENTIAL_NON_COMPLIANT' || s === 'NON_COMPLIANT' || s === 'CONFIRMED') {
    return (
      <span className={`inline-flex items-center gap-1 font-semibold rounded bg-rose-50 text-rose-800 dark:bg-rose-950/60 dark:text-rose-300 border border-rose-300 dark:border-rose-800 ${padding}`}>
        <X className="w-3 h-3 text-rose-600 shrink-0" />
        <span>{s === 'CONFIRMED' ? 'Confirmed Violation' : 'Non-Compliant'}</span>
      </span>
    );
  }

  if (s === 'REQUIRES_REVIEW' || s === 'UNDER_REVIEW') {
    return (
      <span className={`inline-flex items-center gap-1 font-semibold rounded bg-amber-50 text-amber-800 dark:bg-amber-950/60 dark:text-amber-300 border border-amber-300 dark:border-amber-800 ${padding}`}>
        <AlertTriangle className="w-3 h-3 text-amber-600 shrink-0" />
        <span>{s === 'UNDER_REVIEW' ? 'Under Review' : 'Requires Review'}</span>
      </span>
    );
  }

  if (s === 'RESOLVED') {
    return (
      <span className={`inline-flex items-center gap-1 font-semibold rounded bg-blue-50 text-blue-800 dark:bg-blue-950/60 dark:text-blue-300 border border-blue-300 dark:border-blue-800 ${padding}`}>
        <ShieldCheck className="w-3 h-3 text-blue-600 shrink-0" />
        <span>Resolved</span>
      </span>
    );
  }

  if (s === 'DISMISSED' || s === 'FALSE_POSITIVE') {
    return (
      <span className={`inline-flex items-center gap-1 font-medium rounded bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300 border border-slate-300 dark:border-slate-700 ${padding}`}>
        <span>Dismissed</span>
      </span>
    );
  }

  if (s === 'NEW' || s === 'OPEN') {
    return (
      <span className={`inline-flex items-center gap-1 font-semibold rounded bg-purple-50 text-purple-800 dark:bg-purple-950/60 dark:text-purple-300 border border-purple-300 dark:border-purple-800 ${padding}`}>
        <span>Open Finding</span>
      </span>
    );
  }

  return (
    <span className={`inline-flex items-center gap-1 font-medium rounded bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400 border border-slate-200 dark:border-slate-700 ${padding}`}>
      <Clock className="w-3 h-3 text-slate-400 shrink-0" />
      <span>Pending</span>
    </span>
  );
};

export default StatusBadge;
