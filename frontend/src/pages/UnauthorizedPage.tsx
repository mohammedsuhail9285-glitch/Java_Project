import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ShieldAlert, ArrowLeft, UserCheck } from 'lucide-react';

export const UnauthorizedPage: React.FC = () => {
  const { user } = useAuth();

  return (
    <div className="min-h-[70vh] flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-8 shadow-md text-center space-y-5">
        <div className="w-16 h-16 rounded-full bg-amber-50 dark:bg-amber-950 text-amber-600 mx-auto flex items-center justify-center border border-amber-200 dark:border-amber-800">
          <ShieldAlert className="w-8 h-8" />
        </div>

        <div className="space-y-2">
          <span className="text-[11px] font-mono font-bold uppercase tracking-wider text-amber-600 bg-amber-50 dark:bg-amber-950 px-2 py-0.5 rounded border border-amber-200 dark:border-amber-800">
            403 • Restricted Access
          </span>
          <h1 className="text-lg font-bold text-slate-900 dark:text-slate-100">
            Administrative Role Clearance Required
          </h1>
          <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
            Your current account role is{' '}
            <strong className="text-slate-900 dark:text-slate-100 font-mono">
              {user?.role || 'VIEWER'}
            </strong>
            . This module requires elevated regulatory clearance (e.g. <em>ANALYST</em>, <em>REVIEWER</em>, <em>REGULATOR</em>, or <em>ADMIN</em>).
          </p>
        </div>

        <div className="p-3 bg-slate-50 dark:bg-slate-800/60 rounded-lg text-left text-xs text-slate-500 space-y-1.5 border border-slate-200 dark:border-slate-700/60">
          <div className="font-semibold text-slate-700 dark:text-slate-300">How to request access:</div>
          <p className="text-[11px] leading-relaxed">
            Please contact an authorized system administrator with your official email (<strong>{user?.email}</strong>) to request role elevation for SIH26006 freight forecasting or Legal Metrology rule enforcement.
          </p>
        </div>

        <div className="flex items-center justify-center gap-3 pt-2">
          <Link
            to="/"
            className="inline-flex items-center gap-1.5 px-4 py-2 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-200 text-xs font-semibold rounded-lg transition"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Dashboard</span>
          </Link>
          <Link
            to="/profile"
            className="inline-flex items-center gap-1.5 px-4 py-2 bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold rounded-lg transition"
          >
            <UserCheck className="w-3.5 h-3.5" />
            <span>My Profile</span>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default UnauthorizedPage;
