package com.caishi.bigdata.news.word2vec.examples;

import com.caishi.bigdata.common.word2vec.Word2VecUtils;
import org.apache.spark.mllib.feature.Word2VecModel;
import scala.Tuple2;

/**
 * Created by fuli.shen on 2017/7/2.
 */
public class NewsWord2VecSynonyms {
    // word2vec 模型存储路径
    public static final String w2vModelPath = "hdfs://10.4.1.4:9000/news/W2V/News_W2V_Model/";

    public static void main(String[] args) {
        if (args.length == 1) {
            System.out.println("Usage: java NewsWord2VecSynonyms <word>");
            System.exit(-1);
            return;
        }
        String arg = args[0];
        // w2v 模型实例对象
        Word2VecModel w2vModel = Word2VecUtils.loadW2VModel(w2vModelPath);
        System.out.println("-------------------------------【"+arg+"】  关键词的同义词-----------------------------------");
        Tuple2<String, Object>[] w2vModelSynonyms = w2vModel.findSynonyms(arg, 10);
        for (Tuple2<String, Object> tuple2 : w2vModelSynonyms) {
            String word = tuple2._1();
            Double cosineSimilarity = (Double) tuple2._2();
            System.out.println(word + "\t" + cosineSimilarity);
        }
    }
}
