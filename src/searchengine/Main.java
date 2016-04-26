package searchengine;

import java.io.File;
import java.io.IOException;

import java.nio.charset.Charset;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class Main {
	private final static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
	
	public static String path="test/test";	
	
	public static void main(String[] args) throws IOException, ParseException {
		
		logger.debug("test");
		
		// TODO Auto-generated method stub
//		System.out.println("Hello World");
		//Tokenizer.tokenize();
		
		/*
		try {
			String s = HtmlProcessor.readAlltoString(path, Charset.defaultCharset());
			//System.out.println(s);
			String ss = HtmlProcessor.extractText(s);
//			System.out.print(s);
			//Tokenizer.tokenize(ss);
			//String text = "Apache be the simplest yet, a of Powerful java based search library good cheap connective.";
			MyAnalyzer.run(ss);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print("ERROR");
			e.printStackTrace();
		}
		*/
		MyAnalyzer analyzer = new MyAnalyzer();
//		StandardAnalyzer analyzer = new StandardAnalyzer();
		try {
			TextFileIndexer indexer = new TextFileIndexer("abc", analyzer);
			indexer.index(path);
			indexer.closeIndex();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//searcher
	    IndexReader reader = DirectoryReader.open(FSDirectory.open(new File("abc").toPath()));
	    IndexSearcher searcher = new IndexSearcher(reader);
	    TopScoreDocCollector collector = TopScoreDocCollector.create(5);
	    
	    Query q = new QueryParser("contents", analyzer).parse("+mit +twitter");
	    
//	    QueryBuilder builder = new QueryBuilder(analyzer);
//	    Query a = builder.createBooleanQuery("body", "just a test");
//	    Query b = builder.createPhraseQuery("body", "another test");
//	    Query c = builder.createMinShouldMatchQuery("body", "another test", 0.5f);
//		Query q = builder.createPhraseQuery("contents", "spotlight control");
		
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		System.out.println("Found " + hits.length + " hits.");
		
		for(int i=0;i<hits.length;++i) {
	          int docId = hits[i].doc;
	          Document d = searcher.doc(docId);
	          System.out.println((i + 1) + ". " + d.get("path") + " score=" + hits[i].score);
	        }
	}	
}
