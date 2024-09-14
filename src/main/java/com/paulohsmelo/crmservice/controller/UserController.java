package com.paulohsmelo.crmservice.controller;

import com.paulohsmelo.crmservice.dto.ChangeUserRoleDTO;
import com.paulohsmelo.crmservice.dto.CreateUserDTO;
import com.paulohsmelo.crmservice.dto.UserDTO;
import com.paulohsmelo.crmservice.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final IUserService userService;

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> findAllUsers() {
        log.debug("Find all users");
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> findUserById(@PathVariable Long id) {
        log.debug("Find user by id: {}", id);
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        log.debug("Create user: {}", createUserDTO);
        return ResponseEntity.ok(userService.create(createUserDTO));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody CreateUserDTO createUserDTO) {
        log.debug("Update user, id: {}, request body: {}", id, createUserDTO);
        return ResponseEntity.ok(userService.update(id, createUserDTO));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        log.debug("Delete user, id: {}", id);
        userService.delete(id);
        return ResponseEntity.ok("User " + id + " was deleted successfully");
    }

    @PostMapping(value = "/{id}/role", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> changeRole(@PathVariable Long id, @Valid @RequestBody ChangeUserRoleDTO changeUserRoleDTO) {
        log.debug("Change user role, id: {}, request body: {}", id, changeUserRoleDTO);
        return ResponseEntity.ok(userService.changeRole(id, changeUserRoleDTO));
    }
}
