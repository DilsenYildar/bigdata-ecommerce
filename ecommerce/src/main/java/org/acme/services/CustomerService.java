package org.acme.services;

import org.acme.crypto.Cryptographer;
import org.acme.models.entites.Customer;
import org.acme.repository.CustomerRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;


@Singleton
public class CustomerService {

    @Inject
    Cryptographer cryptographer;

    @Inject
    CustomerRepository customerRepository;

    @Inject
    ProductBuyersService productBuyersService;

    public Customer getByUsername(String username) {
        return customerRepository.find("username", username).firstResult();
    }

    @Transactional
    public void createCustomer(Customer customer) {
        customer.setPassword(cryptographer.encrypt(customer.getPassword()));
        customerRepository.persist(customer);
        productBuyersService.createUserToNeo4j(customer);
    }

    @Transactional
    public boolean deleteCustomer(String username) {
        return customerRepository.delete("username", username) != 0;
    }
}