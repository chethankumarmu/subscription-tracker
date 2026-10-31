import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';
import { formatCurrency, formatDate } from '../../utils/formatters';
import { StatusChip } from '../../components/ui/StatusChip';
import { InvoiceDrawer } from './InvoiceDrawer';
import { Search, Filter, Download, Plus, MoreVertical } from 'lucide-react';

interface ActivityProps {
  currency: 'INR' | 'USD';
  onNotification: (title: string, message?: string) => void;
}

export const Activity: React.FC<ActivityProps> = ({
  currency,
  onNotification,
}) => {
  const [invoices, setInvoices] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [search, setSearch] = useState('');
  const [selectedInvoiceId, setSelectedInvoiceId] = useState<number | null>(null);

  useEffect(() => {
    loadInvoices();
  }, [statusFilter]);

  const loadInvoices = async () => {
    try {
      setLoading(true);
      const data = await api.getInvoices(statusFilter);
      setInvoices(data);
    } catch (err) {
      console.error('Failed to load invoices:', err);
    } finally {
      setLoading(false);
    }
  };

  const filtered = invoices.filter(
    (inv) =>
      inv.number.toLowerCase().includes(search.toLowerCase()) ||
      inv.customerName?.toLowerCase().includes(search.toLowerCase()) ||
      inv.contactName?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
        <div>
          <h1 style={{ fontSize: '28px', fontWeight: 800, letterSpacing: '-0.02em' }}>
            Invoices & Billing Activities
          </h1>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            Audited financial ledger connected to PostgreSQL `invoices` table
          </p>
        </div>

        <button
          onClick={() => onNotification('Export Prepared', 'Exporting CSV of all historical ledger entries')}
          className="btn-secondary"
          style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          <Download size={16} /> Export CSV
        </button>
      </div>

      {/* Filter Row */}
      <div className="card" style={{ padding: '20px', marginBottom: '24px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap', gap: '16px' }}>
          <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
            <div style={{ position: 'relative' }}>
              <Search
                size={16}
                color="var(--text-muted)"
                style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }}
              />
              <input
                type="text"
                placeholder="Search by invoice number or company..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                style={{ paddingLeft: '36px', width: '320px' }}
              />
            </div>

            <div style={{ display: 'flex', gap: '6px' }}>
              {['ALL', 'PAID', 'CONFIRMED', 'DRAFT'].map((st) => (
                <button
                  key={st}
                  onClick={() => setStatusFilter(st)}
                  style={{
                    border: 'none',
                    background: statusFilter === st ? 'var(--indigo-500)' : 'var(--bg-card-secondary)',
                    color: statusFilter === st ? '#ffffff' : 'var(--text-secondary)',
                    padding: '8px 16px',
                    borderRadius: 'var(--radius-pill)',
                    fontSize: '12px',
                    fontWeight: 600,
                    cursor: 'pointer',
                    transition: 'all 150ms ease',
                  }}
                >
                  {st}
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Main Table */}
      <div className="card" style={{ padding: '24px' }}>
        {loading ? (
          <p style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
            Loading ledger from PostgreSQL...
          </p>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', minWidth: '800px' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', fontSize: '12px', textTransform: 'uppercase' }}>
                  <th style={{ padding: '12px 14px' }}>Invoice ID</th>
                  <th style={{ padding: '12px 14px' }}>Customer</th>
                  <th style={{ padding: '12px 14px' }}>Issue Date</th>
                  <th style={{ padding: '12px 14px' }}>Due Date</th>
                  <th style={{ padding: '12px 14px' }}>Total Amount</th>
                  <th style={{ padding: '12px 14px' }}>Balance Due</th>
                  <th style={{ padding: '12px 14px' }}>Status</th>
                  <th style={{ padding: '12px 14px', textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((inv) => (
                  <tr
                    key={inv.id}
                    onClick={() => setSelectedInvoiceId(inv.id)}
                    style={{
                      borderBottom: '1px solid var(--border-color)',
                      fontSize: '13px',
                      cursor: 'pointer',
                      transition: 'background 150ms ease',
                    }}
                    onMouseEnter={(e) => (e.currentTarget.style.background = 'var(--bg-card-secondary)')}
                    onMouseLeave={(e) => (e.currentTarget.style.background = 'transparent')}
                  >
                    <td style={{ padding: '14px', fontWeight: 700, color: 'var(--indigo-500)' }}>
                      {inv.number}
                    </td>
                    <td style={{ padding: '14px' }}>
                      <div style={{ fontWeight: 600 }}>{inv.customerName}</div>
                      <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>{inv.contactName}</div>
                    </td>
                    <td style={{ padding: '14px', color: 'var(--text-secondary)' }}>
                      {formatDate(inv.issueDate)}
                    </td>
                    <td style={{ padding: '14px', color: 'var(--text-secondary)' }}>
                      {formatDate(inv.dueDate)}
                    </td>
                    <td style={{ padding: '14px', fontWeight: 700 }}>
                      {formatCurrency(inv.totalPaise, currency)}
                    </td>
                    <td style={{ padding: '14px', fontWeight: 700, color: inv.balancePaise > 0 ? '#ef4444' : 'var(--emerald-600)' }}>
                      {formatCurrency(inv.balancePaise, currency)}
                    </td>
                    <td style={{ padding: '14px' }}>
                      <StatusChip status={inv.status} />
                    </td>
                    <td style={{ padding: '14px', textAlign: 'right' }} onClick={(e) => e.stopPropagation()}>
                      <button
                        onClick={() => setSelectedInvoiceId(inv.id)}
                        className="btn-secondary"
                        style={{ padding: '4px 12px', fontSize: '12px' }}
                      >
                        View Details
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Invoice Drawer */}
      {selectedInvoiceId && (
        <InvoiceDrawer
          invoiceId={selectedInvoiceId}
          currency={currency}
          onClose={() => setSelectedInvoiceId(null)}
          onPaymentSuccess={(msg) => {
            onNotification('Payment Settled', msg);
            loadInvoices();
          }}
        />
      )}
    </div>
  );
};
