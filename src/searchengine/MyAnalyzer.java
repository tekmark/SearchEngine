package searchengine;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;


public class MyAnalyzer {
	static public void test(String text) throws IOException {
		//String text = "Lucene is the simplest yet, powerful java based search library.";
		Analyzer analyzer = new EnglishAnalyzer();
		TokenStream stream = analyzer.tokenStream(null, new StringReader(text));
		
		//TermAttribute term = tStream.addAttribute(TermAttribute.class);
		CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
		
	    try {
	      stream.reset();
	    
	      // print all tokens until stream is exhausted
	      while (stream.incrementToken()) {
	        System.out.println(termAtt.toString());
	      }
	    
	      stream.end();
	    } finally {
	      stream.close();
	    }
	}
}
