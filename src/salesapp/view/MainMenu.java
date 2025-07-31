package salesapp.view;

import javax.swing.*;
import java.awt.*;
import salesapp.view.customer.CustomerListView;
import salesapp.view.product.ProductListView; 
import salesapp.view.transaction.TransactionHeaderListView;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("SalesApp - Main Menu");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));

        JButton btnCustomer = new JButton("Customer Master");
        JButton btnProduct = new JButton("Product Master");
        JButton btnTransaction = new JButton("Transaction");
        JButton btnExit = new JButton("Exit");

        btnCustomer.addActionListener(e -> new CustomerListView().setVisible(true));
        btnProduct.addActionListener(e -> new ProductListView().setVisible(true));
        btnTransaction.addActionListener(e -> new TransactionHeaderListView().setVisible(true));
        btnExit.addActionListener(e -> System.exit(0));

        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        panel.add(btnCustomer);
        panel.add(btnProduct);
        panel.add(btnTransaction);
        panel.add(btnExit);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainMenu().setVisible(true);
        });
    }
}
