package hr.fesb.java.bank;

import java.io.*;
import java.util.Map;

/**
 * Handles all file I/O for the application.
 * Saves and loads customers, accounts and transactions using CSV files in the data/ folder.
 */
public class AccountFileManager {

    private static final String DATA_DIR          = "data";
    private static final String CUSTOMERS_FILE    = DATA_DIR + "/customers.csv";
    private static final String ACCOUNTS_FILE     = DATA_DIR + "/accounts.csv";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.csv";

    // ── Save ──────────────────────────────────────────────────────────────

    /**
     * Saves all customers, accounts and transactions to CSV files.
     *
     * @param customers map of all customers
     */
    public void saveAll(Map<String, Customer> customers) {
        new File(DATA_DIR).mkdirs();
        saveCustomers(customers);
        saveAccounts(customers);
        saveTransactions(customers);
    }

    private void saveCustomers(Map<String, Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer customer : customers.values()) {
                writer.write(String.format("%s,%s,%s,%s,%s%n",
                        customer.getCustomerId(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail(),
                        customer.getPhone()));
            }
        } catch (IOException e) {
            System.err.println("Error saving customers: " + e.getMessage());
        }
    }

    private void saveAccounts(Map<String, Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (Customer customer : customers.values()) {
                for (Account account : customer.getAccounts()) {
                    writer.write(String.format("%s,%s,%s,%s,%s,%s%n",
                            account.getAccountId(),
                            customer.getCustomerId(),
                            account.getAccountType(),
                            account.getBalance(),
                            account.isActive(),
                            account.extraFieldsToCsv()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        }
    }

    private void saveTransactions(Map<String, Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
            for (Customer customer : customers.values()) {
                for (Account account : customer.getAccounts()) {
                    for (Transaction transaction : account.getTransactions()) {
                        writer.write(String.format("%s|%s%n",
                                account.getAccountId(),
                                transaction.toCsvLine()));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }

    // ── Load ──────────────────────────────────────────────────────────────

    /**
     * Loads all customers, accounts and transactions from CSV files.
     *
     * @param customers map to populate with loaded customers
     * @param accounts  map to populate with loaded accounts
     */
    public void loadAll(Map<String, Customer> customers, Map<String, Account> accounts) {
        customers.clear();
        accounts.clear();
        loadCustomers(customers);
        loadAccounts(customers, accounts);
        loadTransactions(accounts);
    }

    private void loadCustomers(Map<String, Customer> customers) {
        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                Customer c = new Customer(p[0], p[1], p[2], p[3], p[4]);
                customers.put(c.getCustomerId(), c);
            }
        } catch (FileNotFoundException e) {
            System.err.println("No existing customers found, starting fresh.");
        } catch (IOException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
    }

    private void loadAccounts(Map<String, Customer> customers, Map<String, Account> accounts) {
        try (BufferedReader br = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p     = line.split(",");
                String accId   = p[0];
                String custId  = p[1];
                String type    = p[2];
                double balance = Double.parseDouble(p[3]);
                boolean active = Boolean.parseBoolean(p[4]);

                Account a = null;
                switch (type) {
                    case "CheckingAccount":
                        a = new CheckingAccount(accId, custId, 0,
                                Double.parseDouble(p[5]));
                        break;
                    case "SavingsAccount":
                        a = new SavingsAccount(accId, custId, 0,
                                Double.parseDouble(p[5]),
                                Integer.parseInt(p[6]));
                        break;
                    case "BusinessAccount":
                        a = new BusinessAccount(accId, custId, 0,
                                Double.parseDouble(p[5]),
                                p[6],
                                p[7]);
                        break;
                }

                if (a == null) continue;
                a.setBalanceDirectly(balance);
                a.setActive(active);

                Customer c = customers.get(custId);
                if (c != null) c.addAccount(a);
                accounts.put(accId, a);
            }
        } catch (FileNotFoundException e) {
            System.err.println("No existing accounts found, starting fresh.");
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }

    private void loadTransactions(Map<String, Account> accounts) {
        try (BufferedReader br = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                int sep       = line.indexOf('|');
                String accId  = line.substring(0, sep);
                String txLine = line.substring(sep + 1);

                Account a = accounts.get(accId);
                if (a != null) {
                    a.addTransactionDirectly(Transaction.fromCsvLine(txLine));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("No existing transactions found, starting fresh.");
        } catch (IOException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
    }
}