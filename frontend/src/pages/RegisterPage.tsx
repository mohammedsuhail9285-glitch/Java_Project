import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authApi } from '../api/authApi';
import {
  ShieldCheck,
  User,
  Mail,
  Lock,
  Eye,
  EyeOff,
  Phone,
  CheckCircle2,
  AlertCircle,
  Loader2,
  ArrowRight,
  ShieldAlert,
} from 'lucide-react';

export const RegisterPage: React.FC = () => {
  const navigate = useNavigate();

  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [termsAccepted, setTermsAccepted] = useState(false);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successData, setSuccessData] = useState<{
    message: string;
    email: string;
    devToken?: string;
  } | null>(null);

  // Password strength calculations
  const hasMinLength = password.length >= 8;
  const hasUpper = /[A-Z]/.test(password);
  const hasLower = /[a-z]/.test(password);
  const hasDigit = /[0-9]/.test(password);
  const hasSpecial = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password);

  const passedCriteriaCount = [hasMinLength, hasUpper, hasLower, hasDigit, hasSpecial].filter(Boolean).length;
  const strengthScore = (passedCriteriaCount / 5) * 100;

  const getStrengthColor = () => {
    if (passedCriteriaCount <= 2) return 'bg-rose-500';
    if (passedCriteriaCount <= 4) return 'bg-amber-500';
    return 'bg-emerald-500';
  };

  const getStrengthText = () => {
    if (passedCriteriaCount <= 2) return 'Weak';
    if (passedCriteriaCount <= 4) return 'Moderate';
    return 'Strong';
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!termsAccepted) {
      setError('You must accept the terms of service and regulatory compliance policy to proceed.');
      return;
    }

    if (password !== confirmPassword) {
      setError('Password and confirmation password do not match.');
      return;
    }

    if (passedCriteriaCount < 4) {
      setError('Password does not meet required security complexity standards.');
      return;
    }

    setLoading(true);
    try {
      const resp = await authApi.register({
        fullName: fullName.trim(),
        email: email.trim(),
        password,
        confirmPassword,
        phone: phone.trim() || undefined,
        termsAccepted: true,
      });

      setSuccessData({
        message: 'Registration completed successfully! A verification link has been dispatched to your email address.',
        email: email.trim(),
        devToken: (resp as any).verificationToken || undefined,
      });
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Registration failed. Please try again or verify your inputs.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 dark:bg-slate-950 flex items-center justify-center p-4">
      <div className="w-full max-w-xl bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-md p-8">
        {/* Header */}
        <div className="flex items-center gap-3 mb-6 pb-4 border-b border-slate-100 dark:border-slate-800">
          <div className="w-10 h-10 rounded bg-blue-900 flex items-center justify-center text-amber-400 shrink-0 border border-blue-700">
            <ShieldCheck className="w-6 h-6" />
          </div>
          <div>
            <h1 className="text-lg font-bold text-slate-900 dark:text-slate-100">Create Sentinel Account</h1>
            <p className="text-xs text-slate-500 dark:text-slate-400">
              Enterprise Access Registration • SIH26006 Intelligent Freight & Regulatory Metrology
            </p>
          </div>
        </div>

        {/* Error Alert */}
        {error && (
          <div className="mb-5 flex items-start gap-2.5 p-3.5 bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-800 rounded-lg text-xs text-rose-800 dark:text-rose-300">
            <AlertCircle className="w-4 h-4 shrink-0 text-rose-600 dark:text-rose-400 mt-0.5" />
            <div className="flex-1 leading-relaxed">{error}</div>
            <button type="button" onClick={() => setError(null)} className="text-rose-400 hover:text-rose-600 font-bold">
              ×
            </button>
          </div>
        )}

        {/* Success View */}
        {successData ? (
          <div className="space-y-5 py-4">
            <div className="p-4 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 rounded-lg text-emerald-800 dark:text-emerald-300 space-y-2">
              <div className="flex items-center gap-2 font-semibold text-sm">
                <CheckCircle2 className="w-5 h-5 text-emerald-600" />
                <span>Account Created with Restricted Role (VIEWER)</span>
              </div>
              <p className="text-xs leading-relaxed">{successData.message}</p>
              <div className="text-xs pt-1 font-mono text-emerald-700 dark:text-emerald-400">
                Registered Email: <strong>{successData.email}</strong>
              </div>
            </div>

            {successData.devToken && (
              <div className="p-3 bg-amber-50 dark:bg-amber-950/40 border border-amber-200 dark:border-amber-800 rounded-lg text-xs space-y-2">
                <div className="font-semibold text-amber-900 dark:text-amber-200 flex items-center gap-1.5">
                  <ShieldAlert className="w-4 h-4 text-amber-600" />
                  <span>Development Mode Instant Activation:</span>
                </div>
                <div className="text-[11px] text-slate-600 dark:text-slate-400">
                  Because SMTP email delivery is in development mode, you can verify your account immediately with this token:
                </div>
                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    readOnly
                    value={successData.devToken}
                    className="flex-1 font-mono text-[11px] bg-white dark:bg-slate-800 px-2.5 py-1.5 rounded border border-slate-300 dark:border-slate-700 select-all"
                  />
                  <Link
                    to={`/verify-email?token=${encodeURIComponent(successData.devToken)}`}
                    className="px-3 py-1.5 bg-blue-900 text-white rounded text-xs font-semibold hover:bg-blue-800 whitespace-nowrap"
                  >
                    Auto Verify
                  </Link>
                </div>
              </div>
            )}

            <div className="flex items-center justify-between pt-4 border-t border-slate-200 dark:border-slate-800">
              <Link
                to="/login"
                className="inline-flex items-center gap-2 px-4 py-2 bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold rounded-lg shadow-xs transition"
              >
                <span>Proceed to Sign In</span>
                <ArrowRight className="w-4 h-4" />
              </Link>
              <Link to={`/verify-email`} className="text-xs text-blue-600 hover:underline">
                Enter verification token manually →
              </Link>
            </div>
          </div>
        ) : (
          /* Registration Form */
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {/* Full Name */}
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Full Name *
                </label>
                <div className="relative">
                  <User className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                  <input
                    type="text"
                    required
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    className="w-full pl-9 pr-3 py-2 text-xs rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700 dark:text-slate-100"
                    placeholder="e.g. Captain Rajesh Verma"
                  />
                </div>
              </div>

              {/* Official / Work Email */}
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Email Address *
                </label>
                <div className="relative">
                  <Mail className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                  <input
                    type="email"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full pl-9 pr-3 py-2 text-xs rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700 dark:text-slate-100"
                    placeholder="officer@company.com"
                  />
                </div>
              </div>
            </div>

            {/* Phone Number */}
            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Phone Number (Optional)
              </label>
              <div className="relative">
                <Phone className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                <input
                  type="tel"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                  className="w-full pl-9 pr-3 py-2 text-xs rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700 dark:text-slate-100"
                  placeholder="+91 98765 43210"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {/* Password */}
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Password *
                </label>
                <div className="relative">
                  <Lock className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                  <input
                    type={showPassword ? 'text' : 'password'}
                    required
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full pl-9 pr-9 py-2 text-xs rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700 dark:text-slate-100"
                    placeholder="Min 8 characters"
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

              {/* Confirm Password */}
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Confirm Password *
                </label>
                <div className="relative">
                  <Lock className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
                  <input
                    type={showPassword ? 'text' : 'password'}
                    required
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    className="w-full pl-9 pr-3 py-2 text-xs rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 focus:outline-none focus:ring-1 focus:ring-blue-700 dark:text-slate-100"
                    placeholder="Re-type password"
                  />
                </div>
              </div>
            </div>

            {/* Password Strength Indicator */}
            {password && (
              <div className="p-3 bg-slate-50 dark:bg-slate-800/60 rounded-lg border border-slate-200 dark:border-slate-700/60 space-y-2">
                <div className="flex items-center justify-between text-xs">
                  <span className="text-slate-500 font-medium">Password Strength:</span>
                  <span className="font-semibold text-slate-700 dark:text-slate-300">{getStrengthText()}</span>
                </div>
                <div className="w-full h-1.5 bg-slate-200 dark:bg-slate-700 rounded-full overflow-hidden">
                  <div
                    className={`h-full transition-all duration-300 ${getStrengthColor()}`}
                    style={{ width: `${strengthScore}%` }}
                  />
                </div>
                <div className="grid grid-cols-2 gap-1 text-[11px] text-slate-500 dark:text-slate-400 pt-1">
                  <div className={`flex items-center gap-1 ${hasMinLength ? 'text-emerald-600 font-medium' : ''}`}>
                    <span>{hasMinLength ? '✓' : '•'} 8+ Characters</span>
                  </div>
                  <div className={`flex items-center gap-1 ${hasUpper ? 'text-emerald-600 font-medium' : ''}`}>
                    <span>{hasUpper ? '✓' : '•'} Uppercase Letter</span>
                  </div>
                  <div className={`flex items-center gap-1 ${hasLower ? 'text-emerald-600 font-medium' : ''}`}>
                    <span>{hasLower ? '✓' : '•'} Lowercase Letter</span>
                  </div>
                  <div className={`flex items-center gap-1 ${hasDigit ? 'text-emerald-600 font-medium' : ''}`}>
                    <span>{hasDigit ? '✓' : '•'} Number (0-9)</span>
                  </div>
                  <div className={`flex items-center gap-1 ${hasSpecial ? 'text-emerald-600 font-medium' : ''}`}>
                    <span>{hasSpecial ? '✓' : '•'} Symbol (!@#$%...)</span>
                  </div>
                </div>
              </div>
            )}

            {/* Terms and Policies */}
            <div className="pt-2">
              <label className="flex items-start gap-2.5 cursor-pointer">
                <input
                  type="checkbox"
                  required
                  checked={termsAccepted}
                  onChange={(e) => setTermsAccepted(e.target.checked)}
                  className="mt-0.5 rounded border-slate-300 text-blue-900 focus:ring-blue-700"
                />
                <span className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                  I accept the{' '}
                  <span className="text-blue-600 dark:text-blue-400 font-medium underline">Terms of Service</span> and{' '}
                  <span className="text-blue-600 dark:text-blue-400 font-medium underline">Regulatory Privacy Policy</span>.
                  I understand public accounts receive restricted <strong>VIEWER</strong> access until elevated by an administrator.
                </span>
              </label>
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full py-2.5 px-4 bg-blue-900 hover:bg-blue-800 text-white text-xs font-semibold rounded-lg shadow-xs transition flex items-center justify-center gap-2 disabled:opacity-50 mt-4"
            >
              {loading ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>Creating Account...</span>
                </>
              ) : (
                <>
                  <span>Create Account</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>

            {/* Existing Account Link */}
            <div className="pt-3 text-center text-xs text-slate-500">
              Already have an account?{' '}
              <Link to="/login" className="text-blue-600 dark:text-blue-400 font-semibold hover:underline">
                Sign in here
              </Link>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};

export default RegisterPage;
