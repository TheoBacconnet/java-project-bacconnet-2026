package hr.fesb.java.bank;

import java.io.*;
import java.util.List;

public class AccountFileManager {
    private static final String DATA_DIR = "data";
    private static final String CUSTOMERS_FILE = DATA_DIR + "/customers.csv";
    private static final String ACCOUNTS_FILE = DATA_DIR + "/accounts.csv";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.csv";

    public void saveAll(List<Customer> customers) {
        new File(DATA_DIR).mkdirs();
        saveCustomers(customers);
        saveAccounts(customers);
        saveTransactions(customers);
    }

    private void saveCustomers(List<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer customer : customers) {
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

    private void saveAccounts(List<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (Customer customer : customers) {
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

    private void saveTransactions(List<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
            for (Customer customer : customers) {
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

    public void loadAll(List<Customer> customers) {
        customers.clear();
        loadCustomers(customers);
        loadAccounts(customers);
        loadTransactions(customers);
    }

    private void loadCustomers(List<Customer> customers) {
        File f = new File(CUSTOMERS_FILE);
        if (!f.exists())
            return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank())
                    continue;
                String[] p = line.split(",");
                customers.add(new Customer(p[0], p[1], p[2], p[3], p[4]));
            }
        } catch (IOException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
    }

    private void loadAccounts(List<Customer> customers) {
        File f = new File(ACCOUNTS_FILE);
        if (!f.exists())
            return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank())
                    continue;
                String[] p = line.split(",");
                String accId = p[0];
                String custId = p[1];
                String type = p[2];
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

                if (a == null)
                    continue;
                a.setBalanceDirectly(balance);
                a.setActive(active);

                for (Customer c : customers) {
                    if (c.getCustomerId().equals(custId)) {
                        c.addAccount(a);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }

    private void loadTransactions(List<Customer> customers) {
        File f = new File(TRANSACTIONS_FILE);
        if (!f.exists())
            return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank())
                    continue;
                int sep = line.indexOf('|');
                String accId = line.substring(0, sep);
                String txLine = line.substring(sep + 1);

                for (Customer c : customers) {
                    for (Account a : c.getAccounts()) {
                        if (a.getAccountId().equals(accId)) {
                            a.addTransactionDirectly(Transaction.fromCsvLine(txLine));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
    }

}
