import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Search,
  UploadCloud,
  History,
  AlertOctagon,
  Sliders,
  FileSpreadsheet,
  Users,
  Package,
  Activity,
  Shield,
  BarChart3,
  LogOut,
  Ship,
  TrendingUp,
  Anchor,
  Layers,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const Sidebar: React.FC = () => {
  const { user, hasRole, logout } = useAuth();

  const sections = [
    {
      title: 'PLATFORM OVERVIEW',
      items: [
        { to: '/', label: 'Command Center', icon: LayoutDashboard, exact: true },
      ],
    },
    {
      title: 'MARITIME FREIGHT (SIH26006)',
      items: [
        { to: '/freight', label: 'Freight Hub', icon: Ship, exact: true, badge: 'AI CORE' },
        { to: '/freight/forecasts', label: 'Rate Forecasts', icon: TrendingUp, badge: 'ML' },
        { to: '/freight/chartering', label: 'Vessel Chartering', icon: Anchor },
        { to: '/freight/procurement', label: 'Bulk Procurement', icon: Layers, badge: 'SPOT/COA' },
      ],
    },
    {
      title: 'LEGAL METROLOGY (LMPC)',
      items: [
        { to: '/single-scan', label: 'New Inspection', icon: Search },
        { to: '/batch-scan', label: 'Batch Upload', icon: UploadCloud },
        { to: '/scan-history', label: 'Scan History', icon: History },
        { to: '/products', label: 'Catalog Registry', icon: Package },
        { to: '/violations', label: 'Violations Queue', icon: AlertOctagon, badgeAlert: true },
      ],
    },
    {
      title: 'INTELLIGENCE & AUDIT',
      items: [
        { to: '/analytics', label: 'Operational Analytics', icon: BarChart3 },
        { to: '/rules', label: 'Statutory Rules', icon: Sliders },
        { to: '/reports', label: 'Regulatory Reports', icon: FileSpreadsheet },
      ],
    },
    {
      title: 'SYSTEM & GOVERNANCE',
      items: [
        ...(hasRole('ADMIN')
          ? [{ to: '/users', label: 'Access Control', icon: Users }]
          : []),
        ...(hasRole('ADMIN', 'REGULATOR')
          ? [{ to: '/audit-logs', label: 'Audit Trail', icon: Shield }]
          : []),
        { to: '/ai-monitoring', label: 'AI Microservice', icon: Activity, badge: '8000' },
      ],
    },
  ];

  return (
    <aside className="w-64 h-full bg-[#090D16] border-r border-slate-800/80 flex flex-col justify-between shrink-0 select-none z-40 shadow-2xl">
      {/* Brand Logo & Platform Title */}
      <div className="h-16 px-4 flex items-center gap-3 border-b border-slate-800/80 bg-[#070A11] shrink-0">
        <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-blue-600 via-indigo-600 to-cyan-400 p-[1.5px] shadow-lg shadow-blue-500/20 shrink-0">
          <div className="w-full h-full bg-[#090D16] rounded-[10px] flex items-center justify-center">
            <Shield className="w-4 h-4 text-cyan-400" />
          </div>
        </div>
        <div className="min-w-0 flex-1">
          <div className="flex items-center gap-1.5">
            <span className="font-bold text-sm text-white tracking-tight truncate">RegTech Sentinel</span>
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" title="System Online" />
          </div>
          <div className="text-[10px] font-mono font-medium text-slate-400 tracking-wider truncate flex items-center gap-1">
            <span className="text-cyan-400 font-semibold">SIH26006</span>
            <span>·</span>
            <span>DUAL-CORE</span>
          </div>
        </div>
      </div>

      {/* Navigation Sections */}
      <div className="flex-1 py-4 px-3 space-y-5 overflow-y-auto sidebar-scroll min-h-0">
        {sections.map((section) => {
          if (section.items.length === 0) return null;
          return (
            <div key={section.title} className="space-y-1">
              <div className="px-2.5 py-1 text-[10px] font-bold uppercase tracking-widest text-slate-500">
                {section.title}
              </div>
              <div className="space-y-0.5">
                {section.items.map((item) => {
                  const Icon = item.icon;
                  return (
                    <NavLink
                      key={item.to}
                      to={item.to}
                      end={item.exact}
                      className={({ isActive }) =>
                        `group relative flex items-center justify-between px-2.5 py-2 rounded-lg text-xs font-medium transition-all duration-150 border-l-[3px] ${
                          isActive
                            ? 'bg-gradient-to-r from-blue-600/25 via-blue-600/10 to-transparent text-blue-300 font-semibold border-blue-500 shadow-[inset_0_1px_0_0_rgba(255,255,255,0.05)]'
                            : 'border-transparent text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'
                        }`
                      }
                    >
                      {({ isActive }) => (
                        <>
                          <div className="flex items-center gap-2.5 min-w-0">
                            <Icon
                              className={`w-4 h-4 shrink-0 transition-colors ${
                                isActive ? 'text-blue-400' : 'text-slate-500 group-hover:text-slate-300'
                              }`}
                            />
                            <span className="truncate">{item.label}</span>
                          </div>

                          {item.badge && (
                            <span
                              className={`text-[9px] font-mono px-1.5 py-0.5 rounded font-bold uppercase tracking-wider shrink-0 ${
                                isActive
                                  ? 'bg-blue-500/20 text-blue-300 border border-blue-400/30'
                                  : 'bg-slate-800 text-slate-400 border border-slate-700/60 group-hover:border-slate-600 group-hover:text-slate-300'
                              }`}
                            >
                              {item.badge}
                            </span>
                          )}

                          {item.badgeAlert && (
                            <span
                              title="Active violations"
                              className="w-2 h-2 rounded-full bg-rose-500 shadow-[0_0_6px_rgba(244,63,94,0.8)] shrink-0"
                            />
                          )}
                        </>
                      )}
                    </NavLink>
                  );
                })}
              </div>
            </div>
          );
        })}
      </div>

      {/* Footer: Live Telemetry & User Card */}
      <div className="p-3 border-t border-slate-800/80 bg-[#070A11] space-y-2 shrink-0">
        {/* Baltic Dry Index / Fleet Telemetry Pill */}
        <div className="px-2.5 py-1.5 rounded-lg border border-slate-800 bg-slate-900/80 flex items-center justify-between text-[10px]">
          <div className="flex items-center gap-1.5 text-slate-300 font-mono">
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75" />
              <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500" />
            </span>
            <span className="font-semibold text-slate-300">BDI 1,890 pts</span>
          </div>
          <span className="text-emerald-400 font-semibold font-mono flex items-center gap-0.5">
            ▲ +1.4%
          </span>
        </div>

        {/* User Card */}
        {user && (
          <div className="flex items-center justify-between p-2 rounded-xl border border-slate-800/90 bg-slate-900/60 hover:bg-slate-800/60 transition group">
            <NavLink to="/profile" title="View Profile" className="flex items-center gap-2.5 overflow-hidden flex-1">
              <div className="w-8 h-8 rounded-lg bg-gradient-to-tr from-blue-600 to-indigo-600 text-white font-bold text-xs flex items-center justify-center shrink-0 uppercase shadow-inner shadow-white/20 border border-blue-400/30">
                {user.username.substring(0, 2)}
              </div>
              <div className="truncate flex-1">
                <div className="text-xs font-semibold text-white truncate group-hover:text-blue-300 transition">
                  {user.fullName || user.username}
                </div>
                <div className="flex items-center gap-1.5 mt-0.5">
                  <span
                    className={`text-[9px] font-mono uppercase px-1 py-0.2 rounded font-semibold ${
                      user.role === 'ADMIN'
                        ? 'bg-purple-900/60 text-purple-300 border border-purple-700/60'
                        : user.role === 'REGULATOR'
                        ? 'bg-amber-900/60 text-amber-300 border border-amber-700/60'
                        : user.role === 'ANALYST'
                        ? 'bg-cyan-900/60 text-cyan-300 border border-cyan-700/60'
                        : 'bg-blue-900/60 text-blue-300 border border-blue-700/60'
                    }`}
                  >
                    {user.role}
                  </span>
                  <span className="text-[10px] text-slate-500 font-mono">ONLINE</span>
                </div>
              </div>
            </NavLink>

            <button
              onClick={logout}
              title="Sign Out"
              className="p-1.5 text-slate-400 hover:text-rose-400 hover:bg-rose-950/30 rounded-lg transition ml-1"
            >
              <LogOut className="w-3.5 h-3.5" />
            </button>
          </div>
        )}

        {/* Dual Core Engine Indicator */}
        <div className="px-2 py-0.5 rounded text-[10px] text-slate-500 flex items-center justify-between">
          <div className="flex items-center gap-1.5">
            <Shield className="w-3 h-3 text-cyan-400 shrink-0" />
            <span className="truncate">Dual Core Engines</span>
          </div>
          <span className="font-mono text-[9px] text-slate-600">v2.4.1</span>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
