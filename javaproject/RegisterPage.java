import javax.swing.*;
import java.sql.*;
import java.util.Arrays;
public class RegisterPage extends JFrame {
    public RegisterPage() {
        AppUI.setup(this,"Donor Registration",520,530);
        JPanel p=AppUI.form(9);
        JTextField name=new JTextField(), age=new JTextField(), phone=new JTextField(), city=new JTextField(), username=new JTextField();
        JPasswordField password=new JPasswordField();
        JComboBox<String> group=new JComboBox<>(AppUI.groups());
        JCheckBox consent=new JCheckBox("I consent to local storage of my details");
        JButton save=new JButton("Register Donor");
        p.add(new JLabel("Full name"));
        p.add(name);
        p.add(new JLabel("Age"));
        p.add(age);
        p.add(new JLabel("Blood group (self-reported)"));
        p.add(group);
        p.add(new JLabel("Phone"));
        p.add(phone);
        p.add(new JLabel("City"));
        p.add(city);
        p.add(new JLabel("Login username"));
        p.add(username);
        p.add(new JLabel("Login password"));
        p.add(password);
        p.add(new JLabel("Consent"));
        p.add(consent);
        p.add(new JLabel(""));
        p.add(save);
        save.addActionListener(e-> {
            char[] pw=password.getPassword();
            try {
                int a=Integer.parseInt(age.getText().trim());
                if(a<18||a>100||name.getText().trim().isEmpty()||city.getText().trim().isEmpty()||!AppUI.phone(phone.getText().trim())||username.getText().trim().length()<3||pw.length<8||!consent.isSelected()) {
                    AppUI.info(this,"Check all fields: age 18-100, valid Indian mobile, username >=3, password >=8 and consent required.");
                    return;
                }
                String hash=PasswordUtil.hashPassword(pw);
                try(Connection c=DBConnection.getConnection()) {
                    c.setAutoCommit(false);
                    try {
                        int id;
                        try(PreparedStatement st=c.prepareStatement("INSERT INTO donors(full_name,age,blood_group,phone,city) VALUES(?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)) {
                            st.setString(1,name.getText().trim());
                            st.setInt(2,a);
                            st.setString(3,(String)group.getSelectedItem());
                            st.setString(4,phone.getText().trim());
                            st.setString(5,city.getText().trim());
                            st.executeUpdate();
                            try(ResultSet keys=st.getGeneratedKeys()) {
                                if(!keys.next())throw new SQLException("No donor ID returned");
                                id=keys.getInt(1);
                            }
                        }
                        try(PreparedStatement st=c.prepareStatement("INSERT INTO donor_accounts(donor_id,username,password_hash) VALUES(?,?,?)")) {
                            st.setInt(1,id);
                            st.setString(2,username.getText().trim());
                            st.setString(3,hash);
                            st.executeUpdate();
                        }
                        c.commit();
                        AppUI.info(this,"Registration successful. You can now log in.");
                        dispose();
                    }
                    catch(Exception ex) {
                        c.rollback();
                        throw ex;
                    }
                    finally {
                        c.setAutoCommit(true);
                    }
                }
            }
            catch(NumberFormatException ex) {
                AppUI.info(this,"Enter a valid numeric age.");
            }
            catch(Exception ex) {
                AppUI.error(this,ex);
            }
            finally {
                Arrays.fill(pw,'\0');
                password.setText("");
            }
        }
        );
        add(p);
        setVisible(true);
    }
}
