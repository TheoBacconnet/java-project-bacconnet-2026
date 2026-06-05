# Bank Account Management System

**Student:** Theo Emmanuel Bernard Bacconnet
**Course:** Programming in Java (FELP 11)
**University:** University of Split - FESB
**Academic Year:** 2025/2026
**Instructor:** Assoc. Prof. Vladimir Pleština, PhD

---

## Project Description

This is a Java desktop application simulating a simplified bank account management system. It was developed as the main project for the Programming in Java course at FESB.

The application allows a bank employee to manage customers and their accounts. All data is automatically saved to CSV files and reloaded every time the application starts, so no information is lost between sessions.

---

## Features

- Create customers and open accounts (Checking, Savings, Business)
- Deposit, withdraw, and transfer money between accounts
- View the full transaction history for any account
- Search customers by name or email
- Filter accounts by type and balance range
- Sort customers by name and accounts by balance
- Close accounts (marked as inactive, no further transactions allowed)
- Apply monthly rules (interest for savings accounts, withdrawal counter reset)
- Data persistence across sessions using CSV files

---

## Project Structure

```
java-project-bacconnet-2026/
├── src/
│   └── hr/fesb/java/bank/
│       ├── BankApp.java
│       ├── BankGUI.java
│       ├── Bank.java
│       ├── Account.java
│       ├── CheckingAccount.java
│       ├── SavingsAccount.java
│       ├── BusinessAccount.java
│       ├── Customer.java
│       ├── Transaction.java
│       ├── AccountFileManager.java
│       ├── Transactable.java
│       ├── Reportable.java
│       ├── InsufficientFundsException.java
│       └── AccountNotFoundException.java
├── data/
│   ├── customers.csv
│   ├── accounts.csv
│   └── transactions.csv
├── docs/
│   └── report.pdf
├── .gitignore
└── README.md
```

---

## How to Compile

From the root of the project:

```bash
mkdir -p bin
javac -cp src -d bin src/hr/fesb/java/bank/*.java
```

## How to Run

```bash
java -cp bin hr.fesb.java.bank.BankApp
```

The application will load any existing data from the `data/` folder and open the main window.

---

## Known Limitations

- Company names and VAT numbers cannot contain commas (CSV format constraint)
- The monthly rules must be applied manually via the File menu
- No multi-user support
- There is no way to edit a customer's information (email, phone) after they have been created