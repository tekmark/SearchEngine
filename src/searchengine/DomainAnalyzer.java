package searchengine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Hashtable;

/**
 * Created by chaohan on 5/26/16.
 */
public class DomainAnalyzer {
    private final static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    private Hashtable<String, Integer> mHostnameMap;
    private Hashtable<String, Integer> mDomainMap;

    public DomainAnalyzer() {
        mHostnameMap = new Hashtable<>();
        mDomainMap = new Hashtable<>();
    }

    public void analyzeDomain(String domainDumpPath) {
        try {
            FileInputStream fstream = new FileInputStream(domainDumpPath);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String currLine = br.readLine();
            int lineNum = 0;
            while (currLine != null) {
                currLine = br.readLine();
                lineNum++;
            }

            logger.info("Number or lines: " + lineNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void analyzeHostname(String hostnameDumpPath) {}

}
