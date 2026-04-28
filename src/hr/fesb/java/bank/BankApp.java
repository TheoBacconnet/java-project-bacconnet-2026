package hr.fesb.java.bank;

import javax.swing.SwingUtilities;

public class BankApp {

    /*public static void main(String[] args) {

        Bank bank = new Bank();

        System.out.println("=== CUSTOMERS ===");
        for (Customer c : bank.getAllCustomers()) {
            System.out.println(c);
        }

        System.out.println("\n=== ACCOUNTS ===");
        for (Account a : bank.getAllAccounts()) {
            System.out.println(a.getSummary());
        }

        System.out.println("\n=== DEPOSIT ===");
        try {
            Customer alice   = bank.findCustomer("C1001");
            Account checking = alice.getAccounts().get(0);
            bank.deposit(checking, 300.0);
            System.out.println(checking.getSummary());
        } catch (AccountNotFoundException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("\n=== WITHDRAW ===");
        try {
            Customer alice   = bank.findCustomer("C1001");
            Account checking = alice.getAccounts().get(0);
            bank.withdraw(checking, 100.0);
            System.out.println(checking.getSummary());
        } catch (AccountNotFoundException | InsufficientFundsException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("\n=== OVERDRAFT ===");
        try {
            Customer alice   = bank.findCustomer("C1001");
            Account checking = alice.getAccounts().get(0);
            bank.withdraw(checking, 99999.0);
        } catch (AccountNotFoundException | InsufficientFundsException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("\n=== TRANSFER ===");
        try {
            Customer alice   = bank.findCustomer("C1001");
            Customer bob     = bank.findCustomer("C1002");
            Account  from    = alice.getAccounts().get(0);
            Account  to      = bob.getAccounts().get(0);
            bank.transfer(from, to, 200.0);
            System.out.println("From: " + from.getSummary());
            System.out.println("To:   " + to.getSummary());
        } catch (AccountNotFoundException | InsufficientFundsException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("\n=== CLOSE ACCOUNT ===");
        try {
            Customer carla  = bank.findCustomer("C1003");
            Account account = carla.getAccounts().get(0);
            bank.closeAccount(account);
            System.out.println(account.getSummary());
            bank.withdraw(account, 50.0); // doit afficher une erreur
        } catch (AccountNotFoundException | InsufficientFundsException e) {
            System.err.println(e.getMessage());
        }

        // ── Tester search/filter ──────────────────────────────────────────
        System.out.println("\n=== SEARCH BY NAME ===");
        for (Account a : bank.searchByCustomerName("alice")) {
            System.out.println(a.getSummary());
        }

        System.out.println("\n=== FILTER BY TYPE ===");
        for (Account a : bank.filterByType("CheckingAccount")) {
            System.out.println(a.getSummary());
        }

        System.out.println("\n=== FILTER BY BALANCE (500 - 5000) ===");
        for (Account a : bank.filterByBalanceRange(500.0, 5000.0)) {
            System.out.println(a.getSummary());
        }

        System.out.println("\n=== MONTHLY RULES ===");
        Customer alice;
        try {
            alice = bank.findCustomer("C1001");
            Account savings = alice.getAccounts().get(1);
            System.out.println("Before: " + savings.getSummary());
            bank.applyMonthlyRules();
            System.out.println("After:  " + savings.getSummary());
        } catch (AccountNotFoundException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("\n=== SAVE ===");
        bank.save();
        System.out.println("Data saved. Restart to verify persistence.");
    }*/

        public static void main(String[] args){
            Bank bank = new Bank();

            SwingUtilities.invokeLater(() -> {
                BankGUI gui = new BankGUI(bank);
                gui.setVisible(true);
            });
        }
}