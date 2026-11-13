import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Admin-only donor directory. Do not use it as a medically verified donor list. */
public class AdminDonorsPage extends JFrame {

    private final DefaultTableModel model;
    private final JTable donorTable;

    public AdminDonorsPage() {
        if (!AdminSession.active()) {
            throw new SecurityException("Admin login required");
        }

        AppUI.setup(this, "Admin Donor Directory", 850, 500);

        model = new DefaultTableModel(
            new String[] {"Donor ID", "Name", "Age", "Blood Group", "City"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        donorTable = new JTable(model);
        donorTable.setRowHeight(26);

        JButton refreshButton = new JButton("Refresh Donors");
        refreshButton.addActionListener(event -> loadDonors());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Registered donors (self-reported information)"));
        topPanel.add(refreshButton);

        JPanel container = new JPanel(new BorderLayout(12, 12));
        container.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        container.add(topPanel, BorderLayout.NORTH);
        container.add(new JScrollPane(donorTable), BorderLayout.CENTER);
        container.add(
            new JLabel("Donor eligibility and blood group require clinical verification."),
            BorderLayout.SOUTH
        );

        add(container);
        loadDonors();
        setVisible(true);
    }

    private void loadDonors() {
        if (!AdminSession.active()) {
            AppUI.info(this, "Please log in as an administrator.");
            dispose();
            return;
        }

        model.setRowCount(0);

        String query = "SELECT donor_id, full_name, age, blood_group, city "
                     + "FROM donors ORDER BY donor_id DESC LIMIT 500";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                model.addRow(new Object[] {
                    result.getInt("donor_id"),
                    result.getString("full_name"),
                    result.getInt("age"),
                    result.getString("blood_group"),
                    result.getString("city")
                });
            }
        } catch (SQLException exception) {
            AppUI.error(this, exception);
        }
    }
}
