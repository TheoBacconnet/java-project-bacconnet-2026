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
    }

    private void saveAccounts(List<Customer> customers) {
    }

    private void saveTransactions(List<Customer> customers) {
    }
}
