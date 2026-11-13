import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/** Donor sign-in screen using password hashes stored in MySQL. */
public class LoginPage extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginPage() {
        AppUI.setup(this, "Donor Login", 450, 270);

        JPanel form = AppUI.form(3);
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        form.add(new JLabel("Username"));
        form.add(usernameField);
        form.add(new JLabel("Password"));
        form.add(passwordField);
        form.add(new JLabel(""));
        form.add(loginButton);

        loginButton.addActionListener(event -> loginDonor());

        add(form);
        setVisible(true);
    }

    private void loginDonor() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();

        if (username.isEmpty() || password.length == 0) {
            AppUI.info(this, "Please enter your username and password.");
            Arrays.fill(password, '\0');
            return;
        }

        String sql = "SELECT d.donor_id, d.full_name, a.password_hash "
                   + "FROM donor_accounts a "
                   + "JOIN donors d ON d.donor_id = a.donor_id "
                   + "WHERE a.username = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next() && PasswordUtil.verifyPassword(
                        password, result.getString("password_hash"))) {

                    int donorId = result.getInt("donor_id");
                    String donorName = result.getString("full_name");

                    dispose();
                    new DonorDashboard(donorId, donorName);
                } else {
                    AppUI.info(this, "Invalid username or password.");
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
