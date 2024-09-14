package com.paulohsmelo.crmservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paulohsmelo.crmservice.containers.Postgres;
import com.paulohsmelo.crmservice.dto.CreateCustomerDTO;
import com.paulohsmelo.crmservice.dto.CustomerDTO;
import com.paulohsmelo.crmservice.entity.Customer;
import com.paulohsmelo.crmservice.entity.User;
import com.paulohsmelo.crmservice.repository.CustomerRepository;
import com.paulohsmelo.crmservice.repository.UserRepository;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CustomerControllerIntegrationTest extends Postgres {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @AfterEach
    void tearDown() throws IOException {
        FileSystemUtils.deleteRecursively(Path.of(TEST_UPLOADS));
    }

    @Test
    void unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/customer/all")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("wrongUser", "wrongPassword")))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void listAllCustomers() throws Exception {
        createCustomer("Customer1", "Password1");
        createCustomer("Customer2", "Password2");

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/customer/all")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomerDTO[] customers = objectMapper.readValue(response.getResponse().getContentAsString(), CustomerDTO[].class);
        assertNotNull(customers, "Customer list is null");
        assertEquals(customers.length, 2, "Customer list should have 2 customers");
    }

    @Test
    void listAllCustomersEmptyList() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/customer/all")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomerDTO[] customers = objectMapper.readValue(response.getResponse().getContentAsString(), CustomerDTO[].class);
        assertNotNull(customers, "Customer list is null");
        assertEquals(customers.length, 0, "Customer list should be empty");
    }

    @Test
    void listCustomerById() throws Exception {
        Customer customer = createCustomer("Customer3", "Password3");

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/customer/" + customer.getId())
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomerDTO customerDTO = objectMapper.readValue(response.getResponse().getContentAsString(), CustomerDTO.class);
        assertEquals(customerDTO.getId(), customer.getId(), "Customer id don't match");
        assertEquals(customerDTO.getName(), customer.getName(), "Customer name don't match");
        assertEquals(customerDTO.getSurname(), customer.getSurname(), "Customer surname don't match");
    }

    @Test
    void listCustomerByIdNotFound() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/customer/999")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        assertEquals(response.getResponse().getContentAsString(), "Customer id 999 not found");
    }

    @Test
    void createCustomer() throws Exception {
        CreateCustomerDTO createCustomerDTO = CreateCustomerDTO.builder().name("Customer4").surname("Surname4").build();

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/customer")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .content(objectMapper.writeValueAsString(createCustomerDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomerDTO customer = objectMapper.readValue(response.getResponse().getContentAsString(), CustomerDTO.class);
        assertEquals(customer.getName(), "Customer4", "Customer name don't match");
        assertEquals(customer.getSurname(), "Surname4", "Customer surname don't match");
        assertEquals(customer.getCreatedBy(), ADMIN_USERNAME, "CreatedBy user don't match");
        assertTrue(customerRepository.existsById(customer.getId()), "Customer wasn't created in the database");
    }

    @Test
    void createCustomerValidationFailed() throws Exception {
        CreateCustomerDTO createCustomerDTO = CreateCustomerDTO.builder().name("   ").surname(null).build();

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/customer")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .content(objectMapper.writeValueAsString(createCustomerDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String responseBody = response.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Name is required"));
        assertTrue(responseBody.contains("Surname is required"));
    }

    @Test
    void updateCustomer() throws Exception {
        Customer customer = createCustomer("Customer5", "Password5");

        CreateCustomerDTO createCustomerDTO = CreateCustomerDTO.builder().name("Customer5-updated").surname("Surname5-updated").build();

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/customer/" + customer.getId())
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .content(objectMapper.writeValueAsString(createCustomerDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomerDTO customerDTO = objectMapper.readValue(response.getResponse().getContentAsString(), CustomerDTO.class);
        assertEquals(customerDTO.getName(), "Customer5-updated", "Customer name don't match");
        assertEquals(customerDTO.getSurname(), "Surname5-updated", "Customer surname don't match");
        assertEquals(customerDTO.getCreatedBy(), ADMIN_USERNAME, "CreatedBy user don't match");
        assertEquals(customerDTO.getModifiedBy(), ADMIN_USERNAME, "ModifiedBy user don't match");

        Optional<Customer> databaseCustomer = customerRepository.findById(customer.getId());
        assertTrue(databaseCustomer.isPresent(), "Customer wasn't found in the database");
        assertEquals(databaseCustomer.get().getName(), "Customer5-updated", "Customer name wasn't updated");
        assertEquals(databaseCustomer.get().getSurname(), "Surname5-updated", "Customer surname was updated");
    }

    @Test
    void deleteCustomer() throws Exception {
        Customer customer = createCustomer("Customer6", "Password6");

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/customer/" + customer.getId())
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertEquals(response.getResponse().getContentAsString(), "Customer " + customer.getId() + " was deleted successfully");
        assertFalse(customerRepository.existsById(customer.getId()), "Customer wasn't deleted in the database");
    }

    @Test
    void uploadPhoto() throws Exception {
        Customer customer = createCustomer("Customer7", "Password7");

        byte[] image = IOUtils.toByteArray(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("photo-test.jpg")));

        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("photo", "photo-test.jpg", MediaType.IMAGE_JPEG_VALUE, image);

        MvcResult response = mockMvc.perform(
                MockMvcRequestBuilders.multipart("/customer/" + customer.getId() + "/photo")
                        .file(mockMultipartFile)
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CustomerDTO customerDTO = objectMapper.readValue(response.getResponse().getContentAsString(), CustomerDTO.class);
        assertEquals(customerDTO.getPhotoUrl(), TEST_UPLOADS + customer.getId() + "/photo-test.jpg");
    }

    @Test
    void uploadPhotoFileAlreadyExists() throws Exception {
        Customer customer = createCustomer("Customer8", "Password8");

        // Create a file in the customer directory
        String uploadPath = TEST_UPLOADS + customer.getId();
        Files.createDirectories(Path.of(uploadPath));
        Files.createFile(Path.of(uploadPath + "/photo-test.jpg"));

        byte[] image = IOUtils.toByteArray(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("photo-test.jpg")));

        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("photo", "photo-test.jpg", MediaType.IMAGE_JPEG_VALUE, image);

        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/customer/" + customer.getId() + "/photo")
                                .file(mockMultipartFile)
                                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        assertEquals(response.getResponse().getContentAsString(), "File already exists: photo-test.jpg");
    }

    @Test
    void uploadPhotoEmptyFile() throws Exception {
        Customer customer = createCustomer("Customer9", "Password9");

        MockMultipartFile mockMultipartFile = new MockMultipartFile("photo", "empty-file.jpg", null, (byte[]) null);

        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/customer/" + customer.getId() + "/photo")
                                .file(mockMultipartFile)
                                .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        assertEquals(response.getResponse().getContentAsString(), "File is empty: empty-file.jpg");
    }

    private Customer createCustomer(String name, String surname) {
        User admin = userRepository.findByUsername(ADMIN_USERNAME);
        Customer customer = Customer.builder().name(name).surname(surname).createdBy(admin).build();
        return customerRepository.save(customer);
    }

}
