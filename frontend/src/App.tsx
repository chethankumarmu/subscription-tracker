import React, { useState, useEffect } from 'react';
import { Navbar } from './components/layout/Navbar';
import { Sidebar } from './components/layout/Sidebar';
import { ToastContainer, ToastMessage } from './components/ui/Toast';
import { Overview } from './features/finance/Overview';
import { Activity } from './features/finance/Activity';
import { PricingMatrix } from './features/customer/PricingMatrix';
import { Manage } from './features/customer/Manage';
import { Discounts } from './features/admin/Discounts';
import { SupportQueue } from './features/support/SupportQueue';
import { ChatbotDrawer } from './features/chatbot/ChatbotDrawer';

export default function App() {
  const [activeTab, setActiveTab] = useState<string>('overview');
  const [currency, setCurrency] = useState<'INR' | 'USD'>('INR');
  const [theme, setTheme] = useState<'light' | 'dark'>('light');
  const [toasts, setToasts] = useState<ToastMessage[]>([]);
  const [isChatOpen, setIsChatOpen] = useState(false);

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  const handleNotify = (title: string, message?: string, type: 'success' | 'error' | 'info' = 'success') => {
    const newToast: ToastMessage = {
      id: `${Date.now()}_${Math.random().toString(36).substring(2, 7)}`,
      type,
      title,
      message,
    };
    setToasts((prev) => [...prev, newToast]);
  };

  const handleDismissToast = (id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  };

  return (
    <div className="app-container">
      {/* Floating Vertical Icon Sidebar */}
      <Sidebar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        theme={theme}
        setTheme={setTheme}
        onOpenHelp={() => setIsChatOpen(true)}
      />

      <div className="main-content">
        {/* Floating Topbar */}
        <Navbar
          activeTab={activeTab}
          setActiveTab={setActiveTab}
          currency={currency}
          setCurrency={setCurrency}
        />

        {/* Dynamic Page Views */}
        <main className="page-wrapper">
          {activeTab === 'overview' && (
            <Overview
              currency={currency}
              onNotification={handleNotify}
              onNavigateToPricing={() => setActiveTab('pricing')}
              onToggleCurrency={setCurrency}
            />
          )}

          {activeTab === 'activity' && (
            <Activity
              currency={currency}
              onNotification={handleNotify}
            />
          )}

          {activeTab === 'pricing' && (
            <PricingMatrix
              currency={currency}
              onNotification={handleNotify}
            />
          )}

          {activeTab === 'manage' && (
            <Manage
              currency={currency}
              onNotification={handleNotify}
              onNavigateToPricing={() => setActiveTab('pricing')}
            />
          )}

          {activeTab === 'discounts' && (
            <Discounts
              currency={currency}
              onNotification={handleNotify}
            />
          )}

          {activeTab === 'support' && (
            <SupportQueue
              onNotification={handleNotify}
            />
          )}
        </main>
      </div>

      {/* Floating Support Chatbot Drawer with PostgreSQL Knowledge Base */}
      <ChatbotDrawer
        onNotification={handleNotify}
        isOpenControlled={isChatOpen}
        onCloseControlled={() => setIsChatOpen(false)}
      />

      {/* Real-time Toast Notifications */}
      <ToastContainer toasts={toasts} onDismiss={handleDismissToast} />
    </div>
  );
}
