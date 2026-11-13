import javax.swing.*;
import java.sql.*;
public class EmergencyRequestPage extends JFrame {
    public EmergencyRequestPage() {
        AppUI.setup(this,"Emergency Blood Request",540,470);
        JPanel p=AppUI.form(8);
        JTextField patient=new JTextField(),units=new JTextField(),hospital=new JTextField(),city=new JTextField(),contact=new JTextField();
        JComboBox<String> group=new JComboBox<>(AppUI.groups());
        JCheckBox consent=new JCheckBox("I have permission to submit these details");
        JButton submit=new JButton("Submit Request");
        p.add(new JLabel("Patient name"));
        p.add(patient);
        p.add(new JLabel("Blood group (reported)"));
        p.add(group);
        p.add(new JLabel("Units requested"));
        p.add(units);
        p.add(new JLabel("Hospital"));
        p.add(hospital);
        p.add(new JLabel("City"));
        p.add(city);
        p.add(new JLabel("Contact number"));
        p.add(contact);
        p.add(new JLabel("Permission"));
        p.add(consent);
        p.add(new JLabel(""));
        p.add(submit);
        submit.addActionListener(e-> {
            try {
                int n=Integer.parseInt(units.getText().trim());
                if(n<1||n>100||patient.getText().trim().isEmpty()||hospital.getText().trim().isEmpty()||city.getText().trim().isEmpty()||!AppUI.phone(contact.getText().trim())||!consent.isSelected()) {
                    AppUI.info(this,"Complete all fields, units 1-100, valid mobile and permission required.");
                    return;
                }
                try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement("INSERT INTO emergency_requests(patient_name,blood_group,units_required,hospital_name,city,contact_number) VALUES(?,?,?,?,?,?)")) {
                    st.setString(1,patient.getText().trim());
                    st.setString(2,(String)group.getSelectedItem());
                    st.setInt(3,n);
                    st.setString(4,hospital.getText().trim());
                    st.setString(5,city.getText().trim());
                    st.setString(6,contact.getText().trim());
                    st.executeUpdate();
                    AppUI.info(this,"Request recorded as PENDING. This does NOT arrange blood or notify a hospital. For emergencies contact a licensed hospital immediately.");
                    dispose();
                }
            }
            catch(NumberFormatException ex) {
                AppUI.info(this,"Units must be a number.");
            }
            catch(SQLException ex) {
                AppUI.error(this,ex);
            }
        }
        );
        add(p);
        setVisible(true);
    }
}
