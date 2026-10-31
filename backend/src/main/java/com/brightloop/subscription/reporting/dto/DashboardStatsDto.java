package com.brightloop.subscription.reporting.dto;

import java.util.List;

public class DashboardStatsDto {
    private Long mrrPaise;
    private Long arrPaise;
    private Long totalBalancePaise;
    private Long totalEarningsPaise;
    private Long totalSpendingPaise;
    private Long totalRevenuePaise;
    private Long pendingCollectionPaise;
    private Long activeSubscriptionsCount;
    private Double earningsGrowthPercentage;

    private List<WalletItem> wallets;
    private List<MonthlyChartPoint> profitLossChart;
    private List<RecentActivityItem> recentActivities;
    private SpendingLimit spendingLimit;

    public DashboardStatsDto() {}

    public DashboardStatsDto(Long mrrPaise, Long arrPaise, Long totalBalancePaise, Long totalEarningsPaise, Long totalSpendingPaise, Long totalRevenuePaise, Long pendingCollectionPaise, Long activeSubscriptionsCount, Double earningsGrowthPercentage, List<WalletItem> wallets, List<MonthlyChartPoint> profitLossChart, List<RecentActivityItem> recentActivities, SpendingLimit spendingLimit) {
        this.mrrPaise = mrrPaise;
        this.arrPaise = arrPaise;
        this.totalBalancePaise = totalBalancePaise;
        this.totalEarningsPaise = totalEarningsPaise;
        this.totalSpendingPaise = totalSpendingPaise;
        this.totalRevenuePaise = totalRevenuePaise;
        this.pendingCollectionPaise = pendingCollectionPaise;
        this.activeSubscriptionsCount = activeSubscriptionsCount;
        this.earningsGrowthPercentage = earningsGrowthPercentage;
        this.wallets = wallets;
        this.profitLossChart = profitLossChart;
        this.recentActivities = recentActivities;
        this.spendingLimit = spendingLimit;
    }

    public static DashboardStatsDtoBuilder builder() {
        return new DashboardStatsDtoBuilder();
    }

    public static class DashboardStatsDtoBuilder {
        private Long mrrPaise;
        private Long arrPaise;
        private Long totalBalancePaise;
        private Long totalEarningsPaise;
        private Long totalSpendingPaise;
        private Long totalRevenuePaise;
        private Long pendingCollectionPaise;
        private Long activeSubscriptionsCount;
        private Double earningsGrowthPercentage;
        private List<WalletItem> wallets;
        private List<MonthlyChartPoint> profitLossChart;
        private List<RecentActivityItem> recentActivities;
        private SpendingLimit spendingLimit;

        public DashboardStatsDtoBuilder mrrPaise(Long val) { this.mrrPaise = val; return this; }
        public DashboardStatsDtoBuilder arrPaise(Long val) { this.arrPaise = val; return this; }
        public DashboardStatsDtoBuilder totalBalancePaise(Long val) { this.totalBalancePaise = val; return this; }
        public DashboardStatsDtoBuilder totalEarningsPaise(Long val) { this.totalEarningsPaise = val; return this; }
        public DashboardStatsDtoBuilder totalSpendingPaise(Long val) { this.totalSpendingPaise = val; return this; }
        public DashboardStatsDtoBuilder totalRevenuePaise(Long val) { this.totalRevenuePaise = val; return this; }
        public DashboardStatsDtoBuilder pendingCollectionPaise(Long val) { this.pendingCollectionPaise = val; return this; }
        public DashboardStatsDtoBuilder activeSubscriptionsCount(Long val) { this.activeSubscriptionsCount = val; return this; }
        public DashboardStatsDtoBuilder earningsGrowthPercentage(Double val) { this.earningsGrowthPercentage = val; return this; }
        public DashboardStatsDtoBuilder wallets(List<WalletItem> val) { this.wallets = val; return this; }
        public DashboardStatsDtoBuilder profitLossChart(List<MonthlyChartPoint> val) { this.profitLossChart = val; return this; }
        public DashboardStatsDtoBuilder recentActivities(List<RecentActivityItem> val) { this.recentActivities = val; return this; }
        public DashboardStatsDtoBuilder spendingLimit(SpendingLimit val) { this.spendingLimit = val; return this; }

        public DashboardStatsDto build() {
            return new DashboardStatsDto(mrrPaise, arrPaise, totalBalancePaise, totalEarningsPaise, totalSpendingPaise, totalRevenuePaise, pendingCollectionPaise, activeSubscriptionsCount, earningsGrowthPercentage, wallets, profitLossChart, recentActivities, spendingLimit);
        }
    }

    public static class WalletItem {
        private String currency;
        private String symbol;
        private Double amount;
        private Boolean isActive;
        private String formatted;

        public WalletItem() {}
        public WalletItem(String currency, String symbol, Double amount, Boolean isActive, String formatted) {
            this.currency = currency;
            this.symbol = symbol;
            this.amount = amount;
            this.isActive = isActive;
            this.formatted = formatted;
        }

        public static WalletItemBuilder builder() { return new WalletItemBuilder(); }
        public static class WalletItemBuilder {
            private String currency;
            private String symbol;
            private Double amount;
            private Boolean isActive;
            private String formatted;
            public WalletItemBuilder currency(String c) { this.currency = c; return this; }
            public WalletItemBuilder symbol(String s) { this.symbol = s; return this; }
            public WalletItemBuilder amount(Double a) { this.amount = a; return this; }
            public WalletItemBuilder isActive(Boolean a) { this.isActive = a; return this; }
            public WalletItemBuilder formatted(String f) { this.formatted = f; return this; }
            public WalletItem build() { return new WalletItem(currency, symbol, amount, isActive, formatted); }
        }

        public String getCurrency() { return currency; }
        public String getSymbol() { return symbol; }
        public Double getAmount() { return amount; }
        public Boolean getIsActive() { return isActive; }
        public String getFormatted() { return formatted; }
    }

    public static class MonthlyChartPoint {
        private String month;
        private Long revenuePaise;
        private Long expensePaise;

        public MonthlyChartPoint() {}
        public MonthlyChartPoint(String month, Long revenuePaise, Long expensePaise) {
            this.month = month;
            this.revenuePaise = revenuePaise;
            this.expensePaise = expensePaise;
        }

        public String getMonth() { return month; }
        public Long getRevenuePaise() { return revenuePaise; }
        public Long getExpensePaise() { return expensePaise; }
    }

    public static class RecentActivityItem {
        private Long id;
        private String invoiceNumber;
        private String customerName;
        private String description;
        private Long amountPaise;
        private String status;
        private String issueDate;
        private String dueDate;

        public RecentActivityItem() {}
        public RecentActivityItem(Long id, String invoiceNumber, String customerName, String description, Long amountPaise, String status, String issueDate, String dueDate) {
            this.id = id;
            this.invoiceNumber = invoiceNumber;
            this.customerName = customerName;
            this.description = description;
            this.amountPaise = amountPaise;
            this.status = status;
            this.issueDate = issueDate;
            this.dueDate = dueDate;
        }

        public static RecentActivityItemBuilder builder() { return new RecentActivityItemBuilder(); }
        public static class RecentActivityItemBuilder {
            private Long id;
            private String invoiceNumber;
            private String customerName;
            private String description;
            private Long amountPaise;
            private String status;
            private String issueDate;
            private String dueDate;

            public RecentActivityItemBuilder id(Long id) { this.id = id; return this; }
            public RecentActivityItemBuilder invoiceNumber(String n) { this.invoiceNumber = n; return this; }
            public RecentActivityItemBuilder customerName(String n) { this.customerName = n; return this; }
            public RecentActivityItemBuilder description(String d) { this.description = d; return this; }
            public RecentActivityItemBuilder amountPaise(Long a) { this.amountPaise = a; return this; }
            public RecentActivityItemBuilder status(String s) { this.status = s; return this; }
            public RecentActivityItemBuilder issueDate(String d) { this.issueDate = d; return this; }
            public RecentActivityItemBuilder dueDate(String d) { this.dueDate = d; return this; }

            public RecentActivityItem build() {
                return new RecentActivityItem(id, invoiceNumber, customerName, description, amountPaise, status, issueDate, dueDate);
            }
        }

        public Long getId() { return id; }
        public String getInvoiceNumber() { return invoiceNumber; }
        public String getCustomerName() { return customerName; }
        public String getDescription() { return description; }
        public Long getAmountPaise() { return amountPaise; }
        public String getStatus() { return status; }
        public String getIssueDate() { return issueDate; }
        public String getDueDate() { return dueDate; }
    }

    public static class SpendingLimit {
        private Long usedPaise;
        private Long totalLimitPaise;
        private Integer percentage;
        private String cardNumberMasked;
        private String cardHolder;
        private String expiry;

        public SpendingLimit() {}
        public SpendingLimit(Long usedPaise, Long totalLimitPaise, Integer percentage, String cardNumberMasked, String cardHolder, String expiry) {
            this.usedPaise = usedPaise;
            this.totalLimitPaise = totalLimitPaise;
            this.percentage = percentage;
            this.cardNumberMasked = cardNumberMasked;
            this.cardHolder = cardHolder;
            this.expiry = expiry;
        }

        public static SpendingLimitBuilder builder() { return new SpendingLimitBuilder(); }
        public static class SpendingLimitBuilder {
            private Long usedPaise;
            private Long totalLimitPaise;
            private Integer percentage;
            private String cardNumberMasked;
            private String cardHolder;
            private String expiry;

            public SpendingLimitBuilder usedPaise(Long u) { this.usedPaise = u; return this; }
            public SpendingLimitBuilder totalLimitPaise(Long t) { this.totalLimitPaise = t; return this; }
            public SpendingLimitBuilder percentage(Integer p) { this.percentage = p; return this; }
            public SpendingLimitBuilder cardNumberMasked(String c) { this.cardNumberMasked = c; return this; }
            public SpendingLimitBuilder cardHolder(String h) { this.cardHolder = h; return this; }
            public SpendingLimitBuilder expiry(String e) { this.expiry = e; return this; }

            public SpendingLimit build() {
                return new SpendingLimit(usedPaise, totalLimitPaise, percentage, cardNumberMasked, cardHolder, expiry);
            }
        }

        public Long getUsedPaise() { return usedPaise; }
        public Long getTotalLimitPaise() { return totalLimitPaise; }
        public Integer getPercentage() { return percentage; }
        public String getCardNumberMasked() { return cardNumberMasked; }
        public String getCardHolder() { return cardHolder; }
        public String getExpiry() { return expiry; }
    }

    public Long getMrrPaise() { return mrrPaise; }
    public Long getArrPaise() { return arrPaise; }
    public Long getTotalBalancePaise() { return totalBalancePaise; }
    public Long getTotalEarningsPaise() { return totalEarningsPaise; }
    public Long getTotalSpendingPaise() { return totalSpendingPaise; }
    public Long getTotalRevenuePaise() { return totalRevenuePaise; }
    public Long getPendingCollectionPaise() { return pendingCollectionPaise; }
    public Long getActiveSubscriptionsCount() { return activeSubscriptionsCount; }
    public Double getEarningsGrowthPercentage() { return earningsGrowthPercentage; }
    public List<WalletItem> getWallets() { return wallets; }
    public List<MonthlyChartPoint> getProfitLossChart() { return profitLossChart; }
    public List<RecentActivityItem> getRecentActivities() { return recentActivities; }
    public SpendingLimit getSpendingLimit() { return spendingLimit; }
}
