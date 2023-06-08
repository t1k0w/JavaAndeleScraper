package courseWork2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class SignUpWindow extends Frame implements ActionListener {
    private JLabel emailLabel, passwordLabel, confirmPasswordLabel, errorLabel;
    private JTextField emailTextField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton signUpButton;
    
    //DB connection
    private static String url_db = "jdbc:postgresql://localhost:5432/AndeleMandele";
    private static String user_db = "user1";
    private static String pass_db = "pass1";
    
    public SignUpWindow() {

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

        confirmPasswordLabel = new JLabel("Confirm Password:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(confirmPasswordLabel, constraints);

        confirmPasswordField = new JPasswordField(20);
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(confirmPasswordField, constraints);

        signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(this);
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        panel.add(signUpButton, constraints);

        errorLabel = new JLabel("", JLabel.CENTER);
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        panel.add(errorLabel, constraints);

        add(panel);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signUpButton) {
            String email = emailTextField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
            } else if (emailExists(email)) {
                errorLabel.setText("An account with this email already exists. Please log in instead.");
            } else if (!password.equals(confirmPassword)) {
                errorLabel.setText("Passwords do not match. Please try again.");
            } else {
                addUser(email, password);
                errorLabel.setText("Account created successfully.");
                MenuWindow menu = new MenuWindow();
                dispose();
            }
        }
    }


    private boolean emailExists(String email) {
        try (Connection connection = DriverManager.getConnection(url_db, user_db, pass_db);
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?")) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void addUser(String email, String password) {
        try {
            Connection connection = DriverManager.getConnection(url_db, user_db, pass_db);
            PreparedStatement getLastIdStatement = connection.prepareStatement(
                    "SELECT id FROM users ORDER BY id DESC LIMIT 1;");
            ResultSet result = getLastIdStatement.executeQuery();
            int lastId = 0;
            if (result.next()) {
                lastId = result.getInt("id");
            }

            int nextId = lastId + 1;
            
            PreparedStatement addUserStatement = connection.prepareStatement(
                    "INSERT INTO users (id, email, password) VALUES (?, ?, ?);");
            addUserStatement.setInt(1, nextId);
            addUserStatement.setString(2, email);
            addUserStatement.setString(3, hashPassword(password));
            addUserStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
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
