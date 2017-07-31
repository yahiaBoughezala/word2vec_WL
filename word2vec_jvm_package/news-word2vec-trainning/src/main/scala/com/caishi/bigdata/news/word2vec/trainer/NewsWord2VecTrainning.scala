package com.caishi.bigdata.news.word2vec.trainer

import com.caishi.bigdata.news.word2vec.utils.HdfsUtils
import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 训练新闻的Word2Vec 模型
  *
  * 1. 输入数据格式
  * 我 爱 北京 天安门
  * 我 爱 北京 长城
  * 2. 输出模型存储hdfs
  *

  * Created by fuli.shen on 2017/6/30.
  */
object NewsWord2VecTrainning {

  // create a function to tokenize each document
  def tokenize(doc: String): Seq[String] = {
    doc.split("\\s").filter(token => token.size >= 2).toSeq
  }

  def main(args: Array[String]) {

    /*
      val hdfsURL = "hdfs://10.4.1.1:9000"
      val appName = "spark-NewsWord2VecTrainning"
      val newsW2VSampling = "/news/w2v/sampling"
      val newsW2VModelPath = "/news/W2V/News_W2V_Model"
      val vectorSize=200
      val minCount=100
      val windowSize=5
      val numPartitions=24
      val numIterations=5
     */

    if (args.length != 9) {
      println("Usage:NewsWord2VecTrainning <hdfsUrl><appName><newsW2VSampling><newsW2VModelPath><vectorSize><minCount><windowSize><numPartitions><numIterations>")
      sys.exit(-1)
    }
    val Array(hdfsURL, appName, newsW2VSampling, newsW2VModelPath, vectorSize, minCount, windowSize, numPartitions, numIterations) = args
    val conf = new SparkConf().setAppName(appName)
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    // check that our tokenizer achieves the same result as all the steps above
    //println(textFile.flatMap(doc => tokenize(doc)).distinct.count)
    val textFile = sc.textFile(hdfsURL + newsW2VSampling)
    // 1. word2vec 原始数据分词、过滤
    val tokens = textFile.map(doc => tokenize(doc))
    // 2. word2vec 模型训练
    //val word2vec = new Word2Vec().setVectorSize(200).setMinCount(100).setWindowSize(5)
    val word2vec = new Word2Vec()
      .setVectorSize(vectorSize.trim.toInt) // 每个词向量的特征长度
      .setMinCount(minCount.trim.toInt) // 每个词至少出现MinCount
      .setWindowSize(windowSize.trim.toInt) //词的滑动窗口
      .setNumPartitions(numPartitions.trim.toInt)
      .setSeed(42L)
      .setNumIterations(numIterations.trim.toInt)

    try {
      val word2vecModel = word2vec.fit(tokens)
      // word2vec 模型
      HdfsUtils.deleteFile(hdfsURL, newsW2VModelPath, true)
      word2vecModel.save(sc, hdfsURL + newsW2VModelPath)
      // word2vec 模型 标记
      val newsW2VModelPathUpdate = newsW2VModelPath+".hasUpdate";
      HdfsUtils.deleteFile(hdfsURL, newsW2VModelPathUpdate, true)
      word2vecModel.save(sc, hdfsURL + newsW2VModelPathUpdate)
      sc.stop()
    } catch {
      case e: Exception => System.exit(-1)
    } finally {
      println("It all worked out.")
      System.exit(0)
    }

    //val word2vecModel = Word2VecModel.load(sc, "/news/W2V/News_W2V_Model")
  }
}
