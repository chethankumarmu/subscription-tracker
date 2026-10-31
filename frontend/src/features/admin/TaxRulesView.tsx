import React, { useState } from 'react';
import { Percent, Plus, Check, X, ShieldAlert } from 'lucide-react';

interface TaxRulesViewProps {
  onNotification: (title: string, msg?: string, type?: 'success' | 'error' | 'info') => void;
}

export const TaxRulesView: React.FC<TaxRulesViewProps> = ({ onNotification }) => {
  const [taxRules, setTaxRules] = useState([
    {
      id: 1,
      name: 'GST 18% (Standard Services)',
      taxType: 'GST',
      rateBps: 1800,
      country: 'IN',
      isActive: true,
    },
    {
      id: 2,
      name: 'GST 12% (Telecom / IT Tier 2)',
      taxType: 'GST',
      rateBps: 1200,
      country: 'IN',
      isActive: true,
    },
    {
      id: 3,
      name: 'Export 0% (Zero Rated)',
      taxType: 'EXPORT',
      rateBps: 0,
      country: 'GLOBAL',
      isActive: true,
    },
  ]);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [name, setName] = useState('');
  const [taxType, setTaxType] = useState('GST');
  const [ratePercent, setRatePercent] = useState(18);
  const [country, setCountry] = useState('IN');

  const toggleStatus = (id: number) => {
    setTaxRules((prev) =>
      prev.map((r) => (r.id === id ? { ...r, isActive: !r.isActive } : r))
    );
    onNotification('Tax Rule Updated', 'Tax rule status toggled.', 'info');
  };

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    const newRule = {
      id: Date.now(),
      name,
      taxType,
      rateBps: Math.round(ratePercent * 100),
      country,
      isActive: true,
    };
    setTaxRules((prev) => [...prev, newRule]);
    setIsModalOpen(false);
    onNotification('Tax Rule Created', `Created rule ${name} (${ratePercent}%).`, 'success');
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em' }}>
            Tax Rules Configuration
          </h2>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            Configure jurisdiction tax rates (in basis points) applied to line items.
          </p>
        </div>

        <button onClick={() => setIsModalOpen(true)} className="btn-primary">
          <Plus size={15} /> Add Tax Rule
        </button>
      </div>

      <div className="card" style={{ padding: '24px' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', textAlign: 'left' }}>
              <th style={{ padding: '10px 12px' }}>Rule Name</th>
              <th style={{ padding: '10px 12px' }}>Type</th>
              <th style={{ padding: '10px 12px' }}>Rate (%)</th>
              <th style={{ padding: '10px 12px' }}>Basis Points (bps)</th>
              <th style={{ padding: '10px 12px' }}>Region</th>
              <th style={{ padding: '10px 12px' }}>Status</th>
              <th style={{ padding: '10px 12px', textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {taxRules.map((r) => (
              <tr key={r.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                <td style={{ padding: '12px', fontWeight: 700 }}>{r.name}</td>
                <td style={{ padding: '12px' }}>
                  <span
                    style={{
                      background: 'var(--bg-card-secondary)',
                      padding: '2px 8px',
                      borderRadius: 'var(--radius-pill)',
                      fontSize: '11px',
                    }}
                  >
                    {r.taxType}
                  </span>
                </td>
                <td style={{ padding: '12px', fontWeight: 700, color: 'var(--indigo-500)' }}>
                  {r.rateBps / 100}%
                </td>
                <td style={{ padding: '12px', fontFamily: 'var(--font-mono)' }}>{r.rateBps} bps</td>
                <td style={{ padding: '12px' }}>{r.country}</td>
                <td style={{ padding: '12px' }}>
                  <span
                    style={{
                      padding: '2px 8px',
                      borderRadius: 'var(--radius-pill)',
                      fontSize: '11px',
                      fontWeight: 700,
                      background: r.isActive ? 'var(--emerald-50)' : '#fef2f2',
                      color: r.isActive ? 'var(--emerald-600)' : '#b91c1c',
                    }}
                  >
                    {r.isActive ? 'ACTIVE' : 'DEACTIVATED'}
                  </span>
                </td>
                <td style={{ padding: '12px', textAlign: 'right' }}>
                  <button
                    onClick={() => toggleStatus(r.id)}
                    className="btn-secondary"
                    style={{ padding: '4px 10px', fontSize: '11px' }}
                  >
                    {r.isActive ? 'Deactivate' : 'Activate'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: '440px' }}>
            <h3 style={{ fontSize: '18px', fontWeight: 800, marginBottom: '16px' }}>
              Add New Tax Rule
            </h3>

            <form onSubmit={handleCreate} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                  Tax Rule Label
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. GST 18%"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  style={{ width: '100%' }}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                    Tax Rate (%)
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={ratePercent}
                    onChange={(e) => setRatePercent(parseFloat(e.target.value) || 0)}
                    style={{ width: '100%' }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                    Tax Category
                  </label>
                  <select
                    value={taxType}
                    onChange={(e) => setTaxType(e.target.value)}
                    style={{ width: '100%' }}
                  >
                    <option value="GST">GST</option>
                    <option value="VAT">VAT</option>
                    <option value="SALES_TAX">Sales Tax</option>
                    <option value="EXPORT">Export (Zero)</option>
                  </select>
                </div>
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                  Country / Region
                </label>
                <input
                  type="text"
                  value={country}
                  onChange={(e) => setCountry(e.target.value)}
                  style={{ width: '100%' }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px', marginTop: '10px' }}>
                <button type="button" onClick={() => setIsModalOpen(false)} className="btn-secondary">
                  Cancel
                </button>
                <button type="submit" className="btn-primary">
                  Create Rule
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

