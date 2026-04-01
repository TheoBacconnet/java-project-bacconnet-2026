package hr.fesb.java.bank;

import java.util.List;

public interface Reportable {
    List<Transaction> getTransactions();
    String getSummary();
}
