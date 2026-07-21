package com.mongodb.application.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.domain.model.Customer;
import com.mongodb.domain.model.CustomersByCity;
import com.mongodb.domain.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @Test
    void shouldCreateCustomer() throws Exception {

        var customer = createTestCustomer();

        Mockito.when(customerService.insert(Mockito.any(Customer.class))).thenReturn(customer);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ricardo.mello@mongodb.com"));
    }

    @Test
    void shouldFindAllCustomers() throws Exception {
        Mockito.when(customerService.findAll()).thenReturn(List.of(createTestCustomer()));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ricardo.mello@mongodb.com"));
    }

    @Test
    void shouldFindCustomerByEmail() throws Exception {
        Mockito.when(customerService.findCustomerByEmail("ricardo.mello@mongodb.com"))
                .thenReturn(createTestCustomer());

        mockMvc.perform(get("/customers/search")
                        .param("email", "ricardo.mello@mongodb.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ricardo"));
    }

    @Test
    void shouldUpdateCustomerPhone() throws Exception {
        Customer updated = createTestCustomer();
        updated = new Customer(updated.name(), updated.email(), updated.accountNumber(), "1766323", updated.address());

        Mockito.when(customerService.updatePhoneByEmail(eq("ricardo.mello@mongodb.com"), eq("1766323")))
                .thenReturn(updated);

        mockMvc.perform(patch("/customers/ricardo.mello@mongodb.com/phone")
                        .param("phone", "1766323"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("1766323"));
    }

    @Test
    void shouldGetTotalCustomersByCity() throws Exception {
        CustomersByCity result = new CustomersByCity("São Paulo", 5);
        Mockito.when(customerService.totalCustomerByCity()).thenReturn(List.of(result));

        mockMvc.perform(get("/customers/report/total-by-city"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("São Paulo"));
    }

    @Test
    void shouldExplainIndexes() throws Exception {
        Mockito.when(customerService.getCustomerIndexExplanation())
                .thenReturn("{ index info }");

        mockMvc.perform(get("/customers/diagnostics/indexes"))
                .andExpect(status().isOk())
                .andExpect(content().string("{ index info }"));
    }

    @Test
    void shouldDeleteCustomerByEmail() throws Exception {
        mockMvc.perform(delete("/customers/ricardo.mello@mongodb.com"))
                .andExpect(status().isNoContent());
    }

    private Customer createTestCustomer() {
        return new Customer(
                "Ricardo",
                "ricardo.mello@mongodb.com",
                "12345",
                "999999999",
                new Customer.Address("Street 001", "São Paulo")
        );
    }

}
