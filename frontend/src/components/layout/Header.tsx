import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { ShieldCheck, Search, LogOut, CheckCircle2, Ship, Activity, TrendingUp } from 'lucide-react';
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

  const isFreight = location.pathname.startsWith('/freight');

  // Breadcrumb title resolver
  const getPageTitle = (pathname: string) => {
    if (pathname === '/' || pathname === '/dashboard') return 'Executive Command Center';
    if (pathname === '/freight') return 'Maritime Freight Hub & Fleet Status';
    if (pathname.startsWith('/freight/forecasts')) return 'Freight Rate Forecasting & Market Trends';
    if (pathname.startsWith('/freight/chartering')) return 'Vessel Chartering & Port Compatibility';
    if (pathname.startsWith('/freight/procurement')) return 'Bulk Cargo Procurement & Spot vs COA';
    if (pathname.startsWith('/single-scan') || pathname.startsWith('/scans/new')) return 'New Compliance Inspection';
    if (pathname.startsWith('/batch-scan')) return 'Batch Catalog Verification';
    if (pathname.startsWith('/scan-history') || pathname.startsWith('/scans')) return 'Inspection Execution History';
    if (pathname.startsWith('/products')) return 'Monitored Products Registry';
    if (pathname.startsWith('/violations')) return 'Violations Review Queue';
    if (pathname.startsWith('/analytics')) return 'Operational Compliance Analytics';
    if (pathname.startsWith('/rules')) return 'Statutory Rule Engine (LMPC 2011)';
    if (pathname.startsWith('/reports')) return 'Regulatory Reports & Exports';
    if (pathname.startsWith('/users')) return 'Access Control & Users';
    if (pathname.startsWith('/audit-logs') || pathname.startsWith('/audit-log')) return 'System Audit Trail';
    if (pathname.startsWith('/ai-monitoring')) return 'AI Model Monitoring & Telemetry';
    if (pathname.startsWith('/profile')) return 'User Profile & Security';
    return 'RegTech Sentinel Portal';
  };

  return (
    <header className="h-16 shrink-0 border-b border-slate-200 dark:border-slate-800 bg-white/95 dark:bg-slate-900/95 backdrop-blur-md sticky top-0 z-30 px-6 flex items-center justify-between select-none">
      {/* Breadcrumb Navigation Context */}
      <div className="flex items-center gap-2.5">
        <div
          className={`flex items-center gap-1.5 font-mono text-[11px] font-semibold px-2 py-0.5 rounded border ${
            isFreight
              ? 'text-cyan-700 bg-cyan-50 border-cyan-200 dark:text-cyan-300 dark:bg-cyan-950/50 dark:border-cyan-800'
              : 'text-blue-700 bg-blue-50 border-blue-200 dark:text-blue-300 dark:bg-blue-950/50 dark:border-blue-800'
          }`}
        >
          {isFreight ? (
            <>
              <Ship className="w-3.5 h-3.5 text-cyan-600 dark:text-cyan-400" />
              <span>SIH26006 · MARITIME FREIGHT</span>
            </>
          ) : (
            <>
              <ShieldCheck className="w-3.5 h-3.5 text-blue-600 dark:text-blue-400" />
              <span>LEGAL METROLOGY</span>
            </>
          )}
        </div>

        <span className="text-slate-300 dark:text-slate-700 text-sm font-light">/</span>

        <span className="font-semibold text-slate-800 dark:text-slate-200 text-xs sm:text-sm tracking-tight truncate max-w-xs sm:max-w-md">
          {getPageTitle(location.pathname)}
        </span>
      </div>

      {/* Center Quick Search & Telemetry Controls */}
      <div className="hidden md:flex items-center gap-2.5">
        <button
          type="button"
          onClick={() => {
            if (onOpenCommandPalette) onOpenCommandPalette();
            window.dispatchEvent(new Event('open-command-palette'));
          }}
          className="flex items-center gap-2 px-3 py-1.5 text-xs text-slate-400 bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700/80 rounded-lg hover:border-slate-300 dark:hover:border-slate-600 hover:text-slate-600 dark:hover:text-slate-200 transition w-52 shadow-2xs"
        >
          <Search className="w-3.5 h-3.5 text-slate-400" />
          <span className="flex-1 text-left text-[11px]">Command Palette...</span>
          <kbd className="text-[10px] font-mono px-1.5 py-0.5 bg-white dark:bg-slate-900 rounded border border-slate-200 dark:border-slate-700 text-slate-400">
            Ctrl K
          </kbd>
        </button>

        {/* Freight Ticker or Statutory Engine Badge */}
        {isFreight ? (
          <div className="hidden lg:flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[10px] font-semibold text-cyan-800 dark:text-cyan-300 bg-cyan-50 dark:bg-cyan-950/40 border border-cyan-200 dark:border-cyan-800">
            <TrendingUp className="w-3 h-3 text-cyan-600 dark:text-cyan-400" />
            <span>Baltic Dry Index: 1,890 pts</span>
          </div>
        ) : (
          <div className="hidden lg:flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[10px] font-semibold text-emerald-800 dark:text-emerald-300 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800">
            <CheckCircle2 className="w-3 h-3 text-emerald-600 dark:text-emerald-400" />
            <span>Statutory Engine Active</span>
          </div>
        )}

        {/* OCR Engine Telemetry */}
        {ocrEngine && (
          ocrEngine.toUpperCase().includes('SYNTHETIC') ? (
            <span
              title="Synthetic OCR fallback active. Real Tesseract is not engaged."
              className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg text-[10px] font-bold bg-amber-50 text-amber-800 border border-amber-200 dark:bg-amber-950/70 dark:text-amber-300 dark:border-amber-700 shadow-2xs"
            >
              <span className="w-1.5 h-1.5 rounded-full bg-amber-500 animate-pulse" />
              Demo OCR
            </span>
          ) : (
            <span
              title="Active OCR Engine: Real Tesseract OCR"
              className="hidden sm:inline-flex items-center gap-1 px-2.5 py-1 rounded-lg text-[10px] font-bold bg-blue-50 text-blue-800 border border-blue-200 dark:bg-blue-950/60 dark:text-blue-300 dark:border-blue-800"
            >
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500" />
              Tesseract OCR
            </span>
          )
        )}
      </div>

      {/* Right User Profile & Quick Sign Out */}
      <div className="flex items-center gap-3">
        {user && (
          <div className="flex items-center gap-2">
            <Link
              to="/profile"
              title="View Profile & Settings"
              className="flex items-center gap-2.5 p-1 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition"
            >
              <div className="text-right hidden sm:block">
                <div className="text-xs font-semibold text-slate-800 dark:text-slate-200 leading-tight">
                  {user.fullName || user.username}
                </div>
                <div className="text-[10px] font-mono uppercase tracking-wider text-slate-400 dark:text-slate-500">
                  {user.role}
                </div>
              </div>

              <div className="w-8 h-8 rounded-lg bg-gradient-to-tr from-blue-600 to-indigo-600 text-white font-bold text-xs flex items-center justify-center uppercase shadow-2xs">
                {user.username.substring(0, 2)}
              </div>
            </Link>

            <button
              onClick={logout}
              title="Sign Out Session"
              className="p-1.5 text-slate-400 hover:text-rose-600 dark:hover:text-rose-400 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition"
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
