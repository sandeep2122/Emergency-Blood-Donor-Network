import javax.swing.*;
import java.awt.*;

/** Main menu for the local Emergency Blood Donor Network demo. */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createMainWindow());
    }

    private static void createMainWindow() {
        JFrame frame = new JFrame("Emergency Blood Donor Network");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 610);
        frame.setLocationRelativeTo(null);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(Color.WHITE);

        JPanel menu = new JPanel();
        menu.setBackground(Color.WHITE);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("EMERGENCY BLOOD DONOR NETWORK");
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.setForeground(AppUI.RED);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel description = new JLabel("Educational demo - not a clinical matching service");
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        menu.add(heading);
        menu.add(Box.createVerticalStrut(12));
        menu.add(description);
        menu.add(Box.createVerticalStrut(30));

        addMenuButton(menu, "Donor Login", () -> new LoginPage());
        addMenuButton(menu, "Register as Donor", () -> new RegisterPage());
        addMenuButton(menu, "Search Donors", () -> new SearchDonorPage());
        addMenuButton(menu, "Emergency Blood Request", () -> new EmergencyRequestPage());
        addMenuButton(menu, "Admin Login", () -> new AdminLoginPage());

        background.add(menu);
        frame.add(background);
        frame.setVisible(true);
    }

    private static void addMenuButton(JPanel menu, String title, Runnable action) {
        JButton button = new JButton(title);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(330, 46));
        button.setPreferredSize(new Dimension(330, 46));
        button.addActionListener(event -> action.run());

        menu.add(button);
        menu.add(Box.createVerticalStrut(16));
    }
}
