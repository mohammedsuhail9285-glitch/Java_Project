import React, { useState, useEffect } from 'react';
import api from '../api/client';
import { PageHeader } from '../components/layout/PageHeader';
import { History, RefreshCw, Shield } from 'lucide-react';

interface AuditLogItem {
  id: number;
  userId?: number;
  username: string;
  action: string;
  entity: string;
  entityId: string;
  ipAddress: string;
  newValue?: string;
  timestamp: string;
}

export const AuditLogPage: React.FC = () => {
  const [logs, setLogs] = useState<AuditLogItem[]>([]);
  const [loading, setLoading] = useState(true);

  const loadLogs = async () => {
    try {
      setLoading(true);
      const res = await api.get('/audit-logs');
      if (Array.isArray(res.data)) {
        setLogs(res.data);
      } else if (res.data?.content) {
        setLogs(res.data.content);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadLogs();
  }, []);

  return (
    <div className="space-y-5 pb-16">
      <PageHeader
        title="Immutable Regulatory Audit Trail"
        description="Cryptographically recorded log of regulator enforcement actions, review determinations, and statutory configuration modifications."
        breadcrumbs={[{ label: 'Administration' }, { label: 'Audit Log' }]}
        action={
          <button
            onClick={loadLogs}
            disabled={loading}
            className="px-3 py-1.5 border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 rounded-md text-slate-700 dark:text-slate-300 hover:bg-slate-50 text-xs font-semibold flex items-center gap-1.5 transition"
            title="Refresh logs"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin text-blue-800' : ''}`} />
            <span>Refresh</span>
          </button>
        }
      />

      <div className="bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 overflow-hidden">
        {loading && logs.length === 0 ? (
          <div className="p-12 text-center text-xs text-slate-500 flex items-center justify-center gap-2">
            <RefreshCw className="w-4 h-4 animate-spin text-blue-800" />
            <span>Loading audit events...</span>
          </div>
        ) : logs.length === 0 ? (
          <div className="p-12 text-center text-xs text-slate-500">
            No audit log entries recorded.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-600 uppercase tracking-wider font-semibold border-b border-slate-200 dark:border-slate-800">
                <tr>
                  <th className="py-2.5 px-3">Timestamp</th>
                  <th className="py-2.5 px-3">Officer / User</th>
                  <th className="py-2.5 px-3">Action</th>
                  <th className="py-2.5 px-3">Target Entity</th>
                  <th className="py-2.5 px-3">Regulatory Notes / Rationale</th>
                  <th className="py-2.5 px-3">IP Address</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {logs.map((log) => (
                  <tr key={log.id} className="hover:bg-slate-50/70 dark:hover:bg-slate-800/40 transition">
                    <td className="py-2.5 px-3 font-mono text-slate-500 whitespace-nowrap">
                      {log.timestamp ? log.timestamp.replace('T', ' ').substring(0, 19) : '-'}
                    </td>

                    <td className="py-2.5 px-3 font-semibold text-slate-900 dark:text-slate-100">
                      {log.username}
                    </td>

                    <td className="py-2.5 px-3">
                      <span className="font-mono text-[10px] font-bold px-1.5 py-0.5 rounded bg-blue-50 text-blue-900 border border-blue-200">
                        {log.action}
                      </span>
                    </td>

                    <td className="py-2.5 px-3 font-mono text-[11px] text-slate-600 dark:text-slate-400">
                      {log.entity}:{log.entityId}
                    </td>

                    <td className="py-2.5 px-3 text-slate-700 dark:text-slate-300 max-w-md truncate">
                      {log.newValue || '-'}
                    </td>

                    <td className="py-2.5 px-3 font-mono text-slate-400 text-[11px]">
                      {log.ipAddress || '127.0.0.1'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
