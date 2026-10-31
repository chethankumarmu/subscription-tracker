package com.brightloop.subscription.auth.service;

import com.brightloop.subscription.auth.dto.CustomerDto;
import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.auth.repository.CustomerRepository;
import com.brightloop.subscription.auth.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerDto getCustomerById(Long id, UserPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        if ("CUSTOMER".equalsIgnoreCase(principal.getRole())) {
            if (principal.getCustomerId() == null || !principal.getCustomerId().equals(id)) {
                // Return 404 so existence is not revealed
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
            }
        }

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        return toDto(customer);
    }

    public CustomerDto toDto(Customer c) {
        return new CustomerDto(
                c.getId(),
                c.getUserId(),
                c.getCompanyName(),
                c.getContactName(),
                c.getPhone(),
                c.getBillingAddress(),
                c.getGstin(),
                c.getStatus(),
                c.getCreatedAt()
        );
    }
}

