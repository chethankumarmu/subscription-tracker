package com.brightloop.subscription.dto;

import java.util.List;

public class DashboardStatsDto {
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

    public DashboardStatsDto(Long totalBalancePaise, Long totalEarningsPaise, Long totalSpendingPaise, Long totalRevenuePaise, Long pendingCollectionPaise, Long activeSubscriptionsCount, Double earningsGrowthPercentage, List<WalletItem> wallets, List<MonthlyChartPoint> profitLossChart, List<RecentActivityItem> recentActivities, SpendingLimit spendingLimit) {
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
            return new DashboardStatsDto(totalBalancePaise, totalEarningsPaise, totalSpendingPaise, totalRevenuePaise, pendingCollectionPaise, activeSubscriptionsCount, earningsGrowthPercentage, wallets, profitLossChart, recentActivities, spendingLimit);
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
        public void setCurrency(String currency) { this.currency = currency; }
        public String getSymbol() { return symbol; }
        public void setSymbol(String symbol) { this.symbol = symbol; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
        public String getFormatted() { return formatted; }
        public void setFormatted(String formatted) { this.formatted = formatted; }
    }

    public static class MonthlyChartPoint {
        private String month;
        private Long profitPaise;
        private Long lossPaise;

        public MonthlyChartPoint() {}
        public MonthlyChartPoint(String month, Long profitPaise, Long lossPaise) {
            this.month = month;
            this.profitPaise = profitPaise;
            this.lossPaise = lossPaise;
        }

        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }
        public Long getProfitPaise() { return profitPaise; }
        public void setProfitPaise(Long profitPaise) { this.profitPaise = profitPaise; }
        public Long getLossPaise() { return lossPaise; }
        public void setLossPaise(Long lossPaise) { this.lossPaise = lossPaise; }
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
            public RecentActivityItemBuilder invoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; return this; }
            public RecentActivityItemBuilder customerName(String customerName) { this.customerName = customerName; return this; }
            public RecentActivityItemBuilder description(String description) { this.description = description; return this; }
            public RecentActivityItemBuilder amountPaise(Long amountPaise) { this.amountPaise = amountPaise; return this; }
            public RecentActivityItemBuilder status(String status) { this.status = status; return this; }
            public RecentActivityItemBuilder issueDate(String issueDate) { this.issueDate = issueDate; return this; }
            public RecentActivityItemBuilder dueDate(String dueDate) { this.dueDate = dueDate; return this; }

            public RecentActivityItem build() {
                return new RecentActivityItem(id, invoiceNumber, customerName, description, amountPaise, status, issueDate, dueDate);
            }
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getInvoiceNumber() { return invoiceNumber; }
        public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Long getAmountPaise() { return amountPaise; }
        public void setAmountPaise(Long amountPaise) { this.amountPaise = amountPaise; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getIssueDate() { return issueDate; }
        public void setIssueDate(String issueDate) { this.issueDate = issueDate; }
        public String getDueDate() { return dueDate; }
        public void setDueDate(String dueDate) { this.dueDate = dueDate; }
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

            public SpendingLimitBuilder usedPaise(Long usedPaise) { this.usedPaise = usedPaise; return this; }
            public SpendingLimitBuilder totalLimitPaise(Long totalLimitPaise) { this.totalLimitPaise = totalLimitPaise; return this; }
            public SpendingLimitBuilder percentage(Integer percentage) { this.percentage = percentage; return this; }
            public SpendingLimitBuilder cardNumberMasked(String cardNumberMasked) { this.cardNumberMasked = cardNumberMasked; return this; }
            public SpendingLimitBuilder cardHolder(String cardHolder) { this.cardHolder = cardHolder; return this; }
            public SpendingLimitBuilder expiry(String expiry) { this.expiry = expiry; return this; }

            public SpendingLimit build() {
                return new SpendingLimit(usedPaise, totalLimitPaise, percentage, cardNumberMasked, cardHolder, expiry);
            }
        }

        public Long getUsedPaise() { return usedPaise; }
        public void setUsedPaise(Long usedPaise) { this.usedPaise = usedPaise; }
        public Long getTotalLimitPaise() { return totalLimitPaise; }
        public void setTotalLimitPaise(Long totalLimitPaise) { this.totalLimitPaise = totalLimitPaise; }
        public Integer getPercentage() { return percentage; }
        public void setPercentage(Integer percentage) { this.percentage = percentage; }
        public String getCardNumberMasked() { return cardNumberMasked; }
        public void setCardNumberMasked(String cardNumberMasked) { this.cardNumberMasked = cardNumberMasked; }
        public String getCardHolder() { return cardHolder; }
        public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }
        public String getExpiry() { return expiry; }
        public void setExpiry(String expiry) { this.expiry = expiry; }
    }

    public Long getTotalBalancePaise() { return totalBalancePaise; }
    public void setTotalBalancePaise(Long totalBalancePaise) { this.totalBalancePaise = totalBalancePaise; }
    public Long getTotalEarningsPaise() { return totalEarningsPaise; }
    public void setTotalEarningsPaise(Long totalEarningsPaise) { this.totalEarningsPaise = totalEarningsPaise; }
    public Long getTotalSpendingPaise() { return totalSpendingPaise; }
    public void setTotalSpendingPaise(Long totalSpendingPaise) { this.totalSpendingPaise = totalSpendingPaise; }
    public Long getTotalRevenuePaise() { return totalRevenuePaise; }
    public void setTotalRevenuePaise(Long totalRevenuePaise) { this.totalRevenuePaise = totalRevenuePaise; }
    public Long getPendingCollectionPaise() { return pendingCollectionPaise; }
    public void setPendingCollectionPaise(Long pendingCollectionPaise) { this.pendingCollectionPaise = pendingCollectionPaise; }
    public Long getActiveSubscriptionsCount() { return activeSubscriptionsCount; }
    public void setActiveSubscriptionsCount(Long activeSubscriptionsCount) { this.activeSubscriptionsCount = activeSubscriptionsCount; }
    public Double getEarningsGrowthPercentage() { return earningsGrowthPercentage; }
    public void setEarningsGrowthPercentage(Double earningsGrowthPercentage) { this.earningsGrowthPercentage = earningsGrowthPercentage; }
    public List<WalletItem> getWallets() { return wallets; }
    public void setWallets(List<WalletItem> wallets) { this.wallets = wallets; }
    public List<MonthlyChartPoint> getProfitLossChart() { return profitLossChart; }
    public void setProfitLossChart(List<MonthlyChartPoint> profitLossChart) { this.profitLossChart = profitLossChart; }
    public List<RecentActivityItem> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<RecentActivityItem> recentActivities) { this.recentActivities = recentActivities; }
    public SpendingLimit getSpendingLimit() { return spendingLimit; }
    public void setSpendingLimit(SpendingLimit spendingLimit) { this.spendingLimit = spendingLimit; }
}

