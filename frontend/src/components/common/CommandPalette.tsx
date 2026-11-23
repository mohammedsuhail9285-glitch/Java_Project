import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Package, AlertTriangle, ShieldCheck, FileText, ArrowRight, CornerDownLeft } from 'lucide-react';
import { productApi } from '../../api/productApi';
import { ruleApi } from '../../api/ruleApi';
import { Product, Rule } from '../../types';

export const CommandPalette: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [query, setQuery] = useState('');
  const [products, setProducts] = useState<Product[]>([]);
  const [rules, setRules] = useState<Rule[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        setIsOpen((prev) => !prev);
      } else if (e.key === 'Escape') {
        setIsOpen(false);
      }
    };
    const handleCustomOpen = () => setIsOpen(true);
    window.addEventListener('keydown', handleKeyDown);
    window.addEventListener('open-command-palette', handleCustomOpen);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      window.removeEventListener('open-command-palette', handleCustomOpen);
    };
  }, []);

  useEffect(() => {
    if (isOpen) {
      productApi.getProducts({ page: 0, size: 20 }).then((res) => {
        setProducts(res.content || []);
      }).catch(() => {});
      ruleApi.getRules().then((res) => {
        setRules(res || []);
      }).catch(() => {});
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const q = query.trim().toLowerCase();

  const filteredProducts = products.filter(
    (p) =>
      p.name?.toLowerCase().includes(q) ||
      String(p.id).includes(q) ||
      p.categoryName?.toLowerCase().includes(q)
  ).slice(0, 5);

  const filteredRules = rules.filter(
    (r) =>
      r.ruleId?.toLowerCase().includes(q) ||
      r.ruleName?.toLowerCase().includes(q)
  ).slice(0, 4);

  const handleSelect = (url: string) => {
    setIsOpen(false);
    setQuery('');
    navigate(url);
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-xs z-50 flex items-start justify-center pt-20 p-4">
      <div className="w-full max-w-xl bg-white dark:bg-slate-900 rounded-2xl shadow-2xl border border-slate-200 dark:border-slate-800 overflow-hidden space-y-3">
        {/* Search Input Bar */}
        <div className="flex items-center gap-3 px-4 py-3 border-b border-slate-100 dark:border-slate-800">
          <Search className="w-5 h-5 text-slate-400 shrink-0" />
          <input
            autoFocus
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Type a product name, violation ID, or rule code (e.g. LM008)..."
            className="w-full bg-transparent text-sm text-slate-800 dark:text-slate-100 placeholder:text-slate-400 focus:outline-none"
          />
          <kbd className="hidden sm:inline-flex items-center gap-0.5 px-2 py-0.5 text-[10px] font-mono text-slate-400 bg-slate-100 dark:bg-slate-800 rounded border border-slate-200 dark:border-slate-700">
            ESC
          </kbd>
        </div>

        {/* Results Container */}
        <div className="max-h-80 overflow-y-auto px-2 pb-2 space-y-3">
          {/* Quick Actions */}
          {!q && (
            <div className="px-2">
              <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block mb-1">
                Quick Navigation
              </span>
              <div className="grid grid-cols-2 gap-1.5 text-xs">
                <button
                  onClick={() => handleSelect('/products')}
                  className="flex items-center gap-2 p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-200 text-left transition"
                >
                  <Package className="w-4 h-4 text-blue-600" />
                  Product Registry
                </button>
                <button
                  onClick={() => handleSelect('/violations')}
                  className="flex items-center gap-2 p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-200 text-left transition"
                >
                  <AlertTriangle className="w-4 h-4 text-rose-600" />
                  Violations Queue
                </button>
                <button
                  onClick={() => handleSelect('/rules')}
                  className="flex items-center gap-2 p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-200 text-left transition"
                >
                  <ShieldCheck className="w-4 h-4 text-amber-600" />
                  Rules Simulator
                </button>
                <button
                  onClick={() => handleSelect('/reports')}
                  className="flex items-center gap-2 p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-200 text-left transition"
                >
                  <FileText className="w-4 h-4 text-emerald-600" />
                  Reports & Dossiers
                </button>
              </div>
            </div>
          )}

          {/* Products Section */}
          {filteredProducts.length > 0 && (
            <div>
              <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider px-2 block mb-1">
                Matching Products
              </span>
              <div className="space-y-0.5">
                {filteredProducts.map((p) => (
                  <button
                    key={p.id}
                    onClick={() => handleSelect(`/products/${p.id}`)}
                    className="w-full flex items-center justify-between p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-left transition text-xs group"
                  >
                    <div className="flex items-center gap-2.5 truncate">
                      <Package className="w-4 h-4 text-blue-600 shrink-0" />
                      <div className="truncate">
                        <span className="font-semibold text-slate-800 dark:text-slate-200 block truncate">
                          {p.name}
                        </span>
                        <span className="text-[10px] text-slate-400">
                          MRP: ₹{p.declaredMrp} | ID: {p.id}
                        </span>
                      </div>
                    </div>
                    <ArrowRight className="w-3.5 h-3.5 text-slate-400 group-hover:translate-x-0.5 transition" />
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Rules Section */}
          {filteredRules.length > 0 && (
            <div>
              <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider px-2 block mb-1">
                Statutory Rules
              </span>
              <div className="space-y-0.5">
                {filteredRules.map((r) => (
                  <button
                    key={r.id}
                    onClick={() => handleSelect('/rules')}
                    className="w-full flex items-center justify-between p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-left transition text-xs group"
                  >
                    <div className="flex items-center gap-2.5 truncate">
                      <span className="font-mono font-bold text-[10px] px-1.5 py-0.5 bg-blue-50 dark:bg-blue-950/60 text-blue-600 rounded">
                        {r.ruleId}
                      </span>
                      <span className="font-medium text-slate-800 dark:text-slate-200 truncate">
                        {r.ruleName}
                      </span>
                    </div>
                    <ArrowRight className="w-3.5 h-3.5 text-slate-400 group-hover:translate-x-0.5 transition" />
                  </button>
                ))}
              </div>
            </div>
          )}

          {q && filteredProducts.length === 0 && filteredRules.length === 0 && (
            <div className="p-4 text-center text-xs text-slate-400">
              No matching products or rules found for "{query}".
            </div>
          )}
        </div>

        {/* Footer Hint */}
        <div className="px-4 py-2 border-t border-slate-100 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/40 text-[10px] text-slate-400 flex items-center justify-between">
          <span>Press <kbd className="font-mono bg-white dark:bg-slate-700 px-1 py-0.5 rounded border border-slate-200 dark:border-slate-600">Ctrl</kbd> + <kbd className="font-mono bg-white dark:bg-slate-700 px-1 py-0.5 rounded border border-slate-200 dark:border-slate-600">K</kbd> anytime</span>
          <span className="flex items-center gap-1">Select <CornerDownLeft className="w-3 h-3" /></span>
        </div>
      </div>
    </div>
  );
};
