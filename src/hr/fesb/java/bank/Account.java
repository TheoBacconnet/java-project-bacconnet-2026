package hr.fesb.java.bank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all account types.
 * Implements common deposit logic, transaction recording, and account state.
 */
public abstract class Account implements Transactable, Reportable {

    private String accountId;
    private String customerId;
    private double balance;
    private boolean active;
    private List<Transaction> transactions;

    /**
     * Creates a new account with the given parameters.
     *
     * @param accountId      unique account identifier
     * @param customerId     ID of the owning customer
     * @param initialBalance opening balance (must be positive or zero)
     */
    protected Account(String accountId, String customerId, double initialBalance) {
        this.accountId    = accountId;
        this.customerId   = customerId;
        this.balance      = initialBalance;
        this.active       = true;
        this.transactions = new ArrayList<>();
    }

    /** @return current balance */
    @Override
    public double getBalance() {
        return balance;
    }

    /** @return unique account ID */
    public String getAccountId() {
        return accountId;
    }

    /** @return ID of the owning customer */
    public String getCustomerId() {
        return customerId;
    }

    /** @return true if the account is active */
    public boolean isActive() {
        return active;
    }

    /** @param active new active status */
    public void setActive(boolean active) {
        this.active = active;
    }

    /** @param balance new balance value */
    protected void setBalance(double balance) {
        this.balance = balance;
    }

    /** Marks the account as inactive. No further transactions will be allowed. */
    public void closeAccount() {
        this.active = false;
    }

    /**
     * Deposits a positive amount into the account.
     *
     * @param amount amount to deposit
     * @throws IllegalArgumentException if amount is not positive
     * @throws IllegalStateException    if the account is inactive
     */
    @Override
    public void deposit(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive.");
        if (!active)
            throw new IllegalStateException("Cannot deposit into an inactive account.");
        balance += amount;
        transactions.add(new Transaction(LocalDateTime.now(), Transaction.Type.DEPOSIT, amount, balance));
    }

    /** @return a short summary string of the account */
    @Override
    public String getSummary() {
        return String.format("Account [%s] | %s | Balance: %.2f", accountId, getAccountType(), balance);
    }

    /** @return list of all transactions for this account */
    @Override
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /** Applies monthly rules specific to this account type */
    public abstract void applyMonthlyRules();

    /**
     * Withdraws an amount from the account.
     *
     * @param amount amount to withdraw
     * @throws InsufficientFundsException if balance is insufficient
     */
    public abstract void withdraw(double amount) throws InsufficientFundsException;

    /** @return short string identifying the account type */
    public abstract String getAccountType();

    /** @return CSV representation of the subclass-specific fields */
    public abstract String extraFieldsToCsv();

    /**
     * Sets the balance directly without recording a transaction.
     * Used only when loading data from file.
     *
     * @param balance balance to restore
     */
    public void setBalanceDirectly(double balance) {
        this.balance = balance;
    }

    /**
     * Adds a transaction directly without modifying the balance.
     * Used only when loading data from file.
     *
     * @param transaction transaction to add
     */
    public void addTransactionDirectly(Transaction transaction) {
        this.transactions.add(transaction);
    }
}