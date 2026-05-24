package hr.fesb.java.bank;

import java.time.LocalDateTime;

/**
 * Standard checking account with a configurable overdraft limit.
 */
public class CheckingAccount extends Account {

    private double overdraftLimit;

    /**
     * Creates a new CheckingAccount.
     *
     * @param accountId      unique account ID
     * @param customerId     owning customer ID
     * @param initialBalance opening balance
     * @param overdraftLimit maximum negative balance allowed (must be positive or zero)
     */
    public CheckingAccount(String accountId, String customerId, double initialBalance, double overdraftLimit) {
        super(accountId, customerId, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    /**
     * Withdraws an amount, respecting the overdraft limit.
     *
     * @param amount amount to withdraw
     * @throws InsufficientFundsException if the withdrawal would exceed the overdraft limit
     * @throws IllegalArgumentException   if amount is not positive
     * @throws IllegalStateException      if the account is inactive
     */
    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0)
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (!isActive())
            throw new IllegalStateException("Cannot withdraw from an inactive account.");
        if (getBalance() - amount < overdraftLimit)
            throw new InsufficientFundsException(amount, getBalance() - overdraftLimit);
        setBalance(getBalance() - amount);
        getTransactions().add(new Transaction(LocalDateTime.now(), Transaction.Type.WITHDRAWAL, amount, getBalance()));
    }

    /** No monthly rules apply to checking accounts. */
    @Override
    public void applyMonthlyRules() {
    }

    /** @return "CheckingAccount" */
    @Override
    public String getAccountType() {
        return "CheckingAccount";
    }

    /** @return the overdraft limit */
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    /** @param overdraftLimit new overdraft limit */
    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    /** @return CSV representation of the overdraft limit */
    @Override
    public String extraFieldsToCsv() {
        return String.valueOf(overdraftLimit);
    }
}