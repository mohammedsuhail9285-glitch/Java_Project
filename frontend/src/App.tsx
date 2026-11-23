import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Layout } from './components/layout/Layout';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { ForgotPasswordPage } from './pages/ForgotPasswordPage';
import { ResetPasswordPage } from './pages/ResetPasswordPage';
import { VerifyEmailPage } from './pages/VerifyEmailPage';
import { ProfilePage } from './pages/ProfilePage';
import { UnauthorizedPage } from './pages/UnauthorizedPage';
import { OAuthCallbackPage } from './pages/OAuthCallbackPage';
import { DashboardPage } from './pages/DashboardPage';
import { ProductListPage } from './pages/ProductListPage';
import { ProductDetailPage } from './pages/ProductDetailPage';
import { ViolationsPage } from './pages/ViolationsPage';
import { RulesPage } from './pages/RulesPage';
import { ReportsPage } from './pages/ReportsPage';
import { AiMonitoringPage } from './pages/AiMonitoringPage';
import { AuditLogPage } from './pages/AuditLogPage';
import { SingleScanPage } from './pages/SingleScanPage';
import { BatchScanPage } from './pages/BatchScanPage';
import { ScanHistoryPage } from './pages/ScanHistoryPage';
import { UserManagementPage } from './pages/UserManagementPage';
import { AnalyticsPage } from './pages/AnalyticsPage';
import { FreightDashboardPage } from './pages/FreightDashboardPage';
import { FreightForecastPage } from './pages/FreightForecastPage';
import { VesselCharteringPage } from './pages/VesselCharteringPage';
import { BulkProcurementPage } from './pages/BulkProcurementPage';

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-100 dark:bg-slate-950">
        <div className="text-xs font-semibold uppercase tracking-wider text-slate-500 animate-pulse">
          Authenticating Sentinel Portal...
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};

export const App: React.FC = () => {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          <Route path="/reset-password" element={<ResetPasswordPage />} />
          <Route path="/verify-email" element={<VerifyEmailPage />} />
          <Route path="/oauth2/callback" element={<OAuthCallbackPage />} />

          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            <Route index element={<DashboardPage />} />
            <Route path="dashboard" element={<DashboardPage />} />
            <Route path="profile" element={<ProfilePage />} />
            <Route path="unauthorized" element={<UnauthorizedPage />} />
            <Route path="freight" element={<FreightDashboardPage />} />
            <Route path="freight/forecasts" element={<FreightForecastPage />} />
            <Route path="freight/chartering" element={<VesselCharteringPage />} />
            <Route path="freight/procurement" element={<BulkProcurementPage />} />
            <Route path="single-scan" element={<SingleScanPage />} />
            <Route path="scans/new" element={<SingleScanPage />} />
            <Route path="batch-scan" element={<BatchScanPage />} />
            <Route path="scan-history" element={<ScanHistoryPage />} />
            <Route path="scans" element={<ScanHistoryPage />} />
            <Route path="products" element={<ProductListPage />} />
            <Route path="products/:id" element={<ProductDetailPage />} />
            <Route path="violations" element={<ViolationsPage />} />
            <Route path="violations/:id" element={<ViolationsPage />} />
            <Route path="ingestion" element={<SingleScanPage />} />
            <Route path="analytics" element={<AnalyticsPage />} />
            <Route path="rules" element={<RulesPage />} />
            <Route path="rules/:id" element={<RulesPage />} />
            <Route path="reports" element={<ReportsPage />} />
            <Route path="users" element={<UserManagementPage />} />
            <Route path="ai-monitoring" element={<AiMonitoringPage />} />
            <Route path="audit-logs" element={<AuditLogPage />} />
            <Route path="audit-log" element={<AuditLogPage />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
};

export default App;
