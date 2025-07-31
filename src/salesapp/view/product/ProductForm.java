package salesapp.view.product;

import salesapp.controller.ProductFormController;
import salesapp.model.Product;

import javax.swing.*;
import java.awt.*;

public class ProductForm extends JFrame {

    private JTextField tfCode, tfName, tfPrice, tfStock;
    private JButton btnSave;
    private final ProductFormController controller = new ProductFormController();

    private boolean isEditMode = false;
    private int productId = 0;

    public ProductForm() {
        createScreen();
        initComponents();
    }

    private void createScreen() {
        setTitle("Product Form");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

    private void initComponents() {
        setLayout(new GridLayout(5, 2, 5, 5));

        add(new JLabel("Code:"));
        tfCode = new JTextField();
        add(tfCode);

        add(new JLabel("Name:"));
        tfName = new JTextField();
        add(tfName);

        add(new JLabel("Price:"));
        tfPrice = new JTextField();
        add(tfPrice);

        add(new JLabel("Stock:"));
        tfStock = new JTextField();
        add(tfStock);

        add(new JLabel());
        btnSave = new JButton("Save Product");
        btnSave.addActionListener(e -> onSave());
        add(btnSave);
    }

    private void onSave() {
        try {
            String code = tfCode.getText().trim();
            String name = tfName.getText().trim();
            double price = Double.parseDouble(tfPrice.getText().trim());
            int stock = Integer.parseInt(tfStock.getText().trim());

            Product product = new Product(productId, code, name, price, stock);

            if (isEditMode) {
                controller.updateProduct(product, this::dispose);
            } else {
                controller.saveProduct(product, this::clearForm);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price and stock must be numbers.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearForm() {
        tfCode.setText("");
        tfName.setText("");
        tfPrice.setText("");
        tfStock.setText("");
    }

    public void setProduct(Product product) {
        this.productId = product.getId();
        tfCode.setText(product.getCode());
        tfName.setText(product.getName());
        tfPrice.setText(String.valueOf(product.getPrice()));
        tfStock.setText(String.valueOf(product.getStock()));

        tfCode.setEnabled(false);
        isEditMode = true;
        btnSave.setText("Update Product");
    }
}
