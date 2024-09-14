package com.paulohsmelo.crmservice.service;

import com.paulohsmelo.crmservice.dto.ChangeUserRoleDTO;
import com.paulohsmelo.crmservice.dto.CreateUserDTO;
import com.paulohsmelo.crmservice.dto.UserDTO;

import java.util.List;

public interface IUserService {

    List<UserDTO> findAll();

    UserDTO findById(Long id);

    UserDTO create(CreateUserDTO createUserDTO);

    UserDTO update(Long id, CreateUserDTO createUserDTO);

    void delete(Long id);

    UserDTO changeRole(Long id, ChangeUserRoleDTO changeUserRoleDTO);
}
