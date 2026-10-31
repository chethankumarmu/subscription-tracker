package com.brightloop.subscription.plans.service;

import com.brightloop.subscription.auth.entity.User;
import com.brightloop.subscription.auth.repository.UserRepository;
import com.brightloop.subscription.catalog.entity.Product;
import com.brightloop.subscription.catalog.repository.ProductRepository;
import com.brightloop.subscription.plans.dto.PlanVersionDto;
import com.brightloop.subscription.plans.dto.PublishedPlanDto;
import com.brightloop.subscription.plans.entity.Plan;
import com.brightloop.subscription.plans.entity.PlanLimit;
import com.brightloop.subscription.plans.entity.PlanVersion;
import com.brightloop.subscription.plans.repository.PlanLimitRepository;
import com.brightloop.subscription.plans.repository.PlanRepository;
import com.brightloop.subscription.plans.repository.PlanVersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private PlanVersionRepository planVersionRepository;

    @Mock
    private PlanLimitRepository planLimitRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    private PlanService planService;

    @BeforeEach
    void setUp() {
        planService = new PlanService(
                planRepository,
                planVersionRepository,
                planLimitRepository,
                productRepository,
                userRepository
        );
    }

    @Test
    void testPublishingClosesPreviousVersion() {
        Plan plan = Plan.builder()
                .id(1L)
                .code("GROWTH")
                .name("Growth Plan")
                .status("PUBLISHED")
                .build();

        PlanVersion prevVersion = PlanVersion.builder()
                .id(10L)
                .plan(plan)
                .versionNo(1)
                .status("PUBLISHED")
                .pricePaise(149900L)
                .billingCycle("MONTHLY")
                .availableFrom(LocalDate.now().minusMonths(2))
                .effectiveFrom(OffsetDateTime.now().minusMonths(2))
                .effectiveTo(null)
                .build();

        PlanVersion newVersion = PlanVersion.builder()
                .id(20L)
                .plan(plan)
                .versionNo(2)
                .status("DRAFT")
                .pricePaise(159900L)
                .billingCycle("MONTHLY")
                .availableFrom(LocalDate.now().minusDays(1))
                .effectiveFrom(OffsetDateTime.now().minusDays(1))
                .effectiveTo(null)
                .build();

        plan.setCurrentVersion(prevVersion);

        PlanLimit limit = PlanLimit.builder()
                .id(100L)
                .planVersion(newVersion)
                .limitKey("USERS")
                .limitValue(15L)
                .build();

        when(planVersionRepository.findById(20L)).thenReturn(Optional.of(newVersion));
        when(planLimitRepository.findByPlanVersionId(20L)).thenReturn(List.of(limit));
        when(planVersionRepository.findCurrentPublishedVersion(1L)).thenReturn(Optional.of(prevVersion));
        when(planVersionRepository.save(any(PlanVersion.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(planRepository.save(any(Plan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlanVersionDto publishedDto = planService.publishVersion(20L);

        // Verify previous version is closed (effectiveTo set and status RETIRED)
        ArgumentCaptor<PlanVersion> versionCaptor = ArgumentCaptor.forClass(PlanVersion.class);
        verify(planVersionRepository, atLeast(2)).save(versionCaptor.capture());

        List<PlanVersion> savedVersions = versionCaptor.getAllValues();
        PlanVersion closedPrev = savedVersions.stream().filter(v -> v.getId().equals(10L)).findFirst().orElseThrow();
        assertNotNull(closedPrev.getEffectiveTo(), "Previous version effectiveTo must be set to now");
        assertEquals("RETIRED", closedPrev.getStatus());

        // Verify new version is published
        assertEquals("PUBLISHED", publishedDto.getStatus());
        assertEquals(2, publishedDto.getVersionNo());
        assertEquals(159900L, publishedDto.getPricePaise());

        // Verify plan current version was updated
        ArgumentCaptor<Plan> planCaptor = ArgumentCaptor.forClass(Plan.class);
        verify(planRepository).save(planCaptor.capture());
        Plan savedPlan = planCaptor.getValue();
        assertEquals("PUBLISHED", savedPlan.getStatus());
        assertEquals(20L, savedPlan.getCurrentVersion().getId());
    }

    @Test
    void testEditingPublishedVersionIsRejected() {
        PlanVersion publishedVersion = PlanVersion.builder()
                .id(10L)
                .status("PUBLISHED")
                .effectiveTo(null)
                .build();

        when(planVersionRepository.findById(10L)).thenReturn(Optional.of(publishedVersion));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> planService.validateCanEditVersion(10L)
        );

        assertTrue(ex.getMessage().contains("Editing an existing published version is forbidden"));
    }

    @Test
    void testGetPublishedPlansReturnsOnlyPublishedPlans() {
        Plan publishedPlan = Plan.builder()
                .id(1L)
                .code("STARTER")
                .name("Starter Plan")
                .status("PUBLISHED")
                .build();

        PlanVersion activeVersion = PlanVersion.builder()
                .id(11L)
                .plan(publishedPlan)
                .versionNo(1)
                .status("PUBLISHED")
                .pricePaise(49900L)
                .billingCycle("MONTHLY")
                .effectiveTo(null)
                .build();
        publishedPlan.setCurrentVersion(activeVersion);

        Plan draftPlan = Plan.builder()
                .id(2L)
                .code("INTERNAL")
                .name("Internal Plan")
                .status("DRAFT")
                .build();

        when(planRepository.findByStatus("PUBLISHED")).thenReturn(List.of(publishedPlan));
        when(planLimitRepository.findByPlanVersionId(11L)).thenReturn(List.of(
                PlanLimit.builder().limitKey("USERS").limitValue(3L).build(),
                PlanLimit.builder().limitKey("STORAGE_GB").limitValue(10L).build()
        ));

        List<PublishedPlanDto> result = planService.getPublishedPlans();

        assertEquals(1, result.size());
        PublishedPlanDto dto = result.get(0);
        assertEquals("STARTER", dto.getCode());
        assertEquals("PUBLISHED", dto.getStatus());
        assertEquals(49900L, dto.getPricePaise());
        assertEquals(3L, dto.getLimits().get("USERS"));
        assertEquals(10L, dto.getLimits().get("STORAGE_GB"));
    }
}
