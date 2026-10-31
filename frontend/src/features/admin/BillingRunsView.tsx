import React, { useState } from 'react';
import { api } from '../../services/api';
import { Calendar, Play, CheckCircle2, AlertTriangle, Clock, RefreshCw } from 'lucide-react';

interface BillingRunsViewProps {
  onNotification: (title: string, msg?: string, type?: 'success' | 'error' | 'info') => void;
}

export const BillingRunsView: React.FC<BillingRunsViewProps> = ({ onNotification }) => {
  const [targetDate, setTargetDate] = useState(() => new Date().toISOString().split('T')[0]);
  const [running, setRunning] = useState(false);
  const [latestResult, setLatestResult] = useState<{
    created: number;
    skipped: number;
    failed: number;
    runForDate: string;
    status: string;
  } | null>(null);

  const [history, setHistory] = useState<Array<{
    id: string;
    date: string;
    created: number;
    skipped: number;
    failed: number;
    timestamp: string;
  }>>([
    {
      id: 'RUN-1',
      date: '2026-10-01',
      created: 3,
      skipped: 0,
      failed: 0,
      timestamp: '2026-10-01 00:00:15',
    },
  ]);

  const handleRunBilling = async () => {
    setRunning(true);
    try {
      const res = await api.runBilling(targetDate);
      const created = res.created ?? res.createdCount ?? 0;
      const skipped = res.skipped ?? res.skippedCount ?? 0;
      const failed = res.failed ?? res.failedCount ?? 0;

      const result = {
        created,
        skipped,
        failed,
        runForDate: res.runForDate || targetDate,
        status: res.status || 'COMPLETED',
      };
      setLatestResult(result);

      setHistory((prev) => [
        {
          id: `RUN-${Date.now().toString().slice(-4)}`,
          date: targetDate,
          created,
          skipped,
          failed,
          timestamp: new Date().toLocaleTimeString(),
        },
        ...prev,
      ]);

      onNotification(
        'Billing Run Completed',
        `Date: ${targetDate} | Created: ${created}, Skipped: ${skipped}, Failed: ${failed}`,
        failed > 0 ? 'error' : 'success'
      );
    } catch (e: any) {
      onNotification('Billing Run Failed', e.message, 'error');
    } finally {
      setRunning(false);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      {/* Header */}
      <div>
        <h2 style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em' }}>
          Automated Billing Runs
        </h2>
        <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
          Idempotent subscription recurring invoice generator with transaction isolation.
        </p>
      </div>

      {/* Main Billing Control Card */}
      <div className="card" style={{ padding: '28px' }}>
        <h3 style={{ fontSize: '16px', fontWeight: 700, marginBottom: '16px' }}>
          Trigger Recurring Invoicing Batch
        </h3>

        <div style={{ display: 'flex', gap: '16px', alignItems: 'center', flexWrap: 'wrap' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Calendar size={18} color="var(--indigo-500)" />
            <span style={{ fontSize: '13px', fontWeight: 600 }}>Billing Evaluation Date:</span>
            <input
              type="date"
              value={targetDate}
              onChange={(e) => setTargetDate(e.target.value)}
              style={{ padding: '8px 14px', fontSize: '13px', fontWeight: 600 }}
            />
          </div>

          <button
            onClick={handleRunBilling}
            disabled={running}
            className="btn-primary"
            style={{ padding: '8px 20px', fontSize: '13px' }}
          >
            {running ? <RefreshCw size={15} className="spin" /> : <Play size={15} />}
            {running ? 'Processing Invoices...' : 'Execute Billing Run'}
          </button>
        </div>

        {/* Results Banner */}
        {latestResult && (
          <div
            style={{
              marginTop: '24px',
              padding: '20px',
              borderRadius: 'var(--radius-inner)',
              background: 'var(--bg-card-secondary)',
              border: '1px solid var(--border-color)',
            }}
          >
            <div style={{ fontSize: '13px', fontWeight: 700, marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '6px' }}>
              <CheckCircle2 size={16} color="var(--emerald-500)" />
              Result for target date: {latestResult.runForDate}
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '16px' }}>
              <div style={{ background: 'var(--bg-card)', padding: '12px 16px', borderRadius: '12px' }}>
                <span style={{ fontSize: '11px', color: 'var(--text-muted)', display: 'block' }}>Invoices Created</span>
                <strong style={{ fontSize: '20px', color: 'var(--emerald-600)' }}>{latestResult.created}</strong>
              </div>
              <div style={{ background: 'var(--bg-card)', padding: '12px 16px', borderRadius: '12px' }}>
                <span style={{ fontSize: '11px', color: 'var(--text-muted)', display: 'block' }}>Skipped (Already Billed)</span>
                <strong style={{ fontSize: '20px', color: 'var(--indigo-500)' }}>{latestResult.skipped}</strong>
              </div>
              <div style={{ background: 'var(--bg-card)', padding: '12px 16px', borderRadius: '12px' }}>
                <span style={{ fontSize: '11px', color: 'var(--text-muted)', display: 'block' }}>Failed Subscriptions</span>
                <strong style={{ fontSize: '20px', color: latestResult.failed > 0 ? '#ef4444' : 'var(--text-primary)' }}>
                  {latestResult.failed}
                </strong>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Audit History of Job Runs */}
      <div className="card" style={{ padding: '24px' }}>
        <h3 style={{ fontSize: '16px', fontWeight: 700, marginBottom: '16px' }}>
          Historical Batch Runs
        </h3>

        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', textAlign: 'left' }}>
              <th style={{ padding: '10px 12px' }}>Batch ID</th>
              <th style={{ padding: '10px 12px' }}>Run For Date</th>
              <th style={{ padding: '10px 12px' }}>Created</th>
              <th style={{ padding: '10px 12px' }}>Skipped</th>
              <th style={{ padding: '10px 12px' }}>Failed</th>
              <th style={{ padding: '10px 12px' }}>Executed At</th>
              <th style={{ padding: '10px 12px' }}>Status</th>
            </tr>
          </thead>
          <tbody>
            {history.map((row) => (
              <tr key={row.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                <td style={{ padding: '12px', fontWeight: 700 }}>{row.id}</td>
                <td style={{ padding: '12px' }}>{row.date}</td>
                <td style={{ padding: '12px', color: 'var(--emerald-600)', fontWeight: 700 }}>+{row.created}</td>
                <td style={{ padding: '12px', color: 'var(--text-muted)' }}>{row.skipped}</td>
                <td style={{ padding: '12px', color: row.failed > 0 ? '#ef4444' : 'inherit' }}>{row.failed}</td>
                <td style={{ padding: '12px', color: 'var(--text-muted)', fontSize: '12px' }}>{row.timestamp}</td>
                <td style={{ padding: '12px' }}>
                  <span
                    style={{
                      background: 'var(--emerald-50)',
                      color: 'var(--emerald-600)',
                      fontSize: '11px',
                      fontWeight: 700,
                      padding: '2px 8px',
                      borderRadius: 'var(--radius-pill)',
                    }}
                  >
                    COMPLETED
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

