package hr.fesb.java.bank;

import java.util.List;

/**
 * Interface for objects that can produce a transaction report and a summary.
 */
public interface Reportable {

    /** @return list of all transactions */
    List<Transaction> getTransactions();

    /** @return a short summary string of the object */
    String getSummary();
}