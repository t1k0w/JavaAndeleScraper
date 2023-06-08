package courseWork2;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AvarageWindow extends AllInfoWindow {

    private JComboBox<String> dateComboBox;
    private JButton showButton;
	private Connection conn;
	private JTable productsTable;
	
	public AvarageWindow() {
		showButton = new JButton("Show");
        add(showButton);
        dateComboBox = new JComboBox<String>();
        dateComboBox.setPreferredSize(new Dimension(150, 25));
        add(dateComboBox);
        JLabel popularTypeLabel = new JLabel("Most popular type: ");
        JLabel avgPriceLabel = new JLabel("Average price: ");
        add(popularTypeLabel);
        add(avgPriceLabel);

        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/AndeleMandele";
            String username = "user1";
            String password = "pass1";
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }

        try {
            String sql = "SELECT DISTINCT date FROM products ORDER BY date DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Date date = rs.getDate(1);
                dateComboBox.addItem(date.toString());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve dates from the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              
                String selectedDate = (String) dateComboBox.getSelectedItem();
   
                DefaultTableModel model = (DefaultTableModel) productsTable.getModel();
                model.setRowCount(0);
                try {
                    String sql = "SELECT type, AVG(price) FROM products WHERE date = ? GROUP BY type ORDER BY COUNT(*) DESC LIMIT 1";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setDate(1, Date.valueOf(selectedDate));
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        String popularType = rs.getString(1);
                        double avgPrice = rs.getDouble(2);
                        popularTypeLabel.setText("Most popular type: " + popularType);
                        avgPriceLabel.setText("Average price: " + String.format("%.2f", avgPrice));
                    } else {
                        popularTypeLabel.setText("Most popular type: N/A");
                        avgPriceLabel.setText("Average price: N/A");
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    popularTypeLabel.setText("Most popular type: N/A");
                    avgPriceLabel.setText("Average price: N/A");
                }

            }
        });
        
        
	}
}
