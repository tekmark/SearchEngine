package searchengine;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlProcessor {
	public static String readAlltoString(String path, Charset encoding) throws IOException{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public static String jsoupTest(String html) {
//		String html = "/home/chao/Desktop/fall15";
//		String html2 = "<html> <p>An <a href='http://example.com/'><b>example</b></a> link.</p></html>";
		Document doc = 	Jsoup.parse(html);
		String s = doc.select("h2").text();
		System.out.println(s);
		//Element p = doc.select("body").first();
		//System.out.println(p.text());
		return null;
	}
	
	public final static String HeadingTags = "h1,h2,h3,h4,h5,h6";
	public static String extractHeadings(String html) {
		Document doc = Jsoup.parse(html);
		Elements hTags = doc.select(HeadingTags);
		
		String headings = hTags.text();
		System.out.println(headings);
		return headings;
	}
	public static String extractText(String html) {
		Document doc = 	Jsoup.parse(html);
		String s = doc.text();
		System.out.println(s);
		return s;
	}
}
