package hr.fesb.java.bank;

import java.time.LocalDateTime;

public class BusinessAccount extends Account{
    private double overdraftLimit;
    private String companyName;
    private String vatNumber;

    public BusinessAccount(String accountId, String customerId, double initialBalance, double overdraftLimit, String companyName, String vatNumber) {
        super(accountId, customerId, initialBalance);
        this.overdraftLimit = overdraftLimit;
        this.companyName = companyName;
        this.vatNumber = vatNumber;
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        if (getBalance() - amount < overdraftLimit) {
            throw new InsufficientFundsException(amount, getBalance() - overdraftLimit);
        }
        setBalance(getBalance() - amount);
        getTransactions().add(new Transaction(Transaction.Type.WITHDRAWAL, LocalDateTime.now(), amount, getBalance()));
    }

    @Override
    public void applyMonthlyRules() {
        // No specific monthly rules for business accounts
    }

    @Override
    public String getAccountType() {
        return "BusinessAccount";
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

}
