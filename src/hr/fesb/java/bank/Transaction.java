package hr.fesb.java.bank;

import java.time.LocalDateTime;

public class Transaction {

    public enum Type {
        DEPOSIT, WITHDRAWAL, INTEREST
    }

    private Type type;
    private LocalDateTime dateTime;
    private double amount;
    private double balanceAfter;

    public Transaction(Type type, LocalDateTime dateTime, double amount, double balanceAfter) {
        this.type = type;
        this.dateTime = dateTime;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return dateTime + " | " + type + " | " + amount + " EUR | balance: " + balanceAfter;
    }
}
