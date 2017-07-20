#!/usr/bin/python
#coding=utf-8

class TopNews():

    NEWS_DATA_FILE = ''

    class stage:
        NEWS_DATA_FILE = '/home/hadoop/data/hivedata/top_news.txt'
        BATCH_FILE = 'top_news.sh'

    class online:
        NEWS_DATA_FILE = '/home/hadoop/data/hivedata/top_news.txt'
        BATCH_FILE = 'top_news.sh'