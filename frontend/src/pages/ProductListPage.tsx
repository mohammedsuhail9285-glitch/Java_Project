import React, { useState, useEffect } from 'react';
import { productApi } from '../api/productApi';
import { Product } from '../types';
import { StatusBadge } from '../components/common/StatusBadge';
import { PageHeader } from '../components/layout/PageHeader';
import {
  Package,
  Search,
  Filter,
  Eye,
  RefreshCw,
  Plus,
  AlertTriangle,
  Building,
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export const ProductListPage: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [categoryFilter, setCategoryFilter] = useState('ALL');
  const navigate = useNavigate();

  const loadProducts = async () => {
    try {
      setLoading(true);
      const res = await productApi.getProducts({
        search: searchTerm || undefined,
        status: statusFilter !== 'ALL' ? statusFilter : undefined,
        category: categoryFilter !== 'ALL' ? categoryFilter : undefined,
        size: 50,
      });
      setProducts(res.content);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProducts();
  }, [statusFilter, categoryFilter]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    loadProducts();
  };

  return (
    <div className="space-y-6 pb-16">
      {/* Header & Controls */}
      <PageHeader
        title="Monitored Product Catalog"
        description="Inspect physical package label declarations cross-verified with digital marketplace listings under LMPC Rules, 2011."
        icon={Package}
        breadcrumbs={[
          { label: 'Overview', to: '/' },
          { label: 'Compliance' },
          { label: 'Products' },
        ]}
        actions={
          <button
            onClick={() => navigate('/single-scan')}
            className="px-3.5 py-2 rounded-lg bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold shadow-sm flex items-center gap-1.5 transition"
          >
            <Plus className="w-3.5 h-3.5" />
            New Compliance Scan
          </button>
        }
      />

      {/* Filter and Search Bar */}
      <div className="p-4 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm flex flex-col sm:flex-row items-center justify-between gap-4">
        <form onSubmit={handleSearchSubmit} className="relative w-full sm:w-80">
          <Search className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
          <input
            type="text"
            placeholder="Search by product name, SKU, seller..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-9 pr-4 py-2 text-xs rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-600 dark:text-slate-100"
          />
        </form>

        <div className="flex items-center gap-3 w-full sm:w-auto flex-wrap">
          <div className="flex items-center gap-2 text-xs font-semibold text-slate-500">
            <Filter className="w-3.5 h-3.5" />
            Filters:
          </div>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-1.5 rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-xs font-medium text-slate-700 dark:text-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-600"
          >
            <option value="ALL">All Compliance Statuses</option>
            <option value="POTENTIAL_NON_COMPLIANT">Potential Non-Compliance</option>
            <option value="REQUIRES_REVIEW">Requires Review</option>
            <option value="PASSED_AUTOMATED_CHECKS">Passed Automated Checks</option>
            <option value="PENDING_SCAN">Pending Scan</option>
          </select>

          <button
            onClick={loadProducts}
            title="Refresh Catalog"
            className="p-2 border border-slate-200 dark:border-slate-700 rounded-lg hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300"
          >
            <RefreshCw className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Catalog Table */}
      <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-12 text-center text-xs text-slate-500 flex items-center justify-center gap-2">
            <RefreshCw className="w-4 h-4 animate-spin text-blue-600" />
            Loading catalog declarations...
          </div>
        ) : products.length === 0 ? (
          <div className="p-12 text-center text-xs text-slate-500">
            No products match the selected criteria.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 text-slate-500 uppercase tracking-wider font-semibold border-b border-slate-100 dark:border-slate-800">
                <tr>
                  <th className="py-3 px-4">SKU / ID</th>
                  <th className="py-3 px-4">Commodity / Product Name</th>
                  <th className="py-3 px-4">Brand</th>
                  <th className="py-3 px-4">Seller / Platform</th>
                  <th className="py-3 px-4">Origin</th>
                  <th className="py-3 px-4">Declared MRP</th>
                  <th className="py-3 px-4">Listing Price</th>
                  <th className="py-3 px-4">Compliance Status</th>
                  <th className="py-3 px-4 text-right">Inspection Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {products.map((p) => {
                  const hasPriceDiscrepancy =
                    p.declaredMrp && p.listingPrice && p.listingPrice > p.declaredMrp;

                  return (
                    <tr
                      key={p.id}
                      className="hover:bg-slate-50/50 dark:hover:bg-slate-800/40 transition group cursor-pointer"
                      onClick={() => navigate(`/products/${p.id}`)}
                    >
                      <td className="py-3 px-4 font-mono font-medium text-slate-500">
                        PRD-{p.id}
                      </td>

                      <td className="py-3 px-4">
                        <div className="font-semibold text-slate-900 dark:text-slate-100 max-w-xs truncate">
                          {p.name}
                        </div>
                        <div className="text-[11px] text-slate-400">
                          {p.categoryName || 'Packaged Commodity'}
                        </div>
                      </td>

                      <td className="py-3 px-4 text-slate-600 dark:text-slate-400 font-medium">
                        {p.brandName || 'Verified Brand'}
                      </td>

                      <td className="py-3 px-4 text-slate-600 dark:text-slate-400">
                        {p.sellerName || 'RetailBharat'}
                      </td>

                      <td className="py-3 px-4 text-slate-600 dark:text-slate-400">
                        {p.countryOfOrigin || 'India'}
                      </td>

                      <td className="py-3 px-4 font-mono font-semibold text-slate-800 dark:text-slate-200">
                        {p.declaredMrp ? `₹${p.declaredMrp.toFixed(2)}` : 'Not Declared'}
                      </td>

                      <td className="py-3 px-4 font-mono">
                        {p.listingPrice ? (
                          <span
                            className={
                              hasPriceDiscrepancy
                                ? 'text-rose-600 dark:text-rose-400 font-bold flex items-center gap-1'
                                : 'text-slate-700 dark:text-slate-300 font-semibold'
                            }
                          >
                            ₹{p.listingPrice.toFixed(2)}
                            {hasPriceDiscrepancy && (
                              <span title="Listing price exceeds physical package MRP!">
                                <AlertTriangle className="w-3.5 h-3.5 text-rose-600 inline" />
                              </span>
                            )}
                          </span>
                        ) : (
                          'Not Listed'
                        )}
                      </td>

                      <td className="py-3 px-4">
                        <StatusBadge status={p.status} size="sm" />
                      </td>

                      <td className="py-3 px-4 text-right">
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            navigate(`/products/${p.id}`);
                          }}
                          className="px-3 py-1.5 rounded bg-blue-50 dark:bg-blue-950/60 hover:bg-blue-100 dark:hover:bg-blue-900 text-blue-700 dark:text-blue-300 font-semibold text-[11px] inline-flex items-center gap-1 transition"
                        >
                          <Eye className="w-3.5 h-3.5" />
                          Deep Inspect
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProductListPage;
