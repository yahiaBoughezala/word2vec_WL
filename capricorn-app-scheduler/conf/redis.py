#!/usr/bin/env python2.7
#coding=utf-8

class RedisBase():

    FIELD_SEP = '\x00'



class ChannenNewsRedis():

    class stage:
        'STAGE'

        SERVERS=[
           {'name':'channel1','host':'10.1.1.122','port':6380,'db':0},
           {'name':'channel2','host':'10.1.1.132','port':6380,'db':0},
           {'name':'channel3','host':'10.1.1.142','port':6380,'db':0},
           ]

        HOST='10.1.1.122'
        PORT=6380

    class online:
        'ONLINE'
        SERVERS=[
           {'name':'channel1','host':'10.3.1.1','port':6379,'db':0},
           {'name':'channel2','host':'10.3.1.2','port':6379,'db':0},
           {'name':'channel3','host':'10.3.1.3','port':6379,'db':0},
           ]

        HOST='10.3.1.1'
        PORT=6379

