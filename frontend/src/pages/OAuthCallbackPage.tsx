import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ShieldCheck, AlertCircle, Loader2 } from 'lucide-react';

export const OAuthCallbackPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { loginWithToken } = useAuth();
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    const token = searchParams.get('token');
    const error = searchParams.get('error');

    if (error) {
      setErrorMessage(error);
      const timer = setTimeout(() => {
        navigate(`/login?error=${encodeURIComponent(error)}`, { replace: true });
      }, 3000);
      return () => clearTimeout(timer);
    }

    if (token) {
      const processToken = async () => {
        try {
          await loginWithToken(token);
          // Navigate based on user role: ANALYST directly to /freight, others to /
          const savedUser = localStorage.getItem('user');
          if (savedUser) {
            try {
              const parsed = JSON.parse(savedUser);
              if (parsed.role === 'ANALYST') {
                navigate('/freight', { replace: true });
                return;
              }
            } catch {}
          }
          navigate('/', { replace: true });
        } catch (err: any) {
          setErrorMessage('Failed to finalize authentication session.');
          setTimeout(() => navigate('/login', { replace: true }), 3000);
        }
      };
      processToken();
    } else {
      navigate('/login', { replace: true });
    }
  }, [searchParams, loginWithToken, navigate]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-950 to-slate-900 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white dark:bg-slate-900 rounded-2xl shadow-2xl border border-slate-200 dark:border-slate-800 p-8 text-center space-y-4">
        <div className="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-gradient-to-tr from-blue-900 to-indigo-700 text-amber-400 shadow-lg mb-2">
          <ShieldCheck className="w-8 h-8" />
        </div>

        <h2 className="text-xl font-bold tracking-tight text-slate-900 dark:text-slate-100">
          RegTech Sentinel
        </h2>

        {errorMessage ? (
          <div className="space-y-3">
            <div className="flex items-center justify-center gap-2 p-3 bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-800 rounded-lg text-xs text-rose-700 dark:text-rose-300">
              <AlertCircle className="w-5 h-5 shrink-0 text-rose-600 dark:text-rose-400" />
              <span>{errorMessage}</span>
            </div>
            <p className="text-xs text-slate-500">Redirecting to login portal...</p>
          </div>
        ) : (
          <div className="space-y-3">
            <div className="flex items-center justify-center gap-3 text-sm text-slate-600 dark:text-slate-300">
              <Loader2 className="w-5 h-5 animate-spin text-blue-600 dark:text-blue-400" />
              <span>Completing Google Single Sign-On...</span>
            </div>
            <p className="text-xs text-slate-500 dark:text-slate-400">
              Verifying security credentials and issuing portal access token
            </p>
          </div>
        )}
      </div>
    </div>
  );
};
