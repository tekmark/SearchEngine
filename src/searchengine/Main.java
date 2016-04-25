package searchengine;

import java.io.IOException;

import java.nio.charset.Charset;
import org.apache.lucene.analysis.Tokenizer;

public class Main {
	public static String path="test/test";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		System.out.println("Hello World");
		//Tokenizer.tokenize();
		
		try {
			String s = HtmlProcessor.readAlltoString(path, Charset.defaultCharset());
			//System.out.println(s);
			String ss = HtmlProcessor.extractText(s);
//			System.out.print(s);
			//Tokenizer.tokenize(ss);
			//String text = "Apache be the simplest yet, a of Powerful java based search library good cheap connective.";
			MyAnalyzer.run(ss);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print("ERROR");
			e.printStackTrace();
		}
	}	
}
