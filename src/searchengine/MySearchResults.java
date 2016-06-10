package searchengine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaohan on 6/3/16.
 */

public class MySearchResults {
    private List<SearchResultRecord> mRecords;;
    private int mNumOfHits;
    private int mStart;
    private long mEclipsedTime;

    private MySearchResults() {
        mRecords = new ArrayList<SearchResultRecord>();
        mNumOfHits = 0;
        mStart = 0;
    }

    static MySearchResults create (int start, int numOfTotalHits, long eclipsedTime) {
        MySearchResults results = new MySearchResults();
        results.mStart = start;
        results.mNumOfHits = numOfTotalHits;
        results.mEclipsedTime = eclipsedTime;
        return results;
    }

//    public String getSearchingSecondString() {
//        return "";
//    }

    public void add(SearchResultRecord record) {
        mRecords.add(record);
    }

    public void replaceAll(List<SearchResultRecord> records) {
        mRecords = null;
        mRecords = new ArrayList<>(records);
    }

    public int getNumOfRecords () {
        return mRecords.size();
    }

    public int getTotalNumberOfHits() {
        return mNumOfHits;
    }

    public long getEclipsedTime() {
        return mEclipsedTime;
    }

    public long getEclipsedTimeMilliSec() {
        return mEclipsedTime / 1000000;
    }

    public int getStart() {
        return mStart;
    }

    public List<SearchResultRecord> getRecords() {
        return mRecords;
    }
}
