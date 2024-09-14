package com.paulohsmelo.crmservice.repository;

import com.paulohsmelo.crmservice.entity.Customer;
import com.paulohsmelo.crmservice.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Optional<Customer> findByCreatedBy(User user);

    Optional<Customer> findByModifiedBy(User user);
}
