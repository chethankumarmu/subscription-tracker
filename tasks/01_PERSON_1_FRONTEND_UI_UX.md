# Person 1: Frontend & UI/UX Lead - Task Breakdown

## 🎯 Role Focus
- Modern, high-polish SPA built with **React + Vite + TypeScript**.
- Cohesive design system inspired by Finexy and Slingshot aesthetics.
- Role-based navigation, state management, and protected route guards.

---

## 📋 Task Checklist

### Phase 1: Design System & Core Shell (Hours 0:00 - 0:40)
- [ ] **T1.1 Setup Project Shell**
  - Verify Vite + React + TypeScript configuration.
  - Setup routing with `react-router-dom`.
  - Configure HTTP client (`axios` or `fetch` wrapper) with JWT interceptors.
- [ ] **T1.2 Core Tokens & CSS Framework**
  - Implement design system tokens in `src/index.css` (primary indigo, dark slate, surface card backgrounds, border radiuses, typography).
  - Setup reusable micro-interactions and transitions.
- [ ] **T1.3 Shared UI Components**
  - Status Badges: DRAFT (gray), QUOTATION (amber), CONFIRMED (blue), ACTIVE (emerald), CLOSED (rose), OVERDUE (red).
  - Data Table: Server-side pagination, sorting headers, search bar, empty state, skeleton loading.
  - Metric / KPI Card: Stat value, trend percentage indicator, comparison pill, icon slot.
  - Modal / Drawer system: Clean backdrop blur and accessibility esc-close.

---

### Phase 2: Auth & Role Layouts (Hours 0:40 - 2:00)
- [ ] **T1.4 Authentication Screens**
  - Login form (email + password, remember me, validation).
  - Signup form with real-time password strength indicators (uppercase, lowercase, number, symbol, min 8 chars).
  - Forgot Password & Reset Password screens.
- [ ] **T1.5 Role-Based Layout Shells**
  - `AdminLayout`: Sidebar with Catalog, Plans, Discounts, Tax Rules, Staff, Reports.
  - `StaffLayout`: Sidebar with Customers, Subscriptions, Invoices, Payments, Support Queue.
  - `CustomerLayout`: Simplified topbar/sidebar with My Plans, Invoices, Support Chat widget.
  - Route Guard: Redirect unauthorized users based on JWT decoded role claims.

---

### Phase 3: Plan Management & Admin Controls (Hours 2:00 - 3:30)
- [ ] **T1.6 Admin Plan & Versioning UI**
  - Plan list view with status filters (DRAFT, PUBLISHED, RETIRED).
  - Create/Edit Plan Modal: Title, product association, billing interval (DAILY, WEEKLY, MONTHLY, YEARLY).
  - Plan Versioning Editor: Price in INR/Paise, availability window, limits (seats, storage, API calls).
  - Feature Flags toggle: `auto_close`, `allow_early_close`, `allow_pause`, `allow_renewal`.
  - Publish Action with validation check (warns if pricing or availability window is missing).
- [ ] **T1.7 Admin Discount Manager**
  - Discount list: Code, type (FIXED/PERCENT), value, usage limit vs. redeemed counter, date ranges.
  - New Discount form with scope selector (PRODUCT or SUBSCRIPTION).
  - Ensure route and UI elements are strictly hidden for Staff and Customers.

---

### Phase 4: Customer Portal & Chat Widget (Hours 3:30 - 5:00)
- [ ] **T1.8 Customer Portal Views**
  - Active subscription card with plan limits progress bars (e.g. 2/5 seats used).
  - "Request Pause" and "Request Renewal" trigger buttons with confirmation modal.
  - Invoices list with download/view printable receipt action.
- [ ] **T1.9 Floating Chatbot Widget**
  - Floating trigger button on bottom-right corner.
  - Expandable chat pane with message history.
  - Quick-reply action chips ("Check invoice status", "Pause subscription", "Talk to human").
  - "Escalate to Human" action that renders an inline ticket confirmation badge with ticket ID.

---

### Phase 5: Staff Support Queue & Finance Dashboard (Hours 5:00 - 6:30)
- [ ] **T1.10 Staff Support Queue View**
  - Ticket inbox: Status filter (OPEN, IN_PROGRESS, RESOLVED).
  - Ticket Detail pane: Customer info, full chat transcript, staff internal note/reply box.
- [ ] **T1.11 Finance Dashboard UI**
  - Top KPI row: Active Subscriptions, MRR, ARR, Total Collected, Outstanding, Overdue.
  - Revenue chart placeholder / SVG trend curve.
  - Overdue aging breakdown table (0-30, 31-60, 60+ days) with action button to view invoice.
- [ ] **T1.12 Polish & Empty States**
  - Zero-state graphics for tables when no records match filter.
  - Toast notification system for async mutations (success/error).
