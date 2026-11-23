import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { ShieldCheck, Search, Bell, LogOut, CheckCircle2 } from 'lucide-react';
import { Link, useLocation } from 'react-router-dom';

import { aiMonitoringApi } from '../../api/aiMonitoringApi';

interface HeaderProps {
  onOpenCommandPalette?: () => void;
}

export const Header: React.FC<HeaderProps> = ({ onOpenCommandPalette }) => {
  const { user, logout } = useAuth();
  const location = useLocation();
  const [ocrEngine, setOcrEngine] = React.useState<string | null>(null);

  React.useEffect(() => {
    aiMonitoringApi.getMonitoringMetrics()
      .then((m) => {
        setOcrEngine(m.activeEngine || (m.tesseractAvailable ? 'TESSERACT' : 'SYNTHETIC'));
      })
      .catch(() => setOcrEngine('SYNTHETIC'));
  }, []);

  // Simple path breadcrumb title
  const getPageTitle = (pathname: string) => {
    if (pathname === '/' || pathname === '/dashboard') return 'Compliance Overview';
    if (pathname.startsWith('/single-scan') || pathname.startsWith('/scans/new')) return 'New Compliance Scan';
    if (pathname.startsWith('/batch-scan')) return 'Batch Catalog Upload';
    if (pathname.startsWith('/scan-history') || pathname.startsWith('/scans')) return 'Scan Execution History';
    if (pathname.startsWith('/products')) return 'Monitored Products';
    if (pathname.startsWith('/violations')) return 'Violations Review Queue';
    if (pathname.startsWith('/analytics')) return 'Compliance Analytics';
    if (pathname.startsWith('/rules')) return 'Statutory Rule Engine';
    if (pathname.startsWith('/reports')) return 'Reports & Exports';
    if (pathname.startsWith('/users')) return 'User Administration';
    if (pathname.startsWith('/audit-logs') || pathname.startsWith('/audit-log')) return 'Audit Trail';
    if (pathname.startsWith('/ai-monitoring')) return 'AI Model Monitoring';
    return 'LegalMetrix Sentinel';
  };

  return (
    <header className="h-14 border-b border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 sticky top-0 z-30 px-5 flex items-center justify-between select-none">
      {/* Brand & Authority */}
      <div className="flex items-center gap-3">
        <Link to="/" className="flex items-center gap-2.5">
          <div className="w-8 h-8 rounded bg-blue-900 flex items-center justify-center text-amber-400 shrink-0">
            <ShieldCheck className="w-4 h-4" />
          </div>
          <div className="flex items-baseline gap-2">
            <span className="text-sm font-bold tracking-tight text-slate-900 dark:text-slate-100">
              LegalMetrix
            </span>
            <span className="text-[10px] font-mono font-semibold px-1 py-0.2 rounded bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 border border-slate-200 dark:border-slate-700">
              REGULATORY
            </span>
          </div>
        </Link>

        <div className="h-4 w-px bg-slate-200 dark:bg-slate-800 hidden sm:block" />

        <div className="text-xs font-medium text-slate-500 dark:text-slate-400 hidden sm:block truncate max-w-xs">
          {getPageTitle(location.pathname)}
        </div>
      </div>

      {/* Center Search / Command Launcher */}
      <div className="hidden md:flex items-center gap-2">
        <button
          type="button"
          onClick={() => {
            if (onOpenCommandPalette) onOpenCommandPalette();
            window.dispatchEvent(new Event('open-command-palette'));
          }}
          className="flex items-center gap-2.5 px-2.5 py-1 text-xs text-slate-400 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded hover:border-slate-300 transition w-56"
        >
          <Search className="w-3.5 h-3.5" />
          <span className="flex-1 text-left text-slate-500 text-[11px]">Quick search...</span>
          <kbd className="text-[10px] font-mono px-1 py-0.2 bg-white dark:bg-slate-900 rounded border border-slate-200 dark:border-slate-700 text-slate-400">
            Ctrl K
          </kbd>
        </button>

        <div className="hidden lg:flex items-center gap-1.5 px-2 py-0.5 rounded text-[10px] font-semibold text-emerald-800 dark:text-emerald-300 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800">
          <CheckCircle2 className="w-3 h-3 text-emerald-600" />
          <span>Statutory Engine Active</span>
        </div>

        {ocrEngine && (
          ocrEngine.toUpperCase().includes('SYNTHETIC') ? (
            <span
              title="Synthetic OCR fallback active. Real Tesseract is not engaged."
              className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-bold bg-amber-100 text-amber-900 border border-amber-300 dark:bg-amber-950/70 dark:text-amber-300 dark:border-amber-700 shadow-2xs"
            >
              <span className="w-1.5 h-1.5 rounded-full bg-amber-500 animate-pulse" />
              Demo OCR
            </span>
          ) : (
            <span
              title="Active OCR Engine: Real Tesseract OCR"
              className="hidden sm:inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-bold bg-blue-50 text-blue-800 border border-blue-200 dark:bg-blue-950/60 dark:text-blue-300 dark:border-blue-800"
            >
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500" />
              Tesseract OCR
            </span>
          )
        )}
      </div>

      {/* Right User & Actions */}
      <div className="flex items-center gap-3">
        {user && (
          <div className="flex items-center gap-2.5">
            <Link to="/profile" title="View Profile & Account Settings" className="flex items-center gap-2.5 hover:opacity-80 transition cursor-pointer">
              <div className="text-right hidden sm:block">
                <div className="text-xs font-semibold text-slate-800 dark:text-slate-200 leading-tight">
                  {user.fullName || user.username}
                </div>
                <div className="text-[10px] font-mono uppercase tracking-wider text-slate-400 dark:text-slate-500">
                  {user.role}
                </div>
              </div>

              <div className="w-7 h-7 rounded bg-slate-100 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 flex items-center justify-center font-bold text-xs text-slate-700 dark:text-slate-300 uppercase">
                {user.username.substring(0, 2)}
              </div>
            </Link>

            <button
              onClick={logout}
              title="Logout session"
              className="p-1 text-slate-400 hover:text-rose-600 dark:hover:text-rose-400 rounded hover:bg-slate-100 dark:hover:bg-slate-800 transition"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header;
