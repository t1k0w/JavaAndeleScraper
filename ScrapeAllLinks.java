package courseWork2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// scraping tool for getting data from website 

public class ScrapeAllLinks {
	
	static int page;
	public static ArrayList<String> links = new ArrayList<String>();
	
	
	public ScrapeAllLinks(int page) throws Exception {
		this.page = page;
		int sold = 1;
		String x = "{\"order\":\"actual\",\"sold\":\""+sold+"\",\"page\":\""+page+"\"}";
        String encodedFilter = Base64.getEncoder().encodeToString(x.getBytes());
        String urlBase = "https://www.andelemandele.lv/product-data/?filter=";
        URL url = new URL(urlBase + encodedFilter);
        System.out.println(url.toString());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        
        in.close();
        con.disconnect();
        JSONObject obj = new JSONObject(content.toString());
        String html = obj.get("html").toString();
        //System.out.println(html);
        
        System.out.println("Page number: " + page);
        
        String htl = "<html>...</html>";
	    Document doc = Jsoup.parse(html);
	    Elements linkElements = doc.select("a[href^=/perle/]");
        getLinks(linkElements);

        
	}
    
	public static void getLinks(Elements linkElements) {

	    for (Element link : linkElements) {
	        String href = link.attr("href");
	        links.add(href.toString());
	        //System.out.println(href);
	    }
	    
	    System.out.println("Amount of links: " + links.size());

	}

}
