package hr.fesb.java.bank;

import java.time.LocalDateTime;

/**
 * Business account with a higher overdraft limit and company information.
 */
public class BusinessAccount extends Account {

    private double overdraftLimit;
    private String companyName;
    private String vatNumber;

    /**
     * Creates a new BusinessAccount.
     *
     * @param accountId      unique account ID
     * @param customerId     owning customer ID
     * @param initialBalance opening balance
     * @param overdraftLimit maximum negative balance allowed (must be positive or zero)
     * @param companyName    name of the company
     * @param vatNumber      VAT registration number
     */
    public BusinessAccount(String accountId, String customerId, double initialBalance, double overdraftLimit,
            String companyName, String vatNumber) {
        super(accountId, customerId, initialBalance);
        this.overdraftLimit = overdraftLimit;
        this.companyName = companyName;
        this.vatNumber = vatNumber;
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
            throw new IllegalArgumentException("Amount must be positive.");
        if (!isActive())
            throw new IllegalStateException("Cannot withdraw from an inactive account.");
        if (getBalance() - amount < overdraftLimit)
            throw new InsufficientFundsException(amount, getBalance() - overdraftLimit);
        setBalance(getBalance() - amount);
        getTransactions().add(new Transaction(LocalDateTime.now(), Transaction.Type.WITHDRAWAL, amount, getBalance()));
    }

    /** No monthly rules apply to business accounts. */
    @Override
    public void applyMonthlyRules() {
    }

    /** @return "BusinessAccount" */
    @Override
    public String getAccountType() {
        return "BusinessAccount";
    }

    /** @return the overdraft limit */
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    /** @param overdraftLimit new overdraft limit */
    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    /** @return the company name */
    public String getCompanyName() {
        return companyName;
    }

    /** @param companyName new company name */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /** @return the VAT number */
    public String getVatNumber() {
        return vatNumber;
    }

    /** @param vatNumber new VAT number */
    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    /** @return CSV representation of the business-specific fields */
    @Override
    public String extraFieldsToCsv() {
        return overdraftLimit + "," + companyName + "," + vatNumber;
    }
}