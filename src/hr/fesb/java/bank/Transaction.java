package hr.fesb.java.bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable value object representing a single bank transaction.
 */
public class Transaction {

    /** Type of transaction. */
    public enum Type {
        DEPOSIT, WITHDRAWAL, INTEREST
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Type type;
    private LocalDateTime dateTime;
    private double amount;
    private double balanceAfter;

    /**
     * Creates a new Transaction.
     *
     * @param dateTime     timestamp of the transaction
     * @param type         transaction type
     * @param amount       amount involved (always positive)
     * @param balanceAfter account balance after this transaction
     */
    public Transaction(LocalDateTime dateTime, Type type, double amount, double balanceAfter) {
        this.dateTime     = dateTime;
        this.type         = type;
        this.amount       = amount;
        this.balanceAfter = balanceAfter;
    }

    /** @return transaction timestamp */
    public LocalDateTime getDateTime() { return dateTime; }

    /** @return transaction amount */
    public double getAmount() { return amount; }

    /** @return account balance after this transaction */
    public double getBalanceAfter() { return balanceAfter; }

    /** @return transaction type */
    public Type getType() { return type; }

    /** @return formatted date-time string (yyyy-MM-dd HH:mm:ss) */
    public String getFormattedDateTime() { return dateTime.format(FORMATTER); }

    /**
     * Serialises this transaction to a pipe-separated CSV line.
     *
     * @return CSV line representation
     */
    public String toCsvLine() {
        return getFormattedDateTime() + "," + type + "," + amount + "," + balanceAfter;
    }

    /**
     * Deserialises a Transaction from a CSV line produced by {@link #toCsvLine()}.
     *
     * @param line CSV line to parse
     * @return reconstructed Transaction
     */
    public static Transaction fromCsvLine(String line) {
        String[] p = line.split(",");
        return new Transaction(
                LocalDateTime.parse(p[0], FORMATTER),
                Type.valueOf(p[1]),
                Double.parseDouble(p[2]),
                Double.parseDouble(p[3]));
    }

    @Override
    public String toString() {
        return getFormattedDateTime() + " | " + type + " | " + amount + " EUR | balance: " + balanceAfter;
    }
}