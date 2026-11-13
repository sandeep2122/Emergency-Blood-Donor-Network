import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Shared database connection used by every screen. */
public final class DBConnection {

    private static final String DATABASE_URL =
            "jdbc:mysql://localhost:3306/blood_donor_db";

    private static final String DEFAULT_DATABASE_USER = "blood_app";

    private DBConnection() {
        // Utility class: do not create instances.
    }

    public static Connection getConnection() throws SQLException {
        String username = System.getenv("BLOOD_DB_USER");

        if (username == null || username.isBlank()) {
            username = DEFAULT_DATABASE_USER;
        }

        String password = System.getenv("BLOOD_DB_PASSWORD");

        if (password == null || password.isEmpty()) {
            throw new SQLException(
                "BLOOD_DB_PASSWORD is missing. Set it in the terminal first."
            );
        }

        return DriverManager.getConnection(
            DATABASE_URL,
            username,
            password
        );
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            if (!connection.isClosed()) {
                System.out.println("Database Connection Successful!");
            }
        } catch (SQLException exception) {
            System.err.println("Connection failed: " + exception.getMessage());
        }
    }
}
