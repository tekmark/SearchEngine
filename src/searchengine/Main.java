package searchengine;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;


public class Main {
	private final static String VERSION = "0.0.1";
	
	private final static Logger Logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	public static String path="test/test";	
	
	public static void main(String[] args) throws IOException {
		
		// create the command line parser
		CommandLineParser parser = new DefaultParser();
		
		// create the Options
		Options options = new Options();
		options.addOption("v", "version", false, "print version information and exit.");
		options.addOption("h", "help", false, "print this massage.");
		options.addOption("V", "verbose", false, "verbose mode, print more message");
		options.addOption("s", "search", true, "search string");
		options.addOption("d", "directory", true, "directory");
		options.addOption("i", "index", true, "index dump file");
		options.addOption("t", "test", true, "test dump file");
		options.addOption("S", "score", true, "dump scores");
		
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("searchengine", options);
			} else if (line.hasOption("version")) {
				System.out.println("Version - " + VERSION);
			} else if (line.hasOption("search")) {
				String str = line.getOptionValue("search", "no value");
				if (line.hasOption("directory")) {
					String path = line.getOptionValue("directory");
					doSearch(str, path);
				} else {
					System.out.println("missing --directory");
				}
			} else if (line.hasOption("index")) {
				String dumpfilePathStr = line.getOptionValue("index");
				if (line.hasOption("directory")) {
					String targetPathStr = line.getOptionValue("directory");
					doIndex(dumpfilePathStr, targetPathStr);
				}
			} else if (line.hasOption("test")) {
				//String dumpFilePathStr = line.getOptionValue("test");
				String dumpFilePathStr = "/Users/chaohan/git/SearchEngine/aaa";
				doTest(dumpFilePathStr);
			} else if (line.hasOption("score")) {
				ScoreFileReader reader = new ScoreFileReader();
				reader.read();
			} else {
				System.out.println("NO OPTION MACTHED");
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println( "Unexpected exception:" + e.getMessage() );
		}
		
		/*
		MyAnalyzer analyzer = new MyAnalyzer();
		
		try {
			DumpFileIndexer indexer = new DumpFileIndexer("abc", analyzer);
			indexer.index();
			indexer.closeIndex();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MySearcher searcher = new MySearcher("abc");
		
		searcher.search("rutgers univerity"); 
		*/
	}
	
	private static void doSearch(String words, String pathStr) throws IOException {
		Logger.info("Search : " + words + ", Path: " + pathStr);
		//validate directory
		File file = new File(pathStr);
		if (file.exists() && file.isDirectory()) {
			Logger.debug("Diectory exists");
			MySearcher searcher = new MySearcher(pathStr);
			searcher.search(words);
		} else {
			Logger.error("Check " + pathStr + ".");
		}
	}
	
	private static void doIndex(String dumpFilePathStr, String targetPathStr) {
		Logger.info("Index. Dump File path: " + dumpFilePathStr 
				+ ". Target path: " + targetPathStr);
		File file = new File(targetPathStr);
		if (file.isDirectory() && file.exists()) {
			Logger.warn("Target Directory exists.");
		}
		File dumpFile = new File(dumpFilePathStr);
		if (!dumpFile.exists()) {
			Logger.error("Dump file path: " + dumpFilePathStr + "doesn't exist.");
			return;
		} else if (!dumpFile.isDirectory()) {
			Logger.info("Find dump file: " + dumpFilePathStr + ". Dump file size: " + dumpFile.length());
		} else {
			Logger.warn("Check dump file.");
			return;
		}
		MyAnalyzer analyzer = new MyAnalyzer();
		try {
			DumpFileIndexer indexer = new DumpFileIndexer(targetPathStr, analyzer);
			indexer.indexWithScores(dumpFilePathStr);
			indexer.closeIndex();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void doTest(String pathStr) throws IOException {
		Logger.info("Test file: " + pathStr);
		//process(dumpFilePathStr);
//		DumpFileIndexer indexer = new DumpFileIndexer("abced");
//		indexer.index(dumpFilePathStr);
		//validate directory
//		File file = new File(pathStr);
//		if (file.exists() && file.isDirectory()) {
//			Logger.debug("Diectory exists");
//			MySearcher searcher = new MySearcher(pathStr);
//			searcher.searchByUrlTest();
//		} else {
//			Logger.error("Check " + pathStr + ".");
//		}
	}


}
