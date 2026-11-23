import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../api/authApi';
import {
  ShieldCheck,
  CheckCircle2,
  AlertCircle,
  Loader2,
  ArrowRight,
  Mail,
  Send,
} from 'lucide-react';

export const VerifyEmailPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { loginWithToken } = useAuth();

  const tokenParam = searchParams.get('token') || '';
  const [token, setToken] = useState(tokenParam);
  const [emailToResend, setEmailToResend] = useState('');

  const [loading, setLoading] = useState(false);
  const [resendLoading, setResendLoading] = useState(false);
  const [resendMessage, setResendMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [verifiedUser, setVerifiedUser] = useState<any | null>(null);

  const performVerification = async (tokenToUse: string) => {
    if (!tokenToUse.trim()) {
      setError('Please provide a valid verification token.');
      return;
    }
    setError(null);
    setLoading(true);

    try {
      const resp = await authApi.verifyEmail(tokenToUse.trim());
      setVerifiedUser(resp.user);
      if (resp.accessToken) {
        await loginWithToken(resp.accessToken, resp.user);
      }
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Verification token is invalid, expired, or has already been used.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (tokenParam) {
      performVerification(tokenParam);
    }
  }, [tokenParam]);

  const handleManualSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    performVerification(token);
  };

  const handleResend = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!emailToResend.trim()) {
      setError('Please provide your email address to resend verification.');
      return;
    }
    setError(null);
    setResendLoading(true);

    try {
      const resp = await authApi.resendVerification(emailToResend.trim());
      setResendMessage(resp.message || 'Verification link has been resent if the account is registered.');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to resend verification email.');
    } finally {
      setResendLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 dark:bg-slate-950 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-md p-8">
        {/* Header */}
        <div className="flex items-center gap-3 mb-6 pb-4 border-b border-slate-100 dark:border-slate-800">
          <div className="w-10 h-10 rounded bg-blue-900 flex items-center justify-center text-amber-400 shrink-0 border border-blue-700">
            <ShieldCheck className="w-6 h-6" />
          </div>
          <div>
            <h1 className="text-lg font-bold text-slate-900 dark:text-slate-100">Email Verification</h1>
            <p className="text-xs text-slate-500 dark:text-slate-400">
              Identity Ownership • Sentinel Platform
            </p>
          </div>
        </div>

        {/* Global Error Banner */}
        {error && (
          <div className="mb-4 flex items-start gap-2.5 p-3 bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-800 rounded-lg text-xs text-rose-800 dark:text-rose-300">
            <AlertCircle className="w-4 h-4 shrink-0 text-rose-600 dark:text-rose-400 mt-0.5" />
            <div className="flex-1 leading-relaxed">{error}</div>
          </div>
        )}

        {/* Resend Notice */}
        {resendMessage && (
          <div className="mb-4 flex items-start gap-2.5 p-3 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 rounded-lg text-xs text-emerald-800 dark:text-emerald-300">
            <CheckCircle2 className="w-4 h-4 shrink-0 text-emerald-600 mt-0.5" />
            <div className="flex-1 leading-relaxed">{resendMessage}</div>
          </div>
        )}

        {/* State 1: Verification in Progress */}
        {loading && (
          <div className="py-8 text-center space-y-3">
            <Loader2 className="w-8 h-8 animate-spin text-blue-900 dark:text-blue-400 mx-auto" />
            <p className="text-xs font-semibold text-slate-700 dark:text-slate-300">
              Verifying security token with server...
            </p>
          </div>
        )}

        {/* State 2: Verification Successful */}
        {!loading && verifiedUser && (
          <div className="space-y-4 py-2">
            <div className="p-4 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 rounded-lg text-emerald-800 dark:text-emerald-300 space-y-2">
              <div className="flex items-center gap-2 font-semibold text-sm">
                <CheckCircle2 className="w-5 h-5 text-emerald-600" />
                <span>Email Address Verified Successfully!</span>
              </div>
              <p className="text-xs leading-relaxed text-slate-700 dark:text-slate-300">
                Welcome, <strong>{verifiedUser.fullName || verifiedUser.username}</strong>. Your account status is now Active with restricted role (<strong>{verifiedUser.role}</strong>).
              </p>
            </div>

            <div className="pt-2 text-center">
              <Link
                to="/"
                className="w-full inline-flex items-center justify-center gap-2 py-2.5 px-4 bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold rounded-lg shadow-xs transition"
              >
                <span>Enter Sentinel Portal</span>
                <ArrowRight className="w-4 h-4" />
              </Link>
            </div>
          </div>
        )}

        {/* State 3: Manual Token Input or Resend Form */}
        {!loading && !verifiedUser && (
          <div className="space-y-5">
            <form onSubmit={handleManualSubmit} className="space-y-3">
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300">
                Enter Verification Token
              </label>
              <input
                type="text"
                required
                value={token}
                onChange={(e) => setToken(e.target.value)}
                className="w-full px-3 py-2 text-xs font-mono rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700 dark:text-slate-100"
                placeholder="Paste verification token here"
              />
              <button
                type="submit"
                className="w-full py-2 px-4 bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold rounded-lg shadow-xs transition flex items-center justify-center gap-1.5"
              >
                <span>Verify Token</span>
                <ArrowRight className="w-3.5 h-3.5" />
              </button>
            </form>

            <div className="pt-4 border-t border-slate-200 dark:border-slate-800 space-y-3">
              <div className="text-xs font-semibold text-slate-700 dark:text-slate-300">
                Didn't receive an email or token expired?
              </div>
              <form onSubmit={handleResend} className="flex gap-2">
                <div className="relative flex-1">
                  <Mail className="w-3.5 h-3.5 absolute left-2.5 top-2.5 text-slate-400" />
                  <input
                    type="email"
                    required
                    value={emailToResend}
                    onChange={(e) => setEmailToResend(e.target.value)}
                    className="w-full pl-8 pr-2 py-1.5 text-xs rounded border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700 dark:text-slate-100"
                    placeholder="Enter email to resend"
                  />
                </div>
                <button
                  type="submit"
                  disabled={resendLoading}
                  className="px-3 py-1.5 bg-slate-200 dark:bg-slate-800 hover:bg-slate-300 text-slate-800 dark:text-slate-200 text-xs font-semibold rounded transition flex items-center gap-1 disabled:opacity-50"
                >
                  {resendLoading ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Send className="w-3.5 h-3.5" />}
                  <span>Resend</span>
                </button>
              </form>
            </div>

            <div className="pt-2 text-center">
              <Link to="/login" className="text-xs text-blue-600 dark:text-blue-400 hover:underline">
                Return to Sign In
              </Link>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default VerifyEmailPage;
