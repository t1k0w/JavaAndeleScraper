package courseWork2;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//main menu of program where you can choose what to do

public class MenuWindow extends Frame implements ActionListener {

    private JButton AVGPrice, check, scrape;
    
    public MenuWindow() {

        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon("/Users/t1k0w/Desktop/istockphoto-1251263531-170667a.jpg");
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                g.setColor(Color.GREEN);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                String text = "Main menu of Analyzer - choose available options";
                int textWidth = g.getFontMetrics().stringWidth(text);
                int x = (getWidth() - textWidth) / 2;
                int y = 50; 
                g.drawString(text, x, y);
            }
        };

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(20, 20, 20, 20);

        scrape = new JButton("Scraping Tool");
        scrape.addActionListener(this);
        constraints.gridx = 8;
        constraints.gridy = 3;
        panel.add(scrape, constraints);

        AVGPrice = new JButton("AVG prices");
        AVGPrice.addActionListener(this);
        constraints.gridx = 2;
        constraints.gridy = 3;
        panel.add(AVGPrice, constraints);

        check = new JButton("All Info");
        check.addActionListener(this);
        constraints.gridx = 5;
        constraints.gridy = 3;
        panel.add(check, constraints);

        add(panel);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == AVGPrice) {
            new StatisticsWindow();
            dispose();
        }
        if (e.getSource() == check) {
            new AllInfoWindow();
            dispose();
        }
        if (e.getSource() == scrape) {
        	SwingUtilities.invokeLater(ScrapeWindow::new);
            dispose();
        }
    }
}
