package cafemanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame implements ActionListener {

    JCheckBox[] itemCheckboxes;
    JLabel[] itemLabels;
    JLabel[] priceLabels;
    JTextField[] quantityFields;
    JButton orderButton, cancelButton;

    String[] items = {"Coffee", "Burger", "Sandwich", "Pizza", "Juice"};
    double[] prices = {80.0, 120.0, 100.0, 250.0, 60.0};

    public Login() {
        setTitle("Cafe Order Page");
        setSize(850, 700);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 250));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBounds(0, 0, 850, 80);
        headerPanel.setLayout(null);
        add(headerPanel);

        JLabel l1 = new JLabel("CAFE ORDER FORM", SwingConstants.CENTER);
        l1.setFont(new Font("Arial", Font.BOLD, 28));
        l1.setForeground(Color.WHITE);
        l1.setBounds(0, 20, 850, 40);
        headerPanel.add(l1);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBounds(50, 100, 750, 500);
        contentPanel.setLayout(null);
        contentPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        add(contentPanel);

        JLabel selectLabel = new JLabel("Select Items:");
        selectLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectLabel.setBounds(30, 20, 200, 30);
        contentPanel.add(selectLabel);

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new GridLayout(items.length + 1, 4, 10, 10));
        itemsPanel.setBounds(30, 60, 680, 300);
        itemsPanel.setBackground(new Color(245, 245, 245));
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Menu"));

        itemsPanel.add(new JLabel("Select"));
        itemsPanel.add(new JLabel("Item"));
        itemsPanel.add(new JLabel("Price"));
        itemsPanel.add(new JLabel("Quantity"));

        itemCheckboxes = new JCheckBox[items.length];
        itemLabels = new JLabel[items.length];
        priceLabels = new JLabel[items.length];
        quantityFields = new JTextField[items.length];

        for (int i = 0; i < items.length; i++) {
            itemCheckboxes[i] = new JCheckBox();
            itemCheckboxes[i].setBackground(new Color(245, 245, 245));

            itemLabels[i] = new JLabel(items[i]);
            itemLabels[i].setFont(new Font("Arial", Font.PLAIN, 16));

            priceLabels[i] = new JLabel(String.format("%.2f", prices[i]));
            priceLabels[i].setFont(new Font("Arial", Font.PLAIN, 16));

            quantityFields[i] = new JTextField("0");
            quantityFields[i].setFont(new Font("Arial", Font.PLAIN, 16));

            itemsPanel.add(itemCheckboxes[i]);
            itemsPanel.add(itemLabels[i]);
            itemsPanel.add(priceLabels[i]);
            itemsPanel.add(quantityFields[i]);
        }

        contentPanel.add(itemsPanel);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBounds(30, 400, 680, 50);
        buttonPanel.setLayout(new GridLayout(1, 2, 30, 0));
        contentPanel.add(buttonPanel);

        orderButton = new JButton("PLACE ORDER");
        orderButton.setBackground(new Color(0, 102, 204));
        orderButton.setForeground(Color.WHITE);
        orderButton.setFont(new Font("Arial", Font.BOLD, 16));
        orderButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        orderButton.addActionListener(this);
        buttonPanel.add(orderButton);

        cancelButton = new JButton("CANCEL");
        cancelButton.setBackground(new Color(204, 0, 0));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 16));
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            this.dispose();
        } else if (e.getSource() == orderButton) {
            placeOrder();
        }
    }

    private void placeOrder() {
        try {
            Conn conn = new Conn();
            if (conn.c == null) {
                JOptionPane.showMessageDialog(null, "❌ Database connection failed.");
                return;
            }

            // Insert new order
            String insertOrder = "INSERT INTO orders () VALUES ()";
            PreparedStatement orderStmt = conn.c.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);
            orderStmt.executeUpdate();

            ResultSet rs = orderStmt.getGeneratedKeys();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(null, "❌ Failed to retrieve order ID.");
                return;
            }

            int orderId = rs.getInt(1);
            String insertItem = "INSERT INTO order_items (order_id, item_name, quantity, price, total) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement itemStmt = conn.c.prepareStatement(insertItem);

            boolean orderPlaced = false;

            for (int i = 0; i < items.length; i++) {
                if (itemCheckboxes[i].isSelected()) {
                    int qty;
                    try {
                        qty = Integer.parseInt(quantityFields[i].getText().trim());
                        if (qty <= 0) {
                            JOptionPane.showMessageDialog(null, "❗ Quantity for " + items[i] + " must be > 0");
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "❗ Invalid quantity for " + items[i]);
                        return;
                    }

                    double price = prices[i];
                    double total = price * qty;

                    itemStmt.setInt(1, orderId);
                    itemStmt.setString(2, items[i]);
                    itemStmt.setInt(3, qty);
                    itemStmt.setDouble(4, price);
                    itemStmt.setDouble(5, total);
                    itemStmt.executeUpdate();
                    orderPlaced = true;
                }
            }

            if (orderPlaced) {
                JOptionPane.showMessageDialog(null, "✅ Order placed successfully!");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(null, "⚠️ No items selected.");
            }

            rs.close();
            orderStmt.close();
            itemStmt.close();
            conn.c.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "❌ Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        for (int i = 0; i < items.length; i++) {
            itemCheckboxes[i].setSelected(false);
            quantityFields[i].setText("0");
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}