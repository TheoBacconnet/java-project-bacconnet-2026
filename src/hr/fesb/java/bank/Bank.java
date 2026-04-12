package hr.fesb.java.bank;

import java.util.ArrayList;
import java.util.List;

public class Bank {
    private List<Customer> customers;
    private int customerCounter;
    private int accountCounter;

    public Bank() {
        this.customers = new ArrayList<>();
        this.customerCounter = 1000;
        this.accountCounter = 1000;
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
}
