package hr.fesb.java.bank;

public abstract class Account {
    private String accountId;
    private String customerId;
    private double balance;
    private boolean active;

    protected Account(String accountId, String customerId, double initialBalance) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.balance = initialBalance;
        this.active = true;
    }

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

    public void deposit(double amount) {
        if (amount > 0 && active) {
            balance += amount;
        }
    }

    public String getSummary() {
        return String.format("Account [%s] | %s | Balance: %.2f", accountId, getAccountType(), balance);
    }

    public abstract void applyMonthlyRules();
    public abstract void withdraw(double amount);
    public abstract String getAccountType();

}