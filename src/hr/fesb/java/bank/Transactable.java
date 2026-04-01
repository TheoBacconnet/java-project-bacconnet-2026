package hr.fesb.java.bank;

public interface Transactable {
    void deposit(double amount);
    void withdraw(double amount);
    double getBalance();
}
