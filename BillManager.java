import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class BillManager {
    private final List<person> people = new ArrayList<>();

    // Add a person (ignore duplicates by case)
    public void addPerson(String name) {
        if (findPerson(name) != null) return;
        people.add(new person(name.trim()));
    }

    public person findPerson(String name) {
        for (person p : people) {
            if (p.getName().equalsIgnoreCase(name.trim())) return p;
        }
        return null;
    }

    public List<person> getPeople() { return people; }

    public void addExpense(String payerName, double amount) {
        person p = findPerson(payerName);
        if (p == null) {
            throw new IllegalArgumentException("Payer not found: " + payerName);
        }
        p.addExpense(amount);
    }

    public double totalExpenses() {
        double total = 0;
        for (person p : people) total += p.getAmountpaid();
        return total;
    }

    // Calculate share and set balances for everyone
    public double calculateShares() {
        if (people.isEmpty()) return 0;
        double total = totalExpenses();
        double share = total / people.size();
        // set balance = paid - share
        for (person p : people) {
            p.setBalance(round(p.getAmountpaid() - share));
        }
        return share;
    }

    // Debt simplification: return list of transactions like "A pays 12.34 to B"
    public List<String> settleDebts() {
        List<String> transactions = new ArrayList<>();

        List<person> creditors = new ArrayList<>();
        List<person> debtors  = new ArrayList<>();

        for (person p : people) {
            if (p.getBalance() > 0.005) creditors.add(p);
            else if (p.getBalance() < -0.005) debtors.add(p);
        }

        // Sort large creditors first, largest debtors (most negative) first
        creditors.sort((a, b) -> Double.compare(b.getBalance(), a.getBalance()));
        debtors.sort((a, b) -> Double.compare(a.getBalance(), b.getBalance())); // more negative first

        int i = 0, j = 0;
        while (i < debtors.size() && j < creditors.size()) {
            person debtor = debtors.get(i);
            person creditor = creditors.get(j);

            double debtAmt = -debtor.getBalance();
            double creditAmt = creditor.getBalance();

            double settled = round(Math.min(debtAmt, creditAmt));

            // update balances
            debtor.setBalance(round(debtor.getBalance() + settled));
            creditor.setBalance(round(creditor.getBalance() - settled));

            transactions.add(String.format("%s pays %.2f to %s",
                    debtor.getName(), settled, creditor.getName()));

            if (Math.abs(debtor.getBalance()) < 0.01) i++;
            if (Math.abs(creditor.getBalance()) < 0.01) j++;
        }

        return transactions;
    }

    // Save a human-readable summary and CSV of people
    public void saveSummary(String filename, List<String> transactions) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("----- Bill Summary -----");
            pw.printf("Total expense: %.2f%n", totalExpenses());
            pw.println();
            pw.println("Per-person details:");
            for (person p : people) {
                pw.printf("%s, Paid: %.2f, Balance: %.2f%n", p.getName(), p.getAmountpaid(), p.getBalance());
            }
            pw.println();
            pw.println("--- Settlements ---");
            for (String t : transactions) pw.println(t);
        }
    }

    // Save CSV for easy import to Excel
    public void saveCSV(String csvFilename) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFilename))) {
            pw.println("Name,Paid,Balance");
            for (person p : people) {
                pw.printf("%s,%.2f,%.2f%n", p.getName(), p.getAmountpaid(), p.getBalance());
            }
        }
    }

    // Utility rounding helper (2 decimals)
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

