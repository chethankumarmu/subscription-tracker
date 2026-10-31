export interface PlanLimit {
  id: number;
  planVersionId: number;
  limitKey: string;
  limitValue: number;
}

export interface Plan {
  id: number;
  code: string;
  name: string;
  status: string;
  currentVersionId: number;
  monthlyPricePaise: number;
  yearlyPricePaise: number;
  billingCycle: string;
  allowPause: boolean;
  limits: PlanLimit[];
  featureMap: Record<string, number>;
}

export interface Customer {
  id: number;
  userId?: number;
  companyName: string;
  contactName: string;
  phone?: string;
  billingAddress?: string;
  gstin?: string;
  status: string;
}

export interface WalletItem {
  currency: string;
  symbol: string;
  amount: number;
  isActive: boolean;
  formatted: string;
}

export interface MonthlyChartPoint {
  month: string;
  profitPaise: number;
  lossPaise: number;
}

export interface RecentActivityItem {
  id: number;
  invoiceNumber: string;
  customerName: string;
  description: string;
  amountPaise: number;
  status: 'PAID' | 'CONFIRMED' | 'DRAFT' | 'CANCELLED';
  issueDate: string;
  dueDate: string;
}

export interface SpendingLimit {
  usedPaise: number;
  totalLimitPaise: number;
  percentage: number;
  cardNumberMasked: string;
  cardHolder: string;
  expiry: string;
}

export interface DashboardStats {
  totalBalancePaise: number;
  totalEarningsPaise: number;
  totalSpendingPaise: number;
  totalRevenuePaise: number;
  pendingCollectionPaise: number;
  activeSubscriptionsCount: number;
  earningsGrowthPercentage: number;
  wallets: WalletItem[];
  profitLossChart: MonthlyChartPoint[];
  recentActivities: RecentActivityItem[];
  spendingLimit: SpendingLimit;
}

export interface Subscription {
  id: number;
  number: string;
  customerId: number;
  customerName: string;
  planVersionId: number;
  planName: string;
  pricePaise: number;
  status: 'DRAFT' | 'QUOTATION' | 'CONFIRMED' | 'ACTIVE' | 'CLOSED';
  isPaused: boolean;
  allowPause: boolean;
  startDate: string;
  nextBillingDate: string;
}

export interface InvoiceLine {
  id: number;
  invoiceId: number;
  productId: number;
  description: string;
  quantity: number;
  unitPricePaise: number;
  discountPaise: number;
  taxRateBps: number;
  taxPaise: number;
  lineTotalPaise: number;
}

export interface Payment {
  id: number;
  invoiceId: number;
  method: string;
  amountPaise: number;
  paidOn: string;
  reference: string;
  note?: string;
  recordedBy: number;
}

export interface InvoiceDetail {
  invoice: {
    id: number;
    number: string;
    subscriptionId: number;
    customerId: number;
    status: string;
    periodStart: string;
    periodEnd: string;
    issueDate: string;
    dueDate: string;
    currency: string;
    subtotalPaise: number;
    discountPaise: number;
    taxPaise: number;
    totalPaise: number;
    amountPaidPaise: number;
  };
  customer?: Customer;
  lines: InvoiceLine[];
  payments: Payment[];
  balancePaise: number;
}

export interface Discount {
  id: number;
  code: string;
  name: string;
  discountType: 'FIXED' | 'PERCENT';
  value: number;
  scope: string;
  usageLimit?: number;
  startsAt: string;
  endsAt?: string;
  isActive: boolean;
}

export interface SupportTicket {
  id: number;
  number: string;
  customerId: number;
  sessionId?: number;
  subject: string;
  status: 'OPEN' | 'IN_PROGRESS' | 'RESOLVED';
  priority: 'LOW' | 'NORMAL' | 'HIGH';
  assignedStaffId?: number;
  createdAt: string;
  resolvedAt?: string;
}

export interface FaqArticle {
  id: number;
  question: string;
  answer: string;
  category: string;
  isPublished: boolean;
}

export interface ChatMessage {
  id?: number;
  sender: 'CUSTOMER' | 'BOT' | 'AGENT';
  text: string;
  timestamp: string;
  escalated?: boolean;
  ticketNumber?: string;
}
