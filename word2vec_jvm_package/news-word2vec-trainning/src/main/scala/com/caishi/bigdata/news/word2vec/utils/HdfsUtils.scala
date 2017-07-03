package com.caishi.bigdata.news.word2vec.utils

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FSDataOutputStream, FileSystem, Path}

/**
  * Created by fuli.shen n 4/19/17.
  */
object HdfsUtils {
  /**
    * @param hdfsURL   hdfs url ,eg: hdfs://ip:port
    * @param filePath  the path to delete.
    * @param recursive if path is a directory and set to
    *                  true, the directory is deleted else throws an exception. In
    *                  case of a file the recursive can be set to either true or false.
    */
  def deleteFile(hdfsURL: String, filePath: String, recursive: Boolean): Boolean = {
    val fileConf: Configuration = new Configuration
    fileConf.set("fs.defaultFS", hdfsURL)
    val os: FSDataOutputStream = null
    try {
      val fs: FileSystem = FileSystem.get(fileConf)
      val fileAbslutePath: String = hdfsURL + filePath
      if (fs.exists(new Path(fileAbslutePath))) {
        fs.delete(new Path(fileAbslutePath), recursive)
      }
      return true
    }
    catch {
      case e: Exception => {
        println(" HDFS file Error! hdfsUrl {}", hdfsURL)
        System.exit(-1)
        return false
      }
    } finally {
      try {
        if (os != null) {
          os.close
        }
      }
      catch {
        case e: Exception => {
          e.printStackTrace
        }
      }
    }
  }
}
