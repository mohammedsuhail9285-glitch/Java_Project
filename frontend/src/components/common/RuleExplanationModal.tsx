import React from 'react';
import { ShieldCheck, BookOpen, AlertTriangle, X } from 'lucide-react';
import { SeverityBadge } from './SeverityBadge';

interface RuleExplanationModalProps {
  ruleId: string | null;
  onClose: () => void;
}

const STATUTORY_RULES: Record<
  string,
  {
    name: string;
    citation: string;
    severity: 'CRITICAL' | 'MAJOR' | 'MODERATE' | 'MINOR';
    category: string;
    description: string;
    remediation: string;
  }
> = {
  LM001: {
    name: 'Manufacturer / Packer Declaration Missing',
    citation: 'Rule 6(1)(a) of LMPC Rules, 2011',
    severity: 'CRITICAL',
    category: 'IDENTITY',
    description:
      'Every pre-packaged commodity must prominently bear the complete name and address of the manufacturer, packer, or importer on the principal display panel.',
    remediation:
      'Ensure the manufacturer or packer name and complete registered postal address is legibly declared.',
  },
  LM002: {
    name: 'Consumer Care Contact Details Missing',
    citation: 'Rule 6(1)(h) of LMPC Rules, 2011',
    severity: 'MAJOR',
    category: 'CONSUMER_RIGHTS',
    description:
      'The package must declare the name, address, telephone number, and email address of the person or office that can be contacted in case of consumer complaints.',
    remediation:
      'Provide valid telephone numbers (toll-free where applicable) and dedicated email contacts for consumer grievances.',
  },
  LM003: {
    name: 'Net Quantity Declaration Missing / Non-Standard Unit',
    citation: 'Rule 6(1)(c) & Rule 11 of LMPC Rules, 2011',
    severity: 'CRITICAL',
    category: 'QUANTITY',
    description:
      'The net quantity in terms of standard unit of weight or measure (SI units: g, kg, ml, l, m) or number must be stated on the package label without non-standard qualifiers.',
    remediation:
      'Use strictly Schedule II metric units. Remove expressions like "when packed", "jumbo size", or non-metric designations.',
  },
  LM004: {
    name: 'Month and Year of Manufacture / Packing Missing',
    citation: 'Rule 6(1)(d) of LMPC Rules, 2011',
    severity: 'MAJOR',
    category: 'DATE',
    description:
      'Every package must declare the month and year in which the commodity is manufactured, packed, or pre-packed.',
    remediation:
      'Clearly imprint month and year of packing using format MM/YYYY or Month YYYY.',
  },
  LM005: {
    name: 'Country of Origin Declaration Omitted',
    citation: 'Rule 6(1)(n) of LMPC Rules, 2011',
    severity: 'CRITICAL',
    category: 'ORIGIN',
    description:
      'For imported products as well as domestic goods sold on e-commerce marketplaces, the country of origin must be stated explicitly.',
    remediation:
      'Clearly specify Country of Origin on both digital listing and physical packaging.',
  },
  LM006: {
    name: 'Unit Sale Price (USP) Omission or Miscalculation',
    citation: 'Rule 6(10) of LMPC Rules, 2011 (Amended 2021)',
    severity: 'MAJOR',
    category: 'PRICING',
    description:
      'Package declaring net quantity exceeding 1 kg/1 L must declare Unit Sale Price in terms of per gram / per kilogram / per milliliter / per liter rounded to two decimal places.',
    remediation:
      'Calculate USP = Maximum Retail Price divided by total standard unit quantity, formatted as Rs. XX.XX per kg/g/L/ml.',
  },
  LM007: {
    name: 'Net Quantity Discrepancy: Listing vs Physical Package',
    citation: 'Section 18 read with Section 36 of Legal Metrology Act, 2009',
    severity: 'MAJOR',
    category: 'CROSS_SOURCE',
    description:
      'The net quantity advertised on the marketplace page must match precisely the net quantity marked on the physical commodity package.',
    remediation:
      'Reconcile e-commerce product title and specification attributes with the physical label statement.',
  },
  LM008: {
    name: 'Retail Price Mismatch: Listing vs Physical Package',
    citation: 'Section 18 read with Section 36 of Legal Metrology Act, 2009',
    severity: 'CRITICAL',
    category: 'PRICING',
    description:
      'The maximum retail price (MRP) displayed on the digital platform must not exceed or diverge from the MRP marked on the physical package. Selling above physical package MRP is an offence under Section 36.',
    remediation:
      'Update listing price immediately so it does not exceed or diverge deceptively from marked physical package MRP.',
  },
  LM009: {
    name: 'Country of Origin Contradiction',
    citation: 'Rule 6(1)(n) of LMPC Rules, 2011',
    severity: 'CRITICAL',
    category: 'ORIGIN',
    description:
      'The country of origin displayed on the e-commerce product page contradicts the origin declaration detected on the physical package label.',
    remediation:
      'Ensure catalog metadata originates strictly from the certified physical importer/manufacturer declaration.',
  },
  LM010: {
    name: 'Mandatory Declaration Omission on Digital Listing',
    citation: 'Consumer Protection (E-Commerce) Rules, 2020 & Rule 6(1)',
    severity: 'MAJOR',
    category: 'LISTING_OMISSION',
    description:
      'E-commerce entities must display all statutory declarations (MRP, Net Qty, Origin, Expiry, Importer) on the digital product detail page prior to purchase.',
    remediation:
      'Populate all statutory specification fields in the marketplace merchant catalog.',
  },
};

export const RuleExplanationModal: React.FC<RuleExplanationModalProps> = ({ ruleId, onClose }) => {
  if (!ruleId) return null;
  const rule = STATUTORY_RULES[ruleId] || {
    name: `Statutory Rule ${ruleId}`,
    citation: 'Legal Metrology (Packaged Commodities) Rules, 2011',
    severity: 'MAJOR',
    category: 'STATUTORY',
    description: 'Statutory verification rule under Legal Metrology Act, 2009.',
    remediation: 'Review packaging declaration against standard LMPC requirements.',
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-xs z-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white dark:bg-slate-900 rounded-2xl shadow-2xl border border-slate-200 dark:border-slate-800 p-6 space-y-4">
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-2">
            <div className="w-9 h-9 rounded-xl bg-blue-50 dark:bg-blue-950/60 text-blue-600 flex items-center justify-center">
              <BookOpen className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="font-mono text-xs font-bold text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-950/40 px-2 py-0.5 rounded">
                  {ruleId}
                </span>
                <SeverityBadge severity={rule.severity} />
              </div>
              <h3 className="text-sm font-bold text-slate-900 dark:text-slate-100 mt-1">
                {rule.name}
              </h3>
            </div>
          </div>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-300 p-1"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="space-y-3 text-xs">
          <div className="bg-slate-50 dark:bg-slate-800/60 p-3 rounded-lg border border-slate-100 dark:border-slate-800">
            <span className="font-semibold text-slate-500 uppercase tracking-wider block text-[10px] mb-1">
              Legal Citation & Mandate
            </span>
            <span className="font-mono font-bold text-slate-800 dark:text-slate-200">
              {rule.citation}
            </span>
          </div>

          <div>
            <span className="font-semibold text-slate-500 uppercase tracking-wider block text-[10px] mb-1">
              Statutory Requirement
            </span>
            <p className="text-slate-700 dark:text-slate-300 leading-relaxed">
              {rule.description}
            </p>
          </div>

          <div className="bg-amber-50 dark:bg-amber-950/30 border border-amber-200 dark:border-amber-800/60 p-3 rounded-lg text-amber-800 dark:text-amber-300">
            <span className="font-semibold uppercase tracking-wider block text-[10px] mb-1 flex items-center gap-1">
              <AlertTriangle className="w-3.5 h-3.5 text-amber-600" />
              Remediation Action Required
            </span>
            <p className="leading-relaxed">
              {rule.remediation}
            </p>
          </div>
        </div>

        <div className="pt-2 flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 text-xs font-semibold rounded-lg transition"
          >
            Close Explanation
          </button>
        </div>
      </div>
    </div>
  );
};
