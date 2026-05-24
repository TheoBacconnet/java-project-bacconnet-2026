package hr.fesb.java.bank;

import javax.swing.SwingUtilities;

/**
 * Application entry point.
 * Initialises the bank and launches the GUI.
 */
public class BankApp {

    public static void main(String[] args) {
        Bank bank = new Bank();

        SwingUtilities.invokeLater(() -> {
            BankGUI gui = new BankGUI(bank);
            gui.setVisible(true);
        });
    }
}