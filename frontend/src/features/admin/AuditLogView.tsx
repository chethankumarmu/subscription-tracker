import React, { useState } from 'react';
import { Shield, Clock, Search, Filter } from 'lucide-react';

export const AuditLogView: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');

  const logs = [
    {
      id: 101,
      actor: 'admin@brightloop.test (ADMIN)',
      action: 'BILLING_RUN_EXECUTED',
      target: 'Date: 2026-10-31',
      details: 'Evaluated 4 active subscriptions; 1 invoice created, 3 skipped.',
      timestamp: '2026-10-31 14:30:15',
    },
    {
      id: 102,
      actor: 'staff@brightloop.test (STAFF)',
      action: 'PAYMENT_RECORDED',
      target: 'Invoice INV-2026-000001',
      details: 'Payment of ₹1,499.00 recorded via CARD. Invoice marked PAID.',
      timestamp: '2026-10-31 12:15:02',
    },
    {
      id: 103,
      actor: 'admin@brightloop.test (ADMIN)',
      action: 'PLAN_VERSION_PUBLISHED',
      target: 'Plan: GROWTH v2',
      details: 'Published new pricing: ₹1,599.00/mo with 15 users limit.',
      timestamp: '2026-10-30 09:12:44',
    },
    {
      id: 104,
      actor: 'cust@brightloop.test (CUSTOMER)',
      action: 'RENEWAL_REQUESTED',
      target: 'Subscription SUB-2026-000002',
      details: 'Created Support Ticket TKT-REN-SUB-2026-000002 for staff review.',
      timestamp: '2026-10-29 18:45:10',
    },
  ];

  const filteredLogs = logs.filter(
    (l) =>
      l.action.toLowerCase().includes(searchTerm.toLowerCase()) ||
      l.actor.toLowerCase().includes(searchTerm.toLowerCase()) ||
      l.details.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em' }}>
            System Audit Log
          </h2>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            Read-only chronological trail of sensitive administrative actions and security events.
          </p>
        </div>

        <div style={{ position: 'relative', width: '280px' }}>
          <Search size={15} style={{ position: 'absolute', left: '12px', top: '11px', color: 'var(--text-muted)' }} />
          <input
            type="text"
            placeholder="Search action, actor..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ width: '100%', paddingLeft: '34px', fontSize: '12px' }}
          />
        </div>
      </div>

      <div className="card" style={{ padding: '24px' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', textAlign: 'left' }}>
              <th style={{ padding: '10px 12px' }}>Timestamp</th>
              <th style={{ padding: '10px 12px' }}>Actor</th>
              <th style={{ padding: '10px 12px' }}>Event Action</th>
              <th style={{ padding: '10px 12px' }}>Target Resource</th>
              <th style={{ padding: '10px 12px' }}>Audit Details</th>
            </tr>
          </thead>
          <tbody>
            {filteredLogs.map((l) => (
              <tr key={l.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                <td style={{ padding: '12px', color: 'var(--text-muted)', whiteSpace: 'nowrap' }}>{l.timestamp}</td>
                <td style={{ padding: '12px', fontWeight: 600 }}>{l.actor}</td>
                <td style={{ padding: '12px' }}>
                  <span
                    style={{
                      background: 'rgba(99, 102, 241, 0.1)',
                      color: 'var(--indigo-500)',
                      fontSize: '11px',
                      fontWeight: 700,
                      padding: '2px 8px',
                      borderRadius: 'var(--radius-pill)',
                    }}
                  >
                    {l.action}
                  </span>
                </td>
                <td style={{ padding: '12px', fontWeight: 600 }}>{l.target}</td>
                <td style={{ padding: '12px', color: 'var(--text-secondary)' }}>{l.details}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

