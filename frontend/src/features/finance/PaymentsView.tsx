import React, { useState } from 'react';
import { api } from '../../services/api';
import { formatCurrency } from '../../utils/formatters';
import { CreditCard, Plus, CheckCircle, Search, Filter } from 'lucide-react';
import { PaymentModal } from './PaymentModal';

interface PaymentsViewProps {
  currency: 'INR' | 'USD';
  onNotification: (title: string, msg?: string, type?: 'success' | 'error' | 'info') => void;
}

export const PaymentsView: React.FC<PaymentsViewProps> = ({ currency, onNotification }) => {
  const [payments, setPayments] = useState([
    {
      id: 1,
      invoiceId: 1,
      invoiceNumber: 'INV-2026-000001',
      customerName: 'Acme Corp',
      amountPaise: 149900,
      method: 'CARD',
      paidOn: '2026-10-31',
      reference: 'TXN_987654',
      note: 'Full settlement via corporate card',
    },
    {
      id: 2,
      invoiceId: 2,
      invoiceNumber: 'INV-2026-000002',
      customerName: 'TechNova Global',
      amountPaise: 50000,
      method: 'BANK_TRANSFER',
      paidOn: '2026-10-25',
      reference: 'NEFT_441299',
      note: 'Partial payment received on invoice',
    },
  ]);

  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleRecordSuccess = (newPayment: any) => {
    setPayments((prev) => [newPayment, ...prev]);
    onNotification('Payment Recorded', `Recorded ${formatCurrency(newPayment.amountPaise, currency)}.`, 'success');
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em' }}>
            Payments &amp; Collections
          </h2>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            Offline payment settlements, bank wires, corporate cards, and partial payment tracking.
          </p>
        </div>

        <button onClick={() => setIsModalOpen(true)} className="btn-primary">
          <Plus size={15} /> Record Payment
        </button>
      </div>

      <div className="card" style={{ padding: '24px' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', textAlign: 'left' }}>
              <th style={{ padding: '10px 12px' }}>Payment #</th>
              <th style={{ padding: '10px 12px' }}>Invoice</th>
              <th style={{ padding: '10px 12px' }}>Customer</th>
              <th style={{ padding: '10px 12px' }}>Method</th>
              <th style={{ padding: '10px 12px' }}>Amount Paid</th>
              <th style={{ padding: '10px 12px' }}>Date</th>
              <th style={{ padding: '10px 12px' }}>Reference</th>
              <th style={{ padding: '10px 12px' }}>Note</th>
            </tr>
          </thead>
          <tbody>
            {payments.map((p) => (
              <tr key={p.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                <td style={{ padding: '12px', fontWeight: 700 }}>#{p.id}</td>
                <td style={{ padding: '12px', fontFamily: 'var(--font-mono)', color: 'var(--indigo-500)', fontWeight: 600 }}>
                  {p.invoiceNumber}
                </td>
                <td style={{ padding: '12px', fontWeight: 600 }}>{p.customerName}</td>
                <td style={{ padding: '12px' }}>
                  <span
                    style={{
                      background: 'var(--bg-card-secondary)',
                      padding: '2px 8px',
                      borderRadius: 'var(--radius-pill)',
                      fontSize: '11px',
                    }}
                  >
                    {p.method}
                  </span>
                </td>
                <td style={{ padding: '12px', fontWeight: 700, color: 'var(--emerald-600)' }}>
                  {formatCurrency(p.amountPaise, currency)}
                </td>
                <td style={{ padding: '12px', color: 'var(--text-muted)' }}>{p.paidOn}</td>
                <td style={{ padding: '12px', fontFamily: 'var(--font-mono)', fontSize: '12px' }}>
                  {p.reference}
                </td>
                <td style={{ padding: '12px', color: 'var(--text-secondary)' }}>{p.note || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {isModalOpen && (
        <PaymentModal
          invoiceId={2}
          invoiceNumber="INV-2026-0002"
          balancePaise={109900}
          currency={currency}
          onClose={() => setIsModalOpen(false)}
          onSuccess={handleRecordSuccess}
        />
      )}
    </div>
  );
};
