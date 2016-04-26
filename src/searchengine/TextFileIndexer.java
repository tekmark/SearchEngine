package searchengine;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class TextFileIndexer {
	
	private IndexWriter mWriter;
	
	
	//constructor. 
	public TextFileIndexer (String dir, Analyzer analyzer) throws IOException {
		Path path = new File(dir).toPath();
		Directory directory = FSDirectory.open(path);
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		mWriter = new IndexWriter (directory, config);
	}
	
	/**
	 * Indexes a file or directory
	 * @param fileName the name of a text file or a folder we wish to add to the index
	 * @throws java.io.IOException when exception
	 */
	public void index(String filename) throws IOException {
		System.out.println("indexing...");
		File file = new File(filename);
		FileReader reader = null;
		try {
			Document doc = new Document();
			reader = new FileReader(file);
			System.out.println("file path: " + file.getPath() + ", size: " + file.length());
			//===================================================
			// add contents of file
			//===================================================
			doc.add(new TextField("contents", reader));
			doc.add(new StringField("path", file.getPath(), Field.Store.YES));
			doc.add(new StringField("filename", file.getName(), Field.Store.YES));
			mWriter.addDocument(doc);
			System.out.println("add document");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        reader.close();
		}
		
		
		System.out.println("Done");
	}
	
	/**
	 * Close the index.
	 * @throws java.io.IOException when exception closing
	 */
	public void closeIndex() throws IOException {
		mWriter.close();
	}
}
