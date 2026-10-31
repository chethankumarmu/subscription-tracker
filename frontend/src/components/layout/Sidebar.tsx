import React from 'react';
import { useAuth } from '../../context/AuthContext';
import {
  LayoutDashboard,
  Package,
  Layers,
  Tag,
  Percent,
  Users,
  CreditCard,
  PlayCircle,
  FileText,
  BadgeDollarSign,
  AlertTriangle,
  Headphones,
  UserPlus,
  Shield,
  Mail,
  Sun,
  Moon,
  LogOut,
  HelpCircle,
} from 'lucide-react';

interface SidebarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
  theme: 'light' | 'dark';
  setTheme: (t: 'light' | 'dark') => void;
  onOpenHelp?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({
  activeTab,
  setActiveTab,
  theme,
  setTheme,
  onOpenHelp,
}) => {
  const { user, logout } = useAuth();
  const role = user?.role || 'CUSTOMER';

  // STEP 1: Admin layout
  const adminNav = [
    { id: 'overview', icon: LayoutDashboard, label: 'Overview' },
    { id: 'products', icon: Package, label: 'Products' },
    { id: 'plans', icon: Layers, label: 'Plans' },
    { id: 'discounts', icon: Tag, label: 'Discounts' },
    { id: 'tax-rules', icon: Percent, label: 'Tax rules' },
    { id: 'customers', icon: Users, label: 'Customers' },
    { id: 'subscriptions', icon: CreditCard, label: 'Subscriptions' },
    { id: 'billing-runs', icon: PlayCircle, label: 'Billing runs' },
    { id: 'invoices', icon: FileText, label: 'Invoices' },
    { id: 'payments', icon: BadgeDollarSign, label: 'Payments' },
    { id: 'overdue', icon: AlertTriangle, label: 'Overdue & Aging' },
    { id: 'support', icon: Headphones, label: 'Support Queue' },
    { id: 'staff', icon: UserPlus, label: 'Staff Accounts' },
    { id: 'audit', icon: Shield, label: 'Audit Log' },
    { id: 'email-outbox', icon: Mail, label: 'Email Outbox' },
  ];

  // STEP 2: Staff layout
  const staffNav = [
    { id: 'overview', icon: LayoutDashboard, label: 'Overview' },
    { id: 'customers', icon: Users, label: 'Customers' },
    { id: 'subscriptions', icon: CreditCard, label: 'Subscriptions' },
    { id: 'invoices', icon: FileText, label: 'Invoices' },
    { id: 'payments', icon: BadgeDollarSign, label: 'Payments' },
    { id: 'overdue', icon: AlertTriangle, label: 'Overdue & Aging' },
    { id: 'support', icon: Headphones, label: 'Support Queue' },
    { id: 'plans', icon: Layers, label: 'Plans (View Only)' },
  ];

  // STEP 3: Customer portal
  const customerNav = [
    { id: 'overview', icon: LayoutDashboard, label: 'Overview' },
    { id: 'my-plan', icon: Layers, label: 'My Plan' },
    { id: 'my-invoices', icon: FileText, label: 'My Invoices' },
    { id: 'my-payments', icon: BadgeDollarSign, label: 'My Payments' },
    { id: 'support', icon: Headphones, label: 'Support' },
  ];

  const navItems = role === 'ADMIN' ? adminNav : role === 'STAFF' ? staffNav : customerNav;

  return (
    <aside
      style={{
        position: 'fixed',
        top: '20px',
        bottom: '20px',
        left: '20px',
        width: '64px',
        zIndex: 50,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '14px 6px',
        overflowY: 'auto',
      }}
      className="floating-pill"
    >
      {/* Top Brand Glyph */}
      <div
        onClick={() => setActiveTab('overview')}
        style={{
          width: '38px',
          height: '38px',
          minHeight: '38px',
          borderRadius: '50%',
          background: 'linear-gradient(135deg, var(--emerald-500), var(--indigo-500))',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#ffffff',
          fontWeight: 800,
          fontSize: '18px',
          cursor: 'pointer',
          boxShadow: '0 4px 12px rgba(16, 185, 129, 0.35)',
          marginBottom: '10px',
        }}
      >
        F
      </div>

      {/* Middle Navigation Group */}
      <div
        style={{
          display: 'flex',
          flexDirection: 'column',
          gap: '6px',
          width: '100%',
          alignItems: 'center',
          overflowY: 'auto',
          padding: '2px 0',
        }}
      >
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              title={item.label}
              style={{
                width: '38px',
                height: '38px',
                minHeight: '38px',
                borderRadius: '50%',
                border: 'none',
                background: isActive ? 'var(--indigo-500)' : 'transparent',
                color: isActive ? '#ffffff' : 'var(--text-secondary)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'pointer',
                transition: 'all 180ms ease',
                boxShadow: isActive ? '0 4px 12px rgba(99, 102, 241, 0.4)' : 'none',
              }}
            >
              <Icon size={17} />
            </button>
          );
        })}
      </div>

      {/* Bottom Controls: Theme Toggle & Logout */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '6px', marginTop: '10px' }}>
        <button
          onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}
          title="Toggle Day/Night theme"
          style={{
            width: '36px',
            height: '36px',
            borderRadius: '50%',
            border: 'none',
            background: 'var(--bg-card-secondary)',
            color: 'var(--text-secondary)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            transition: 'all 200ms ease',
          }}
        >
          {theme === 'light' ? <Moon size={16} /> : <Sun size={16} />}
        </button>

        <button
          onClick={logout}
          title="Sign out"
          style={{
            width: '36px',
            height: '36px',
            borderRadius: '50%',
            border: 'none',
            background: 'transparent',
            color: 'var(--text-muted)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            transition: 'all 150ms ease',
          }}
        >
          <LogOut size={16} />
        </button>
      </div>
    </aside>
  );
};
