package searchengine;

import com.mongodb.*;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

/**
 * Created by chaohan on 6/7/16.
 */
public class MongoDBJConnector {
    private final static Logger Logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    private MongoClient mClient;
    private MongoDatabase mDatabase;

    public MongoDBJConnector () {
        mClient = new MongoClient();
    }

    public MongoDBJConnector (String address, int port) {
        try {
            mClient = new MongoClient(address, port);
        } catch (MongoClientException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean testConnection() {
        try {
            ServerAddress serverAddr = mClient.getAddress();
            System.out.println("MongoDB is connected. Host: " + serverAddr.getHost() + " port: " + serverAddr.getPort());
            return true;
        } catch (MongoException e) {
            System.out.println("MongoDB is not connected");
            System.out.println("Message:" + e.getMessage());
            return false;
        }
    }

    public boolean connectDataBase(String dbName) {
        try {
            mDatabase = mClient.getDatabase(dbName);
            ServerAddress serverAddr = mClient.getAddress();
            Logger.info("MongoDB connected. " + serverAddr.toString());
            Logger.info("DB name : " + dbName);
            return true;
        } catch (MongoException e) {
            Logger.error("Failed to connect to database : " + dbName +". Please check connections");
            return false;
        }
    }

    public Document getUrlDocByUrl(String url) {
        MongoCollection<Document> urlsCollection = mDatabase.getCollection("mytest");

        BasicDBObject query = new BasicDBObject("url", url);
        MongoCursor<Document> cursor = urlsCollection.find(query).limit(1).iterator();
        if (cursor.hasNext()) {
            return cursor.next();
        } else {
            return null;
        }
    }

    public String getHostInfoByUrl (String host) {
        MongoCollection<Document> urlsCollection = mDatabase.getCollection("hosts");

        BasicDBObject query = new BasicDBObject("host", host);
        MongoCursor<Document> cursor = urlsCollection.find(query).limit(1).iterator();
        if (cursor.hasNext()) {
            Document document = cursor.next();
            return document.getString("stats");
        } else {
            return "";
        }
    }

    public String getDomainInfoByUrl (String domain) {
        MongoCollection<Document> urlsCollection = mDatabase.getCollection("domains");

        BasicDBObject query = new BasicDBObject("domain", domain);
        MongoCursor<Document> cursor = urlsCollection.find(query).limit(1).iterator();
        if (cursor.hasNext()) {
            Document document = cursor.next();
            return document.getString("stats");
        } else {
            return "";
        }
    }
}
