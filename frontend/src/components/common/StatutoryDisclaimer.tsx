import React from 'react';
import { ShieldCheck } from 'lucide-react';

interface StatutoryDisclaimerProps {
  compact?: boolean;
}

export const StatutoryDisclaimer: React.FC<StatutoryDisclaimerProps> = ({ compact = false }) => {
  if (compact) {
    return (
      <div className="flex items-center gap-2 px-3 py-1.5 bg-amber-500/10 border border-amber-500/30 rounded-lg text-xs text-amber-800 dark:text-amber-300">
        <ShieldCheck className="w-3.5 h-3.5 shrink-0 text-amber-600 dark:text-amber-400" />
        <span>
          <strong>Statutory Notice:</strong> Automated decision support system under Legal Metrology (Packaged Commodities) Rules, 2011. Flags indicate Potential Non-Compliance requiring officer review.
        </span>
      </div>
    );
  }

  return (
    <div className="flex items-start gap-3.5 p-3.5 bg-gradient-to-r from-amber-500/10 via-amber-500/5 to-transparent border-l-4 border-amber-600 border-t border-r border-b border-amber-500/20 rounded-r-xl text-xs shadow-xs">
      <div className="mt-0.5 shrink-0">
        <div className="w-7 h-7 rounded-lg bg-amber-500/15 border border-amber-500/30 flex items-center justify-center">
          <ShieldCheck className="w-4 h-4 text-amber-600 dark:text-amber-400" />
        </div>
      </div>
      <div className="space-y-1 text-slate-700 dark:text-slate-300 leading-normal flex-1">
        <div className="font-semibold text-amber-950 dark:text-amber-200 flex flex-wrap items-center gap-2 text-xs">
          <span>Department of Consumer Affairs — Legal Metrology Compliance Decision Support</span>
          <span className="text-[10px] px-1.5 py-0.5 bg-amber-500/20 text-amber-800 dark:text-amber-300 rounded font-mono border border-amber-500/30 font-bold">
            LMPC Rules, 2011
          </span>
          <span className="text-[10px] text-slate-500 dark:text-slate-400 font-mono">
            Mandatory Statutory Framework
          </span>
        </div>
        <p className="text-[11px] text-slate-600 dark:text-slate-400">
          <strong>Official Legal Advisory:</strong> Declarations extracted via computer vision OCR are evaluated against statutory rules as an automated pre-screening filter. Findings represent <em>"Potential Non-Compliance"</em> to guide inspection priorities. Formal legal adjudication and show-cause proceedings remain strictly reserved for authorized statutory enforcement officers under the Legal Metrology Act, 2009.
        </p>
      </div>
    </div>
  );
};

export default StatutoryDisclaimer;
