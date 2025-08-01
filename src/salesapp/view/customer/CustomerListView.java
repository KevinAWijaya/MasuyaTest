package salesapp.view.customer;

import salesapp.model.Customer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import salesapp.controller.CustomerListController;
import salesapp.utils.IconUtils;

public class CustomerListView extends JFrame {

    private final CustomerListController controller = new CustomerListController();
    private JTable table;
    private DefaultTableModel tableModel;

    public CustomerListView() {
        createScreen();
        initComponents();
        loadCustomerData();
    }

    private void createScreen() {
        setTitle("Customer List");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        setupTable();
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = createButtonPanel();

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);
    }

    private void setupTable() {
        String[] columns = {
            "Code", "Name", "Address", "Province",
            "City", "District", "Subdistrict", "Postal Code", "Actions"
        };

        tableModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Only the "Actions" column is editable
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);

        // Set custom renderer and editor for the "Actions" column
        TableColumn actionColumn = table.getColumnModel().getColumn(8);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add Customer");
        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");

        addButton.addActionListener(e -> onAddCustomer());
        refreshButton.addActionListener(e -> loadCustomerData());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    private void onAddCustomer() {
        CustomerForm form = new CustomerForm(this);
        form.setVisible(true);
    }

    private CustomerListView getCurrentForm() {
        return this;
    }

    public void loadCustomerData() {
        List<Customer> customers = controller.getAllCustomers();
        tableModel.setRowCount(0);

        customers.forEach((c) -> {
            tableModel.addRow(new Object[]{
                c.getCode(),
                c.getName(),
                c.getAddress(),
                c.getProvince(),
                c.getCity(),
                c.getDistrict(),
                c.getSubdistrict(),
                c.getPostalCode(),
                "Edit/Delete"
            });
        });
    }

    // ========== Action Button renderer ========== //
    class ButtonRenderer extends JPanel implements TableCellRenderer {

        JButton editButton = new JButton(IconUtils.getIcon("edit.png", 10, 10));
        JButton deleteButton = new JButton(IconUtils.getIcon("delete.png", 10, 10));

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
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

    // ========== Action Button editor ========== //
    class ButtonEditor extends DefaultCellEditor {

        protected final JPanel panel = new JPanel();

        JButton editButton = new JButton(IconUtils.getIcon("edit.png", 10, 10));
        JButton deleteButton = new JButton(IconUtils.getIcon("delete.png", 10, 10));

        private String currentCode;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.add(editButton);
            panel.add(deleteButton);

            editButton.addActionListener(this::handleEdit);
            deleteButton.addActionListener(this::handleDelete);
        }

        private void handleEdit(ActionEvent e) {
            if (currentCode != null) {
                Customer customer = controller.getCustomerByCode(currentCode);
                if (customer != null) {
                    CustomerForm form = new CustomerForm(getCurrentForm());
                    form.setCustomer(customer);
                    form.setVisible(true);
                }
            }
        }

        private void handleDelete(ActionEvent e) {
            if (currentCode != null) {
                int confirm = JOptionPane.showConfirmDialog(
                        CustomerListView.this,
                        "Are you sure you want to delete this customer?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        controller.deleteCustomer(currentCode);

                        // Stop editing to avoid error
                        if (table.isEditing()) {
                            table.getCellEditor().stopCellEditing();
                        }

                        loadCustomerData();
                    } catch (RuntimeException ex) {
                        JOptionPane.showMessageDialog(
                                CustomerListView.this,
                                ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentCode = (String) table.getValueAt(row, 0); // ambil CustomerCode
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Edit/Delete";
        }
    }
}
