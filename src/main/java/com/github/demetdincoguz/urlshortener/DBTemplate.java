package com.github.demetdincoguz.urlshortener;

import com.mongodb.*;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;

public class DBTemplate {
    private static DBTemplate template;
    private MongoClient mongoClient;
    private DBTemplate() throws UnknownHostException {
        mongoClient = new MongoClient();
    }

    public static DBTemplate getInstance() throws UnknownHostException {
        if(template == null){
            synchronized (DBTemplate.class) {
                if (template == null) {
                    template = new DBTemplate();
                }
            }
        }
        return template;
    }

    public boolean saveUrl(String shortUrl, String longUrl) {
        if(!shortUrl.equals("") && getUrl(shortUrl).equals("")) {
            DB db = mongoClient.getDB("url-db");
            DBCollection collection = db.getCollection("shortUrls");
            BasicDBObject doc = new BasicDBObject("_id", new ObjectId()).append("shortUrl", shortUrl).append("longUrl", longUrl);
            collection.insert(doc);
        }
        return true;

    }
    public String getUrl(String shortUrl){
        DB db =  mongoClient.getDB("url-db");
        DBCollection collection = db.getCollection("shortUrls");

        QueryBuilder queryBuilder = new QueryBuilder().start("shortUrl").is(shortUrl);
        DBObject query=queryBuilder.get();

        DBCursor cursor = collection.find(query, new BasicDBObject("longUrl", true));
        try{
            while(cursor.hasNext()){
                DBObject cur = cursor.next();
                System.out.println(cur);
                return cur.get("longUrl").toString();
            }
        }finally {
            cursor.close();
        }

        return "";
    }
}
