package hr.fesb.java.bank;

import java.util.ArrayList;
import java.util.List;

public class Bank {
    private List<Customer> customers;
    private int customerCounter;
    private int accountCounter;
    private AccountFileManager fileManager;

    public Bank() {
        this.customers = new ArrayList<>();
        this.customerCounter = 1000;
        this.accountCounter = 1000;
        this.fileManager = new AccountFileManager();
        load();
        syncCounters();
    }

    public Customer createCustomer(String firstName, String lastName, String email, String phone) {
        String customerId = "C" + (++customerCounter);
        Customer customer = new Customer(customerId, firstName, lastName, email, phone);
        customers.add(customer);
        return customer;
    }

    public Customer findCustomer(String customerId) throws AccountNotFoundException {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        throw new AccountNotFoundException(customerId);
    }

    public List<Customer> getAllCustomers() {
        return customers;
    }

    public CheckingAccount openCheckingAccount(String customerId, double initialBalance, double overdraftLimit)
            throws AccountNotFoundException {
        Customer customer = findCustomer(customerId);
        String accountId = "A" + (++accountCounter);
        CheckingAccount account = new CheckingAccount(accountId, customerId, initialBalance, overdraftLimit);
        customer.addAccount(account);
        return account;
    }

    public SavingsAccount openSavingsAccount(String customerId, double initialBalance, double monthlyRate,
            int maxWithdrawals) throws AccountNotFoundException {
        Customer customer = findCustomer(customerId);
        String accountId = "A" + (++accountCounter);
        SavingsAccount account = new SavingsAccount(accountId, customerId, initialBalance, monthlyRate, maxWithdrawals);
        customer.addAccount(account);
        return account;
    }

    public BusinessAccount openBusinessAccount(String customerId, double initialBalance, double overdraftLimit,
            String companyName, String vatNumber) throws AccountNotFoundException {
        Customer customer = findCustomer(customerId);
        String accountId = "A" + (++accountCounter);
        BusinessAccount account = new BusinessAccount(accountId, customerId, initialBalance, overdraftLimit,
                companyName, vatNumber);
        customer.addAccount(account);
        return account;
    }

    public void deposit(Account account, double amount) {
        account.deposit(amount);
    }

    public void withdraw(Account account, double amount) throws InsufficientFundsException {
        account.withdraw(amount);
    }

    public void transfer(Account from, Account to, double amount) throws InsufficientFundsException {
        from.withdraw(amount);
        to.deposit(amount);
    }

    public void closeAccount(Account account) {
        account.closeAccount();
    }

    public void applyMonthlyRules() {
        for (Customer customer : customers) {
            for (Account account : customer.getAccounts()) {
                if (account.isActive()) {
                    account.applyMonthlyRules();
                }
            }
        }
    }

    public int getTotalCustomerCount() {
        return customers.size();
    }

    public int getTotalAccountCount() {
        int count = 0;
        for (Customer customer : customers) {
            count += customer.getAccounts().size();
        }
        return count;
    }

    public double getTotalBalance() {
        double total = 0;
        for (Customer customer : customers) {
            total += customer.getTotalBalance();
        }
        return total;
    }

    public List<Account> getAllAccounts() {
        List<Account> allAccounts = new ArrayList<>();
        for (Customer customer : customers) {
            allAccounts.addAll(customer.getAccounts());
        }
        return allAccounts;
    }

    public List<Account> searchByCustomerName(String query) {
        List<Account> result = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (Customer c : customers) {
            if (c.getFullName().toLowerCase().contains(lowerQuery)) {
                result.addAll(c.getAccounts());
            }
        }
        return result;
    }

    public List<Account> filterByType(String accountType) {
        List<Account> result = new ArrayList<>();
        for (Account a : getAllAccounts()) {
            if (a.getAccountType().equals(accountType)) {
                result.add(a);
            }
        }
        return result;
    }

    public List<Account> filterByBalanceRange(double min, double max) {
        List<Account> result = new ArrayList<>();
        for (Account a : getAllAccounts()) {
            if (a.getBalance() >= min && a.getBalance() <= max) {
                result.add(a);
            }
        }
        return result;
    }

    public void save() {
        fileManager.saveAll(customers);
    }

    private void load() {
        fileManager.loadAll(customers);
    }

    private void syncCounters() {
        for (Customer c : customers) {
            int custIdNum = Integer.parseInt(c.getCustomerId().substring(1));
            if (custIdNum > customerCounter)
                customerCounter = custIdNum;

            for (Account a : c.getAccounts()) {
                int accIdNum = Integer.parseInt(a.getAccountId().substring(1));
                if (accIdNum > accountCounter)
                    accountCounter = accIdNum;
            }
        }
    }
}
