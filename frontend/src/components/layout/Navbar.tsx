import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { Search, Bell, Info, LogOut, ShieldCheck, UserCheck, Shield } from 'lucide-react';

interface NavbarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
  currency: 'INR' | 'USD';
  setCurrency: (c: 'INR' | 'USD') => void;
}

export const Navbar: React.FC<NavbarProps> = ({
  activeTab,
  setActiveTab,
  currency,
  setCurrency,
}) => {
  const { user, logout } = useAuth();

  const getRoleBadge = (role?: string) => {
    switch (role) {
      case 'ADMIN':
        return { label: 'ADMIN', bg: '#fef3c7', color: '#b45309' };
      case 'STAFF':
        return { label: 'STAFF', bg: '#ede9fe', color: '#6d28d9' };
      case 'CUSTOMER':
      default:
        return { label: 'CUSTOMER', bg: 'var(--emerald-50)', color: 'var(--emerald-600)' };
    }
  };

  const badge = getRoleBadge(user?.role);
  const initials = user?.fullName
    ? user.fullName
        .split(' ')
        .map((n) => n[0])
        .join('')
        .substring(0, 2)
        .toUpperCase()
    : 'U';

  return (
    <header
      style={{
        position: 'fixed',
        top: '20px',
        left: '112px',
        right: '32px',
        zIndex: 50,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        pointerEvents: 'none',
      }}
    >
      {/* Brand Badge */}
      <div
        className="floating-pill"
        onClick={() => setActiveTab('overview')}
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: '10px',
          padding: '10px 20px',
          pointerEvents: 'auto',
          cursor: 'pointer',
        }}
      >
        <div
          style={{
            width: '26px',
            height: '26px',
            borderRadius: '8px',
            background: 'var(--emerald-500)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#ffffff',
            fontWeight: 800,
            fontSize: '14px',
            boxShadow: '0 0 12px var(--emerald-400)',
          }}
        >
          ↗
        </div>
        <span style={{ fontWeight: 800, fontSize: '18px', letterSpacing: '-0.02em' }}>
          Finexy<span style={{ color: 'var(--indigo-500)' }}>.</span>
        </span>
      </div>

      {/* Center Breadcrumb/Status Pill */}
      <div
        className="floating-pill"
        style={{
          padding: '8px 20px',
          fontSize: '13px',
          fontWeight: 700,
          color: 'var(--text-secondary)',
          display: 'flex',
          alignItems: 'center',
          gap: '8px',
          pointerEvents: 'auto',
        }}
      >
        <span style={{ textTransform: 'uppercase', fontSize: '11px', color: 'var(--text-muted)' }}>
          {user?.role === 'CUSTOMER' ? 'Client Portal' : `${user?.role || 'Guest'} Workspace`}
        </span>
        <span>/</span>
        <span style={{ color: 'var(--text-primary)', textTransform: 'capitalize' }}>
          {activeTab.replace('-', ' ')}
        </span>
      </div>

      {/* Right Controls */}
      <div
        className="floating-pill"
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: '12px',
          padding: '6px 14px',
          pointerEvents: 'auto',
        }}
      >
        {/* Currency Switcher */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            background: 'var(--bg-card-secondary)',
            borderRadius: 'var(--radius-pill)',
            padding: '2px',
            fontSize: '12px',
            fontWeight: 700,
          }}
        >
          <button
            onClick={() => setCurrency('INR')}
            style={{
              border: 'none',
              background: currency === 'INR' ? 'var(--bg-card)' : 'transparent',
              color: currency === 'INR' ? 'var(--emerald-600)' : 'var(--text-muted)',
              padding: '4px 10px',
              borderRadius: 'var(--radius-pill)',
              cursor: 'pointer',
              boxShadow: currency === 'INR' ? '0 1px 4px rgba(0,0,0,0.1)' : 'none',
              transition: 'all 150ms ease',
            }}
          >
            ₹ INR
          </button>
          <button
            onClick={() => setCurrency('USD')}
            style={{
              border: 'none',
              background: currency === 'USD' ? 'var(--bg-card)' : 'transparent',
              color: currency === 'USD' ? 'var(--indigo-500)' : 'var(--text-muted)',
              padding: '4px 10px',
              borderRadius: 'var(--radius-pill)',
              cursor: 'pointer',
              boxShadow: currency === 'USD' ? '0 1px 4px rgba(0,0,0,0.1)' : 'none',
              transition: 'all 150ms ease',
            }}
          >
            $ USD
          </button>
        </div>

        {/* STEP 4: Top-right user menu with Name, Role, and Log out */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            paddingLeft: '6px',
            borderLeft: '1px solid var(--border-color)',
          }}
        >
          <div
            style={{
              width: '32px',
              height: '32px',
              borderRadius: '50%',
              background: 'linear-gradient(135deg, var(--indigo-500), var(--emerald-500))',
              color: '#ffffff',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: '12px',
              fontWeight: 800,
              boxShadow: '0 2px 8px rgba(99, 102, 241, 0.3)',
            }}
          >
            {initials}
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', textAlign: 'left', lineHeight: '1.2' }}>
            <span style={{ fontSize: '13px', fontWeight: 700 }}>{user?.fullName || 'Anonymous'}</span>
            <div style={{ display: 'flex', alignItems: 'center', gap: '4px', marginTop: '2px' }}>
              <span
                style={{
                  fontSize: '9px',
                  fontWeight: 800,
                  padding: '1px 6px',
                  borderRadius: 'var(--radius-pill)',
                  background: badge.bg,
                  color: badge.color,
                  letterSpacing: '0.04em',
                }}
              >
                {badge.label}
              </span>
              <span style={{ fontSize: '10px', color: 'var(--text-muted)' }}>{user?.email}</span>
            </div>
          </div>

          <button
            onClick={logout}
            title="Log out"
            style={{
              border: 'none',
              background: 'var(--bg-card-secondary)',
              color: 'var(--text-muted)',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '4px',
              padding: '6px 10px',
              borderRadius: 'var(--radius-pill)',
              fontSize: '11px',
              fontWeight: 700,
              marginLeft: '4px',
              transition: 'all 150ms ease',
            }}
          >
            <LogOut size={13} /> Log out
          </button>
        </div>
      </div>
    </header>
  );
};
