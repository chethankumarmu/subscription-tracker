import React from 'react';
import {
  LayoutDashboard,
  CreditCard,
  BarChart3,
  Layers,
  Tag,
  Headphones,
  Sun,
  Moon,
  LogOut,
} from 'lucide-react';

interface SidebarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
  theme: 'light' | 'dark';
  setTheme: (t: 'light' | 'dark') => void;
}

export const Sidebar: React.FC<SidebarProps> = ({
  activeTab,
  setActiveTab,
  theme,
  setTheme,
}) => {
  const navItems = [
    { id: 'overview', icon: LayoutDashboard, label: 'Overview' },
    { id: 'activity', icon: BarChart3, label: 'Activity' },
    { id: 'pricing', icon: Layers, label: 'Pricing Matrix' },
    { id: 'manage', icon: CreditCard, label: 'Subscriptions' },
    { id: 'discounts', icon: Tag, label: 'Discounts' },
    { id: 'support', icon: Headphones, label: 'Support Queue' },
  ];

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
        padding: '16px 8px',
      }}
      className="floating-pill"
    >
      {/* Top Brand Glyph */}
      <div
        onClick={() => setActiveTab('overview')}
        style={{
          width: '38px',
          height: '38px',
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
        }}
      >
        F
      </div>

      {/* Middle Navigation Group */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              title={item.label}
              style={{
                width: '42px',
                height: '42px',
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
              <Icon size={19} />
            </button>
          );
        })}
      </div>

      {/* Bottom Controls: Theme Toggle & Logout */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
        <button
          onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}
          title="Toggle Day/Night theme"
          style={{
            width: '40px',
            height: '40px',
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
          {theme === 'light' ? <Moon size={18} /> : <Sun size={18} />}
        </button>

        <button
          title="Sign out"
          style={{
            width: '40px',
            height: '40px',
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
          <LogOut size={18} />
        </button>
      </div>
    </aside>
  );
};
