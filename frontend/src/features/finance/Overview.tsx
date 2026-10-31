import React, { useState, useEffect } from 'react';
import { DashboardStats, RecentActivityItem } from '../../types';
import { api } from '../../services/api';
import { formatCurrency, formatDate } from '../../utils/formatters';
import { StatusChip } from '../../components/ui/StatusChip';
import { InvoiceDrawer } from './InvoiceDrawer';
import {
  ArrowUpRight,
  ArrowDownLeft,
  Search,
  MoreVertical,
  TrendingUp,
  CreditCard,
  Wifi,
  ChevronDown,
} from 'lucide-react';

interface OverviewProps {
  currency: 'INR' | 'USD';
  onNotification: (title: string, message?: string) => void;
  onNavigateToPricing: () => void;
}

export const Overview: React.FC<OverviewProps> = ({
  currency,
  onNotification,
  onNavigateToPricing,
}) => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [selectedInvoiceId, setSelectedInvoiceId] = useState<number | null>(null);

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
      act.customerName.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus =
      statusFilter === 'ALL' || act.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div>
      {/* Executive Greeting Header matching Image 2 */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '32px',
        }}
      >
        <div>
          <h1 style={{ fontSize: '28px', fontWeight: 800, letterSpacing: '-0.02em' }}>
            Good morning, Fall
          </h1>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            Here is your executive subscription portfolio and financial telemetry for today.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '12px' }}>
          <button onClick={onNavigateToPricing} className="btn-primary">
            + New Subscription Plan
          </button>
        </div>
      </div>

      {/* Top 4-Column Grid: Balance, Wallets, Earnings, Spending Limit */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
          gap: '24px',
          marginBottom: '32px',
        }}
      >
        {/* 1. Total Balance Card */}
        <div className="card" style={{ padding: '24px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
              <span style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-secondary)' }}>
                Total Balance
              </span>
              <span style={{ fontSize: '11px', background: 'var(--bg-card-secondary)', padding: '2px 8px', borderRadius: 'var(--radius-pill)', fontWeight: 700 }}>
                Live Settlement
              </span>
            </div>
            <div style={{ fontSize: '32px', fontWeight: 800, letterSpacing: '-0.02em', marginBottom: '16px' }}>
              {formatCurrency(stats.totalBalancePaise, currency)}
            </div>
          </div>

          <div style={{ display: 'flex', gap: '10px' }}>
            <button
              onClick={() => onNotification('Transfer Ready', 'Domestic NEFT/RTGS gateway initialized')}
              className="btn-secondary"
              style={{ flex: 1, padding: '8px 14px', fontSize: '13px' }}
            >
              <ArrowUpRight size={14} color="var(--indigo-500)" /> Transfer
            </button>
            <button
              onClick={() => onNotification('Payment Link Generated', 'Customer billing invoice link copied')}
              className="btn-secondary"
              style={{ flex: 1, padding: '8px 14px', fontSize: '13px' }}
            >
              <ArrowDownLeft size={14} color="var(--emerald-500)" /> Request
            </button>
          </div>
        </div>

        {/* 2. Multi-Currency Wallets Stack */}
        <div className="card" style={{ padding: '24px' }}>
          <span style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-secondary)', display: 'block', marginBottom: '12px' }}>
            Multi-Currency Wallets
          </span>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
            {stats.wallets.map((w) => (
              <div
                key={w.currency}
                style={{
                  background: 'var(--bg-card-secondary)',
                  borderRadius: 'var(--radius-inner)',
                  padding: '12px',
                  border: w.isActive ? '1px solid var(--emerald-500)' : '1px solid transparent',
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
                  <span style={{ fontSize: '12px', fontWeight: 700 }}>{w.currency}</span>
                  {w.isActive && (
                    <span style={{ width: '6px', height: '6px', borderRadius: '50%', background: 'var(--emerald-500)' }} />
                  )}
                </div>
                <div style={{ fontSize: '15px', fontWeight: 800 }}>{w.formatted}</div>
              </div>
            ))}
          </div>
        </div>

        {/* 3. Gradient Highlight Card: Total Earnings */}
        <div
          className="card"
          style={{
            padding: '24px',
            background: 'linear-gradient(135deg, #059669, #10b981)',
            color: '#ffffff',
            boxShadow: 'var(--shadow-emerald-glow)',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between',
          }}
        >
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
              <span style={{ fontSize: '13px', fontWeight: 600, opacity: 0.9 }}>
                Total Earnings
              </span>
              <span
                style={{
                  background: 'rgba(255, 255, 255, 0.25)',
                  padding: '3px 10px',
                  borderRadius: 'var(--radius-pill)',
                  fontSize: '11px',
                  fontWeight: 700,
                  display: 'flex',
                  alignItems: 'center',
                  gap: '4px',
                }}
              >
                <TrendingUp size={12} /> ↑ {stats.earningsGrowthPercentage}% this month
              </span>
            </div>
            <div style={{ fontSize: '32px', fontWeight: 800, letterSpacing: '-0.02em', margin: '12px 0' }}>
              {formatCurrency(stats.totalEarningsPaise, currency)}
            </div>
          </div>
          <p style={{ fontSize: '12px', opacity: 0.85 }}>
            {stats.activeSubscriptionsCount} active customer recurring accounts settled.
          </p>
        </div>

        {/* 4. Spending Limit & Virtual Debit Card */}
        <div className="card" style={{ padding: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
            <span style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-secondary)' }}>
              Spending Limit
            </span>
            <span style={{ fontSize: '12px', fontWeight: 700, color: 'var(--indigo-500)' }}>
              {stats.spendingLimit.percentage}% used
            </span>
          </div>

          {/* Progress bar */}
          <div
            style={{
              height: '8px',
              borderRadius: '9999px',
              background: 'var(--bg-card-secondary)',
              overflow: 'hidden',
              marginBottom: '16px',
            }}
          >
            <div
              style={{
                height: '100%',
                width: `${Math.min(100, stats.spendingLimit.percentage)}%`,
                background: 'linear-gradient(90deg, var(--emerald-500), var(--indigo-500))',
                borderRadius: '9999px',
                transition: 'width 400ms ease',
              }}
            />
          </div>

          {/* Virtual Card Preview */}
          <div
            style={{
              background: 'linear-gradient(135deg, #1e1b4b, #312e81)',
              borderRadius: 'var(--radius-inner)',
              padding: '14px',
              color: '#ffffff',
              boxShadow: '0 8px 16px rgba(30, 27, 75, 0.3)',
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
              <span style={{ fontSize: '12px', fontWeight: 800, letterSpacing: '0.05em' }}>FINEXY BUSINESS</span>
              <Wifi size={14} style={{ transform: 'rotate(90deg)' }} />
            </div>
            <div style={{ fontSize: '13px', letterSpacing: '0.15em', fontFamily: 'var(--font-mono)', marginBottom: '8px' }}>
              {stats.spendingLimit.cardNumberMasked}
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '10px', opacity: 0.8 }}>
              <span>{stats.spendingLimit.cardHolder}</span>
              <span>EXP {stats.spendingLimit.expiry}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Middle Section: Profit & Loss Bar Chart matching Image 2 */}
      <div className="card" style={{ padding: '28px', marginBottom: '32px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
          <div>
            <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Profit & Loss Performance</h3>
            <p style={{ fontSize: '13px', color: 'var(--text-muted)', marginTop: '2px' }}>
              Monthly revenue inflows (emerald) vs operating costs (indigo)
            </p>
          </div>
          <div style={{ display: 'flex', gap: '16px', fontSize: '12px', fontWeight: 600 }}>
            <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <span style={{ width: '10px', height: '10px', borderRadius: '3px', background: 'var(--emerald-500)' }} />
              Profit & Revenue
            </span>
            <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <span style={{ width: '10px', height: '10px', borderRadius: '3px', background: '#312e81' }} />
              Expenses & Tax
            </span>
          </div>
        </div>

        {/* SVG Bar Chart */}
        <div style={{ height: '220px', width: '100%', position: 'relative' }}>
          <svg style={{ width: '100%', height: '100%', overflow: 'visible' }}>
            {stats.profitLossChart.map((pt, idx) => {
              const maxVal = 35000000;
              const profitHeight = Math.max(10, (pt.profitPaise / maxVal) * 160);
              const lossHeight = Math.max(6, (pt.lossPaise / maxVal) * 160);
              const xPos = 12 + idx * 16; // percentage

              return (
                <g key={pt.month}>
                  {/* Loss bar */}
                  <rect
                    x={`${xPos}%`}
                    y={180 - lossHeight}
                    width="2.5%"
                    height={lossHeight}
                    fill="#312e81"
                    rx="4"
                  />
                  {/* Profit bar */}
                  <rect
                    x={`${xPos + 3.2}%`}
                    y={180 - profitHeight}
                    width="2.5%"
                    height={profitHeight}
                    fill="var(--emerald-500)"
                    rx="4"
                  />
                  {/* Month Label */}
                  <text
                    x={`${xPos + 2.5}%`}
                    y={202}
                    textAnchor="middle"
                    fill="var(--text-muted)"
                    fontSize="12"
                    fontWeight="500"
                  >
                    {pt.month}
                  </text>
                </g>
              );
            })}
          </svg>
        </div>
      </div>

      {/* Bottom Section: Recent Activities Data Table matching Image 2 */}
      <div className="card" style={{ padding: '28px' }}>
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            flexWrap: 'wrap',
            gap: '16px',
            marginBottom: '20px',
          }}
        >
          <div>
            <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Recent Activities</h3>
            <p style={{ fontSize: '13px', color: 'var(--text-muted)', marginTop: '2px' }}>
              Real transaction history from PostgreSQL `invoices` table
            </p>
          </div>

          <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
            {/* Search Input */}
            <div style={{ position: 'relative' }}>
              <Search
                size={16}
                color="var(--text-muted)"
                style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }}
              />
              <input
                type="text"
                placeholder="Search invoice or customer..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                style={{ paddingLeft: '36px', width: '240px' }}
              />
            </div>

            {/* Category Filter */}
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              style={{ fontWeight: 600 }}
            >
              <option value="ALL">All Statuses</option>
              <option value="PAID">Paid</option>
              <option value="CONFIRMED">Confirmed / Pending</option>
              <option value="DRAFT">Draft</option>
            </select>
          </div>
        </div>

        {/* Table */}
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', minWidth: '800px' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', fontSize: '12px', textTransform: 'uppercase' }}>
                <th style={{ padding: '12px 14px', width: '40px' }}>
                  <input type="checkbox" />
                </th>
                <th style={{ padding: '12px 14px' }}>Order ID</th>
                <th style={{ padding: '12px 14px' }}>Customer / Activity</th>
                <th style={{ padding: '12px 14px' }}>Amount</th>
                <th style={{ padding: '12px 14px' }}>Status</th>
                <th style={{ padding: '12px 14px' }}>Issue Date</th>
                <th style={{ padding: '12px 14px', textAlign: 'right' }}>Action</th>
              </tr>
            </thead>
            <tbody>
              {filteredActivities.map((act) => (
                <tr
                  key={act.id}
                  onClick={() => setSelectedInvoiceId(act.id)}
                  style={{
                    borderBottom: '1px solid var(--border-color)',
                    fontSize: '13px',
                    cursor: 'pointer',
                    transition: 'background 150ms ease',
                  }}
                  onMouseEnter={(e) => (e.currentTarget.style.background = 'var(--bg-card-secondary)')}
                  onMouseLeave={(e) => (e.currentTarget.style.background = 'transparent')}
                >
                  <td style={{ padding: '14px' }} onClick={(e) => e.stopPropagation()}>
                    <input type="checkbox" />
                  </td>
                  <td style={{ padding: '14px', fontWeight: 700, color: 'var(--indigo-500)' }}>
                    {act.invoiceNumber}
                  </td>
                  <td style={{ padding: '14px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                      <div
                        style={{
                          width: '32px',
                          height: '32px',
                          borderRadius: '50%',
                          background: 'var(--violet-50)',
                          color: 'var(--indigo-600)',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          fontWeight: 700,
                          fontSize: '12px',
                        }}
                      >
                        {act.customerName.charAt(0)}
                      </div>
                      <div>
                        <div style={{ fontWeight: 600 }}>{act.customerName}</div>
                        <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>{act.description}</div>
                      </div>
                    </div>
                  </td>
                  <td style={{ padding: '14px', fontWeight: 700 }}>
                    {formatCurrency(act.amountPaise, currency)}
                  </td>
                  <td style={{ padding: '14px' }}>
                    <StatusChip status={act.status} />
                  </td>
                  <td style={{ padding: '14px', color: 'var(--text-secondary)' }}>
                    {formatDate(act.issueDate)}
                  </td>
                  <td style={{ padding: '14px', textAlign: 'right' }} onClick={(e) => e.stopPropagation()}>
                    <button
                      onClick={() => setSelectedInvoiceId(act.id)}
                      style={{
                        border: 'none',
                        background: 'transparent',
                        color: 'var(--text-muted)',
                        cursor: 'pointer',
                      }}
                    >
                      <MoreVertical size={16} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Invoice Detail Drawer */}
      {selectedInvoiceId && (
        <InvoiceDrawer
          invoiceId={selectedInvoiceId}
          currency={currency}
          onClose={() => setSelectedInvoiceId(null)}
          onPaymentSuccess={(msg) => {
            onNotification('Payment Settled', msg);
            loadStats();
          }}
        />
      )}
    </div>
  );
};
