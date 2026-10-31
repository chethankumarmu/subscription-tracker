import React, { useState } from 'react';
import { Plan, Customer } from '../../types';
import { api } from '../../services/api';
import { formatCurrency } from '../../utils/formatters';
import { X, Check, ShieldCheck, Tag } from 'lucide-react';

interface CheckoutModalProps {
  plan: Plan;
  billingCycle: 'MONTHLY' | 'YEARLY';
  customers: Customer[];
  currency: 'INR' | 'USD';
  onClose: () => void;
  onSuccess: (message: string) => void;
}

export const CheckoutModal: React.FC<CheckoutModalProps> = ({
  plan,
  billingCycle,
  customers,
  currency,
  onClose,
  onSuccess,
}) => {
  const [selectedCustomerId, setSelectedCustomerId] = useState<number>(
    customers[0]?.id || 1
  );
  const [couponCode, setCouponCode] = useState('');
  const [discountPercent, setDiscountPercent] = useState<number>(0);
  const [couponStatus, setCouponStatus] = useState<string>('');
  const [submitting, setSubmitting] = useState(false);

  const basePricePaise =
    billingCycle === 'YEARLY' ? plan.yearlyPricePaise : plan.monthlyPricePaise;

  const discountAmountPaise = Math.round((basePricePaise * discountPercent) / 100);
  const subtotalPaise = Math.max(0, basePricePaise - discountAmountPaise);
  const taxPaise = Math.round(subtotalPaise * 0.18);
  const totalPaise = subtotalPaise + taxPaise;

  const handleApplyCoupon = async () => {
    if (!couponCode.trim()) return;
    try {
      const res = await api.validateDiscount(couponCode.trim());
      if (res && res.isActive) {
        if (res.discountType === 'PERCENT') {
          setDiscountPercent(res.value / 100); // 2000 bps -> 20%
          setCouponStatus(`Applied! ${res.value / 100}% off`);
        } else {
          setDiscountPercent(15);
          setCouponStatus(`Applied! Flat discount applied`);
        }
      }
    } catch {
      setCouponStatus('Invalid or expired coupon code');
    }
  };

  const handleConfirm = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await api.createSubscription({
        customerId: selectedCustomerId,
        planId: plan.id,
        billingCycle,
        discountCode: couponCode || undefined,
      });
      onSuccess(`Subscribed to ${plan.name} plan successfully! Real invoice generated.`);
      onClose();
    } catch (err: any) {
      alert(`Error creating subscription: ${err.message}`);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: '24px 28px',
            borderBottom: '1px solid var(--border-color)',
          }}
        >
          <div>
            <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Confirm Subscription</h3>
            <p style={{ fontSize: '13px', color: 'var(--text-muted)', marginTop: '2px' }}>
              Subscribe customer to {plan.name} Tier ({billingCycle})
            </p>
          </div>
          <button
            onClick={onClose}
            style={{
              border: 'none',
              background: 'transparent',
              color: 'var(--text-muted)',
              cursor: 'pointer',
            }}
          >
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleConfirm} style={{ padding: '24px 28px' }}>
          {/* Customer Selector */}
          <div style={{ marginBottom: '20px' }}>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, marginBottom: '8px' }}>
              Select Customer
            </label>
            <select
              value={selectedCustomerId}
              onChange={(e) => setSelectedCustomerId(Number(e.target.value))}
              style={{ width: '100%' }}
              required
            >
              {customers.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.companyName} ({c.contactName})
                </option>
              ))}
            </select>
          </div>

          {/* Coupon Input */}
          <div style={{ marginBottom: '24px' }}>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, marginBottom: '8px' }}>
              Discount Promo Code
            </label>
            <div style={{ display: 'flex', gap: '8px' }}>
              <input
                type="text"
                placeholder="e.g. EARLYBIRD"
                value={couponCode}
                onChange={(e) => setCouponCode(e.target.value.toUpperCase())}
                style={{ flex: 1 }}
              />
              <button
                type="button"
                onClick={handleApplyCoupon}
                className="btn-secondary"
                style={{ padding: '8px 16px' }}
              >
                <Tag size={14} /> Apply
              </button>
            </div>
            {couponStatus && (
              <p
                style={{
                  fontSize: '12px',
                  color: couponStatus.includes('Applied') ? 'var(--emerald-600)' : '#ef4444',
                  marginTop: '6px',
                  fontWeight: 600,
                }}
              >
                {couponStatus}
              </p>
            )}
          </div>

          {/* Breakdown summary */}
          <div
            style={{
              background: 'var(--bg-card-secondary)',
              borderRadius: 'var(--radius-inner)',
              padding: '16px',
              marginBottom: '24px',
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', marginBottom: '8px' }}>
              <span style={{ color: 'var(--text-secondary)' }}>Base Plan Price</span>
              <span>{formatCurrency(basePricePaise, currency)}</span>
            </div>
            {discountAmountPaise > 0 && (
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', marginBottom: '8px', color: 'var(--emerald-600)' }}>
                <span>Discount ({discountPercent}%)</span>
                <span>-{formatCurrency(discountAmountPaise, currency)}</span>
              </div>
            )}
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', marginBottom: '8px' }}>
              <span style={{ color: 'var(--text-secondary)' }}>GST (18%)</span>
              <span>{formatCurrency(taxPaise, currency)}</span>
            </div>
            <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                fontSize: '15px',
                fontWeight: 700,
                paddingTop: '8px',
                borderTop: '1px solid var(--border-color)',
              }}
            >
              <span>Total Payable</span>
              <span style={{ color: 'var(--indigo-500)' }}>{formatCurrency(totalPaise, currency)}</span>
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '24px', color: 'var(--text-muted)', fontSize: '12px' }}>
            <ShieldCheck size={16} color="var(--emerald-500)" />
            <span>Directly saves into PostgreSQL database table `subscriptions` & `invoices`.</span>
          </div>

          <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
            <button type="button" onClick={onClose} className="btn-secondary">
              Cancel
            </button>
            <button type="submit" disabled={submitting} className="btn-primary">
              {submitting ? 'Creating Record...' : 'Confirm Subscription'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
