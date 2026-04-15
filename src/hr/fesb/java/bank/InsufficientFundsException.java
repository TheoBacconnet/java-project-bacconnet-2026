package hr.fesb.java.bank;

public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(double requested, double available) {
        super("Insufficient funds: requested " + requested + " EUR but only " + available + " EUR available.");
    }

}
