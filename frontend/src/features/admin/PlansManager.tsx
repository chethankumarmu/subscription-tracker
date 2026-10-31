import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { formatCurrency } from '../../utils/formatters';
import { Layers, Plus, CheckCircle, Ban, Sliders, Shield } from 'lucide-react';

interface PlansManagerProps {
  currency: 'INR' | 'USD';
  onNotification: (title: string, msg?: string, type?: 'success' | 'error' | 'info') => void;
}

export const PlansManager: React.FC<PlansManagerProps> = ({ currency, onNotification }) => {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';

  const [plans, setPlans] = useState([
    {
      id: 1,
      code: 'STARTER',
      name: 'Starter Plan',
      versions: [
        {
          id: 101,
          versionNo: 1,
          status: 'PUBLISHED',
          pricePaise: 49900,
          billingCycle: 'MONTHLY',
          allowRenewal: true,
          limits: { USERS: 3, STORAGE_GB: 10, CUSTOM_DOMAINS: 0 },
        },
      ],
    },
    {
      id: 2,
      code: 'GROWTH',
      name: 'Growth Plan',
      versions: [
        {
          id: 201,
          versionNo: 1,
          status: 'RETIRED',
          pricePaise: 149900,
          billingCycle: 'MONTHLY',
          allowRenewal: true,
          limits: { USERS: 10, STORAGE_GB: 50, CUSTOM_DOMAINS: 1 },
        },
        {
          id: 202,
          versionNo: 2,
          status: 'PUBLISHED',
          pricePaise: 159900,
          billingCycle: 'MONTHLY',
          allowRenewal: true,
          limits: { USERS: 15, STORAGE_GB: 100, CUSTOM_DOMAINS: 1 },
        },
      ],
    },
    {
      id: 3,
      code: 'ENTERPRISE',
      name: 'Enterprise Plan',
      versions: [
        {
          id: 301,
          versionNo: 1,
          status: 'PUBLISHED',
          pricePaise: 499900,
          billingCycle: 'YEARLY',
          allowRenewal: true,
          limits: { USERS: 50, STORAGE_GB: 500, CUSTOM_DOMAINS: 5 },
        },
      ],
    },
  ]);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedPlanId, setSelectedPlanId] = useState<number>(1);
  const [newPrice, setNewPrice] = useState(1999);
  const [cycle, setCycle] = useState('MONTHLY');
  const [usersLimit, setUsersLimit] = useState(20);
  const [storageLimit, setStorageLimit] = useState(200);

  const handlePublish = (planId: number, versionId: number) => {
    setPlans((prev) =>
      prev.map((p) =>
        p.id === planId
          ? {
              ...p,
              versions: p.versions.map((v) =>
                v.id === versionId ? { ...v, status: 'PUBLISHED' } : v
              ),
            }
          : p
      )
    );
    onNotification('Plan Version Published', `Version #${versionId} is now live for subscriptions`, 'success');
  };

  const handleRetire = (planId: number, versionId: number) => {
    setPlans((prev) =>
      prev.map((p) =>
        p.id === planId
          ? {
              ...p,
              versions: p.versions.map((v) =>
                v.id === versionId ? { ...v, status: 'RETIRED' } : v
              ),
            }
          : p
      )
    );
    onNotification('Plan Version Retired', `Version #${versionId} marked RETIRED. Existing subs remain valid.`, 'info');
  };

  const handleCreateVersion = (e: React.FormEvent) => {
    e.preventDefault();
    setPlans((prev) =>
      prev.map((p) => {
        if (p.id !== selectedPlanId) return p;
        const nextVerNo = p.versions.length + 1;
        const newVer = {
          id: Date.now(),
          versionNo: nextVerNo,
          status: 'DRAFT',
          pricePaise: Math.round(newPrice * 100),
          billingCycle: cycle,
          allowRenewal: true,
          limits: { USERS: usersLimit, STORAGE_GB: storageLimit, CUSTOM_DOMAINS: 1 },
        };
        return { ...p, versions: [...p.versions, newVer] };
      })
    );

    setIsModalOpen(false);
    onNotification('Draft Plan Version Created', `New draft version created. Ready for publishing.`, 'success');
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em' }}>
            Subscription Plans &amp; Versioning
          </h2>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            {isAdmin
              ? 'Create, publish, retire plan versions, and manage tier feature limits.'
              : 'View-only access to published and retired plan versions.'}
          </p>
        </div>

        {isAdmin && (
          <button onClick={() => setIsModalOpen(true)} className="btn-primary">
            <Plus size={15} /> Create Plan Version
          </button>
        )}
      </div>

      {plans.map((p) => (
        <div key={p.id} className="card" style={{ padding: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <div
                style={{
                  width: '32px',
                  height: '32px',
                  borderRadius: '8px',
                  background: 'var(--bg-card-secondary)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  color: 'var(--indigo-500)',
                }}
              >
                <Layers size={17} />
              </div>
              <div>
                <h3 style={{ fontSize: '16px', fontWeight: 700 }}>{p.name}</h3>
                <span style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
                  Code: {p.code}
                </span>
              </div>
            </div>
          </div>

          {/* Versions Table */}
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', textAlign: 'left' }}>
                <th style={{ padding: '8px 10px' }}>Version</th>
                <th style={{ padding: '8px 10px' }}>Cycle</th>
                <th style={{ padding: '8px 10px' }}>Price</th>
                <th style={{ padding: '8px 10px' }}>Limits (Users / Storage / Domains)</th>
                <th style={{ padding: '8px 10px' }}>Status</th>
                {isAdmin && <th style={{ padding: '8px 10px', textAlign: 'right' }}>Actions</th>}
              </tr>
            </thead>
            <tbody>
              {p.versions.map((v) => (
                <tr key={v.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                  <td style={{ padding: '10px', fontWeight: 700 }}>v{v.versionNo}</td>
                  <td style={{ padding: '10px' }}>{v.billingCycle}</td>
                  <td style={{ padding: '10px', fontWeight: 700, color: 'var(--emerald-600)' }}>
                    {formatCurrency(v.pricePaise, currency)}
                  </td>
                  <td style={{ padding: '10px', color: 'var(--text-secondary)' }}>
                    {v.limits.USERS} users • {v.limits.STORAGE_GB} GB • {v.limits.CUSTOM_DOMAINS} domains
                  </td>
                  <td style={{ padding: '10px' }}>
                    <span
                      style={{
                        padding: '2px 8px',
                        borderRadius: 'var(--radius-pill)',
                        fontSize: '11px',
                        fontWeight: 700,
                        background:
                          v.status === 'PUBLISHED'
                            ? 'var(--emerald-50)'
                            : v.status === 'RETIRED'
                            ? '#fef2f2'
                            : 'var(--bg-card-secondary)',
                        color:
                          v.status === 'PUBLISHED'
                            ? 'var(--emerald-600)'
                            : v.status === 'RETIRED'
                            ? '#b91c1c'
                            : 'var(--text-muted)',
                      }}
                    >
                      {v.status}
                    </span>
                  </td>
                  {isAdmin && (
                    <td style={{ padding: '10px', textAlign: 'right' }}>
                      <div style={{ display: 'inline-flex', gap: '6px' }}>
                        {v.status !== 'PUBLISHED' && (
                          <button
                            onClick={() => handlePublish(p.id, v.id)}
                            className="btn-emerald"
                            style={{ padding: '4px 10px', fontSize: '11px' }}
                          >
                            <CheckCircle size={12} /> Publish
                          </button>
                        )}
                        {v.status !== 'RETIRED' && (
                          <button
                            onClick={() => handleRetire(p.id, v.id)}
                            className="btn-secondary"
                            style={{ padding: '4px 10px', fontSize: '11px' }}
                          >
                            <Ban size={12} /> Retire
                          </button>
                        )}
                      </div>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ))}

      {/* Admin Version Creator Modal */}
      {isModalOpen && isAdmin && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: '440px' }}>
            <h3 style={{ fontSize: '18px', fontWeight: 800, marginBottom: '16px' }}>
              Create New Plan Version
            </h3>

            <form onSubmit={handleCreateVersion} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                  Target Plan Tier
                </label>
                <select
                  value={selectedPlanId}
                  onChange={(e) => setSelectedPlanId(Number(e.target.value))}
                  style={{ width: '100%' }}
                >
                  {plans.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.name} ({p.code})
                    </option>
                  ))}
                </select>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                    Price (₹ / $)
                  </label>
                  <input
                    type="number"
                    required
                    min="1"
                    value={newPrice}
                    onChange={(e) => setNewPrice(parseFloat(e.target.value) || 0)}
                    style={{ width: '100%' }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                    Billing Cycle
                  </label>
                  <select
                    value={cycle}
                    onChange={(e) => setCycle(e.target.value)}
                    style={{ width: '100%' }}
                  >
                    <option value="MONTHLY">Monthly</option>
                    <option value="YEARLY">Yearly</option>
                    <option value="WEEKLY">Weekly</option>
                  </select>
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                    User Seats Limit
                  </label>
                  <input
                    type="number"
                    min="1"
                    value={usersLimit}
                    onChange={(e) => setUsersLimit(Number(e.target.value))}
                    style={{ width: '100%' }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                    Storage (GB)
                  </label>
                  <input
                    type="number"
                    min="1"
                    value={storageLimit}
                    onChange={(e) => setStorageLimit(Number(e.target.value))}
                    style={{ width: '100%' }}
                  />
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px', marginTop: '10px' }}>
                <button type="button" onClick={() => setIsModalOpen(false)} className="btn-secondary">
                  Cancel
                </button>
                <button type="submit" className="btn-primary">
                  Create Version
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

