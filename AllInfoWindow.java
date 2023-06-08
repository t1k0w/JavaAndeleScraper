package courseWork2;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class AllInfoWindow extends Frame {

    private JComboBox<String> dateComboBox;
    private JButton showButton;
    private JTable productsTable;

    private Connection conn;

    public AllInfoWindow() {
    	
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.X_AXIS));
        datePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        datePanel.add(new JLabel("Select a date: "));
        dateComboBox = new JComboBox<String>();
        dateComboBox.setPreferredSize(new Dimension(150, 25));
        datePanel.add(dateComboBox);
        showButton = new JButton("Show");
        datePanel.add(showButton);
        
        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setIcon(new ImageIcon("/Users/t1k0w/Desktop/istockphoto-1251263531-170667a.jpg"));

        setLayout(new BorderLayout());

        add(backgroundLabel, BorderLayout.CENTER);
        backgroundLabel.setLayout(new BorderLayout());


        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Type", "Brand", "Price", "Date"}, 0);
        productsTable = new JTable(model);
        productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(productsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
  
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        JButton logoutButton = new JButton("Log Out");
        buttonsPanel.add(backButton);
        buttonsPanel.add(logoutButton);

        backgroundLabel.add(datePanel, BorderLayout.NORTH);
        backgroundLabel.add(tablePanel, BorderLayout.CENTER);
        backgroundLabel.add(buttonsPanel, BorderLayout.SOUTH);
  
        setLayout(new BorderLayout());
        add(datePanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
        setVisible(true);

    
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
                    String sql = "SELECT * FROM products WHERE date = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setDate(1, Date.valueOf(selectedDate));
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String type = rs.getString("type");
                        String brand = rs.getString("brand");
                        double price = rs.getDouble("price");
                        Date date = rs.getDate("date");
                        Object[] row = {id, type, brand, price, date};
                        model.addRow(row);
                    }
                }catch(SQLException e1) {
                	e1.printStackTrace();
                }
            }
        });
        
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
   
            
               
