import React from 'react';
import { ChevronRight } from 'lucide-react';
import { Link } from 'react-router-dom';

interface BreadcrumbItem {
  label: string;
  to?: string;
}

interface PageHeaderProps {
  title: string;
  description?: string;
  icon?: React.ComponentType<{ className?: string }>;
  breadcrumbs?: BreadcrumbItem[];
  badge?: React.ReactNode;
  actions?: React.ReactNode;
  action?: React.ReactNode;
}

export const PageHeader: React.FC<PageHeaderProps> = ({
  title,
  description,
  icon: Icon,
  breadcrumbs,
  badge,
  actions,
  action,
}) => {
  const actionContent = action || actions;
  return (
    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-slate-200/80 dark:border-slate-800/80">
      <div className="space-y-1">
        {breadcrumbs && breadcrumbs.length > 0 && (
          <nav className="flex items-center gap-1.5 text-[11px] text-slate-500 font-medium">
            <Link to="/" className="hover:text-blue-900 dark:hover:text-blue-400 transition">
              Home
            </Link>
            {breadcrumbs.map((crumb, idx) => (
              <React.Fragment key={idx}>
                <ChevronRight className="w-3 h-3 text-slate-400" />
                {crumb.to ? (
                  <Link to={crumb.to} className="hover:text-blue-900 dark:hover:text-blue-400 transition">
                    {crumb.label}
                  </Link>
                ) : (
                  <span className="text-slate-800 dark:text-slate-200 font-semibold">{crumb.label}</span>
                )}
              </React.Fragment>
            ))}
          </nav>
        )}

        <div className="flex items-center gap-2.5">
          {Icon && (
            <div className="w-7 h-7 rounded-lg bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-900/60 text-blue-900 dark:text-blue-400 flex items-center justify-center shrink-0">
              <Icon className="w-4 h-4" />
            </div>
          )}
          <h1 className="text-lg font-bold tracking-tight text-slate-900 dark:text-slate-100 flex items-center gap-2">
            {title}
            {badge}
          </h1>
        </div>

        {description && (
          <p className="text-xs text-slate-500 dark:text-slate-400 leading-relaxed max-w-3xl">
            {description}
          </p>
        )}
      </div>

      {actionContent && <div className="flex items-center gap-2 shrink-0">{actionContent}</div>}
    </div>
  );
};
