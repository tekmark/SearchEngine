package searchengine;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.net.URL;
import java.util.Hashtable;

/**
 * Created by chaohan on 5/26/16.
 */
public class ScoreFileReader {
    private final static Logger Logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    static public String path = "/Users/chaohan/apache-nutch-1.11/topn1/part-00000";

    private Hashtable<String, Double> scroes;
    public ScoreFileReader() {
        scroes = new Hashtable<>();
    }
    public void read() {
        try {
            FileInputStream fstream = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String currLine = br.readLine();
            int lineNum = 0;
            while (currLine != null) {
//                Logger.debug(currLine);
                String [] strings = currLine.split("\\s");
                if (strings.length == 2) {
                    String scoreStr = strings[0];
                    String urlStr = strings[1];
                    Double score = Double.parseDouble(scoreStr);
                    //add score to map.
                    scroes.put(urlStr, score);
                    //Logger.debug(urlStr + " | " + scoreStr);
                } else {
                    Logger.warn("#" + lineNum + " Invalid : " + currLine + ".Skip." );
                }
                currLine = br.readLine();
                lineNum++;
            }
            Logger.info("Number or lines: " + lineNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getScore(String url) {
        Double score = scroes.get(url);
        if (score == null) {
            return 0;
        } else {
            return score;
        }
    }
}
