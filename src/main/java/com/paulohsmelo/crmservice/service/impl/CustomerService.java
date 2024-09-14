package com.paulohsmelo.crmservice.service.impl;

import com.paulohsmelo.crmservice.dto.CreateCustomerDTO;
import com.paulohsmelo.crmservice.dto.CustomerDTO;
import com.paulohsmelo.crmservice.entity.Customer;
import com.paulohsmelo.crmservice.exception.NoDataFoundException;
import com.paulohsmelo.crmservice.mapper.CustomerMapper;
import com.paulohsmelo.crmservice.repository.CustomerRepository;
import com.paulohsmelo.crmservice.service.ICustomerService;
import com.paulohsmelo.crmservice.service.IUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.paulohsmelo.crmservice.mapper.CustomerMapper.mapToDTO;
import static java.util.stream.StreamSupport.stream;

@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;

    private final IUploadService uploadService;

    @Override
    public List<CustomerDTO> findAll() {
        return stream(customerRepository.findAll().spliterator(), false)
                .map(CustomerMapper::mapToDTO)
                .toList();
    }

    @Override
    public CustomerDTO findById(Long id) {
        return mapToDTO(getCustomer(id));
    }

    @Override
    public CustomerDTO create(CreateCustomerDTO createCustomerDTO) {
        Customer customer = Customer.builder()
                .name(createCustomerDTO.getName())
                .surname(createCustomerDTO.getSurname())
                .build();
        return mapToDTO(customerRepository.save(customer));
    }

    @Override
    public CustomerDTO update(Long id, CreateCustomerDTO customerDTO) {
        Customer customer = getCustomer(id);

        customer.setName(customerDTO.getName());
        customer.setSurname(customerDTO.getSurname());

        return mapToDTO(customerRepository.save(customer));
    }

    @Override
    public void delete(Long id) {
        Customer customer = getCustomer(id);
        customerRepository.deleteById(customer.getId());
    }

    @Override
    public CustomerDTO uploadPhoto(Long id, MultipartFile file) {
        Customer customer = getCustomer(id);

        String photoUrl = uploadService.uploadCustomerPhoto(customer.getId(), file);

        customer.setPhotoUrl(photoUrl);
        customerRepository.save(customer);

        return mapToDTO(customer);
    }

    private Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NoDataFoundException("Customer id " + id + " not found"));
    }
}
