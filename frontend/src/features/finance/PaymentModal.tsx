import React, { useState } from 'react';
import { api } from '../../services/api';
import { formatCurrency } from '../../utils/formatters';
import { X, CreditCard, QrCode, Building2, Banknote, ShieldCheck } from 'lucide-react';

interface PaymentModalProps {
  invoiceId: number;
  invoiceNumber: string;
  balancePaise: number;
  currency: 'INR' | 'USD';
  onClose: () => void;
  onSuccess: (message: string) => void;
}

export const PaymentModal: React.FC<PaymentModalProps> = ({
  invoiceId,
  invoiceNumber,
  balancePaise,
  currency,
  onClose,
  onSuccess,
}) => {
  const [method, setMethod] = useState<'CARD' | 'UPI' | 'BANK_TRANSFER' | 'CASH'>('UPI');
  const initialInput = currency === 'USD'
    ? ((balancePaise / 100) / 85.0).toFixed(2)
    : (balancePaise / 100).toFixed(2);
  const [amountInput, setAmountInput] = useState<string>(initialInput);
  const [reference, setReference] = useState<string>('');
  const [note, setNote] = useState<string>('');
  const [submitting, setSubmitting] = useState<boolean>(false);

  const parsedVal = parseFloat(amountInput || '0');
  const calculatedPaise = currency === 'USD'
    ? Math.round(parsedVal * 85.0 * 100)
    : Math.round(parsedVal * 100);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (calculatedPaise <= 0) return;
    setSubmitting(true);
    try {
      await api.recordPayment({
        invoiceId,
        amountPaise: calculatedPaise,
        method,
        reference: reference || `REF_${Date.now() % 1000000}`,
        note,
      });
      onSuccess(`Payment of ${formatCurrency(calculatedPaise, currency)} recorded against ${invoiceNumber}!`);
      onClose();
    } catch (err: any) {
      alert(`Error recording payment: ${err.message}`);
    } finally {
      setSubmitting(false);
    }
  };

  const methods = [
    { id: 'UPI', label: 'UPI / QR', icon: QrCode },
    { id: 'CARD', label: 'Debit/Credit Card', icon: CreditCard },
    { id: 'BANK_TRANSFER', label: 'NEFT / RTGS', icon: Building2 },
    { id: 'CASH', label: 'Cash / Cheque', icon: Banknote },
  ];

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
            <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Record Payment Settlement</h3>
            <p style={{ fontSize: '13px', color: 'var(--text-muted)', marginTop: '2px' }}>
              Invoice {invoiceNumber} • Outstanding: {formatCurrency(balancePaise, currency)}
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

        <form onSubmit={handleSubmit} style={{ padding: '24px 28px' }}>
          {/* Method Selection */}
          <div style={{ marginBottom: '20px' }}>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, marginBottom: '8px' }}>
              Payment Channel
            </label>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
              {methods.map((m) => {
                const Icon = m.icon;
                const isSelected = method === m.id;
                return (
                  <button
                    key={m.id}
                    type="button"
                    onClick={() => setMethod(m.id as any)}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '8px',
                      padding: '10px 14px',
                      borderRadius: 'var(--radius-inner)',
                      border: `1px solid ${isSelected ? 'var(--indigo-500)' : 'var(--border-color)'}`,
                      background: isSelected ? 'var(--violet-50)' : 'var(--bg-card)',
                      color: isSelected ? 'var(--indigo-600)' : 'var(--text-primary)',
                      cursor: 'pointer',
                      fontWeight: isSelected ? 600 : 500,
                      fontSize: '13px',
                      transition: 'all 150ms ease',
                    }}
                  >
                    <Icon size={16} />
                    {m.label}
                  </button>
                );
              })}
            </div>
          </div>

          {/* Amount Input */}
          <div style={{ marginBottom: '20px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
              <label style={{ fontSize: '13px', fontWeight: 600 }}>
                Amount ({currency === 'USD' ? '$ USD' : '₹ INR'})
              </label>
              <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                {currency === 'USD'
                  ? `≈ ₹${(calculatedPaise / 100).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })} INR`
                  : `≈ $${((calculatedPaise / 100) / 85.0).toFixed(2)} USD`}
              </span>
            </div>
            <input
              type="number"
              step="any"
              value={amountInput}
              onChange={(e) => setAmountInput(e.target.value)}
              style={{ width: '100%', fontSize: '16px', fontWeight: 700 }}
              required
            />
          </div>

          {/* Reference */}
          <div style={{ marginBottom: '20px' }}>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, marginBottom: '8px' }}>
              Transaction Reference / UTR
            </label>
            <input
              type="text"
              placeholder="e.g. UPI/HDFC/98218392"
              value={reference}
              onChange={(e) => setReference(e.target.value)}
              style={{ width: '100%' }}
            />
          </div>

          {/* Note */}
          <div style={{ marginBottom: '24px' }}>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, marginBottom: '8px' }}>
              Notes (Optional)
            </label>
            <textarea
              rows={2}
              placeholder="Any additional settlement details..."
              value={note}
              onChange={(e) => setNote(e.target.value)}
              style={{ width: '100%' }}
            />
          </div>

          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
              marginBottom: '24px',
              fontSize: '12px',
              color: 'var(--text-muted)',
            }}
          >
            <ShieldCheck size={16} color="var(--emerald-500)" />
            <span>Saves directly into PostgreSQL `payments` table and automatically updates invoice status to PAID.</span>
          </div>

          <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
            <button type="button" onClick={onClose} className="btn-secondary">
              Cancel
            </button>
            <button type="submit" disabled={submitting} className="btn-emerald">
              {submitting ? 'Recording...' : 'Record Payment'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

