import React, { useState, useEffect } from 'react';
import { Discount } from '../../types';
import { api } from '../../services/api';
import { formatCurrency, formatDate } from '../../utils/formatters';
import { Tag, Plus, X } from 'lucide-react';

interface DiscountsProps {
  currency: 'INR' | 'USD';
  onNotification: (title: string, message?: string) => void;
}

export const Discounts: React.FC<DiscountsProps> = ({
  currency,
  onNotification,
}) => {
  const [discounts, setDiscounts] = useState<Discount[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);

  // Form state
  const [code, setCode] = useState('');
  const [name, setName] = useState('');
  const [discountType, setDiscountType] = useState<'PERCENT' | 'FIXED'>('PERCENT');
  const [value, setValue] = useState<number>(20);
  const [usageLimit, setUsageLimit] = useState<number>(100);

  useEffect(() => {
    loadDiscounts();
  }, []);

  const loadDiscounts = async () => {
    try {
      setLoading(true);
      const res = await api.getDiscounts();
      setDiscounts(res);
    } catch (err) {
      console.error('Failed to load discounts:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const realValue = discountType === 'PERCENT' ? value * 100 : value * 100; // bps or paise
      await api.createDiscount({
        code: code.toUpperCase(),
        name,
        discountType,
        value: realValue,
        scope: 'SUBSCRIPTION',
        usageLimit,
      });
      onNotification('Coupon Created', `Discount code ${code.toUpperCase()} is now live.`);
      setShowCreateModal(false);
      setCode('');
      setName('');
      loadDiscounts();
    } catch (err: any) {
      alert(`Failed to create discount: ${err.message}`);
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
        <div>
          <h1 style={{ fontSize: '28px', fontWeight: 800, letterSpacing: '-0.02em' }}>
            Promotional Coupons & Discounts
          </h1>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            Row-locked concurrent redemption rules stored in PostgreSQL `discounts` table
          </p>
        </div>

        <button onClick={() => setShowCreateModal(true)} className="btn-primary">
          <Plus size={16} /> Create Discount Code
        </button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '24px' }}>
        {discounts.map((d) => (
          <div key={d.id} className="card" style={{ padding: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <div
                style={{
                  background: 'var(--violet-100)',
                  color: 'var(--indigo-600)',
                  padding: '6px 14px',
                  borderRadius: 'var(--radius-pill)',
                  fontWeight: 800,
                  fontSize: '14px',
                  letterSpacing: '0.05em',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                }}
              >
                <Tag size={14} /> {d.code}
              </div>

              <span
                style={{
                  fontSize: '11px',
                  fontWeight: 700,
                  color: d.isActive ? 'var(--emerald-600)' : 'var(--text-muted)',
                  background: d.isActive ? 'var(--emerald-50)' : 'var(--bg-card-secondary)',
                  padding: '3px 10px',
                  borderRadius: 'var(--radius-pill)',
                }}
              >
                {d.isActive ? 'ACTIVE' : 'EXPIRED'}
              </span>
            </div>

            <h3 style={{ fontSize: '16px', fontWeight: 700, marginBottom: '6px' }}>{d.name}</h3>
            <p style={{ fontSize: '24px', fontWeight: 800, color: 'var(--emerald-600)', marginBottom: '16px' }}>
              {d.discountType === 'PERCENT'
                ? `${d.value / 100}% OFF`
                : `${formatCurrency(d.value, currency)} FLAT`}
            </p>

            <div
              style={{
                background: 'var(--bg-card-secondary)',
                borderRadius: 'var(--radius-inner)',
                padding: '12px',
                fontSize: '12px',
                display: 'flex',
                flexDirection: 'column',
                gap: '4px',
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--text-muted)' }}>Usage Limit</span>
                <span style={{ fontWeight: 600 }}>{d.usageLimit || 'Unlimited'}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--text-muted)' }}>Scope</span>
                <span style={{ fontWeight: 600 }}>{d.scope}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--text-muted)' }}>Valid Until</span>
                <span style={{ fontWeight: 600 }}>{d.endsAt ? formatDate(d.endsAt) : 'Perpetual'}</span>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Create Modal */}
      {showCreateModal && (
        <div className="modal-overlay" onClick={() => setShowCreateModal(false)}>
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
              <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Add New Promotion Rule</h3>
              <button
                onClick={() => setShowCreateModal(false)}
                style={{ border: 'none', background: 'transparent', color: 'var(--text-muted)', cursor: 'pointer' }}
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleCreate} style={{ padding: '24px 28px' }}>
              <div style={{ marginBottom: '16px' }}>
                <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, marginBottom: '6px' }}>
                  Discount Code
                </label>
                <input
                  type="text"
                  placeholder="e.g. FLASH30"
                  value={code}
                  onChange={(e) => setCode(e.target.value)}
                  style={{ width: '100%', textTransform: 'uppercase', fontWeight: 700 }}
                  required
                />
              </div>

              <div style={{ marginBottom: '16px' }}>
                <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, marginBottom: '6px' }}>
                  Campaign Name
                </label>
                <input
                  type="text"
                  placeholder="e.g. Summer Flash Sale 30% Off"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  style={{ width: '100%' }}
                  required
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginBottom: '16px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, marginBottom: '6px' }}>
                    Type
                  </label>
                  <select
                    value={discountType}
                    onChange={(e) => setDiscountType(e.target.value as any)}
                    style={{ width: '100%' }}
                  >
                    <option value="PERCENT">Percentage (%)</option>
                    <option value="FIXED">Fixed Amount (₹)</option>
                  </select>
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, marginBottom: '6px' }}>
                    Value ({discountType === 'PERCENT' ? '%' : '₹'})
                  </label>
                  <input
                    type="number"
                    value={value}
                    onChange={(e) => setValue(Number(e.target.value))}
                    style={{ width: '100%' }}
                    required
                  />
                </div>
              </div>

              <div style={{ marginBottom: '24px' }}>
                <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, marginBottom: '6px' }}>
                  Max Redemptions
                </label>
                <input
                  type="number"
                  value={usageLimit}
                  onChange={(e) => setUsageLimit(Number(e.target.value))}
                  style={{ width: '100%' }}
                  required
                />
              </div>

              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
                <button type="button" onClick={() => setShowCreateModal(false)} className="btn-secondary">
                  Cancel
                </button>
                <button type="submit" className="btn-primary">
                  Save Rule
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
