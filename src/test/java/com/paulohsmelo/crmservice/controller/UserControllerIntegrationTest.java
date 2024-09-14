package com.paulohsmelo.crmservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paulohsmelo.crmservice.containers.Postgres;
import com.paulohsmelo.crmservice.dto.ChangeUserRoleDTO;
import com.paulohsmelo.crmservice.dto.CreateUserDTO;
import com.paulohsmelo.crmservice.dto.UserDTO;
import com.paulohsmelo.crmservice.entity.Customer;
import com.paulohsmelo.crmservice.entity.User;
import com.paulohsmelo.crmservice.entity.UserRole;
import com.paulohsmelo.crmservice.repository.CustomerRepository;
import com.paulohsmelo.crmservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest extends Postgres {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        userRepository.deleteAll();
        createUser(ADMIN_USERNAME, ADMIN_PASSWORD, UserRole.ROLE_ADMIN);
    }

    @Test
    void unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/all")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("wrongUser", "wrongPassword")))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void forbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/all")
                        .with(SecurityMockMvcRequestPostProcessors.user(ADMIN_USERNAME)
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void listAllUsers() throws Exception {
        createUser("User1", "Password1", UserRole.ROLE_USER);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/user/all")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        UserDTO[] users = objectMapper.readValue(response.getResponse().getContentAsString(), UserDTO[].class);
        assertNotNull(users, "Users list is null");
        assertEquals(users.length, 2, "Users list should have 2 users");
    }

    @Test
    void listUserById() throws Exception {
        User user = createUser("User2", "Password2", UserRole.ROLE_USER);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/user/" + user.getId())
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        UserDTO userDTO = objectMapper.readValue(response.getResponse().getContentAsString(), UserDTO.class);
        assertEquals(userDTO.getId(), user.getId() , "User id don't match");
        assertEquals(userDTO.getUsername(), user.getUsername() , "User name don't match");
        assertEquals(userDTO.getRole(), user.getRole().toValue() , "User role don't match");
    }

    @Test
    void listUserByIdNotFound() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/user/999")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        assertEquals(response.getResponse().getContentAsString(), "User id 999 not found");
    }

    @Test
    void createUser() throws Exception {
        CreateUserDTO createUserDTO = CreateUserDTO.builder().username("User3").password("Password3").build();

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .content(objectMapper.writeValueAsString(createUserDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        UserDTO userDTO = objectMapper.readValue(response.getResponse().getContentAsString(), UserDTO.class);
        assertEquals(userDTO.getUsername(), createUserDTO.getUsername() , "User name don't match");
        assertEquals(userDTO.getRole(), UserRole.ROLE_USER.toValue() , "User role don't match");

        assertTrue(userRepository.existsById(userDTO.getId()), "User wasn't created in the database");
    }

    @Test
    void createUserValidationFailed() throws Exception {
        CreateUserDTO createUserDTO = CreateUserDTO.builder().username("  ").password(null).build();

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .content(objectMapper.writeValueAsString(createUserDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String responseBody = response.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Username is required"));
        assertTrue(responseBody.contains("Password is required"));
    }

    @Test
    void updateUser() throws Exception {
        User user = createUser("User4", "Password4", UserRole.ROLE_USER);

        CreateUserDTO createUserDTO = CreateUserDTO.builder().username("User4-updated").password("Password4-updated").build();

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/user/" + user.getId())
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .content(objectMapper.writeValueAsString(createUserDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        UserDTO userDTO = objectMapper.readValue(response.getResponse().getContentAsString(), UserDTO.class);
        assertEquals(userDTO.getUsername(), "User4-updated" , "User name don't match");

        Optional<User> databaseUser = userRepository.findById(user.getId());
        assertTrue(databaseUser.isPresent(), "User wasn't found in the database");
        assertEquals(databaseUser.get().getUsername(), "User4-updated" , "User name wasn't updated");
        assertNotEquals(databaseUser.get().getPassword(), user.getPassword() , "User wasn't updated");
    }

    @Test
    void deleteUser() throws Exception {
        User user = createUser("User5", "Password5", UserRole.ROLE_USER);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/user/" + user.getId())
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertEquals(response.getResponse().getContentAsString(), "User " + user.getId() + " was deleted successfully");
        assertFalse(userRepository.existsById(user.getId()), "User wasn't deleted in the database");
    }

    @Test
    void deleteUserWithCustomerReference() throws Exception {
        User user = createUser("User6", "Password6", UserRole.ROLE_USER);
        Customer customer = Customer.builder().name("Customer").surname("Surname").createdBy(user).build();
        customerRepository.save(customer);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/user/" + user.getId())
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andReturn();

        assertEquals(response.getResponse().getContentAsString(), "User " + user.getId() + " has reference to customers and cannot be deleted");
    }

    @Test
    void changeRole() throws Exception {
        User user = createUser("User7", "Password7", UserRole.ROLE_USER);

        ChangeUserRoleDTO changeUserRoleDTO = ChangeUserRoleDTO.builder().role(UserRole.ROLE_ADMIN.toValue()).build();

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/user/" + user.getId() + "/role")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .content(objectMapper.writeValueAsString(changeUserRoleDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        UserDTO userDTO = objectMapper.readValue(response.getResponse().getContentAsString(), UserDTO.class);
        assertEquals(userDTO.getUsername(), "User7" , "User name don't match");
        assertEquals(userDTO.getRole(), UserRole.ROLE_ADMIN.toValue() , "User role don't match");

        Optional<User> databaseUser = userRepository.findById(user.getId());
        assertTrue(databaseUser.isPresent(), "User wasn't found in the database");
        assertEquals(databaseUser.get().getRole(), UserRole.ROLE_ADMIN, "User role wasn't updated");
    }

    @Test
    void changeRoleInvalidValue() throws Exception {
        User user = createUser("User8", "Password8", UserRole.ROLE_USER);

        ChangeUserRoleDTO changeUserRoleDTO = ChangeUserRoleDTO.builder().role("invalid_role").build();

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/user/" + user.getId() + "/role")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .content(objectMapper.writeValueAsString(changeUserRoleDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        assertTrue(response.getResponse().getContentAsString().contains("Invalid role value, options: admin | user"));
    }

    private User createUser(String username, String password, UserRole role) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = User.builder().username(username).password(encodedPassword).role(role).build();
        return userRepository.save(user);
    }
}
