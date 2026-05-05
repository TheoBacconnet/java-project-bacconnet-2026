package hr.fesb.java.bank;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
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

    private DefaultTableModel txTableModel;
    private JTable txTable;
    private JComboBox<String> cmbTxAccount;

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

        JButton btnNewAccount = new JButton("New Account");
        btnNewAccount.addActionListener(e -> showNewAccountDialog());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(btnNewAccount);
        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", buildDashboardPanel());
        tabbedPane.addTab("Customers", buildCustomersPanel());
        tabbedPane.addTab("Accounts", buildAccountsPanel());
        tabbedPane.addTab("Transactions", buildTransactionsPanel());
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
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        customerTable = new JTable(customerTableModel);
        customerTable.setRowHeight(24);
        customerTable.getTableHeader().setReorderingAllowed(false);

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(customerTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterBar.add(new JLabel("Type:"));
        cmbTypeFilter = new JComboBox<>(
                new String[] { "All", "CheckingAccount", "SavingsAccount", "BusinessAccount" });
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
            public boolean isCellEditable(int row, int col) {
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

    private JPanel buildTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(new JLabel("Account:"));
        cmbTxAccount = new JComboBox<>();
        cmbTxAccount.setPreferredSize(new Dimension(250, 25));
        cmbTxAccount.addActionListener(e -> loadTransactions());
        topBar.add(cmbTxAccount);

        String[] columns = { "Date / Time", "Type", "Amount", "Balance After" };
        txTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        txTable = new JTable(txTableModel);
        txTable.setRowHeight(24);
        txTable.getTableHeader().setReorderingAllowed(false);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(txTable), BorderLayout.CENTER);
        return panel;
    }

    private void refreshAll() {
        refreshDashboard();
        refreshCustomerTable();
        refreshAccountTable();
        refreshTxAccountCombo();
        loadTransactions();
    }

    private void refreshDashboard() {
        lblTotalCustomers.setText("Total Customers: " + bank.getTotalCustomerCount());
        lblTotalAccounts.setText("Total Accounts: " + bank.getTotalAccountCount());
        lblTotalBalance.setText(String.format("Total Balance: %.2f EUR", bank.getTotalBalance()));
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
            accountTableModel.addRow(new Object[] {
                    a.getAccountId(),
                    getCustomerName(a.getCustomerId()),
                    a.getAccountType(),
                    String.format("%.2f EUR", a.getBalance()),
                    a.isActive() ? "ACTIVE" : "CLOSED"
            });
        }
    }

    private void filterAccounts() {
        String type = (String) cmbTypeFilter.getSelectedItem();
        String minS = txtMinBalance.getText().trim();
        String maxS = txtMaxBalance.getText().trim();

        List<Account> list = new ArrayList<>(bank.getAllAccounts());
        if (!"All".equals(type))
            list = bank.filterByType(type);

        accountTableModel.setRowCount(0);
        for (Account a : list) {
            if (!minS.isEmpty() && a.getBalance() < Double.parseDouble(minS))
                continue;
            if (!maxS.isEmpty() && a.getBalance() > Double.parseDouble(maxS))
                continue;
            accountTableModel.addRow(new Object[] {
                    a.getAccountId(),
                    getCustomerName(a.getCustomerId()),
                    a.getAccountType(),
                    String.format("%.2f EUR", a.getBalance()),
                    a.isActive() ? "ACTIVE" : "CLOSED"
            });
        }
    }

    private void refreshTxAccountCombo() {
        cmbTxAccount.removeAllItems();
        for (Account a : bank.getAllAccounts()) {
            try {
                Customer c = bank.findCustomer(a.getCustomerId());
                cmbTxAccount.addItem(a.getAccountId() + " — " + c.getFullName());
            } catch (AccountNotFoundException e) {
                cmbTxAccount.addItem(a.getAccountId());
            }
        }
    }

    private void loadTransactions() {
        txTableModel.setRowCount(0);
        String selected = (String) cmbTxAccount.getSelectedItem();
        if (selected == null)
            return;
        String accId = selected.split(" — ")[0];
        try {
            Account a = bank.findAccount(accId);
            for (Transaction t : a.getTransactions()) {
                txTableModel.addRow(new Object[] {
                        t.getFormattedDateTime(),
                        t.getType(),
                        String.format("%.2f EUR", t.getAmount()),
                        String.format("%.2f EUR", t.getBalanceAfter())
                });
            }
        } catch (AccountNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private String getCustomerName(String customerId) {
        try {
            return bank.findCustomer(customerId).getFullName();
        } catch (AccountNotFoundException e) {
            return "";
        }
    }

    private void showNewAccountDialog() {
        JDialog dialog = new JDialog(this, "New Customer & Account", true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(8, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtFirst = new JTextField();
        JTextField txtLast = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtBalance = new JTextField();
        JComboBox<String> cmbType = new JComboBox<>(
                new String[] { "CheckingAccount", "SavingsAccount", "BusinessAccount" });
        JTextField txtExtra1 = new JTextField();
        JTextField txtExtra2 = new JTextField();

        form.add(new JLabel("First Name:"));
        form.add(txtFirst);
        form.add(new JLabel("Last Name:"));
        form.add(txtLast);
        form.add(new JLabel("Email:"));
        form.add(txtEmail);
        form.add(new JLabel("Phone:"));
        form.add(txtPhone);
        form.add(new JLabel("Account Type:"));
        form.add(cmbType);
        form.add(new JLabel("Initial Balance:"));
        form.add(txtBalance);
        form.add(new JLabel("Extra field 1:"));
        form.add(txtExtra1);
        form.add(new JLabel("Extra field 2:"));
        form.add(txtExtra2);

        JButton btnCreate = new JButton("Create");
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());
        btnCreate.addActionListener(e -> {
            try {
                String first = txtFirst.getText().trim();
                String last = txtLast.getText().trim();
                String email = txtEmail.getText().trim();
                String phone = txtPhone.getText().trim();
                double balance = Double.parseDouble(txtBalance.getText().trim());
                String type = (String) cmbType.getSelectedItem();

                Customer c = bank.createCustomer(first, last, email, phone);

                switch (type) {
                    case "CheckingAccount":
                        double overdraft = Double.parseDouble(txtExtra1.getText().trim());
                        bank.openCheckingAccount(c.getCustomerId(), balance, overdraft);
                        break;
                    case "SavingsAccount":
                        double rate = Double.parseDouble(txtExtra1.getText().trim());
                        int maxW = Integer.parseInt(txtExtra2.getText().trim());
                        bank.openSavingsAccount(c.getCustomerId(), balance, rate, maxW);
                        break;
                    case "BusinessAccount":
                        double bOverdraft = Double.parseDouble(txtExtra1.getText().trim());
                        String company = txtExtra2.getText().trim();
                        bank.openBusinessAccount(c.getCustomerId(), balance, bOverdraft, company, "N/A");
                        break;
                }

                bank.save();
                refreshAll();
                dialog.dispose();
            } catch (AccountNotFoundException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.add(btnCreate);
        btnRow.add(btnCancel);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

}