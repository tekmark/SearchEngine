package searchengine;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

public class MyQueryParserUtils {
	public static String toQueryString(String str) {
		String queryStr = str;
		//process string 
		
		return queryStr;
	}
	
	public static Query getQueryFromString(String str, Analyzer analyzer) {
		//String queryStr = MyQueryParserUtils.toQueryString(str);
		
		try {
			Query q = new QueryParser("contents", analyzer).parse(str);
			return q;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null; 
	}
}
