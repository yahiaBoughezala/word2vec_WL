package com.caishi.bigdata.categorize.textseg.common.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * <function>MongoDB工具类</function>
 * Created by fuli.shen on 2016/5/17.
 */
public class MongoUtil {

    private static transient MongoClient mongoClient = null;

    private MongoUtil() {
    }

    /**
     * 获取MongoDB实例
     *
     * @param address
     * @param userName
     * @param password
     * @param databaseName
     * @return
     */
    public static MongoClient getInstance(String address, String userName, String password, String databaseName) {
        if (null == mongoClient) {
            final List<ServerAddress> seeds = new ArrayList<ServerAddress>();
            String[] addresses = address.split(",");
            for (String addr : addresses) {
                seeds.add(new ServerAddress(addr.split(":")[0], new Integer(addr.split(":")[1])));
            }
            final List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();
            credentialsList.add(MongoCredential.createCredential(userName, databaseName, password.toCharArray()));
            mongoClient = new MongoClient(seeds, credentialsList);
        }
        return mongoClient;
    }

    /**
     * 更新或者插入一条记录
     * @param address
     * @param userName
     * @param password
     * @param database
     * @param collection
     * @param doc
     * @param id
     * @return
     */
    public static long upsert(String address, String userName, String password, String database, String collection,Document doc,String id){
        MongoCollection<Document> mongoCollection = getInstance(address, userName, password, database).getDatabase(database).getCollection(collection);
        UpdateResult updateResult = mongoCollection.updateMany(Filters.eq("_id", id), new Document("$set", doc), new UpdateOptions().upsert(true));
        return updateResult.getModifiedCount();
    }

    /**
     * 根据用户id从collection中获取一条记录
     * @param id
     * @param address
     * @param userName
     * @param password
     * @param database
     * @param collection
     * @return
     */
    public static Document getFirstDoc( String address, String userName, String password, String database, String collection,String id) {
        MongoCollection<Document> mongoCollection = getInstance(address, userName, password, database).getDatabase(database).getCollection(collection);
        Document doc = mongoCollection.find(Filters.eq("_id",id)).first();
        return null == doc?new Document():doc;
    }

}
