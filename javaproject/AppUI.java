import javax.swing.*;
import java.awt.*;
public final class AppUI {
    private AppUI() {
    }
    public static final Color RED = new Color(178, 36, 49);
    public static JPanel form(int rows) {
        JPanel p = new JPanel(new GridLayout(rows, 2, 12, 12));
        p.setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));
        return p;
    }
    public static void setup(JFrame f, String title, int w, int h) {
        f.setTitle(title + " | Emergency Blood Donor Network");
        f.setSize(w,h);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    public static void error(Component c, Exception e) {
        JOptionPane.showMessageDialog(c, "Operation failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    public static void info(Component c, String message) {
        JOptionPane.showMessageDialog(c, message);
    }
    public static String[] groups() {
        return new String[] {
            "A+","A-","B+","B-","AB+","AB-","O+","O-"
        };
    }
    public static boolean phone(String s) {
        return s.matches("[6-9][0-9]{9}");
    }
}
