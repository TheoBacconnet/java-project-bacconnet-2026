package hr.fesb.java.bank;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;

/**
 * Central class managing all customers and accounts.
 * Provides all business operations and delegates persistence to
 * AccountFileManager.
 */
public class Bank {

    private List<Customer> customers;
    private Map<String, Account> accounts;
    private int customerCounter;
    private int accountCounter;
    private AccountFileManager fileManager;

    /**
     * Creates a new Bank instance and loads any existing data from CSV files.
     */
    public Bank() {
        this.customers = new ArrayList<>();
        this.accounts = new HashMap<>();
        this.customerCounter = 1000;
        this.accountCounter = 1000;
        this.fileManager = new AccountFileManager();
        load();
        syncCounters();
    }

    /**
     * Creates a new customer and adds them to the bank.
     *
     * @param firstName customer's first name
     * @param lastName  customer's last name
     * @param email     customer's email
     * @param phone     customer's phone number
     * @return the newly created customer
     */
    public Customer createCustomer(String firstName, String lastName,
            String email, String phone) {
        String customerId = "C" + (++customerCounter);
        Customer customer = new Customer(customerId, firstName,
                lastName, email, phone);
        customers.add(customer);
        return customer;
    }

    /**
     * Finds a customer by ID.
     *
     * @param customerId the customer ID to look up
     * @return the matching customer
     * @throws AccountNotFoundException if no customer with that ID exists
     */
    public Customer findCustomer(String customerId)
            throws AccountNotFoundException {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId))
                return customer;
        }
        throw new AccountNotFoundException(customerId);
    }

    /**
     * Finds an account by ID.
     *
     * @param accountId the account ID to look up
     * @return the matching account
     * @throws AccountNotFoundException if no account with that ID exists
     */
    public Account findAccount(String accountId) throws AccountNotFoundException {
        Account account = accounts.get(accountId);
        if (account == null)
            throw new AccountNotFoundException(accountId);
        return account;
    }

    /** @return all customers in the bank */
    public List<Customer> getAllCustomers() {
        return customers;
    }

    /**
     * Opens a new CheckingAccount for a customer.
     *
     * @param customerId     owning customer ID
     * @param initialBalance opening balance
     * @param overdraftLimit maximum negative balance allowed (must be positive or
     *                       zero)
     * @return the newly created account
     * @throws AccountNotFoundException if the customer does not exist
     */
    public CheckingAccount openCheckingAccount(String customerId, double initialBalance, double overdraftLimit)
            throws AccountNotFoundException {
        Customer customer = findCustomer(customerId);
        String accountId = "A" + (++accountCounter);
        CheckingAccount account = new CheckingAccount(accountId, customerId, initialBalance, overdraftLimit);
        customer.addAccount(account);
        accounts.put(accountId, account);
        return account;
    }

    /**
     * Opens a new SavingsAccount for a customer.
     *
     * @param customerId     owning customer ID
     * @param initialBalance opening balance
     * @param monthlyRate    monthly interest rate (e.g. 0.05 for 5%)
     * @param maxWithdrawals maximum withdrawals allowed per month
     * @return the newly created account
     * @throws AccountNotFoundException if the customer does not exist
     */
    public SavingsAccount openSavingsAccount(String customerId, double initialBalance, double monthlyRate,
            int maxWithdrawals) throws AccountNotFoundException {
        Customer customer = findCustomer(customerId);
        String accountId = "A" + (++accountCounter);
        SavingsAccount account = new SavingsAccount(accountId, customerId, initialBalance, monthlyRate, maxWithdrawals);
        customer.addAccount(account);
        accounts.put(accountId, account);
        return account;
    }

    /**
     * Opens a new BusinessAccount for a customer.
     *
     * @param customerId     owning customer ID
     * @param initialBalance opening balance
     * @param overdraftLimit maximum negative balance allowed (must be positive or
     *                       zero)
     * @param companyName    name of the company
     * @param vatNumber      VAT registration number
     * @return the newly created account
     * @throws AccountNotFoundException if the customer does not exist
     */
    public BusinessAccount openBusinessAccount(String customerId, double initialBalance, double overdraftLimit,
            String companyName, String vatNumber) throws AccountNotFoundException {
        Customer customer = findCustomer(customerId);
        String accountId = "A" + (++accountCounter);
        BusinessAccount account = new BusinessAccount(accountId, customerId, initialBalance, overdraftLimit,
                companyName, vatNumber);
        customer.addAccount(account);
        accounts.put(accountId, account);
        return account;
    }

    /**
     * Deposits an amount into an account.
     *
     * @param account target account
     * @param amount  amount to deposit
     */
    public void deposit(Account account, double amount) {
        account.deposit(amount);
    }

    /**
     * Withdraws an amount from an account.
     *
     * @param account target account
     * @param amount  amount to withdraw
     * @throws InsufficientFundsException if balance is insufficient
     */
    public void withdraw(Account account, double amount) throws InsufficientFundsException {
        account.withdraw(amount);
    }

    /**
     * Transfers an amount from one account to another.
     *
     * @param from   source account
     * @param to     destination account
     * @param amount amount to transfer
     * @throws InsufficientFundsException if the source account has insufficient
     *                                    funds
     */
    public void transfer(Account from, Account to, double amount) throws InsufficientFundsException {
        from.withdraw(amount);
        to.deposit(amount);
    }

    /**
     * Closes an account, marking it as inactive.
     *
     * @param account account to close
     */
    public void closeAccount(Account account) {
        account.closeAccount();
    }

    /** Applies monthly rules to all active accounts. */
    public void applyMonthlyRules() {
        for (Account account : accounts.values()) {
            if (account.isActive())
                account.applyMonthlyRules();
        }
    }

    /** @return total number of customers */
    public int getTotalCustomerCount() {
        return customers.size();
    }

    /** @return total number of accounts */
    public int getTotalAccountCount() {
        return accounts.size();
    }

    /** @return sum of all account balances */
    public double getTotalBalance() {
        double total = 0;
        for (Customer customer : customers)
            total += customer.getTotalBalance();
        return total;
    }

    /** @return all accounts in the bank */
    public Collection<Account> getAllAccounts() {
        return accounts.values();
    }

    /**
     * Returns all accounts belonging to customers whose name matches the query.
     *
     * @param query case-insensitive partial name search
     * @return list of matching accounts
     */
    public List<Account> searchByCustomerName(String query) {
        List<Account> result = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (Customer c : customers) {
            if (c.getFullName().toLowerCase().contains(lowerQuery))
                result.addAll(c.getAccounts());
        }
        return result;
    }

    /**
     * Filters accounts by type.
     *
     * @param accountType account type string (e.g. "CheckingAccount")
     * @return list of matching accounts
     */
    public List<Account> filterByType(String accountType) {
        List<Account> result = new ArrayList<>();
        for (Account a : getAllAccounts()) {
            if (a.getAccountType().equals(accountType))
                result.add(a);
        }
        return result;
    }

    /**
     * Filters accounts whose balance is within the given range.
     *
     * @param min minimum balance (inclusive)
     * @param max maximum balance (inclusive)
     * @return list of matching accounts
     */
    public List<Account> filterByBalanceRange(double min, double max) {
        List<Account> result = new ArrayList<>();
        for (Account a : getAllAccounts()) {
            if (a.getBalance() >= min && a.getBalance() <= max)
                result.add(a);
        }
        return result;
    }

    /** Saves all data to disk. */
    public void save() {
        fileManager.saveAll(customers);
    }

    private void load() {
        fileManager.loadAll(customers, accounts);
    }

    // Syncs ID counters after loading to avoid duplicate IDs
    private void syncCounters() {
        for (Customer c : customers) {
            int custIdNum = Integer.parseInt(
                    c.getCustomerId().substring(1));
            if (custIdNum > customerCounter)
                customerCounter = custIdNum;
        }
        for (Account a : accounts.values()) {
            int accIdNum = Integer.parseInt(
                    a.getAccountId().substring(1));
            if (accIdNum > accountCounter)
                accountCounter = accIdNum;
        }
    }

    /**
     * Returns all customers sorted alphabetically by last name.
     *
     * @return sorted list of customers
     */
    public List<Customer> getCustomersSortedByName() {
        List<Customer> sorted = new ArrayList<>(customers);
        Collections.sort(sorted, new Comparator<Customer>() {
            @Override
            public int compare(Customer c1, Customer c2) {
                return c1.getLastName().compareToIgnoreCase(c2.getLastName());
            }
        });
        return sorted;
    }

    /**
     * Returns all accounts sorted by balance in descending order.
     *
     * @return sorted list of accounts
     */
    public List<Account> getAccountsSortedByBalance() {
        List<Account> sorted = new ArrayList<>(accounts.values());
        Collections.sort(sorted, new Comparator<Account>() {
            @Override
            public int compare(Account a1, Account a2) {
                return Double.compare(a2.getBalance(), a1.getBalance());
            }
        });
        return sorted;
    }
}