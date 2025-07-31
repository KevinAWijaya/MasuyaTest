package salesapp.view.transaction.line;

import salesapp.controller.ProductFormController;
import salesapp.controller.TransactionLineController;
import salesapp.model.Product;
import salesapp.model.TransactionLine;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import salesapp.controller.ProductListController;
import salesapp.view.transaction.TransactionHeaderDetailView;

public class TransactionLineForm extends JFrame {

    private final int transactionId;
    private final TransactionHeaderDetailView detailView;

    private JComboBox<Product> cmbProduct;
    private JTextField txtQuantity, txtPrice, txtDisc1, txtDisc2, txtDisc3, txtNetPrice, txtAmount;
    private JLabel lblStock;
    private JButton btnSave;

    private final ProductListController productController = new ProductListController();
    private final TransactionLineController lineController = new TransactionLineController();

    private TransactionLine editingLine;
    private boolean isEditMode = false;

    public TransactionLineForm(int transactionId, TransactionHeaderDetailView detailView) {
        this.transactionId = transactionId;
        this.detailView = detailView;

        setTitle("Add Transaction Line");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblStock = new JLabel("Stok: 0");
        panel.add(new JLabel("Stock:"));
        panel.add(lblStock);

        cmbProduct = new JComboBox<>();
        txtQuantity = new JTextField("1");
        txtPrice = new JTextField();
        txtDisc1 = new JTextField("0");
        txtDisc2 = new JTextField("0");
        txtDisc3 = new JTextField("0");
        txtNetPrice = new JTextField();
        txtAmount = new JTextField();

        txtNetPrice.setEditable(false);
        txtAmount.setEditable(false);

        loadProducts();

        // Event listeners
        cmbProduct.addActionListener(e -> updateProductPrice());
        txtQuantity.addCaretListener(e -> updateNetAndAmount());
        txtDisc1.addCaretListener(e -> updateNetAndAmount());
        txtDisc2.addCaretListener(e -> updateNetAndAmount());
        txtDisc3.addCaretListener(e -> updateNetAndAmount());

        panel.add(new JLabel("Product:"));
        panel.add(cmbProduct);
        panel.add(new JLabel("Quantity:"));
        panel.add(txtQuantity);
        panel.add(new JLabel("Price:"));
        panel.add(txtPrice);
        panel.add(new JLabel("Disc1 (%):"));
        panel.add(txtDisc1);
        panel.add(new JLabel("Disc2 (%):"));
        panel.add(txtDisc2);
        panel.add(new JLabel("Disc3 (%):"));
        panel.add(txtDisc3);
        panel.add(new JLabel("Amount:"));
        panel.add(txtAmount);
        panel.add(new JLabel("Net Price:"));
        panel.add(txtNetPrice);

        btnSave = new JButton("Save");
        btnSave.addActionListener(e -> onSave());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnSave);

        add(panel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        updateProductPrice(); // inisialisasi awal

        setVisible(true);
    }

    private void loadProducts() {
        List<Product> products = productController.getAllProducts();
        DefaultComboBoxModel<Product> model = new DefaultComboBoxModel<>();
        for (Product p : products) {
            model.addElement(p);
        }
        cmbProduct.setModel(model);
    }

    private void updateProductPrice() {
        if (isEditMode) {
            return; // hindari overwrite saat edit
        }

        Product selected = (Product) cmbProduct.getSelectedItem();
        if (selected != null) {
            txtPrice.setText(String.valueOf(selected.getPrice()));

            int remainingStock = lineController.getRemainingStock(transactionId, selected, null);
            lblStock.setText("Stok Tersisa: " + remainingStock);
            updateNetAndAmount();
        }
    }

    private void updateNetAndAmount() {
        try {
            int quantity = Integer.parseInt(txtQuantity.getText());
            double price = Double.parseDouble(txtPrice.getText());
            double disc1 = Double.parseDouble(txtDisc1.getText());
            double disc2 = Double.parseDouble(txtDisc2.getText());
            double disc3 = Double.parseDouble(txtDisc3.getText());

            double amount = price * quantity; // total sebelum diskon
            double netPrice = price * quantity;
            netPrice -= netPrice * disc1 / 100;
            netPrice -= netPrice * disc2 / 100;
            netPrice -= netPrice * disc3 / 100;

            txtAmount.setText(String.format("%.2f", amount));
            txtNetPrice.setText(String.format("%.2f", netPrice));

        } catch (NumberFormatException e) {
            txtNetPrice.setText("");
            txtAmount.setText("");
        }
    }

    private void onSave() {
        try {
            Product selected = (Product) cmbProduct.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Product harus dipilih.");
                return;
            }

            int quantity = Integer.parseInt(txtQuantity.getText());

            int remainingStock = lineController.getRemainingStock(transactionId, selected, editingLine);
            if (quantity > remainingStock) {
                JOptionPane.showMessageDialog(this, "Quantity melebihi stok tersisa (" + remainingStock + ").");
                return;
            }

            TransactionLine line = new TransactionLine();
            if (isEditMode && editingLine != null) {
                line.setTransactionLineID(editingLine.getTransactionLineID());
            }

            line.setTransactionID(transactionId);
            line.setProductID(selected.getId());
            line.setQuantity(quantity);
            line.setPrice(Double.parseDouble(txtPrice.getText()));
            line.setDisc1(Double.parseDouble(txtDisc1.getText()));
            line.setDisc2(Double.parseDouble(txtDisc2.getText()));
            line.setDisc3(Double.parseDouble(txtDisc3.getText()));
            line.setNetPrice(Double.parseDouble(txtNetPrice.getText()));
            line.setAmount(Double.parseDouble(txtAmount.getText()));

            boolean success = lineController.saveTransactionLine(line);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        isEditMode ? "Item berhasil diperbarui." : "Item berhasil ditambahkan.");
                detailView.loadTransactionLines();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan item.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Input tidak valid: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }

    public void setEditMode(TransactionLine line) {
        this.editingLine = line;
        this.isEditMode = true;

        // Set selected product
        for (int i = 0; i < cmbProduct.getItemCount(); i++) {
            Product p = cmbProduct.getItemAt(i);
            if (p.getId() == line.getProductID()) {
                cmbProduct.setSelectedIndex(i);
                break;
            }
        }

        cmbProduct.setEnabled(false);

        txtQuantity.setText(String.valueOf(line.getQuantity()));
        txtPrice.setText(String.valueOf(line.getPrice()));
        txtDisc1.setText(String.valueOf(line.getDisc1()));
        txtDisc2.setText(String.valueOf(line.getDisc2()));
        txtDisc3.setText(String.valueOf(line.getDisc3()));

        // Hitung ulang net dan amount
        updateNetAndAmount();

        btnSave.setText("Update Item");
        setTitle("Edit Transaction Line");
    }
}
