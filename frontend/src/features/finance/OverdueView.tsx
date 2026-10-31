import React from 'react';
import { formatCurrency } from '../../utils/formatters';
import { AlertTriangle, Clock, Calendar, CheckCircle } from 'lucide-react';

interface OverdueViewProps {
  currency: 'INR' | 'USD';
}

export const OverdueView: React.FC<OverdueViewProps> = ({ currency }) => {
  const agingSummary = [
    { bucket: '1 - 30 Days Overdue', count: 2, amountPaise: 299800, color: '#f59e0b' },
    { bucket: '31 - 60 Days Overdue', count: 1, amountPaise: 159900, color: '#f97316' },
    { bucket: '61 - 90 Days Overdue', count: 0, amountPaise: 0, color: '#ef4444' },
    { bucket: '90+ Days (Default Risk)', count: 0, amountPaise: 0, color: '#991b1b' },
  ];

  const overdueInvoices = [
    {
      id: 501,
      invoiceNumber: 'INV-2026-000501',
      customerName: 'Acme Corp',
      contactName: 'Alice Vance',
      dueDate: '2026-10-15',
      daysOverdue: 16,
      totalPaise: 149900,
      balancePaise: 149900,
    },
    {
      id: 502,
      invoiceNumber: 'INV-2026-000502',
      customerName: 'TechNova Global',
      contactName: 'Robert Smith',
      dueDate: '2026-10-20',
      daysOverdue: 11,
      totalPaise: 149900,
      balancePaise: 149900,
    },
    {
      id: 489,
      invoiceNumber: 'INV-2026-000489',
      customerName: 'QuantumLeap Inc.',
      contactName: 'Deepa Rao',
      dueDate: '2026-09-25',
      daysOverdue: 36,
      totalPaise: 159900,
      balancePaise: 159900,
    },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div>
        <h2 style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em' }}>
          Overdue &amp; Aging Receivables
        </h2>
        <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
          Real-time aging analysis of uncollected invoices across billing intervals.
        </p>
      </div>

      {/* Aging KPI Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
        {agingSummary.map((b) => (
          <div key={b.bucket} className="card" style={{ padding: '20px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
              <span style={{ fontSize: '12px', fontWeight: 700, color: 'var(--text-secondary)' }}>
                {b.bucket}
              </span>
              <span
                style={{
                  background: `${b.color}20`,
                  color: b.color,
                  fontSize: '11px',
                  fontWeight: 800,
                  padding: '2px 8px',
                  borderRadius: 'var(--radius-pill)',
                }}
              >
                {b.count} invoices
              </span>
            </div>
            <div style={{ fontSize: '22px', fontWeight: 800, color: b.amountPaise > 0 ? b.color : 'var(--text-muted)' }}>
              {formatCurrency(b.amountPaise, currency)}
            </div>
          </div>
        ))}
      </div>

      {/* Detailed Overdue Table */}
      <div className="card" style={{ padding: '24px' }}>
        <h3 style={{ fontSize: '16px', fontWeight: 700, marginBottom: '16px' }}>
          Delinquent Invoices Requiring Follow-up
        </h3>

        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', textAlign: 'left' }}>
              <th style={{ padding: '10px 12px' }}>Invoice</th>
              <th style={{ padding: '10px 12px' }}>Customer</th>
              <th style={{ padding: '10px 12px' }}>Due Date</th>
              <th style={{ padding: '10px 12px' }}>Days Overdue</th>
              <th style={{ padding: '10px 12px' }}>Total Amount</th>
              <th style={{ padding: '10px 12px' }}>Outstanding Balance</th>
            </tr>
          </thead>
          <tbody>
            {overdueInvoices.map((inv) => (
              <tr key={inv.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                <td style={{ padding: '12px', fontWeight: 700, fontFamily: 'var(--font-mono)' }}>
                  {inv.invoiceNumber}
                </td>
                <td style={{ padding: '12px' }}>
                  <div style={{ fontWeight: 600 }}>{inv.customerName}</div>
                  <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>{inv.contactName}</span>
                </td>
                <td style={{ padding: '12px', color: 'var(--text-muted)' }}>{inv.dueDate}</td>
                <td style={{ padding: '12px' }}>
                  <span
                    style={{
                      background: '#fef2f2',
                      color: '#b91c1c',
                      fontSize: '11px',
                      fontWeight: 700,
                      padding: '2px 8px',
                      borderRadius: 'var(--radius-pill)',
                    }}
                  >
                    {inv.daysOverdue} days late
                  </span>
                </td>
                <td style={{ padding: '12px', color: 'var(--text-muted)' }}>
                  {formatCurrency(inv.totalPaise, currency)}
                </td>
                <td style={{ padding: '12px', fontWeight: 700, color: '#ef4444' }}>
                  {formatCurrency(inv.balancePaise, currency)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

