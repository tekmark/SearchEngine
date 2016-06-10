package searchengine;

import org.apache.lucene.document.Document;

public class SearchResultRecord implements Comparable<SearchResultRecord> {
	private String mUrl;
	private String mTitle;
	private double mScore;
	private double mInlink;
	private double mOutlink;
	private double mRelevance;

	private SearchResultRecord () {
		mScore = 0;
		mInlink = 0;
		mOutlink = 0;
	}
	
	public static SearchResultRecord newRecord(String title, String url) {
		SearchResultRecord record = new SearchResultRecord();
		record.mTitle = title;
		record.mUrl = url;
		return record;
	}

	public static SearchResultRecord newRecord (Document luceneDoc) {
		SearchResultRecord record = new SearchResultRecord();
		record.mUrl = luceneDoc.get(NutchDocField.URL);
		record.mTitle = luceneDoc.get(NutchDocField.Title);
		//extra info;
		String score = luceneDoc.get(NutchDocField.Score);
		if (score != null) {
			record.mScore = Double.parseDouble(score);
		}
		String inlink = luceneDoc.get(NutchDocField.Inlink);
		if (inlink != null) {
			record.mInlink = Double.parseDouble(inlink);
		}
		String outlink = luceneDoc.get(NutchDocField.Outlink);
		if (outlink != null) {
			record.mOutlink = Double.parseDouble(outlink);
		}

		return record;
	}

	public void setLuceneScore(double score) {
		mRelevance = score;
	}

	public String getUrl() {
		return mUrl;
	}
	
	public String getTitle() {
		return mTitle;
	}

	public Double getUrlScore() {
		return mScore;
	}

	public Double getLuceneScore() {
		return mRelevance;
	}

	public Double getInlink() {
		return mInlink;
	}

	public Double getOutlink() {
		return mOutlink;
	}

	public String toString() {
		String str = mUrl + " | ";
		str += mTitle;
		str += " | ";
		str += mRelevance;
		str += " | ";
		str += mScore;
		str += " | ";
		return str;
	}

	@Override
	public int compareTo(SearchResultRecord r) {
		double diff = this.mScore - r.mScore;
		if (diff < 0) {
			return -1;
		} else if (diff > 0) {
			return 1;
		} else {
			return 0;
		}
	}
}
