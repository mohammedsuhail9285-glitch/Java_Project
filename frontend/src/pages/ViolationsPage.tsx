import React, { useState, useEffect, useMemo } from 'react';
import { violationApi } from '../api/violationApi';
import { reportApi } from '../api/reportApi';
import { Violation } from '../types';
import { SeverityBadge } from '../components/common/SeverityBadge';
import { StatusBadge } from '../components/common/StatusBadge';
import { RuleExplanationModal } from '../components/common/RuleExplanationModal';
import { PageHeader } from '../components/layout/PageHeader';
import {
  AlertOctagon,
  Filter,
  CheckCircle,
  XCircle,
  FileSpreadsheet,
  RefreshCw,
  Eye,
  ShieldAlert,
  Sparkles,
  HelpCircle,
  CheckSquare,
  Square,
  MinusSquare,
  LayoutGrid,
  List,
  FileText,
  ExternalLink,
  ChevronRight,
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export const ViolationsPage: React.FC = () => {
  const [violations, setViolations] = useState<Violation[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [severityFilter, setSeverityFilter] = useState('ALL');
  const [viewMode, setViewMode] = useState<'TABLE' | 'CARDS'>('TABLE');
  const [exporting, setExporting] = useState(false);
  const navigate = useNavigate();

  // Smart Review Queue state
  const [smartPriority, setSmartPriority] = useState<boolean>(true);

  // Bulk Actions state
  const [selectedIds, setSelectedIds] = useState<Set<number>>(new Set());
  const [bulkAction, setBulkAction] = useState<'CONFIRM' | 'DISMISS' | null>(null);
  const [bulkComment, setBulkComment] = useState('');
  const [bulkSubmitting, setBulkSubmitting] = useState(false);
  const [bulkProgress, setBulkProgress] = useState<{ current: number; total: number } | null>(null);

  // Single Review modal state
  const [activeReview, setActiveReview] = useState<Violation | null>(null);
  const [reviewAction, setReviewAction] = useState<'CONFIRM' | 'DISMISS'>('CONFIRM');
  const [reviewComment, setReviewComment] = useState('');
  const [submitting, setSubmitting] = useState(false);

  // Rule explanation modal state
  const [explainingRuleId, setExplainingRuleId] = useState<string | null>(null);

  const loadViolations = async () => {
    try {
      setLoading(true);
      const res = await violationApi.getViolations({
        status: statusFilter !== 'ALL' ? statusFilter : undefined,
        severity: severityFilter !== 'ALL' ? severityFilter : undefined,
        size: 100,
      });
      setViolations(res.content);
      setSelectedIds(new Set());
    } catch (err) {
      console.error('Failed to load violations:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadViolations();
  }, [statusFilter, severityFilter]);

  // Compute priority score based on severity, confidence, and recency
  const computePriority = (v: Violation) => {
    const sevWeight = v.severity === 'CRITICAL' ? 3.0 : v.severity === 'MAJOR' ? 2.0 : 1.0;
    const rawConf = v.detectionConfidence ?? v.ocrConfidence ?? 85.0;
    const confNorm = rawConf > 1.0 ? rawConf / 100.0 : rawConf;
    const ambiguity = Math.max(0.1, 1.0 - confNorm);
    const ageHours = (Date.now() - new Date(v.timestamp).getTime()) / (1000 * 3600);
    const recencyBonus = 1.0 / (1.0 + Math.max(0, ageHours) / 72.0);
    const score = sevWeight * 1.8 + ambiguity * 2.5 + recencyBonus * 0.8;
    return Math.round(score * 10) / 10;
  };

  const displayedViolations = useMemo(() => {
    if (!smartPriority) return violations;
    return [...violations].sort((a, b) => computePriority(b) - computePriority(a));
  }, [violations, smartPriority]);

  // Bulk Selection Helpers
  const handleToggleSelectAll = () => {
    if (selectedIds.size === displayedViolations.length) {
      setSelectedIds(new Set());
    } else {
      setSelectedIds(new Set(displayedViolations.map((v) => v.id)));
    }
  };

  const handleToggleSelectRow = (id: number) => {
    const next = new Set(selectedIds);
    if (next.has(id)) {
      next.delete(id);
    } else {
      next.add(id);
    }
    setSelectedIds(next);
  };

  const handleOpenBulkModal = (action: 'CONFIRM' | 'DISMISS') => {
    setBulkAction(action);
    setBulkComment(
      action === 'CONFIRM'
        ? `Bulk statutory determination confirmed across ${selectedIds.size} listings following discrepancy analysis.`
        : `Bulk dismissal: Declarations corroborated manually; automated flags marked non-actionable.`
    );
  };

  const handleExecuteBulkReview = async () => {
    if (!bulkAction || selectedIds.size === 0) return;
    try {
      setBulkSubmitting(true);
      const ids = Array.from(selectedIds);
      let count = 0;
      for (const id of ids) {
        setBulkProgress({ current: count + 1, total: ids.length });
        await violationApi.submitReview(id, bulkAction, bulkComment);
        count++;
      }
      setBulkAction(null);
      setSelectedIds(new Set());
      await loadViolations();
    } catch (err) {
      console.error('Bulk review execution error:', err);
    } finally {
      setBulkSubmitting(false);
      setBulkProgress(null);
    }
  };

  const handleOpenReview = (v: Violation, action: 'CONFIRM' | 'DISMISS') => {
    setActiveReview(v);
    setReviewAction(action);
    setReviewComment(
      action === 'CONFIRM'
        ? 'Verified physical package label does not bear compliant declaration as required by LMPC Rule 6.'
        : 'Declaration verified manually on auxiliary panel; automated flag dismissed as non-actionable.'
    );
  };

  const handleSubmitReview = async () => {
    if (!activeReview) return;
    try {
      setSubmitting(true);
      await violationApi.submitReview(activeReview.id, reviewAction, reviewComment);
      setActiveReview(null);
      await loadViolations();
    } catch (err) {
      console.error(err);
    } finally {
      setSubmitting(false);
    }
  };

  const handleExportCsv = async () => {
    try {
      setExporting(true);
      await reportApi.downloadCsv();
    } catch (err) {
      console.error(err);
    } finally {
      setExporting(false);
    }
  };

  return (
    <div className="space-y-5 pb-16">
      <PageHeader
        title="Statutory Violations & Discrepancies"
        description="Formal inspection review queue requiring human regulator determination or dismissal under Legal Metrology Rules."
        breadcrumbs={[{ label: 'Compliance' }, { label: 'Violations' }]}
        action={
          <div className="flex items-center gap-2">
            {/* View Mode Toggle */}
            <div className="flex items-center border border-slate-200 dark:border-slate-800 rounded-md p-0.5 bg-slate-50 dark:bg-slate-900">
              <button
                type="button"
                onClick={() => setViewMode('TABLE')}
                className={`p-1.5 rounded text-xs font-semibold flex items-center gap-1 transition ${
                  viewMode === 'TABLE'
                    ? 'bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 shadow-2xs'
                    : 'text-slate-500 hover:text-slate-800 dark:hover:text-slate-200'
                }`}
                title="Table View"
              >
                <List className="w-3.5 h-3.5" />
                <span className="hidden sm:inline">Table</span>
              </button>
              <button
                type="button"
                onClick={() => setViewMode('CARDS')}
                className={`p-1.5 rounded text-xs font-semibold flex items-center gap-1 transition ${
                  viewMode === 'CARDS'
                    ? 'bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 shadow-2xs'
                    : 'text-slate-500 hover:text-slate-800 dark:hover:text-slate-200'
                }`}
                title="Inspection Findings Card View"
              >
                <LayoutGrid className="w-3.5 h-3.5" />
                <span className="hidden sm:inline">Findings</span>
              </button>
            </div>

            {/* Smart Priority Toggle */}
            <button
              onClick={() => setSmartPriority(!smartPriority)}
              className={`px-2.5 py-1.5 rounded-md text-xs font-semibold flex items-center gap-1.5 border transition ${
                smartPriority
                  ? 'bg-blue-50 text-blue-900 border-blue-200 dark:bg-blue-950/60 dark:text-blue-300 dark:border-blue-800'
                  : 'bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 text-slate-600 dark:text-slate-300'
              }`}
              title="Rank findings by severity and confidence"
            >
              <Sparkles className={`w-3.5 h-3.5 ${smartPriority ? 'text-blue-700 dark:text-blue-400' : 'text-slate-400'}`} />
              <span>{smartPriority ? 'Priority Sort' : 'Default Sort'}</span>
            </button>

            {/* Export CSV */}
            <button
              onClick={handleExportCsv}
              disabled={exporting}
              className="px-3 py-1.5 rounded-md bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 hover:bg-slate-50 text-slate-700 dark:text-slate-200 text-xs font-semibold flex items-center gap-1.5 transition disabled:opacity-50"
            >
              <FileSpreadsheet className="w-3.5 h-3.5 text-blue-700" />
              <span>{exporting ? 'Exporting...' : 'Export CSV'}</span>
            </button>
          </div>
        }
      />

      {/* Filter Toolbar */}
      <div className="p-3 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 flex flex-wrap items-center justify-between gap-3 text-xs">
        <div className="flex items-center gap-3 flex-wrap">
          <div className="flex items-center gap-1.5 font-semibold text-slate-500">
            <Filter className="w-3.5 h-3.5" />
            <span>Filter:</span>
          </div>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-2.5 py-1.5 rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 font-medium text-slate-700 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-blue-700"
          >
            <option value="ALL">All Statuses</option>
            <option value="NEW">New Findings</option>
            <option value="UNDER_REVIEW">Under Review</option>
            <option value="CONFIRMED">Confirmed Violations</option>
            <option value="DISMISSED">Dismissed / False Positives</option>
            <option value="RESOLVED">Resolved</option>
          </select>

          <select
            value={severityFilter}
            onChange={(e) => setSeverityFilter(e.target.value)}
            className="px-2.5 py-1.5 rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 font-medium text-slate-700 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-blue-700"
          >
            <option value="ALL">All Severities</option>
            <option value="CRITICAL">Critical</option>
            <option value="MAJOR">Major</option>
            <option value="MINOR">Minor</option>
          </select>

          <span className="text-slate-400">|</span>

          <span className="font-mono text-slate-500">
            Showing <strong>{displayedViolations.length}</strong> findings
          </span>
        </div>

        <button
          onClick={loadViolations}
          title="Refresh Queue"
          className="p-1.5 border border-slate-200 dark:border-slate-800 rounded-md hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300 transition"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin text-blue-700' : ''}`} />
        </button>
      </div>

      {/* Main Content Area */}
      {loading ? (
        <div className="p-12 text-center text-xs text-slate-500 flex items-center justify-center gap-2 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800">
          <RefreshCw className="w-4 h-4 animate-spin text-blue-800" />
          <span>Loading regulatory inspection queue...</span>
        </div>
      ) : displayedViolations.length === 0 ? (
        <div className="p-12 text-center text-xs text-slate-500 bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 space-y-2">
          <div className="font-semibold text-slate-700 dark:text-slate-300">No Inspection Findings</div>
          <p>No statutory violations match the selected filter criteria.</p>
        </div>
      ) : viewMode === 'TABLE' ? (
        /* TABLE VIEW */
        <div className="bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-600 uppercase tracking-wider font-semibold border-b border-slate-200 dark:border-slate-800">
                <tr>
                  <th className="py-2.5 px-3 w-8">
                    <button
                      type="button"
                      onClick={handleToggleSelectAll}
                      className="text-slate-400 hover:text-slate-600 flex items-center"
                      title={selectedIds.size === displayedViolations.length ? 'Deselect All' : 'Select All'}
                    >
                      {selectedIds.size === 0 ? (
                        <Square className="w-4 h-4" />
                      ) : selectedIds.size === displayedViolations.length ? (
                        <CheckSquare className="w-4 h-4 text-blue-800" />
                      ) : (
                        <MinusSquare className="w-4 h-4 text-blue-800" />
                      )}
                    </button>
                  </th>
                  <th className="py-2.5 px-3">Ref ID</th>
                  {smartPriority && <th className="py-2.5 px-3 text-center">Priority</th>}
                  <th className="py-2.5 px-3">Commodity / Product</th>
                  <th className="py-2.5 px-3">Statutory Rule</th>
                  <th className="py-2.5 px-3">Severity</th>
                  <th className="py-2.5 px-3">Observed Discrepancy</th>
                  <th className="py-2.5 px-3">Status</th>
                  <th className="py-2.5 px-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {displayedViolations.map((v) => {
                  const isSelected = selectedIds.has(v.id);
                  const priorityScore = computePriority(v);

                  return (
                    <tr
                      key={v.id}
                      className={`hover:bg-slate-50/70 dark:hover:bg-slate-800/40 transition ${
                        isSelected ? 'bg-blue-50/40 dark:bg-blue-950/20' : ''
                      }`}
                    >
                      <td className="py-2.5 px-3">
                        <input
                          type="checkbox"
                          checked={isSelected}
                          onChange={() => handleToggleSelectRow(v.id)}
                          className="rounded border-slate-300 text-blue-800 focus:ring-blue-700 cursor-pointer"
                        />
                      </td>

                      <td className="py-2.5 px-3 font-mono font-medium text-slate-500">
                        {v.violationId}
                      </td>

                      {smartPriority && (
                        <td className="py-2.5 px-3 text-center">
                          <span
                            className="inline-flex items-center px-1.5 py-0.5 rounded text-[10px] font-mono font-bold bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 border border-slate-200 dark:border-slate-700"
                            title={`Priority Score: ${priorityScore}`}
                          >
                            {priorityScore}
                          </span>
                        </td>
                      )}

                      <td className="py-2.5 px-3">
                        <button
                          onClick={() => navigate(`/products/${v.productId}`)}
                          className="font-medium text-blue-900 dark:text-blue-400 hover:underline text-left max-w-xs truncate block"
                        >
                          {v.productName || `Product #${v.productId}`}
                        </button>
                      </td>

                      <td className="py-2.5 px-3">
                        <button
                          type="button"
                          onClick={() => setExplainingRuleId(v.ruleId)}
                          className="text-left group cursor-pointer"
                          title="View statutory rule definition"
                        >
                          <div className="font-mono font-semibold text-blue-900 dark:text-blue-400 group-hover:underline flex items-center gap-1">
                            <span>{v.ruleId}</span>
                            <HelpCircle className="w-3 h-3 text-slate-400 group-hover:text-blue-800" />
                          </div>
                          <div className="text-[10px] text-slate-400 truncate max-w-[160px]">
                            {v.ruleName}
                          </div>
                        </button>
                      </td>

                      <td className="py-2.5 px-3">
                        <SeverityBadge severity={v.severity} />
                      </td>

                      <td className="py-2.5 px-3 max-w-xs">
                        <div className="font-mono text-slate-800 dark:text-slate-200 truncate">
                          {v.detectedValue}
                        </div>
                        <div className="text-[10px] text-slate-400 truncate">
                          Req: {v.expectedCondition}
                        </div>
                      </td>

                      <td className="py-2.5 px-3">
                        <StatusBadge status={v.status} size="sm" />
                      </td>

                      <td className="py-2.5 px-3 text-right">
                        {v.status === 'NEW' || v.status === 'UNDER_REVIEW' ? (
                          <div className="inline-flex items-center gap-1">
                            <button
                              onClick={() => handleOpenReview(v, 'CONFIRM')}
                              className="px-2 py-1 rounded bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200 font-medium text-[11px] transition"
                              title="Confirm Violation"
                            >
                              Confirm
                            </button>
                            <button
                              onClick={() => handleOpenReview(v, 'DISMISS')}
                              className="px-2 py-1 rounded bg-slate-100 hover:bg-slate-200 text-slate-700 border border-slate-200 font-medium text-[11px] transition"
                              title="Dismiss Finding"
                            >
                              Dismiss
                            </button>
                            <button
                              onClick={() => navigate(`/products/${v.productId}`)}
                              className="p-1 rounded text-slate-500 hover:text-blue-800 hover:bg-slate-100 transition"
                              title="Deep Evidence Inspection"
                            >
                              <Eye className="w-3.5 h-3.5" />
                            </button>
                          </div>
                        ) : (
                          <button
                            onClick={() => navigate(`/products/${v.productId}`)}
                            className="px-2.5 py-1 rounded bg-slate-100 hover:bg-slate-200 text-slate-700 font-medium text-[11px]"
                          >
                            Details
                          </button>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        /* INSPECTION FINDINGS CARDS VIEW (Prompt Spec) */
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {displayedViolations.map((v) => (
            <div
              key={v.id}
              className="bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 p-4 flex flex-col justify-between space-y-3.5 hover:border-slate-300 transition"
            >
              <div className="space-y-2.5">
                {/* Header: Severity + Rule ID */}
                <div className="flex items-center justify-between gap-2 border-b border-slate-100 dark:border-slate-800 pb-2">
                  <div className="flex items-center gap-2">
                    <SeverityBadge severity={v.severity} />
                    <button
                      type="button"
                      onClick={() => setExplainingRuleId(v.ruleId)}
                      className="font-mono text-xs font-bold text-blue-900 dark:text-blue-400 hover:underline flex items-center gap-1"
                    >
                      <span>{v.ruleId}</span>
                      <HelpCircle className="w-3 h-3 text-slate-400 inline" />
                    </button>
                  </div>
                  <span className="font-mono text-[10px] text-slate-400">
                    {v.violationId}
                  </span>
                </div>

                {/* Issue Description */}
                <div>
                  <h4 className="text-xs font-bold text-slate-900 dark:text-slate-100">
                    {v.ruleName || 'Statutory Requirement Issue'}
                  </h4>
                  <div className="text-[11px] text-slate-500 truncate">
                    Product: {v.productName || `Product #${v.productId}`}
                  </div>
                </div>

                {/* Detected vs Expected */}
                <div className="p-2.5 bg-slate-50 dark:bg-slate-800/50 rounded-md border border-slate-200/80 dark:border-slate-800 text-xs space-y-1.5">
                  <div>
                    <span className="text-slate-500 font-medium">Detected value: </span>
                    <span className="font-mono font-semibold text-slate-900 dark:text-slate-100">
                      {v.detectedValue || 'Not Disclosed'}
                    </span>
                  </div>
                  <div>
                    <span className="text-slate-500 font-medium">Expected: </span>
                    <span className="text-slate-700 dark:text-slate-300">
                      {v.expectedCondition || 'Configured Legal Metrology Requirement'}
                    </span>
                  </div>
                </div>

                {/* Evidence Status */}
                <div className="flex items-center justify-between text-[11px] text-slate-500 pt-0.5">
                  <span className="flex items-center gap-1">
                    <span className="w-1.5 h-1.5 rounded-full bg-blue-600" />
                    <span>Evidence available (OCR: {v.ocrConfidence ?? 94}%)</span>
                  </span>
                  <StatusBadge status={v.status} size="sm" />
                </div>
              </div>

              {/* Action Buttons */}
              <div className="pt-2 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between gap-2">
                <button
                  type="button"
                  onClick={() => navigate(`/products/${v.productId}`)}
                  className="px-2.5 py-1.5 rounded-md border border-slate-200 dark:border-slate-700 hover:bg-slate-50 text-slate-700 dark:text-slate-200 text-xs font-semibold flex items-center gap-1 transition"
                >
                  <Eye className="w-3.5 h-3.5 text-slate-500" />
                  <span>View Evidence</span>
                </button>

                {v.status === 'NEW' || v.status === 'UNDER_REVIEW' ? (
                  <div className="flex items-center gap-1.5">
                    <button
                      type="button"
                      onClick={() => handleOpenReview(v, 'CONFIRM')}
                      className="px-2.5 py-1.5 rounded-md bg-rose-50 hover:bg-rose-100 text-rose-800 border border-rose-200 text-xs font-semibold transition"
                    >
                      Confirm
                    </button>
                    <button
                      type="button"
                      onClick={() => handleOpenReview(v, 'DISMISS')}
                      className="px-2.5 py-1.5 rounded-md bg-slate-100 hover:bg-slate-200 text-slate-700 border border-slate-200 text-xs font-semibold transition"
                    >
                      Dismiss
                    </button>
                  </div>
                ) : (
                  <span className="text-[11px] font-medium text-slate-400">
                    Determination Recorded
                  </span>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Floating Sticky Bulk Actions Bar */}
      {selectedIds.size > 0 && (
        <div className="fixed bottom-6 left-1/2 -translate-x-1/2 z-40 bg-slate-900 text-white px-5 py-2.5 rounded-lg shadow-xl border border-slate-700 flex items-center gap-4 animate-in fade-in slide-in-from-bottom-2">
          <div className="flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-blue-500" />
            <span className="text-xs font-mono font-semibold">
              {selectedIds.size} findings selected
            </span>
          </div>

          <div className="h-4 w-px bg-slate-700" />

          <div className="flex items-center gap-2">
            <button
              type="button"
              onClick={() => handleOpenBulkModal('CONFIRM')}
              className="px-3 py-1.5 rounded-md bg-rose-600 hover:bg-rose-500 text-white text-xs font-semibold shadow-2xs transition flex items-center gap-1.5"
            >
              <CheckCircle className="w-3.5 h-3.5" />
              <span>Bulk Confirm</span>
            </button>

            <button
              type="button"
              onClick={() => handleOpenBulkModal('DISMISS')}
              className="px-3 py-1.5 rounded-md bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold border border-slate-600 transition flex items-center gap-1.5"
            >
              <XCircle className="w-3.5 h-3.5" />
              <span>Bulk Dismiss</span>
            </button>

            <button
              type="button"
              onClick={() => setSelectedIds(new Set())}
              className="px-2 py-1.5 rounded text-slate-400 hover:text-white text-xs font-medium transition"
            >
              Deselect
            </button>
          </div>
        </div>
      )}

      {/* Bulk Review Execution Modal */}
      {bulkAction && (
        <div className="fixed inset-0 bg-slate-950/60 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <div className="w-full max-w-lg bg-white dark:bg-slate-900 rounded-lg shadow-xl border border-slate-200 dark:border-slate-800 p-5 space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="text-sm font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
                <ShieldAlert className="w-4 h-4 text-blue-800" />
                <span>Bulk Statutory Determination ({selectedIds.size} Findings)</span>
              </h3>
              <button
                disabled={bulkSubmitting}
                onClick={() => setBulkAction(null)}
                className="text-slate-400 hover:text-slate-600 disabled:opacity-50"
              >
                ✕
              </button>
            </div>

            <div className="p-3 bg-slate-50 dark:bg-slate-800/60 rounded-md text-xs space-y-1">
              <div>
                <strong>Action: </strong>
                <span
                  className={
                    bulkAction === 'CONFIRM'
                      ? 'text-rose-700 font-bold uppercase'
                      : 'text-slate-700 dark:text-slate-300 font-bold uppercase'
                  }
                >
                  {bulkAction === 'CONFIRM' ? 'Confirm Violations' : 'Dismiss as Non-Actionable'}
                </span>
              </div>
              <div className="text-slate-500">
                Selected entries will be recorded in the regulatory compliance registry and signed under your officer credentials.
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Regulatory Rationale / Official Note
              </label>
              <textarea
                rows={3}
                disabled={bulkSubmitting}
                value={bulkComment}
                onChange={(e) => setBulkComment(e.target.value)}
                className="w-full p-2.5 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700 dark:text-slate-100 disabled:opacity-50"
                placeholder="State unified statutory rationale for the batch action..."
              />
            </div>

            {bulkProgress && (
              <div className="space-y-1.5 pt-1">
                <div className="flex justify-between text-[11px] font-mono text-slate-500">
                  <span>Processing batch reviews...</span>
                  <span>{bulkProgress.current} / {bulkProgress.total}</span>
                </div>
                <div className="w-full h-1.5 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                  <div
                    className="h-full bg-blue-800 transition-all duration-200"
                    style={{ width: `${(bulkProgress.current / bulkProgress.total) * 100}%` }}
                  />
                </div>
              </div>
            )}

            <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-100 dark:border-slate-800">
              <button
                type="button"
                disabled={bulkSubmitting}
                onClick={() => setBulkAction(null)}
                className="px-3 py-1.5 rounded-md bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold transition disabled:opacity-50"
              >
                Cancel
              </button>
              <button
                type="button"
                disabled={bulkSubmitting || !bulkComment.trim()}
                onClick={handleExecuteBulkReview}
                className="px-3.5 py-1.5 rounded-md bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold shadow-2xs transition disabled:opacity-50"
              >
                {bulkSubmitting ? 'Committing...' : `Commit for ${selectedIds.size} Findings`}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Single Review Modal */}
      {activeReview && (
        <div className="fixed inset-0 bg-slate-950/60 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <div className="w-full max-w-lg bg-white dark:bg-slate-900 rounded-lg shadow-xl border border-slate-200 dark:border-slate-800 p-5 space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="text-sm font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
                <ShieldAlert className="w-4 h-4 text-blue-800" />
                <span>Statutory Determination Review</span>
              </h3>
              <button onClick={() => setActiveReview(null)} className="text-slate-400 hover:text-slate-600">
                ✕
              </button>
            </div>

            <div className="text-xs space-y-1.5 text-slate-600 dark:text-slate-300 bg-slate-50 dark:bg-slate-800/50 p-3 rounded-md border border-slate-200/80 dark:border-slate-800">
              <div>
                <strong>Product: </strong> {activeReview.productName}
              </div>
              <div>
                <strong>Rule: </strong> {activeReview.ruleId} ({activeReview.ruleName})
              </div>
              <div>
                <strong>Observed Deviation: </strong>
                <span className="font-mono text-rose-700 dark:text-rose-400 font-semibold">{activeReview.detectedValue}</span>
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                Determination Action
              </label>
              <div className="grid grid-cols-2 gap-2">
                <button
                  type="button"
                  onClick={() => setReviewAction('CONFIRM')}
                  className={`py-2 px-3 rounded-md text-xs font-semibold border transition ${
                    reviewAction === 'CONFIRM'
                      ? 'bg-rose-50 border-rose-300 text-rose-800 dark:bg-rose-950/60 dark:text-rose-300'
                      : 'border-slate-200 dark:border-slate-700 text-slate-600'
                  }`}
                >
                  Confirm Potential Violation
                </button>
                <button
                  type="button"
                  onClick={() => setReviewAction('DISMISS')}
                  className={`py-2 px-3 rounded-md text-xs font-semibold border transition ${
                    reviewAction === 'DISMISS'
                      ? 'bg-slate-100 border-slate-300 text-slate-900 dark:bg-slate-800 dark:text-slate-100'
                      : 'border-slate-200 dark:border-slate-700 text-slate-600'
                  }`}
                >
                  Dismiss / False Positive
                </button>
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Official Rationale / Instructions
              </label>
              <textarea
                rows={3}
                value={reviewComment}
                onChange={(e) => setReviewComment(e.target.value)}
                className="w-full p-2.5 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700 dark:text-slate-100"
                placeholder="State regulatory rationale for the compliance registry..."
              />
            </div>

            <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-100 dark:border-slate-800">
              <button
                type="button"
                onClick={() => setActiveReview(null)}
                className="px-3 py-1.5 rounded-md bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold transition"
              >
                Cancel
              </button>
              <button
                type="button"
                disabled={submitting || !reviewComment.trim()}
                onClick={handleSubmitReview}
                className="px-3.5 py-1.5 rounded-md bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold shadow-2xs transition disabled:opacity-50"
              >
                {submitting ? 'Recording...' : 'Commit Decision'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Rule Explanation Modal */}
      <RuleExplanationModal
        ruleId={explainingRuleId}
        onClose={() => setExplainingRuleId(null)}
      />
    </div>
  );
};
