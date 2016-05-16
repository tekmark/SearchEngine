package searchengine;

import java.io.IOException;
import java.io.StringReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class DumpFileUtils {
	
	private final static Logger Logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
	
	public static Document convertDumpFileRecToNutchDoc(DumpFileRecord record, Analyzer analyzer) throws IOException {
		Document doc = null;
		if (analyzer != null && record.isValid() == 0) {
			doc = new Document();

			//analyze title filed and add it to nutch doc
			//TokenStream ts = analyzer.tokenStream("title", new StringReader(record.getTitle()));
			//CharTermAttribute termAtt1 = ts.addAttribute(CharTermAttribute.class);
			//TextField titleField = new TextField(NutchDocField.Title, ts); 
			//StringField title = new StringField(NutchDocField.Title, record.getTitle(), Field.Store.YES);
			//doc.add(title);
			
			//analyze parse text and add it to nutch doc
			TokenStream ts = analyzer.tokenStream("contents", new StringReader(record.getParseText()));
			CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
			
			TextField parseText = new TextField(NutchDocField.Contents, ts);
			doc.add(parseText);
			//tokenStream2.reset();
			
			//add title to doc
			StringField title = new StringField(NutchDocField.Title, record.getTitle(), Field.Store.YES);
			doc.add(title);
			//add url to nutch doc
			doc.add(new StringField(NutchDocField.URL, record.getUrl(), Field.Store.YES));	
			return doc;
		} 
		
		if (analyzer == null) {
			Logger.error("No analyzer specified");
		} else {
			Logger.error("reecord is not valid");
		}
		return null;
	}
	
	//get the label from a string line
	//return root label
	public static String getRootLabel (String line) {
		if (line.startsWith(DumpFileLabel.RECNO)) {
			return DumpFileLabel.RECNO;
		} else if (line.startsWith(DumpFileLabel.URL)) {
			return DumpFileLabel.URL;
		} else if (line.startsWith(DumpFileLabel.PARSE_TEXT)) {
			return DumpFileLabel.PARSE_TEXT;
		} else if (line.startsWith(DumpFileLabel.PARSE_DATA)) { 
			return DumpFileLabel.PARSE_DATA;
		} else {
			return DumpFileLabel.NO_LABEL;
		}
	}
	
	//return true if starts with root label.
	public static boolean startsWithRootLabel (String line) {
		return line.startsWith(DumpFileLabel.RECNO) || line.startsWith(DumpFileLabel.URL) ||
				line.startsWith(DumpFileLabel.PARSE_DATA) ||
				line.startsWith(DumpFileLabel.PARSE_TEXT) ||
				line.startsWith(DumpFileLabel.CRAWL_DATUM);
	}
	
	public static String getParseDataSubLabel(String line) {
		if (line.startsWith(DumpFileLabel.ParseData.TITLE)) {
			return DumpFileLabel.ParseData.TITLE;
		} else if (line.startsWith(DumpFileLabel.ParseData.STATUS)) {
			return DumpFileLabel.ParseData.STATUS;
		} else if (line.startsWith(DumpFileLabel.ParseData.OUTLINKS)) {
			return DumpFileLabel.ParseData.OUTLINKS;
		} else {
			return null;
		}
	}
	
	public static String getInfo(String line, String label) {
		String info = "";
		if (label == DumpFileLabel.RECNO || label == DumpFileLabel.URL) {
			info = line.substring(label.length()); 
		} else {
			
		}
		return info;
	}
	
	public static String getLabelInfo(String line, String label) {
		return line.substring(label.length());
	}
	
	public static int getRecNo(String line) {
		String s = line.substring(DumpFileLabel.RECNO.length());
		int recNo = Integer.parseInt(s);
		return recNo;
	}
	
	
//	private static int mCurrRecNo = -1;
}
