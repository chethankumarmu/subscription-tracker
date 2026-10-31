import React from 'react';
import { Search, Bell, Sparkles } from 'lucide-react';

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
  const tabs = [
    { id: 'overview', label: 'Overview' },
    { id: 'activity', label: 'Activity' },
    { id: 'pricing', label: 'Pricing Matrix' },
    { id: 'manage', label: 'Manage' },
    { id: 'discounts', label: 'Discounts' },
    { id: 'support', label: 'Support Queue' },
  ];

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
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: '10px',
          padding: '10px 20px',
          pointerEvents: 'auto',
        }}
      >
        <div
          style={{
            width: '12px',
            height: '12px',
            borderRadius: '50%',
            background: 'var(--emerald-500)',
            boxShadow: '0 0 10px var(--emerald-400)',
          }}
        />
        <span style={{ fontWeight: 800, fontSize: '18px', letterSpacing: '-0.02em' }}>
          Finexy<span style={{ color: 'var(--indigo-500)' }}>.</span>
        </span>
      </div>

      {/* Floating Center Tabs */}
      <nav
        className="floating-pill"
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: '4px',
          padding: '6px 8px',
          pointerEvents: 'auto',
        }}
      >
        {tabs.map((tab) => {
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              style={{
                border: 'none',
                background: isActive ? 'var(--indigo-500)' : 'transparent',
                color: isActive ? '#ffffff' : 'var(--text-secondary)',
                padding: '8px 18px',
                borderRadius: 'var(--radius-pill)',
                fontSize: '13px',
                fontWeight: isActive ? 600 : 500,
                cursor: 'pointer',
                transition: 'all 200ms ease',
                boxShadow: isActive ? '0 2px 10px rgba(99, 102, 241, 0.35)' : 'none',
              }}
            >
              {tab.label}
            </button>
          );
        })}
      </nav>

      {/* Right Controls */}
      <div
        className="floating-pill"
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: '12px',
          padding: '8px 16px',
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

        {/* Search Icon */}
        <button
          style={{
            border: 'none',
            background: 'transparent',
            color: 'var(--text-secondary)',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '6px',
            borderRadius: '50%',
          }}
        >
          <Search size={17} />
        </button>

        {/* Notification Bell */}
        <button
          style={{
            border: 'none',
            background: 'transparent',
            color: 'var(--text-secondary)',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '6px',
            borderRadius: '50%',
            position: 'relative',
          }}
        >
          <Bell size={17} />
          <span
            style={{
              position: 'absolute',
              top: '4px',
              right: '4px',
              width: '6px',
              height: '6px',
              background: 'var(--emerald-500)',
              borderRadius: '50%',
            }}
          />
        </button>

        {/* User Avatar */}
        <div
          style={{
            width: '34px',
            height: '34px',
            borderRadius: '50%',
            background: 'linear-gradient(135deg, var(--indigo-500), var(--violet-600))',
            color: '#ffffff',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '12px',
            fontWeight: 700,
            cursor: 'pointer',
            boxShadow: '0 2px 8px rgba(99, 102, 241, 0.3)',
          }}
        >
          AV
        </div>
      </div>
    </header>
  );
};
