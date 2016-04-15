package searchengine;

import java.util.StringTokenizer;


public class Tokenizer {
	public static void tokenize(String s) {
		StringTokenizer st = new StringTokenizer(s);
	     while (st.hasMoreTokens()) {
	         System.out.println(st.nextToken());
	     }
	}
}
