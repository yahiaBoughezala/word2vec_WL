#!/usr/bin/env python2.7
#coding=utf-8

import logging
import time

class MyLogger(object):

    logger = None
    LOG_FORMAT = logging.Formatter("[%(levelname)s][%(asctime)s][%(module)s][%(funcName)s line:%(lineno)d]%(message)s")
    #LOG_PATH = '/home/hadoop/caishi/log/apps/'
    LOG_PATH = '../logs/'

    @staticmethod
    def getLogger(log_name='app'):

        if MyLogger.logger is not None:
            return MyLogger.logger

        now_data=time.strftime('%Y-%m-%d',time.localtime(time.time()))
        MyLogger.logger = logging.getLogger(log_name)
        stream_handler = logging.StreamHandler()
        stream_handler.setLevel(logging.DEBUG)
        stream_handler.setFormatter(MyLogger.LOG_FORMAT)


        debug_log_file = MyLogger.LOG_PATH + log_name + '.log'
        debug_file_handler = logging.FileHandler(debug_log_file)
        debug_file_handler.setFormatter(MyLogger.LOG_FORMAT)
        debug_file_handler.setLevel(logging.DEBUG)

        MyLogger.logger.addHandler(stream_handler)
        MyLogger.logger.setLevel(logging.DEBUG)
        MyLogger.logger.addHandler(debug_file_handler)
        # MyLogger.logger.addHandler(err_file_handler)

        return MyLogger.logger


if __name__ == "__main__":
    logger = MyLogger.getLogger()


    while True:
        logger.debug("this is debug info")
        logger.info("this is info msg")
        logger.warn("this is warn msg")
        logger.error("this is error msg")
        time.sleep(60)
