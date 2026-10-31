import React, { useState, useEffect } from 'react';
import { InvoiceDetail } from '../../types';
import { api } from '../../services/api';
import { formatCurrency, formatDate } from '../../utils/formatters';
import { StatusChip } from '../../components/ui/StatusChip';
import { PaymentModal } from './PaymentModal';
import { X, Printer, CreditCard, Building, Calendar, Receipt } from 'lucide-react';

interface InvoiceDrawerProps {
  invoiceId: number;
  currency?: 'INR' | 'USD';
  onClose: () => void;
  onPaymentSuccess?: (msg?: string) => void;
  onNotification?: (title: string, msg?: string, type?: 'success' | 'error' | 'info') => void;
}

export const InvoiceDrawer: React.FC<InvoiceDrawerProps> = ({
  invoiceId,
  currency = 'INR',
  onClose,
  onPaymentSuccess = () => {},
  onNotification,
}) => {
  const [data, setData] = useState<InvoiceDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [showPaymentModal, setShowPaymentModal] = useState(false);

  useEffect(() => {
    loadDetails();
  }, [invoiceId]);

  const loadDetails = async () => {
    try {
      setLoading(true);
      const res = await api.getInvoiceDetails(invoiceId);
      setData(res);
    } catch (err) {
      console.error('Failed to load invoice details:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading || !data) {
    return (
      <div className="modal-overlay" onClick={onClose}>
        <div className="modal-content" style={{ padding: '40px', textAlign: 'center' }}>
          <p style={{ color: 'var(--text-muted)' }}>Loading invoice from PostgreSQL...</p>
        </div>
      </div>
    );
  }

  const { invoice, customer, lines, payments, balancePaise } = data;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div
        className="modal-content"
        onClick={(e) => e.stopPropagation()}
        style={{ maxWidth: '680px' }}
      >
        {/* Header */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: '24px 28px',
            borderBottom: '1px solid var(--border-color)',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <div
              style={{
                width: '40px',
                height: '40px',
                borderRadius: '50%',
                background: 'var(--emerald-50)',
                color: 'var(--emerald-600)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <Receipt size={20} />
            </div>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <h3 style={{ fontSize: '18px', fontWeight: 800 }}>{invoice.number}</h3>
                <StatusChip status={invoice.status} />
              </div>
              <p style={{ fontSize: '13px', color: 'var(--text-muted)', marginTop: '2px' }}>
                Issued: {formatDate(invoice.issueDate)} • Due: {formatDate(invoice.dueDate)}
              </p>
            </div>
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

        {/* Content */}
        <div style={{ padding: '24px 28px' }}>
          {/* Customer Card */}
          <div
            style={{
              background: 'var(--bg-card-secondary)',
              borderRadius: 'var(--radius-inner)',
              padding: '16px',
              marginBottom: '24px',
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <div>
                <p style={{ fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>
                  Billed To Customer
                </p>
                <p style={{ fontSize: '16px', fontWeight: 700, marginTop: '2px' }}>
                  {customer?.companyName || 'Corporate Client'}
                </p>
                <p style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>
                  Attn: {customer?.contactName || 'Representative'} {customer?.phone ? `• ${customer.phone}` : ''}
                </p>
                {customer?.gstin && (
                  <p style={{ fontSize: '12px', color: 'var(--indigo-500)', fontWeight: 600, marginTop: '4px' }}>
                    GSTIN: {customer.gstin}
                  </p>
                )}
              </div>
              <div style={{ textAlign: 'right' }}>
                <p style={{ fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>
                  Amount Due
                </p>
                <p style={{ fontSize: '20px', fontWeight: 800, color: balancePaise > 0 ? '#ef4444' : 'var(--emerald-600)', marginTop: '2px' }}>
                  {formatCurrency(balancePaise, currency)}
                </p>
              </div>
            </div>
          </div>

          {/* Line Items Table */}
          <div style={{ marginBottom: '24px' }}>
            <h4 style={{ fontSize: '13px', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', marginBottom: '12px' }}>
              Line Items
            </h4>
            <div style={{ border: '1px solid var(--border-color)', borderRadius: 'var(--radius-inner)', overflow: 'hidden' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '13px' }}>
                <thead style={{ background: 'var(--bg-card-secondary)' }}>
                  <tr>
                    <th style={{ padding: '10px 14px', fontWeight: 600 }}>Description</th>
                    <th style={{ padding: '10px 14px', fontWeight: 600, textAlign: 'center' }}>Qty</th>
                    <th style={{ padding: '10px 14px', fontWeight: 600, textAlign: 'right' }}>Unit Price</th>
                    <th style={{ padding: '10px 14px', fontWeight: 600, textAlign: 'right' }}>Total</th>
                  </tr>
                </thead>
                <tbody>
                  {lines.map((l) => (
                    <tr key={l.id} style={{ borderTop: '1px solid var(--border-color)' }}>
                      <td style={{ padding: '12px 14px', fontWeight: 500 }}>{l.description}</td>
                      <td style={{ padding: '12px 14px', textAlign: 'center' }}>{l.quantity}</td>
                      <td style={{ padding: '12px 14px', textAlign: 'right' }}>{formatCurrency(l.unitPricePaise, currency)}</td>
                      <td style={{ padding: '12px 14px', textAlign: 'right', fontWeight: 600 }}>
                        {formatCurrency(l.lineTotalPaise, currency)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Summary Breakdown */}
          <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '24px' }}>
            <div style={{ width: '280px', fontSize: '13px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px' }}>
                <span style={{ color: 'var(--text-secondary)' }}>Subtotal</span>
                <span>{formatCurrency(invoice.subtotalPaise, currency)}</span>
              </div>
              {invoice.discountPaise > 0 && (
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px', color: 'var(--emerald-600)' }}>
                  <span>Applied Discount</span>
                  <span>-{formatCurrency(invoice.discountPaise, currency)}</span>
                </div>
              )}
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px' }}>
                <span style={{ color: 'var(--text-secondary)' }}>GST (18%)</span>
                <span>{formatCurrency(invoice.taxPaise, currency)}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px', fontWeight: 700 }}>
                <span>Invoice Total</span>
                <span>{formatCurrency(invoice.totalPaise, currency)}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px', color: 'var(--emerald-600)' }}>
                <span>Amount Paid</span>
                <span>-{formatCurrency(invoice.amountPaidPaise, currency)}</span>
              </div>
              <div
                style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  paddingTop: '6px',
                  borderTop: '1px solid var(--border-color)',
                  fontWeight: 800,
                  fontSize: '15px',
                }}
              >
                <span>Balance Due</span>
                <span style={{ color: balancePaise > 0 ? '#ef4444' : 'var(--emerald-600)' }}>
                  {formatCurrency(balancePaise, currency)}
                </span>
              </div>
            </div>
          </div>

          {/* Past Payments History */}
          {payments && payments.length > 0 && (
            <div style={{ marginBottom: '24px' }}>
              <h4 style={{ fontSize: '13px', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', marginBottom: '10px' }}>
                Payment Transactions
              </h4>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                {payments.map((p) => (
                  <div
                    key={p.id}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      padding: '10px 14px',
                      background: 'var(--bg-card-secondary)',
                      borderRadius: 'var(--radius-inner)',
                      fontSize: '13px',
                    }}
                  >
                    <div>
                      <span style={{ fontWeight: 600 }}>{p.method}</span> •{' '}
                      <span style={{ color: 'var(--text-muted)' }}>{p.reference}</span>
                      <p style={{ fontSize: '11px', color: 'var(--text-muted)' }}>Paid on {formatDate(p.paidOn)}</p>
                    </div>
                    <span style={{ fontWeight: 700, color: 'var(--emerald-600)' }}>
                      {formatCurrency(p.amountPaise, currency)}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Actions */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <button
              onClick={() => window.print()}
              className="btn-secondary"
              style={{ display: 'flex', alignItems: 'center', gap: '6px' }}
            >
              <Printer size={16} /> Print Receipt
            </button>

            {balancePaise > 0 && (
              <button
                onClick={() => setShowPaymentModal(true)}
                className="btn-emerald"
                style={{ display: 'flex', alignItems: 'center', gap: '6px' }}
              >
                <CreditCard size={16} /> Record Payment
              </button>
            )}
          </div>
        </div>

        {/* Payment Modal */}
        {showPaymentModal && (
          <PaymentModal
            invoiceId={invoice.id}
            invoiceNumber={invoice.number}
            balancePaise={balancePaise}
            currency={currency}
            onClose={() => setShowPaymentModal(false)}
            onSuccess={(msg) => {
              onPaymentSuccess(msg);
              setShowPaymentModal(false);
              loadDetails();
            }}
          />
        )}
      </div>
    </div>
  );
};

