#!/usr/bin/env python2.7
#coding=utf-8
import os
from lib.log import MyLogger


class HDFSApi():
    '''

    TODO: current invoke cli command for temp usage
    Need implement with hdfs rest api
    '''
    def __init__(self):
        self.logger = MyLogger.getLogger()


    def createDirectory(self, dir):
        cmd = 'hadoop fs -mkdir %s' % dir
        ret = os.system(cmd)
        if ret != 0:
            self.logger.error("Failed to create directory on hdfs path: %s" % dir)
        else:
            self.logger.info("Create directory on hdfs successfully:%s" % dir)

    def putFiles(self, fn, dir):
        cmd = 'hadoop fs -put -f %s %s' % (fn, dir)
        ret = os.system(cmd)
        if ret != 0:
            self.logger.error("Failed to copy files %s to path: %s" % (fn, dir))
        else:
            self.logger.info("copy files %s to path: %s successfully" % (fn, dir))

