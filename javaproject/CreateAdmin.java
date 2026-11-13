import java.sql.*;
import java.io.Console;
import java.util.Arrays;
public class CreateAdmin {
    public static void main(String[] args) {
        Console console = System.console();
        if (console == null) {
            System.out.println( "Please run this program from the terminal." );
            return;
        }
        String url = "jdbc:mysql://localhost:3306/blood_donor_db";
        String username = console.readLine( "Enter new admin username: " ).trim();
        if (!username.matches("[A-Za-z0-9_]{3,50}")) {
            System.out.println("Invalid username.");
            return;
        }
        char[] adminPassword = console.readPassword( "Create admin password: " );
        if (adminPassword == null || adminPassword.length < 12) {
            System.out.println( "Password must have at least 12 characters." );
            if (adminPassword != null) {
                Arrays.fill(adminPassword, '\0');
            }
            return;
        }
        char[] dbPassword = console.readPassword( "Enter local MySQL password: " );
        if (dbPassword == null) {
            Arrays.fill(adminPassword, '\0');
            return;
        }
        String sql = "INSERT INTO admins (username, password_hash) " + "VALUES (?, ?)";
        try {
            String hash = PasswordUtil.hashPassword( adminPassword );
            try ( Connection con = DriverManager.getConnection( url, "root", new String(dbPassword) ); PreparedStatement pst = con.prepareStatement(sql) ) {
                pst.setString(1, username);
                pst.setString(2, hash);
                pst.executeUpdate();
                System.out.println( "Admin Account Created Successfully!" );
            }
        }
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.println( "This admin username already exists." );
        }
        catch (Exception e) {
            System.out.println( "Admin creation failed: " + e.getMessage() );
        }
        finally {
            Arrays.fill(adminPassword, '\0');
            Arrays.fill(dbPassword, '\0');
        }
    }
}
