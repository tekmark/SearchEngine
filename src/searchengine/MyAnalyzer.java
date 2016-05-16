package searchengine;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.KeywordMarkerFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;


public class MyAnalyzer extends Analyzer {
	
	public static MyAnalyzer getDefaultAnalyzer () { 
		return new MyAnalyzer(); 
	}
	
	public MyAnalyzer () {
		super();
		keywordSet = new HashSet<String>();
	}
	
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
	    analyzer.close();
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer source = new StandardTokenizer();
		
		//filters.
		TokenStream filter = new LowerCaseFilter(source);
		
		filter = new SetKeywordMarkerFilter(filter, new CharArraySet(keywordSet, true));
		filter = new StopFilter(filter, EnglishAnalyzer.getDefaultStopSet());
		filter = new PorterStemFilter(filter); 
		
		//TokenStreamComponents components = new TokenStreamComponents(source, filter);
		//return components;
		return new TokenStreamComponents(source, filter);
	}
	
	private Set<String> keywordSet;
	
	public void setKeywords (Collection<String> keywords ) {
		keywordSet.clear();
		if (keywords != null) {
			keywordSet.addAll(keywords);
		}
	}
	
	static public void run(String text) throws IOException {
		//String text = "Lucene is the simplest yet, powerful java based search library.";
		MyAnalyzer analyzer = new MyAnalyzer();
		
//		List<String> keywords = new ArrayList<String>();
//		keywords.add("google");
//		keywords.add("youtube");
//		analyzer.setKeywords(keywords);
		
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
	    analyzer.close();
	}
}