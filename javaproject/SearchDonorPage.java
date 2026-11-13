import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
public class SearchDonorPage extends JFrame {
    public SearchDonorPage() {
        AppUI.setup(this,"Search Donors",750,470);
        JPanel p=new JPanel(new BorderLayout(12,12));
        p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        JPanel filters=new JPanel(new FlowLayout());
        JComboBox<String> group=new JComboBox<>(AppUI.groups());
        JTextField city=new JTextField(15);
        JButton search=new JButton("Search");
        filters.add(new JLabel("Blood group"));
        filters.add(group);
        filters.add(new JLabel("City"));
        filters.add(city);
        filters.add(search);
        DefaultTableModel model=new DefaultTableModel(new String[] {
            "Donor ID","Name","Blood Group","City"
        }
        ,0) {
            public boolean isCellEditable(int r,int c) {
                return false;
            }
        };
        JTable table=new JTable(model);
        p.add(filters,BorderLayout.NORTH);
        p.add(new JScrollPane(table),BorderLayout.CENTER);
        p.add(new JLabel("Contact details are private. Blood type and donor eligibility require medical confirmation."),BorderLayout.SOUTH);
        search.addActionListener(e-> {
            if(city.getText().trim().isEmpty()) {
                AppUI.info(this,"Enter a city.");
                return;
            }
            model.setRowCount(0);
            try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement("SELECT donor_id,full_name,blood_group,city FROM donors WHERE blood_group=? AND LOWER(city)=LOWER(?) ORDER BY donor_id DESC")) {
                st.setString(1,(String)group.getSelectedItem());
                st.setString(2,city.getText().trim());
                try(ResultSet r=st.executeQuery()) {
                    while(r.next())model.addRow(new Object[] {
                        r.getInt(1),r.getString(2),r.getString(3),r.getString(4)
                    }
                    );
                }
                if(model.getRowCount()==0)AppUI.info(this,"No matching records found.");
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
