import React, { useState, useEffect } from 'react';
import { Subscription } from '../../types';
import { api } from '../../services/api';
import { formatCurrency, formatDate } from '../../utils/formatters';
import { StatusChip } from '../../components/ui/StatusChip';
import { Play, Pause, XCircle, ArrowRight, ShieldAlert, Sparkles } from 'lucide-react';

interface ManageProps {
  currency: 'INR' | 'USD';
  onNotification: (title: string, message?: string) => void;
  onNavigateToPricing: () => void;
}

export const Manage: React.FC<ManageProps> = ({
  currency,
  onNotification,
  onNavigateToPricing,
}) => {
  const [subscriptions, setSubscriptions] = useState<Subscription[]>([]);
  const [loading, setLoading] = useState(true);
  const [actionInProgress, setActionInProgress] = useState<number | null>(null);

  useEffect(() => {
    loadSubscriptions();
  }, []);

  const loadSubscriptions = async () => {
    try {
      setLoading(true);
      const data = await api.getSubscriptions();
      setSubscriptions(data);
    } catch (err) {
      console.error('Failed to load subscriptions:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleTogglePause = async (sub: Subscription) => {
    try {
      setActionInProgress(sub.id);
      await api.pauseSubscription(sub.id);
      const newStatus = !sub.isPaused ? 'Paused' : 'Resumed';
      onNotification(
        `Subscription ${newStatus}`,
        `${sub.number} (${sub.customerName}) has been ${newStatus.toLowerCase()} in PostgreSQL.`
      );
      loadSubscriptions();
    } catch (err: any) {
      alert(`Action failed: ${err.message}`);
    } finally {
      setActionInProgress(null);
    }
  };

  const handleClose = async (sub: Subscription) => {
    if (!window.confirm(`Are you sure you want to cancel subscription ${sub.number}?`)) {
      return;
    }
    try {
      setActionInProgress(sub.id);
      await api.closeSubscription(sub.id);
      onNotification(
        'Subscription Cancelled',
        `${sub.number} status updated to CLOSED in database.`
      );
      loadSubscriptions();
    } catch (err: any) {
      alert(`Cancellation failed: ${err.message}`);
    } finally {
      setActionInProgress(null);
    }
  };

  const stateSteps = ['DRAFT', 'QUOTATION', 'CONFIRMED', 'ACTIVE', 'CLOSED'];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
        <div>
          <h1 style={{ fontSize: '28px', fontWeight: 800, letterSpacing: '-0.02em' }}>
            Subscription Lifecycle Management
          </h1>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            Inspect, pause, or transition subscription contracts in PostgreSQL
          </p>
        </div>

        <button onClick={onNavigateToPricing} className="btn-primary">
          + Add New Subscription
        </button>
      </div>

      {/* State Machine Visualizer */}
      <div className="card" style={{ padding: '24px', marginBottom: '32px' }}>
        <h3 style={{ fontSize: '14px', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', marginBottom: '16px' }}>
          State Machine Workflow
        </h3>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '8px' }}>
          {stateSteps.map((step, idx) => (
            <React.Fragment key={step}>
              <div
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  background: step === 'ACTIVE' ? 'var(--emerald-50)' : 'var(--bg-card-secondary)',
                  color: step === 'ACTIVE' ? 'var(--emerald-600)' : 'var(--text-secondary)',
                  padding: '8px 16px',
                  borderRadius: 'var(--radius-pill)',
                  fontWeight: 700,
                  fontSize: '12px',
                  border: step === 'ACTIVE' ? '1px solid var(--emerald-500)' : '1px solid var(--border-color)',
                }}
              >
                <span
                  style={{
                    width: '6px',
                    height: '6px',
                    borderRadius: '50%',
                    background: step === 'ACTIVE' ? 'var(--emerald-500)' : 'var(--text-muted)',
                  }}
                />
                {step}
              </div>
              {idx < stateSteps.length - 1 && (
                <ArrowRight size={16} color="var(--text-muted)" />
              )}
            </React.Fragment>
          ))}
        </div>
      </div>

      {/* Subscription Cards Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(340px, 1fr))', gap: '24px' }}>
        {subscriptions.map((sub) => {
          const isActive = sub.status === 'ACTIVE';
          const isPaused = sub.isPaused;
          const isClosed = sub.status === 'CLOSED';

          return (
            <div
              key={sub.id}
              className="card"
              style={{
                padding: '24px',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between',
                borderLeft: isPaused
                  ? '4px solid #f59e0b'
                  : isActive
                  ? '4px solid var(--emerald-500)'
                  : '4px solid var(--border-color)',
              }}
            >
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                  <span style={{ fontSize: '13px', fontWeight: 800, color: 'var(--indigo-500)' }}>
                    {sub.number}
                  </span>
                  <div style={{ display: 'flex', gap: '6px' }}>
                    {isPaused && (
                      <span className="status-chip draft">
                        <span className="status-dot" /> Paused
                      </span>
                    )}
                    <StatusChip status={sub.status} />
                  </div>
                </div>

                <h3 style={{ fontSize: '18px', fontWeight: 700, marginBottom: '4px' }}>
                  {sub.customerName}
                </h3>
                <p style={{ fontSize: '13px', color: 'var(--text-secondary)', marginBottom: '16px' }}>
                  Plan: <strong>{sub.planName}</strong> • {formatCurrency(sub.pricePaise, currency)}/mo
                </p>

                <div
                  style={{
                    background: 'var(--bg-card-secondary)',
                    borderRadius: 'var(--radius-inner)',
                    padding: '12px 14px',
                    fontSize: '12px',
                    display: 'flex',
                    flexDirection: 'column',
                    gap: '6px',
                    marginBottom: '20px',
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ color: 'var(--text-muted)' }}>Activated On</span>
                    <span style={{ fontWeight: 600 }}>{formatDate(sub.startDate)}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ color: 'var(--text-muted)' }}>Next Billing Date</span>
                    <span style={{ fontWeight: 600 }}>{formatDate(sub.nextBillingDate)}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ color: 'var(--text-muted)' }}>Pause Allowed</span>
                    <span style={{ fontWeight: 600, color: sub.allowPause ? 'var(--emerald-600)' : 'var(--text-muted)' }}>
                      {sub.allowPause ? 'Yes (Pro/Enterprise)' : 'No (Starter)'}
                    </span>
                  </div>
                </div>
              </div>

              {/* Action Buttons */}
              {!isClosed ? (
                <div style={{ display: 'flex', gap: '10px' }}>
                  {sub.allowPause && (
                    <button
                      onClick={() => handleTogglePause(sub)}
                      disabled={actionInProgress === sub.id}
                      className="btn-secondary"
                      style={{
                        flex: 1,
                        padding: '8px 12px',
                        fontSize: '12px',
                        justifyContent: 'center',
                      }}
                    >
                      {isPaused ? (
                        <>
                          <Play size={14} color="var(--emerald-500)" /> Resume
                        </>
                      ) : (
                        <>
                          <Pause size={14} color="#f59e0b" /> Pause
                        </>
                      )}
                    </button>
                  )}

                  <button
                    onClick={() => handleClose(sub)}
                    disabled={actionInProgress === sub.id}
                    className="btn-secondary"
                    style={{
                      flex: 1,
                      padding: '8px 12px',
                      fontSize: '12px',
                      justifyContent: 'center',
                      color: '#ef4444',
                    }}
                  >
                    <XCircle size={14} /> Cancel
                  </button>
                </div>
              ) : (
                <div style={{ fontSize: '12px', color: 'var(--text-muted)', textAlign: 'center', padding: '8px 0' }}>
                  Contract Terminated
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};

