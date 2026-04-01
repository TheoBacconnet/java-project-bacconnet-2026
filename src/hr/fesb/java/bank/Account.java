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

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0 && active) {
            balance += amount;
            transactions.add(new Transaction(Transaction.Type.DEPOSIT, LocalDateTime.now(), amount, balance));
        }
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
    public abstract void withdraw(double amount);
    public abstract String getAccountType();

}