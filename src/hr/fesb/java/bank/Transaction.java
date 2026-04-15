package hr.fesb.java.bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

    public enum Type {
        DEPOSIT, WITHDRAWAL, INTEREST
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Type type;
    private LocalDateTime dateTime;
    private double amount;
    private double balanceAfter;

    public Transaction(LocalDateTime dateTime, Type type, double amount, double balanceAfter) {
        this.dateTime = dateTime;
        this.type = type;
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

    public String getFormattedDateTime() {
        return dateTime.format(FORMATTER);
    }

    public String toCsvLine() {
        return getFormattedDateTime() + "|" + type + "|" + amount + "|" + balanceAfter;
    }

    public static Transaction fromCsvLine(String line) {
        String[] p = line.split("\\|");
        return new Transaction(LocalDateTime.parse(p[0], FORMATTER), Type.valueOf(p[1]), Double.parseDouble(p[2]),
                Double.parseDouble(p[3]));

    }

    @Override
    public String toString() {
        return getFormattedDateTime() + " | " + type + " | " + amount + " EUR | balance: " + balanceAfter;
    }
}
