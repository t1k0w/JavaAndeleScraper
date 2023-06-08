package courseWork2;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import windows.ChoiceWindow;
import javax.swing.JPanel;
import java.awt.TextField;
import java.awt.Label;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.DropMode;

//first window where you login or sign up

public class InviteWindow extends Frame {
	private JTextField headText;

	public InviteWindow(){
	     
	        JDesktopPane desktopPane = new JDesktopPane();
	        getContentPane().add(desktopPane, BorderLayout.CENTER);
	        
	        JButton btnNewButton = new JButton("SIGN UP");
	        btnNewButton.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		SignUpWindow one = new SignUpWindow();
	        		dispose();
	        	}
	        });
	        btnNewButton.setBounds(165, 213, 160, 35);
	        desktopPane.add(btnNewButton);
	        
	        JButton btnNewButton_1 = new JButton("LOGIN");
	        btnNewButton_1.setBackground(Color.GRAY);
	        btnNewButton_1.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		LoginWindow one = new LoginWindow();
	        		dispose();
	
	        	}
	        });
	        btnNewButton_1.setBounds(165, 165, 160, 35);
	        desktopPane.add(btnNewButton_1);
	        
	        headText = new JTextField();
	        headText.setBackground(new Color(224, 254, 237));
	        desktopPane.setLayer(headText, 1);
	        headText.setColumns(10);
	        headText.setFont(new Font("Lucida Grande", Font.ITALIC, 16));
	        headText.setHorizontalAlignment(SwingConstants.CENTER);
	        headText.setText("Welcome to AndeleMandele.lv Analyzer");
	        headText.setBounds(81, 6, 331, 52);
	        desktopPane.add(headText);
	        
	        JLabel lblNewLabel = new JLabel("New label");
	        lblNewLabel.setIcon(new ImageIcon("/Users/t1k0w/Desktop/istockphoto-1251263531-170667a.jpg"));
	        lblNewLabel.setBounds(0, 0, 501, 317);
	        desktopPane.add(lblNewLabel);

	        setVisible(true);
	    }
}