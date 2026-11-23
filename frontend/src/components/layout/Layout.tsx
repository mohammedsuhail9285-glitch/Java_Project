import React from 'react';
import { Outlet } from 'react-router-dom';
import { Header } from './Header';
import { Sidebar } from './Sidebar';
import { StatutoryDisclaimer } from '../common/StatutoryDisclaimer';
import { CommandPalette } from '../common/CommandPalette';

export const Layout: React.FC = () => {
  return (
    <div className="min-h-screen bg-slate-100/50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 flex flex-col">
      <Header />
      <CommandPalette />
      <div className="flex flex-1">
        <Sidebar />
        <main className="flex-1 p-6 overflow-y-auto max-w-7xl mx-auto w-full space-y-6">
          <StatutoryDisclaimer />
          <Outlet />
        </main>
      </div>
    </div>
  );
};
