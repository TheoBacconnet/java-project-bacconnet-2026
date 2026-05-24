package hr.fesb.java.bank;

import java.time.LocalDateTime;

/**
 * Savings account that earns monthly interest and limits the number of withdrawals per month.
 */
public class SavingsAccount extends Account {

    private double monthlyInterestRate;
    private int maxWithdrawalsPerMonth;
    private int withdrawalsThisMonth;

    /**
     * Creates a new SavingsAccount.
     *
     * @param accountId              unique account ID
     * @param customerId             owning customer ID
     * @param initialBalance         opening balance
     * @param monthlyInterestRate    monthly interest rate (e.g. 0.05 for 5%)
     * @param maxWithdrawalsPerMonth maximum number of withdrawals allowed per month
     */
    public SavingsAccount(String accountId, String customerId, double initialBalance, double monthlyInterestRate,
            int maxWithdrawalsPerMonth) {
        super(accountId, customerId, initialBalance);
        this.monthlyInterestRate    = monthlyInterestRate;
        this.maxWithdrawalsPerMonth = maxWithdrawalsPerMonth;
        this.withdrawalsThisMonth   = 0;
    }

    /**
     * Withdraws an amount, respecting the balance and monthly withdrawal limit.
     *
     * @param amount amount to withdraw
     * @throws InsufficientFundsException if balance is insufficient or monthly limit is reached
     * @throws IllegalArgumentException   if amount is not positive
     * @throws IllegalStateException      if the account is inactive
     */
    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0)
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (!isActive())
            throw new IllegalStateException("Cannot withdraw from an inactive account.");
        if (withdrawalsThisMonth >= maxWithdrawalsPerMonth)
            throw new InsufficientFundsException(amount, 0);
        if (getBalance() < amount)
            throw new InsufficientFundsException(amount, getBalance());
        setBalance(getBalance() - amount);
        withdrawalsThisMonth++;
        getTransactions().add(new Transaction(LocalDateTime.now(), Transaction.Type.WITHDRAWAL, amount, getBalance()));
    }

    /**
     * Applies monthly interest to the balance and resets the withdrawal counter.
     * Should be called once per month.
     */
    @Override
    public void applyMonthlyRules() {
        double interest = getBalance() * monthlyInterestRate;
        setBalance(getBalance() + interest);
        getTransactions().add(new Transaction(LocalDateTime.now(), Transaction.Type.INTEREST, interest, getBalance()));
        withdrawalsThisMonth = 0;
    }

    /** @return "SavingsAccount" */
    @Override
    public String getAccountType() {
        return "SavingsAccount";
    }

    /** @return monthly interest rate */
    public double getMonthlyInterestRate() {
        return monthlyInterestRate;
    }

    /** @param monthlyInterestRate new monthly interest rate */
    public void setMonthlyInterestRate(double monthlyInterestRate) {
        this.monthlyInterestRate = monthlyInterestRate;
    }

    /** @return maximum withdrawals allowed per month */
    public int getMaxWithdrawalsPerMonth() {
        return maxWithdrawalsPerMonth;
    }

    /** @return number of withdrawals made this month */
    public int getWithdrawalsThisMonth() {
        return withdrawalsThisMonth;
    }

    /** @return CSV representation of the savings-specific fields */
    @Override
    public String extraFieldsToCsv() {
        return monthlyInterestRate + "," + maxWithdrawalsPerMonth + "," + withdrawalsThisMonth;
    }
}