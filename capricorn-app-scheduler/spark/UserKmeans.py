#!/usr/bin/env python2.7
#coding=utf-8

from pyspark.mllib.clustering import KMeans
from pyspark.mllib.linalg import Vectors
from pyspark.sql import SQLContext
from pyspark import SparkContext,SparkConf
from lib.utils import Utils


class AppConf():

    def __init__(self, appName, hdfsUrl, userProfileDir, commonEventDir, toDir):
        self.appName = appName
        self.hdfsUrl = hdfsUrl
        self.userProfileDir = userProfileDir
        self.commonEventDir = commonEventDir
        self.toDir = toDir


class UserKmeans():

    def __init__(self, appConf, numCenter, numIteration):
        self.appConf = appConf
        self.numCenter = numCenter
        self.numIteration = numIteration

    def _initSpark(self):
        self.conf = SparkConf().setAppName(self.appConf.appName)
        self.sc = SparkContext(self.conf)


    def loadData(self):
        self.data = self.sc.textFile(self.appConf.userProfileDir)
        self.cachedData = self.data.map(lambda line: Utils.transform_json(line)).cache()
        parsedData = self.cachedData.map(lambda p: str(list(p.values())).replace('[','').replace(']','').split(',').map(lambda x: float(x)))
        vector = parsedData.map(lambda x: Vectors.dense(x))
        model = KMeans.train(vector, self.numCenter, self.numIteration)



