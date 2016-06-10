package searchengine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyRankingAlgorithm {
    private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    public static void rankRecordsByUrlScore (List<SearchResultRecord> records) {
        logger.debug("Rank records by url score");
        long startTime = System.nanoTime();
        //Ranking
        SearchResultRecordComparator comparator = new SearchResultRecordComparator();
        Collections.sort(records, comparator);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  //Note: divide by 1000000 to get milliseconds.
        logger.debug("Ranked " + records.size() + " records in " + duration/1000000 + " milliseconds.");
    }

    public static void rankByUrlAnalysis (List<SearchResultRecord> records) {

    }


    private static class SearchResultRecordComparator implements Comparator<SearchResultRecord> {

        @Override
        public int compare(SearchResultRecord r1, SearchResultRecord r2) {
            double score1 = r1.getUrlScore();
            double score2 = r2.getUrlScore();
            if (score1 < score2) {
                return 1;
            } else if (score1 > score2) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
