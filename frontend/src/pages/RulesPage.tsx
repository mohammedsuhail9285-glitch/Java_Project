import React, { useState, useEffect } from 'react';
import { ruleApi } from '../api/ruleApi';
import { Rule } from '../types';
import { SeverityBadge } from '../components/common/SeverityBadge';
import { useAuth } from '../context/AuthContext';
import { PageHeader } from '../components/layout/PageHeader';
import {
  Sliders,
  Play,
  Edit,
  CheckCircle,
  AlertTriangle,
  RefreshCw,
  Plus,
  Shield,
  Layers,
} from 'lucide-react';

export const RulesPage: React.FC = () => {
  const { hasRole } = useAuth();
  const [rules, setRules] = useState<Rule[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'CATALOG' | 'SIMULATOR'>('CATALOG');

  // Edit Modal State
  const [editingRule, setEditingRule] = useState<Rule | null>(null);
  const [savingRule, setSavingRule] = useState(false);

  // Simulator State
  const [simRuleId, setSimRuleId] = useState('LM001');
  const [simSampleSize, setSimSampleSize] = useState(10);
  const [simResults, setSimResults] = useState<any | null>(null);
  const [runningSim, setRunningSim] = useState(false);

  const loadRules = async () => {
    try {
      setLoading(true);
      const res = await ruleApi.getRules();
      setRules(res);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRules();
  }, []);

  const handleToggleRule = async (rule: Rule) => {
    if (!hasRole('ADMIN', 'REGULATOR')) return;
    try {
      const updated = await ruleApi.updateRule(rule.id, { enabled: !rule.enabled });
      setRules(rules.map((r) => (r.id === updated.id ? updated : r)));
    } catch (err) {
      console.error(err);
    }
  };

  const handleSaveEdit = async () => {
    if (!editingRule) return;
    try {
      setSavingRule(true);
      const updated = await ruleApi.updateRule(editingRule.id, editingRule);
      setRules(rules.map((r) => (r.id === updated.id ? updated : r)));
      setEditingRule(null);
    } catch (err) {
      console.error(err);
    } finally {
      setSavingRule(false);
    }
  };

  const handleRunSimulator = async () => {
    const selectedRule = rules.find((r) => r.ruleId === simRuleId);
    if (!selectedRule) return;

    try {
      setRunningSim(true);
      const res = await ruleApi.simulateRule(selectedRule, undefined, simSampleSize);
      setSimResults(res);
    } catch (err) {
      console.error(err);
    } finally {
      setRunningSim(false);
    }
  };

  return (
    <div className="space-y-5 pb-16">
      <PageHeader
        title="Legal Metrology Rule Engine & Simulator"
        description="Statutory rule definitions under the Legal Metrology (Packaged Commodities) Rules, 2011 and Consumer Protection E-Commerce Rules."
        breadcrumbs={[{ label: 'Administration' }, { label: 'Rules' }]}
        action={
          <div className="flex items-center gap-2">
            <div className="flex items-center border border-slate-200 dark:border-slate-800 rounded-md p-0.5 bg-slate-50 dark:bg-slate-900">
              <button
                onClick={() => setActiveTab('CATALOG')}
                className={`px-3 py-1.5 rounded text-xs font-semibold transition ${
                  activeTab === 'CATALOG'
                    ? 'bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 shadow-2xs'
                    : 'text-slate-600 hover:text-slate-900 dark:text-slate-400'
                }`}
              >
                Statutory Rules ({rules.length})
              </button>
              <button
                onClick={() => setActiveTab('SIMULATOR')}
                className={`px-3 py-1.5 rounded text-xs font-semibold transition flex items-center gap-1.5 ${
                  activeTab === 'SIMULATOR'
                    ? 'bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 shadow-2xs'
                    : 'text-slate-600 hover:text-slate-900 dark:text-slate-400'
                }`}
              >
                <Play className="w-3 h-3 text-blue-800" />
                <span>Dry-Run Simulator</span>
              </button>
            </div>
            <button
              onClick={loadRules}
              disabled={loading}
              className="p-1.5 border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 rounded-md text-slate-600 hover:bg-slate-50 transition"
              title="Refresh Rules"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin text-blue-800' : ''}`} />
            </button>
          </div>
        }
      />

      {/* TAB 1: Rules Catalog */}
      {activeTab === 'CATALOG' && (
        <div className="bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 overflow-hidden">
          {loading ? (
            <div className="p-12 text-center text-xs text-slate-500 flex items-center justify-center gap-2">
              <RefreshCw className="w-4 h-4 animate-spin text-blue-800" />
              <span>Loading statutory rule registry...</span>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-600 uppercase tracking-wider font-semibold border-b border-slate-200 dark:border-slate-800">
                  <tr>
                    <th className="py-2.5 px-3">Rule Code</th>
                    <th className="py-2.5 px-3">Rule Description & Title</th>
                    <th className="py-2.5 px-3">Statutory Citation</th>
                    <th className="py-2.5 px-3">Severity</th>
                    <th className="py-2.5 px-3">Validation Type</th>
                    <th className="py-2.5 px-3">Version</th>
                    <th className="py-2.5 px-3">Status</th>
                    <th className="py-2.5 px-3 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                  {rules.map((r) => (
                    <tr key={r.id} className="hover:bg-slate-50/70 dark:hover:bg-slate-800/40 transition">
                      <td className="py-2.5 px-3 font-mono font-bold text-blue-900 dark:text-blue-400">
                        {r.ruleId}
                      </td>

                      <td className="py-2.5 px-3 max-w-sm">
                        <div className="font-semibold text-slate-900 dark:text-slate-100">
                          {r.ruleName}
                        </div>
                        <div className="text-[11px] text-slate-500 truncate">
                          {r.description}
                        </div>
                      </td>

                      <td className="py-2.5 px-3">
                        <span className="inline-flex px-1.5 py-0.5 rounded bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-mono text-[11px] font-medium border border-slate-200 dark:border-slate-700">
                          {r.source || 'LMPC Rules, 2011'}
                        </span>
                      </td>

                      <td className="py-2.5 px-3">
                        <SeverityBadge severity={r.severity} />
                      </td>

                      <td className="py-2.5 px-3 font-mono text-[11px] text-slate-600 dark:text-slate-400">
                        {r.validationType}
                      </td>

                      <td className="py-2.5 px-3 font-mono text-[11px] text-slate-400">
                        v{r.version}
                      </td>

                      <td className="py-2.5 px-3">
                        <button
                          disabled={!hasRole('ADMIN', 'REGULATOR')}
                          onClick={() => handleToggleRule(r)}
                          className={`px-2 py-0.5 rounded text-[10px] font-bold uppercase transition border ${
                            r.enabled
                              ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
                              : 'bg-slate-100 text-slate-500 border-slate-200'
                          }`}
                        >
                          {r.enabled ? 'Active' : 'Disabled'}
                        </button>
                      </td>

                      <td className="py-2.5 px-3 text-right">
                        {hasRole('ADMIN', 'REGULATOR') && (
                          <button
                            onClick={() => setEditingRule(r)}
                            className="p-1 rounded hover:bg-slate-100 text-slate-600 transition"
                            title="Edit Rule Configuration"
                          >
                            <Edit className="w-3.5 h-3.5" />
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* TAB 2: Rule Simulator */}
      {activeTab === 'SIMULATOR' && (
        <div className="space-y-5">
          <div className="p-4 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 space-y-4">
            <div>
              <h3 className="text-xs font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
                <Play className="w-3.5 h-3.5 text-blue-800" />
                <span>Dry-Run Sandbox Simulation</span>
              </h3>
              <p className="text-[11px] text-slate-500 mt-0.5">
                Simulate proposed statutory rule parameter changes against catalog commodities prior to live activation.
              </p>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Statutory Rule to Test
                </label>
                <select
                  value={simRuleId}
                  onChange={(e) => setSimRuleId(e.target.value)}
                  className="w-full p-2 rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-xs dark:text-slate-100 font-semibold focus:outline-none focus:ring-1 focus:ring-blue-700"
                >
                  {rules.map((r) => (
                    <option key={r.id} value={r.ruleId}>
                      {r.ruleId} — {r.ruleName}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Sample Batch Size
                </label>
                <input
                  type="number"
                  min={1}
                  max={50}
                  value={simSampleSize}
                  onChange={(e) => setSimSampleSize(parseInt(e.target.value) || 10)}
                  className="w-full p-2 rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-xs dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                />
              </div>

              <div className="flex items-end">
                <button
                  onClick={handleRunSimulator}
                  disabled={runningSim}
                  className="w-full py-2 bg-blue-900 hover:bg-blue-800 text-white font-semibold text-xs rounded-md shadow-2xs flex items-center justify-center gap-2 transition disabled:opacity-50"
                >
                  <Play className="w-3.5 h-3.5" />
                  <span>{runningSim ? 'Evaluating Sample...' : 'Run Simulation'}</span>
                </button>
              </div>
            </div>
          </div>

          {/* Simulation Output */}
          {simResults && (
            <div className="bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 p-4 space-y-3">
              <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-2.5">
                <div>
                  <h4 className="text-xs font-bold text-slate-900 dark:text-slate-100">
                    Simulation Results: {simResults.ruleTested}
                  </h4>
                  <p className="text-[11px] text-slate-500">
                    Evaluated across {simResults.testedCount} catalog commodities.
                  </p>
                </div>
                <div>
                  <span className="text-xs font-bold text-rose-800 bg-rose-50 px-2 py-0.5 rounded border border-rose-200 font-mono">
                    Flagged: {simResults.flaggedCount} ({simResults.flaggedRatePct}%)
                  </span>
                </div>
              </div>

              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-600 uppercase tracking-wider font-semibold">
                    <tr>
                      <th className="py-2 px-3">SKU</th>
                      <th className="py-2 px-3">Commodity Name</th>
                      <th className="py-2 px-3">Outcome</th>
                      <th className="py-2 px-3">Detected Value / Label Marking</th>
                      <th className="py-2 px-3">Remediation Guidance</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                    {simResults.outcomes.map((o: any) => (
                      <tr key={o.productId} className="hover:bg-slate-50/70 dark:hover:bg-slate-800/40">
                        <td className="py-2 px-3 font-mono text-slate-500">PRD-{o.productId}</td>
                        <td className="py-2 px-3 font-semibold text-slate-900 dark:text-slate-100">{o.productName}</td>
                        <td className="py-2 px-3">
                          {o.flaggedPotentialViolation ? (
                            <span className="font-bold text-rose-700 dark:text-rose-400">Violation Flagged</span>
                          ) : (
                            <span className="font-bold text-emerald-700 dark:text-emerald-400">Passed</span>
                          )}
                        </td>
                        <td className="py-2 px-3 font-mono text-slate-700 dark:text-slate-300">{o.detectedValue}</td>
                        <td className="py-2 px-3 text-slate-500">{o.remediationMessage}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      )}

      {/* Edit Rule Modal */}
      {editingRule && (
        <div className="fixed inset-0 bg-slate-950/60 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <div className="w-full max-w-lg bg-white dark:bg-slate-900 rounded-lg shadow-xl border border-slate-200 dark:border-slate-800 p-5 space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="text-sm font-bold text-slate-900 dark:text-slate-100">
                Edit Statutory Rule: {editingRule.ruleId}
              </h3>
              <button onClick={() => setEditingRule(null)} className="text-slate-400 hover:text-slate-600">✕</button>
            </div>

            <div className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Rule Name
                </label>
                <input
                  type="text"
                  value={editingRule.ruleName}
                  onChange={(e) => setEditingRule({ ...editingRule, ruleName: e.target.value })}
                  className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Statutory Legal Reference
                </label>
                <input
                  type="text"
                  value={editingRule.source || ''}
                  onChange={(e) => setEditingRule({ ...editingRule, source: e.target.value })}
                  className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Severity
                </label>
                <select
                  value={editingRule.severity}
                  onChange={(e) => setEditingRule({ ...editingRule, severity: e.target.value as any })}
                  className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 font-semibold focus:outline-none focus:ring-1 focus:ring-blue-700"
                >
                  <option value="CRITICAL">CRITICAL</option>
                  <option value="MAJOR">MAJOR</option>
                  <option value="MINOR">MINOR</option>
                  <option value="INFO">INFO</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Remediation Guidance Message
                </label>
                <textarea
                  rows={3}
                  value={editingRule.remediationMessage || ''}
                  onChange={(e) => setEditingRule({ ...editingRule, remediationMessage: e.target.value })}
                  className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                />
              </div>
            </div>

            <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-100 dark:border-slate-800">
              <button
                type="button"
                onClick={() => setEditingRule(null)}
                className="px-3 py-1.5 rounded-md bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold"
              >
                Cancel
              </button>
              <button
                type="button"
                disabled={savingRule}
                onClick={handleSaveEdit}
                className="px-3.5 py-1.5 rounded-md bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold shadow-2xs transition disabled:opacity-50"
              >
                {savingRule ? 'Saving...' : 'Save & Bump Version'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
