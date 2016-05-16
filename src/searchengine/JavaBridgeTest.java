package searchengine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JavaBridgeTest {
	private final static Logger Logger = LogManager.getLogger(JavaBridgeTest.class);
	
	private String msg;
	public JavaBridgeTest() {
		msg = "Test(default)";
	}
	
	public JavaBridgeTest(String str) { 
		msg = "TEST";
	}
	
	public String getTestMsg() {
		return "Test";
	}
	
	public String getMsg() {
		return Logger.getName();
	}
}
