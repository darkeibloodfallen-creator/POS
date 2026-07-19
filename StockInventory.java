import java.io.*;
import java.util.*;

public class StockInventory {
    private static final String FILE_NAME = "stocks.txt";

    public static void main(String[] args) {
        List<String> stocks = loadStocks();

        System.out.println("Current stocks: " + stocks);

        // Example: remove a stock
        String toRemove = "AAPL";
        if (stocks.remove(toRemove)) {
            System.out.println(toRemove + " removed.");
        } else {
            System.out.println(toRemove + " not found.");
        }

        saveStocks(stocks);
        System.out.println("Updated stocks: " + stocks);
    }

    // Load stock list from file into memory
    private static List<String> loadStocks() {
        List<String> stocks = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            // First run — create some default data
            stocks.addAll(Arrays.asList("AAPL", "GOOG", "MSFT", "TSLA"));
            saveStocks(stocks);
            return stocks;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    stocks.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading stocks: " + e.getMessage());
        }

        return stocks;
    }

    // Save current stock list back to file
    private static void saveStocks(List<String> stocks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String stock : stocks) {
                writer.write(stock);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving stocks: " + e.getMessage());
        }
    }
}