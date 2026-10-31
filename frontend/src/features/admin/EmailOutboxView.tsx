import React, { useState } from 'react';
import { Mail, CheckCircle2, Clock, AlertCircle, FileText } from 'lucide-react';

export const EmailOutboxView: React.FC = () => {
  const emails = [
    {
      id: 201,
      toAddress: 'cust@brightloop.test',
      subject: 'Brightloop Invoice: INV-2026-000001',
      templateKey: 'INVOICE',
      attachmentName: 'INV-2026-000001.pdf',
      status: 'SENT',
      attempts: 1,
      sentAt: '2026-10-31 14:31:02',
    },
    {
      id: 202,
      toAddress: 'alice@acme.com',
      subject: 'Renewal Reminder: SUB-2026-000002 expires in 7 day(s)',
      templateKey: 'RENEWAL_REMINDER',
      attachmentName: null,
      status: 'SENT',
      attempts: 1,
      sentAt: '2026-10-31 09:00:15',
    },
    {
      id: 203,
      toAddress: 'finance@startup.io',
      subject: 'Brightloop Invoice: INV-2026-000003',
      templateKey: 'INVOICE',
      attachmentName: 'INV-2026-000003.pdf',
      status: 'PENDING',
      attempts: 0,
      sentAt: null,
    },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div>
        <h2 style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em' }}>
          Transactional Email Outbox
        </h2>
        <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
          Read-only monitor of queued SMTP notifications and dynamic PDF invoice attachments.
        </p>
      </div>

      <div className="card" style={{ padding: '24px' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', textAlign: 'left' }}>
              <th style={{ padding: '10px 12px' }}>ID</th>
              <th style={{ padding: '10px 12px' }}>Recipient</th>
              <th style={{ padding: '10px 12px' }}>Subject</th>
              <th style={{ padding: '10px 12px' }}>Template</th>
              <th style={{ padding: '10px 12px' }}>Dynamic Attachment</th>
              <th style={{ padding: '10px 12px' }}>Attempts</th>
              <th style={{ padding: '10px 12px' }}>Status</th>
              <th style={{ padding: '10px 12px' }}>Sent Timestamp</th>
            </tr>
          </thead>
          <tbody>
            {emails.map((e) => (
              <tr key={e.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                <td style={{ padding: '12px', fontWeight: 700 }}>#{e.id}</td>
                <td style={{ padding: '12px', fontFamily: 'var(--font-mono)' }}>{e.toAddress}</td>
                <td style={{ padding: '12px', fontWeight: 600 }}>{e.subject}</td>
                <td style={{ padding: '12px' }}>
                  <span
                    style={{
                      background: 'var(--bg-card-secondary)',
                      padding: '2px 8px',
                      borderRadius: 'var(--radius-pill)',
                      fontSize: '11px',
                    }}
                  >
                    {e.templateKey}
                  </span>
                </td>
                <td style={{ padding: '12px' }}>
                  {e.attachmentName ? (
                    <span
                      style={{
                        display: 'inline-flex',
                        alignItems: 'center',
                        gap: '4px',
                        color: 'var(--indigo-500)',
                        fontSize: '12px',
                        fontWeight: 600,
                      }}
                    >
                      <FileText size={13} /> {e.attachmentName}
                    </span>
                  ) : (
                    <span style={{ color: 'var(--text-muted)' }}>None</span>
                  )}
                </td>
                <td style={{ padding: '12px', textAlign: 'center' }}>{e.attempts}/3</td>
                <td style={{ padding: '12px' }}>
                  <span
                    style={{
                      padding: '2px 8px',
                      borderRadius: 'var(--radius-pill)',
                      fontSize: '11px',
                      fontWeight: 700,
                      background:
                        e.status === 'SENT'
                          ? 'var(--emerald-50)'
                          : e.status === 'FAILED'
                          ? '#fef2f2'
                          : 'rgba(99, 102, 241, 0.1)',
                      color:
                        e.status === 'SENT'
                          ? 'var(--emerald-600)'
                          : e.status === 'FAILED'
                          ? '#b91c1c'
                          : 'var(--indigo-500)',
                    }}
                  >
                    {e.status}
                  </span>
                </td>
                <td style={{ padding: '12px', color: 'var(--text-muted)' }}>{e.sentAt || 'Queued'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

