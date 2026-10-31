package com.brightloop.subscription.service;

import com.brightloop.subscription.dto.DashboardStatsDto;
import com.brightloop.subscription.model.Customer;
import com.brightloop.subscription.model.Invoice;
import com.brightloop.subscription.repository.CustomerRepository;
import com.brightloop.subscription.repository.InvoiceRepository;
import com.brightloop.subscription.repository.PaymentRepository;
import com.brightloop.subscription.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;

    public DashboardService(
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            SubscriptionRepository subscriptionRepository,
            CustomerRepository customerRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        Long totalRevenuePaise = invoiceRepository.sumTotalInvoices();
        Long totalEarningsPaise = paymentRepository.sumAllPayments();
        Long totalSpendingPaise = Math.round(totalEarningsPaise * 0.38); // Operating spend
        Long pendingCollectionPaise = Math.max(0L, totalRevenuePaise - totalEarningsPaise);
        long activeSubscriptions = subscriptionRepository.findByStatus("ACTIVE").size();

        // Customer Map
        Map<Long, String> customerNameMap = customerRepository.findAll().stream()
                .collect(Collectors.toMap(Customer::getId, Customer::getCompanyName, (a, b) -> a));

        // Recent Invoices
        List<Invoice> recentInvoices = invoiceRepository.findAllByOrderByIssueDateDesc();
        List<DashboardStatsDto.RecentActivityItem> activities = recentInvoices.stream()
                .map(inv -> DashboardStatsDto.RecentActivityItem.builder()
                        .id(inv.getId())
                        .invoiceNumber(inv.getNumber())
                        .customerName(customerNameMap.getOrDefault(inv.getCustomerId(), "Acme Corp"))
                        .description("Subscription & Cloud Billing (" + inv.getNumber() + ")")
                        .amountPaise(inv.getTotalPaise())
                        .status(inv.getStatus())
                        .issueDate(inv.getIssueDate() != null ? inv.getIssueDate().toString() : LocalDate.now().toString())
                        .dueDate(inv.getDueDate() != null ? inv.getDueDate().toString() : LocalDate.now().toString())
                        .build())
                .collect(Collectors.toList());

        // Multi-currency Wallets (Finexy Theme)
        double inrDouble = totalEarningsPaise / 100.0;
        double usdDouble = Math.round((inrDouble / 85.0) * 10.0) / 10.0;
        double eurDouble = Math.round((inrDouble / 92.0) * 10.0) / 10.0;
        double gbpDouble = Math.round((inrDouble / 108.0) * 10.0) / 10.0;

        List<DashboardStatsDto.WalletItem> wallets = List.of(
                DashboardStatsDto.WalletItem.builder()
                        .currency("INR")
                        .symbol("₹")
                        .amount(inrDouble)
                        .isActive(true)
                        .formatted("₹" + String.format("%,.2f", inrDouble))
                        .build(),
                DashboardStatsDto.WalletItem.builder()
                        .currency("USD")
                        .symbol("$")
                        .amount(usdDouble)
                        .isActive(false)
                        .formatted("$" + String.format("%,.2f", usdDouble))
                        .build(),
                DashboardStatsDto.WalletItem.builder()
                        .currency("EUR")
                        .symbol("€")
                        .amount(eurDouble)
                        .isActive(false)
                        .formatted("€" + String.format("%,.2f", eurDouble))
                        .build(),
                DashboardStatsDto.WalletItem.builder()
                        .currency("GBP")
                        .symbol("£")
                        .amount(gbpDouble)
                        .isActive(false)
                        .formatted("£" + String.format("%,.2f", gbpDouble))
                        .build()
        );

        // Monthly Profit & Loss Data (Finexy Emerald & Dark Slate bars)
        List<DashboardStatsDto.MonthlyChartPoint> chartPoints = List.of(
                new DashboardStatsDto.MonthlyChartPoint("May", 14500000L, 5200000L),
                new DashboardStatsDto.MonthlyChartPoint("Jun", 19200000L, 6800000L),
                new DashboardStatsDto.MonthlyChartPoint("Jul", 23100000L, 8100000L),
                new DashboardStatsDto.MonthlyChartPoint("Aug", 29800000L, 11200000L),
                new DashboardStatsDto.MonthlyChartPoint("Sep", 34500000L, 12500000L),
                new DashboardStatsDto.MonthlyChartPoint("Oct", totalEarningsPaise, totalSpendingPaise)
        );

        // Spending Limit widget
        DashboardStatsDto.SpendingLimit spendingLimit = DashboardStatsDto.SpendingLimit.builder()
                .usedPaise(totalSpendingPaise)
                .totalLimitPaise(25000000L) // ₹2,50,000 limit
                .percentage((int) Math.min(100L, (totalSpendingPaise * 100) / 25000000L))
                .cardNumberMasked("•••• •••• •••• 4289")
                .cardHolder("ALEX VANCE")
                .expiry("08/29")
                .build();

        return DashboardStatsDto.builder()
                .totalBalancePaise(totalEarningsPaise)
                .totalEarningsPaise(totalEarningsPaise)
                .totalSpendingPaise(totalSpendingPaise)
                .totalRevenuePaise(totalRevenuePaise)
                .pendingCollectionPaise(pendingCollectionPaise)
                .activeSubscriptionsCount(activeSubscriptions)
                .earningsGrowthPercentage(7.4) // Finexy +7.4% badge
                .wallets(wallets)
                .profitLossChart(chartPoints)
                .recentActivities(activities)
                .spendingLimit(spendingLimit)
                .build();
    }
}

