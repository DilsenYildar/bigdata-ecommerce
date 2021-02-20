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

    public Customer get(String username) throws Exception {
        return customerRepository.find("username", username).firstResult();
    }

    @Transactional
    public void set(Customer customer) {
        customer.setPassword(cryptographer.encrypt(customer.getPassword()));
        customerRepository.persist(customer);
    }

    @Transactional
    public boolean remove(String username) {
        return customerRepository.delete("username", username) != 0;
    }
}