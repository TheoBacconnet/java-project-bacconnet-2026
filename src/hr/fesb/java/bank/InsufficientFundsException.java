package hr.fesb.java.bank;

/**
 * Exception thrown when a withdrawal or transfer exceeds the available balance.
 */
public class InsufficientFundsException extends Exception {

    /**
     * @param requested amount requested
     * @param available amount actually available
     */
    public InsufficientFundsException(double requested, double available) {
        super("Insufficient funds: requested " + requested + " EUR but only " + available + " EUR available.");
    }
}