import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/** Administrator login page. */
public class AdminLoginPage extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public AdminLoginPage() {
        AppUI.setup(this, "Admin Login", 450, 270);

        JPanel form = AppUI.form(3);
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Admin Login");

        form.add(new JLabel("Admin Username"));
        form.add(usernameField);
        form.add(new JLabel("Password"));
        form.add(passwordField);
        form.add(new JLabel(""));
        form.add(loginButton);

        loginButton.addActionListener(event -> loginAdmin());

        add(form);
        setVisible(true);
    }

    private void loginAdmin() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();

        String sql = "SELECT password_hash FROM admins WHERE username = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next() && PasswordUtil.verifyPassword(
                        password, result.getString("password_hash"))) {

                    AdminSession.login();
                    dispose();
                    new AdminDashboard();
                } else {
                    AppUI.info(this, "Invalid admin username or password.");
                }
            }
        } catch (SQLException exception) {
            AppUI.error(this, exception);
        } finally {
            Arrays.fill(password, '\0');
            passwordField.setText("");
        }
    }
}
