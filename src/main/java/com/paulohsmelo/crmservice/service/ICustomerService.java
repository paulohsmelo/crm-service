package com.paulohsmelo.crmservice.service;

import com.paulohsmelo.crmservice.dto.CreateCustomerDTO;
import com.paulohsmelo.crmservice.dto.CustomerDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ICustomerService {

    List<CustomerDTO> findAll();

    CustomerDTO findById(Long id);

    CustomerDTO create(CreateCustomerDTO createCustomerDTO);

    CustomerDTO update(Long id, CreateCustomerDTO customerDTO);

    void delete(Long id);

    CustomerDTO uploadPhoto(Long id, MultipartFile file);
}
