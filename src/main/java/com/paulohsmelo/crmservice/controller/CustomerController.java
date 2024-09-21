package com.paulohsmelo.crmservice.controller;

import com.paulohsmelo.crmservice.dto.CreateCustomerDTO;
import com.paulohsmelo.crmservice.dto.CustomerDTO;
import com.paulohsmelo.crmservice.service.ICustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final ICustomerService customerService;

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CustomerDTO>> findAllCustomers() {
        log.debug("Find all customers");
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDTO> findCustomerById(@PathVariable Long id) {
        log.debug("Find customer by id: {}", id);
        return ResponseEntity.ok(customerService.findById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CreateCustomerDTO createCustomerDTO) {
        log.debug("Create customer: {}", createCustomerDTO);
        return ResponseEntity.ok(customerService.create(createCustomerDTO));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody CreateCustomerDTO createCustomerDTO) {
        log.debug("Update customer, id: {}, request body: {}", id, createCustomerDTO);
        return ResponseEntity.ok(customerService.update(id, createCustomerDTO));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        log.debug("Delete customer, id: {}", id);
        customerService.delete(id);
        return ResponseEntity.ok("Customer " + id + " was deleted successfully");
    }

    @PostMapping(value = "/{id}/photo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDTO> uploadPhoto(@PathVariable Long id, @Valid @RequestParam("photo") MultipartFile file) {
        log.debug("Upload photo, id: {}, fileName: {}", id, file.getOriginalFilename());
        return ResponseEntity.ok(customerService.uploadPhoto(id, file));
    }

    @GetMapping(value = "/{id}/photo", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getPhotoUrl(@PathVariable Long id) {
        log.debug("Get photo, id: {}", id);
        return ResponseEntity.ok(customerService.getPhotoUrl(id).toString());
    }

}
