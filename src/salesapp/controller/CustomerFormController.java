package salesapp.controller;

import java.util.List;
import salesapp.model.Customer;
import salesapp.repository.CustomerRepository;

import javax.swing.*;
import salesapp.repository.AddIntoDatabaseResult;
import salesapp.utils.dialog.LoadingUtils;

public class CustomerFormController {

    private final CustomerRepository repository;

    public CustomerFormController() {
        repository = new CustomerRepository();
    }

    public Customer getCustomerByCode(String code) {
        return repository.getByCode(code);
    }

    public Customer getCustomerById(int id) {
        return repository.findById(id);
    }

    public void saveCustomer(Customer customer, Runnable onSuccessClearForm) {
        JDialog[] loadingDialog = new JDialog[1];

        SwingUtilities.invokeLater(() -> {
            loadingDialog[0] = LoadingUtils.showLoadingDialog("Saving customer...");

            new Thread(() -> {
                try {
                    AddIntoDatabaseResult result = repository.addCustomer(customer);

                    SwingUtilities.invokeLater(() -> {
                        LoadingUtils.closeDialog(loadingDialog[0]);

                        switch (result) {
                            case SUCCESS:
                                showSuccess("Customer saved successfully!");
                                onSuccessClearForm.run();
                                break;
                            case DUPLICATE:
                                showError("Customer code already exists!");
                                break;
                            case INVALID:
                                showError("Code must be filled in and alphanumeric only.");
                                break;
                            case FAILED:
                            default:
                                showError("Failed to save customer.");
                                break;
                        }
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        LoadingUtils.closeDialog(loadingDialog[0]);
                        showError("Unexpected error: " + e.getMessage());
                    });
                    e.printStackTrace();
                }
            }).start();
        });
    }

    public void updateCustomer(Customer customer, Runnable onSuccess) {
        if (customer.getCode().isEmpty() || customer.getName().isEmpty()) {
            showError("Code and name are required!");
            return;
        }

        JDialog[] loadingDialog = new JDialog[1];

        SwingUtilities.invokeLater(() -> {
            loadingDialog[0] = LoadingUtils.showLoadingDialog("Updating customer...");

            new Thread(() -> {
                try {
                    boolean success = repository.updateCustomer(customer);

                    SwingUtilities.invokeLater(() -> {
                        LoadingUtils.closeDialog(loadingDialog[0]);

                        if (success) {
                            showSuccess("Customer updated successfully!");
                            onSuccess.run();
                        } else {
                            showError("Failed to update customer.");
                        }
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        LoadingUtils.closeDialog(loadingDialog[0]);
                        showError("Unexpected error: " + e.getMessage());
                    });
                    e.printStackTrace();
                }
            }).start();
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public Customer buildCustomerFromForm(
            String code, String name, String address,
            String province, String city, String district,
            String subdistrict, String postal
    ) {
        Customer customer = new Customer();
        customer.setCode(code.trim());
        customer.setName(name.trim());
        customer.setAddress(address.trim());
        customer.setProvince(province.trim());
        customer.setCity(city.trim());
        customer.setDistrict(district.trim());
        customer.setSubdistrict(subdistrict.trim());
        customer.setPostalCode(postal.trim());
        return customer;
    }
}
