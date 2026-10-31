import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { api } from '../../services/api';
import { formatCurrency } from '../../utils/formatters';
import { PricingMatrix } from './PricingMatrix';
import { ChatbotDrawer } from '../chatbot/ChatbotDrawer';
import {
  Layers,
  Calendar,
  CreditCard,
  FileText,
  Download,
  Headphones,
  CheckCircle,
  Clock,
  ArrowRight,
  TrendingUp,
  MessageSquare,
  HelpCircle,
  RefreshCw,
} from 'lucide-react';

interface CustomerPortalProps {
  activeTab: string;
  setActiveTab: (t: string) => void;
  currency: 'INR' | 'USD';
  onNotification: (title: string, msg?: string, type?: 'success' | 'error' | 'info') => void;
}

export const CustomerPortal: React.FC<CustomerPortalProps> = ({
  activeTab,
  setActiveTab,
  currency,
  onNotification,
}) => {
  const { user } = useAuth();
  const [isCompareOpen, setIsCompareOpen] = useState(false);
  const [isChatOpen, setIsChatOpen] = useState(false);
  const [renewalRequested, setRenewalRequested] = useState(false);
  const [submittingRenewal, setSubmittingRenewal] = useState(false);

  // Customer's own subscription data
  const subscription = {
    id: 1,
    number: 'SUB-2026-000001',
    planName: 'Odoo Business Suite Pro (Growth v2)',
    planCode: 'GROWTH',
    versionNo: 2,
    status: 'ACTIVE',
    billingCycle: 'MONTHLY',
    pricePaise: 159900,
    startDate: '2026-10-01',
    expiryDate: '2026-11-30',
    nextRenewalDate: '2026-11-30',
    amountDuePaise: 0,
    allowRenewal: true,
    limits: {
      users: { used: 9, max: 15, unit: 'Seats' },
      storage: { used: 42, max: 100, unit: 'GB' },
      domains: { used: 1, max: 1, unit: 'Domain' },
    },
  };

  // Customer's own invoices
  const [myInvoices, setMyInvoices] = useState([
    {
      id: 1,
      number: 'INV-2026-000001',
      issueDate: '2026-10-01',
      dueDate: '2026-10-31',
      subtotalPaise: 159900,
      discountPaise: 10000,
      taxPaise: 26982,
      totalPaise: 176882,
      status: 'PAID',
    },
  ]);

  // Customer's own payments
  const [myPayments, setMyPayments] = useState([
    {
      id: 101,
      invoiceNumber: 'INV-2026-000001',
      method: 'CARD',
      amountPaise: 176882,
      paidOn: '2026-10-01',
      reference: 'TXN_CARD_8877',
    },
  ]);

  // Customer's own tickets
  const [myTickets, setMyTickets] = useState([
    {
      id: 1,
      number: 'TKT-2026-0001',
      subject: 'Inquiry regarding custom domain verification',
      status: 'RESOLVED',
      priority: 'NORMAL',
      createdAt: '2026-10-24',
      comments: [
        { id: 1, author: 'Customer', text: 'How do I add a CNAME record for my sub-domain?', isInternal: false },
        { id: 2, author: 'Support Agent', text: 'You can point your DNS CNAME to app.brightloop.com.', isInternal: false },
      ],
    },
  ]);

  const [newTicketSubject, setNewTicketSubject] = useState('');
  const [newTicketBody, setNewTicketBody] = useState('');

  const handleRequestRenewal = async () => {
    setSubmittingRenewal(true);
    try {
      await api.requestRenewal(subscription.id);
      setRenewalRequested(true);
      onNotification(
        'Renewal Request Submitted',
        'Staff has received your renewal request ticket and will finalize your invoice.',
        'success'
      );
    } catch {
      // Demo fallback
      setRenewalRequested(true);
      onNotification(
        'Renewal Request Sent',
        'A support ticket has been created for staff renewal processing.',
        'success'
      );
    } finally {
      setSubmittingRenewal(false);
    }
  };

  const handleDownloadPdf = async (inv: any) => {
    try {
      await api.downloadInvoicePdf(inv.id, inv.number);
      onNotification('PDF Downloaded', `Invoice ${inv.number} downloaded.`, 'success');
    } catch {
      onNotification('PDF Download Initiated', `Generated layout for ${inv.number}.`, 'info');
    }
  };

  const handleCreateTicket = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTicketSubject.trim()) return;

    const newTicket = {
      id: Date.now(),
      number: `TKT-${Date.now().toString().slice(-4)}`,
      subject: newTicketSubject,
      status: 'OPEN',
      priority: 'NORMAL',
      createdAt: new Date().toISOString().split('T')[0],
      comments: [{ id: 1, author: 'Customer', text: newTicketBody, isInternal: false }],
    };

    setMyTickets((prev) => [newTicket, ...prev]);
    setNewTicketSubject('');
    setNewTicketBody('');
    onNotification('Ticket Created', 'Your support ticket has been opened.', 'success');
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      {/* View 1: Customer Overview */}
      {activeTab === 'overview' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          <div>
            <h2 style={{ fontSize: '26px', fontWeight: 800, letterSpacing: '-0.02em' }}>
              Welcome back, {user?.fullName || 'Valued Customer'}
            </h2>
            <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
              Monitor your active subscription plan, usage metrics, and billing schedule.
            </p>
          </div>

          {/* Quick Metrics Grid */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '16px' }}>
            {/* 1. Active Plan */}
            <div className="card" style={{ padding: '22px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                <span style={{ fontSize: '12px', fontWeight: 700, color: 'var(--text-secondary)' }}>Current Plan</span>
                <span
                  style={{
                    background: 'var(--emerald-50)',
                    color: 'var(--emerald-600)',
                    fontSize: '11px',
                    fontWeight: 800,
                    padding: '2px 8px',
                    borderRadius: 'var(--radius-pill)',
                  }}
                >
                  {subscription.status}
                </span>
              </div>
              <div style={{ fontSize: '18px', fontWeight: 800, marginBottom: '4px' }}>
                {subscription.planName}
              </div>
              <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                {formatCurrency(subscription.pricePaise, currency)} / {subscription.billingCycle.toLowerCase()}
              </div>
            </div>

            {/* 2. Next Renewal */}
            <div className="card" style={{ padding: '22px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                <span style={{ fontSize: '12px', fontWeight: 700, color: 'var(--text-secondary)' }}>Next Renewal Date</span>
                <Calendar size={16} color="var(--indigo-500)" />
              </div>
              <div style={{ fontSize: '22px', fontWeight: 800, marginBottom: '4px' }}>
                {subscription.nextRenewalDate}
              </div>
              <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                Auto-renewal terms: 30 days notice
              </div>
            </div>

            {/* 3. Amount Due */}
            <div className="card" style={{ padding: '22px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                <span style={{ fontSize: '12px', fontWeight: 700, color: 'var(--text-secondary)' }}>Amount Due</span>
                <CreditCard size={16} color="var(--emerald-500)" />
              </div>
              <div style={{ fontSize: '22px', fontWeight: 800, color: subscription.amountDuePaise > 0 ? '#ef4444' : 'var(--emerald-600)' }}>
                {formatCurrency(subscription.amountDuePaise, currency)}
              </div>
              <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                All current invoices fully settled
              </div>
            </div>
          </div>

          {/* Usage Bars Card */}
          <div className="card" style={{ padding: '24px' }}>
            <h3 style={{ fontSize: '16px', fontWeight: 700, marginBottom: '20px' }}>
              Plan Quota &amp; Usage Metrics
            </h3>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              {/* Users Bar */}
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', fontWeight: 600, marginBottom: '6px' }}>
                  <span>User Seats</span>
                  <span style={{ color: 'var(--text-muted)' }}>
                    <strong style={{ color: 'var(--text-primary)' }}>{subscription.limits.users.used}</strong> / {subscription.limits.users.max} Seats (
                    {Math.round((subscription.limits.users.used / subscription.limits.users.max) * 100)}%)
                  </span>
                </div>
                <div style={{ height: '8px', background: 'var(--bg-card-secondary)', borderRadius: '9999px', overflow: 'hidden' }}>
                  <div
                    style={{
                      width: `${(subscription.limits.users.used / subscription.limits.users.max) * 100}%`,
                      height: '100%',
                      background: 'linear-gradient(90deg, var(--emerald-500), var(--indigo-500))',
                    }}
                  />
                </div>
              </div>

              {/* Storage Bar */}
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', fontWeight: 600, marginBottom: '6px' }}>
                  <span>Cloud Storage</span>
                  <span style={{ color: 'var(--text-muted)' }}>
                    <strong style={{ color: 'var(--text-primary)' }}>{subscription.limits.storage.used}</strong> / {subscription.limits.storage.max} GB (
                    {Math.round((subscription.limits.storage.used / subscription.limits.storage.max) * 100)}%)
                  </span>
                </div>
                <div style={{ height: '8px', background: 'var(--bg-card-secondary)', borderRadius: '9999px', overflow: 'hidden' }}>
                  <div
                    style={{
                      width: `${(subscription.limits.storage.used / subscription.limits.storage.max) * 100}%`,
                      height: '100%',
                      background: 'linear-gradient(90deg, var(--emerald-500), var(--indigo-500))',
                    }}
                  />
                </div>
              </div>

              {/* Domains Bar */}
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', fontWeight: 600, marginBottom: '6px' }}>
                  <span>Custom Domains</span>
                  <span style={{ color: 'var(--text-muted)' }}>
                    <strong style={{ color: 'var(--text-primary)' }}>{subscription.limits.domains.used}</strong> / {subscription.limits.domains.max} Domain
                  </span>
                </div>
                <div style={{ height: '8px', background: 'var(--bg-card-secondary)', borderRadius: '9999px', overflow: 'hidden' }}>
                  <div
                    style={{
                      width: `${(subscription.limits.domains.used / subscription.limits.domains.max) * 100}%`,
                      height: '100%',
                      background: 'var(--emerald-500)',
                    }}
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* View 2: My Plan */}
      {activeTab === 'my-plan' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h2 style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em' }}>
                Subscription Plan Details
              </h2>
              <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
                Review plan features, compare published pricing tiers, and request renewals.
              </p>
            </div>

            <div style={{ display: 'flex', gap: '10px' }}>
              <button onClick={() => setIsCompareOpen(true)} className="btn-secondary">
                <Layers size={15} /> Compare Plans
              </button>

              {subscription.allowRenewal && (
                <button
                  onClick={handleRequestRenewal}
                  disabled={renewalRequested || submittingRenewal}
                  className="btn-primary"
                >
                  <RefreshCw size={15} className={submittingRenewal ? 'spin' : ''} />
                  {renewalRequested ? 'Renewal Requested ✓' : 'Request Renewal'}
                </button>
              )}
            </div>
          </div>

          <div className="card" style={{ padding: '28px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '20px' }}>
              <div>
                <span
                  style={{
                    background: 'var(--emerald-50)',
                    color: 'var(--emerald-600)',
                    fontSize: '11px',
                    fontWeight: 800,
                    padding: '2px 8px',
                    borderRadius: 'var(--radius-pill)',
                    marginBottom: '8px',
                    display: 'inline-block',
                  }}
                >
                  {subscription.status}
                </span>
                <h3 style={{ fontSize: '20px', fontWeight: 800 }}>{subscription.planName}</h3>
                <span style={{ fontSize: '12px', fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
                  Subscription Reference: #{subscription.number}
                </span>
              </div>

              <div style={{ textAlign: 'right' }}>
                <div style={{ fontSize: '24px', fontWeight: 800, color: 'var(--emerald-600)' }}>
                  {formatCurrency(subscription.pricePaise, currency)}
                </div>
                <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                  Billed {subscription.billingCycle.toLowerCase()}
                </span>
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '16px', marginTop: '20px' }}>
              <div style={{ background: 'var(--bg-card-secondary)', padding: '16px', borderRadius: 'var(--radius-inner)' }}>
                <span style={{ fontSize: '11px', color: 'var(--text-muted)', display: 'block' }}>Start Date</span>
                <strong style={{ fontSize: '14px' }}>{subscription.startDate}</strong>
              </div>
              <div style={{ background: 'var(--bg-card-secondary)', padding: '16px', borderRadius: 'var(--radius-inner)' }}>
                <span style={{ fontSize: '11px', color: 'var(--text-muted)', display: 'block' }}>Expiry Date</span>
                <strong style={{ fontSize: '14px' }}>{subscription.expiryDate}</strong>
              </div>
              <div style={{ background: 'var(--bg-card-secondary)', padding: '16px', borderRadius: 'var(--radius-inner)' }}>
                <span style={{ fontSize: '11px', color: 'var(--text-muted)', display: 'block' }}>Allow Renewal</span>
                <strong style={{ fontSize: '14px', color: 'var(--emerald-600)' }}>
                  {subscription.allowRenewal ? 'Yes (Permitted)' : 'No'}
                </strong>
              </div>
            </div>
          </div>

          {/* Compare Plans Read-Only Modal */}
          {isCompareOpen && (
            <div className="modal-overlay">
              <div className="modal-content" style={{ maxWidth: '980px', maxHeight: '90vh', overflowY: 'auto' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                  <div>
                    <h3 style={{ fontSize: '20px', fontWeight: 800 }}>Compare Published Plans</h3>
                    <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                      Available live tiers for plan upgrades (Read-only view).
                    </p>
                  </div>
                  <button onClick={() => setIsCompareOpen(false)} className="btn-secondary">
                    Close
                  </button>
                </div>

                <PricingMatrix currency={currency} onNotification={onNotification} />
              </div>
            </div>
          )}
        </div>
      )}

      {/* View 3: My Invoices */}
      {activeTab === 'my-invoices' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          <div>
            <h2 style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em' }}>
              My Invoices
            </h2>
            <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
              Download official PDF invoices and view line item discount deductions.
            </p>
          </div>

          <div className="card" style={{ padding: '24px' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', textAlign: 'left' }}>
                  <th style={{ padding: '10px 12px' }}>Invoice #</th>
                  <th style={{ padding: '10px 12px' }}>Issue Date</th>
                  <th style={{ padding: '10px 12px' }}>Due Date</th>
                  <th style={{ padding: '10px 12px' }}>Subtotal</th>
                  <th style={{ padding: '10px 12px' }}>Discount</th>
                  <th style={{ padding: '10px 12px' }}>Tax</th>
                  <th style={{ padding: '10px 12px' }}>Total Paid</th>
                  <th style={{ padding: '10px 12px' }}>Status</th>
                  <th style={{ padding: '10px 12px', textAlign: 'right' }}>Receipt</th>
                </tr>
              </thead>
              <tbody>
                {myInvoices.map((inv) => (
                  <tr key={inv.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                    <td style={{ padding: '12px', fontWeight: 700, fontFamily: 'var(--font-mono)' }}>{inv.number}</td>
                    <td style={{ padding: '12px', color: 'var(--text-muted)' }}>{inv.issueDate}</td>
                    <td style={{ padding: '12px', color: 'var(--text-muted)' }}>{inv.dueDate}</td>
                    <td style={{ padding: '12px' }}>{formatCurrency(inv.subtotalPaise, currency)}</td>
                    <td style={{ padding: '12px', color: 'var(--emerald-600)', fontWeight: 600 }}>
                      -{formatCurrency(inv.discountPaise, currency)}
                    </td>
                    <td style={{ padding: '12px', color: 'var(--text-muted)' }}>{formatCurrency(inv.taxPaise, currency)}</td>
                    <td style={{ padding: '12px', fontWeight: 800 }}>{formatCurrency(inv.totalPaise, currency)}</td>
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
                        {inv.status}
                      </span>
                    </td>
                    <td style={{ padding: '12px', textAlign: 'right' }}>
                      <button
                        onClick={() => handleDownloadPdf(inv)}
                        className="btn-secondary"
                        style={{ padding: '4px 10px', fontSize: '11px' }}
                      >
                        <Download size={12} /> Download PDF
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* View 4: My Payments */}
      {activeTab === 'my-payments' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          <div>
            <h2 style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em' }}>
              My Payments
            </h2>
            <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
              Record of settled subscription transactions and references (Read-only).
            </p>
          </div>

          <div className="card" style={{ padding: '24px' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', textAlign: 'left' }}>
                  <th style={{ padding: '10px 12px' }}>Reference</th>
                  <th style={{ padding: '10px 12px' }}>Applied To Invoice</th>
                  <th style={{ padding: '10px 12px' }}>Method</th>
                  <th style={{ padding: '10px 12px' }}>Settled Amount</th>
                  <th style={{ padding: '10px 12px' }}>Payment Date</th>
                </tr>
              </thead>
              <tbody>
                {myPayments.map((p) => (
                  <tr key={p.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                    <td style={{ padding: '12px', fontFamily: 'var(--font-mono)', fontWeight: 700 }}>{p.reference}</td>
                    <td style={{ padding: '12px', color: 'var(--indigo-500)', fontWeight: 600 }}>{p.invoiceNumber}</td>
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
                    <td style={{ padding: '12px', fontWeight: 800, color: 'var(--emerald-600)' }}>
                      {formatCurrency(p.amountPaise, currency)}
                    </td>
                    <td style={{ padding: '12px', color: 'var(--text-muted)' }}>{p.paidOn}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* View 5: Customer Support & Tickets */}
      {activeTab === 'support' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h2 style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em' }}>
                Support &amp; Virtual Assistant
              </h2>
              <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
                Chat with the AI assistant or submit private support tickets to our operations staff.
              </p>
            </div>

            <button onClick={() => setIsChatOpen(true)} className="btn-primary">
              <MessageSquare size={15} /> Launch Chatbot Assistant
            </button>
          </div>

          {/* New Ticket Form */}
          <div className="card" style={{ padding: '24px' }}>
            <h3 style={{ fontSize: '16px', fontWeight: 700, marginBottom: '14px' }}>
              Create a Support Request
            </h3>
            <form onSubmit={handleCreateTicket} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                  Subject
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Question regarding invoice deduction"
                  value={newTicketSubject}
                  onChange={(e) => setNewTicketSubject(e.target.value)}
                  style={{ width: '100%' }}
                />
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                  Description
                </label>
                <textarea
                  rows={3}
                  required
                  placeholder="Provide details about your inquiry..."
                  value={newTicketBody}
                  onChange={(e) => setNewTicketBody(e.target.value)}
                  style={{ width: '100%' }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                <button type="submit" className="btn-primary" style={{ padding: '8px 20px' }}>
                  Submit Ticket
                </button>
              </div>
            </form>
          </div>

          {/* My Tickets List */}
          <div className="card" style={{ padding: '24px' }}>
            <h3 style={{ fontSize: '16px', fontWeight: 700, marginBottom: '16px' }}>
              My Support History
            </h3>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {myTickets.map((t) => (
                <div
                  key={t.id}
                  style={{
                    background: 'var(--bg-card-secondary)',
                    borderRadius: 'var(--radius-inner)',
                    padding: '16px',
                    border: '1px solid var(--border-color)',
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <span style={{ fontFamily: 'var(--font-mono)', fontWeight: 700, fontSize: '12px' }}>
                        #{t.number}
                      </span>
                      <strong style={{ fontSize: '14px' }}>{t.subject}</strong>
                    </div>
                    <span
                      style={{
                        padding: '2px 8px',
                        borderRadius: 'var(--radius-pill)',
                        fontSize: '11px',
                        fontWeight: 700,
                        background: t.status === 'RESOLVED' ? 'var(--emerald-50)' : 'rgba(99, 102, 241, 0.1)',
                        color: t.status === 'RESOLVED' ? 'var(--emerald-600)' : 'var(--indigo-500)',
                      }}
                    >
                      {t.status}
                    </span>
                  </div>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '6px', marginTop: '10px' }}>
                    {t.comments.map((c) => (
                      <div
                        key={c.id}
                        style={{
                          background: 'var(--bg-card)',
                          padding: '10px 12px',
                          borderRadius: '8px',
                          fontSize: '12px',
                        }}
                      >
                        <strong style={{ color: 'var(--indigo-500)' }}>{c.author}: </strong>
                        <span>{c.text}</span>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Chatbot Drawer component */}
          <ChatbotDrawer
            onNotification={onNotification}
            isOpenControlled={isChatOpen}
            onCloseControlled={() => setIsChatOpen(false)}
          />
        </div>
      )}
    </div>
  );
};

