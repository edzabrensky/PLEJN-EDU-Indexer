
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
import java.util.Queue; 
import java.util.LinkedList; 
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Crawler {
	private static ConcurrentHashMap<String, Boolean> seenLinks = new ConcurrentHashMap<>();
	private static Boolean stopCrawling = false;
	// private final static int numCrawlers = 6;
	private static Queue<URL>[] fetcherBQ;//= new Queue[numCrawlers]; //for committing queue
	private static ArrayBlockingQueue<Document>[] committerBQ;//= new ArrayBlockingQueue[numCrawlers];
	private static Integer totalDataSizeInMB;
	private static String outputDir;
	//need lock for seenLinks write 
	//Array of frontiers stacks only access ur own frontier
	//Array of document queues to be committed.
	//put in try blocks so it doesnt crash whole program :)
	public static void fetcher(Integer id) {
		Thread fetcher = new Thread(new Runnable() {   
			public void run() {
				System.out.println("Hello from a fetcher!" + id.toString());
				//Create new frontier stack
				//while frontier stack is not empty and stopCrawling = false, fetch, connect, get document, push fetched document to committer, get links, check if link is in seenlinks, add unseen links to frontier.
				try {
					while(!stopCrawling && !fetcherBQ[id].isEmpty()){
						URL curr = fetcherBQ[id].remove();
						try {
							Document doc = Jsoup.connect(curr.toString()).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
							Elements links = doc.select("a[href]");
							if(doc.location().contains(".edu")) { //make sure we are not redirected to a non edu link when we actually visit link
								committerBQ[id].put(doc);
								for (Element link : links) {
									String actualLink = link.attr("href");
						            if(actualLink.contains(".edu") && seenLinks.putIfAbsent(actualLink, true) == null) {
						            	try {
						            		new URL(actualLink).toURI();//final check to see if url is valid
						            		fetcherBQ[id].add(new URL(actualLink));
						            	}
						            	catch(Exception e) {

						            	}
						            	// System.out.println("Put in unique link: " + link.attr("href")
						            }
						        }
					    	}
					    	Thread.sleep(20); //small delay so that we dont constantly ping the server (i think its fairness)
				    	}
				    	catch(Exception e) {
				    		// System.out.println("Error occurred in crawler" + e.toString());
				    	}
				    }
				}
				catch(Exception e) {
					System.out.println("Error occurred in crawler" + id.toString() + e.toString());
				}
			}
		});
  		fetcher.start();
    }
    public static void committer(Integer id) {
		Thread committer = new Thread(new Runnable() {   
			public void run() {
				System.out.println("Hello from a committer!" + id.toString());
				try {
					PrintStream writeToFile = new PrintStream(new FileOutputStream(outputDir + "/web" + id.toString() + ".data", true));
					Document doc;
					while(!stopCrawling) {
						doc = committerBQ[id].take();
						JSONObject myDoc = new JSONObject();
						myDoc.put("title", doc.title());
						myDoc.put("text", doc.text());
						myDoc.put("url", doc.location());
						writeToFile.println(myDoc.toString());
						// writeToFile.close();
						// System.out.println(doc.title());
					}
					writeToFile.close();
				}
				catch(Exception e) {
					System.out.println("Error occurred in fetcher" + id.toString());
				}
			}
		});
  		committer.start();
    }
    public static void sizeChecker() {
		Thread sizeChecker = new Thread(new Runnable() {   
			public void run() {
				System.out.println("Hello from a sizeChecker!");
				long size = 0;
				Integer sleepTime = 10000;//1000*60;
				while((double) size/(1024*1024) < totalDataSizeInMB) { //while size < 1000MB
					size = 0;
					try {
						Thread.sleep(sleepTime);
					}
					catch(Exception e) {
						System.out.println("Error sleepin bruh");
					}
					File dir = new File(outputDir); //get directory
					for (File file : dir.listFiles()) {
						if(file.isFile())
						{
							size += file.length();
						}
					}
					System.out.println((double) size/(1024*1024) + "MB");
				}
				stopCrawling = true;
				try {
					Thread.sleep(sleepTime);
				}
				catch(Exception e) {
					System.out.println("Error sleepin bruh");
				}
				System.out.println("Reached the size goal!");
				System.exit(0);
				//sleep for like 1 minute then check size of folder.
				//if size of folder is 1gb> then set stopCrawling = true
			}
		});
  		sizeChecker.start();
    }
    //<seed of .edu addresses> <Amount of data to collect in bytes> <out-put-dir>
	public static void main(String[] args) throws IOException  {
		System.out.println(args.length);
		if(args.length != 3) {
			System.out.println("Needs 3 arguments: <seed of .edu addresses> <Amount of data to collect in Mbytes> <out-put-dir>");
			return;
		}
		Stack<URL> Frontier = new Stack();
		File uniFile = new File(args[0]);
		if(!(uniFile.exists())) {
			System.out.println("Could not find file " + uniFile);
			System.out.println("Please create file");
			return;
		}
		totalDataSizeInMB = new Integer(Integer.valueOf(args[1]));
		outputDir = new String(args[2]);
		if(!(new File(outputDir).isDirectory())) {
			System.out.println("Could not find directory " + outputDir);
			System.out.println("Please create direcotry");
			return;
		}
		Scanner uniListReader = new Scanner(uniFile);
		String uni = "";
		List<String> unis = new ArrayList<String>();;
		Integer numLines = 0;
		while(uniListReader.hasNextLine()) {
			uni = uniListReader.nextLine();
			unis.add(uni);
			numLines++;
		}
		fetcherBQ = new Queue[numLines];
		committerBQ= new ArrayBlockingQueue[numLines];
		for(int i = 0; i < unis.size(); ++i) {
			fetcherBQ[i] = new LinkedList<>(); 
			committerBQ[i] = new ArrayBlockingQueue<Document>(1024);
			System.out.println("Uni: " + unis.get(i) + " i: " + i);
			fetcherBQ[i].add(new URL(unis.get(i)));
			fetcher(i);
			committer(i);
		}
		sizeChecker();
	}

}