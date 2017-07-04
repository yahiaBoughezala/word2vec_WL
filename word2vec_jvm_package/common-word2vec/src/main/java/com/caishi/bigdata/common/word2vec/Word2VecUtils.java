package com.caishi.bigdata.common.word2vec;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.feature.Word2VecModel;
import org.apache.spark.mllib.linalg.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Word2Vec 模型构建词向量 工具类
 * Created by fuli.shen on 2017/6/30.
 */
public class Word2VecUtils {

    public static final transient Logger LOGGER = LoggerFactory.getLogger(Word2VecUtils.class);

    /**
     * 通过Word2Vec 模型构建词向量的字符串表示
     * @param wordsVector
     * @return
     */
    public static String getStringWithWord2Vec(double[] wordsVector) {
        if (null == wordsVector || wordsVector.length == 0) {
            return null;
        }
        StringBuffer wordsBuffer = new StringBuffer("");
        int count = 0;
        int wordsVectorLength = wordsVector.length;
        for (int i = 0; i < wordsVectorLength; i++) {
            wordsBuffer.append(wordsVector[i]);
            count += 1;
            if (count != wordsVectorLength) {
                wordsBuffer.append(",");
            }
        }
        return wordsBuffer.toString().trim();
    }

    /**
     * 通过Word2Vec 模型构建词向量
     *
     * @param words      文章分词列表，空格分割。例如：我 爱 北京 天安门
     * @param vectorSize w2vModel 模型向量的长度
     * @param w2vModel   Word2VecModel
     * @return
     */
    public static double[] buildWordsVector(String[] words, int vectorSize, Word2VecModel w2vModel) {

        if (null == words || words.length == 0) {
            LOGGER.warn("words input is null when buildWordsVector,please check it .", words);
            return null;
        }
        if (null == w2vModel) {
            LOGGER.warn("w2vModel is null when buildWordsVector,please check it .");
            return null;
        }
        double[] vectors = new double[vectorSize];
        int totalWordCount = 0;
        for (String word : words) {
            try {
                if (word.length() >= 2) {
                    Vector wordVector = w2vModel.transform(word);
                    double[] wordVectorArray = wordVector.toArray();
                    for (int i = 0; i < wordVectorArray.length; i++) {
                        vectors[i] += wordVectorArray[i];
                    }
                    totalWordCount++;
                }
            } catch (Exception e) {
                LOGGER.warn("NOT Found [" + word + "] in word2Vec   when buildWordsVector");
            }
        }
        if (0 != totalWordCount) {
            for (int i = 0; i < vectors.length; i++) {
                vectors[i] = 1.0 * vectors[i] / totalWordCount;
            }
        }
        return vectors;
    }

    /**
     * 计算 word2ve 词向量的相似度
     *
     * @param vectorX
     * @param vectorY
     * @return
     */
    public static double computeCosineScore(double[] vectorX, double[] vectorY) {
        if (null == vectorX || null == vectorY || vectorX.length != vectorY.length) {
            return -1.0;
        }

        double sum = 0;
        double sumX = 0;
        double sumY = 0;
        for (int i = 0; i < vectorX.length; i++) {
            double xI = vectorX[i];
            double yI = vectorY[i];
            sum += xI * yI;
            sumX += Math.pow(xI, 2);
            sumY += Math.pow(yI, 2);
        }
        if (sumX <= 0 || sumY <= 0) {
            return -1.0d;
        }
        double rate = sum * 1.0 / Math.sqrt(sumX * sumY);
        return rate;
    }

    /**
     * 从HDFS上加载 Word2Vec 模型
     *
     * @param w2vModelPath
     * @return
     */
    public static Word2VecModel loadW2VModel(String w2vModelPath) {
        JavaSparkContext jsc = null;
        try {
            SparkConf conf = new SparkConf().setAppName("spark-Word2VecModel").setMaster("local");
            jsc = new JavaSparkContext(conf);
            Word2VecModel w2vModel = Word2VecModel.load(jsc.sc(), w2vModelPath);
            return w2vModel;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jsc.stop();
        }
        return null;
    }
}
