package com.mongodb.application.web;

import com.mongodb.domain.model.Customer;
import com.mongodb.domain.model.CustomersByCity;
import com.mongodb.domain.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    CustomerService customerService;

    CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Customer> insert(@RequestBody Customer customer) {
        return ResponseEntity.status(201).body(customerService.insert(customer));
    }

    @GetMapping
    public ResponseEntity<List<Customer>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Customer> findCustomerByEmail(@RequestParam String email) {
        return ResponseEntity.ok(customerService.findCustomerByEmail(email));
    }

    @GetMapping("/report/total-by-city")
    public ResponseEntity<List<CustomersByCity>> totalCustomerByCity() {
        return ResponseEntity.ok(customerService.totalCustomerByCity());
    }

    @GetMapping("/diagnostics/indexes")
    public ResponseEntity<String> getCustomerIndexExplanation() {
        return ResponseEntity.ok(customerService.getCustomerIndexExplanation());
    }

    @PatchMapping("/{email}/phone")
    public ResponseEntity<Customer> updateCustomerPhone(@PathVariable String email, @RequestParam String phone) {
        return ResponseEntity.ok(customerService.updatePhoneByEmail(email, phone));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteCustomerByEmail(@PathVariable String email) {
        customerService.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{accountNumber}/with-transaction")
    public ResponseEntity<String> deleteAndRecreateWithTransaction(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "false") boolean failBeforeRecreating) {
        return ResponseEntity.ok(
                customerService.deleteAndRecreateCustomerWithTransaction(accountNumber, failBeforeRecreating));
    }

    @PostMapping("/{accountNumber}/without-transaction")
    public ResponseEntity<String> deleteAndRecreateWithoutTransaction(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "false") boolean failBeforeRecreating) {
        return ResponseEntity.ok(
                customerService.deleteAndRecreateCustomerWithoutTransaction(accountNumber, failBeforeRecreating));
    }

}
