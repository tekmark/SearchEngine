package searchengine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
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
	
//	public List<SearchResultRecord> search(String str) throws IOException {
//		Logger.debug("Search: " + str);
//		MyAnalyzer analyzer = new MyAnalyzer();
//		Query q = MyQueryParserUtils.getQueryFromString(str, analyzer);
//		Logger.info("Query: " + q.toString());
//
//		TopScoreDocCollector collector = TopScoreDocCollector.create(1000);
//
//		Random rand = new Random();
//		List<Integer> largeArr = new ArrayList<>();
//		for (int x = 0; x < 100000; ++x) {
//			int num = rand.nextInt();
//			largeArr.add(num);
//		}
//
//		long startTime = System.nanoTime();
//		mSearcher.search(q, collector);
//		Collections.sort(largeArr);
//		long stopTime = System.nanoTime();
//		long duration = stopTime - startTime;
//
//		int totalHits = collector.getTotalHits();
//		//Logger.info("# of hits : " + totalHits);
//		Logger.info("Found " + totalHits + " hits in " + duration/1000000 + " milliseconds.");
//		ScoreDoc[] hits = collector.topDocs().scoreDocs;
//
//		List<SearchResultRecord> records = new ArrayList<SearchResultRecord>();
//
//		for(int i=0;i<hits.length;++i) {
//	          int docId = hits[i].doc;
//	          Document d = mSearcher.doc(docId);
//	          String url = d.get(NutchDocField.URL);
//	          String title = d.get(NutchDocField.Title);
//			  Logger.debug((i + 1) + ". " + url + " title: " + title + " | " + hits[i].score);
//	          SearchResultRecord record = SearchResultRecord.newRecord(title, url);
//	          records.add(record);
//	    }
//
//		return records;
//	}

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

		long startTime1 = System.nanoTime();
		//do search
		mSearcher.search(query, collector);
		long endTime1 = System.nanoTime();
		//calculate eclipsed time.
		long searchingTime = (endTime1 - startTime1);  //Note: divide by 1000000 to get milliseconds.

		int totalHits = collector.getTotalHits();
		Logger.debug("Found " + totalHits + " hits in " + searchingTime/1000000 +" milliseconds.");

		ScoreDoc[] hits = collector.topDocs().scoreDocs;

        if (totalHits > 0) {
            double highest = hits[0].score;
                    Logger.debug("Highest Lucene Score is " + highest);
        }

		//generate a array list to run ranking algorithm.
		List<SearchResultRecord> records = new ArrayList<SearchResultRecord>();

		for (int i = start; i < totalHits && i < MaxNumberOfResults; ++i) {
			int docId = hits[i].doc;
			Document doc = mSearcher.doc(docId);
			SearchResultRecord record = SearchResultRecord.newRecord(doc);
			record.setLuceneScore(hits[i].score);
			     records.add(record);
		}

		//TODO: run a ranking algorithm here.
//		MyRankingAlgorithm.rankRecordsByUrlScore(records);
        long startTime2 = System.nanoTime();
        MyRankingAlgorithm.rankRecords(records);
        long endTime2 = System.nanoTime();

        long rankingTime = (endTime2 - startTime2);
        Logger.debug("Ranking Time : " + rankingTime/1000000 + " milliseconds.");

        long totalTime = endTime2 - startTime1;
        Logger.info("Total eclipsed Time is " + totalTime / 1000000 + " milliseconds.");
        //create a result object.
		MySearchResults results = MySearchResults.create(start, totalHits);

        int end = records.size() < start + howMany ? records.size() : start + howMany;
		results.replaceAll(records.subList(start, end));

        results.setSearchingTime(searchingTime);
        results.setRankingTime(rankingTime);
        results.setTotalEclipsedTime(totalTime);

		for (SearchResultRecord r : results.getRecords()) {
			Logger.debug(r.toString());
		}
		return results;
	}

	public String getPath() {
		return mPath;
	}
}
