package salesapp.repository;

import salesapp.dao.CustomerDAO;
import salesapp.model.Customer;

import java.util.List;
import salesapp.utils.ValidationUtils;

public class CustomerRepository {

    private final CustomerDAO dao;

    public CustomerRepository() {
        dao = new CustomerDAO();
    }

    public AddIntoDatabaseResult addCustomer(Customer customer) {
        if (customer.getCode() == null || customer.getCode().trim().isEmpty()) {
            return AddIntoDatabaseResult.INVALID;
        }

        if (!ValidationUtils.isAlphanumeric(customer.getCode())) {
            return AddIntoDatabaseResult.INVALID;
        }

        if (isCodeExist(customer.getCode())) {
            return AddIntoDatabaseResult.DUPLICATE;
        }

        boolean inserted = dao.insert(customer);
        return inserted ? AddIntoDatabaseResult.SUCCESS : AddIntoDatabaseResult.FAILED;
    }

    public List<Customer> getAll() {
        return dao.getAll();
    }

    public Customer getByCode(String code) {
        return dao.findByCode(code);
    }

    public boolean updateCustomer(Customer customer) {
        return dao.update(customer);
    }

    public boolean deleteCustomer(String code) {
        return dao.delete(code);
    }

    public boolean isCodeExist(String code) {
        return dao.findByCode(code) != null;
    }

    public Customer findById(Integer id) {
        return dao.findById(id);
    }
}
