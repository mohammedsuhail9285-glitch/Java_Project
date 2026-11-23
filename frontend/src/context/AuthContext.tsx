import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, Role } from '../types';
import { authApi } from '../api/authApi';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (u: string, p: string) => Promise<void>;
  loginWithOtp: (identifier: string, code: string) => Promise<void>;
  loginWithToken: (token: string, user?: User) => Promise<void>;
  logout: () => Promise<void>;
  hasRole: (...roles: Role[]) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    try {
      const saved = localStorage.getItem('user');
      if (!saved || saved === 'undefined' || saved === 'null') return null;
      return JSON.parse(saved);
    } catch {
      localStorage.removeItem('user');
      return null;
    }
  });
  const [token, setToken] = useState<string | null>(() => {
    const saved = localStorage.getItem('token');
    if (!saved || saved === 'undefined' || saved === 'null') {
      localStorage.removeItem('token');
      return null;
    }
    return saved;
  });
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const initAuth = async () => {
      const storedToken = localStorage.getItem('token');
      if (storedToken && storedToken !== 'undefined' && storedToken !== 'null') {
        try {
          const currentUser = await authApi.getCurrentUser();
          setUser(currentUser);
          localStorage.setItem('user', JSON.stringify(currentUser));
        } catch (err) {
          console.error('Session expired or invalid token', err);
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          setUser(null);
          setToken(null);
        }
      } else {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      }
      setIsLoading(false);
    };

    initAuth();
  }, []);

  const login = async (u: string, p: string) => {
    const resp: any = await authApi.login(u, p);
    const resolvedToken = resp?.accessToken || resp?.token || resp?.data?.accessToken;
    const resolvedUser = resp?.user || resp?.data?.user;
    if (!resolvedToken) {
      throw new Error('Authentication failed: No access token received.');
    }
    setToken(resolvedToken);
    setUser(resolvedUser);
    localStorage.setItem('token', resolvedToken);
    if (resolvedUser) {
      localStorage.setItem('user', JSON.stringify(resolvedUser));
    }
  };

  const loginWithOtp = async (identifier: string, code: string) => {
    const resp: any = await authApi.verifyOtp(identifier, code);
    const resolvedToken = resp?.accessToken || resp?.token || resp?.data?.accessToken;
    const resolvedUser = resp?.user || resp?.data?.user;
    if (!resolvedToken) {
      throw new Error('OTP verification failed: No access token received.');
    }
    setToken(resolvedToken);
    setUser(resolvedUser);
    localStorage.setItem('token', resolvedToken);
    if (resolvedUser) {
      localStorage.setItem('user', JSON.stringify(resolvedUser));
    }
  };

  const loginWithToken = async (newToken: string, directUser?: User) => {
    setToken(newToken);
    localStorage.setItem('token', newToken);
    if (directUser) {
      setUser(directUser);
      localStorage.setItem('user', JSON.stringify(directUser));
    } else {
      try {
        const currentUser = await authApi.getCurrentUser();
        setUser(currentUser);
        localStorage.setItem('user', JSON.stringify(currentUser));
      } catch (err) {
        console.error('Failed to load user with provided token', err);
      }
    }
  };

  const logout = async () => {
    await authApi.logout();
    setToken(null);
    setUser(null);
  };

  const hasRole = (...roles: Role[]) => {
    if (!user) return false;
    return roles.includes(user.role);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!user && !!token,
        isLoading,
        login,
        loginWithOtp,
        loginWithToken,
        logout,
        hasRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
