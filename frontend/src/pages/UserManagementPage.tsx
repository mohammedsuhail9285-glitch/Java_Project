import React, { useState, useEffect } from 'react';
import { userApi, CreateUserPayload } from '../api/userApi';
import { User, Role } from '../types';
import { useAuth } from '../context/AuthContext';
import { PageHeader } from '../components/layout/PageHeader';
import {
  Users,
  UserPlus,
  Shield,
  CheckCircle2,
  XCircle,
  AlertCircle,
  RefreshCw,
  X,
  Mail,
  Lock,
  UserCheck,
} from 'lucide-react';

export const UserManagementPage: React.FC = () => {
  const { user: currentUser } = useAuth();
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  // Create User Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [creating, setCreating] = useState(false);
  const [form, setForm] = useState<CreateUserPayload>({
    username: '',
    email: '',
    password: '',
    fullName: '',
    role: 'REGULATOR',
  });

  const loadUsers = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await userApi.getUsers();
      setUsers(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch users.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);

  const handleToggleStatus = async (user: User) => {
    try {
      setError(null);
      const updated = await userApi.toggleStatus(user.id);
      setUsers((prev) => prev.map((u) => (u.id === updated.id ? updated : u)));
      setSuccessMsg(`User '${user.username}' status updated to ${updated.enabled !== false ? 'Enabled' : 'Disabled'}.`);
      setTimeout(() => setSuccessMsg(null), 4000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to toggle user status.');
    }
  };

  const handleCreateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.username.trim() || !form.email.trim() || !form.password.trim() || !form.fullName.trim()) {
      setError('Please fill in all mandatory user fields.');
      return;
    }

    try {
      setCreating(true);
      setError(null);
      const created = await userApi.createUser(form);
      setUsers((prev) => [created, ...prev]);
      setSuccessMsg(`User '${created.username}' created successfully.`);
      setIsModalOpen(false);
      setForm({
        username: '',
        email: '',
        password: '',
        fullName: '',
        role: 'REGULATOR',
      });
      setTimeout(() => setSuccessMsg(null), 4000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create user account.');
    } finally {
      setCreating(false);
    }
  };

  const getRoleBadge = (role: Role) => {
    switch (role) {
      case 'ADMIN':
        return 'bg-purple-50 text-purple-900 border-purple-200';
      case 'REGULATOR':
        return 'bg-blue-50 text-blue-900 border-blue-200';
      case 'REVIEWER':
        return 'bg-amber-50 text-amber-900 border-amber-200';
      case 'ANALYST':
        return 'bg-emerald-50 text-emerald-900 border-emerald-200';
      case 'VIEWER':
        return 'bg-slate-100 text-slate-700 border-slate-300 dark:bg-slate-800 dark:text-slate-400';
      default:
        return 'bg-slate-100 text-slate-800 border-slate-200';
    }
  };

  const handleRoleChange = async (userId: number, newRole: Role) => {
    try {
      setError(null);
      const updated = await userApi.updateRole(userId, newRole);
      setUsers((prev) => prev.map((u) => (u.id === updated.id ? updated : u)));
      setSuccessMsg(`User '${updated.username}' promoted to ${newRole}.`);
      setTimeout(() => setSuccessMsg(null), 4000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update user role.');
    }
  };

  const handleUnlockUser = async (userId: number) => {
    try {
      setError(null);
      const updated = await userApi.unlockUser(userId);
      setUsers((prev) => prev.map((u) => (u.id === updated.id ? updated : u)));
      setSuccessMsg(`User account '${updated.username}' unlocked.`);
      setTimeout(() => setSuccessMsg(null), 4000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to unlock user.');
    }
  };

  return (
    <div className="space-y-5 pb-16">
      <PageHeader
        title="Regulator & Officer Account Administration"
        description="Role-based statutory access control, enforcement officer provisioning, and authentication credentials."
        breadcrumbs={[{ label: 'Administration' }, { label: 'Users' }]}
        action={
          <div className="flex items-center gap-2">
            <button
              onClick={loadUsers}
              disabled={loading}
              className="p-1.5 border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 rounded-md text-slate-700 hover:bg-slate-50 transition"
              title="Refresh Users"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin text-blue-800' : ''}`} />
            </button>
            <button
              onClick={() => setIsModalOpen(true)}
              className="px-3 py-1.5 bg-blue-900 hover:bg-blue-800 text-white rounded-md text-xs font-semibold flex items-center gap-1.5 shadow-2xs transition"
            >
              <UserPlus className="w-3.5 h-3.5" />
              <span>Add Officer Account</span>
            </button>
          </div>
        }
      />

      {/* Notifications */}
      {error && (
        <div className="flex items-center gap-2 p-3 bg-rose-50 border border-rose-200 text-rose-900 rounded-lg text-xs font-semibold">
          <AlertCircle className="w-4 h-4 shrink-0 text-rose-600" />
          <span>{error}</span>
        </div>
      )}

      {successMsg && (
        <div className="flex items-center gap-2 p-3 bg-emerald-50 border border-emerald-200 text-emerald-900 rounded-lg text-xs font-semibold">
          <CheckCircle2 className="w-4 h-4 shrink-0 text-emerald-600" />
          <span>{successMsg}</span>
        </div>
      )}

      {/* Users Table */}
      <div className="bg-white dark:bg-slate-900 rounded-lg border border-slate-200 dark:border-slate-800 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse text-xs">
            <thead>
              <tr className="bg-slate-50 dark:bg-slate-800/60 border-b border-slate-200 dark:border-slate-800 text-slate-600 font-semibold uppercase tracking-wider text-[10px]">
                <th className="py-2.5 px-3">Officer / User</th>
                <th className="py-2.5 px-3">Statutory Role</th>
                <th className="py-2.5 px-3">Email Address</th>
                <th className="py-2.5 px-3">Account Status</th>
                <th className="py-2.5 px-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
              {loading && users.length === 0 ? (
                <tr>
                  <td colSpan={5} className="py-8 text-center text-slate-400">
                    <RefreshCw className="w-4 h-4 animate-spin mx-auto mb-2 text-blue-800" />
                    <span>Loading officer accounts...</span>
                  </td>
                </tr>
              ) : users.length === 0 ? (
                <tr>
                  <td colSpan={5} className="py-8 text-center text-slate-400">
                    No registered user accounts found.
                  </td>
                </tr>
              ) : (
                users.map((u) => {
                  const isSelf = currentUser?.username === u.username;
                  const isEnabled = u.enabled !== false;
                  return (
                    <tr key={u.id} className="hover:bg-slate-50/70 dark:hover:bg-slate-800/40 transition">
                      <td className="py-2.5 px-3">
                        <div className="font-semibold text-slate-900 dark:text-slate-100 flex items-center gap-2">
                          <div className="w-6 h-6 rounded bg-slate-100 text-slate-700 flex items-center justify-center font-bold text-xs uppercase shrink-0">
                            {u.username.substring(0, 2)}
                          </div>
                          <div>
                            <div>{u.fullName || u.username}</div>
                            <div className="text-[10px] text-slate-400 font-mono">@{u.username}</div>
                          </div>
                        </div>
                      </td>

                      <td className="py-2.5 px-3">
                        {isSelf ? (
                          <span className={`inline-flex px-1.5 py-0.5 rounded text-[10px] font-mono font-bold uppercase border ${getRoleBadge(u.role)}`}>
                            {u.role}
                          </span>
                        ) : (
                          <select
                            value={u.role}
                            onChange={(e) => handleRoleChange(u.id, e.target.value as Role)}
                            className={`px-1.5 py-0.5 rounded text-[10px] font-mono font-bold uppercase border cursor-pointer ${getRoleBadge(u.role)} bg-white dark:bg-slate-900`}
                          >
                            <option value="VIEWER">VIEWER</option>
                            <option value="ANALYST">ANALYST</option>
                            <option value="REVIEWER">REVIEWER</option>
                            <option value="REGULATOR">REGULATOR</option>
                            <option value="ADMIN">ADMIN</option>
                          </select>
                        )}
                      </td>

                      <td className="py-2.5 px-3 font-mono text-slate-600 dark:text-slate-400">
                        {u.email}
                      </td>

                      <td className="py-2.5 px-3">
                        <span className={`inline-flex items-center gap-1 px-1.5 py-0.5 rounded text-[10px] font-mono font-bold uppercase border ${
                          isEnabled
                            ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
                            : 'bg-rose-50 text-rose-800 border-rose-200'
                        }`}>
                          <span className={`w-1.5 h-1.5 rounded-full ${isEnabled ? 'bg-emerald-600' : 'bg-rose-600'}`} />
                          {isEnabled ? 'ACTIVE' : 'SUSPENDED'}
                        </span>
                      </td>

                      <td className="py-2.5 px-3 text-right">
                        {isSelf ? (
                          <span className="text-[10px] text-slate-400 italic">Current Session</span>
                        ) : (
                          <div className="flex items-center justify-end gap-1.5">
                            {((u.failedLoginAttempts && u.failedLoginAttempts >= 5) || u.accountStatus === 'LOCKED') && (
                              <button
                                type="button"
                                onClick={() => handleUnlockUser(u.id)}
                                className="px-2 py-0.5 rounded text-[10px] font-semibold border border-amber-300 text-amber-800 bg-amber-50 hover:bg-amber-100 transition"
                              >
                                Unlock
                              </button>
                            )}
                            <button
                              type="button"
                              onClick={() => handleToggleStatus(u)}
                              className={`px-2 py-0.5 rounded text-[10px] font-semibold border transition ${
                                isEnabled
                                  ? 'border-rose-200 text-rose-700 hover:bg-rose-50'
                                  : 'border-emerald-200 text-emerald-700 hover:bg-emerald-50'
                              }`}
                            >
                              {isEnabled ? 'Suspend' : 'Activate'}
                            </button>
                          </div>
                        )}
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add User Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-slate-950/60 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <div className="w-full max-w-md bg-white dark:bg-slate-900 rounded-lg shadow-xl border border-slate-200 dark:border-slate-800 p-5 space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-3">
              <h3 className="text-sm font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
                <UserCheck className="w-4 h-4 text-blue-800" />
                <span>Provision Officer Account</span>
              </h3>
              <button
                type="button"
                disabled={creating}
                onClick={() => setIsModalOpen(false)}
                className="text-slate-400 hover:text-slate-600"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleCreateSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Full Name *
                </label>
                <input
                  type="text"
                  required
                  value={form.fullName}
                  onChange={(e) => setForm({ ...form, fullName: e.target.value })}
                  placeholder="e.g. Ramesh Kumar"
                  className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Username *
                </label>
                <input
                  type="text"
                  required
                  value={form.username}
                  onChange={(e) => setForm({ ...form, username: e.target.value })}
                  placeholder="e.g. ramesh_officer"
                  className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 font-mono focus:outline-none focus:ring-1 focus:ring-blue-700"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Official Email *
                </label>
                <input
                  type="email"
                  required
                  value={form.email}
                  onChange={(e) => setForm({ ...form, email: e.target.value })}
                  placeholder="e.g. ramesh@nic.in"
                  className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Temporary Password *
                </label>
                <input
                  type="password"
                  required
                  value={form.password}
                  onChange={(e) => setForm({ ...form, password: e.target.value })}
                  className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Enforcement Role *
                </label>
                <select
                  value={form.role}
                  onChange={(e) => setForm({ ...form, role: e.target.value as Role })}
                  className="w-full p-2 text-xs rounded-md border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 font-semibold focus:outline-none focus:ring-1 focus:ring-blue-700"
                >
                  <option value="VIEWER">VIEWER (Public Observer / Self-Registered)</option>
                  <option value="ANALYST">ANALYST (Read-Only Analytics)</option>
                  <option value="REVIEWER">REVIEWER (Evidence Triage & Screening)</option>
                  <option value="REGULATOR">REGULATOR (Statutory Enforcement & Determinations)</option>
                  <option value="ADMIN">ADMIN (Full Platform Management)</option>
                </select>
              </div>

              <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-100 dark:border-slate-800">
                <button
                  type="button"
                  disabled={creating}
                  onClick={() => setIsModalOpen(false)}
                  className="px-3 py-1.5 rounded-md bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={creating}
                  className="px-3.5 py-1.5 rounded-md bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold shadow-2xs transition disabled:opacity-50"
                >
                  {creating ? 'Provisioning...' : 'Provision Officer'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
