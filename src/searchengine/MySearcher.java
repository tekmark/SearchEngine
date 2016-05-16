package searchengine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.logging.log4j.Logger;

public class MySearcher {
	private final static Logger Logger = LogManager.getLogger(MySearcher.class);
	
	private IndexSearcher mSearcher;
	private String mPath;
	
	public MySearcher(String pathname) {
		mPath = pathname;
		Path path = new File(pathname).toPath();		
		Directory dir = null;
		try {
			dir = FSDirectory.open(path);
			IndexReader reader = DirectoryReader.open(dir);
			mSearcher = new IndexSearcher(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public MySearcher(String pathname, String debug) {
		mPath = pathname;
	}
	public MySearcher() {
		mPath = "dummy_path";
	}
	
	public void getSearchResults(Query q, int numOfDocs) throws IOException {
		TopScoreDocCollector collector = TopScoreDocCollector.create(numOfDocs);
		mSearcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		System.out.println("Found " + hits.length + " hits.");
		
		for(int i=0;i<hits.length;++i) {
	          int docId = hits[i].doc;
	          Document d = mSearcher.doc(docId);
	          System.out.println((i + 1) + ". " + d.get(NutchDocField.URL) + " score=" + hits[i].score);
	    }
	}
	
	public void getSearchResults(Query q) throws IOException {
		getSearchResults(q, 100);
	}
	
	public List<SearchResultRecord> search(String str) throws IOException {
		Logger.debug("Search: " + str);
		MyAnalyzer analyzer = new MyAnalyzer();
		Query q = MyQueryParserUtils.getQueryFromString(str, analyzer);
		Logger.info("Query: " + q.toString());
		
		TopScoreDocCollector collector = TopScoreDocCollector.create(100);
		mSearcher.search(q, collector);
		
		int totalHits = collector.getTotalHits(); 
		//Logger.info("# of hits : " + totalHits);
		Logger.info("Found " + totalHits + " hits");
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		List<SearchResultRecord> records = new ArrayList<SearchResultRecord>();
		
		for(int i=0;i<hits.length;++i) {
	          int docId = hits[i].doc;
	          Document d = mSearcher.doc(docId);
	          String url = d.get(NutchDocField.URL);
	          String title = d.get(NutchDocField.Title);
	          Logger.debug((i + 1) + ". " + url + " title: " + title + ", score=" + hits[i].score);
	          SearchResultRecord record = SearchResultRecord.newRecord(title, url);
	          records.add(record);
	    }
		
		return records;
	}
	
	public List<SearchResultRecord> testJavaBridge() {
		List<SearchResultRecord> records = new ArrayList<SearchResultRecord>();
		SearchResultRecord record = SearchResultRecord.newRecord("Rutgers Univerity", "http://www.rutgers.edu");
		records.add(record);
		return records;
	}
	
	public String getPath() {
		return "dummy path";
	}
}
