package searchengine;

public class SearchResultRecord implements Comparable{
	private String mUrl;
	private String mTitle;
	
	private SearchResultRecord () {
		
	}
	
	public static SearchResultRecord newRecord(String title, String url) {
		SearchResultRecord record = new SearchResultRecord();
		record.mTitle = title;
		record.mUrl = url;
		return record;
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	public String getTitle() {
		return mTitle;
	}

	@Override
	public int compareTo(Object o) {

		return 0;
	}
}
