import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../api/authApi';
import {
  User as UserIcon,
  Mail,
  Phone,
  Shield,
  KeyRound,
  CheckCircle2,
  AlertCircle,
  Loader2,
  Lock,
  Eye,
  EyeOff,
  Building,
  Calendar,
  AlertTriangle,
} from 'lucide-react';

export const ProfilePage: React.FC = () => {
  const { user, loginWithToken, token } = useAuth();

  // Profile Edit State
  const [fullName, setFullName] = useState(user?.fullName || '');
  const [phone, setPhone] = useState(user?.phone || '');
  const [profileLoading, setProfileLoading] = useState(false);
  const [profileSuccess, setProfileSuccess] = useState<string | null>(null);
  const [profileError, setProfileError] = useState<string | null>(null);

  // Password Change State
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [passwordLoading, setPasswordLoading] = useState(false);
  const [passwordSuccess, setPasswordSuccess] = useState<string | null>(null);
  const [passwordError, setPasswordError] = useState<string | null>(null);

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setProfileError(null);
    setProfileSuccess(null);
    setProfileLoading(true);

    try {
      const updated = await authApi.updateProfile({
        fullName: fullName.trim(),
        phone: phone.trim() || undefined,
      });
      if (token) {
        await loginWithToken(token, updated);
      }
      setProfileSuccess('Profile details successfully updated.');
    } catch (err: any) {
      setProfileError(err.response?.data?.message || 'Failed to update profile.');
    } finally {
      setProfileLoading(false);
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setPasswordError(null);
    setPasswordSuccess(null);

    if (newPassword !== confirmPassword) {
      setPasswordError('New password and confirm password do not match.');
      return;
    }
    if (newPassword.length < 8) {
      setPasswordError('New password must be at least 8 characters with complexity requirements.');
      return;
    }

    setPasswordLoading(true);

    try {
      await authApi.changePassword({
        currentPassword,
        newPassword,
        confirmPassword,
      });
      setPasswordSuccess('Password successfully changed.');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err: any) {
      setPasswordError(err.response?.data?.message || 'Failed to change password. Please verify your current password.');
    } finally {
      setPasswordLoading(false);
    }
  };

  if (!user) {
    return (
      <div className="p-8 text-center text-xs text-slate-500">
        Please sign in to view your profile.
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Title */}
      <div>
        <h1 className="text-xl font-bold text-slate-900 dark:text-slate-100">User Profile & Account Security</h1>
        <p className="text-xs text-slate-500 dark:text-slate-400">
          Manage your enterprise credentials, contact information, and role privileges.
        </p>
      </div>

      {/* Role Restriction Banner if VIEWER */}
      {user.role === 'VIEWER' && (
        <div className="p-4 bg-amber-50 dark:bg-amber-950/40 border border-amber-200 dark:border-amber-800 rounded-xl flex items-start gap-3">
          <AlertTriangle className="w-5 h-5 text-amber-600 shrink-0 mt-0.5" />
          <div className="space-y-1 text-xs text-amber-900 dark:text-amber-200">
            <div className="font-semibold">Restricted Access Mode: VIEWER Role Assigned</div>
            <p className="leading-relaxed">
              Public self-registered accounts and third-party logins are initially granted read-only observer permissions. To execute automated scans, configure freight optimizations, or perform regulatory reviews, contact a system administrator to request role promotion to <strong>ANALYST</strong>, <strong>REVIEWER</strong>, or <strong>REGULATOR</strong>.
            </p>
          </div>
        </div>
      )}

      {/* Profile Overview Card */}
      <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-2xs">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-slate-100 dark:border-slate-800">
          <div className="flex items-center gap-4">
            <div className="w-14 h-14 rounded-full bg-blue-900 text-amber-400 flex items-center justify-center font-bold text-xl border-2 border-blue-700">
              {user.fullName ? user.fullName.charAt(0).toUpperCase() : user.username.charAt(0).toUpperCase()}
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-base font-bold text-slate-900 dark:text-slate-100">
                  {user.fullName || user.username}
                </h2>
                <span className="text-[10px] font-mono px-2 py-0.5 rounded-full font-bold uppercase tracking-wider bg-blue-100 text-blue-800 dark:bg-blue-950 dark:text-blue-300">
                  {user.role}
                </span>
                {user.emailVerified ? (
                  <span className="text-[10px] px-2 py-0.5 rounded-full font-medium bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300 flex items-center gap-1">
                    <CheckCircle2 className="w-3 h-3 text-emerald-600" />
                    Verified
                  </span>
                ) : (
                  <span className="text-[10px] px-2 py-0.5 rounded-full font-medium bg-rose-100 text-rose-800 dark:bg-rose-950 dark:text-rose-300">
                    Unverified
                  </span>
                )}
              </div>
              <p className="text-xs text-slate-500 font-mono">@{user.username}</p>
            </div>
          </div>

          <div className="text-xs text-slate-500 space-y-1 text-right">
            <div>Auth Provider: <strong className="uppercase">{user.authProvider || 'LOCAL'}</strong></div>
            <div>Account Status: <strong className="text-emerald-600">{user.accountStatus || 'ACTIVE'}</strong></div>
          </div>
        </div>

        {/* Edit Profile Form */}
        <form onSubmit={handleUpdateProfile} className="pt-6 space-y-4">
          <h3 className="text-sm font-semibold text-slate-900 dark:text-slate-100">Personal Information</h3>

          {profileError && (
            <div className="p-3 bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-800 rounded text-xs text-rose-800 dark:text-rose-300">
              {profileError}
            </div>
          )}
          {profileSuccess && (
            <div className="p-3 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 rounded text-xs text-emerald-800 dark:text-emerald-300">
              {profileSuccess}
            </div>
          )}

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1">
                Full Name
              </label>
              <div className="relative">
                <UserIcon className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                <input
                  type="text"
                  required
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  className="w-full pl-9 pr-3 py-2 text-xs rounded border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1">
                Email Address (Read-Only)
              </label>
              <div className="relative">
                <Mail className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                <input
                  type="email"
                  disabled
                  value={user.email}
                  className="w-full pl-9 pr-3 py-2 text-xs rounded border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-850 text-slate-500 cursor-not-allowed"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1">
                Phone Number
              </label>
              <div className="relative">
                <Phone className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                <input
                  type="tel"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                  className="w-full pl-9 pr-3 py-2 text-xs rounded border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                  placeholder="+91 98765 43210"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1">
                Role & Clearances
              </label>
              <div className="relative">
                <Shield className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                <input
                  type="text"
                  disabled
                  value={user.role}
                  className="w-full pl-9 pr-3 py-2 text-xs rounded border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-850 text-slate-500 cursor-not-allowed font-mono"
                />
              </div>
            </div>
          </div>

          <div className="pt-2 flex justify-end">
            <button
              type="submit"
              disabled={profileLoading}
              className="py-2 px-4 bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold rounded shadow-xs transition flex items-center gap-1.5 disabled:opacity-50"
            >
              {profileLoading ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : null}
              <span>Save Changes</span>
            </button>
          </div>
        </form>
      </div>

      {/* Change Password Card */}
      <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-2xs">
        <form onSubmit={handleChangePassword} className="space-y-4">
          <div className="flex items-center gap-2 pb-2 border-b border-slate-100 dark:border-slate-800">
            <KeyRound className="w-4 h-4 text-blue-900 dark:text-blue-400" />
            <h3 className="text-sm font-semibold text-slate-900 dark:text-slate-100">Change Account Password</h3>
          </div>

          {passwordError && (
            <div className="p-3 bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-800 rounded text-xs text-rose-800 dark:text-rose-300">
              {passwordError}
            </div>
          )}
          {passwordSuccess && (
            <div className="p-3 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 rounded text-xs text-emerald-800 dark:text-emerald-300">
              {passwordSuccess}
            </div>
          )}

          <div className="space-y-3">
            <div>
              <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1">
                Current Password
              </label>
              <div className="relative">
                <Lock className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  required
                  value={currentPassword}
                  onChange={(e) => setCurrentPassword(e.target.value)}
                  className="w-full pl-9 pr-9 py-2 text-xs rounded border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                  placeholder="Enter current password"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-2.5 text-slate-400 hover:text-slate-600"
                >
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1">
                  New Password
                </label>
                <div className="relative">
                  <Lock className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                  <input
                    type={showPassword ? 'text' : 'password'}
                    required
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    className="w-full pl-9 pr-3 py-2 text-xs rounded border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                    placeholder="Min 8 characters with symbols"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1">
                  Confirm New Password
                </label>
                <div className="relative">
                  <Lock className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                  <input
                    type={showPassword ? 'text' : 'password'}
                    required
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    className="w-full pl-9 pr-3 py-2 text-xs rounded border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                    placeholder="Confirm new password"
                  />
                </div>
              </div>
            </div>
          </div>

          <div className="pt-2 flex justify-end">
            <button
              type="submit"
              disabled={passwordLoading}
              className="py-2 px-4 bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold rounded shadow-xs transition flex items-center gap-1.5 disabled:opacity-50"
            >
              {passwordLoading ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : null}
              <span>Update Password</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ProfilePage;
