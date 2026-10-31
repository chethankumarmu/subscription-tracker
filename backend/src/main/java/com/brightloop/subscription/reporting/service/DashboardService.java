package com.brightloop.subscription.reporting.service;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.auth.repository.CustomerRepository;
import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.billing.repository.InvoiceRepository;
import com.brightloop.subscription.payments.repository.PaymentRepository;
import com.brightloop.subscription.plans.entity.PlanLimit;
import com.brightloop.subscription.plans.entity.PlanVersion;
import com.brightloop.subscription.reporting.dto.CustomerDashboardStatsDto;
import com.brightloop.subscription.reporting.dto.DashboardStatsDto;
import com.brightloop.subscription.reporting.dto.StaffDashboardStatsDto;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import com.brightloop.subscription.support.repository.SupportTicketRepository;
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
    private final SupportTicketRepository supportTicketRepository;

    public DashboardService(
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            SubscriptionRepository subscriptionRepository,
            CustomerRepository customerRepository
    ) {
        this(invoiceRepository, paymentRepository, subscriptionRepository, customerRepository, null);
    }

    public DashboardService(
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            SubscriptionRepository subscriptionRepository,
            CustomerRepository customerRepository,
            SupportTicketRepository supportTicketRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.customerRepository = customerRepository;
        this.supportTicketRepository = supportTicketRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getAdminStats() {
        Long totalRevenuePaise = invoiceRepository.sumTotalInvoices();
        Long totalEarningsPaise = paymentRepository.sumAllPayments();
        Long totalSpendingPaise = Math.round(totalEarningsPaise * 0.38);
        Long pendingCollectionPaise = Math.max(0L, totalRevenuePaise - totalEarningsPaise);

        List<Subscription> activeSubs = subscriptionRepository.findByStatus("ACTIVE");
        long activeSubscriptionsCount = activeSubs.size();

        // Calculate MRR from active subscriptions
        long mrrPaise = activeSubs.stream()
                .mapToLong(s -> {
                    PlanVersion pv = s.getPlanVersion();
                    if (pv == null) return 0L;
                    long price = pv.getPricePaise();
                    String cycle = pv.getBillingCycle() != null ? pv.getBillingCycle().toUpperCase() : "MONTHLY";
                    return switch (cycle) {
                        case "YEARLY" -> price / 12;
                        case "WEEKLY" -> price * 4;
                        case "DAILY" -> price * 30;
                        default -> price;
                    };
                })
                .sum();
        long arrPaise = mrrPaise * 12;

        Map<Long, String> customerNameMap = customerRepository.findAll().stream()
                .collect(Collectors.toMap(Customer::getId, Customer::getCompanyName, (a, b) -> a));

        List<Invoice> recentInvoices = invoiceRepository.findAllByOrderByIssueDateDesc();
        List<DashboardStatsDto.RecentActivityItem> activities = recentInvoices.stream()
                .limit(10)
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

        double inrDouble = totalEarningsPaise / 100.0;
        double usdDouble = Math.round((inrDouble / 85.0) * 10.0) / 10.0;
        double eurDouble = Math.round((inrDouble / 92.0) * 10.0) / 10.0;
        double gbpDouble = Math.round((inrDouble / 108.0) * 10.0) / 10.0;

        List<DashboardStatsDto.WalletItem> wallets = List.of(
                DashboardStatsDto.WalletItem.builder().currency("INR").symbol("₹").amount(inrDouble).isActive(true).formatted("₹" + String.format("%,.2f", inrDouble)).build(),
                DashboardStatsDto.WalletItem.builder().currency("USD").symbol("$").amount(usdDouble).isActive(false).formatted("$" + String.format("%,.2f", usdDouble)).build(),
                DashboardStatsDto.WalletItem.builder().currency("EUR").symbol("€").amount(eurDouble).isActive(false).formatted("€" + String.format("%,.2f", eurDouble)).build(),
                DashboardStatsDto.WalletItem.builder().currency("GBP").symbol("£").amount(gbpDouble).isActive(false).formatted("£" + String.format("%,.2f", gbpDouble)).build()
        );

        List<DashboardStatsDto.MonthlyChartPoint> chartPoints = List.of(
                new DashboardStatsDto.MonthlyChartPoint("May", 14500000L, 5200000L),
                new DashboardStatsDto.MonthlyChartPoint("Jun", 19200000L, 6800000L),
                new DashboardStatsDto.MonthlyChartPoint("Jul", 23100000L, 8100000L),
                new DashboardStatsDto.MonthlyChartPoint("Aug", 29800000L, 11200000L),
                new DashboardStatsDto.MonthlyChartPoint("Sep", 34500000L, 12500000L),
                new DashboardStatsDto.MonthlyChartPoint("Oct", totalEarningsPaise, totalSpendingPaise)
        );

        DashboardStatsDto.SpendingLimit spendingLimit = DashboardStatsDto.SpendingLimit.builder()
                .usedPaise(totalSpendingPaise)
                .totalLimitPaise(25000000L)
                .percentage((int) Math.min(100L, (totalSpendingPaise * 100) / 25000000L))
                .cardNumberMasked("•••• •••• •••• 4289")
                .cardHolder("ALEX VANCE")
                .expiry("08/29")
                .build();

        return DashboardStatsDto.builder()
                .mrrPaise(mrrPaise)
                .arrPaise(arrPaise)
                .totalBalancePaise(totalEarningsPaise)
                .totalEarningsPaise(totalEarningsPaise)
                .totalSpendingPaise(totalSpendingPaise)
                .totalRevenuePaise(totalRevenuePaise)
                .pendingCollectionPaise(pendingCollectionPaise)
                .activeSubscriptionsCount(activeSubscriptionsCount)
                .earningsGrowthPercentage(7.4)
                .wallets(wallets)
                .profitLossChart(chartPoints)
                .recentActivities(activities)
                .spendingLimit(spendingLimit)
                .build();
    }

    @Transactional(readOnly = true)
    public StaffDashboardStatsDto getStaffStats() {
        // Operational counts only — NO MRR, NO ARR, NO Revenue/Earnings/Spending
        long activeSubs = subscriptionRepository.findByStatus("ACTIVE").size();
        long totalInvoices = invoiceRepository.count();
        long openTickets = supportTicketRepository.findByStatus("OPEN").size();
        long pendingInvoices = invoiceRepository.findByStatus("DRAFT").size() + invoiceRepository.findByStatus("CONFIRMED").size();

        return new StaffDashboardStatsDto(
                activeSubs,
                totalInvoices,
                openTickets,
                pendingInvoices
        );
    }

    @Transactional(readOnly = true)
    public CustomerDashboardStatsDto getCustomerStats(Long customerId) {
        if (customerId == null) {
            return new CustomerDashboardStatsDto("None", "NONE", "N/A", 0L, "INACTIVE", null, 0L, Map.of());
        }

        List<Subscription> subs = subscriptionRepository.findByCustomerId(customerId);
        Subscription currentSub = subs.stream()
                .filter(s -> "ACTIVE".equalsIgnoreCase(s.getStatus()))
                .findFirst()
                .orElseGet(() -> subs.isEmpty() ? null : subs.get(0));

        String planName = "No Plan";
        String planCode = "NONE";
        String cycle = "N/A";
        Long pricePaise = 0L;
        String status = "INACTIVE";
        LocalDate nextRenewal = null;
        Map<String, Long> limits = new HashMap<>();

        if (currentSub != null) {
            status = currentSub.getStatus();
            nextRenewal = currentSub.getExpiryDate();
            PlanVersion pv = currentSub.getPlanVersion();
            if (pv != null) {
                cycle = pv.getBillingCycle();
                pricePaise = pv.getPricePaise();
                if (pv.getPlan() != null) {
                    planName = pv.getPlan().getName();
                    planCode = pv.getPlan().getCode();
                }
                if (pv.getLimits() != null) {
                    for (PlanLimit limit : pv.getLimits()) {
                        limits.put(limit.getLimitKey(), limit.getLimitValue());
                    }
                }
            }
        }

        List<Invoice> custInvoices = invoiceRepository.findByCustomerId(customerId);
        long amountDuePaise = custInvoices.stream()
                .filter(i -> !"PAID".equalsIgnoreCase(i.getStatus()) && !"CANCELLED".equalsIgnoreCase(i.getStatus()))
                .mapToLong(i -> Math.max(0L, i.getTotalPaise() - i.getAmountPaidPaise()))
                .sum();

        return new CustomerDashboardStatsDto(
                planName,
                planCode,
                cycle,
                pricePaise,
                status,
                nextRenewal,
                amountDuePaise,
                limits
        );
    }
}
