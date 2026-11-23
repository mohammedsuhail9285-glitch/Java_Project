export type Role = 'ADMIN' | 'REGULATOR' | 'REVIEWER' | 'ANALYST' | 'VIEWER';

export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: Role;
  designation?: string;
  department?: string;
  phone?: string;
  enabled?: boolean;
  emailVerified?: boolean;
  accountStatus?: string;
  authProvider?: string;
  failedLoginAttempts?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  user: User;
  verificationToken?: string;
}

export interface RegisterRequest {
  username?: string;
  email: string;
  password: string;
  confirmPassword?: string;
  fullName: string;
  phone?: string;
  termsAccepted: boolean;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
  confirmPassword?: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword?: string;
}

export interface UpdateProfileRequest {
  fullName: string;
  phone?: string;
}

export interface AuthConfig {
  googleOAuthEnabled: boolean;
  otpDeliveryMode: 'demo' | 'smtp' | 'sms' | string;
  demoMode: boolean;
}

export interface OtpRequestResponse {
  message: string;
  devOtpHint?: string;
  expiresInSeconds: number;
  cooldownSeconds: number;
  deliveryChannel: string;
}

export type ComplianceStatus =
  | 'PASSED_AUTOMATED_CHECKS'
  | 'POTENTIAL_NON_COMPLIANT'
  | 'REQUIRES_REVIEW'
  | 'PENDING_SCAN';

export type Severity = 'CRITICAL' | 'MAJOR' | 'MINOR' | 'INFO';

export type ViolationStatus =
  | 'NEW'
  | 'UNDER_REVIEW'
  | 'CONFIRMED'
  | 'DISMISSED'
  | 'RESOLVED';

export interface Product {
  id: number;
  name: string;
  brandId?: number;
  sellerId?: number;
  categoryId?: number;
  declaredMrp?: number;
  listingPrice?: number;
  description?: string;
  countryOfOrigin?: string;
  productUrl?: string;
  ingestionSource?: string;
  status: ComplianceStatus;
  createdAt: string;
  updatedAt?: string;
  brandName?: string;
  sellerName?: string;
  categoryName?: string;
}

export interface Violation {
  id: number;
  violationId: string;
  productId: number;
  productName?: string;
  ruleId: string;
  ruleName?: string;
  severity: Severity;
  detectedValue: string;
  expectedCondition: string;
  status: ViolationStatus;
  source: 'IMAGE' | 'LISTING';
  ocrConfidence: number;
  detectionConfidence: number;
  timestamp: string;
  evidenceId?: number;
  originalImagePath?: string;
  croppedImagePath?: string;
  ocrText?: string;
  boundingBox?: string;
  sourceUrl?: string;
  reviewerId?: number;
  reviewerUsername?: string;
  reviewerComment?: string;
  resolution?: string;
  resolutionTimestamp?: string;
}

export interface Rule {
  id: number;
  ruleId: string;
  ruleName: string;
  description: string;
  category: string;
  applicableProductCategory: string;
  severity: Severity;
  validationType: string;
  expectedField?: string;
  enabled: boolean;
  version: number;
  source?: string;
  remediationMessage?: string;
}

export interface DashboardSummary {
  totalProducts: number;
  compliantProducts: number;
  potentialNonCompliantProducts: number;
  requiresReviewProducts: number;
  pendingScanProducts?: number;
  overallComplianceRate: number;
  totalViolations: number;
  criticalViolations: number;
  majorViolations: number;
  minorViolations: number;
  totalScans: number;
  activeScans: number;
  categoryRisks: Array<{
    category: string;
    totalProducts: number;
    nonCompliantCount: number;
    complianceRate: number;
    riskLevel: string;
  }>;
  sellerRisks: Array<{
    sellerName: string;
    platform: string;
    totalProducts: number;
    violationCount: number;
    complianceRate: number;
    riskLevel: string;
  }>;
  complianceTrends: Array<{
    date: string;
    complianceRate: number;
    potentialViolations: number;
  }>;
  violationDistribution?: Record<string, number>;
  statutoryDisclaimer: string;
}

export type ScanJobStatus =
  | 'PENDING'
  | 'QUEUED'
  | 'PROCESSING'
  | 'OCR_PROCESSING'
  | 'VALIDATING'
  | 'COMPLETED'
  | 'FAILED';

export interface ScanJob {
  id: number;
  jobType: string;
  status: ScanJobStatus;
  progressPct: number;
  totalItems: number;
  processedItems: number;
  productId?: number;
  batchId?: string;
  startedAt?: string;
  createdAt: string;
  completedAt?: string;
  errorMessage?: string;
}

export interface ExtractedField {
  id: number;
  productId: number;
  fieldType: string;
  rawValue: string;
  normalizedValue?: string;
  unit?: string;
  source: string;
  confidence: number;
  boundingBox?: string;
}

export interface OCRResult {
  id: number;
  rawText: string;
  language: string;
  confidence: number;
  engine: string;
  processedAt: string;
}

// -------------------------------------------------------------
// Maritime Freight & Bulk Cargo Procurement Types (SIH26006)
// -------------------------------------------------------------

export interface MaritimePort {
  id: number;
  portCode: string;
  portName: string;
  country: string;
  coast: string;
  maxDraftMeters: number;
  maxDwt: number;
  maxLoaMeters?: number;
  maxBeamMeters?: number;
  dischargeRateTpd: number;
  averageBerthWaitDays: number;
  congestionIndex: number;
  portTariffPerTon: number;
  tideRestrictionNotes?: string;
  lighterageSupported?: boolean;
  lighterageZone?: string;
  lighterageTariffPerTon?: number;
}

export interface VesselClass {
  id: number;
  classCode: string;
  className: string;
  minDwt: number;
  maxDwt: number;
  maxDraftMeters: number;
  typicalLoaMeters?: number;
  typicalBeamMeters?: number;
  ballastDraftMeters?: number;
  fuelConsumptionTpd: number;
  dailyOperatingCostUsd: number;
  speedKnots: number;
}

export interface TradeRoute {
  id: number;
  routeCode: string;
  originPort: string;
  originCountry: string;
  destinationPort?: MaritimePort;
  commodity: string;
  distanceNauticalMiles: number;
  benchmarkRateUsdPerTon: number;
  originMaxDraftMeters?: number;
  originMaxLoaMeters?: number;
  loadingRateTpd?: number;
}

export interface FreightMarketData {
  id: number;
  recordDate: string;
  bdi: number;
  bci: number;
  bpi: number;
  bsi: number;
  bunkerVlsfoUsd: number;
  usdInrRate: number;
  sentimentScore: number;
  dataSource?: string;
}

export interface FreightForecast {
  id: number;
  tradeRoute?: TradeRoute;
  vesselClassCode: string;
  currentSpotRate: number;
  forecast7d: number;
  forecast14d: number;
  forecast30d: number;
  lowerBound14d: number;
  upperBound14d: number;
  trendDirection: 'BULLISH' | 'BEARISH' | 'STABLE';
  volatilityIndex: 'LOW' | 'MODERATE' | 'HIGH';
  confidenceScore: number;
}

export type CharterDecision = 'CHARTER_NOW' | 'HOLD_WAIT' | 'SPLIT_COA';

export interface ProcurementPlan {
  id: number;
  planReference: string;
  commodity: string;
  quantityTons: number;
  originCountry: string;
  destinationPort?: MaritimePort;
  targetLaycanStart: string;
  targetLaycanEnd: string;
  recommendedVesselClass: string;
  charterDecision: CharterDecision;
  contractType?: string;
  coaDiscountPct?: number;
  coaTotalVolumeTons?: number;
  fobCargoPriceUsdTon: number;
  predictedFreightUsdTon: number;
  bunkerAdjustmentUsdTon: number;
  portChargesUsdTon: number;
  demurrageRiskUsdTon: number;
  lighterageRequired?: boolean;
  lighterageAnchorage?: string;
  lighteredQuantityTons?: number;
  lighterageCostUsdTon?: number;
  totalLandedCostUsdTon: number;
  totalLandedCostInrTon: number;
  estimatedCostSavingsInr: number;
  decisionRationale: string;
  demurrageRiskLevel?: string;
  alternativePortRecommendation?: string;
  volatilityAlertLevel?: string;
  status: string;
  createdAt: string;
}

export interface FreightOverview {
  marketData: FreightMarketData;
  totalRoutes: number;
  totalPorts: number;
  activeProcurementPlans: number;
  totalOptimizedSavingsInr: number;
  eastCoastPorts: MaritimePort[];
  recentPlans: ProcurementPlan[];
}

export interface CharterOptimizationRequest {
  commodity: string;
  quantityTons: number;
  originCountry: string;
  originPortName?: string;
  destinationPortCode: string;
  targetLaycanStart: string;
  targetLaycanEnd: string;
  fobPriceUsd: number;
  contractType?: string;
}

export interface CharterOptimizationResponse {
  planReference: string;
  recommendedVesselClass: string;
  charterDecision: CharterDecision;
  spotRateUsd: number;
  predictedFreightUsdTon: number;
  bunkerAdjustmentUsdTon: number;
  portChargesUsdTon: number;
  demurrageRiskUsdTon: number;
  totalLandedCostUsdTon: number;
  totalLandedCostInrTon: number;
  estimatedCostSavingsInr: number;
  decisionRationale: string;
  portCompatibilityNotice: string;
  portDraftMeters: number;
  vesselMaxDraftMeters: number;
  draftFeasible: boolean;

  portMaxLoaMeters?: number;
  vesselLoaMeters?: number;
  loaFeasible?: boolean;

  portMaxBeamMeters?: number;
  vesselBeamMeters?: number;
  beamFeasible?: boolean;

  contractType?: string;
  spotFreightUsdTon?: number;
  coaFreightUsdTon?: number;
  coaDiscountPct?: number;
  spotTotalLandedInrTon?: number;
  coaTotalLandedInrTon?: number;
  coaNetSavingsInr?: number;
  coaSuitabilityRecommendation?: string;

  lighterageRequired?: boolean;
  lighterageAnchorage?: string;
  lighteredQuantityTons?: number;
  directDischargeQuantityTons?: number;
  lighterageCostUsdTon?: number;
  lighterageOperationsNotes?: string;

  estimatedBerthWaitDays?: number;
  dailyDemurrageRateUsd?: number;
  projectedDemurrageUsd?: number;
  demurrageRiskLevel?: string;
  alternativePortRecommendation?: string;

  volatilityAlertLevel?: string;
  earlyWarningMessage?: string;
  confidenceInterval95?: string;
  dataSource?: string;
}

export interface ModelAccuracyMetrics {
  model_name: string;
  problem_statement: string;
  evaluation_timestamp: string;
  dataset: {
    name: string;
    total_trading_days: number;
    train_days: number;
    test_holdout_days: number;
    evaluated_forecast_windows: number;
    forecast_horizon_days: number;
  };
  metrics: {
    mae_usd_per_ton: number;
    rmse_usd_per_ton: number;
    mape_percent: number;
    directional_accuracy_percent: number;
    ci_95_empirical_coverage_percent: number;
    r_squared: number;
  };
  benchmark_comparison: {
    naive_random_walk_mape: number;
    classical_arima_mape: number;
    sih26006_ensemble_mape: number;
    accuracy_improvement_vs_naive_pct: number;
  };
}
