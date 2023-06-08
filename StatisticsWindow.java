package courseWork2;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class StatisticsWindow extends Frame {
	
    private JTable table;
    private DefaultTableModel model;

    public StatisticsWindow() {
   
        model = new DefaultTableModel(new Object[][]{}, new String[]{"Type", "Amount", "Price"});
        table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoCreateRowSorter(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        JButton logoutButton = new JButton("Log Out");
        buttonsPanel.add(backButton);
        buttonsPanel.add(logoutButton);
        add(buttonsPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane);
        setVisible(true);

        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/AndeleMandele", "user1", "pass1");

            Statement stmt = conn.createStatement();

            String sql = "SELECT type, COUNT(type) as count, ROUND(AVG(price)::numeric, 1) as avg_price \n"
                    + "FROM products \n"
                    + "GROUP BY type \n"
                    + "ORDER BY count DESC;";


            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String type = rs.getString("type");
                int count = rs.getInt("count");
                float price = rs.getFloat("avg_price");
                model.addRow(new Object[]{type, count, price});
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	new MenuWindow();
                dispose();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	new InviteWindow(); 
                dispose();
            }
        });
    }
}
