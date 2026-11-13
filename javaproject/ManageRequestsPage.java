import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
public class ManageRequestsPage extends JFrame {
    private final DefaultTableModel model=new DefaultTableModel(new String[] {
        "ID","Patient","Blood Group","Units","Hospital","City","Status"
    }
    ,0) {
        public boolean isCellEditable(int r,int c) {
            return false;
        }
    };
    private final JTable table=new JTable(model);
    public ManageRequestsPage() {
        if(!AdminSession.active())throw new SecurityException("Admin login required");
        AppUI.setup(this,"Manage Requests",1000,520);
        JPanel p=new JPanel(new BorderLayout(10,10));
        p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        JButton reload=new JButton("Reload Requests"),verify=new JButton("Mark Reviewed"),complete=new JButton("Mark Closed"),reject=new JButton("Mark Rejected");
        JPanel top=new JPanel(new FlowLayout());
        top.add(new JLabel("ADMIN: Emergency Requests"));
        top.add(reload);
        p.add(top,BorderLayout.NORTH);
        p.add(new JScrollPane(table),BorderLayout.CENTER);
        JPanel bottom=new JPanel(new FlowLayout());
        bottom.add(verify);
        bottom.add(complete);
        bottom.add(reject);
        p.add(bottom,BorderLayout.SOUTH);
        reload.addActionListener(e->load());
        verify.addActionListener(e->update("VERIFIED"));
        complete.addActionListener(e->update("COMPLETED"));
        reject.addActionListener(e->update("REJECTED"));
        add(p);
        load();
        setVisible(true);
    }
    private void load() {
        if(!AdminSession.active()) {
            dispose();
            return;
        }
        model.setRowCount(0);
        try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement("SELECT request_id,patient_name,blood_group,units_required,hospital_name,city,status FROM emergency_requests ORDER BY request_id DESC");ResultSet r=st.executeQuery()) {
            while(r.next())model.addRow(new Object[] {
                r.getInt(1),r.getString(2),r.getString(3),r.getInt(4),r.getString(5),r.getString(6),r.getString(7)
            }
            );
        }
        catch(SQLException ex) {
            AppUI.error(this,ex);
        }
    }
    private void update(String status) {
        if(!AdminSession.active()) {
            dispose();
            return;
        }
        int row=table.getSelectedRow();
        if(row<0) {
            AppUI.info(this,"Select a request first.");
            return;
        }
        int id=(int)model.getValueAt(table.convertRowIndexToModel(row),0);
        if(JOptionPane.showConfirmDialog(this,"Set request #"+id+" to "+status+"?\nThese are administrative statuses, NOT medical verification.","Confirm",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement("UPDATE emergency_requests SET status=? WHERE request_id=?")) {
            st.setString(1,status);
            st.setInt(2,id);
            st.executeUpdate();
            load();
        }
        catch(SQLException ex) {
            AppUI.error(this,ex);
        }
    }
}
