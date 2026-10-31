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

  return (
    <div>
      {/* Header section matching Image 1 */}
      <div style={{ textAlign: 'center', marginBottom: '40px' }}>
        <h1
          style={{
            fontSize: '36px',
            fontWeight: 800,
            letterSpacing: '-0.03em',
            marginBottom: '12px',
          }}
        >
          Compare Plans with Clarity
        </h1>
        <p
          style={{
            fontSize: '16px',
            color: 'var(--text-secondary)',
            maxWidth: '600px',
            margin: '0 auto 28px',
          }}
        >
          Choose a transparent plan that seamlessly scales with your team's workflow and recurring billing.
        </p>

        {/* Monthly / Yearly Toggle */}
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
              background: billingCycle === 'MONTHLY' ? 'var(--indigo-500)' : 'transparent',
              color: billingCycle === 'MONTHLY' ? '#ffffff' : 'var(--text-secondary)',
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
              background: billingCycle === 'YEARLY' ? 'var(--indigo-500)' : 'transparent',
              color: billingCycle === 'YEARLY' ? '#ffffff' : 'var(--text-secondary)',
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
            <span
              style={{
                background: billingCycle === 'YEARLY' ? '#ffffff' : 'var(--emerald-50)',
                color: billingCycle === 'YEARLY' ? 'var(--indigo-600)' : 'var(--emerald-600)',
                fontSize: '10px',
                fontWeight: 700,
                padding: '2px 8px',
                borderRadius: 'var(--radius-pill)',
              }}
            >
              SAVE 20%
            </span>
          </button>
        </div>
      </div>

      {/* 3 Plan Cards matching Image 1 */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
          gap: '28px',
          marginBottom: '56px',
        }}
      >
        {plans.map((plan) => {
          const isPro = plan.code.toUpperCase().includes('PRO');
          const isStarter = plan.code.toUpperCase().includes('STARTER');
          const pricePaise = getPrice(plan);

          return (
            <div
              key={plan.id}
              className="card"
              style={{
                padding: '36px 32px',
                position: 'relative',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between',
                borderColor: isPro ? 'var(--indigo-500)' : 'var(--border-color)',
                boxShadow: isPro ? 'var(--shadow-violet-glow)' : 'var(--shadow-card)',
                transform: isPro ? 'scale(1.02)' : 'none',
              }}
            >
              {/* Recommended Badge for Pro */}
              {isPro && (
                <div
                  style={{
                    position: 'absolute',
                    top: '-14px',
                    left: '50%',
                    transform: 'translateX(-50%)',
                    background: 'linear-gradient(135deg, var(--indigo-500), var(--violet-600))',
                    color: '#ffffff',
                    fontSize: '11px',
                    fontWeight: 800,
                    letterSpacing: '0.08em',
                    textTransform: 'uppercase',
                    padding: '4px 16px',
                    borderRadius: 'var(--radius-pill)',
                    boxShadow: '0 4px 12px rgba(99, 102, 241, 0.4)',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '4px',
                  }}
                >
                  <Sparkles size={12} /> Recommended
                </div>
              )}

              <div>
                <h3 style={{ fontSize: '20px', fontWeight: 700, marginBottom: '6px' }}>
                  {plan.name}
                </h3>
                <p style={{ fontSize: '13px', color: 'var(--text-secondary)', marginBottom: '24px' }}>
                  {isStarter
                    ? 'Perfect for startups and individual experiments.'
                    : isPro
                    ? 'Best for fast-growing companies and multi-user teams.'
                    : 'For enterprises requiring dedicated infrastructure and SLAs.'}
                </p>

                {/* Price Display */}
                <div style={{ display: 'flex', alignItems: 'baseline', gap: '6px', marginBottom: '28px' }}>
                  <span style={{ fontSize: '38px', fontWeight: 800, letterSpacing: '-0.03em' }}>
                    {pricePaise === 0 ? 'Free' : formatCurrency(pricePaise, currency, false)}
                  </span>
                  {pricePaise > 0 && (
                    <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
                      /{billingCycle === 'YEARLY' ? 'year' : 'month'}
                    </span>
                  )}
                </div>

                {/* Top Features */}
                <ul style={{ listStyle: 'none', padding: 0, marginBottom: '32px' }}>
                  <li style={{ display: 'flex', alignItems: 'center', gap: '10px', fontSize: '13px', marginBottom: '12px' }}>
                    <Check size={16} color="var(--emerald-500)" />
                    <span>
                      {plan.featureMap?.projects === -1
                        ? 'Unlimited Projects'
                        : `${plan.featureMap?.projects || 3} Projects included`}
                    </span>
                  </li>
                  <li style={{ display: 'flex', alignItems: 'center', gap: '10px', fontSize: '13px', marginBottom: '12px' }}>
                    <Check size={16} color="var(--emerald-500)" />
                    <span>{plan.featureMap?.storage_gb || 5} GB Cloud NVMe Storage</span>
                  </li>
                  <li style={{ display: 'flex', alignItems: 'center', gap: '10px', fontSize: '13px', marginBottom: '12px' }}>
                    <Check size={16} color="var(--emerald-500)" />
                    <span>
                      {plan.featureMap?.custom_domains === 0
                        ? 'No custom domains'
                        : plan.featureMap?.custom_domains === -1
                        ? 'Unlimited Custom Domains'
                        : `${plan.featureMap?.custom_domains} Custom Domains`}
                    </span>
                  </li>
                  <li style={{ display: 'flex', alignItems: 'center', gap: '10px', fontSize: '13px', marginBottom: '12px' }}>
                    <Check size={16} color="var(--emerald-500)" />
                    <span>{isStarter ? 'Basic Library' : isPro ? 'Pro Library (50+)' : 'Full Enterprise Library'}</span>
                  </li>
                </ul>
              </div>

              {/* Action Button */}
              <button
                onClick={() => setSelectedPlanForCheckout(plan)}
                className={isPro ? 'btn-primary' : 'btn-secondary'}
                style={{ width: '100%', padding: '12px', fontSize: '14px' }}
              >
                {isStarter ? 'Get Started Free' : isPro ? 'Choose Pro' : 'Choose Enterprise'}
              </button>
            </div>
          );
        })}
      </div>

      {/* Comparison Matrix Table matching Image 1 */}
      <div className="card" style={{ padding: '32px', overflowX: 'auto' }}>
        <h3 style={{ fontSize: '16px', fontWeight: 700, marginBottom: '24px', letterSpacing: '0.05em', color: 'var(--text-muted)' }}>
          CORE FEATURES
        </h3>

        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', minWidth: '700px' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid var(--border-color)' }}>
              <th style={{ padding: '12px 16px', fontSize: '14px', fontWeight: 600, color: 'var(--text-secondary)' }}>
                Feature Breakdown
              </th>
              {plans.map((p) => (
                <th key={p.id} style={{ padding: '12px 16px', fontSize: '14px', fontWeight: 700, textAlign: 'center' }}>
                  {p.name}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            <tr style={{ borderBottom: '1px solid var(--border-color)' }}>
              <td style={{ padding: '16px', fontSize: '14px', fontWeight: 500 }}>Active Projects</td>
              <td style={{ padding: '16px', fontSize: '13px', textAlign: 'center' }}>3 Projects</td>
              <td style={{ padding: '16px', fontSize: '13px', textAlign: 'center', color: 'var(--indigo-500)', fontWeight: 600 }}>
                Unlimited
              </td>
              <td style={{ padding: '16px', fontSize: '13px', textAlign: 'center', fontWeight: 600 }}>Unlimited</td>
            </tr>
            <tr style={{ borderBottom: '1px solid var(--border-color)' }}>
              <td style={{ padding: '16px', fontSize: '14px', fontWeight: 500 }}>Cloud Storage</td>
              <td style={{ padding: '16px', fontSize: '13px', textAlign: 'center' }}>5 GB</td>
              <td style={{ padding: '16px', fontSize: '13px', textAlign: 'center', color: 'var(--indigo-500)', fontWeight: 600 }}>
                50 GB
              </td>
              <td style={{ padding: '16px', fontSize: '13px', textAlign: 'center', fontWeight: 600 }}>500 GB Dedicated</td>
            </tr>
            <tr style={{ borderBottom: '1px solid var(--border-color)' }}>
              <td style={{ padding: '16px', fontSize: '14px', fontWeight: 500 }}>Custom Domains</td>
              <td style={{ padding: '16px', fontSize: '13px', textAlign: 'center', color: 'var(--text-muted)' }}>—</td>
              <td style={{ padding: '16px', fontSize: '13px', textAlign: 'center', color: 'var(--indigo-500)', fontWeight: 600 }}>
                3 Domains
              </td>
              <td style={{ padding: '16px', fontSize: '13px', textAlign: 'center', fontWeight: 600 }}>Unlimited</td>
            </tr>
            <tr style={{ borderBottom: '1px solid var(--border-color)' }}>
              <td style={{ padding: '16px', fontSize: '14px', fontWeight: 500 }}>Template Library</td>
              <td style={{ padding: '16px', textAlign: 'center' }}>
                <span style={{ fontSize: '11px', background: 'var(--bg-card-secondary)', padding: '3px 10px', borderRadius: 'var(--radius-pill)', fontWeight: 600 }}>
                  BASIC LIBRARY
                </span>
              </td>
              <td style={{ padding: '16px', textAlign: 'center' }}>
                <span style={{ fontSize: '11px', background: 'var(--violet-100)', color: 'var(--indigo-600)', padding: '3px 10px', borderRadius: 'var(--radius-pill)', fontWeight: 700 }}>
                  PRO LIBRARY
                </span>
              </td>
              <td style={{ padding: '16px', textAlign: 'center' }}>
                <span style={{ fontSize: '11px', background: 'var(--emerald-50)', color: 'var(--emerald-600)', padding: '3px 10px', borderRadius: 'var(--radius-pill)', fontWeight: 700 }}>
                  FULL LIBRARY
                </span>
              </td>
            </tr>
            <tr style={{ borderBottom: '1px solid var(--border-color)' }}>
              <td style={{ padding: '16px', fontSize: '14px', fontWeight: 500 }}>Priority Support & Escalations</td>
              <td style={{ padding: '16px', textAlign: 'center', color: 'var(--text-muted)' }}>Community only</td>
              <td style={{ padding: '16px', textAlign: 'center' }}>
                <span style={{ display: 'inline-flex', alignItems: 'center', gap: '4px', color: 'var(--emerald-500)', fontWeight: 600, fontSize: '13px' }}>
                  <Check size={16} /> 24/7 Priority
                </span>
              </td>
              <td style={{ padding: '16px', textAlign: 'center' }}>
                <span style={{ display: 'inline-flex', alignItems: 'center', gap: '4px', color: 'var(--emerald-500)', fontWeight: 600, fontSize: '13px' }}>
                  <Check size={16} /> Dedicated Account Manager
                </span>
              </td>
            </tr>
            <tr>
              <td style={{ padding: '16px', fontSize: '14px', fontWeight: 500 }}>Pause Subscription</td>
              <td style={{ padding: '16px', textAlign: 'center', color: 'var(--text-muted)' }}>
                <X size={16} style={{ display: 'inline' }} />
              </td>
              <td style={{ padding: '16px', textAlign: 'center', color: 'var(--emerald-500)', fontWeight: 600 }}>
                <Check size={16} style={{ display: 'inline' }} /> Up to 90 days
              </td>
              <td style={{ padding: '16px', textAlign: 'center', color: 'var(--emerald-500)', fontWeight: 600 }}>
                <Check size={16} style={{ display: 'inline' }} /> Flexible
              </td>
            </tr>
          </tbody>
        </table>
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
