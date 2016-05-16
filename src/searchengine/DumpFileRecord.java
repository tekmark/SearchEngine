package searchengine;

import java.net.MalformedURLException;
import java.net.URL;

public class DumpFileRecord {
	
	public static final int INVALID_RECNO = -1;
	
	public static class ErrorCode {
		public static final int RECNO_ERROR = -1;
		public static final int URL_ERROR = -2;
		public static final int NO_PARSE_TEXT_ERROR = -3; 
	}
	
	private int mRecNo;
	private String mUrlStr;
	private String mParseText;
	private String mTitle; 
	
	public DumpFileRecord () {
		mRecNo = INVALID_RECNO;
		mUrlStr = null;
		mTitle = null;
		mParseText = null;
	}
	
	public DumpFileRecord(int recNo) {
		mRecNo = recNo;
		mUrlStr = null;
		mTitle = null;
		mParseText = null;
	}
	
	public void setRecNo(int recNo) {
		mRecNo = recNo;
	}
	
	public void setUrl (String url) {
		mUrlStr = url;
	}
	
	public void setParseText(String text) {
		mParseText = text;
	}
	
	public void setTitle(String title) {
		mTitle = title;
	}
	
	public int getRecNo() {
		return mRecNo;
	}
	public String getParseText() {
		return mParseText;
	}
	
	public String getUrl() {
		return mUrlStr;
	}
	
	//public URL getURL() {
	//	return mURL;
	//}
	
	public String getTitle() {
		return mTitle;
	}
	
	public static DumpFileRecord newRecord(int recNo, String url, String text) {
		DumpFileRecord rec = new DumpFileRecord();
		rec.mRecNo = recNo;
		rec.mUrlStr = url;
		rec.mParseText = text;
		return rec;
	}
	
	public int isValid() {
		if (mRecNo == INVALID_RECNO) {
			return ErrorCode.RECNO_ERROR;
		} else if (mUrlStr == null) {
			return ErrorCode.URL_ERROR;
		} else if (mParseText == null || mParseText.isEmpty()) {
			return ErrorCode.NO_PARSE_TEXT_ERROR;
		} else {
			return 0;
		}
	}
	
	public String toString() {
		String strRecord = "";
		strRecord += mRecNo;
		strRecord += "|";
		strRecord += mUrlStr;
		strRecord += "|";
		strRecord += mTitle;
		strRecord += "|"; 
		//strRecord += mParseText;
		return strRecord;
	}
}
