package hr.fesb.java.bank;

import javax.swing.*;
import java.awt.*;

public class BankGUI extends JFrame {
    private Bank bank;

    private JLabel lblTotalCustomers;
    private JLabel lblTotalAccounts;
    private JLabel lblTotalBalance;

    public BankGUI(Bank bank){
        super("Bank Account Management System");
        this.bank = bank;
        initUI();
        refreshDashboard();
    }

    private void initUI(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,700);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", buildDashboardPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel buildDashboardPanel(){
        JPanel panel = new JPanel(new GridLayout(3,1,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));

        lblTotalCustomers = new JLabel("", SwingConstants.CENTER);
        lblTotalAccounts = new JLabel("", SwingConstants.CENTER);
        lblTotalBalance   = new JLabel("", SwingConstants.CENTER);

        lblTotalCustomers.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTotalAccounts.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTotalBalance.setFont(new Font("SansSerif", Font.BOLD, 20));

        panel.add(lblTotalCustomers);
        panel.add(lblTotalAccounts);
        panel.add(lblTotalBalance);

        return panel;
    }

    private void refreshDashboard(){
        lblTotalCustomers.setText("Total Customers: " + bank.getTotalCustomerCount());
        lblTotalAccounts.setText("Total Accounts: " + bank.getTotalAccountCount());
        lblTotalBalance.setText(String.format("Total Balance: $%.2f EUR", bank.getTotalBalance()));
    }

}
