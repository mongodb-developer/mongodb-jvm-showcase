package com.mongodb.application.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.domain.model.Transaction;
import com.mongodb.domain.model.Transaction.AccountDetails;
import com.mongodb.domain.model.Transaction.Beneficiary;
import com.mongodb.domain.model.Transaction.Originator;
import com.mongodb.domain.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

 @AutoConfigureMockMvc
@SpringBootTest
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateTransaction() throws Exception {
        Transaction transaction = createSampleTransaction();

        Mockito.when(transactionService.save(any())).thenReturn(transaction);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    void shouldListPaginatedTransactions() throws Exception {
        Mockito.when(transactionService.findAll(PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id"))))
                .thenReturn(new PageImpl<>(List.of(createSampleTransaction())));

        mockMvc.perform(get("/transactions")
                        .param("page", "0")
                        .param("size", "100")
                        .param("sortBy", "id")
                        .param("direction", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].currency").value("USD"));
    }

    @Test
    void shouldSearchTransactions() throws Exception {
        Mockito.when(transactionService.searchTransactions("Credit", "USD", "PENDING"))
                .thenReturn(List.of(createSampleTransaction()));

        mockMvc.perform(get("/transactions/search")
                        .param("type", "Credit")
                        .param("currency", "USD")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void shouldExportErrorTransactions() throws Exception {
        mockMvc.perform(post("/transactions/actions/export-errors"))
                .andExpect(status().isAccepted());
    }

    @Test
    void shouldUpdateTransaction() throws Exception {
        Transaction updated = createSampleTransaction();
        updated.setStatus("COMPLETED");

        Mockito.when(transactionService.update(any())).thenReturn(updated);

        mockMvc.perform(put("/transactions/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void shouldDeleteTransaction() throws Exception {
        mockMvc.perform(delete("/transactions/123"))
                .andExpect(status().isNoContent());
    }

    private Transaction createSampleTransaction() {
        return new Transaction(
                "123",
                "Credit",
                500.0,
                "USD",
                "PENDING",
                "Sample",
                new AccountDetails(
                        new Originator("111", "Ricardo", "Bank A"),
                        new Beneficiary("222", "Maria", "Bank B")
                ),
                "123",
                LocalDateTime.now());
    }
}
