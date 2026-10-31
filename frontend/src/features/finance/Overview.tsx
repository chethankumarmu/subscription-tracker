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
} from 'lucide-react';

interface OverviewProps {
  currency: 'INR' | 'USD';
  onNotification: (title: string, message?: string) => void;
  onNavigateToPricing: () => void;
  onToggleCurrency?: (c: 'INR' | 'USD') => void;
}

export const Overview: React.FC<OverviewProps> = ({
  currency,
  onNotification,
  onNavigateToPricing,
  onToggleCurrency,
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

  return (
    <div>
      {/* Executive Greeting Header matching Image 2 */}
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
            Good morning, Alex Vance
          </h1>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            Stay on top of your tasks, monitor progress, and track status.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '12px' }}>
          <button onClick={onNavigateToPricing} className="btn-primary">
            + New Subscription Plan
          </button>
        </div>
      </div>

      {/* Main Finexy Dashboard Grid: Left Column (Balance, Spending, Cards) + Right Column (KPIs, Chart, Activities) */}
      <div className="finexy-dashboard-grid">
        {/* LEFT COLUMN: Total Balance, Wallets, Monthly Spending Limit, My Cards */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          {/* 1. Total Balance Card matching Image 2 */}
          <div className="card" style={{ padding: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
              <div>
                <span style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-secondary)' }}>
                  Total Balance
                </span>
                <div style={{ fontSize: '32px', fontWeight: 800, letterSpacing: '-0.02em', margin: '4px 0 6px' }}>
                  {formatCurrency(stats.totalBalancePaise, currency)}
                </div>
                <div style={{ display: 'inline-flex', alignItems: 'center', gap: '4px', fontSize: '11px', color: 'var(--emerald-600)', background: 'var(--emerald-50)', padding: '2px 8px', borderRadius: 'var(--radius-pill)', fontWeight: 700 }}>
                  <TrendingUp size={11} /> ↑ 5% than last month
                </div>
              </div>

              {/* Currency Selector Pill */}
              <div
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '4px',
                  background: 'var(--bg-card-secondary)',
                  borderRadius: 'var(--radius-pill)',
                  padding: '4px 10px',
                  fontSize: '12px',
                  fontWeight: 700,
                  border: '1px solid var(--border-color)',
                  cursor: 'pointer',
                }}
                onClick={() => onToggleCurrency && onToggleCurrency(currency === 'INR' ? 'USD' : 'INR')}
                title="Click to toggle active currency"
              >
                <span>{currency === 'INR' ? '🇮🇳 INR' : '🇺🇸 USD'}</span>
                <ChevronDown size={14} color="var(--text-muted)" />
              </div>
            </div>

            {/* Action Buttons: Transfer & Request */}
            <div style={{ display: 'flex', gap: '10px', margin: '20px 0 24px' }}>
              <button
                onClick={() => onNotification('Transfer Initialized', 'Domestic NEFT/RTGS gateway initialized')}
                className="btn-emerald"
                style={{ flex: 1, padding: '9px 14px', fontSize: '13px' }}
              >
                <ArrowUpRight size={15} /> Transfer
              </button>
              <button
                onClick={() => onNotification('Payment Link Generated', 'Customer billing invoice link copied to clipboard')}
                className="btn-secondary"
                style={{ flex: 1, padding: '9px 14px', fontSize: '13px', justifyContent: 'center' }}
              >
                <ArrowDownLeft size={15} color="var(--indigo-500)" /> Request
              </button>
            </div>

            {/* Multi-Currency Wallets Stack matching Image 2 */}
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                <span style={{ fontSize: '12px', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>
                  Wallets | Total {stats.wallets.length} wallets
                </span>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(95px, 1fr))', gap: '8px' }}>
                {stats.wallets.map((w) => (
                  <div
                    key={w.currency}
                    style={{
                      background: 'var(--bg-card-secondary)',
                      borderRadius: 'var(--radius-inner)',
                      padding: '10px',
                      border: w.isActive ? '1px solid var(--emerald-500)' : '1px solid var(--border-color)',
                      transition: 'all 150ms ease',
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
                      <span style={{ fontSize: '11px', fontWeight: 800 }}>{w.currency}</span>
                      <span
                        style={{
                          fontSize: '9px',
                          fontWeight: 700,
                          padding: '1px 5px',
                          borderRadius: 'var(--radius-pill)',
                          background: w.isActive ? 'var(--emerald-50)' : 'transparent',
                          color: w.isActive ? 'var(--emerald-600)' : 'var(--text-muted)',
                        }}
                      >
                        {w.isActive ? 'Active' : 'Inactive'}
                      </span>
                    </div>
                    <div style={{ fontSize: '13px', fontWeight: 800 }}>{w.formatted}</div>
                    <div style={{ fontSize: '9px', color: 'var(--text-muted)', marginTop: '2px' }}>
                      Limit {w.currency === 'INR' ? '₹1.5L' : '$15k'}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* 2. Monthly Spending Limit Card matching Image 2 */}
          <div className="card" style={{ padding: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
              <span style={{ fontSize: '13px', fontWeight: 700 }}>
                Monthly Spending Limit
              </span>
              <span style={{ fontSize: '12px', fontWeight: 700, color: 'var(--indigo-500)' }}>
                {stats.spendingLimit.percentage}% used
              </span>
            </div>

            {/* Spent out of total text */}
            <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginBottom: '12px' }}>
              <strong style={{ color: 'var(--text-primary)' }}>
                {formatCurrency(stats.spendingLimit.usedPaise, currency)}
              </strong>{' '}
              spent out of{' '}
              <strong style={{ color: 'var(--text-primary)' }}>
                {formatCurrency(stats.spendingLimit.totalLimitPaise, currency)}
              </strong>
            </div>

            {/* Progress bar */}
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
                  borderRadius: '9999px',
                  transition: 'width 400ms ease',
                }}
              />
            </div>
          </div>

          {/* 3. My Cards Card matching Image 2 */}
          <div className="card" style={{ padding: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <CreditCard size={16} color="var(--indigo-500)" />
                <span style={{ fontSize: '14px', fontWeight: 700 }}>My Cards</span>
              </div>
              <button
                onClick={() => onNotification('Card Manager', 'Virtual corporate card issuing modal initialized')}
                style={{
                  border: 'none',
                  background: 'var(--bg-card-secondary)',
                  color: 'var(--text-primary)',
                  fontSize: '11px',
                  fontWeight: 700,
                  padding: '4px 10px',
                  borderRadius: 'var(--radius-pill)',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '4px',
                }}
              >
                <Plus size={12} /> Add new
              </button>
            </div>

            {/* Visual Credit Cards Stack */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
              {/* Card 1: Matte Dark Mastercard */}
              <div
                style={{
                  background: 'linear-gradient(135deg, #0f172a, #1e293b)',
                  borderRadius: '16px',
                  padding: '16px',
                  color: '#ffffff',
                  boxShadow: '0 8px 16px rgba(0, 0, 0, 0.25)',
                  display: 'flex',
                  flexDirection: 'column',
                  justifyContent: 'space-between',
                  height: '120px',
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Wifi size={14} style={{ transform: 'rotate(90deg)', opacity: 0.8 }} />
                  <span style={{ fontSize: '9px', background: 'rgba(255,255,255,0.2)', padding: '2px 6px', borderRadius: 'var(--radius-pill)', fontWeight: 700 }}>
                    Active
                  </span>
                </div>
                <div>
                  <div style={{ fontSize: '12px', letterSpacing: '0.12em', fontFamily: 'var(--font-mono)', marginBottom: '6px' }}>
                    •••• 8782
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '9px', opacity: 0.75 }}>
                    <span>EXP 09/29</span>
                    <span>CVV 611</span>
                  </div>
                </div>
              </div>

              {/* Card 2: Emerald Gradient Card */}
              <div
                style={{
                  background: 'linear-gradient(135deg, #064e3b, #059669)',
                  borderRadius: '16px',
                  padding: '16px',
                  color: '#ffffff',
                  boxShadow: '0 8px 16px rgba(5, 150, 105, 0.25)',
                  display: 'flex',
                  flexDirection: 'column',
                  justifyContent: 'space-between',
                  height: '120px',
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Wifi size={14} style={{ transform: 'rotate(90deg)', opacity: 0.8 }} />
                  <span style={{ fontSize: '9px', background: 'rgba(255,255,255,0.25)', padding: '2px 6px', borderRadius: 'var(--radius-pill)', fontWeight: 700 }}>
                    Active
                  </span>
                </div>
                <div>
                  <div style={{ fontSize: '12px', letterSpacing: '0.12em', fontFamily: 'var(--font-mono)', marginBottom: '6px' }}>
                    •••• 4358
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '9px', opacity: 0.85 }}>
                    <span>EXP 11/30</span>
                    <span>CVV 920</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* RIGHT COLUMN: Top Section (4 KPIs + Profit & Loss Chart) and Bottom Section (Recent Activities) */}
        <div style={{ display: 'flex', flexDirection: 'column' }}>
          {/* Top Section: 4 KPI Cards + Profit & Loss Chart */}
          <div className="kpi-chart-row">
            {/* 4 KPI Cards Matrix (2x2) matching Image 2 */}
            <div className="kpi-matrix-2x2">
              {/* 1. Total Earnings (Emerald Gradient Card) */}
              <div
                className="card"
                style={{
                  padding: '20px',
                  background: 'linear-gradient(135deg, #059669, #10b981)',
                  color: '#ffffff',
                  boxShadow: 'var(--shadow-emerald-glow)',
                  display: 'flex',
                  flexDirection: 'column',
                  justifyContent: 'space-between',
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontSize: '12px', fontWeight: 600, opacity: 0.9 }}>Total Earnings</span>
                  <div style={{ width: '28px', height: '28px', borderRadius: '50%', background: 'rgba(255,255,255,0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Wallet size={15} />
                  </div>
                </div>
                <div style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em', margin: '8px 0' }}>
                  {formatCurrency(stats.totalEarningsPaise, currency)}
                </div>
                <div style={{ fontSize: '11px', display: 'flex', alignItems: 'center', gap: '3px', opacity: 0.9 }}>
                  <TrendingUp size={12} /> ↑ {stats.earningsGrowthPercentage}% this month
                </div>
              </div>

              {/* 2. Total Spending */}
              <div className="card" style={{ padding: '20px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Total Spending</span>
                  <div style={{ width: '28px', height: '28px', borderRadius: '50%', background: 'var(--bg-card-secondary)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <ShoppingBag size={15} color="var(--text-secondary)" />
                  </div>
                </div>
                <div style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em', margin: '8px 0' }}>
                  {formatCurrency(stats.totalSpendingPaise, currency)}
                </div>
                <div style={{ fontSize: '11px', display: 'flex', alignItems: 'center', gap: '3px', color: '#ef4444', fontWeight: 600 }}>
                  <TrendingDown size={12} /> ↓ 5% this month
                </div>
              </div>

              {/* 3. Total Income */}
              <div className="card" style={{ padding: '20px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Total Income</span>
                  <div style={{ width: '28px', height: '28px', borderRadius: '50%', background: 'var(--bg-card-secondary)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Coins size={15} color="var(--emerald-500)" />
                  </div>
                </div>
                <div style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em', margin: '8px 0' }}>
                  {formatCurrency(stats.totalBalancePaise + 100000, currency)}
                </div>
                <div style={{ fontSize: '11px', display: 'flex', alignItems: 'center', gap: '3px', color: 'var(--emerald-600)', fontWeight: 600 }}>
                  <TrendingUp size={12} /> ↑ 8% this month
                </div>
              </div>

              {/* 4. Total Revenue */}
              <div className="card" style={{ padding: '20px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Total Revenue</span>
                  <div style={{ width: '28px', height: '28px', borderRadius: '50%', background: 'var(--bg-card-secondary)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <BadgeDollarSign size={15} color="var(--indigo-500)" />
                  </div>
                </div>
                <div style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em', margin: '8px 0' }}>
                  {formatCurrency(stats.totalRevenuePaise, currency)}
                </div>
                <div style={{ fontSize: '11px', display: 'flex', alignItems: 'center', gap: '3px', color: 'var(--emerald-600)', fontWeight: 600 }}>
                  <TrendingUp size={12} /> ↑ 4% this month
                </div>
              </div>
            </div>

            {/* Profit & Loss Bar Chart matching Image 2 */}
            <div className="card" style={{ padding: '24px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '16px' }}>
                <div>
                  <h3 style={{ fontSize: '16px', fontWeight: 700 }}>Total Income</h3>
                  <p style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '2px' }}>
                    View your income in a certain period of time
                  </p>
                </div>
                <div style={{ display: 'flex', gap: '12px', fontSize: '11px', fontWeight: 600 }}>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                    <span style={{ width: '8px', height: '8px', borderRadius: '2px', background: 'var(--emerald-500)' }} />
                    Profit
                  </span>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                    <span style={{ width: '8px', height: '8px', borderRadius: '2px', background: '#1e1b4b' }} />
                    Loss
                  </span>
                </div>
              </div>

              {/* SVG Chart */}
              <div style={{ height: '160px', width: '100%', position: 'relative' }}>
                <svg style={{ width: '100%', height: '100%', overflow: 'visible' }}>
                  {stats.profitLossChart.map((pt, idx) => {
                    const maxVal = 35000000;
                    const profitHeight = Math.max(12, (pt.profitPaise / maxVal) * 110);
                    const lossHeight = Math.max(8, (pt.lossPaise / maxVal) * 110);
                    const xPos = 8 + idx * 15; // percentage

                    return (
                      <g key={pt.month}>
                        {/* Loss bar (Dark slate) */}
                        <rect
                          x={`${xPos}%`}
                          y={130 - lossHeight}
                          width="3.5%"
                          height={lossHeight}
                          fill="#1e1b4b"
                          rx="3"
                        />
                        {/* Profit bar (Light lime / emerald) */}
                        <rect
                          x={`${xPos + 4}%`}
                          y={130 - profitHeight}
                          width="3.5%"
                          height={profitHeight}
                          fill="var(--emerald-500)"
                          rx="3"
                        />
                        {/* Month Label */}
                        <text
                          x={`${xPos + 3.5}%`}
                          y={150}
                          textAnchor="middle"
                          fill="var(--text-muted)"
                          fontSize="11"
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
          </div>

          {/* Bottom Section: Recent Activities Data Table matching Image 2 */}
          <div className="card" style={{ padding: '24px' }}>
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

              <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                {/* Search Bar */}
                <div style={{ position: 'relative' }}>
                  <Search
                    size={15}
                    color="var(--text-muted)"
                    style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }}
                  />
                  <input
                    type="text"
                    placeholder="Search..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    style={{ paddingLeft: '34px', width: '200px', fontSize: '13px', borderRadius: 'var(--radius-pill)' }}
                  />
                </div>

                {/* Filter Selector */}
                <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <select
                    value={statusFilter}
                    onChange={(e) => setStatusFilter(e.target.value)}
                    style={{ fontWeight: 600, fontSize: '12px', borderRadius: 'var(--radius-pill)', padding: '8px 12px' }}
                  >
                    <option value="ALL">Filter: All</option>
                    <option value="PAID">Paid</option>
                    <option value="CONFIRMED">Pending / Confirmed</option>
                    <option value="DRAFT">Draft</option>
                  </select>
                </div>
              </div>
            </div>

            {/* Table */}
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', minWidth: '750px' }}>
                <thead>
                  <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', fontSize: '12px', textTransform: 'uppercase' }}>
                    <th style={{ padding: '10px 12px', width: '36px' }}>
                      <input type="checkbox" />
                    </th>
                    <th style={{ padding: '10px 12px' }}>Order ID</th>
                    <th style={{ padding: '10px 12px' }}>Activity</th>
                    <th style={{ padding: '10px 12px' }}>Price</th>
                    <th style={{ padding: '10px 12px' }}>Status</th>
                    <th style={{ padding: '10px 12px' }}>Date</th>
                    <th style={{ padding: '10px 12px', textAlign: 'right' }}>...</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredActivities.map((act) => {
                    const styleMeta = getActivityIcon(act.description);
                    const IconComponent = styleMeta.icon;

                    return (
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
                        <td style={{ padding: '12px' }} onClick={(e) => e.stopPropagation()}>
                          <input type="checkbox" />
                        </td>
                        <td style={{ padding: '12px', fontWeight: 700, color: 'var(--text-primary)' }}>
                          {act.invoiceNumber}
                        </td>
                        <td style={{ padding: '12px' }}>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                            <div
                              style={{
                                width: '30px',
                                height: '30px',
                                borderRadius: '8px',
                                background: styleMeta.bg,
                                color: styleMeta.color,
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                              }}
                            >
                              <IconComponent size={16} />
                            </div>
                            <div>
                              <div style={{ fontWeight: 600 }}>{act.customerName}</div>
                              <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>{act.description}</div>
                            </div>
                          </div>
                        </td>
                        <td style={{ padding: '12px', fontWeight: 700 }}>
                          {formatCurrency(act.amountPaise, currency)}
                        </td>
                        <td style={{ padding: '12px' }}>
                          <StatusChip status={act.status} />
                        </td>
                        <td style={{ padding: '12px', color: 'var(--text-secondary)' }}>
                          {formatDate(act.issueDate)}
                        </td>
                        <td style={{ padding: '12px', textAlign: 'right' }} onClick={(e) => e.stopPropagation()}>
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
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
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
