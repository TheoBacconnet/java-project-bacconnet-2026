package hr.fesb.java.bank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Account implements Transactable, Reportable {
    private String accountId;
    private String customerId;
    private double balance;
    private boolean active;
    private List<Transaction> transactions;

    protected Account(String accountId, String customerId, double initialBalance) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.balance = initialBalance;
        this.active = true;
        this.transactions = new ArrayList<>();
    }

    @Override
    public double getBalance() {
        return balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    public void closeAccount() {
        this.active = false;
    }

    @Override
    public void deposit(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive.");
        balance += amount;
        transactions.add(new Transaction(LocalDateTime.now(), Transaction.Type.DEPOSIT, amount, balance));
    }

    @Override
    public String getSummary() {
        return String.format("Account [%s] | %s | Balance: %.2f", accountId, getAccountType(), balance);
    }

    @Override
    public List<Transaction> getTransactions() {
        return transactions;
    }

    public abstract void applyMonthlyRules();

    public abstract void withdraw(double amount) throws InsufficientFundsException;

    public abstract String getAccountType();

    public abstract String extraFieldsToCsv();

    public void setBalanceDirectly(double balance) {
        this.balance = balance;
    }

    public void addTransactionDirectly(Transaction transaction) {
        this.transactions.add(transaction);
    }

}