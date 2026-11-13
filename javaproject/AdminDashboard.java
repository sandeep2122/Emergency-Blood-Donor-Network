import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Home screen accessible after successful administrator login. */
public class AdminDashboard extends JFrame {

    private final JLabel donorCountLabel;
    private final JLabel requestCountLabel;

    public AdminDashboard() {
        if (!AdminSession.active()) {
            throw new SecurityException("Admin login required");
        }

        AppUI.setup(this, "Admin Dashboard", 560, 450);

        JPanel content = new JPanel(new GridLayout(7, 1, 12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));

        JLabel heading = new JLabel("ADMIN DASHBOARD", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 24));
        heading.setForeground(AppUI.RED);

        donorCountLabel = new JLabel("Registered donors: --", SwingConstants.CENTER);
        requestCountLabel = new JLabel("Emergency requests: --", SwingConstants.CENTER);

        JButton refreshButton = new JButton("Refresh Statistics");
        JButton donorsButton = new JButton("View Registered Donors");
        JButton requestsButton = new JButton("Manage Emergency Requests");
        JButton logoutButton = new JButton("Logout");

        refreshButton.addActionListener(event -> refreshStatistics());
        donorsButton.addActionListener(event -> new AdminDonorsPage());
        requestsButton.addActionListener(event -> new ManageRequestsPage());
        logoutButton.addActionListener(event -> {
            AdminSession.logout();
            dispose();
        });

        content.add(heading);
        content.add(donorCountLabel);
        content.add(requestCountLabel);
        content.add(refreshButton);
        content.add(donorsButton);
        content.add(requestsButton);
        content.add(logoutButton);

        add(content);
        refreshStatistics();
        setVisible(true);
    }

    private void refreshStatistics() {
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {

            try (ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM donors")) {
                if (result.next()) {
                    donorCountLabel.setText("Registered donors: " + result.getInt(1));
                }
            }

            try (ResultSet result = statement.executeQuery(
                    "SELECT COUNT(*) FROM emergency_requests")) {
                if (result.next()) {
                    requestCountLabel.setText("Emergency requests: " + result.getInt(1));
                }
            }
        } catch (SQLException exception) {
            AppUI.error(this, exception);
        }
    }
}
