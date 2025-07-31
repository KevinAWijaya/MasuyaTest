package salesapp.view.customer;

import salesapp.controller.CustomerFormController;
import salesapp.model.Customer;

import javax.swing.*;
import java.awt.*;

public class CustomerForm extends JFrame {

    private JTextField tfCode, tfName, tfAddress, tfProvince, tfCity, tfDistrict, tfSubdistrict, tfPostal;
    private JButton btnSave;
    private final CustomerFormController controller = new CustomerFormController();

    private boolean isEditMode = false;

    public CustomerForm() {
        initScreen();
        initComponents();
    }

    private void initScreen() {
        setTitle("Customer Input Form");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

    private void initComponents() {
        setLayout(new GridLayout(10, 2, 5, 5));

        add(new JLabel("Code:"));
        tfCode = new JTextField();
        add(tfCode);

        add(new JLabel("Name:"));
        tfName = new JTextField();
        add(tfName);

        add(new JLabel("Address:"));
        tfAddress = new JTextField();
        add(tfAddress);

        add(new JLabel("Province:"));
        tfProvince = new JTextField();
        add(tfProvince);

        add(new JLabel("City:"));
        tfCity = new JTextField();
        add(tfCity);

        add(new JLabel("District:"));
        tfDistrict = new JTextField();
        add(tfDistrict);

        add(new JLabel("Subdistrict:"));
        tfSubdistrict = new JTextField();
        add(tfSubdistrict);

        add(new JLabel("Postal Code:"));
        tfPostal = new JTextField();
        add(tfPostal);

        add(new JLabel());
        btnSave = new JButton("Save Customer");
        btnSave.addActionListener(e -> onSave());
        add(btnSave);
    }

    private void onSave() {
        Customer customer = controller.buildCustomerFromForm(
                tfCode.getText(),
                tfName.getText(),
                tfAddress.getText(),
                tfProvince.getText(),
                tfCity.getText(),
                tfDistrict.getText(),
                tfSubdistrict.getText(),
                tfPostal.getText()
        );

        if (isEditMode) {
            controller.updateCustomer(customer, () -> {
                dispose();
            });
        } else {
            controller.saveCustomer(customer, this::clearForm);
        }
    }

    private void clearForm() {
        tfCode.setText("");
        tfName.setText("");
        tfAddress.setText("");
        tfProvince.setText("");
        tfCity.setText("");
        tfDistrict.setText("");
        tfSubdistrict.setText("");
        tfPostal.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerForm().setVisible(true));
    }

    public void setCustomer(Customer customer) {
        tfCode.setText(customer.getCode());
        tfName.setText(customer.getName());
        tfAddress.setText(customer.getAddress());
        tfProvince.setText(customer.getProvince());
        tfCity.setText(customer.getCity());
        tfDistrict.setText(customer.getDistrict());
        tfSubdistrict.setText(customer.getSubdistrict());
        tfPostal.setText(customer.getPostalCode());

        tfCode.setEnabled(false);

        isEditMode = true;
        btnSave.setText("Update Customer");
    }
}
