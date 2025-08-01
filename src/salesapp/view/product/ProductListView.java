package salesapp.view.product;
 
import salesapp.model.Product;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import salesapp.controller.ProductListController;
import salesapp.utils.IconUtils;

public final class ProductListView extends JFrame {

    private final ProductListController controller = new ProductListController();

    private JTable table;
    private DefaultTableModel tableModel; 

    public ProductListView() {
        createScreen();
        initComponents();
        loadProductData();
    }

    private void createScreen() {
        setTitle("Product List");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getContentPane().add(panel);

        // Table
        String[] columns = {"Code", "Name", "Price", "Stock", "Actions"};
        tableModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(40);

        TableColumn actionColumn = table.getColumnModel().getColumn(4);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons at bottom
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add Product");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnExit = new JButton("Exit");

        btnAdd.addActionListener(e -> onAddProduct());
        btnRefresh.addActionListener(e -> loadProductData());
        btnExit.addActionListener(e -> dispose());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnExit);

        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadProductData() {
        tableModel.setRowCount(0); // clear existing data
        List<Product> products = controller.getAllProducts();

        products.forEach((p) -> {
            tableModel.addRow(new Object[]{
                p.getCode(),
                p.getName(),
                p.getPrice(),
                p.getStock(),
                "Edit/Delete"
            });
        });
    }

    private void onAddProduct() {
        ProductForm form = new ProductForm(this);
        form.setVisible(true);
    }
    
    private ProductListView getCurrentForm (){
        return this;
    }

    // ==== Renderer for Action Buttons ====
    class ButtonRenderer extends JPanel implements TableCellRenderer {

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

    // ==== Editor for Action Buttons ====
    class ButtonEditor extends DefaultCellEditor {

        protected final JPanel panel = new JPanel();

        JButton editButton = new JButton(IconUtils.getIcon("edit.png", 10, 10));
        JButton deleteButton = new JButton(IconUtils.getIcon("delete.png", 10, 10));

        private String currentCode;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 0));
            panel.add(editButton);
            panel.add(deleteButton);

            editButton.addActionListener(this::handleEdit);
            deleteButton.addActionListener(this::handleDelete);
        }

        private void handleEdit(ActionEvent e) {
            if (currentCode != null) {
                Product product = controller.getProductByCode(currentCode);
                if (product != null) {
                    ProductForm form = new ProductForm(getCurrentForm());
                    form.setProduct(product);
                    form.setVisible(true);
                }
            }
        }

        private void handleDelete(ActionEvent e) {
            if (currentCode != null) {
                int confirm = JOptionPane.showConfirmDialog(
                        ProductListView.this,
                        "Are you sure you want to delete this product?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        controller.deleteProduct(currentCode);

                        if (table.isEditing()) {
                            table.getCellEditor().stopCellEditing();
                        }

                        loadProductData();
                    } catch (RuntimeException ex) {
                        JOptionPane.showMessageDialog(
                                ProductListView.this,
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
            currentCode = (String) table.getValueAt(row, 0); // kolom 1 = code
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Edit/Delete";
        }
    }
}
