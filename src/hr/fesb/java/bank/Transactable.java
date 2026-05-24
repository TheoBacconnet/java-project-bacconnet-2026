package hr.fesb.java.bank;

/**
 * Interface defining the core financial operations of a bank account.
 */
public interface Transactable {

    /** @param amount amount to deposit (must be positive) */
    void deposit(double amount);

    /**
     * @param amount amount to withdraw (must be positive)
     * @throws InsufficientFundsException if balance is insufficient
     */
    void withdraw(double amount) throws InsufficientFundsException;

    /** @return current balance */
    double getBalance();
}