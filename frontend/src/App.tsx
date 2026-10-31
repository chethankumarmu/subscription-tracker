import { useState, useEffect } from 'react';
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
  const [theme, setTheme] = useState<'light' | 'dark'>('light');
  const [activeTab, setActiveTab] = useState<string>('overview');
  const [currency, setCurrency] = useState<'INR' | 'USD'>('INR');
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  const addNotification = (title: string, message?: string, type: 'success' | 'error' | 'info' = 'success') => {
    const id = Date.now().toString() + Math.random().toString(36).substring(2, 6);
    setToasts((prev) => [...prev, { id, title, message, type }]);
  };

  const removeToast = (id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  };

  return (
    <div className="app-container">
      {/* Floating Vertical Sidebar */}
      <Sidebar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        theme={theme}
        setTheme={setTheme}
      />

      {/* Floating Top Navbar */}
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        currency={currency}
        setCurrency={setCurrency}
      />

      {/* Main Content Area */}
      <main className="main-content">
        <div className="page-wrapper">
          {activeTab === 'overview' && (
            <Overview
              currency={currency}
              onNotification={addNotification}
              onNavigateToPricing={() => setActiveTab('pricing')}
            />
          )}

          {activeTab === 'activity' && (
            <Activity
              currency={currency}
              onNotification={addNotification}
            />
          )}

          {activeTab === 'pricing' && (
            <PricingMatrix
              currency={currency}
              onNotification={addNotification}
            />
          )}

          {activeTab === 'manage' && (
            <Manage
              currency={currency}
              onNotification={addNotification}
              onNavigateToPricing={() => setActiveTab('pricing')}
            />
          )}

          {activeTab === 'discounts' && (
            <Discounts
              currency={currency}
              onNotification={addNotification}
            />
          )}

          {activeTab === 'support' && (
            <SupportQueue
              onNotification={addNotification}
            />
          )}
        </div>
      </main>

      {/* Floating AI Support Chatbot */}
      <ChatbotDrawer onNotification={addNotification} />

      {/* Toast Notification Container */}
      <ToastContainer toasts={toasts} onDismiss={removeToast} />
    </div>
  );
}
