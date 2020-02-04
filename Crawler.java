
import java.io.IOException;
import java.net.URL; 
import org.jsoup.Jsoup;
import org.jsoup.Connection;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.io.ObjectInputStream.GetField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.io.File;
import org.json.JSONObject;
import java.util.Stack;

public class Crawler {
	private HashSet<String> seenLinks;
	public static void main(String[] args) throws IOException {
		System.out.println("Hello world");
		Stack<URL> Frontier = new Stack();
		Frontier.push(new URL("http://www.ucr.edu/"));
		Frontier.push(new URL("https://www.berkeley.edu/"));
		Frontier.push(new URL("https://www.ucsb.edu/"));
		Frontier.push(new URL("http://www.uci.edu/"));
		Frontier.push(new URL("http://www.ucmerced.edu/"));
		Frontier.push(new URL("http://www.ucsd.edu/"));
		Frontier.push(new URL("http://www.ucla.edu/"));
		Frontier.push(new URL("http://www.ucdavis.edu/"));
		Frontier.push(new URL("http://www.ucsc.edu/"));
		Frontier.push(new URL("http://www.ucsf.edu/"));
		Frontier.push(new URL("https://www.stanford.edu/"));
		// Frontier.push(new URL("http://ycombinator.com"));
		URL curr = Frontier.pop();
		System.out.println("top level domain: " + curr.getHost().contains(".edu"));
		Document doc = Jsoup.connect(curr.toString()).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
		Elements links = doc.select("a[href]");
        // for (Element link : links) {
        //     // System.out.println(link.text()); //link name
        //     System.out.println(link.attr("href")); //actual url
        // }
		// System.out.println(doc.title());
		// Document document = Jsoup.connect(curr.toString()).get();
		// TransformerFactory transformerFactory = TransformerFactory.newInstance();
		// Transformer transformer = transformerFactory.newTransformer();

		// DOMSource source = new DOMSource(doc);
		// DOMSource domSource = new DOMSource(doc);
		// FileOutputStream fos = new FileOutputStream("doc.ser");//, true); 
		// ObjectOutputStream oos = new ObjectOutputStream(fos);
		// oos.writeObject(doc.text());
		// oos.close();
		// File input = new File("doc.ser");
		// Document doc2 = Jsoup.parse(input, "UTF-8");//"doc.ser", "UTF-8");
		//store text,title, and link
		PrintStream writeToFile = new PrintStream(new FileOutputStream("web.data", true));
		JSONObject myDoc = new JSONObject();
		myDoc.put("title", doc.title());
		myDoc.put("text", doc.text());
		myDoc.put("url", curr.toString());
		// myDoc.addProperty()
		// System.out.println(myDoc.toString());
		writeToFile.println(myDoc.toString());
		writeToFile.close();

		//IN ANOTHER JAVA FILE add something like this, this is not final code: 
		FileReader fileRdr = new FileReader("web.data");
		BufferedReader bufferRdr = new BufferedReader(fileRdr);
		String jsonLine = null;
		//IndexWriter idxWriter = new IndexWriter("<index direcotry>", analyzer type)
		while((jsonLine = bufferRdr.readLine()) != null) {
			//Document currDoc = new Document(); //USE LUCENE DOCUMENT not JSOUP 
			//e.g. basically import lucene package Document then u can construct documentts like this
			JSONObject jsonObj = new JSONObject(jsonLine);
			if(jsonObj.has("title") && !jsonObj.isNull("title"))
			{
				String title = jsonObj.getString("title");
				System.out.println(title);
				//currDoc.add(new TextField("title", title, Field.Store.YES));
			}
			//do this for the rest of the attributes

			//indxWriter.add(currDoc)
		}
		
	}

}