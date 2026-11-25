import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { authApi } from '../api/authApi';
import { AuthConfig } from '../types';
import {
  ShieldCheck,
  Lock,
  User,
  ArrowRight,
  AlertCircle,
  Mail,
  Eye,
  EyeOff,
  CheckCircle2,
  Loader2,
  Shield,
  FileCheck2,
  Anchor,
  TrendingUp,
  AlertTriangle,
} from 'lucide-react';

const GoogleIcon: React.FC<{ className?: string }> = ({ className = 'w-4 h-4' }) => (
  <svg className={className} viewBox="0 0 24 24" fill="none">
    <path
      d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
      fill="#4285F4"
    />
    <path
      d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
      fill="#34A853"
    />
    <path
      d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"
      fill="#FBBC05"
    />
    <path
      d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"
      fill="#EA4335"
    />
  </svg>
);

export const LoginPage: React.FC = () => {
  const { login, loginWithOtp, loginWithToken } = useAuth();
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  // Active sign-in tab
  const [activeTab, setActiveTab] = useState<'password' | 'otp'>('password');

  // Password flow state
  const [username, setUsername] = useState('regulator');
  const [password, setPassword] = useState('Regulator@123');
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(true);
  const [passwordLoading, setPasswordLoading] = useState(false);

  // OTP flow state
  const [otpStep, setOtpStep] = useState<'request' | 'verify'>('request');
  const [identifier, setIdentifier] = useState('regulator@consumer.gov.in');
  const [otpCode, setOtpCode] = useState('');
  const [devOtpHint, setDevOtpHint] = useState<string | null>(null);
  const [otpMessage, setOtpMessage] = useState<string | null>(null);
  const [otpLoading, setOtpLoading] = useState(false);
  const [verifyLoading, setVerifyLoading] = useState(false);
  const [resendCooldown, setResendCooldown] = useState(0);
  const [countdownSeconds, setCountdownSeconds] = useState(300);

  // Google OAuth & Modal state
  const [googleLoading, setGoogleLoading] = useState(false);
  const [showGoogleModal, setShowGoogleModal] = useState(false);
  const [customGoogleEmail, setCustomGoogleEmail] = useState('');
  const [googleModalLoading, setGoogleModalLoading] = useState(false);

  // General error banner & config
  const [error, setError] = useState<string | null>(null);
  const [unverifiedEmail, setUnverifiedEmail] = useState<string | null>(null);
  const [resendStatus, setResendStatus] = useState<string | null>(null);
  const [authConfig, setAuthConfig] = useState<AuthConfig | null>(null);

  // Check backend auth config on mount
  useEffect(() => {
    const fetchConfig = async () => {
      try {
        const config = await authApi.getAuthConfig();
        setAuthConfig(config);
      } catch (err) {
        console.warn('Could not load auth config, defaulting to standard mode.', err);
      }
    };
    fetchConfig();

    const urlError = searchParams.get('error');
    if (urlError) {
      setError(urlError);
    }
  }, [searchParams]);

  // Countdown timer for OTP expiration
  useEffect(() => {
    if (otpStep !== 'verify' || countdownSeconds <= 0) return;
    const interval = setInterval(() => {
      setCountdownSeconds((prev) => (prev > 0 ? prev - 1 : 0));
    }, 1000);
    return () => clearInterval(interval);
  }, [otpStep, countdownSeconds]);

  // Resend cooldown timer
  useEffect(() => {
    if (resendCooldown <= 0) return;
    const interval = setInterval(() => {
      setResendCooldown((prev) => (prev > 0 ? prev - 1 : 0));
    }, 1000);
    return () => clearInterval(interval);
  }, [resendCooldown]);

  // Clear error and clean URL error query param in React Router and address bar
  const clearError = () => {
    setError(null);
    if (searchParams.get('error')) {
      setSearchParams({}, { replace: true });
    }
    if (window.location.search) {
      window.history.replaceState({}, '', window.location.pathname);
    }
  };

  const handleDemoSelect = (demoUsername: string, demoPassword: string) => {
    clearError();
    setUsername(demoUsername);
    setPassword(demoPassword);
  };

  // Handle standard password login
  const handlePasswordSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !password) {
      setError('Please provide your username or registered email and password.');
      return;
    }
    clearError();
    setUnverifiedEmail(null);
    setPasswordLoading(true);

    try {
      const loggedInUser = await login(username.trim(), password);
      if (loggedInUser?.role === 'ANALYST') {
        navigate('/freight');
      } else {
        navigate('/');
      }
    } catch (err: any) {
      let msg = err.response?.data?.message || err.response?.data?.error;
      if (!msg) {
        if (!err.response || err.code === 'ERR_NETWORK' || err.message?.includes('Network Error')) {
          msg = 'Unable to connect to authentication server. Please ensure the Spring Boot backend is running on port 8080.';
        } else {
          msg = 'Invalid username or password. Please verify your credentials.';
        }
      }

      if (msg.toLowerCase().includes('not verified') || msg.toLowerCase().includes('unverified')) {
        setUnverifiedEmail(username.includes('@') ? username : null);
      }

      setError(msg);
    } finally {
      setPasswordLoading(false);
    }
  };

  // Step 1: Send OTP
  const handleRequestOtp = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!identifier.trim()) {
      setError('Please provide a registered email address or mobile number.');
      return;
    }
    setError(null);
    setOtpLoading(true);

    try {
      const resp = await authApi.requestOtp(identifier.trim());
      setOtpMessage(resp.message);
      setDevOtpHint(resp.devOtpHint || null);
      setCountdownSeconds(resp.expiresInSeconds || 300);
      setResendCooldown(resp.cooldownSeconds || 60);
      setOtpStep('verify');
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Failed to dispatch verification code. Please try again.';
      setError(msg);
    } finally {
      setOtpLoading(false);
    }
  };

  // Step 2: Verify OTP & Sign In
  const handleVerifyOtp = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!otpCode.trim() || otpCode.trim().length < 6) {
      setError('Please enter the complete 6-digit verification code.');
      return;
    }
    setError(null);
    setVerifyLoading(true);

    try {
      const loggedInUser = await loginWithOtp(identifier.trim(), otpCode.trim());
      if (loggedInUser?.role === 'ANALYST') {
        navigate('/freight');
      } else {
        navigate('/');
      }
    } catch (err: any) {
      const msg = err.response?.data?.message || err.response?.data?.error || 'Invalid or expired verification code.';
      setError(msg);
    } finally {
      setVerifyLoading(false);
    }
  };

  // Google OAuth button handler
  const handleGoogleSignIn = () => {
    clearError();
    if (authConfig?.googleOAuthEnabled) {
      setGoogleLoading(true);
      window.location.href = '/oauth2/authorization/google';
    } else {
      setShowGoogleModal(true);
    }
  };

  const handleGoogleDemoSelect = async (email: string, fullName: string, role: string) => {
    setGoogleModalLoading(true);
    try {
      const resp = await authApi.googleDemoLogin(email, fullName, role);
      await loginWithToken(resp.accessToken, resp.user);
      setShowGoogleModal(false);
      if (resp.user?.role === 'ANALYST') {
        navigate('/freight');
      } else {
        navigate('/');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Google authentication failed.');
      setShowGoogleModal(false);
    } finally {
      setGoogleModalLoading(false);
    }
  };

  // Handle Resend Verification
  const handleResendVerification = async () => {
    if (!unverifiedEmail) return;
    try {
      await authApi.resendVerification(unverifiedEmail);
      setResendStatus('Verification link sent! Check your inbox.');
    } catch (err: any) {
      setResendStatus('Could not resend verification email.');
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 dark:bg-slate-950 flex items-center justify-center p-4">
      {/* Enterprise Split Container */}
      <div className="w-full max-w-4xl bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-md overflow-hidden grid grid-cols-1 md:grid-cols-12 min-h-[580px]">
        {/* LEFT COLUMN: BRANDING & PLATFORM CAPABILITIES */}
        <div className="md:col-span-5 bg-slate-900 text-white p-8 flex flex-col justify-between border-b md:border-b-0 md:border-r border-slate-800">
          <div className="space-y-6">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded bg-blue-800 flex items-center justify-center text-amber-400 shrink-0 border border-blue-700">
                <ShieldCheck className="w-6 h-6" />
              </div>
              <div>
                <h1 className="text-lg font-bold tracking-tight">RegTech Sentinel</h1>
                <p className="text-[11px] text-slate-400 font-medium">Dual-Core Enterprise Platform</p>
              </div>
            </div>

            <div className="space-y-2">
              <div className="text-[11px] font-mono text-amber-400 font-semibold uppercase tracking-wider">
                Smart India Hackathon • SIH26006
              </div>
              <h2 className="text-base font-semibold text-slate-100 leading-snug">
                Intelligent Freight Forecasting & Port Procurement
              </h2>
              <p className="text-xs text-slate-400 leading-relaxed">
                Empowering bulk cargo chartering to East Coast of India with AI rate forecasting, vessel-port compatibility analysis, demurrage mitigation, and unified compliance automation.
              </p>
            </div>

            <div className="space-y-3 pt-2 text-xs text-slate-300">
              <div className="flex items-start gap-2.5">
                <Anchor className="w-4 h-4 text-blue-400 shrink-0 mt-0.5" />
                <span>East Coast India port draft, LOA, beam & lighterage checks</span>
              </div>
              <div className="flex items-start gap-2.5">
                <TrendingUp className="w-4 h-4 text-blue-400 shrink-0 mt-0.5" />
                <span>Multi-horizon freight forecasting & Spot vs COA optimization</span>
              </div>
              <div className="flex items-start gap-2.5">
                <FileCheck2 className="w-4 h-4 text-blue-400 shrink-0 mt-0.5" />
                <span>Dual-core Legal Metrology statutory declaration scanner</span>
              </div>
            </div>
          </div>

          <div className="pt-6 border-t border-slate-800 text-[10px] text-slate-500 space-y-1">
            <div>Production Authentication System • Role-Based Access Control</div>
            <div>Secure HttpOnly Sessions & Multi-Factor Verification</div>
          </div>
        </div>

        {/* RIGHT COLUMN: PROFESSIONAL SIGN-IN INTERFACE */}
        <div className="md:col-span-7 p-8 flex flex-col justify-center space-y-5 bg-white dark:bg-slate-900">
          <div>
            <h3 className="text-lg font-bold text-slate-900 dark:text-slate-100">
              Account Sign-In
            </h3>
            <p className="text-xs text-slate-500 dark:text-slate-400">
              Access the maritime freight engine and regulatory inspection console.
            </p>
          </div>

          {/* Global Error Banner */}
          {error && (
            <div className="flex items-start gap-2.5 p-3.5 bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-800 rounded-lg text-xs text-rose-800 dark:text-rose-300">
              <AlertCircle className="w-4 h-4 shrink-0 text-rose-600 dark:text-rose-400 mt-0.5" />
              <div className="flex-1 leading-normal">
                <div>{error}</div>
                {error.includes('Google authentication failed') && (
                  <div className="mt-2 pt-2 border-t border-rose-200 dark:border-rose-800 text-[11px] text-rose-700 dark:text-rose-400 flex items-center justify-between">
                    <span>Use direct demo credentials below to sign in without Google:</span>
                    <button
                      type="button"
                      onClick={() => handleDemoSelect('regulator', 'Regulator@123')}
                      className="ml-2 px-2 py-0.5 rounded bg-blue-600 text-white font-medium hover:bg-blue-700 transition"
                    >
                      Fill Demo Credentials
                    </button>
                  </div>
                )}
                {unverifiedEmail && (
                  <div className="pt-1.5 flex items-center gap-2">
                    <button
                      type="button"
                      onClick={handleResendVerification}
                      className="text-blue-600 dark:text-blue-400 font-semibold underline hover:no-underline"
                    >
                      Resend verification email
                    </button>
                    {resendStatus && <span className="text-slate-600 dark:text-slate-400">({resendStatus})</span>}
                  </div>
                )}
              </div>
              <button
                type="button"
                onClick={clearError}
                className="text-rose-400 hover:text-rose-600 text-sm font-bold leading-none p-1"
                aria-label="Dismiss error"
              >
                ×
              </button>
            </div>
          )}

          {/* Google OAuth Button */}
          <div>
            <button
              type="button"
              onClick={handleGoogleSignIn}
              disabled={googleLoading}
              className="w-full py-2.5 px-4 bg-white dark:bg-slate-800 border border-slate-300 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-750 text-slate-700 dark:text-slate-200 text-xs font-semibold rounded-lg shadow-2xs transition flex items-center justify-center gap-2.5"
            >
              {googleLoading ? (
                <Loader2 className="w-4 h-4 animate-spin text-slate-500" />
              ) : (
                <GoogleIcon className="w-4 h-4" />
              )}
              <span>Continue with Google</span>
            </button>
          </div>

          {/* Divider */}
          <div className="relative flex items-center justify-center">
            <div className="border-t border-slate-200 dark:border-slate-800 w-full" />
            <span className="bg-white dark:bg-slate-900 px-3 text-[11px] font-medium text-slate-400 uppercase tracking-wider">
              Or sign in with credentials
            </span>
          </div>

          {/* Authentication Method Selector */}
          <div className="flex rounded-lg border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800 p-0.5">
            <button
              type="button"
              onClick={() => {
                setActiveTab('password');
                clearError();
              }}
              className={`flex-1 py-1.5 text-xs font-semibold rounded-md transition ${
                activeTab === 'password'
                  ? 'bg-white dark:bg-slate-900 text-slate-900 dark:text-slate-100 shadow-2xs'
                  : 'text-slate-500 hover:text-slate-800 dark:hover:text-slate-200'
              }`}
            >
              Password Login
            </button>
            <button
              type="button"
              onClick={() => {
                setActiveTab('otp');
                clearError();
              }}
              className={`flex-1 py-1.5 text-xs font-semibold rounded-md transition ${
                activeTab === 'otp'
                  ? 'bg-white dark:bg-slate-900 text-slate-900 dark:text-slate-100 shadow-2xs'
                  : 'text-slate-500 hover:text-slate-800 dark:hover:text-slate-200'
              }`}
            >
              OTP Code Login
            </button>
          </div>

          {/* TAB 1: PASSWORD LOGIN */}
          {activeTab === 'password' && (
            <form onSubmit={handlePasswordSubmit} className="space-y-3.5">
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Username or Email Address
                </label>
                <div className="relative">
                  <User className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                  <input
                    type="text"
                    required
                    value={username}
                    onChange={(e) => {
                      setUsername(e.target.value);
                      if (error) clearError();
                    }}
                    className="w-full pl-9 pr-3 py-2 text-xs rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700 dark:text-slate-100"
                    placeholder="e.g. regulator, admin, or officer@company.com"
                    autoComplete="username"
                  />
                </div>
              </div>

              <div>
                <div className="flex items-center justify-between mb-1">
                  <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">
                    Password
                  </label>
                  <Link
                    to="/forgot-password"
                    className="text-[11px] font-medium text-blue-600 dark:text-blue-400 hover:underline"
                  >
                    Forgot password?
                  </Link>
                </div>
                <div className="relative">
                  <Lock className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                  <input
                    type={showPassword ? 'text' : 'password'}
                    required
                    value={password}
                    onChange={(e) => {
                      setPassword(e.target.value);
                      if (error) clearError();
                    }}
                    className="w-full pl-9 pr-9 py-2 text-xs rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700 dark:text-slate-100"
                    placeholder="Enter password"
                    autoComplete="current-password"
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

              {/* Remember Me Checkbox */}
              <div className="flex items-center justify-between pt-0.5">
                <label className="flex items-center gap-2 cursor-pointer text-xs text-slate-600 dark:text-slate-400">
                  <input
                    type="checkbox"
                    checked={rememberMe}
                    onChange={(e) => setRememberMe(e.target.checked)}
                    className="rounded border-slate-300 text-blue-900 focus:ring-blue-700"
                  />
                  <span>Remember me on this browser</span>
                </label>
              </div>

              {/* Quick Preset Buttons for Demonstration */}
              <div className="pt-1">
                <div className="text-[10px] text-slate-400 uppercase tracking-wider mb-1 font-semibold">
                  Demo Evaluation Accounts:
                </div>
                <div className="flex flex-wrap gap-1.5">
                  <button
                    type="button"
                    onClick={() => handleDemoSelect('regulator', 'Regulator@123')}
                    className={`text-[10px] px-2 py-0.5 rounded border transition font-mono ${
                      username === 'regulator'
                        ? 'bg-blue-50 border-blue-300 text-blue-900 dark:bg-blue-950 dark:text-blue-300 font-bold'
                        : 'bg-slate-50 border-slate-200 text-slate-600 hover:bg-slate-100 dark:bg-slate-800 dark:text-slate-400'
                    }`}
                  >
                    Regulator
                  </button>
                  <button
                    type="button"
                    onClick={() => handleDemoSelect('admin', 'Admin@123')}
                    className={`text-[10px] px-2 py-0.5 rounded border transition font-mono ${
                      username === 'admin'
                        ? 'bg-blue-50 border-blue-300 text-blue-900 dark:bg-blue-950 dark:text-blue-300 font-bold'
                        : 'bg-slate-50 border-slate-200 text-slate-600 hover:bg-slate-100 dark:bg-slate-800 dark:text-slate-400'
                    }`}
                  >
                    Admin
                  </button>
                  <button
                    type="button"
                    onClick={() => handleDemoSelect('analyst', 'Analyst@123')}
                    className={`text-[10px] px-2 py-0.5 rounded border transition font-mono ${
                      username === 'analyst'
                        ? 'bg-blue-50 border-blue-300 text-blue-900 dark:bg-blue-950 dark:text-blue-300 font-bold'
                        : 'bg-slate-50 border-slate-200 text-slate-600 hover:bg-slate-100 dark:bg-slate-800 dark:text-slate-400'
                    }`}
                  >
                    Analyst
                  </button>
                  <button
                    type="button"
                    onClick={() => handleDemoSelect('reviewer', 'Reviewer@123')}
                    className={`text-[10px] px-2 py-0.5 rounded border transition font-mono ${
                      username === 'reviewer'
                        ? 'bg-blue-50 border-blue-300 text-blue-900 dark:bg-blue-950 dark:text-blue-300 font-bold'
                        : 'bg-slate-50 border-slate-200 text-slate-600 hover:bg-slate-100 dark:bg-slate-800 dark:text-slate-400'
                    }`}
                  >
                    Reviewer
                  </button>
                </div>
              </div>

              {/* Submit Button */}
              <button
                type="submit"
                disabled={passwordLoading}
                className="w-full py-2.5 px-4 bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold rounded-lg shadow-xs transition flex items-center justify-center gap-1.5 disabled:opacity-50"
              >
                {passwordLoading ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin" />
                    <span>Verifying Credentials...</span>
                  </>
                ) : (
                  <>
                    <span>Sign In to Platform</span>
                    <ArrowRight className="w-4 h-4" />
                  </>
                )}
              </button>

              {/* Create Account Link */}
              <div className="pt-2 text-center text-xs text-slate-500 dark:text-slate-400">
                Don't have an enterprise account?{' '}
                <Link to="/register" className="text-blue-600 dark:text-blue-400 font-semibold hover:underline">
                  Create an account
                </Link>
              </div>
            </form>
          )}

          {/* TAB 2: OTP SIGN-IN */}
          {activeTab === 'otp' && (
            <div className="space-y-4">
              {otpStep === 'request' ? (
                <form onSubmit={handleRequestOtp} className="space-y-4">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                      Registered Email or Mobile Number
                    </label>
                    <div className="relative">
                      <Mail className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                      <input
                        type="text"
                        required
                        value={identifier}
                        onChange={(e) => setIdentifier(e.target.value)}
                        className="w-full pl-9 pr-3 py-2 text-xs rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700 dark:text-slate-100"
                        placeholder="e.g. regulator@consumer.gov.in"
                      />
                    </div>
                  </div>

                  <button
                    type="submit"
                    disabled={otpLoading}
                    className="w-full py-2 px-4 bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold rounded-lg shadow-xs transition flex items-center justify-center gap-1.5 disabled:opacity-50"
                  >
                    {otpLoading ? (
                      <>
                        <Loader2 className="w-3.5 h-3.5 animate-spin" />
                        <span>Dispatching OTP...</span>
                      </>
                    ) : (
                      <>
                        <span>Send 6-Digit OTP</span>
                        <ArrowRight className="w-3.5 h-3.5" />
                      </>
                    )}
                  </button>
                </form>
              ) : (
                <form onSubmit={handleVerifyOtp} className="space-y-4">
                  <div className="flex items-center justify-between text-xs text-slate-600 dark:text-slate-400">
                    <span>Sent to: <strong className="text-slate-800 dark:text-slate-200">{identifier}</strong></span>
                    <button
                      type="button"
                      onClick={() => {
                        setOtpStep('request');
                        setOtpCode('');
                        setError(null);
                      }}
                      className="text-blue-600 hover:underline text-[11px]"
                    >
                      Change
                    </button>
                  </div>

                  {devOtpHint && (
                    <div className="p-2.5 bg-blue-50 dark:bg-blue-950/40 border border-blue-200 dark:border-blue-800 rounded-lg text-xs flex items-center justify-between">
                      <span className="text-slate-600 dark:text-slate-400">Demo Code:</span>
                      <span className="font-mono font-bold text-blue-900 dark:text-blue-300">{devOtpHint}</span>
                      <button
                        type="button"
                        onClick={() => setOtpCode(devOtpHint)}
                        className="text-[11px] text-blue-600 hover:underline font-semibold"
                      >
                        Auto-fill
                      </button>
                    </div>
                  )}

                  <div>
                    <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                      Enter 6-Digit OTP
                    </label>
                    <input
                      type="text"
                      maxLength={6}
                      required
                      value={otpCode}
                      onChange={(e) => setOtpCode(e.target.value.replace(/\D/g, ''))}
                      className="w-full text-center tracking-[0.5em] font-mono text-base py-2 rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700 dark:text-slate-100"
                      placeholder="••••••"
                    />
                  </div>

                  <button
                    type="submit"
                    disabled={verifyLoading || otpCode.length < 6}
                    className="w-full py-2 px-4 bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold rounded-lg shadow-xs transition flex items-center justify-center gap-1.5 disabled:opacity-50"
                  >
                    {verifyLoading ? (
                      <>
                        <Loader2 className="w-3.5 h-3.5 animate-spin" />
                        <span>Verifying...</span>
                      </>
                    ) : (
                      <>
                        <span>Confirm & Access Portal</span>
                        <ArrowRight className="w-3.5 h-3.5" />
                      </>
                    )}
                  </button>
                </form>
              )}
            </div>
          )}
        </div>
      </div>

      {/* GOOGLE ACCOUNT CHOOSER MODAL (FOR SEAMLESS EVALUATION / DEMO OAUTH) */}
      {showGoogleModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-xs p-4 animate-in fade-in duration-150">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl max-w-md w-full p-6 space-y-5">
            {/* Modal Header */}
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
              <div className="flex items-center gap-2.5">
                <GoogleIcon className="w-6 h-6" />
                <div>
                  <h4 className="text-sm font-bold text-slate-900 dark:text-slate-100">
                    Sign in with Google
                  </h4>
                  <p className="text-[11px] text-slate-500">
                    to continue to RegTech Sentinel
                  </p>
                </div>
              </div>
              <button
                type="button"
                onClick={() => setShowGoogleModal(false)}
                className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 text-lg font-bold p-1 rounded leading-none"
              >
                ×
              </button>
            </div>

            {/* Account List */}
            <div className="space-y-2">
              <div className="text-[11px] font-semibold text-slate-500 uppercase tracking-wider">
                Choose an account
              </div>

              {/* Option 1: Maritime Freight Analyst */}
              <button
                type="button"
                disabled={googleModalLoading}
                onClick={() =>
                  handleGoogleDemoSelect(
                    'analyst.sih26006@gmail.com',
                    'Dr. Vikram Malhotra (Lead Analyst)',
                    'ANALYST'
                  )
                }
                className="w-full text-left p-3 rounded-xl border border-slate-200 dark:border-slate-800 hover:border-blue-500 dark:hover:border-blue-500 hover:bg-blue-50/50 dark:hover:bg-blue-950/30 transition flex items-center gap-3 group"
              >
                <div className="w-9 h-9 rounded-full bg-blue-600 text-white font-bold flex items-center justify-center text-xs shrink-0 shadow-xs">
                  VM
                </div>
                <div className="flex-1 min-w-0">
                  <div className="text-xs font-semibold text-slate-800 dark:text-slate-100 truncate group-hover:text-blue-600 dark:group-hover:text-blue-400">
                    Dr. Vikram Malhotra
                  </div>
                  <div className="text-[11px] text-slate-500 truncate">
                    analyst.sih26006@gmail.com
                  </div>
                </div>
                <span className="text-[10px] px-2 py-0.5 rounded font-mono bg-blue-100 text-blue-800 dark:bg-blue-900/60 dark:text-blue-300 font-semibold shrink-0">
                  ANALYST
                </span>
              </button>

              {/* Option 2: Legal Metrology Regulatory Officer */}
              <button
                type="button"
                disabled={googleModalLoading}
                onClick={() =>
                  handleGoogleDemoSelect(
                    'regulator.officer@gmail.com',
                    'Priya Sharma (Senior Metrology Officer)',
                    'REGULATOR'
                  )
                }
                className="w-full text-left p-3 rounded-xl border border-slate-200 dark:border-slate-800 hover:border-blue-500 dark:hover:border-blue-500 hover:bg-blue-50/50 dark:hover:bg-blue-950/30 transition flex items-center gap-3 group"
              >
                <div className="w-9 h-9 rounded-full bg-emerald-600 text-white font-bold flex items-center justify-center text-xs shrink-0 shadow-xs">
                  PS
                </div>
                <div className="flex-1 min-w-0">
                  <div className="text-xs font-semibold text-slate-800 dark:text-slate-100 truncate group-hover:text-blue-600 dark:group-hover:text-blue-400">
                    Priya Sharma
                  </div>
                  <div className="text-[11px] text-slate-500 truncate">
                    regulator.officer@gmail.com
                  </div>
                </div>
                <span className="text-[10px] px-2 py-0.5 rounded font-mono bg-emerald-100 text-emerald-800 dark:bg-emerald-900/60 dark:text-emerald-300 font-semibold shrink-0">
                  REGULATOR
                </span>
              </button>

              {/* Option 3: System Administrator */}
              <button
                type="button"
                disabled={googleModalLoading}
                onClick={() =>
                  handleGoogleDemoSelect(
                    'director.admin@gmail.com',
                    'Rajesh Varma (System Administrator)',
                    'ADMIN'
                  )
                }
                className="w-full text-left p-3 rounded-xl border border-slate-200 dark:border-slate-800 hover:border-blue-500 dark:hover:border-blue-500 hover:bg-blue-50/50 dark:hover:bg-blue-950/30 transition flex items-center gap-3 group"
              >
                <div className="w-9 h-9 rounded-full bg-purple-600 text-white font-bold flex items-center justify-center text-xs shrink-0 shadow-xs">
                  RV
                </div>
                <div className="flex-1 min-w-0">
                  <div className="text-xs font-semibold text-slate-800 dark:text-slate-100 truncate group-hover:text-blue-600 dark:group-hover:text-blue-400">
                    Rajesh Varma
                  </div>
                  <div className="text-[11px] text-slate-500 truncate">
                    director.admin@gmail.com
                  </div>
                </div>
                <span className="text-[10px] px-2 py-0.5 rounded font-mono bg-purple-100 text-purple-800 dark:bg-purple-900/60 dark:text-purple-300 font-semibold shrink-0">
                  ADMIN
                </span>
              </button>
            </div>

            {/* Custom Google Email Option */}
            <div className="pt-2 border-t border-slate-100 dark:border-slate-800">
              <div className="text-[11px] text-slate-600 dark:text-slate-400 mb-1.5 font-medium">
                Or enter another Google account:
              </div>
              <div className="flex gap-2">
                <input
                  type="email"
                  value={customGoogleEmail}
                  onChange={(e) => setCustomGoogleEmail(e.target.value)}
                  placeholder="yourname@gmail.com"
                  className="flex-1 px-3 py-1.5 text-xs rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-700"
                />
                <button
                  type="button"
                  disabled={googleModalLoading || !customGoogleEmail.includes('@')}
                  onClick={() =>
                    handleGoogleDemoSelect(
                      customGoogleEmail.trim(),
                      customGoogleEmail.split('@')[0],
                      'ANALYST'
                    )
                  }
                  className="px-3 py-1.5 bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold rounded-lg transition disabled:opacity-50"
                >
                  Continue
                </button>
              </div>
            </div>

            {/* Informational Guidance */}
            <div className="p-3 bg-slate-50 dark:bg-slate-800/60 rounded-xl text-[11px] text-slate-500 space-y-1">
              <div className="font-semibold text-slate-700 dark:text-slate-300 flex items-center gap-1.5">
                <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600 dark:text-emerald-400" />
                <span>Demonstration Google Authentication Active</span>
              </div>
              <p>
                To enable live cloud Google Sign-In with Google's servers, add your Google Cloud Console Client ID & Secret to <code className="bg-slate-200 dark:bg-slate-700 px-1 rounded text-[10px]">.env</code>.
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default LoginPage;
