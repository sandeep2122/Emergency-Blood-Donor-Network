import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class DonorDashboard extends JFrame {
    private final int donorId;
    public DonorDashboard(int id,String name) {
        donorId=id;
        AppUI.setup(this,"Donor Dashboard",500,320);
        JPanel p=new JPanel(new GridLayout(5,1,10,10));
        p.setBorder(BorderFactory.createEmptyBorder(20,30,20,30));
        p.add(new JLabel("Welcome, "+name+" (Donor #"+id+")",SwingConstants.CENTER));
        JButton details=new JButton("View My Profile"), update=new JButton("Update My City / Phone"), search=new JButton("Search Donors"), close=new JButton("Logout");
        p.add(details);
        p.add(update);
        p.add(search);
        p.add(close);
        details.addActionListener(e-> {
            try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement("SELECT full_name,age,blood_group,phone,city FROM donors WHERE donor_id=?")) {
                st.setInt(1,donorId);
                try(ResultSet r=st.executeQuery()) {
                    if(r.next())AppUI.info(this,"Name: "+r.getString(1)+"\nAge: "+r.getInt(2)+"\nBlood group (self-reported): "+r.getString(3)+"\nPhone: "+r.getString(4)+"\nCity: "+r.getString(5));
                }
            }
            catch(SQLException ex) {
                AppUI.error(this,ex);
            }
        }
        );
        update.addActionListener(e-> {
            JTextField city=new JTextField(),phone=new JTextField();
            JPanel form=new JPanel(new GridLayout(2,2));
            form.add(new JLabel("New city"));
            form.add(city);
            form.add(new JLabel("New phone"));
            form.add(phone);
            if(JOptionPane.showConfirmDialog(this,form,"Update profile",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION)return;
            if(city.getText().trim().isEmpty()||!AppUI.phone(phone.getText().trim())) {
                AppUI.info(this,"Enter a city and valid 10-digit mobile.");
                return;
            }
            try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement("UPDATE donors SET city=?,phone=? WHERE donor_id=?")) {
                st.setString(1,city.getText().trim());
                st.setString(2,phone.getText().trim());
                st.setInt(3,donorId);
                st.executeUpdate();
                AppUI.info(this,"Profile updated.");
            }
            catch(SQLException ex) {
                AppUI.error(this,ex);
            }
        }
        );
        search.addActionListener(e->new SearchDonorPage());
        close.addActionListener(e->dispose());
        add(p);
        setVisible(true);
    }
}
