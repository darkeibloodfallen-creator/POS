import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class POS {
private static final String STOCK_FILE = "stocks.txt";

public static void main(String[] args) {
Scanner scanner = new Scanner(System.in);

String[] categories = {"\t\t\t\t\tCHIPS", "\t\t\t\t\tHYGIENE", "\t\t\t\t\t CANNED", "\t\t\t\t\tBEVERAGES", "\t\t\t\t\tBISCUITS"};
        int[][] productIds = {
                {101, 102, 103, 104, 105},
                {201, 202, 203, 204, 205},
                {301, 302, 303, 304, 305},
                {401, 402, 403, 404, 405},
                {501, 502, 503, 504, 505}
        };
        String[][] productNames = {
                {"Piattos", "Nova", "Clover", "V-Cut", "Chippy"},
                {"Safeguard", "Dove", "Palmolive", "Bioderm", "Silka"},
                {"Century Tuna", "Argentina Corned Beef", "555 Sardines", "Mega Sardines", "Maling"},
                {"Coke", "Sprite", "Royal", "C2", "Bottled Water"},
                {"Skyflakes", "Rebisco", "Hansel", "Fita", "Presto"}
        };
        int[][] prices = {
                {20, 20, 10, 25, 10},
                {45, 65, 40, 35, 38},
                {45, 42, 28, 30, 85},
                {25, 25, 25, 35, 15},
                {12, 10, 9, 13, 10}
        };
        // Default stocks - will be used when stocks.txt can't be found
        // shit wont work kapag wala rin, failsafe lang to
        int[][] stocks = {
                {20, 20, 20, 20, 20},
                {15, 15, 15, 15, 15},
                {12, 12, 12, 12, 12},
                {25, 25, 25, 25, 25},
                {30, 30, 30, 30, 30}
        };
        loadStocks(stocks);

    int totalAmount = 0;
    boolean running = true;
        while (running) {
            showProducts(categories, productIds, productNames, prices, stocks);
            System.out.print("\nEnter ID and amount (can be in pairs), or 0 to exit: ");
            String input = scanner.nextLine();
            if (input.equals("0")) {
                running = false;
                continue;
            }
            // error handling for invalid amounts/inputs
            if (input.isEmpty()) {
                System.out.println("Invalid: Enter pairs like 101 2 201 1");
                continue;
            }

            String[] values = input.split("\\s+");

            if (values.length % 2 != 0) {
                System.out.println("Invalid: Enter pairs like 101 2 201 1");
                continue;
            }
            for (int i = 0; i < values.length; i += 2) {
                int selectedId;
                int quantity;

                try {
                    selectedId = Integer.parseInt(values[i]);
                    quantity = Integer.parseInt(values[i + 1]);
                } catch (NumberFormatException e) {
                    System.out.println("Number invalid: Enter pairs like 101 2 201 1");
                    break;
                }

                int[] productLocation = findProductLocation(productIds, selectedId);
                int categoryIndex = productLocation[0];
                int productIndex = productLocation[1];

                if (categoryIndex == -1) {
                    System.out.println("ID " + selectedId + " is not available.");
                    continue;
                }

                if (quantity <= 0) {
                    System.out.println("Amount is invalid for " + productNames[categoryIndex][productIndex] + ".");
                    continue;
                }

                if (stocks[categoryIndex][productIndex] == 0) {
                    System.out.println(productNames[categoryIndex][productIndex] + " is out of stock.");
                    continue;
                }
                if (quantity > stocks[categoryIndex][productIndex]) {
                    System.out.println("Not enough stock for " + productNames[categoryIndex][productIndex] + ". Available stock: " + stocks[categoryIndex][productIndex]);
                    continue;
                }

                int subtotal = prices[categoryIndex][productIndex] * quantity;
                stocks[categoryIndex][productIndex] -= quantity;
                totalAmount += subtotal;

                System.out.println("\nPurchased: " + productNames[categoryIndex][productIndex]);
                System.out.println("Category: " + categories[categoryIndex]);
                System.out.println("Amount: " + quantity);
                System.out.println("Price each: " + prices[categoryIndex][productIndex]);
                System.out.println("Subtotal: " + subtotal);
                System.out.println("Remaining stock: " + stocks[categoryIndex][productIndex]);
            }

            saveStocks(stocks);
            System.out.println("Current total: " + totalAmount);
        }

        saveStocks(stocks);
        System.out.println("Total: " + totalAmount);
        System.out.println("Thank you for shopping at Triple T's Grocery store!");
        scanner.close();
    }
// header ng store + yung table, dito
    private static void showProducts(String[] categories, int[][] productIds, String[][] productNames, int[][] prices, int[][] stocks) {
        System.out.println("\nWelcome to Triple T's Grocery store!");
        System.out.println("Please follow the format of [ID] [AMOUNT] when purchasing!");

        for (int category = 0; category < categories.length; category++) {
            System.out.println("\n" + categories[category]);
            System.out.printf("%-6s %-24s %8s %8s%n", "ID", "Name", "Price", "Stock");
            System.out.println("——————————————————————————————————————————————————");

            for (int product = 0; product < productIds[category].length; product++) {
                System.out.printf("%-6d %-24s %8d %8d%n", productIds[category][product], productNames[category][product], prices[category][product], stocks[category][product]);
            }
        }
    }

    private static int[] findProductLocation(int[][] productIds, int selectedId) {
        for (int category = 0; category < productIds.length; category++) {
            for (int product = 0; product < productIds[category].length; product++) {
                if (productIds[category][product] == selectedId) {
                    return new int[]{category, product};
                }
            }
        }

        return new int[]{-1, -1};
    }

    private static void loadStocks(int[][] stocks) {
        File file = new File(STOCK_FILE);

        if (!file.exists()) {
            saveStocks(stocks);
            return;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            for (int category = 0; category < stocks.length; category++) {
                for (int product = 0; product < stocks[category].length; product++) {
                    if (fileScanner.hasNextInt()) {
                        stocks[category][product] = fileScanner.nextInt();
                    }
                }
            }
            // halos wala rin instance na mangyayari to, failsafe lng tlga ->>
        } catch (FileNotFoundException e) {
            System.out.println("Stocks file [stocks.txt] not found. Using default stocks instead.");
        }
    }

    private static void saveStocks(int[][] stocks) {
        try (PrintWriter writer = new PrintWriter(STOCK_FILE)) {
            for (int category = 0; category < stocks.length; category++) {
                for (int product = 0; product < stocks[category].length; product++) {
                    writer.print(stocks[category][product] + " ");
                }

                writer.println();
            } // ->> eto rin
        } catch (FileNotFoundException e) {
            System.out.println("Unable to save stocks.");
        }
    }
}