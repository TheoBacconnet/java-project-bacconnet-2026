package hr.fesb.java.bank;

public class BankApp {

    public static void main(String[] args) {
        /*
         * CheckingAccount checking = new CheckingAccount("A1001", "C1001", 1000.0,
         * -200.0);
         * SavingsAccount savings = new SavingsAccount("A1002", "C1001", 500.0, 0.05,
         * 3);
         * BusinessAccount business = new BusinessAccount("A1003", "C1002", 8000.0,
         * -5000.0,
         * "Acme Corp", "FR12345678901");
         * 
         * Customer alice = new Customer("C1001", "Alice", "Martin",
         * "alice@email.com", "0612345678");
         * Customer bob = new Customer("C1002", "Bob", "Dupont",
         * "bob@email.com", "0698765432");
         * 
         * alice.addAccount(checking);
         * alice.addAccount(savings);
         * bob.addAccount(business);
         * 
         * System.out.println("=== DEPOSIT ===");
         * checking.deposit(500.0);
         * System.out.println(checking.getSummary());
         * 
         * System.out.println("\n=== WITHDRAW ===");
         * try {
         * checking.withdraw(200.0);
         * } catch (InsufficientFundsException e) {
         * System.out.println(e.getMessage());
         * }
         * System.out.println(checking.getSummary());
         * 
         * System.out.println("\n=== OVERDRAFT ===");
         * try {
         * checking.withdraw(2000.0);
         * } catch (InsufficientFundsException e) {
         * System.out.println(e.getMessage());
         * }
         * System.out.println(checking.getSummary());
         * 
         * System.out.println("\n=== SAVINGS WITHDRAW LIMIT ===");
         * try {
         * savings.withdraw(50.0);
         * savings.withdraw(50.0);
         * savings.withdraw(50.0);
         * savings.withdraw(50.0); // 4th withdrawal should fail
         * } catch (InsufficientFundsException e) {
         * System.out.println(e.getMessage());
         * }
         * System.out.println(savings.getSummary());
         * 
         * System.out.println("\n=== MONTHLY RULES ===");
         * System.out.println("Before: " + savings.getSummary());
         * savings.applyMonthlyRules();
         * System.out.println("After:  " + savings.getSummary());
         * 
         * System.out.println("\n=== TRANSACTION HISTORY (checking) ===");
         * for (Transaction t : checking.getTransactions()) {
         * System.out.println(t);
         * }
         * 
         * System.out.println("\n=== CUSTOMERS ===");
         * System.out.println(alice);
         * System.out.println(bob);
         * 
         * System.out.println("\n=== VIA INTERFACES ===");
         * Transactable t1 = checking;
         * t1.deposit(100.0);
         * System.out.println("Balance via Transactable: " + t1.getBalance());
         * 
         * Reportable r1 = checking;
         * System.out.println("Summary via Reportable: " + r1.getSummary());
         */
        
        Bank bank = new Bank();

        System.out.println("=== CUSTOMERS LOADED ===");
        for (Customer c : bank.getAllCustomers()) {
            System.out.println(c);
        }

        System.out.println("\n=== ACCOUNTS LOADED ===");
        for (Account a : bank.getAllAccounts()) {
            System.out.println(a.getSummary());
        }

        System.out.println("\n=== TRANSACTIONS A1001 ===");
        try {
            Customer alice = bank.findCustomer("C1001");
            for (Account a : alice.getAccounts()) {
                if (a.getAccountId().equals("A1001")) {
                    for (Transaction t : a.getTransactions()) {
                        System.out.println(t);
                    }
                }
            }
        } catch (AccountNotFoundException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("\n=== DEPOSIT + SAVE ===");
        try {
            Customer alice = bank.findCustomer("C1001");
            Account checking = alice.getAccounts().get(0);
            bank.deposit(checking, 500.0);
            System.out.println("After deposit: " + checking.getSummary());
            bank.save();
            System.out.println("Data saved.");
        } catch (AccountNotFoundException e) {
            System.err.println(e.getMessage());
        }

    }
}