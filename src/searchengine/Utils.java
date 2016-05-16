package searchengine;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;


public class Utils {
	public static boolean isValidURL(String urlStr) {
	    try {
	      URL url = new URL(urlStr);
	      return true;
	    }
	    catch (MalformedURLException e) {
	        return false;
	    }
	}
}
