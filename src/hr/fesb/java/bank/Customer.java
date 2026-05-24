package hr.fesb.java.bank;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bank customer who can hold one or more accounts.
 */
public class Customer {

    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private List<Account> accounts;

    /**
     * Creates a new Customer.
     *
     * @param customerId unique customer identifier
     * @param firstName  first name
     * @param lastName   last name
     * @param email      email address
     * @param phone      phone number
     */
    public Customer(String customerId, String firstName, String lastName, String email, String phone) {
        this.customerId = customerId;
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.email      = email;
        this.phone      = phone;
        this.accounts   = new ArrayList<>();
    }

    /**
     * Adds an account to this customer's list.
     *
     * @param account account to add
     */
    public void addAccount(Account account) {
        accounts.add(account);
    }

    /** @return sum of balances across all accounts */
    public double getTotalBalance() {
        double total = 0;
        for (Account account : accounts)
            total += account.getBalance();
        return total;
    }

    /** @return unique customer ID */
    public String getCustomerId() { return customerId; }

    /** @return first name */
    public String getFirstName() { return firstName; }

    /** @return last name */
    public String getLastName() { return lastName; }

    /** @return full name (first + last) */
    public String getFullName() { return firstName + " " + lastName; }

    /** @return email address */
    public String getEmail() { return email; }

    /** @return phone number */
    public String getPhone() { return phone; }

    /** @return list of accounts */
    public List<Account> getAccounts() { return accounts; }

    /** @param firstName new first name */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /** @param lastName new last name */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /** @param email new email address */
    public void setEmail(String email) { this.email = email; }

    /** @param phone new phone number */
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return "Customer [" + customerId + "] " + getFullName()
                + " | Accounts: " + accounts.size()
                + " | Total: " + getTotalBalance() + " EUR";
    }
}