package hr.fesb.java.bank;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Main Swing window for the Bank Account Management System.
 * Displays customers, accounts and transactions, and provides dialogs for all operations.
 */
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

    /**
     * Creates the main window.
     *
     * @param bank the Bank instance to work with
     */
    public BankGUI(Bank bank) {
        super("Bank Account Management System");
        this.bank = bank;
        initUI();
        refreshAll();
    }

    // Builds the window layout, buttons and tabbed pane
    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JButton btnNewCustomer = new JButton("New Customer");
        btnNewCustomer.addActionListener(e -> showNewCustomerDialog());
        JButton btnNewAccount = new JButton("New Account");
        btnNewAccount.addActionListener(e -> showNewAccountDialog());

        JButton btnDeposit = new JButton("Deposit");
        btnDeposit.addActionListener(e -> showDepositDialog());
        JButton btnWithdraw = new JButton("Withdraw");
        btnWithdraw.addActionListener(e -> showWithdrawDialog());
        JButton btnTransfer = new JButton("Transfer");
        btnTransfer.addActionListener(e -> showTransferDialog());
        JButton btnClose = new JButton("Close Account");
        btnClose.addActionListener(e -> showCloseAccountDialog());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(btnNewCustomer);
        topPanel.add(btnNewAccount);
        topPanel.add(btnDeposit);
        topPanel.add(btnWithdraw);
        topPanel.add(btnTransfer);
        topPanel.add(btnClose);
        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", buildDashboardPanel());
        tabbedPane.addTab("Customers", buildCustomersPanel());
        tabbedPane.addTab("Accounts", buildAccountsPanel());
        tabbedPane.addTab("Transactions", buildTransactionsPanel());
        add(tabbedPane, BorderLayout.CENTER);

        setJMenuBar(buildMenuBar());
    }

    // Builds the dashboard panel showing global statistics
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

    // Builds the customers tab with search bar and table
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

        // Double-click on a row to view the customer's accounts
        customerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showCustomerAccounts();
                }
            }
        });

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(customerTable), BorderLayout.CENTER);
        return panel;
    }

    // Builds the accounts tab with type/balance filters and sort button
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

        JButton btnSort = new JButton("Sort by Balance");
        btnSort.addActionListener(e -> {
            accountTableModel.setRowCount(0);
            for (Account a : bank.getAccountsSortedByBalance()) {
                accountTableModel.addRow(new Object[] {
                        a.getAccountId(),
                        getCustomerName(a.getCustomerId()),
                        a.getAccountType(),
                        String.format("%.2f EUR", a.getBalance()),
                        a.isActive() ? "ACTIVE" : "CLOSED"
                });
            }
        });
        filterBar.add(btnSort);

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

    // Builds the transactions tab with account selector and sort buttons
    private JPanel buildTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(new JLabel("Account:"));
        cmbTxAccount = new JComboBox<>();
        cmbTxAccount.setPreferredSize(new Dimension(250, 25));
        cmbTxAccount.addActionListener(e -> loadTransactions());
        topBar.add(cmbTxAccount);

        JButton btnSortDate = new JButton("Sort by Date ↑");
        btnSortDate.addActionListener(e -> sortTransactions("date"));
        topBar.add(btnSortDate);

        JButton btnSortAmount = new JButton("Sort by Amount ↓");
        btnSortAmount.addActionListener(e -> sortTransactions("amount"));
        topBar.add(btnSortAmount);

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

    // Refreshes all tabs and dashboard
    private void refreshAll() {
        refreshDashboard();
        refreshCustomerTable();
        refreshAccountTable();
        refreshTxAccountCombo();
        loadTransactions();
    }

    // Updates dashboard labels with current statistics
    private void refreshDashboard() {
        lblTotalCustomers.setText("Total Customers: " + bank.getTotalCustomerCount());
        lblTotalAccounts.setText("Total Accounts: " + bank.getTotalAccountCount());
        lblTotalBalance.setText(String.format("Total Balance: %.2f EUR", bank.getTotalBalance()));
    }

    // Repopulates the customer table sorted by last name
    private void refreshCustomerTable() {
        customerTableModel.setRowCount(0);
        for (Customer c : bank.getCustomersSortedByName()) {
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

    // Filters the customer table by name or email
    private void filterCustomers() {
        String query = txtCustomerSearch.getText().trim();
        if (query.isEmpty()) {
            refreshCustomerTable();
            return;
        }
        customerTableModel.setRowCount(0);
        for (Customer c : bank.getCustomersSortedByName()) {
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

    // Repopulates the account table with all accounts
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

    // Filters the account table by type and/or balance range
    private void filterAccounts() {
        String type = (String) cmbTypeFilter.getSelectedItem();
        String minS = txtMinBalance.getText().trim();
        String maxS = txtMaxBalance.getText().trim();

        try {
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
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for balance range.\nPress 'Reset'",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Repopulates the transaction account selector combo
    private void refreshTxAccountCombo() {
        cmbTxAccount.removeAllItems();
        for (Account a : bank.getAllAccounts()) {
            try {
                Customer c = bank.findCustomer(a.getCustomerId());
                cmbTxAccount.addItem(a.getAccountId() + "-" + c.getFullName());
            } catch (AccountNotFoundException e) {
                cmbTxAccount.addItem(a.getAccountId());
            }
        }
    }

    // Loads transactions for the currently selected account
    private void loadTransactions() {
        txTableModel.setRowCount(0);
        String selected = (String) cmbTxAccount.getSelectedItem();
        if (selected == null)
            return;
        String accId = selected.split("-")[0];
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

    // Returns the full name of a customer by ID, or empty string if not found
    private String getCustomerName(String customerId) {
        try {
            return bank.findCustomer(customerId).getFullName();
        } catch (AccountNotFoundException e) {
            return "";
        }
    }

    // Opens the New Account dialog
    private void showNewAccountDialog() {
        if (bank.getAllCustomers().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please create a customer first.",
                    "No customers", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "New Account", true);
        dialog.setSize(380, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel fixedForm = new JPanel(new GridLayout(3, 2, 8, 8));
        fixedForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JComboBox<String> cmbCustomer = new JComboBox<>();
        for (Customer c : bank.getAllCustomers()) {
            cmbCustomer.addItem(c.getCustomerId() + " — " + c.getFullName());
        }

        JComboBox<String> cmbType = new JComboBox<>(
                new String[] { "CheckingAccount", "SavingsAccount", "BusinessAccount" });
        JTextField txtBalance = new JTextField();

        fixedForm.add(new JLabel("Customer:"));
        fixedForm.add(cmbCustomer);
        fixedForm.add(new JLabel("Account Type:"));
        fixedForm.add(cmbType);
        fixedForm.add(new JLabel("Initial Balance:"));
        fixedForm.add(txtBalance);

        JTextField txtOverdraft = new JTextField();
        JTextField txtRate = new JTextField();
        JTextField txtMaxW = new JTextField();
        JTextField txtOverdraftB = new JTextField();
        JTextField txtCompany = new JTextField();
        JTextField txtVat = new JTextField();

        JPanel checkingPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        checkingPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 5, 15));
        checkingPanel.add(new JLabel("Overdraft Limit (e.g. -200):"));
        checkingPanel.add(txtOverdraft);

        JPanel savingsPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        savingsPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 5, 15));
        savingsPanel.add(new JLabel("Interest Rate (e.g. 0.05):"));
        savingsPanel.add(txtRate);
        savingsPanel.add(new JLabel("Max Withdrawals/Month:"));
        savingsPanel.add(txtMaxW);

        JPanel businessPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        businessPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 5, 15));
        businessPanel.add(new JLabel("Overdraft Limit (e.g. -5000):"));
        businessPanel.add(txtOverdraftB);
        businessPanel.add(new JLabel("Company Name:"));
        businessPanel.add(txtCompany);
        businessPanel.add(new JLabel("VAT Number:"));
        businessPanel.add(txtVat);

        JPanel extraPanel = new JPanel(new BorderLayout());
        extraPanel.add(checkingPanel, BorderLayout.CENTER);

        // Swap the extra fields panel when the account type changes
        cmbType.addActionListener(e -> {
            extraPanel.removeAll();
            switch ((String) cmbType.getSelectedItem()) {
                case "CheckingAccount":
                    extraPanel.add(checkingPanel, BorderLayout.CENTER);
                    dialog.setSize(380, 300);
                    break;
                case "SavingsAccount":
                    extraPanel.add(savingsPanel, BorderLayout.CENTER);
                    dialog.setSize(380, 330);
                    break;
                case "BusinessAccount":
                    extraPanel.add(businessPanel, BorderLayout.CENTER);
                    dialog.setSize(380, 360);
                    break;
            }
            extraPanel.revalidate();
            extraPanel.repaint();
        });

        JButton btnCreate = new JButton("Create");
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnCreate.addActionListener(e -> {
            try {
                String selected = (String) cmbCustomer.getSelectedItem();
                String custId = selected.split(" — ")[0];
                double balance = Double.parseDouble(txtBalance.getText().trim());
                String type = (String) cmbType.getSelectedItem();

                if (balance < 0) {
                    JOptionPane.showMessageDialog(dialog, "Initial balance cannot be negative.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                switch (type) {
                    case "CheckingAccount":
                        double overdraft = Double.parseDouble(txtOverdraft.getText().trim());
                        if (overdraft > 0) {
                            JOptionPane.showMessageDialog(dialog,
                                    "Overdraft limit must be negative (e.g. -200).",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        bank.openCheckingAccount(custId, balance, overdraft);
                        break;

                    case "SavingsAccount":
                        double rate = Double.parseDouble(txtRate.getText().trim());
                        int maxW = Integer.parseInt(txtMaxW.getText().trim());
                        if (rate <= 0 || rate >= 1) {
                            JOptionPane.showMessageDialog(dialog,
                                    "Interest rate must be between 0 and 1 (e.g. 0.05 for 5%).",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (maxW <= 0) {
                            JOptionPane.showMessageDialog(dialog,
                                    "Max withdrawals must be greater than 0.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        bank.openSavingsAccount(custId, balance, rate, maxW);
                        break;

                    case "BusinessAccount":
                        double bOverdraft = Double.parseDouble(txtOverdraftB.getText().trim());
                        String company = txtCompany.getText().trim();
                        String vat = txtVat.getText().trim();
                        if (bOverdraft >= 0) {
                            JOptionPane.showMessageDialog(dialog,
                                    "Overdraft limit must be negative (e.g. -5000).",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (company.isEmpty()) {
                            JOptionPane.showMessageDialog(dialog,
                                    "Company name cannot be empty.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (vat.isEmpty()) {
                            JOptionPane.showMessageDialog(dialog,
                                    "VAT number cannot be empty.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        bank.openBusinessAccount(custId, balance, bOverdraft, company, vat);
                        break;
                }

                bank.save();
                refreshAll();
                dialog.dispose();

            } catch (AccountNotFoundException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.add(btnCancel);
        btnRow.add(btnCreate);

        JPanel content = new JPanel(new BorderLayout());
        content.add(fixedForm, BorderLayout.NORTH);
        content.add(extraPanel, BorderLayout.CENTER);

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Opens the New Customer dialog
    private void showNewCustomerDialog() {
        JDialog dialog = new JDialog(this, "New Customer", true);
        dialog.setSize(350, 240);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JTextField txtFirst = new JTextField();
        JTextField txtLast = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtPhone = new JTextField();

        form.add(new JLabel("First Name:"));
        form.add(txtFirst);
        form.add(new JLabel("Last Name:"));
        form.add(txtLast);
        form.add(new JLabel("Email:"));
        form.add(txtEmail);
        form.add(new JLabel("Phone:"));
        form.add(txtPhone);

        JButton btnCreate = new JButton("Create");
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnCreate.addActionListener(e -> {
            try {
                String first = txtFirst.getText().trim();
                String last = txtLast.getText().trim();
                String email = txtEmail.getText().trim();
                String phone = txtPhone.getText().trim();

                if (first.isEmpty() || last.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                bank.createCustomer(first, last, email, phone);
                bank.save();
                refreshAll();
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.add(btnCancel);
        btnRow.add(btnCreate);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Shows a dialog listing all accounts for the selected customer
    private void showCustomerAccounts() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a customer first.",
                    "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String customerId = (String) customerTableModel.getValueAt(selectedRow, 0);

        try {
            Customer c = bank.findCustomer(customerId);

            if (c.getAccounts().isEmpty()) {
                JOptionPane.showMessageDialog(this, "This customer has no accounts.",
                        "Accounts", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog(this, "Accounts — " + c.getFullName(), true);
            dialog.setSize(550, 250);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout(10, 10));

            String[] columns = { "Account ID", "Type", "Balance", "Status" };
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };

            for (Account a : c.getAccounts()) {
                model.addRow(new Object[] {
                        a.getAccountId(),
                        a.getAccountType(),
                        String.format("%.2f EUR", a.getBalance()),
                        a.isActive() ? "ACTIVE" : "CLOSED"
                });
            }

            JTable table = new JTable(model);
            table.setRowHeight(24);
            table.getTableHeader().setReorderingAllowed(false);

            JButton btnClose = new JButton("Close");
            btnClose.addActionListener(e -> dialog.dispose());
            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnRow.add(btnClose);

            dialog.add(new JScrollPane(table), BorderLayout.CENTER);
            dialog.add(btnRow, BorderLayout.SOUTH);
            dialog.setVisible(true);

        } catch (AccountNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Builds a combo box populated with all accounts
    private JComboBox<String> buildAccountCombo() {
        JComboBox<String> combo = new JComboBox<>();
        for (Account a : bank.getAllAccounts()) {
            combo.addItem(a.getAccountId() + "-" + getCustomerName((a.getCustomerId())));
        }
        return combo;
    }

    // Extracts and returns the selected account from a combo box
    private Account getAccountFromCombo(JComboBox<String> combo) throws AccountNotFoundException {
        String selected = (String) combo.getSelectedItem();
        if (selected == null)
            throw new AccountNotFoundException("No account selected.");
        String accId = selected.split("-")[0];
        return bank.findAccount(accId);
    }

    // Opens the Deposit dialog
    private void showDepositDialog() {
        JDialog dialog = new JDialog(this, "Deposit", true);
        dialog.setSize(350, 160);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JComboBox<String> cmbAccount = buildAccountCombo();
        JTextField txtAmount = new JTextField();

        form.add(new JLabel("Account:"));
        form.add(cmbAccount);
        form.add(new JLabel("Amount:"));
        form.add(txtAmount);

        JButton btnOk = new JButton("Deposit");
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());
        btnOk.addActionListener(e -> {
            try {
                Account a = getAccountFromCombo(cmbAccount);
                double amount = Double.parseDouble(txtAmount.getText().trim());
                bank.deposit(a, amount);
                bank.save();
                refreshAll();
                dialog.dispose();
            } catch (AccountNotFoundException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid amount.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.add(btnCancel);
        btnRow.add(btnOk);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Opens the Withdraw dialog
    private void showWithdrawDialog() {
        JDialog dialog = new JDialog(this, "Withdraw", true);
        dialog.setSize(350, 160);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JComboBox<String> cmbAccount = buildAccountCombo();
        JTextField txtAmount = new JTextField();

        form.add(new JLabel("Account:"));
        form.add(cmbAccount);
        form.add(new JLabel("Amount:"));
        form.add(txtAmount);

        JButton btnOk = new JButton("Withdraw");
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnOk.addActionListener(e -> {
            try {
                Account a = getAccountFromCombo(cmbAccount);
                double amt = Double.parseDouble(txtAmount.getText().trim());
                bank.withdraw(a, amt);
                bank.save();
                refreshAll();
                dialog.dispose();
            } catch (AccountNotFoundException | InsufficientFundsException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid amount.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.add(btnCancel);
        btnRow.add(btnOk);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Opens the Transfer dialog
    private void showTransferDialog() {
        JDialog dialog = new JDialog(this, "Transfer", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JComboBox<String> cmbFrom = buildAccountCombo();
        JComboBox<String> cmbTo = buildAccountCombo();
        JTextField txtAmount = new JTextField();

        form.add(new JLabel("From:"));
        form.add(cmbFrom);
        form.add(new JLabel("To:"));
        form.add(cmbTo);
        form.add(new JLabel("Amount:"));
        form.add(txtAmount);

        JButton btnOk = new JButton("Transfer");
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnOk.addActionListener(e -> {
            try {
                Account from = getAccountFromCombo(cmbFrom);
                Account to = getAccountFromCombo(cmbTo);

                if (from.getAccountId().equals(to.getAccountId())) {
                    JOptionPane.showMessageDialog(dialog,
                            "Source and destination accounts must be different.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double amt = Double.parseDouble(txtAmount.getText().trim());

                if (amt <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Amount must be positive.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                bank.transfer(from, to, amt);
                bank.save();
                refreshAll();
                dialog.dispose();

            } catch (AccountNotFoundException | InsufficientFundsException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid amount.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.add(btnCancel);
        btnRow.add(btnOk);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Builds the menu bar with File and Help menus
    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem itemSave = new JMenuItem("Save");
        itemSave.addActionListener(e -> {
            bank.save();
            JOptionPane.showMessageDialog(this, "Data saved successfully.",
                    "Save", JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem itemApplyMonthly = new JMenuItem("Apply Monthly Rules");
        itemApplyMonthly.addActionListener(e -> {
            bank.applyMonthlyRules();
            bank.save();
            refreshAll();
            JOptionPane.showMessageDialog(this, "Monthly rules applied.",
                    "Monthly Rules", JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem itemExit = new JMenuItem("Exit");
        itemExit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Save before exiting?", "Exit",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                bank.save();
                System.exit(0);
            } else if (confirm == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
        });

        fileMenu.add(itemSave);
        fileMenu.add(itemApplyMonthly);
        fileMenu.addSeparator();
        fileMenu.add(itemExit);

        JMenu helpMenu = new JMenu("Help");

        JMenuItem itemAbout = new JMenuItem("About");
        itemAbout.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Bank Account Management System\n" +
                        "Student: Theo Emmanuel Bernard Bacconnet\n" +
                        "Course: Programming in Java (FELP11)\n" +
                        "University of Split - FESB\n" +
                        "Academic Year 2025/2026",
                "About", JOptionPane.INFORMATION_MESSAGE));

        helpMenu.add(itemAbout);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    // Sorts the transaction table by date or amount
    private void sortTransactions(String criteria) {
        String selected = (String) cmbTxAccount.getSelectedItem();
        if (selected == null)
            return;
        String accId = selected.split(" — ")[0];

        try {
            Account a = bank.findAccount(accId);
            List<Transaction> list = new ArrayList<>(a.getTransactions());

            if (criteria.equals("date")) {
                Collections.sort(list, new Comparator<Transaction>() {
                    @Override
                    public int compare(Transaction t1, Transaction t2) {
                        return t1.getDateTime().compareTo(t2.getDateTime());
                    }
                });
            } else if (criteria.equals("amount")) {
                Collections.sort(list, new Comparator<Transaction>() {
                    @Override
                    public int compare(Transaction t1, Transaction t2) {
                        return Double.compare(t2.getAmount(), t1.getAmount());
                    }
                });
            }

            txTableModel.setRowCount(0);
            for (Transaction t : list) {
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

    // Opens the Close Account dialog with confirmation prompt
    private void showCloseAccountDialog() {
        JDialog dialog = new JDialog(this, "Close Account", true);
        dialog.setSize(350, 160);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(1, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JComboBox<String> cmbAccount = buildAccountCombo();
        form.add(new JLabel("Account:"));
        form.add(cmbAccount);

        JButton btnClose = new JButton("Close Account");
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnClose.addActionListener(e -> {
            try {
                Account a = getAccountFromCombo(cmbAccount);

                int confirm = JOptionPane.showConfirmDialog(dialog,
                        "Close account " + a.getAccountId() + "? This cannot be undone.",
                        "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (confirm != JOptionPane.YES_OPTION)
                    return;

                bank.closeAccount(a);
                bank.save();
                refreshAll();
                dialog.dispose();

            } catch (AccountNotFoundException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.add(btnCancel);
        btnRow.add(btnClose);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}