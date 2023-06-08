package courseWork2;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginWindow extends Frame implements ActionListener {
    private JLabel emailLabel, passwordLabel;
	private static JLabel errorLabel;
    private JTextField emailTextField;
    private JPasswordField passwordField;
    private JButton loginButton, signUpButton;
    private static String url_db = "jdbc:postgresql://localhost:5432/AndeleMandele";
    private static String user_db = "user1";
    private static String pass_db = "pass1";
    
    public LoginWindow() {   

    	JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon("/Users/t1k0w/Desktop/istockphoto-1251263531-170667a.jpg");
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        emailLabel = new JLabel("Email:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(emailLabel, constraints);

        emailTextField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(emailTextField, constraints);

        passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(passwordLabel, constraints);

        passwordField = new JPasswordField(20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(passwordField, constraints);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(loginButton, constraints);

        signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(this);
        constraints.gridx = 1;
        constraints.gridy = -1;
        panel.add(signUpButton, constraints);

        errorLabel = new JLabel("", JLabel.CENTER);
        constraints.gridx = 0;
        constraints.gridy = -2;
        constraints.gridwidth = 2;
        panel.add(errorLabel, constraints);

        add(panel);
        
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String email = emailTextField.getText();
            String password = new String(passwordField.getPassword());

            if (checkEmail(email) && checkPassword(email, password)) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                MenuWindow menu = new MenuWindow();
                dispose();
            } else {
                errorLabel.setText("Incorrect email or password. Please try again or sign up.");
            }
        } else if (e.getSource() == signUpButton) {
        	
            SignUpWindow one = new SignUpWindow();
            dispose();
        }
    }

    
    public static boolean checkEmail(String email) {
    	
    	if (!isValidEmail(email)) {
    		errorLabel.setText("Incorrect email or password. Please try again or sign up.");
            throw new IllegalArgumentException("Invalid email format");
        }

        try (Connection conn = DriverManager.getConnection(url_db, user_db, pass_db)) {
            String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    System.out.println("Done");
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        System.out.println("Done");
        return matcher.matches();
    }
    
    public boolean checkPassword(String email, String password) {
        boolean isCorrect = false;
        
        try {
            Connection conn = DriverManager.getConnection(url_db, user_db, pass_db);

            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
            pstmt.setString(1, email);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String passwordHash = rs.getString("password");
                if (passwordHash.equals(hashPassword(password))) {
                    isCorrect = true;
                }
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return isCorrect;
    }
    
    
    //Solution of encryption from Google 
    private String hashPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hash) {
                stringBuilder.append(String.format("%02x", b));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
   
}

