package searchengine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyRankingAlgorithm {
    private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    private static final double DEFAULT_LUCENE_SCORE_THRESHOLD = 0.15;

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

    public static void rankRecordsByCombinedScore(List<SearchResultRecord> records, double luceneScoreThreshold) {
        logger.debug("Rank records by using lucene scores and url scores. Threshold is " + luceneScoreThreshold);
        if (records.isEmpty()) {
            return;
        }

        //get highest lucene score.
        double highLuceneScore = records.get(0).getLuceneScore();
        double p = 0;
        if (1 - luceneScoreThreshold < 0 || 1 - luceneScoreThreshold >= 1) {
            p = 1 - DEFAULT_LUCENE_SCORE_THRESHOLD;
            logger.debug("Reset threshold to default value (" + DEFAULT_LUCENE_SCORE_THRESHOLD + ").");
        } else {
            p = 1 - luceneScoreThreshold;
        }
        double lowLuceneScore = highLuceneScore * p;
        logger.debug("Lucene score range is " + lowLuceneScore + " to " + highLuceneScore);
        //threshold
        int end_index = records.size() - 1;
        for (int i = 0; i < records.size(); ++i) {
            double score = records.get(i).getLuceneScore();
            if (score < lowLuceneScore) {
                end_index = i;
                break;
            }
        }
        logger.debug("Ranking end index is " + end_index);
        List<SearchResultRecord> newList = records.subList(0, end_index);
        SearchResultRecordComparator comparator = new SearchResultRecordComparator();
        Collections.sort(newList, comparator);

        //replace sorted elements;
        for (int i = 0; i < newList.size(); ++i) {
            SearchResultRecord record = newList.get(i);
//            logger.debug("#" + i + " " + record.toString());
            records.set(i, record);
        }
    }

    public static void rankRecords(List<SearchResultRecord> records) {
        rankRecordsByCombinedScore(records, DEFAULT_LUCENE_SCORE_THRESHOLD);
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
