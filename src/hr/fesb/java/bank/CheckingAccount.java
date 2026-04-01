package hr.fesb.java.bank;

import java.time.LocalDateTime;

public class CheckingAccount extends Account {
    private double overdraftLimit;

    public CheckingAccount(String accountId, String customerId, double initialBalance, double overdraftLimit) {
        super(accountId, customerId, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(double amount) {
        if (getBalance() - amount < -overdraftLimit) {
            System.out.println("Error: overdraft limit reached.");
            return;
        }
        setBalance(getBalance() - amount);
        getTransactions().add(new Transaction(Transaction.Type.WITHDRAWAL, LocalDateTime.now(), amount, getBalance()));
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
}
