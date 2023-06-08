package courseWork2;
import javax.swing.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ScrapeWindow extends Frame implements ActionListener {
	
	private static String url_db = "jdbc:postgresql://localhost:5432/AndeleMandele";
    private static String user_db = "user1";
    private static String pass_db = "pass1";

    private JButton startScrapeButton, stopScrapeButton;
    private JButton startInsertButton, stopInsertButton;
    private JLabel statusLabel;

    private Thread scrapeThread;
    private Thread insertThread;
    private boolean scrapeRunning;
    private boolean insertRunning;

    public ScrapeWindow() {

        JPanel buttonPanel = new JPanel(new FlowLayout());

        startScrapeButton = new JButton("Start Scrape");
        startScrapeButton.addActionListener(this);
        buttonPanel.add(startScrapeButton);

        stopScrapeButton = new JButton("Stop Scrape");
        stopScrapeButton.addActionListener(this);
        stopScrapeButton.setEnabled(false);
        buttonPanel.add(stopScrapeButton);

        startInsertButton = new JButton("Start Insert");
        startInsertButton.addActionListener(this);
        buttonPanel.add(startInsertButton);

        stopInsertButton = new JButton("Stop Insert");
        stopInsertButton.addActionListener(this);
        stopInsertButton.setEnabled(false);
        buttonPanel.add(stopInsertButton);

        add(buttonPanel, BorderLayout.NORTH);
        
        JPanel statusPanel = new JPanel(new FlowLayout());
        statusLabel = new JLabel("Status: Idle");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.CENTER);

        
        JPanel consolePanel = new JPanel(new BorderLayout());
        JTextArea consoleTextArea = new JTextArea(10, 40);
        consoleTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(consoleTextArea);
        consolePanel.add(scrollPane, BorderLayout.CENTER);
        add(consolePanel, BorderLayout.SOUTH);

   
        PrintStream consolePrintStream = new PrintStream(new TextAreaOutputStream(consoleTextArea));
        System.setOut(consolePrintStream);
        System.setErr(consolePrintStream);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
       
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startScrapeButton) {
            startScrape();
        } else if (e.getSource() == stopScrapeButton) {
            stopScrape();
        } else if (e.getSource() == startInsertButton) {
            startInsert();
        } else if (e.getSource() == stopInsertButton) {
            stopInsert();
        }
        
        
    }
    

    private void startScrape() {
        statusLabel.setText("Status: Scrape in progress");
        startScrapeButton.setEnabled(false);
        stopScrapeButton.setEnabled(true);

        scrapeRunning = true;
        scrapeThread = new Thread(() -> {
            while (scrapeRunning) {
                scrapeLinks();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            statusLabel.setText("Status: Scrape stopped");
            startScrapeButton.setEnabled(true);
            stopScrapeButton.setEnabled(false);
        });
        scrapeThread.start();
    }

    private void stopScrape() {
        scrapeRunning = false;
        try {
        	scrapeThread.interrupt(); 
        }catch(Exception e){
        	System.out.println("Done");
        }
        
    }


    private void startInsert() {
        statusLabel.setText("Status: Insert in progress");
        startInsertButton.setEnabled(false);
        stopInsertButton.setEnabled(true);

        insertRunning = true;
        insertThread = new Thread(() -> {
            while (insertRunning) {
                try {
					getDataFromLinks();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            statusLabel.setText("Status: Insert stopped");
            startInsertButton.setEnabled(true);
            stopInsertButton.setEnabled(false);
        });
        insertThread.start();
    }

    private void stopInsert() {
        insertRunning = false;
        try {
        	 insertThread.interrupt(); 
        }catch(Exception e){
        	System.out.println("Done");
        }
     
    }

    static void scrapeLinks() {
        int page = 1;
        int counter = 0;
        boolean hasNextPage = true;

        while (hasNextPage) {
            try {
                ScrapeAllLinks scrapeAllLinks = new ScrapeAllLinks(page);
                if (scrapeAllLinks.links.size() > counter) {
                    page++;
                    counter = scrapeAllLinks.links.size();
                } else {
                    hasNextPage = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void getDataFromLinks() throws SQLException {
    	
    	Connection conn = DriverManager.getConnection(url_db, user_db, pass_db);

    	String sql = "SELECT id FROM products ORDER BY id DESC LIMIT 1";
    	Statement stmt = conn.createStatement();
    	ResultSet rs = stmt.executeQuery(sql);
    	rs.next();
    	int id = rs.getInt(1)+1;

        for (String link : ScrapeAllLinks.links) {
            try {
                extractProductInfo(link, id);
                System.out.println("Added: " + id);
                id++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void extractProductInfo(String link, int id) throws Exception {

        String urlBase = "https://www.andelemandele.lv/";
        URL url = new URL(urlBase + link);
        System.out.println(url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "text/html");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        Document doc = Jsoup.parse(content.toString());
        float price = priceElement(doc);
        String type = typeElement(doc);
        String brand = getBrand(doc);
        

        ConnectDB db = new ConnectDB();
        try {
            db.insertProduct(id, type, brand, price);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            db.close();
        }

    }

    static String getBrand(Document doc) {
        Element table = doc.selectFirst("table.attribute-list");
        Elements rows = table.select("tr");

        for (Element row : rows) {
            String key = row.selectFirst("td.attribute-list__key").text();
            if (key.equals("Zīmols")) {
                String brand = row.selectFirst("td.attribute-list__value a").text();
                System.out.println(brand);
                return brand;
            }
        }
        return null;
    }

    static String typeElement(Document doc) {
        Elements typeElement = doc.select(".breadcrumb-item");
        String type = typeElement.text();
        return type;
    }

    static float priceElement(Document doc) {
        Elements priceElement = doc.select("span.product__price.old-price");
        String price = priceElement.text().split(" ")[0].trim();
        return Float.parseFloat(price);
    }

    static void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
        }
    }

    
}

        
           

