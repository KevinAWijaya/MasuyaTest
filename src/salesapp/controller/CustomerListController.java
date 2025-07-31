package salesapp.controller;

import salesapp.model.Customer;
import salesapp.repository.CustomerRepository;

import java.util.List;

public class CustomerListController {

    private final CustomerRepository repository;

    public CustomerListController() {
        this.repository = new CustomerRepository();
    }

    public List<Customer> getAllCustomers() {
        return repository.getAll();
    }

    public Customer getCustomerByCode(String code) {
        return repository.getByCode(code);
    }

    public boolean deleteCustomer(String code) {
        return repository.deleteCustomer(code);
    }
}
