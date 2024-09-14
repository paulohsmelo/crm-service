package com.paulohsmelo.crmservice.mapper;

import com.paulohsmelo.crmservice.dto.UserDTO;
import com.paulohsmelo.crmservice.entity.User;

public class UserMapper {

    public static UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().toValue())
                .build();
    }
}
