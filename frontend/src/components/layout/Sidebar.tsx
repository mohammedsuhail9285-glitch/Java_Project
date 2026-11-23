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
      title: 'OVERVIEW',
      items: [
        { to: '/', label: 'Dashboard', icon: LayoutDashboard, exact: true },
      ],
    },
    {
      title: 'FREIGHT & CHARTER (SIH26006)',
      items: [
        { to: '/freight', label: 'Freight Hub', icon: Ship, exact: true },
        { to: '/freight/forecasts', label: 'Rate Forecasts', icon: TrendingUp },
        { to: '/freight/chartering', label: 'Vessel Chartering', icon: Anchor },
        { to: '/freight/procurement', label: 'Bulk Procurement', icon: Layers },
      ],
    },
    {
      title: 'COMPLIANCE',
      items: [
        { to: '/single-scan', label: 'New Scan', icon: Search },
        { to: '/batch-scan', label: 'Batch Scan', icon: UploadCloud },
        { to: '/scan-history', label: 'Scan History', icon: History },
        { to: '/products', label: 'Products', icon: Package },
        { to: '/violations', label: 'Violations', icon: AlertOctagon },
      ],
    },
    {
      title: 'INTELLIGENCE',
      items: [
        { to: '/analytics', label: 'Analytics', icon: BarChart3 },
        { to: '/rules', label: 'Rules', icon: Sliders },
      ],
    },
    {
      title: 'REPORTING',
      items: [
        { to: '/reports', label: 'Reports', icon: FileSpreadsheet },
      ],
    },
    {
      title: 'ADMINISTRATION',
      items: [
        ...(hasRole('ADMIN')
          ? [{ to: '/users', label: 'Users', icon: Users }]
          : []),
        ...(hasRole('ADMIN', 'REGULATOR')
          ? [{ to: '/audit-logs', label: 'Audit Log', icon: Shield }]
          : []),
        { to: '/ai-monitoring', label: 'AI Health', icon: Activity },
      ],
    },
  ];

  return (
    <aside className="w-60 border-r border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 flex flex-col justify-between shrink-0 min-h-[calc(100vh-4rem)] select-none">
      {/* Navigation Sections */}
      <div className="py-4 px-2 space-y-4 overflow-y-auto max-h-[calc(100vh-10rem)]">
        {sections.map((section) => {
          if (section.items.length === 0) return null;
          return (
            <div key={section.title} className="space-y-0.5">
              <div className="px-2.5 py-1 text-[10px] font-bold uppercase tracking-wider text-slate-400 dark:text-slate-500">
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
                        `flex items-center gap-2.5 py-1.5 pr-2 rounded text-xs transition-colors duration-150 ${
                          isActive
                            ? 'border-l-[3px] border-blue-800 bg-slate-100/90 text-slate-950 font-semibold dark:border-blue-400 dark:bg-slate-800/80 dark:text-slate-100 pl-[9px]'
                            : 'border-l-[3px] border-transparent text-slate-600 hover:text-slate-900 hover:bg-slate-50 dark:text-slate-400 dark:hover:text-slate-200 dark:hover:bg-slate-800/40 pl-[9px]'
                        }`
                      }
                    >
                      <Icon className="w-4 h-4 shrink-0 text-slate-500 dark:text-slate-400" />
                      <span>{item.label}</span>
                    </NavLink>
                  );
                })}
              </div>
            </div>
          );
        })}
      </div>

      {/* User Card & Statutory Badge Footer */}
      <div className="p-3 border-t border-slate-200 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-900/50 space-y-2">
        {user && (
          <div className="flex items-center justify-between p-2 rounded border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-800">
            <NavLink to="/profile" title="View Profile" className="flex items-center gap-2 overflow-hidden hover:opacity-85 transition">
              <div className="w-7 h-7 rounded bg-slate-100 dark:bg-slate-700 text-slate-800 dark:text-slate-200 font-bold text-xs flex items-center justify-center shrink-0 uppercase border border-slate-200 dark:border-slate-600">
                {user.username.substring(0, 2)}
              </div>
              <div className="truncate">
                <div className="text-xs font-semibold text-slate-800 dark:text-slate-200 truncate">
                  {user.fullName || user.username}
                </div>
                <div className="text-[10px] font-mono text-slate-400 dark:text-slate-500 uppercase tracking-wider">
                  {user.role}
                </div>
              </div>
            </NavLink>

            <button
              onClick={logout}
              title="Sign Out"
              className="p-1 text-slate-400 hover:text-rose-600 dark:hover:text-rose-400 rounded transition"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>
        )}

        <div className="px-2 py-1 rounded border border-slate-200 dark:border-slate-800 text-[10px] text-slate-500 dark:text-slate-400 flex items-center gap-1.5 font-medium bg-white dark:bg-slate-800/40">
          <Shield className="w-3 h-3 text-blue-700 dark:text-blue-400 shrink-0" />
          <span className="truncate">LMPC Rules, 2011 Active</span>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
