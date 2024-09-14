package com.paulohsmelo.crmservice.mapper;

import com.paulohsmelo.crmservice.dto.CustomerDTO;
import com.paulohsmelo.crmservice.entity.Customer;
import com.paulohsmelo.crmservice.entity.User;

import static java.util.Optional.ofNullable;

public class CustomerMapper {

    public static CustomerDTO mapToDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .photoUrl(customer.getPhotoUrl())
                .createdBy(customer.getCreatedBy().getUsername())
                .modifiedBy(ofNullable(customer.getModifiedBy()).map(User::getUsername).orElse(null))
                .build();
    }
}
