package salesapp.view.transaction;

import salesapp.controller.TransactionHeaderController;
import salesapp.model.TransactionHeader;
import salesapp.repository.TransactionStatus;
import salesapp.utils.IconUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class TransactionHeaderListView extends JFrame {

    private final TransactionHeaderController controller = new TransactionHeaderController();
    private JTable table;
    private DefaultTableModel tableModel;

    private List<TransactionHeader> headerList;

    public TransactionHeaderListView() {
        setTitle("Transaction List");
        setSize(800, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getContentPane().add(panel);

        String[] columns = {"Invoice No", "Customer ID", "Date", "Total", "Status", "Actions"};
        tableModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);

        TableColumn actionColumn = table.getColumnModel().getColumn(5);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add Transaction");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnExit = new JButton("Exit");

        btnAdd.addActionListener(e -> onAddTransaction());
        btnRefresh.addActionListener(e -> loadData());
        btnExit.addActionListener(e -> dispose());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnExit);

        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        headerList = controller.getAllTransactionHeaders(); // perbarui data

        for (TransactionHeader header : headerList) {
            tableModel.addRow(new Object[]{
                header.getInvoiceNumber(),
                header.getCustomerID(),
                header.getInvoiceDate(),
                header.getTotal(),
                header.getStatus(),
                "Actions"
            });
        }

        // Refresh UI
        SwingUtilities.invokeLater(() -> {
            // reset editor dan renderer
            TableColumn actionColumn = table.getColumnModel().getColumn(5);
            actionColumn.setCellRenderer(new ButtonRenderer());
            actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

            // force repaint
            table.revalidate();
            table.repaint();
        });
    }

    private void onAddTransaction() {
        TransactionHeaderForm form = new TransactionHeaderForm();
        form.setVisible(true);
    }

    // ==== Renderer ====
    class ButtonRenderer extends JPanel implements TableCellRenderer {

        JButton viewButton = new JButton(IconUtils.getIcon("view.png", 10, 10));
        JButton editButton = new JButton(IconUtils.getIcon("edit.png", 10, 10));
        JButton deleteButton = new JButton(IconUtils.getIcon("delete.png", 10, 10));

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 0));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            TransactionHeader header = headerList.get(row);
            removeAll();
            add(viewButton);

            if (!TransactionStatus.VOID.getValue().equalsIgnoreCase(header.getStatus())) {
                add(editButton);
                add(deleteButton);
            }

            return this;
        }
    }

    // ==== Editor ====
    class ButtonEditor extends DefaultCellEditor {

        protected final JPanel panel = new JPanel();

        JButton viewButton = new JButton(IconUtils.getIcon("view.png", 10, 10));
        JButton editButton = new JButton(IconUtils.getIcon("edit.png", 10, 10));
        JButton deleteButton = new JButton(IconUtils.getIcon("delete.png", 10, 10));

        private TransactionHeader currentHeader;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 0));
            panel.add(viewButton);
            panel.add(editButton);
            panel.add(deleteButton);

            viewButton.addActionListener(this::handleView);
            editButton.addActionListener(this::handleEdit);
            deleteButton.addActionListener(this::handleDelete);
        }

        private void handleView(ActionEvent e) {
            if (currentHeader != null) {
                TransactionHeaderDetailView transactionHeaderDetailView = new TransactionHeaderDetailView(currentHeader);
                transactionHeaderDetailView.setVisible(true);
            }
        }

        private void handleEdit(ActionEvent e) {
            if (currentHeader != null) {
                if (TransactionStatus.VOID.getValue().equalsIgnoreCase(currentHeader.getStatus())) {
                    JOptionPane.showMessageDialog(null, "Transaksi sudah di-VOID, tidak bisa diedit.");
                    return;
                }

                if (TransactionStatus.DRAFT.getValue().equalsIgnoreCase(currentHeader.getStatus())) {
                    TransactionHeaderForm form = new TransactionHeaderForm();
                    form.setTransactionHeader(currentHeader);
                    form.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Hanya transaksi draft yang bisa diedit.");
                }
            }
        }

        private void handleDelete(ActionEvent e) {
            if (currentHeader != null) {
                int confirm = JOptionPane.showConfirmDialog(
                        TransactionHeaderListView.this,
                        "Yakin ingin VOID transaksi ini?",
                        "Konfirmasi",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = controller.updateTotalAndStatus(
                            currentHeader.getTransactionID(),
                            currentHeader.getTotal(),
                            TransactionStatus.VOID.getValue()
                    );

                    if (success) {
                        JOptionPane.showMessageDialog(null, "Transaksi berhasil di-VOID.");
                        // Hentikan editor aktif
                        if (table.isEditing()) {
                            table.getCellEditor().stopCellEditing();
                        }

                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(null, "Gagal melakukan VOID transaksi.");
                    }
                }
            }

        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentHeader = headerList.get(row);
            panel.removeAll();
            panel.add(viewButton);

            if (!TransactionStatus.VOID.getValue().equalsIgnoreCase(currentHeader.getStatus())) {
                panel.add(editButton);
                panel.add(deleteButton);
            }

            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }
}
