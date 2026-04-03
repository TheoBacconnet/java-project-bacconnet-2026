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
    public void withdraw(double amount) {
        if (withdrawalsThisMonth >= maxWithdrawalsPerMonth) {
            System.out.println("Error: maximum number of withdrawals for this month reached.");
            return;
        }
        if (getBalance() < amount) {
            System.out.println("Error: insufficient funds.");
            return;
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
