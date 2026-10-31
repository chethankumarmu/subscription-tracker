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
  const res = await fetch(`${API_BASE}${url}`, {
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json',
      ...options?.headers,
    },
    ...options,
  });
  if (!res.ok) {
    const errorText = await res.text();
    throw new Error(errorText || `Request failed with status ${res.status}`);
  }
  return res.json();
}

export const api = {
  // Plans & Pricing Matrix
  getPlans: () => fetchJson<Plan[]>('/plans'),

  // Executive Dashboard Stats
  getDashboardStats: () => fetchJson<DashboardStats>('/dashboard/stats'),

  // Subscriptions
  getSubscriptions: () => fetchJson<Subscription[]>('/subscriptions'),
  createSubscription: (data: { customerId: number; planId: number; billingCycle: string; discountCode?: string }) =>
    fetchJson<Subscription>('/subscriptions', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  togglePauseSubscription: (id: number) =>
    fetchJson<Subscription>(`/subscriptions/${id}/pause`, {
      method: 'PUT',
    }),
  closeSubscription: (id: number, reason?: string) =>
    fetchJson<Subscription>(`/subscriptions/${id}/close`, {
      method: 'PUT',
      body: JSON.stringify({ reason }),
    }),

  // Invoices & Billing
  getInvoices: (status?: string) => {
    const query = status && status !== 'ALL' ? `?status=${status}` : '';
    return fetchJson<any[]>(`/invoices${query}`);
  },
  getInvoiceDetails: (id: number) => fetchJson<InvoiceDetail>(`/invoices/${id}`),

  // Payments
  recordPayment: (data: { invoiceId: number; amountPaise: number; method: string; reference?: string; note?: string }) =>
    fetchJson<any>('/payments', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // Discounts
  getDiscounts: () => fetchJson<Discount[]>('/discounts'),
  validateDiscount: (code: string) => fetchJson<Discount>(`/discounts/validate/${code}`),
  createDiscount: (data: Partial<Discount>) =>
    fetchJson<Discount>('/discounts', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // Support & Chatbot
  getFaqs: () => fetchJson<FaqArticle[]>('/support/faqs'),
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
  getTickets: () => fetchJson<SupportTicket[]>('/support/tickets'),
  updateTicketStatus: (id: number, status: string) =>
    fetchJson<SupportTicket>(`/support/tickets/${id}/status`, {
      method: 'PUT',
      body: JSON.stringify({ status }),
    }),

  // Customers
  getCustomers: () => fetchJson<Customer[]>('/customers'),
};

