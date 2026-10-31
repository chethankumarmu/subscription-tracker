import {
  Plan,
  DashboardStats,
  Subscription,
  Customer,
  InvoiceDetail,
  Discount,
  SupportTicket,
  FaqArticle,
} from '../types';

const API_BASE = 'http://localhost:8080/api';

async function fetchJson<T>(url: string, options?: RequestInit): Promise<T> {
  const token = localStorage.getItem('brightloop_token');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    Accept: 'application/json',
    ...(options?.headers as Record<string, string>),
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const res = await fetch(`${API_BASE}${url}`, {
    ...options,
    headers,
  });

  if (!res.ok) {
    const errorText = await res.text();
    throw new Error(errorText || `Request failed with status ${res.status}`);
  }
  return res.json();
}

export const api = {
  // Auth
  login: (data: { email: string; password: string }) =>
    fetchJson<{ accessToken: string; refreshToken: string; role: string; customerId?: number }>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  register: (data: any) =>
    fetchJson<any>('/auth/register', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // Products
  getProducts: () => fetchJson<any[]>('/products'),
  createProduct: (data: any) =>
    fetchJson<any>('/products', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  updateProduct: (id: number, data: any) =>
    fetchJson<any>(`/products/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  // Plans & Pricing Matrix
  getPlans: () => fetchJson<Plan[]>('/plans'),
  createPlan: (data: any) =>
    fetchJson<any>('/plans', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  publishPlanVersion: (planId: number, versionId: number) =>
    fetchJson<any>(`/plans/${planId}/versions/${versionId}/publish`, {
      method: 'POST',
    }),
  retirePlanVersion: (planId: number, versionId: number) =>
    fetchJson<any>(`/plans/${planId}/versions/${versionId}/retire`, {
      method: 'POST',
    }),

  // Executive Dashboard Stats
  getDashboardStats: () => fetchJson<DashboardStats>('/dashboard/stats'),

  // Subscriptions
  getSubscriptions: () => fetchJson<Subscription[]>('/subscriptions'),
  createSubscription: (data: { customerId: number; planVersionId: number; billingCycle?: string; paymentTermsDays?: number; quantity?: number }) =>
    fetchJson<Subscription>('/subscriptions', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  activateSubscription: (id: number) =>
    fetchJson<{ message: string; invoiceId: number; invoiceNumber: string; status: string }>(`/subscriptions/${id}/activate`, {
      method: 'POST',
    }),
  pauseSubscription: (id: number) =>
    fetchJson<Subscription>(`/subscriptions/${id}/pause`, {
      method: 'POST',
    }),
  closeSubscription: (id: number) =>
    fetchJson<Subscription>(`/subscriptions/${id}/close`, {
      method: 'POST',
    }),
  requestRenewal: (id: number) =>
    fetchJson<{ ticketId: number; ticketNumber: string; subscriptionId: number; subscriptionNumber: string; message: string }>(
      `/subscriptions/${id}/renewal-request`,
      { method: 'POST' }
    ),
  renewSubscription: (id: number) =>
    fetchJson<any>(`/subscriptions/${id}/renew`, {
      method: 'POST',
    }),

  // Billing Runs (Admin Only)
  runBilling: (date?: string) => {
    const query = date ? `?date=${encodeURIComponent(date)}` : '';
    return fetchJson<{
      created: number;
      skipped: number;
      failed: number;
      createdCount?: number;
      skippedCount?: number;
      failedCount?: number;
      runForDate: string;
      status: string;
    }>(`/admin/billing-runs${query}`, {
      method: 'POST',
    });
  },

  // Invoices & Billing
  getInvoices: (status?: string) => {
    const query = status && status !== 'ALL' ? `?status=${status}` : '';
    return fetchJson<any[]>(`/invoices${query}`);
  },
  getInvoiceDetails: (id: number) => fetchJson<InvoiceDetail>(`/invoices/${id}`),
  confirmInvoice: (id: number) =>
    fetchJson<any>(`/invoices/${id}/confirm`, {
      method: 'POST',
    }),
  cancelInvoice: (id: number) =>
    fetchJson<any>(`/invoices/${id}/cancel`, {
      method: 'POST',
    }),
  downloadInvoicePdf: async (id: number, invoiceNumber: string) => {
    const token = localStorage.getItem('brightloop_token');
    const headers: Record<string, string> = {};
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await fetch(`${API_BASE}/invoices/${id}/pdf`, { headers });
    if (!res.ok) throw new Error('Failed to download invoice PDF');
    const blob = await res.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${invoiceNumber}.pdf`;
    document.body.appendChild(a);
    a.click();
    a.remove();
  },

  // Payments
  getPayments: () => fetchJson<any[]>('/payments'),
  recordPayment: (data: { invoiceId: number; amountPaise: number; method: string; reference?: string; note?: string }) =>
    fetchJson<any>('/payments', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // Discounts & Tax Rules
  getDiscounts: () => fetchJson<Discount[]>('/discounts'),
  validateDiscount: (code: string) => fetchJson<Discount>(`/discounts/validate/${code}`),
  createDiscount: (data: Partial<Discount>) =>
    fetchJson<Discount>('/discounts', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  getTaxRules: () => fetchJson<any[]>('/tax-rules'),
  createTaxRule: (data: any) =>
    fetchJson<any>('/tax-rules', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // Support & Chatbot
  getFaqs: () => fetchJson<FaqArticle[]>('/support/faqs'),
  getTickets: () => fetchJson<SupportTicket[]>('/support/tickets'),
  replyTicket: (ticketId: number, body: string, isInternal: boolean = false) =>
    fetchJson<any>(`/support/tickets/${ticketId}/comments`, {
      method: 'POST',
      body: JSON.stringify({ body, isInternal }),
    }),
  updateTicketStatus: (id: number, status: string) =>
    fetchJson<SupportTicket>(`/support/tickets/${id}/status`, {
      method: 'PUT',
      body: JSON.stringify({ status }),
    }),
  sendChatMessage: (data: { sessionId?: number; customerId?: number; message: string }) =>
    fetchJson<{
      sessionId: number;
      sender: 'BOT' | 'AGENT';
      reply: string;
      intent: string;
      escalated: boolean;
      ticketNumber?: string;
      suggestions: string[];
      timestamp: string;
    }>('/support/chat', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // Staff (Admin Only)
  createStaff: (data: { email: string; password: string; fullName: string }) =>
    fetchJson<any>('/admin/staff', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // Audit Logs & Email Outbox (Admin Only)
  getAuditLogs: () => fetchJson<any[]>('/admin/audit-logs'),
  getEmailOutbox: () => fetchJson<any[]>('/admin/email-outbox'),

  // Customers
  getCustomers: () => fetchJson<Customer[]>('/customers'),
};
