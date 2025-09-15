
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Billsplitterapp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BillManager manager = new BillManager();
        System.out.println("=== Bill Splitter ===");

        // 1) Add people
        System.out.print("How many people? ");
        int n = readInt(sc);
        sc.nextLine();
        for (int i = 0; i < n; i++) {
            System.out.print("Enter name of person " + (i + 1) + ": ");
            manager.addPerson(sc.nextLine());
        }

        // 2) Enter expenses
        System.out.print("Enter number of expense entries: ");
        int e = readInt(sc);
        sc.nextLine();
        for (int i = 0; i < e; i++) {
            System.out.print("Who paid? ");
            String payer = sc.nextLine();
            System.out.print("Amount: ");
            double amt = readDouble(sc);
            sc.nextLine();
            try {
                manager.addExpense(payer, amt);
            } catch (IllegalArgumentException ex) {
                System.out.println("Error: " + ex.getMessage());
                i--; // retry this entry
            }
        }

        // 3) Calculate shares & settle
        double share = manager.calculateShares();
        System.out.printf("%nTotal expense: %.2f%nEach person's share: %.2f%n%n",
                manager.totalExpenses(), share);

        List<String> transactions = manager.settleDebts();

        System.out.println("--- Settlements ---");
        if (transactions.isEmpty()) System.out.println("No settlements needed. Everyone is even.");
        else transactions.forEach(System.out::println);

        // 4) Save output
        System.out.print("\nSave summary to file? (y/n): ");
        String save = sc.nextLine().trim();
        if (save.equalsIgnoreCase("y")) {
            try {
                manager.saveSummary("bill_summary.txt", transactions);
                manager.saveCSV("bill_summary.csv");
                System.out.println("Saved bill_summary.txt and bill_summary.csv");
            } catch (IOException ex) {
                System.out.println("Error saving files: " + ex.getMessage());
            }
        }

        System.out.println("\nDone. Goodbye!");
        sc.close();
    }

    private static int readInt(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.print("Enter a valid integer: ");
            sc.next();
        }
        return sc.nextInt();
    }

    private static double readDouble(Scanner sc) {
        while (!sc.hasNextDouble()) {
            System.out.print("Enter a valid number: ");
            sc.next();
        }
        return sc.nextDouble();
    }
}
