package salesapp.view.transaction;

import salesapp.controller.TransactionLineController;
import salesapp.controller.CustomerFormController;
import salesapp.model.TransactionHeader;
import salesapp.model.TransactionLine;
import salesapp.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import salesapp.controller.ProductFormController;
import salesapp.controller.TransactionHeaderController;
import salesapp.repository.TransactionStatus;
import salesapp.utils.IconUtils;
import salesapp.view.transaction.line.TransactionLineForm;

public class TransactionHeaderDetailView extends JFrame {

    private final TransactionHeader header;
    private final TransactionLineController lineController = new TransactionLineController();
    private final CustomerFormController customerController = new CustomerFormController();
    private final ProductFormController productController = new ProductFormController();

    private DefaultTableModel tableModel;
    private JTable table;

    public TransactionHeaderDetailView(TransactionHeader header) {
        this.header = header;

        setTitle("Transaction Detail - " + header.getInvoiceNumber());
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildHeaderInfoPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        loadTransactionLines();

        setVisible(true);
    }

    private JPanel buildHeaderInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        Customer customer = customerController.getCustomerById(header.getCustomerID());

        panel.add(new JLabel("Invoice Number: " + header.getInvoiceNumber()));
        panel.add(new JLabel("Date: " + header.getInvoiceDate()));
        panel.add(new JLabel("Customer: " + (customer != null ? customer.getName() : "Unknown")));

        panel.add(new JLabel("Total: Rp " + header.getTotal()));
        panel.add(new JLabel("Status: " + header.getStatus()));
        panel.add(new JLabel(" ")); // spacer

        return panel;
    }

    private JScrollPane buildTablePanel() {
        String[] columns = {"Product Name", "Quantity", "Price", "Disc1", "Disc2", "Disc3", "Amount", "Net Price", "Actions"};

        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox()));
        table.setRowHeight(30);
        return new JScrollPane(table);
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnAdd = new JButton("Add Item");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnExit = new JButton("Exit");
        JButton btnSubmit = new JButton("Submit Transaction");

        btnSubmit.addActionListener(e -> onSubmitTransaction());
        btnAdd.addActionListener(e -> onAddItem());
        btnRefresh.addActionListener(e -> loadTransactionLines());
        btnExit.addActionListener(e -> dispose());

        // Disable tombol jika transaksi sudah submited/void
        if (!TransactionStatus.DRAFT.getValue().equalsIgnoreCase(header.getStatus())) {
            btnSubmit.setEnabled(false);
            btnAdd.setEnabled(false);
        }

        panel.add(btnSubmit);
        panel.add(btnAdd);
        panel.add(btnRefresh);
        panel.add(btnExit);

        return panel;
    }

    class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {

        JButton editButton = new JButton(IconUtils.getIcon("edit.png", 10, 10));
        JButton deleteButton = new JButton(IconUtils.getIcon("delete.png", 10, 10));

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 0));
            add(editButton);
            add(deleteButton);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {

        private final JPanel panel = new JPanel();

        JButton editButton = new JButton(IconUtils.getIcon("edit.png", 10, 10));
        JButton deleteButton = new JButton(IconUtils.getIcon("delete.png", 10, 10));

        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 0));
            panel.add(editButton);
            panel.add(deleteButton);

            editButton.addActionListener(e -> handleEdit());
            deleteButton.addActionListener(e -> handleDelete());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.selectedRow = row;
            return panel;
        }

        private void handleEdit() {
            int transactionLineId = lineController
                    .getTransactionLinesByTransactionID(header.getTransactionID())
                    .get(selectedRow).getTransactionLineID();

            TransactionLine selectedLine = lineController
                    .getTransactionLinesByTransactionID(header.getTransactionID())
                    .stream()
                    .filter(line -> line.getTransactionLineID() == transactionLineId)
                    .findFirst()
                    .orElse(null);

            if (selectedLine != null) {
                TransactionLineForm form = new TransactionLineForm(header.getTransactionID(), TransactionHeaderDetailView.this);
                form.setEditMode(selectedLine);
                form.setVisible(true);
            }

            fireEditingStopped();
        }

        private void handleDelete() {
            int confirm = JOptionPane.showConfirmDialog(null, "Yakin ingin menghapus item ini?");
            if (confirm == JOptionPane.YES_OPTION) {
                int transactionLineId = lineController
                        .getTransactionLinesByTransactionID(header.getTransactionID())
                        .get(selectedRow).getTransactionLineID();

                boolean success = lineController.deleteTransactionLineById(transactionLineId);
                if (success) {
                    JOptionPane.showMessageDialog(null, "Item berhasil dihapus.");
                    loadTransactionLines();
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal menghapus item.");
                }
            }
            fireEditingStopped();
        }

        @Override
        public Object getCellEditorValue() {
            return "Edit/Delete";
        }
    }

    public void loadTransactionLines() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        List<TransactionLine> lines = lineController.getTransactionLinesByTransactionID(header.getTransactionID());
        tableModel.setRowCount(0);

        for (TransactionLine line : lines) {
            String productName = productController.getProductById(line.getProductID()).getName(); // ambil nama produk
            tableModel.addRow(new Object[]{
                productName,
                line.getQuantity(),
                line.getPrice(),
                line.getDisc1(),
                line.getDisc2(),
                line.getDisc3(),
                line.getAmount(),
                line.getNetPrice(),
                "Edit/Delete"
            });
        }
    }

    private void onSubmitTransaction() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin submit transaksi ini? Setelah submit tidak bisa diubah.",
                "Konfirmasi Submit", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            List<TransactionLine> lines = lineController.getTransactionLinesByTransactionID(header.getTransactionID());

            double totalNet = 0;
            for (TransactionLine line : lines) {
                totalNet += line.getNetPrice();
            }

            TransactionHeaderController headerController = new TransactionHeaderController();
            boolean success = headerController.updateTotalAndStatus(header.getTransactionID(), totalNet, "submitted");

            if (success) {
                // KURANGI STOK
                boolean stockUpdateSuccess = true;
                for (TransactionLine line : lines) {
                    boolean updated = productController.reduceStock(line.getProductID(), line.getQuantity());
                    if (!updated) {
                        stockUpdateSuccess = false;
                        break;
                    }
                }

                if (!stockUpdateSuccess) {
                    JOptionPane.showMessageDialog(this, "Transaksi disubmit, tapi update stok gagal. Harap cek manual.");
                } else {
                    JOptionPane.showMessageDialog(this, "Transaksi berhasil disubmit dan stok telah dikurangi.");
                }

                header.setStatus("submitted");
                header.setTotal(totalNet);

                dispose();
                new TransactionHeaderDetailView(header); // reload
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan status transaksi.");
            }
        }
    }

    private void onAddItem() {
        TransactionLineForm transactionLineForm = new TransactionLineForm(header.getTransactionID(), this);
        transactionLineForm.setVisible(true);
    }

}
