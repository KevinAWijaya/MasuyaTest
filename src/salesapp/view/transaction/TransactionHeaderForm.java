package salesapp.view.transaction;

import salesapp.controller.TransactionHeaderController;
import salesapp.model.Customer;
import salesapp.model.TransactionHeader;
import salesapp.repository.TransactionStatus;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;
import salesapp.controller.CustomerListController;

public final class TransactionHeaderForm extends JFrame {

    private JComboBox<Customer> cmbCustomer;
    private JTextField txtInvoiceNumber;
    private JSpinner spnDate;
    private JButton btnSave;

    private final CustomerListController customerController;
    private final TransactionHeaderController headerController;

    private final TransactionHeaderListView detailView;

    private TransactionHeader transactionHeader;
    private boolean isEditMode = false;
    private boolean isReadOnly = false;

    public TransactionHeaderForm(TransactionHeaderListView detailView) {
        this.detailView = detailView; 
        customerController = new CustomerListController();
        headerController = new TransactionHeaderController();
        
        createScreen();
        initComponent();

        // Default: jika tambah transaksi baru
        if (!isEditMode) {
            generateInvoiceNumber();
        }

        setVisible(true);
    }

    private void createScreen() {

        setTitle("Transaction Header Form");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initComponent() {

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtInvoiceNumber = new JTextField();
        txtInvoiceNumber.setEditable(false); // tidak bisa diedit manual

        cmbCustomer = new JComboBox<>();
        loadCustomers();

        spnDate = new JSpinner(new SpinnerDateModel());
        spnDate.setEditor(new JSpinner.DateEditor(spnDate, "yyyy-MM-dd"));

        btnSave = new JButton("Save");
        btnSave.addActionListener(e -> saveTransaction());

        panel.add(new JLabel("Invoice Number:"));
        panel.add(txtInvoiceNumber);

        panel.add(new JLabel("Customer:"));
        panel.add(cmbCustomer);

        panel.add(new JLabel("Date:"));
        panel.add(spnDate);

        panel.add(new JLabel());
        panel.add(btnSave);

        add(panel, BorderLayout.CENTER);
    }

    private void loadCustomers() {
        List<Customer> customers = customerController.getAllCustomers();
        DefaultComboBoxModel<Customer> model = new DefaultComboBoxModel<>();
        customers.forEach((c) -> {
            model.addElement(c);
        });
        cmbCustomer.setModel(model);
    }

    private void generateInvoiceNumber() {
        if (isEditMode) {
            return;
        }

        Date date = (Date) spnDate.getValue();
        String invoiceNo = headerController.generateInvoiceNumber(date);
        txtInvoiceNumber.setText(invoiceNo);
    }

    private void saveTransaction() {
        if (isReadOnly) {
            JOptionPane.showMessageDialog(this, "Transaksi ini sudah VOID dan tidak bisa diubah.");
            return;
        }
        Customer selected = (Customer) cmbCustomer.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Customer harus dipilih!");
            return;
        }

        try {
            if (transactionHeader == null) {
                transactionHeader = new TransactionHeader();
            }

            transactionHeader.setCustomerID(selected.getId());
            transactionHeader.setInvoiceDate((Date) spnDate.getValue());

            if (!isEditMode) {
                generateInvoiceNumber();
                transactionHeader.setInvoiceNumber(txtInvoiceNumber.getText());
                transactionHeader.setTotal(0.0);
                transactionHeader.setStatus(TransactionStatus.DRAFT.getValue());
                boolean success = headerController.saveTransactionHeader(transactionHeader);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Header transaksi berhasil disimpan.");
                    detailView.loadData();
                    dispose(); // tutup form
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi.");
                }
            } else {
                transactionHeader.setInvoiceNumber(txtInvoiceNumber.getText());
                boolean success = headerController.updateTransactionHeader(transactionHeader);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Header transaksi berhasil diperbarui.");
                    detailView.loadData();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal memperbarui transaksi.");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public void setTransactionHeader(TransactionHeader header) {
        this.transactionHeader = header;
        this.isEditMode = true;

        txtInvoiceNumber.setText(header.getInvoiceNumber());
        spnDate.setValue(header.getInvoiceDate());
        spnDate.setEnabled(false);
        ((JSpinner.DefaultEditor) spnDate.getEditor()).getTextField().setEditable(false);

        // Set selected customer
        for (int i = 0; i < cmbCustomer.getItemCount(); i++) {
            Customer c = cmbCustomer.getItemAt(i);
            if (c.getId() == header.getCustomerID()) {
                cmbCustomer.setSelectedIndex(i);
                break;
            }
        }

        //  Jika status VOID → readonly mode
        if (TransactionStatus.VOID.getValue().equalsIgnoreCase(header.getStatus())) {
            isReadOnly = true;
            cmbCustomer.setEnabled(false);
            btnSave.setEnabled(false);
            setTitle("View Transaction (VOID)");
        } else {
            setTitle("Edit Transaction");
        }
    }

}
