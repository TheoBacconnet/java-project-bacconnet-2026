package hr.fesb.java.bank;

import java.time.LocalDateTime;

public class SavingsAccount extends Account {
    private double monthlyInterestRate;
    private int maxWithdrawalsPerMonth;
    private int withdrawalsThisMonth;

    public SavingsAccount(String accountId, String customerId, double initialBalance, double monthlyInterestRate, int maxWithdrawalsPerMonth) {
        super(accountId, customerId, initialBalance);
        this.monthlyInterestRate = monthlyInterestRate;
        this.maxWithdrawalsPerMonth = maxWithdrawalsPerMonth;
        this.withdrawalsThisMonth = 0;
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (withdrawalsThisMonth >= maxWithdrawalsPerMonth) {
            throw new InsufficientFundsException(amount, 0);
        }
        if (getBalance() < amount) {
            throw new InsufficientFundsException(amount, getBalance());
        }
        setBalance(getBalance() - amount);
        withdrawalsThisMonth++;
        getTransactions().add(new Transaction(Transaction.Type.WITHDRAWAL, LocalDateTime.now(), amount, getBalance()));

    }

    @Override
    public void applyMonthlyRules() {
        double interest = getBalance() * monthlyInterestRate;
        setBalance(getBalance() + interest);
        getTransactions().add(new Transaction(Transaction.Type.INTEREST, LocalDateTime.now(), interest, getBalance()));
        withdrawalsThisMonth = 0;
    }

    @Override
    public String getAccountType() {
        return "SavingsAccount";
    }

    public double getMonthlyInterestRate() {
        return monthlyInterestRate;
    }

    public void setMonthlyInterestRate(double monthlyInterestRate) {
        this.monthlyInterestRate = monthlyInterestRate;
    }

    public int getMaxWithdrawalsPerMonth() {
        return maxWithdrawalsPerMonth;
    }

    public int getWithdrawalsThisMonth() {
        return withdrawalsThisMonth;
    }
}
