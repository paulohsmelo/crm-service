package com.paulohsmelo.crmservice.service.impl;

import com.paulohsmelo.crmservice.dto.ChangeUserRoleDTO;
import com.paulohsmelo.crmservice.dto.CreateUserDTO;
import com.paulohsmelo.crmservice.dto.UserDTO;
import com.paulohsmelo.crmservice.entity.User;
import com.paulohsmelo.crmservice.entity.UserRole;
import com.paulohsmelo.crmservice.exception.NoDataFoundException;
import com.paulohsmelo.crmservice.exception.UnprocessableEntityException;
import com.paulohsmelo.crmservice.mapper.UserMapper;
import com.paulohsmelo.crmservice.repository.CustomerRepository;
import com.paulohsmelo.crmservice.repository.UserRepository;
import com.paulohsmelo.crmservice.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paulohsmelo.crmservice.mapper.UserMapper.mapToDTO;
import static java.util.stream.StreamSupport.stream;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> findAll() {
        return stream(userRepository.findAll().spliterator(), false)
                .map(UserMapper::mapToDTO)
                .toList();
    }

    @Override
    public UserDTO findById(Long id) {
        return mapToDTO(getUser(id));
    }

    @Override
    public UserDTO create(CreateUserDTO createUserDTO) {
        User user = User.builder()
                .username(createUserDTO.getUsername())
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .role(UserRole.ROLE_USER)
                .build();
        return mapToDTO(userRepository.save(user));
    }

    @Override
    public UserDTO update(Long id, CreateUserDTO createUserDTO) {
        User user = getUser(id);

        user.setUsername(createUserDTO.getUsername());
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));

        return mapToDTO(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        User user = getUser(id);

        if (userHasCustomerReference(user)) {
            throw new UnprocessableEntityException("User " + id + " has reference to customers and cannot be deleted");
        }

        userRepository.deleteById(user.getId());
    }

    @Override
    public UserDTO changeRole(Long id, ChangeUserRoleDTO changeUserRoleDTO) {
        User user = getUser(id);
        user.setRole(UserRole.forValue(changeUserRoleDTO.getRole()));
        return mapToDTO(userRepository.save(user));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoDataFoundException("User id " + id + " not found"));
    }

    private boolean userHasCustomerReference(User user) {
        return customerRepository.findByCreatedBy(user).isPresent() || customerRepository.findByModifiedBy(user).isPresent();
    }
}
