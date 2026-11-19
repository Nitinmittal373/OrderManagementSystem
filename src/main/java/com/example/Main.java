
import java.sql.*;
import java.util.Scanner;

public class Main {

    // MySQL database configuration
    private static final String URL =
            "jdbc:mysql://localhost:3306/ecommerce_db";

    private static final String USER = "root";

    private static final String PASSWORD = "Nitin@123";

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        try {
            // Establish database connection
            Connection con = DriverManager.getConnection(
                    URL, USER, PASSWORD
            );

            System.out.println("===============================");
            System.out.println("     ORDER MANAGEMENT SYSTEM");
            System.out.println("===============================");
            System.out.println("Database Connected Successfully!");

            int choice;

            do {
                System.out.println("\n========== MAIN MENU ==========");
                System.out.println("1. Insert Order Records");
                System.out.println("2. Retrieve Orders by Status");
                System.out.println("3. Update Order Quantity");
                System.out.println("4. Search and Update Order Status");
                System.out.println("5. Display Total Order Value and Delete");
                System.out.println("6. Exit");

                System.out.print("Enter your choice: ");
                choice = sc.nextInt();

                switch (choice) {

                    case 1:
                        insertOrders(con);
                        break;

                    case 2:
                        retrieveOrders(con);
                        break;

                    case 3:
                        updateQuantity(con);
                        break;

                    case 4:
                        updateOrderStatus(con);
                        break;

                    case 5:
                        totalOrderValueAndDelete(con);
                        break;

                    case 6:
                        System.out.println("Thank you for using the system!");
                        break;

                    default:
                        System.out.println("Invalid choice!");
                }

            } while (choice != 6);

            con.close();
            sc.close();

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // (A) INSERT AT LEAST THREE ORDER RECORDS

    public static void insertOrders(Connection con) {

        String query = "INSERT INTO `Orders` " +
                "(order_id, customer_name, product_name, quantity, unit_price, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(query)) {

            for (int i = 1; i <= 3; i++) {

                System.out.println("\nEnter Order Details " + i);

                System.out.print("Order ID: ");
                int id = sc.nextInt();
                sc.nextLine();

                System.out.print("Customer Name: ");
                String customer = sc.nextLine();

                System.out.print("Product Name: ");
                String product = sc.nextLine();

                System.out.print("Quantity: ");
                int quantity = sc.nextInt();

                System.out.print("Unit Price: ");
                double price = sc.nextDouble();
                sc.nextLine();

                System.out.print("Status: ");
                String status = sc.nextLine();

                if (quantity <= 0 || price <= 0) {
                    System.out.println("Invalid quantity or price!");
                    continue;
                }

                ps.setInt(1, id);
                ps.setString(2, customer);
                ps.setString(3, product);
                ps.setInt(4, quantity);
                ps.setDouble(5, price);
                ps.setString(6, status);

                try {
                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        System.out.println("Order inserted successfully!");
                    }

                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Duplicate Order ID! Record not inserted.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Insert Error: " + e.getMessage());
        }
    }

    // (B) CONDITIONAL RETRIEVAL AND SORTING

    public static void retrieveOrders(Connection con) {

        String query = "SELECT * FROM `Orders` " +
                "WHERE status = ? AND quantity > 2 " +
                "ORDER BY unit_price DESC";

        try (PreparedStatement ps = con.prepareStatement(query)) {

            sc.nextLine();

            System.out.print("Enter order status: ");
            String status = sc.nextLine();

            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                System.out.println("\nMatching Orders:");

                while (rs.next()) {

                    found = true;

                    System.out.println("---------------------------");

                    System.out.println("Order ID: " +
                            rs.getInt("order_id"));

                    System.out.println("Customer Name: " +
                            rs.getString("customer_name"));

                    System.out.println("Product Name: " +
                            rs.getString("product_name"));

                    System.out.println("Quantity: " +
                            rs.getInt("quantity"));

                    System.out.println("Unit Price: " +
                            rs.getDouble("unit_price"));

                    System.out.println("Status: " +
                            rs.getString("status"));
                }

                if (!found) {
                    System.out.println("No matching orders found!");
                }
            }

        } catch (SQLException e) {
            System.out.println("Retrieval Error: " + e.getMessage());
        }
    }

    // (C) INCREASE EXISTING ORDER QUANTITY

    public static void updateQuantity(Connection con) {

        String query = "UPDATE `Orders` " +
                "SET quantity = quantity + ? " +
                "WHERE order_id = ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {

            System.out.print("Enter Order ID: ");
            int id = sc.nextInt();

            System.out.print("Enter additional quantity: ");
            int additionalQty = sc.nextInt();

            if (additionalQty <= 0) {
                System.out.println("Additional quantity must be positive!");
                return;
            }

            ps.setInt(1, additionalQty);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Order quantity updated successfully!");
            } else {
                System.out.println("Order does not exist!");
            }

        } catch (SQLException e) {
            System.out.println("Quantity Update Error: " + e.getMessage());
        }
    }

    // (D) SEARCH AND UPDATE ORDER STATUS

    public static void updateOrderStatus(Connection con) {

        try {

            System.out.print("Enter Order ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            String searchQuery =
                    "SELECT * FROM `Orders` WHERE order_id = ?";

            try (PreparedStatement searchPs =
                         con.prepareStatement(searchQuery)) {

                searchPs.setInt(1, id);

                try (ResultSet rs = searchPs.executeQuery()) {

                    if (rs.next()) {

                        System.out.println("\nOrder Found!");

                        System.out.println("Order ID: " +
                                rs.getInt("order_id"));

                        System.out.println("Customer Name: " +
                                rs.getString("customer_name"));

                        System.out.println("Product Name: " +
                                rs.getString("product_name"));

                        System.out.println("Quantity: " +
                                rs.getInt("quantity"));

                        System.out.println("Unit Price: " +
                                rs.getDouble("unit_price"));

                        System.out.println("Current Status: " +
                                rs.getString("status"));

                    } else {
                        System.out.println("Order does not exist!");
                        return;
                    }
                }
            }

            System.out.print("\nEnter New Status (Pending/Shipped/Delivered): ");
            String newStatus = sc.nextLine().trim();

            // Validate new status
            if (!newStatus.equalsIgnoreCase("Pending") &&
                    !newStatus.equalsIgnoreCase("Shipped") &&
                    !newStatus.equalsIgnoreCase("Delivered")) {

                System.out.println("Invalid Status!");
                System.out.println(
                        "Allowed statuses: Pending, Shipped, Delivered"
                );
                return;
            }

            // Standardize status format
            newStatus = newStatus.substring(0, 1).toUpperCase()
                    + newStatus.substring(1).toLowerCase();

            String updateQuery =
                    "UPDATE `Orders` SET status = ? WHERE order_id = ?";

            try (PreparedStatement updatePs =
                         con.prepareStatement(updateQuery)) {

                updatePs.setString(1, newStatus);
                updatePs.setInt(2, id);

                updatePs.executeUpdate();

                System.out.println("Order status updated successfully!");
            }

            // Display updated order details
            String displayQuery =
                    "SELECT * FROM `Orders` WHERE order_id = ?";

            try (PreparedStatement displayPs =
                         con.prepareStatement(displayQuery)) {

                displayPs.setInt(1, id);

                try (ResultSet updatedRs = displayPs.executeQuery()) {

                    if (updatedRs.next()) {

                        System.out.println("\nUPDATED ORDER DETAILS");

                        System.out.println("Order ID: " +
                                updatedRs.getInt("order_id"));

                        System.out.println("Customer Name: " +
                                updatedRs.getString("customer_name"));

                        System.out.println("Product Name: " +
                                updatedRs.getString("product_name"));

                        System.out.println("Quantity: " +
                                updatedRs.getInt("quantity"));

                        System.out.println("Unit Price: " +
                                updatedRs.getDouble("unit_price"));

                        System.out.println("New Status: " +
                                updatedRs.getString("status"));
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Status Update Error: " + e.getMessage());
        }
    }

    // (E) AGGREGATE QUERY AND CONDITIONAL DELETION

    public static void totalOrderValueAndDelete(Connection con) {

        String sumQuery =
                "SELECT COALESCE(SUM(quantity * unit_price), 0) " +
                        "AS total_value FROM `Orders`";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sumQuery)) {

            if (rs.next()) {

                System.out.println("\nTotal Order Value: Rs. " +
                        rs.getDouble("total_value"));
            }

        } catch (SQLException e) {
            System.out.println("Aggregate Error: " + e.getMessage());
            return;
        }

        System.out.print("\nEnter Order ID to delete: ");
        int id = sc.nextInt();

        String deleteQuery =
                "DELETE FROM `Orders` " +
                        "WHERE order_id = ? AND LOWER(status) = 'cancelled'";

        try (PreparedStatement ps = con.prepareStatement(deleteQuery)) {

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {

                System.out.println("Order deleted successfully!");

            } else {

                String checkQuery =
                        "SELECT status FROM `Orders` WHERE order_id = ?";

                try (PreparedStatement checkPs =
                             con.prepareStatement(checkQuery)) {

                    checkPs.setInt(1, id);

                    try (ResultSet checkRs = checkPs.executeQuery()) {

                        if (checkRs.next()) {

                            System.out.println(
                                    "Order cannot be deleted! " +
                                            "Status must be Cancelled."
                            );

                        } else {

                            System.out.println("Order does not exist!");
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Deletion Error: " + e.getMessage());
        }
    }
}
