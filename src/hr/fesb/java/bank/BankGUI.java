package hr.fesb.java.bank;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class BankGUI extends JFrame {
    private Bank bank;

    private JLabel lblTotalCustomers;
    private JLabel lblTotalAccounts;
    private JLabel lblTotalBalance;

    private DefaultTableModel customerTableModel;
    private JTable customerTable;
    private JTextField txtCustomerSearch;

    private DefaultTableModel accountTableModel;
    private JTable accountTable;
    private JComboBox<String> cmbTypeFilter;
    private JTextField txtMinBalance;
    private JTextField txtMaxBalance;

    public BankGUI(Bank bank) {
        super("Bank Account Management System");
        this.bank = bank;
        initUI();
        refreshAll();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", buildDashboardPanel());
        tabbedPane.addTab("Customers", buildCustomersPanel());
        tabbedPane.addTab("Accounts", buildAccountsPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel buildDashboardPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        lblTotalCustomers = new JLabel("", SwingConstants.CENTER);
        lblTotalAccounts = new JLabel("", SwingConstants.CENTER);
        lblTotalBalance = new JLabel("", SwingConstants.CENTER);

        lblTotalCustomers.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTotalAccounts.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTotalBalance.setFont(new Font("SansSerif", Font.BOLD, 20));

        panel.add(lblTotalCustomers);
        panel.add(lblTotalAccounts);
        panel.add(lblTotalBalance);

        return panel;
    }

    private JPanel buildCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.add(new JLabel("Search:"));
        txtCustomerSearch = new JTextField(20);
        searchBar.add(txtCustomerSearch);
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> filterCustomers());
        searchBar.add(btnSearch);
        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> {
            txtCustomerSearch.setText("");
            refreshCustomerTable();
        });
        searchBar.add(btnReset);

        String[] columns = { "ID", "First Name", "Last Name", "Email", "Phone", "Accounts", "Total Balance" };
        customerTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(customerTableModel);
        customerTable.setRowHeight(24);
        customerTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(searchBar, BorderLayout.NORTH);
        return panel;
    }

    private JPanel buildAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterBar.add(new JLabel("Type:"));
        cmbTypeFilter = new JComboBox<>(new String[] { "All", "CheckingAccount", "SavingsAccount", "BusinessAccount" });
        filterBar.add(cmbTypeFilter);

        filterBar.add(new JLabel("Min Balance:"));
        txtMinBalance = new JTextField(6);
        filterBar.add(txtMinBalance);

        filterBar.add(new JLabel("Max Balance:"));
        txtMaxBalance = new JTextField(6);
        filterBar.add(txtMaxBalance);

        JButton btnFilter = new JButton("Filter");
        btnFilter.addActionListener(e -> filterAccounts());
        filterBar.add(btnFilter);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> {
            cmbTypeFilter.setSelectedIndex(0);
            txtMinBalance.setText("");
            txtMaxBalance.setText("");
            refreshAccountTable();
        });
        filterBar.add(btnReset);

        String[] columns = { "Account ID", "Customer", "Type", "Balance", "Status" };
        accountTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        accountTable = new JTable(accountTableModel);
        accountTable.setRowHeight(24);
        accountTable.getTableHeader().setReorderingAllowed(false);

        panel.add(filterBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(accountTable), BorderLayout.CENTER);
        return panel;
    }

    private void refreshAll() {
        refreshDashboard();
        refreshCustomerTable();
        refreshAccountTable();
    }

    private void refreshDashboard() {
        lblTotalCustomers.setText("Total Customers: " + bank.getTotalCustomerCount());
        lblTotalAccounts.setText("Total Accounts: " + bank.getTotalAccountCount());
        lblTotalBalance.setText(String.format("Total Balance: $%.2f EUR", bank.getTotalBalance()));
    }

    private void refreshCustomerTable() {
        customerTableModel.setRowCount(0);
        for (Customer c : bank.getAllCustomers()) {
            customerTableModel.addRow(new Object[] {
                    c.getCustomerId(),
                    c.getFirstName(),
                    c.getLastName(),
                    c.getEmail(),
                    c.getPhone(),
                    c.getAccounts().size(),
                    String.format("%.2f EUR", c.getTotalBalance())
            });
        }
    }

    private void filterCustomers() {
        String query = txtCustomerSearch.getText().trim();
        if (query.isEmpty()) {
            refreshCustomerTable();
            return;
        }
        customerTableModel.setRowCount(0);
        for (Customer c : bank.getAllCustomers()) {
            if (c.getFullName().toLowerCase().contains(query.toLowerCase())
                    || c.getEmail().toLowerCase().contains(query.toLowerCase())) {
                customerTableModel.addRow(new Object[] {
                        c.getCustomerId(),
                        c.getFirstName(),
                        c.getLastName(),
                        c.getEmail(),
                        c.getPhone(),
                        c.getAccounts().size(),
                        String.format("%.2f EUR", c.getTotalBalance())
                });
            }
        }
    }

    private void refreshAccountTable() {
        accountTableModel.setRowCount(0);
        for (Account a : bank.getAllAccounts()) {
            String customerName = "";
            for (Customer c : bank.getAllCustomers()) {
                if (c.getCustomerId().equals(a.getCustomerId())) {
                    customerName = c.getFullName();
                    break;
                }
            }
            accountTableModel.addRow(new Object[] {
                    a.getAccountId(),
                    customerName,
                    a.getAccountType(),
                    String.format("%.2f EUR", a.getBalance()),
                    a.isActive() ? "ACTIVE" : "CLOSED"
            });
        }
    }

    private void filterAccounts(){
        String type = (String) cmbTypeFilter.getSelectedItem();
        String minS = txtMinBalance.getText().trim();
        String maxS = txtMaxBalance.getText().trim();

        List<Account> list = bank.getAllAccounts();

        if (!type.equals("All")){
            list = bank.filterByType(type);
        }

        accountTableModel.setRowCount(0);
        for (Account a : list){
            if (!minS.isEmpty() && a.getBalance() < Double.parseDouble(minS)) continue;
            if (!maxS.isEmpty() && a.getBalance() > Double.parseDouble(maxS)) continue;

            String customerName = "";
            for (Customer c : bank.getAllCustomers()) {
                if (c.getCustomerId().equals(a.getCustomerId())) {
                    customerName = c.getFullName();
                    break;
                }
            }
            accountTableModel.addRow(new Object[] {
                    a.getAccountId(),
                    customerName,
                    a.getAccountType(),
                    String.format("%.2f EUR", a.getBalance()),
                    a.isActive() ? "ACTIVE" : "CLOSED"
            });
        }
    }

}
