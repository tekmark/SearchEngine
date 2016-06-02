package searchengine;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class DumpFileIndexer {
	
	private final static Logger Logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
	
	private int mCurrRecNo = -1; 
	
	private List<DumpFileRecord> records;
	
	private Analyzer mAnalyzer;
	private IndexWriter mWriter;

	private ScoreFileReader mReader;

	public DumpFileIndexer(String dir, Analyzer analyzer) throws IOException {
		if (analyzer == null) {
			mAnalyzer = new MyAnalyzer();
		} else {
			mAnalyzer = analyzer;
		}
		setup(dir);
	}
	
	public DumpFileIndexer(String dir) throws IOException {
		mAnalyzer = new MyAnalyzer();
		//mAnalyzer = new EnglishAnalyzer();
		setup(dir);
	}
	
	private void setup(String dir) {
		//mCurrRecNo = INVALID_RECNO;
		records = new ArrayList<DumpFileRecord> ();
		Path path = new File(dir).toPath();
		Directory directory = null;
		try {
			Logger.info("Configure indexer");
			directory = FSDirectory.open(path);
			IndexWriterConfig config = new IndexWriterConfig(mAnalyzer);
			mWriter = new IndexWriter (directory, config);
			//
			mReader = new ScoreFileReader();
			mReader.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//return a list of documents in dump file.
	public List<Document> getDocuments(String path) {
		return null;
	}
	
	public DumpFileRecord getRecord (int recNo) {
		return null;
	}
	
	public List<DumpFileRecord> getAllRecords() {
		return records;
	}
	
	public void addDoc(Document doc) throws IOException {
		mWriter.addDocument(doc);
	}
	
//	public static void index(String dumpFile, String targetDir) throws IOException {
//		DumpFileIndexer indexer = new DumpFileIndexer(targetDir);	
//	}

	public void indexWithScores(String dumpFilePathStr) {
		Logger.info("indexing... with scores");
		try {
			FileInputStream fstream = new FileInputStream(dumpFilePathStr);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String currLine = br.readLine();
			DumpFileRecord record = null;
			String currRootLabel = null;

			int lineNum = 0;

			while (currLine != null) {
				//for debug.
//				if (lineNum >= 500000) {
//					Logger.info(lineNum + " lines processed");
//					break;
//				}

				// skip empty lines.
				if (currLine.isEmpty()) {
					currLine = br.readLine();
					lineNum++;
					continue;
				}

				// check if root label
				if (DumpFileUtils.startsWithRootLabel(currLine)) {
					currRootLabel = DumpFileUtils.getRootLabel(currLine);
					//Logger.debug("Find Root Label: " + currRootLabel);
				}
				//Logger.debug(currLine);
				if (currRootLabel.equals(DumpFileLabel.RECNO)) {
					if (record != null) {
						//check validation of record.
						if (record.isValid() == 0) {
							//Logger.trace(record.toString());
							//add record to record list;
							//records.add(record);
							Document doc = DumpFileUtils.convertDumpFileRecToNutchDoc(record, mWriter.getAnalyzer());
							if (doc != null) {
								//Logger.debug(doc.toString());
								String url = record.getUrl();
								double score = mReader.getScore(url);
								if (score > 0) {
									Logger.debug("Url: " + url + " | Score: " + score);
								}
								mWriter.addDocument(doc);
							} else {
								Logger.debug("Document is null.");
							}
						} else {
							Logger.warn("Invalid record. # of line: " + lineNum + ". Error Code: " + record.isValid());
							//currLine = br.readLine();
							//lineNum++;
							//continue;
						}
					}

					int recNo = DumpFileUtils.getRecNo(currLine);
					//Logger.trace("Find a new record # " + recNo);

					if (mCurrRecNo == DumpFileRecord.INVALID_RECNO || mCurrRecNo + 1 == recNo) {
						mCurrRecNo = recNo;
						record = new DumpFileRecord(mCurrRecNo);
					} else if (mCurrRecNo + 1 < recNo) {
						int fromRecNo = mCurrRecNo + 1;
						int toRecNo = recNo - 1;
						Logger.warn("Missing RECNO: " + fromRecNo + " - " + toRecNo +
								". Reset Current RecNo to " + recNo);
						mCurrRecNo = recNo;
						record = new DumpFileRecord(mCurrRecNo);
					} else {
						Logger.warn("Dump File bad record # : " + recNo);
					}
				} else if (record != null && currRootLabel == DumpFileLabel.URL) {
					String url = currLine.substring(DumpFileLabel.URL.length());
					record.setUrl(url);
				} else if (record != null && currRootLabel == DumpFileLabel.PARSE_DATA) {
					String subLabel = DumpFileUtils.getParseDataSubLabel(currLine);
					if (subLabel == DumpFileLabel.ParseData.TITLE) {
						String title = DumpFileUtils.getLabelInfo(currLine, subLabel);
						record.setTitle(title);
					}
				} else if (record != null && currRootLabel == DumpFileLabel.PARSE_TEXT) {
					if (DumpFileUtils.getRootLabel(currLine) == DumpFileLabel.NO_LABEL) {
						record.setParseText(currLine);
						//Logger.debug(record.toString());
					}
				} else {

				}
				//
				currLine = br.readLine();
				lineNum++;
			}

			if (record != null && record.isValid() == 0) {
				Logger.trace(record.toString());
			}
			Logger.info("Reach the end of dump file. # of lines: " + lineNum);
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void index(String dumpFilePath) {
		Logger.info("indexing...");
		try {
			FileInputStream fstream = new FileInputStream(dumpFilePath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String currLine = br.readLine();
			DumpFileRecord record = null;
			String currRootLabel = null;
			
			int lineNum = 0;
			
			while (currLine != null) {
				//for debug.
				/*if (lineNum >= 50000) {
					Logger.info(lineNum + " lines processed");
					break;
				}*/
				
				// skip empty lines. 
				if (currLine.isEmpty()) {
					currLine = br.readLine();
					lineNum++; 
					continue; 
				}

				// check if root label
				if (DumpFileUtils.startsWithRootLabel(currLine)) {
					currRootLabel = DumpFileUtils.getRootLabel(currLine);
					//Logger.debug("Find Root Label: " + currRootLabel);
				}
				//Logger.debug(currLine);
				if (currRootLabel.equals(DumpFileLabel.RECNO)) {
					if (record != null) {
						//check validation of record.
						if (record.isValid() == 0) {
							Logger.trace(record.toString());
							//add record to record list;
							//records.add(record);
							Document doc = DumpFileUtils.convertDumpFileRecToNutchDoc(record, mWriter.getAnalyzer());
							if (doc != null) {
								//Logger.debug(doc.toString());
								mWriter.addDocument(doc);
							} else {
								Logger.debug("Document is null.");
							}
						} else {
							Logger.warn("Invalid record. # of line: " + lineNum + ". Error Code: " + record.isValid());
							//currLine = br.readLine();
							//lineNum++; 
							//continue;
						}
					} 
				
					int recNo = DumpFileUtils.getRecNo(currLine);
					//Logger.trace("Find a new record # " + recNo); 

					if (mCurrRecNo == DumpFileRecord.INVALID_RECNO || mCurrRecNo + 1 == recNo) {
						mCurrRecNo = recNo;
						record = new DumpFileRecord(mCurrRecNo);
					} else if (mCurrRecNo + 1 < recNo) {
						int fromRecNo = mCurrRecNo + 1;
						int toRecNo = recNo - 1;
						Logger.warn("Missing RECNO: " + fromRecNo + " - " + toRecNo + 
								". Reset Current RecNo to " + recNo);
						mCurrRecNo = recNo;
						record = new DumpFileRecord(mCurrRecNo);
					} else {
						Logger.warn("Dump File bad record # : " + recNo);
					}
				} else if (record != null && currRootLabel == DumpFileLabel.URL) {
					String url = currLine.substring(DumpFileLabel.URL.length());
					record.setUrl(url);
				} else if (record != null && currRootLabel == DumpFileLabel.PARSE_DATA) {
					String subLabel = DumpFileUtils.getParseDataSubLabel(currLine);
					if (subLabel == DumpFileLabel.ParseData.TITLE) {
						String title = DumpFileUtils.getLabelInfo(currLine, subLabel);
						record.setTitle(title);
					}
				} else if (record != null && currRootLabel == DumpFileLabel.PARSE_TEXT) {
					if (DumpFileUtils.getRootLabel(currLine) == DumpFileLabel.NO_LABEL) {
						record.setParseText(currLine);
						//Logger.debug(record.toString());
					}
				} else {
					
				}
				//
				currLine = br.readLine();
				lineNum++; 
			}
			
			if (record != null && record.isValid() == 0) {
				Logger.trace(record.toString());
			}
			Logger.info("Reach the end of dump file. # of lines: " + lineNum);
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void index() {
		//indexSingleFile(PATH);
	}
	
	/**
	 * Close the index.
	 * @throws java.io.IOException when exception closing
	 */
	public void closeIndex() throws IOException {
		mWriter.close();
	}

	public void updateDocByUrl (String url, Document newDoc) {

	}

	public void updateUrlScoresTest(String scoreFilePath) {
		ScoreFileReader reader = new ScoreFileReader();
		reader.read();
		Term term = new Term(NutchDocField.URL, "www.rutgers.edu");
	}
}
