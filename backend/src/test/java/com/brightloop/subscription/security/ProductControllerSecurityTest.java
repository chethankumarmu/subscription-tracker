package com.brightloop.subscription.security;

import com.brightloop.subscription.auth.security.JwtAuthenticationFilter;
import com.brightloop.subscription.auth.security.JwtService;
import com.brightloop.subscription.catalog.controller.ProductController;
import com.brightloop.subscription.catalog.dto.CreateProductRequest;
import com.brightloop.subscription.catalog.dto.ProductAdminDto;
import com.brightloop.subscription.catalog.service.ProductService;
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

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class ProductControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "staff_member", roles = "STAFF")
    void staff_cannotCreateProduct_returns403() throws Exception {
        CreateProductRequest request = new CreateProductRequest("SKU-1", "Product A", "SERVICE", 50000L, 20000L, "Notes");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "customer_user", roles = "CUSTOMER")
    void customer_cannotCreateProduct_returns403() throws Exception {
        CreateProductRequest request = new CreateProductRequest("SKU-1", "Product A", "SERVICE", 50000L, 20000L, "Notes");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_user", roles = "ADMIN")
    void admin_canCreateProduct_returns201() throws Exception {
        CreateProductRequest request = new CreateProductRequest("SKU-1", "Product A", "SERVICE", 50000L, 20000L, "Notes");
        ProductAdminDto responseDto = new ProductAdminDto(1L, "SKU-1", "Product A", "SERVICE", 50000L, 20000L, 30000L, "Notes", true);

        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}

