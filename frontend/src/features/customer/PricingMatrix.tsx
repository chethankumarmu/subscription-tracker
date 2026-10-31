import React, { useState, useEffect } from 'react';
import { Plan, Customer } from '../../types';
import { api } from '../../services/api';
import { formatCurrency } from '../../utils/formatters';
import { CheckoutModal } from './CheckoutModal';
import { Check, X, Sparkles, HelpCircle } from 'lucide-react';

interface PricingMatrixProps {
  currency: 'INR' | 'USD';
  onNotification: (title: string, message?: string) => void;
}

export const PricingMatrix: React.FC<PricingMatrixProps> = ({
  currency,
  onNotification,
}) => {
  const [plans, setPlans] = useState<Plan[]>([]);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [billingCycle, setBillingCycle] = useState<'MONTHLY' | 'YEARLY'>('MONTHLY');
  const [loading, setLoading] = useState(true);
  const [selectedPlanForCheckout, setSelectedPlanForCheckout] = useState<Plan | null>(null);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [plansData, custData] = await Promise.all([
        api.getPlans(),
        api.getCustomers(),
      ]);
      setPlans(plansData);
      setCustomers(custData);
    } catch (err) {
      console.error('Error fetching plans:', err);
    } finally {
      setLoading(false);
    }
  };

  const getPrice = (plan: Plan) => {
    return billingCycle === 'YEARLY' ? plan.yearlyPricePaise : plan.monthlyPricePaise;
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '80px 0' }}>
        <div
          style={{
            width: '40px',
            height: '40px',
            borderRadius: '50%',
            border: '3px solid var(--border-color)',
            borderTopColor: 'var(--indigo-500)',
            animation: 'spin 1s infinite linear',
            margin: '0 auto 16px',
          }}
        />
        <p style={{ color: 'var(--text-muted)' }}>Loading real plans from PostgreSQL database...</p>
        <style>{`@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }`}</style>
      </div>
    );
  }

  const starterPlan = plans.find((p) => p.code.toUpperCase().includes('STARTER')) || plans[0];
  const proPlan = plans.find((p) => p.code.toUpperCase().includes('PRO')) || plans[1] || plans[0];
  const enterprisePlan = plans.find((p) => p.code.toUpperCase().includes('ENTERPRISE')) || plans[2] || plans[0];

  return (
    <div>
      {/* Header section matching Image 1: Compare plans with clarity */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'flex-start',
          marginBottom: '36px',
          flexWrap: 'wrap',
          gap: '20px',
        }}
      >
        <div>
          <h1
            style={{
              fontSize: '34px',
              fontWeight: 800,
              letterSpacing: '-0.03em',
              marginBottom: '8px',
            }}
          >
            Compare plans with clarity
          </h1>
          <p
            style={{
              fontSize: '15px',
              color: 'var(--text-secondary)',
            }}
          >
            A polished feature matrix for SaaS pricing, service tiers, package comparisons, and premium offers.
          </p>
        </div>

        {/* Monthly / Yearly Toggle matching Image 1 */}
        <div
          style={{
            display: 'inline-flex',
            alignItems: 'center',
            background: 'var(--bg-card)',
            border: '1px solid var(--border-color)',
            borderRadius: 'var(--radius-pill)',
            padding: '4px',
            boxShadow: 'var(--shadow-sm)',
          }}
        >
          <button
            onClick={() => setBillingCycle('MONTHLY')}
            style={{
              border: 'none',
              background: billingCycle === 'MONTHLY' ? 'var(--text-primary)' : 'transparent',
              color: billingCycle === 'MONTHLY' ? 'var(--bg-card)' : 'var(--text-secondary)',
              padding: '8px 22px',
              borderRadius: 'var(--radius-pill)',
              fontWeight: 600,
              fontSize: '13px',
              cursor: 'pointer',
              transition: 'all 200ms ease',
            }}
          >
            Monthly
          </button>
          <button
            onClick={() => setBillingCycle('YEARLY')}
            style={{
              border: 'none',
              background: billingCycle === 'YEARLY' ? 'var(--text-primary)' : 'transparent',
              color: billingCycle === 'YEARLY' ? 'var(--bg-card)' : 'var(--text-secondary)',
              padding: '8px 22px',
              borderRadius: 'var(--radius-pill)',
              fontWeight: 600,
              fontSize: '13px',
              cursor: 'pointer',
              transition: 'all 200ms ease',
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
            }}
          >
            Yearly
          </button>
        </div>
      </div>

      {/* Top 4-Column Cards Row matching Image 1: Features | Starter | Pro | Enterprise */}
      <div className="pricing-matrix-grid" style={{ marginBottom: '24px' }}>
        {/* Card 1: Features Anchor Card */}
        <div
          className="card"
          style={{
            padding: '32px 28px',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'flex-end',
            background: 'var(--bg-card)',
            borderRadius: '24px',
          }}
        >
          <div>
            <h2 style={{ fontSize: '24px', fontWeight: 800, marginBottom: '8px', letterSpacing: '-0.02em' }}>
              Features
            </h2>
            <p style={{ fontSize: '13px', color: 'var(--text-secondary)', lineHeight: '1.4' }}>
              Compare what each plan unlocks at a glance.
            </p>
          </div>
        </div>

        {/* Card 2: Starter Card matching Image 1 */}
        {starterPlan && (
          <div
            className="card"
            style={{
              padding: '32px 28px',
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'space-between',
              borderRadius: '24px',
            }}
          >
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
                <h3 style={{ fontSize: '20px', fontWeight: 800 }}>{starterPlan.name}</h3>
                <span
                  style={{
                    background: 'var(--violet-50)',
                    color: 'var(--indigo-600)',
                    fontSize: '10px',
                    fontWeight: 800,
                    letterSpacing: '0.05em',
                    padding: '3px 10px',
                    borderRadius: 'var(--radius-pill)',
                  }}
                >
                  FREE
                </span>
              </div>
              <p style={{ fontSize: '12px', color: 'var(--text-muted)', marginBottom: '20px' }}>
                For personal projects
              </p>

              {/* Price */}
              <div style={{ display: 'flex', alignItems: 'baseline', gap: '4px', marginBottom: '16px' }}>
                <span style={{ fontSize: '40px', fontWeight: 800, letterSpacing: '-0.03em' }}>
                  $0
                </span>
                <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>/mo</span>
              </div>

              <p style={{ fontSize: '12px', color: 'var(--text-secondary)', lineHeight: '1.4', marginBottom: '28px' }}>
                Perfect for trying the product, shipping a first project, and exploring the basics.
              </p>
            </div>

            <button
              onClick={() => setSelectedPlanForCheckout(starterPlan)}
              className="btn-secondary"
              style={{ width: '100%', padding: '12px', fontSize: '13px', justifyContent: 'center' }}
            >
              Start Free
            </button>
          </div>
        )}

        {/* Card 3: Pro Card (Recommended) matching Image 1 */}
        {proPlan && (
          <div
            className="card"
            style={{
              padding: '32px 28px',
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'space-between',
              borderRadius: '24px',
              position: 'relative',
              border: '2px solid rgba(124, 58, 237, 0.6)',
              boxShadow: 'var(--shadow-violet-glow)',
              background: 'linear-gradient(180deg, rgba(245, 243, 255, 0.8) 0%, var(--bg-card) 40%)',
            }}
          >
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
                <h3 style={{ fontSize: '20px', fontWeight: 800 }}>{proPlan.name}</h3>
                <span
                  style={{
                    background: 'linear-gradient(135deg, var(--indigo-500), var(--violet-600))',
                    color: '#ffffff',
                    fontSize: '10px',
                    fontWeight: 800,
                    letterSpacing: '0.05em',
                    padding: '3px 10px',
                    borderRadius: 'var(--radius-pill)',
                  }}
                >
                  RECOMMENDED
                </span>
              </div>
              <p style={{ fontSize: '12px', color: 'var(--text-muted)', marginBottom: '20px' }}>
                For growing teams
              </p>

              {/* Price */}
              <div style={{ display: 'flex', alignItems: 'baseline', gap: '4px', marginBottom: '16px' }}>
                <span style={{ fontSize: '40px', fontWeight: 800, letterSpacing: '-0.03em', color: 'var(--indigo-600)' }}>
                  {currency === 'USD' ? '$29' : formatCurrency(getPrice(proPlan), 'INR', false)}
                </span>
                <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
                  /{billingCycle === 'YEARLY' ? 'yr' : 'mo'}
                </span>
              </div>

              <p style={{ fontSize: '12px', color: 'var(--text-secondary)', lineHeight: '1.4', marginBottom: '28px' }}>
                Best for serious use with automation, collaboration, analytics, and priority support.
              </p>
            </div>

            <button
              onClick={() => setSelectedPlanForCheckout(proPlan)}
              className="btn-primary"
              style={{
                width: '100%',
                padding: '12px',
                fontSize: '13px',
                justifyContent: 'center',
                background: 'linear-gradient(135deg, #7c3aed, #6366f1)',
              }}
            >
              Choose Pro
            </button>
          </div>
        )}

        {/* Card 4: Enterprise Card matching Image 1 */}
        {enterprisePlan && (
          <div
            className="card"
            style={{
              padding: '32px 28px',
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'space-between',
              borderRadius: '24px',
            }}
          >
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
                <h3 style={{ fontSize: '20px', fontWeight: 800 }}>{enterprisePlan.name}</h3>
                <span
                  style={{
                    background: 'var(--violet-50)',
                    color: 'var(--indigo-600)',
                    fontSize: '10px',
                    fontWeight: 800,
                    letterSpacing: '0.05em',
                    padding: '3px 10px',
                    borderRadius: 'var(--radius-pill)',
                  }}
                >
                  CUSTOM
                </span>
              </div>
              <p style={{ fontSize: '12px', color: 'var(--text-muted)', marginBottom: '20px' }}>
                For advanced organizations
              </p>

              {/* Price */}
              <div style={{ display: 'flex', alignItems: 'baseline', gap: '4px', marginBottom: '16px' }}>
                <span style={{ fontSize: '40px', fontWeight: 800, letterSpacing: '-0.03em' }}>
                  Custom
                </span>
              </div>

              <p style={{ fontSize: '12px', color: 'var(--text-secondary)', lineHeight: '1.4', marginBottom: '28px' }}>
                Security, governance, onboarding, and custom limits tailored for larger businesses.
              </p>
            </div>

            <button
              onClick={() => setSelectedPlanForCheckout(enterprisePlan)}
              className="btn-secondary"
              style={{ width: '100%', padding: '12px', fontSize: '13px', justifyContent: 'center' }}
            >
              Talk to Sales
            </button>
          </div>
        )}
      </div>

      {/* CORE FEATURES Section Pill Banner matching Image 1 */}
      <div
        style={{
          background: 'var(--bg-card)',
          border: '1px solid var(--border-color)',
          borderRadius: 'var(--radius-pill)',
          padding: '12px 28px',
          marginBottom: '16px',
        }}
      >
        <span style={{ fontSize: '11px', fontWeight: 800, letterSpacing: '0.08em', color: 'var(--text-muted)', textTransform: 'uppercase' }}>
          CORE FEATURES
        </span>
      </div>

      {/* 4-Column Feature Comparison Rows matching Image 1 */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', marginBottom: '48px' }}>
        {/* Row 1: Projects */}
        <div className="pricing-matrix-grid" style={{ alignItems: 'center' }}>
          <div className="card" style={{ padding: '18px 24px', borderRadius: '16px' }}>
            <div style={{ fontWeight: 700, fontSize: '14px' }}>Projects</div>
            <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Workspaces and active projects</div>
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px', fontWeight: 600, fontSize: '14px' }}>
            {starterPlan?.featureMap?.projects ?? 1}
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px', fontWeight: 700, fontSize: '14px', color: 'var(--indigo-600)' }}>
            Unlimited
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px', fontWeight: 700, fontSize: '14px', color: 'var(--indigo-600)' }}>
            Unlimited
          </div>
        </div>

        {/* Row 2: Storage */}
        <div className="pricing-matrix-grid" style={{ alignItems: 'center' }}>
          <div className="card" style={{ padding: '18px 24px', borderRadius: '16px' }}>
            <div style={{ fontWeight: 700, fontSize: '14px' }}>Storage</div>
            <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Included file and asset storage</div>
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px', fontWeight: 600, fontSize: '14px' }}>
            {starterPlan?.featureMap?.storage_gb ?? 5} GB
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px', fontWeight: 600, fontSize: '14px' }}>
            250 GB
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px', fontWeight: 600, fontSize: '14px' }}>
            Custom
          </div>
        </div>

        {/* Row 3: Custom domains */}
        <div className="pricing-matrix-grid" style={{ alignItems: 'center' }}>
          <div className="card" style={{ padding: '18px 24px', borderRadius: '16px' }}>
            <div style={{ fontWeight: 700, fontSize: '14px' }}>Custom domains</div>
            <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Attach your own branded domains</div>
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px', fontSize: '13px' }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: '#ef4444', fontWeight: 600 }}>
              <span style={{ width: '16px', height: '16px', borderRadius: '50%', background: '#fee2e2', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '10px' }}>✕</span>
              Not included
            </span>
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px', fontSize: '13px' }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: 'var(--emerald-600)', fontWeight: 600 }}>
              <span style={{ width: '16px', height: '16px', borderRadius: '50%', background: 'var(--emerald-50)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '10px' }}>✓</span>
              Included
            </span>
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px', fontSize: '13px' }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: 'var(--emerald-600)', fontWeight: 600 }}>
              <span style={{ width: '16px', height: '16px', borderRadius: '50%', background: 'var(--emerald-50)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '10px' }}>✓</span>
              Included
            </span>
          </div>
        </div>

        {/* Row 4: Templates */}
        <div className="pricing-matrix-grid" style={{ alignItems: 'center' }}>
          <div className="card" style={{ padding: '18px 24px', borderRadius: '16px' }}>
            <div style={{ fontWeight: 700, fontSize: '14px' }}>Templates</div>
            <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Starting points for faster setup</div>
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px' }}>
            <span
              style={{
                background: 'var(--violet-100)',
                color: 'var(--indigo-600)',
                fontSize: '11px',
                fontWeight: 700,
                letterSpacing: '0.04em',
                padding: '4px 12px',
                borderRadius: 'var(--radius-pill)',
              }}
            >
              BASIC LIBRARY
            </span>
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px' }}>
            <span
              style={{
                background: 'var(--violet-100)',
                color: 'var(--indigo-600)',
                fontSize: '11px',
                fontWeight: 700,
                letterSpacing: '0.04em',
                padding: '4px 12px',
                borderRadius: 'var(--radius-pill)',
              }}
            >
              PRO LIBRARY
            </span>
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px' }}>
            <span
              style={{
                background: 'var(--violet-100)',
                color: 'var(--indigo-600)',
                fontSize: '11px',
                fontWeight: 700,
                letterSpacing: '0.04em',
                padding: '4px 12px',
                borderRadius: 'var(--radius-pill)',
              }}
            >
              FULL LIBRARY
            </span>
          </div>
        </div>

        {/* Row 5: Priority Support & SLAs */}
        <div className="pricing-matrix-grid" style={{ alignItems: 'center' }}>
          <div className="card" style={{ padding: '18px 24px', borderRadius: '16px' }}>
            <div style={{ fontWeight: 700, fontSize: '14px' }}>Priority Support</div>
            <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Response time and dedicated SLAs</div>
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px', fontSize: '13px', color: 'var(--text-muted)' }}>
            Community only
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px', fontSize: '13px' }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: 'var(--emerald-600)', fontWeight: 600 }}>
              <Check size={14} /> 24/7 Priority
            </span>
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px', fontSize: '13px' }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: 'var(--emerald-600)', fontWeight: 600 }}>
              <Check size={14} /> Dedicated Manager
            </span>
          </div>
        </div>

        {/* Row 6: Pause Subscription */}
        <div className="pricing-matrix-grid" style={{ alignItems: 'center' }}>
          <div className="card" style={{ padding: '18px 24px', borderRadius: '16px' }}>
            <div style={{ fontWeight: 700, fontSize: '14px' }}>Pause Subscription</div>
            <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Flexible billing hold periods</div>
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px', fontSize: '13px' }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: '#ef4444', fontWeight: 600 }}>
              <span style={{ width: '16px', height: '16px', borderRadius: '50%', background: '#fee2e2', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '10px' }}>✕</span>
              Not included
            </span>
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px', fontSize: '13px' }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: 'var(--emerald-600)', fontWeight: 600 }}>
              <Check size={14} /> Up to 90 days
            </span>
          </div>
          <div className="card" style={{ padding: '18px 24px', textAlign: 'center', borderRadius: '16px', fontSize: '13px' }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: 'var(--emerald-600)', fontWeight: 600 }}>
              <Check size={14} /> Flexible
            </span>
          </div>
        </div>
      </div>

      {/* Checkout Modal */}
      {selectedPlanForCheckout && (
        <CheckoutModal
          plan={selectedPlanForCheckout}
          billingCycle={billingCycle}
          customers={customers}
          currency={currency}
          onClose={() => setSelectedPlanForCheckout(null)}
          onSuccess={(msg) => {
            onNotification('Subscription Activated', msg);
            loadData();
          }}
        />
      )}
    </div>
  );
};
