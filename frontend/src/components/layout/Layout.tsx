import React from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import { Header } from './Header';
import { Sidebar } from './Sidebar';
import { StatutoryDisclaimer } from '../common/StatutoryDisclaimer';
import { CommandPalette } from '../common/CommandPalette';

export const Layout: React.FC = () => {
  const location = useLocation();
  const isFreight = location.pathname.startsWith('/freight');
  const isProfile = location.pathname.startsWith('/profile');

  return (
    <div className="h-screen w-screen flex overflow-hidden bg-slate-100/60 dark:bg-slate-950 text-slate-900 dark:text-slate-100">
      {/* Bespoke Dark Sidebar Pillar - Full Viewport Height */}
      <Sidebar />

      {/* Main Workspace Pillar - Independent Smooth Scroll */}
      <div className="flex-1 flex flex-col h-screen min-w-0 overflow-hidden">
        <Header />
        <CommandPalette />
        <main className="flex-1 overflow-y-auto p-6 w-full">
          <div className="max-w-7xl mx-auto space-y-6">
            {!isFreight && !isProfile && <StatutoryDisclaimer />}
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
};

export default Layout;
