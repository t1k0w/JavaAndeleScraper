package courseWork2;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;

public class ConnectDB {
	private Connection conn;
	
	public ConnectDB() {
        conn = null;
        String url = "jdbc:postgresql://localhost:5432/AndeleMandele";
        String user = "user1";
        String password = "pass1";

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC driver not found.");
        }
    }

    public void insertProduct(int id, String type, String brand, float price) throws SQLException {
        String query = "INSERT INTO products (id, type, brand, price, date) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, id);
        stmt.setString(2, type);
        stmt.setString(3, brand);
        stmt.setFloat(4, price);
        stmt.setDate(5, Date.valueOf(LocalDate.now()));
        stmt.executeUpdate();
        stmt.close();
    }
     void getDataFromDB() throws SQLException {
            Statement stmt = conn.createStatement();
            
            String sql = "SELECT type, COUNT(type) as count, ROUND(AVG(price)::numeric, 1) as avg_price \n"
            		+ "FROM products \n"
            		+ "GROUP BY type \n"
            		+ "ORDER BY count DESC;";
            
            ResultSet rs = stmt.executeQuery(sql);
            
            System.err.println("Type                 " + "   Amount  " + "   Price  ");
            while (rs.next()) {
                String type = rs.getString("type");
                int count = rs.getInt("count");
                float price = rs.getFloat("avg_price");
                System.out.println(type + " || " + count + " || Price: " + price);
            }
            
        
    }

    public void close() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

	
}
