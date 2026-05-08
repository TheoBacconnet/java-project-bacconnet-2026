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

        JButton btnNewCustomer = new JButton("New Customer");
        btnNewCustomer.addActionListener(e -> showNewCustomerDialog());
        JButton btnNewAccount = new JButton("New Account");
        btnNewAccount.addActionListener(e -> showNewAccountDialog());

        JButton btnDeposit = new JButton("Deposit");
        btnDeposit.addActionListener(e -> showDepositDialog());
        JButton btnWithdraw = new JButton("Withdraw");
        btnWithdraw.addActionListener(e -> showWithdrawDialog());
        JButton btnTransfer = new JButton("Transfer");
        // btnTransfer.addActionListener(e -> showTransferDialog());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(btnNewCustomer);
        topPanel.add(btnNewAccount);
        topPanel.add(btnDeposit);
        topPanel.add(btnWithdraw);
        topPanel.add(btnTransfer);
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

        JPanel checkingPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        checkingPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 5, 15));
        JTextField txtOverdraft = new JTextField();
        checkingPanel.add(new JLabel("Overdraft Limit (EUR):"));
        checkingPanel.add(txtOverdraft);

        JPanel savingsPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        savingsPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 5, 15));
        JTextField txtRate = new JTextField();
        JTextField txtMaxW = new JTextField();
        savingsPanel.add(new JLabel("Monthly Interest Rate:"));
        savingsPanel.add(txtRate);
        savingsPanel.add(new JLabel("Max Withdrawals/Month:"));
        savingsPanel.add(txtMaxW);

        JPanel businessPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        businessPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 5, 15));
        JTextField txtOverdraftB = new JTextField();
        JTextField txtCompany = new JTextField();
        businessPanel.add(new JLabel("Overdraft Limit (EUR):"));
        businessPanel.add(txtOverdraftB);
        businessPanel.add(new JLabel("Company Name:"));
        businessPanel.add(txtCompany);

        JPanel extraPanel = new JPanel(new BorderLayout());
        extraPanel.add(checkingPanel, BorderLayout.CENTER);

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
                    dialog.setSize(380, 330);
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
                if (selected == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a customer.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String customerId = selected.split(" — ")[0];
                double balance = Double.parseDouble(txtBalance.getText().trim());
                String type = (String) cmbType.getSelectedItem();

                switch (type) {
                    case "CheckingAccount":
                        double overdraft = Double.parseDouble(txtOverdraft.getText().trim());
                        bank.openCheckingAccount(customerId, balance, overdraft);
                        break;
                    case "SavingsAccount":
                        double rate = Double.parseDouble(txtRate.getText().trim());
                        int maxW = Integer.parseInt(txtMaxW.getText().trim());
                        bank.openSavingsAccount(customerId, balance, rate, maxW);
                        break;
                    case "BusinessAccount":
                        double bOverdraft = Double.parseDouble(txtOverdraftB.getText().trim());
                        String company = txtCompany.getText().trim();
                        bank.openBusinessAccount(customerId, balance, bOverdraft, company, "N/A");
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

            // construire le dialog
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

    private JComboBox<String> buildAccountCombo() {
        JComboBox<String> combo = new JComboBox<>();
        for (Account a : bank.getAllAccounts()) {
            combo.addItem(a.getAccountId() + "-" + getCustomerName((a.getCustomerId())));
        }
        return combo;
    }

    private Account getAccountFromCombo(JComboBox<String> combo) throws AccountNotFoundException {
        String selected = (String) combo.getSelectedItem();
        if (selected == null)
            throw new AccountNotFoundException("No account selected.");
        String accId = selected.split("-")[0];
        return bank.findAccount(accId);
    }

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
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.add(btnCancel);
        btnRow.add(btnOk);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

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
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.add(btnCancel);
        btnRow.add(btnOk);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

}