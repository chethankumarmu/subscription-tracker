import React, { useState, useEffect } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Navbar } from './components/layout/Navbar';
import { Sidebar } from './components/layout/Sidebar';
import { ToastContainer, ToastMessage } from './components/ui/Toast';
import { AuthPage } from './features/auth/AuthPage';

// Admin & Staff features
import { Overview } from './features/finance/Overview';
import { ProductsManager } from './features/admin/ProductsManager';
import { PlansManager } from './features/admin/PlansManager';
import { Discounts } from './features/admin/Discounts';
import { TaxRulesView } from './features/admin/TaxRulesView';
import { CustomersView } from './features/customers/CustomersView';
import { Manage } from './features/customer/Manage';
import { BillingRunsView } from './features/admin/BillingRunsView';
import { InvoicesView } from './features/finance/InvoicesView';
import { PaymentsView } from './features/finance/PaymentsView';
import { OverdueView } from './features/finance/OverdueView';
import { SupportQueue } from './features/support/SupportQueue';
import { StaffManager } from './features/admin/StaffManager';
import { AuditLogView } from './features/admin/AuditLogView';
import { EmailOutboxView } from './features/admin/EmailOutboxView';
import { ChatbotDrawer } from './features/chatbot/ChatbotDrawer';

// Customer portal
import { CustomerPortal } from './features/customer/CustomerPortal';

function MainApp() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState<string>('overview');
  const [currency, setCurrency] = useState<'INR' | 'USD'>('INR');
  const [theme, setTheme] = useState<'light' | 'dark'>('light');
  const [toasts, setToasts] = useState<ToastMessage[]>([]);
  const [isChatOpen, setIsChatOpen] = useState(false);

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  // Reset tab to overview on login or role change
  useEffect(() => {
    setActiveTab('overview');
  }, [user?.role]);

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

  // If user is not authenticated, show landing/auth page
  if (!user) {
    return (
      <>
        <AuthPage
          onSuccess={(role) => {
            setActiveTab('overview');
          }}
          onNotification={handleNotify}
        />
        <ToastContainer toasts={toasts} onDismiss={handleDismissToast} />
      </>
    );
  }

  const role = user.role;

  return (
    <div className="app-container">
      {/* Floating Vertical Icon Sidebar based on active role */}
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
          {/* STEP 3: Customer Portal */}
          {role === 'CUSTOMER' && (
            <CustomerPortal
              activeTab={activeTab}
              setActiveTab={setActiveTab}
              currency={currency}
              onNotification={handleNotify}
            />
          )}

          {/* STEP 1 (ADMIN) & STEP 2 (STAFF) */}
          {role !== 'CUSTOMER' && (
            <>
              {activeTab === 'overview' && (
                <Overview
                  currency={currency}
                  onNotification={handleNotify}
                  onNavigateToPricing={() => setActiveTab('plans')}
                  onToggleCurrency={setCurrency}
                />
              )}

              {/* Admin only: Products management with cost price */}
              {activeTab === 'products' && role === 'ADMIN' && (
                <ProductsManager
                  currency={currency}
                  onNotification={handleNotify}
                />
              )}

              {/* Plans: Admin can edit/publish/retire; Staff is view only */}
              {activeTab === 'plans' && (
                <PlansManager
                  currency={currency}
                  onNotification={handleNotify}
                />
              )}

              {/* Admin only: Discounts & Tax rules */}
              {activeTab === 'discounts' && role === 'ADMIN' && (
                <Discounts
                  currency={currency}
                  onNotification={handleNotify}
                />
              )}

              {activeTab === 'tax-rules' && role === 'ADMIN' && (
                <TaxRulesView
                  onNotification={handleNotify}
                />
              )}

              {/* Customers: Admin & Staff */}
              {activeTab === 'customers' && (
                <CustomersView />
              )}

              {/* Subscriptions: Admin (all actions) & Staff (create, activate, pause) */}
              {activeTab === 'subscriptions' && (
                <Manage
                  currency={currency}
                  onNotification={handleNotify}
                  onNavigateToPricing={() => setActiveTab('plans')}
                />
              )}

              {/* Admin only: Billing runs */}
              {activeTab === 'billing-runs' && role === 'ADMIN' && (
                <BillingRunsView
                  onNotification={handleNotify}
                />
              )}

              {/* Invoices: Admin & Staff */}
              {activeTab === 'invoices' && (
                <InvoicesView
                  currency={currency}
                  onNotification={handleNotify}
                />
              )}

              {/* Payments: Admin & Staff */}
              {activeTab === 'payments' && (
                <PaymentsView
                  currency={currency}
                  onNotification={handleNotify}
                />
              )}

              {/* Overdue and aging: Admin & Staff */}
              {activeTab === 'overdue' && (
                <OverdueView
                  currency={currency}
                />
              )}

              {/* Support: Admin (queue, reply, internal notes) & Staff (queue, reply) */}
              {activeTab === 'support' && (
                <SupportQueue
                  onNotification={handleNotify}
                />
              )}

              {/* Admin only: Staff accounts */}
              {activeTab === 'staff' && role === 'ADMIN' && (
                <StaffManager
                  onNotification={handleNotify}
                />
              )}

              {/* Admin only: Audit log */}
              {activeTab === 'audit' && role === 'ADMIN' && (
                <AuditLogView />
              )}

              {/* Admin only: Email outbox */}
              {activeTab === 'email-outbox' && role === 'ADMIN' && (
                <EmailOutboxView />
              )}
            </>
          )}
        </main>
      </div>

      {/* Floating Support Chatbot Drawer */}
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

export default function App() {
  return (
    <AuthProvider>
      <MainApp />
    </AuthProvider>
  );
}
