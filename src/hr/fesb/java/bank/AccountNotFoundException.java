package hr.fesb.java.bank;

/**
 * Exception thrown when an account or customer lookup by ID fails.
 */
public class AccountNotFoundException extends Exception {

    /**
     * @param accountNumber the ID that was not found
     */
    public AccountNotFoundException(String accountNumber) {
        super("Account with number " + accountNumber + " not found.");
    }
}