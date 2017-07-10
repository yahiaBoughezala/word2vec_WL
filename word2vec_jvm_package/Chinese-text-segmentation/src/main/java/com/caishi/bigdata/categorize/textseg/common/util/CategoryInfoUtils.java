package com.caishi.bigdata.categorize.textseg.common.util;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by devbox-4 on 4/21/17.
 */
public class CategoryInfoUtils {


    public static Map<Integer, String> categorysMap = new TreeMap<>();


    static {
         loadCategoryInfo();
    }

    /**
     * Load News Category_INFO Predict Metrics
     */
    public static void loadCategoryInfo() {
        MongoClient mongoClient = new MongoClient(Arrays.asList(new ServerAddress("10.3.1.4:27017")));
        MongoDatabase mongoDatabase = mongoClient.getDatabase("caishi");
        MongoCollection classifierMetrics = mongoDatabase.getCollection("category_info");// category_info : news category_info
        MongoCursor cursor = null;
        try {
            FindIterable iterable = classifierMetrics.find();
            cursor = iterable.iterator();
            while (cursor.hasNext()) {
                Document document = (Document) cursor.next();
                Object categoryId = document.get("categoryId");
                Object categoryName = document.get("categoryName");
                categorysMap.put(Integer.valueOf(categoryId.toString()), categoryName.toString());
            }

            /**
             * old category info
             categoryId	categoryName	categoryLevel	pid	            mapping_categoryId	    count
             11100	    国际新闻	        2	        10000	        20000	                1051(23957)
             11300	    外交	            2	        10000	        20100	                44(17086)
             31300	    国际社会	        2	        30000	        20300	                1495(52385)
             60700	    创业	            2	        60000	        70300	                147(11231)
             60900	    企业名人	        2	        60000	        70100	                118(9938)
             61100	    投资	            2	        60000	        70500	                87(12420)
             140900	    天文	            2	        140000	        160100	                73(8244)
             141100	    地理	            2	        140000	        160300	                77(6495)
             141300	    生物	            2	        140000	        160500	                91(8788)
             141500	    数理化	            2	        140000	        160700	                89(6004)
             210500	    考古发现	        2	        210000	        220300	                68(7212)
             260900	    生肖	            2	        260000	        410300	                268(28367)
             500000	    宗教	            1	        500000	        260300	                11(8064)
             */
            categorysMap.put(20000, "国际新闻");
            categorysMap.put(20100, "外交");
            categorysMap.put(20300, "国际社会");
            categorysMap.put(70300, "创业");
            categorysMap.put(70100, "企业名人");
            categorysMap.put(70500, "投资");
            categorysMap.put(160100, "天文");
            categorysMap.put(160300, "地理");
            categorysMap.put(160500, "生物");
            categorysMap.put(160700, "数理化");
            categorysMap.put(220300, "考古发现");
            categorysMap.put(410300, "生肖");
            categorysMap.put(260300, "宗教");
            System.out.println("categorysMap.size:" + categorysMap.size());
            System.out.println("categorysMap:" + categorysMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }
}
