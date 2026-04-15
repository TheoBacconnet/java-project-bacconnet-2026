package hr.fesb.java.bank;

import java.time.LocalDateTime;

public class CheckingAccount extends Account {
    private double overdraftLimit;

    public CheckingAccount(String accountId, String customerId, double initialBalance, double overdraftLimit) {
        super(accountId, customerId, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (getBalance() - amount < overdraftLimit) {
            throw new InsufficientFundsException(amount, getBalance() - overdraftLimit);
        }
        setBalance(getBalance() - amount);
        getTransactions().add(new Transaction(LocalDateTime.now(), Transaction.Type.WITHDRAWAL, amount, getBalance()));
    }

    @Override
    public void applyMonthlyRules() {
        // No specific monthly rules for checking accounts
    }

    @Override
    public String getAccountType() {
        return "CheckingAccount";
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public String extraFieldsToCsv() {
        return String.valueOf(overdraftLimit);
    }
}
