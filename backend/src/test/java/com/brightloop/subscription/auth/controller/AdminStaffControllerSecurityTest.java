package com.brightloop.subscription.auth.controller;

import com.brightloop.subscription.auth.dto.CreateStaffRequest;
import com.brightloop.subscription.auth.dto.UserProfileResponse;
import com.brightloop.subscription.auth.security.JwtAuthenticationFilter;
import com.brightloop.subscription.auth.security.JwtService;
import com.brightloop.subscription.auth.service.AuthService;
import com.brightloop.subscription.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminStaffController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AdminStaffControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "customer", roles = "CUSTOMER")
    void createStaff_shouldReturnForbidden_whenUserHasCustomerRole() throws Exception {
        CreateStaffRequest request = new CreateStaffRequest("staff@example.com", "P@ssword123", "Staff Member");

        mockMvc.perform(post("/api/admin/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "staff", roles = "STAFF")
    void createStaff_shouldReturnForbidden_whenUserHasStaffRole() throws Exception {
        CreateStaffRequest request = new CreateStaffRequest("staff@example.com", "P@ssword123", "Staff Member");

        mockMvc.perform(post("/api/admin/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createStaff_shouldReturnForbiddenOrUnauthorized_whenUnauthenticated() throws Exception {
        CreateStaffRequest request = new CreateStaffRequest("staff@example.com", "P@ssword123", "Staff Member");

        mockMvc.perform(post("/api/admin/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createStaff_shouldSucceed_whenUserHasAdminRole() throws Exception {
        CreateStaffRequest request = new CreateStaffRequest("staff@example.com", "P@ssword123", "Staff Member");
        UserProfileResponse response = new UserProfileResponse(2L, "staff@example.com", "Staff Member", "STAFF", null);

        when(authService.createStaff(any(CreateStaffRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/admin/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}

