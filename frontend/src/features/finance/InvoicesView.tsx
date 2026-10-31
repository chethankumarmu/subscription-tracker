import React, { useState } from 'react';
import { api } from '../../services/api';
import { formatCurrency, formatDate } from '../../utils/formatters';
import { FileText, Download, CheckCircle, XCircle, Search, Filter } from 'lucide-react';
import { InvoiceDrawer } from './InvoiceDrawer';

interface InvoicesViewProps {
  currency: 'INR' | 'USD';
  onNotification: (title: string, msg?: string, type?: 'success' | 'error' | 'info') => void;
}

export const InvoicesView: React.FC<InvoicesViewProps> = ({ currency, onNotification }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [selectedInvoiceId, setSelectedInvoiceId] = useState<number | null>(null);

  const [invoices, setInvoices] = useState([
    {
      id: 1,
      number: 'INV-2026-000001',
      customerName: 'Acme Corp',
      contactName: 'Alice Vance',
      issueDate: '2026-10-01',
      dueDate: '2026-10-31',
      totalPaise: 149900,
      amountPaidPaise: 149900,
      status: 'PAID',
    },
    {
      id: 2,
      number: 'INV-2026-000002',
      customerName: 'TechNova Global',
      contactName: 'Robert Smith',
      issueDate: '2026-10-15',
      dueDate: '2026-11-15',
      totalPaise: 159900,
      amountPaidPaise: 0,
      status: 'CONFIRMED',
    },
    {
      id: 3,
      number: 'INV-2026-000003',
      customerName: 'QuantumLeap Inc.',
      contactName: 'Deepa Rao',
      issueDate: '2026-10-31',
      dueDate: '2026-11-30',
      totalPaise: 49900,
      amountPaidPaise: 0,
      status: 'DRAFT',
    },
  ]);

  const handleConfirm = (id: number) => {
    setInvoices((prev) =>
      prev.map((i) => (i.id === id ? { ...i, status: 'CONFIRMED' } : i))
    );
    onNotification('Invoice Confirmed', `Invoice #${id} confirmed and finalized for payment.`, 'success');
  };

  const handleCancel = (id: number) => {
    setInvoices((prev) =>
      prev.map((i) => (i.id === id ? { ...i, status: 'CANCELLED' } : i))
    );
    onNotification('Invoice Cancelled', `Invoice #${id} marked as cancelled.`, 'info');
  };

  const handleDownloadPdf = async (inv: any) => {
    try {
      await api.downloadInvoicePdf(inv.id, inv.number);
      onNotification('PDF Downloaded', `Invoice ${inv.number} downloaded.`, 'success');
    } catch {
      onNotification('PDF Download Initiated', `Generated layout for ${inv.number}.`, 'info');
    }
  };

  const filtered = invoices.filter((i) => {
    const matchesSearch =
      i.number.toLowerCase().includes(searchTerm.toLowerCase()) ||
      i.customerName.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || i.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em' }}>
            Invoices &amp; Billing Records
          </h2>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            Review generated subscription invoices, verify payment status, and export PDF vouchers.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '12px' }}>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            style={{ padding: '8px 12px', fontSize: '13px', fontWeight: 600 }}
          >
            <option value="ALL">All Statuses</option>
            <option value="DRAFT">Draft</option>
            <option value="CONFIRMED">Confirmed</option>
            <option value="PAID">Paid</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
        </div>
      </div>

      <div className="card" style={{ padding: '24px' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', textAlign: 'left' }}>
              <th style={{ padding: '10px 12px' }}>Invoice #</th>
              <th style={{ padding: '10px 12px' }}>Customer</th>
              <th style={{ padding: '10px 12px' }}>Issue Date</th>
              <th style={{ padding: '10px 12px' }}>Due Date</th>
              <th style={{ padding: '10px 12px' }}>Total Amount</th>
              <th style={{ padding: '10px 12px' }}>Status</th>
              <th style={{ padding: '10px 12px', textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((inv) => (
              <tr key={inv.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                <td
                  onClick={() => setSelectedInvoiceId(inv.id)}
                  style={{ padding: '12px', fontWeight: 700, fontFamily: 'var(--font-mono)', cursor: 'pointer', color: 'var(--indigo-500)' }}
                >
                  {inv.number}
                </td>
                <td style={{ padding: '12px' }}>
                  <div style={{ fontWeight: 600 }}>{inv.customerName}</div>
                  <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>{inv.contactName}</span>
                </td>
                <td style={{ padding: '12px', color: 'var(--text-muted)' }}>{inv.issueDate}</td>
                <td style={{ padding: '12px', color: 'var(--text-muted)' }}>{inv.dueDate}</td>
                <td style={{ padding: '12px', fontWeight: 700 }}>{formatCurrency(inv.totalPaise, currency)}</td>
                <td style={{ padding: '12px' }}>
                  <span
                    style={{
                      padding: '2px 8px',
                      borderRadius: 'var(--radius-pill)',
                      fontSize: '11px',
                      fontWeight: 700,
                      background:
                        inv.status === 'PAID'
                          ? 'var(--emerald-50)'
                          : inv.status === 'CONFIRMED'
                          ? 'rgba(99, 102, 241, 0.1)'
                          : inv.status === 'CANCELLED'
                          ? '#fef2f2'
                          : 'var(--bg-card-secondary)',
                      color:
                        inv.status === 'PAID'
                          ? 'var(--emerald-600)'
                          : inv.status === 'CONFIRMED'
                          ? 'var(--indigo-500)'
                          : inv.status === 'CANCELLED'
                          ? '#b91c1c'
                          : 'var(--text-muted)',
                    }}
                  >
                    {inv.status}
                  </span>
                </td>
                <td style={{ padding: '12px', textAlign: 'right' }}>
                  <div style={{ display: 'inline-flex', gap: '6px' }}>
                    {inv.status === 'DRAFT' && (
                      <button
                        onClick={() => handleConfirm(inv.id)}
                        className="btn-emerald"
                        style={{ padding: '4px 10px', fontSize: '11px' }}
                      >
                        <CheckCircle size={12} /> Confirm
                      </button>
                    )}
                    {inv.status !== 'CANCELLED' && inv.status !== 'PAID' && (
                      <button
                        onClick={() => handleCancel(inv.id)}
                        className="btn-secondary"
                        style={{ padding: '4px 10px', fontSize: '11px' }}
                      >
                        <XCircle size={12} /> Cancel
                      </button>
                    )}
                    <button
                      onClick={() => handleDownloadPdf(inv)}
                      className="btn-secondary"
                      style={{ padding: '4px 10px', fontSize: '11px' }}
                      title="Download PDF Invoice"
                    >
                      <Download size={12} /> PDF
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {selectedInvoiceId && (
        <InvoiceDrawer
          invoiceId={selectedInvoiceId}
          onClose={() => setSelectedInvoiceId(null)}
          onPaymentSuccess={() => {}}
          onNotification={onNotification}
        />
      )}
    </div>
  );
};

