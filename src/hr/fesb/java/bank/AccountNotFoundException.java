package hr.fesb.java.bank;

public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(String accountNumber) {
        super("Account with number " + accountNumber + " not found.");
    }

}
