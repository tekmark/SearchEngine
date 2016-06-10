package searchengine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.logging.log4j.Logger;

public class MySearcher {
	private final static Logger Logger = LogManager.getLogger(MySearcher.class);
	private final static int MaxNumberOfResults = 5000;

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
		
		TopScoreDocCollector collector = TopScoreDocCollector.create(1000);

		Random rand = new Random();
		List<Integer> largeArr = new ArrayList<>();
		for (int x = 0; x < 100000; ++x) {
			int num = rand.nextInt();
			largeArr.add(num);
		}

		long startTime = System.nanoTime();
		mSearcher.search(q, collector);
		Collections.sort(largeArr);
		long stopTime = System.nanoTime();
		long duration = stopTime - startTime;

		int totalHits = collector.getTotalHits(); 
		//Logger.info("# of hits : " + totalHits);
		Logger.info("Found " + totalHits + " hits in " + duration/1000000 + " milliseconds.");
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		List<SearchResultRecord> records = new ArrayList<SearchResultRecord>();
		
		for(int i=0;i<hits.length;++i) {
	          int docId = hits[i].doc;
	          Document d = mSearcher.doc(docId);
	          String url = d.get(NutchDocField.URL);
	          String title = d.get(NutchDocField.Title);
			  Logger.debug((i + 1) + ". " + url + " title: " + title + " | " + hits[i].score);
	          SearchResultRecord record = SearchResultRecord.newRecord(title, url);
	          records.add(record);
	    }
		
		return records;
	}

	public MySearchResults getResultsSlice(String str, int start, int howMany) throws IOException {
		return search(str, start, howMany);
	}

	public MySearchResults search(String str, int start, int howMany) throws IOException {
		Logger.debug("Search: " + str + " Start: " + start + " howMany: " + howMany);
		MyAnalyzer analyzer = new MyAnalyzer();
		Query query = MyQueryParserUtils.getQueryFromString(str, analyzer);

		if (query == null) {
			Logger.error("Failed to build query from text: " + str);
			return null;
		}

		Logger.info("Query: " + query.toString());

		TopScoreDocCollector collector = TopScoreDocCollector.create(MaxNumberOfResults);

		long startTime = System.nanoTime();
		//do search
		mSearcher.search(query, collector);
		long endTime = System.nanoTime();
		//calculate eclipsed time.
		long duration = (endTime - startTime);  //Note: divide by 1000000 to get milliseconds.

		int totalHits = collector.getTotalHits();
		Logger.info("Found " + totalHits + " hits in " + duration/1000000 +" milliseconds");

		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		//generate a array list to run ranking algorithm.
		List<SearchResultRecord> records = new ArrayList<SearchResultRecord>();

//		for (int i = start; i < start + howMany && i < hits.length; ++i) {
		for (int i = start; i < totalHits && i < MaxNumberOfResults; ++i) {
			int docId = hits[i].doc;
			Document doc = mSearcher.doc(docId);
//			String url = doc.get(NutchDocField.URL);
//			String title = doc.get(NutchDocField.Title);
			//String urlScoreStr = d.get(NutchDocField.UrlScore);
			//double urlScore = Double.parseDouble(urlScoreStr);
//			Logger.debug((i + 1) + ". " + url + " | " + title + " | " + hits[i].score);
//			SearchResultRecord record = SearchResultRecord.newRecord(title, url);
			SearchResultRecord record = SearchResultRecord.newRecord(doc);
			record.setLuceneScore(hits[i].score);
			records.add(record);
		}
		//TODO: run a ranking algorithm here.
		MyRankingAlgorithm.rankRecordsByUrlScore(records);

		MySearchResults results = MySearchResults.create(start, totalHits, duration);
		results.replaceAll(records.subList(start, start + howMany));

//		for (int i = 0; i < records.size(); ++i) {
//			Logger.debug((i + 1) + ". " + records.get(i).toString());
//		}
		for (SearchResultRecord r : results.getRecords()) {
			Logger.debug(r.toString());
		}
		return results;
	}



	public void searchByUrlTest() throws IOException {
		String url = "http://www.rutgers.edu/academics/catalog-archive-edward-j-bloustein-school-planning-and-public-policy";
		Term term = new Term(NutchDocField.URL, url);
		Query query = new TermQuery(term);
		TopDocsCollector collector = TopScoreDocCollector.create(1);
		mSearcher.search(query, collector);
		int hits = collector.getTotalHits();
		Logger.info("# of hits : " + hits);
		if (hits > 0) {
			Logger.info("Found Url: " + url);
			ScoreDoc[] docs = collector.topDocs().scoreDocs;
			Logger.debug(docs[0].toString());
		} else {
			Logger.info("Term is not found");
		}
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
