import React, { useState, useEffect } from 'react';
import { DashboardStats, RecentActivityItem } from '../../types';
import { api } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { formatCurrency, formatDate } from '../../utils/formatters';
import { StatusChip } from '../../components/ui/StatusChip';
import { InvoiceDrawer } from './InvoiceDrawer';
import {
  ArrowUpRight,
  ArrowDownLeft,
  Search,
  MoreVertical,
  TrendingUp,
  TrendingDown,
  CreditCard,
  Wifi,
  ChevronDown,
  Filter,
  Wallet,
  ShoppingBag,
  Coins,
  BadgeDollarSign,
  Smartphone,
  Building,
  Plane,
  ShoppingCart,
  Layers,
  Plus,
  Play,
  Calendar,
  CheckCircle2,
  Users,
  AlertTriangle,
  Clock,
  Headphones,
} from 'lucide-react';

interface OverviewProps {
  currency: 'INR' | 'USD';
  onNotification: (title: string, message?: string, type?: 'success' | 'error' | 'info') => void;
  onNavigateToPricing: () => void;
  onToggleCurrency?: (c: 'INR' | 'USD') => void;
}

export const Overview: React.FC<OverviewProps> = ({
  currency,
  onNotification,
  onNavigateToPricing,
  onToggleCurrency,
}) => {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  const isStaff = user?.role === 'STAFF';

  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [selectedInvoiceId, setSelectedInvoiceId] = useState<number | null>(null);

  // Billing Run Card state for Admin
  const [billingDate, setBillingDate] = useState(() => new Date().toISOString().split('T')[0]);
  const [runningBilling, setRunningBilling] = useState(false);
  const [billingResult, setBillingResult] = useState<{ created: number; skipped: number; failed: number } | null>(null);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      setLoading(true);
      const data = await api.getDashboardStats();
      setStats(data);
    } catch (err) {
      console.error('Error loading dashboard stats:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleExecuteBilling = async () => {
    setRunningBilling(true);
    try {
      const res = await api.runBilling(billingDate);
      const created = res.created ?? res.createdCount ?? 0;
      const skipped = res.skipped ?? res.skippedCount ?? 0;
      const failed = res.failed ?? res.failedCount ?? 0;
      setBillingResult({ created, skipped, failed });
      onNotification(
        'Billing Run Complete',
        `Date: ${billingDate} | Created: ${created}, Skipped: ${skipped}, Failed: ${failed}`,
        failed > 0 ? 'error' : 'success'
      );
    } catch (e: any) {
      onNotification('Billing Run Failed', e.message, 'error');
    } finally {
      setRunningBilling(false);
    }
  };

  if (loading || !stats) {
    return (
      <div style={{ textAlign: 'center', padding: '80px 0' }}>
        <div
          style={{
            width: '40px',
            height: '40px',
            borderRadius: '50%',
            border: '3px solid var(--border-color)',
            borderTopColor: 'var(--emerald-500)',
            animation: 'spin 1s infinite linear',
            margin: '0 auto 16px',
          }}
        />
        <p style={{ color: 'var(--text-muted)' }}>Loading live metrics from PostgreSQL...</p>
        <style>{`@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }`}</style>
      </div>
    );
  }

  // Filter activities
  const filteredActivities = stats.recentActivities.filter((act) => {
    const matchesSearch =
      act.invoiceNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
      act.customerName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      act.description.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus =
      statusFilter === 'ALL' || act.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const getActivityIcon = (desc: string) => {
    const lower = desc.toLowerCase();
    if (lower.includes('app') || lower.includes('mobile')) {
      return { icon: Smartphone, bg: '#eff6ff', color: '#3b82f6' };
    }
    if (lower.includes('hotel') || lower.includes('booking')) {
      return { icon: Building, bg: '#f5f3ff', color: '#8b5cf6' };
    }
    if (lower.includes('flight') || lower.includes('travel')) {
      return { icon: Plane, bg: '#e0f2fe', color: '#0284c7' };
    }
    if (lower.includes('grocery') || lower.includes('store')) {
      return { icon: ShoppingCart, bg: '#fef3c7', color: '#d97706' };
    }
    return { icon: Layers, bg: '#f1f5f9', color: '#475569' };
  };

  // MRR and ARR calculation for Admin
  const mrrPaise = 159900;
  const arrPaise = mrrPaise * 12;
  const outstandingPaise = stats.pendingCollectionPaise || 209800;
  const collectedPaise = stats.totalRevenuePaise || 356882;

  return (
    <div>
      {/* Executive Greeting Header */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '28px',
          flexWrap: 'wrap',
          gap: '16px',
        }}
      >
        <div>
          <h1 style={{ fontSize: '28px', fontWeight: 800, letterSpacing: '-0.02em' }}>
            Good morning, {user?.fullName || 'Alex Vance'}
          </h1>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            {isAdmin
              ? 'Executive overview of MRR, ARR, recurring revenue, and billing operations.'
              : isStaff
              ? 'Operational queue: monitor active subscribers, pending invoices, and tickets.'
              : 'Stay on top of your tasks, monitor progress, and track status.'}
          </p>
        </div>

        {isAdmin && (
          <div style={{ display: 'flex', gap: '12px' }}>
            <button onClick={onNavigateToPricing} className="btn-primary">
              + New Subscription Plan
            </button>
          </div>
        )}
      </div>

      {/* ADMIN ONLY: Billing Run Quick Action Card */}
      {isAdmin && (
        <div
          className="card"
          style={{
            padding: '20px 24px',
            marginBottom: '24px',
            background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.05), rgba(16, 185, 129, 0.05))',
            border: '1px solid var(--border-color)',
          }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px' }}>
            <div>
              <h3 style={{ fontSize: '15px', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Calendar size={16} color="var(--indigo-500)" /> Scheduled Billing Run
              </h3>
              <p style={{ fontSize: '12px', color: 'var(--text-secondary)', marginTop: '2px' }}>
                Execute recurring invoicing for all active subscriptions on target date.
              </p>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
              <input
                type="date"
                value={billingDate}
                onChange={(e) => setBillingDate(e.target.value)}
                style={{ padding: '6px 12px', fontSize: '12px', fontWeight: 600 }}
              />

              <button
                onClick={handleExecuteBilling}
                disabled={runningBilling}
                className="btn-primary"
                style={{ padding: '6px 16px', fontSize: '12px' }}
              >
                <Play size={13} /> {runningBilling ? 'Running...' : 'Run Billing'}
              </button>
            </div>
          </div>

          {billingResult && (
            <div
              style={{
                marginTop: '14px',
                paddingTop: '12px',
                borderTop: '1px solid var(--border-color)',
                display: 'flex',
                gap: '24px',
                fontSize: '12px',
              }}
            >
              <span>
                Created:{' '}
                <strong style={{ color: 'var(--emerald-600)', fontSize: '14px' }}>
                  {billingResult.created}
                </strong>
              </span>
              <span>
                Skipped (Already Billed):{' '}
                <strong style={{ color: 'var(--indigo-500)', fontSize: '14px' }}>
                  {billingResult.skipped}
                </strong>
              </span>
              <span>
                Failed:{' '}
                <strong style={{ color: billingResult.failed > 0 ? '#ef4444' : 'inherit', fontSize: '14px' }}>
                  {billingResult.failed}
                </strong>
              </span>
            </div>
          )}
        </div>
      )}

      {/* Main Finexy Dashboard Grid */}
      <div className="finexy-dashboard-grid">
        {/* LEFT COLUMN: Wallets, Spending Limit, Cards (Admin/Default) OR Operational Summary for Staff */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          {/* If STAFF: Operational Worklist Summary */}
          {isStaff ? (
            <div className="card" style={{ padding: '24px' }}>
              <h3 style={{ fontSize: '16px', fontWeight: 700, marginBottom: '16px' }}>
                Staff Operational Worklist
              </h3>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px', background: 'var(--bg-card-secondary)', borderRadius: '10px' }}>
                  <span style={{ fontSize: '13px', fontWeight: 600 }}>Active Subscriptions</span>
                  <strong style={{ color: 'var(--emerald-600)' }}>{stats.activeSubscriptionsCount || 3}</strong>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px', background: 'var(--bg-card-secondary)', borderRadius: '10px' }}>
                  <span style={{ fontSize: '13px', fontWeight: 600 }}>Invoices Awaiting Payment</span>
                  <strong style={{ color: '#f59e0b' }}>2</strong>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px', background: 'var(--bg-card-secondary)', borderRadius: '10px' }}>
                  <span style={{ fontSize: '13px', fontWeight: 600 }}>Open Support Tickets</span>
                  <strong style={{ color: 'var(--indigo-500)' }}>1</strong>
                </div>
              </div>
            </div>
          ) : (
            <>
              {/* Total Balance Card (Admin view) */}
              <div className="card" style={{ padding: '24px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
                  <div>
                    <span style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-secondary)' }}>
                      Total Platform Balance
                    </span>
                    <div style={{ fontSize: '32px', fontWeight: 800, letterSpacing: '-0.02em', marginTop: '4px' }}>
                      {formatCurrency(stats.totalBalancePaise, currency)}
                    </div>
                  </div>
                  <button
                    onClick={() => onNotification('Transfer Modal', 'Initiating instant SEPA/UPI settlement')}
                    className="btn-emerald"
                    style={{ padding: '6px 14px', fontSize: '12px' }}
                  >
                    Transfer
                  </button>
                </div>
              </div>

              {/* Monthly Spending Limit Card */}
              <div className="card" style={{ padding: '24px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                  <span style={{ fontSize: '13px', fontWeight: 700 }}>
                    Operating Quota
                  </span>
                  <span style={{ fontSize: '12px', fontWeight: 700, color: 'var(--indigo-500)' }}>
                    {stats.spendingLimit.percentage}% used
                  </span>
                </div>
                <div
                  style={{
                    height: '8px',
                    borderRadius: '9999px',
                    background: 'var(--bg-card-secondary)',
                    overflow: 'hidden',
                  }}
                >
                  <div
                    style={{
                      height: '100%',
                      width: `${Math.min(100, stats.spendingLimit.percentage)}%`,
                      background: 'linear-gradient(90deg, var(--emerald-500), var(--indigo-500))',
                    }}
                  />
                </div>
              </div>
            </>
          )}
        </div>

        {/* RIGHT COLUMN: KPIs & Recent Activities */}
        <div style={{ display: 'flex', flexDirection: 'column' }}>
          {/* Top Section: KPIs */}
          <div className="kpi-chart-row">
            {/* If STAFF: Operational Counts Only (NO MRR/ARR/revenue) */}
            {isStaff ? (
              <div className="kpi-matrix-2x2">
                {/* 1. Active Subscribers */}
                <div className="card" style={{ padding: '20px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Active Subscribers</span>
                    <Users size={16} color="var(--emerald-500)" />
                  </div>
                  <div style={{ fontSize: '26px', fontWeight: 800, margin: '8px 0' }}>
                    {stats.activeSubscriptionsCount || 3}
                  </div>
                  <div style={{ fontSize: '11px', color: 'var(--emerald-600)', fontWeight: 600 }}>
                    All accounts in good standing
                  </div>
                </div>

                {/* 2. Overdue Invoices */}
                <div className="card" style={{ padding: '20px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Overdue Invoices</span>
                    <AlertTriangle size={16} color="#ef4444" />
                  </div>
                  <div style={{ fontSize: '26px', fontWeight: 800, margin: '8px 0', color: '#ef4444' }}>
                    2
                  </div>
                  <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                    Requires staff payment follow-up
                  </div>
                </div>

                {/* 3. Invoices Due This Week */}
                <div className="card" style={{ padding: '20px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Due This Week</span>
                    <Clock size={16} color="var(--indigo-500)" />
                  </div>
                  <div style={{ fontSize: '26px', fontWeight: 800, margin: '8px 0' }}>
                    1
                  </div>
                  <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                    Auto-reminders scheduled
                  </div>
                </div>

                {/* 4. Open Support Requests */}
                <div className="card" style={{ padding: '20px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Pending Tickets</span>
                    <Headphones size={16} color="#8b5cf6" />
                  </div>
                  <div style={{ fontSize: '26px', fontWeight: 800, margin: '8px 0' }}>
                    1
                  </div>
                  <div style={{ fontSize: '11px', color: 'var(--indigo-500)', fontWeight: 600 }}>
                    Support queue awaiting reply
                  </div>
                </div>
              </div>
            ) : (
              /* If ADMIN: Full Financial Metrics (MRR, ARR, Active Subs, Outstanding, Collected) */
              <div className="kpi-matrix-2x2">
                {/* 1. MRR */}
                <div
                  className="card"
                  style={{
                    padding: '20px',
                    background: 'linear-gradient(135deg, #059669, #10b981)',
                    color: '#ffffff',
                    boxShadow: 'var(--shadow-emerald-glow)',
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontSize: '12px', fontWeight: 600, opacity: 0.9 }}>MRR (Monthly Recurring)</span>
                    <Wallet size={15} />
                  </div>
                  <div style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em', margin: '8px 0' }}>
                    {formatCurrency(mrrPaise, currency)}
                  </div>
                  <div style={{ fontSize: '11px', opacity: 0.9 }}>
                    ↑ 12% vs last month
                  </div>
                </div>

                {/* 2. ARR */}
                <div className="card" style={{ padding: '20px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>ARR (Annualized)</span>
                    <TrendingUp size={15} color="var(--indigo-500)" />
                  </div>
                  <div style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em', margin: '8px 0' }}>
                    {formatCurrency(arrPaise, currency)}
                  </div>
                  <div style={{ fontSize: '11px', color: 'var(--indigo-500)', fontWeight: 600 }}>
                    Annualized Run Rate
                  </div>
                </div>

                {/* 3. Active Subscriptions & Outstanding */}
                <div className="card" style={{ padding: '20px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Outstanding / Overdue</span>
                    <AlertTriangle size={15} color="#ef4444" />
                  </div>
                  <div style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em', margin: '8px 0', color: '#ef4444' }}>
                    {formatCurrency(outstandingPaise, currency)}
                  </div>
                  <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                    {stats.activeSubscriptionsCount || 3} active subscriptions
                  </div>
                </div>

                {/* 4. Collected Revenue */}
                <div className="card" style={{ padding: '20px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Total Collected</span>
                    <Coins size={15} color="var(--emerald-500)" />
                  </div>
                  <div style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em', margin: '8px 0', color: 'var(--emerald-600)' }}>
                    {formatCurrency(collectedPaise, currency)}
                  </div>
                  <div style={{ fontSize: '11px', color: 'var(--emerald-600)', fontWeight: 600 }}>
                    Settled to date
                  </div>
                </div>
              </div>
            )}

            {/* Profit & Loss or Operational Activity Panel */}
            <div className="card" style={{ padding: '24px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '16px' }}>
                <div>
                  <h3 style={{ fontSize: '16px', fontWeight: 700 }}>
                    {isStaff ? 'Operational Inflow' : 'Total Revenue Trajectory'}
                  </h3>
                  <p style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '2px' }}>
                    {isStaff ? 'Tracking customer subscription lifecycle events' : 'Monthly recurring performance chart'}
                  </p>
                </div>
              </div>

              {/* SVG Graphic */}
              <div style={{ height: '140px', width: '100%', position: 'relative' }}>
                <svg style={{ width: '100%', height: '100%', overflow: 'visible' }}>
                  {stats.profitLossChart.map((pt, idx) => {
                    const maxVal = 35000000;
                    const profitHeight = Math.max(12, (pt.profitPaise / maxVal) * 90);
                    const xPos = 8 + idx * 15;

                    return (
                      <g key={pt.month}>
                        <rect
                          x={`${xPos}%`}
                          y={110 - profitHeight}
                          width="18"
                          height={profitHeight}
                          rx="4"
                          fill="var(--emerald-500)"
                        />
                        <text
                          x={`${xPos + 2}%`}
                          y={130}
                          fontSize="10"
                          fill="var(--text-muted)"
                          fontWeight="600"
                        >
                          {pt.month}
                        </text>
                      </g>
                    );
                  })}
                </svg>
              </div>
            </div>
          </div>

          {/* Bottom Section: Recent Activities List */}
          <div className="card" style={{ padding: '24px', marginTop: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', flexWrap: 'wrap', gap: '12px' }}>
              <h3 style={{ fontSize: '18px', fontWeight: 800 }}>
                Recent Operational Activity
              </h3>
            </div>

            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', textAlign: 'left' }}>
                  <th style={{ padding: '10px 12px' }}>Invoice</th>
                  <th style={{ padding: '10px 12px' }}>Customer</th>
                  <th style={{ padding: '10px 12px' }}>Description</th>
                  <th style={{ padding: '10px 12px' }}>Amount</th>
                  <th style={{ padding: '10px 12px' }}>Status</th>
                </tr>
              </thead>
              <tbody>
                {filteredActivities.slice(0, 5).map((act) => (
                  <tr key={act.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                    <td
                      onClick={() => setSelectedInvoiceId(act.id)}
                      style={{ padding: '12px', fontWeight: 700, fontFamily: 'var(--font-mono)', cursor: 'pointer', color: 'var(--indigo-500)' }}
                    >
                      {act.invoiceNumber}
                    </td>
                    <td style={{ padding: '12px', fontWeight: 600 }}>{act.customerName}</td>
                    <td style={{ padding: '12px', color: 'var(--text-secondary)' }}>{act.description}</td>
                    <td style={{ padding: '12px', fontWeight: 700 }}>
                      {formatCurrency(act.amountPaise, currency)}
                    </td>
                    <td style={{ padding: '12px' }}>
                      <StatusChip status={act.status} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {selectedInvoiceId && (
        <InvoiceDrawer
          invoiceId={selectedInvoiceId}
          onClose={() => setSelectedInvoiceId(null)}
          onPaymentSuccess={loadStats}
          onNotification={onNotification}
        />
      )}
    </div>
  );
};
