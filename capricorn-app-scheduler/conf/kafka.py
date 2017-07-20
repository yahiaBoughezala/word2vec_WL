#!/usr/bin/env python2.7
#coding=utf-8

class TopNewsKafka():


    class stage:
        'STAGE'
        TOP_NEWS_KAFKA_TOPIC = 'topNewsUpdate'
        KAFKA_BROKER_SERVERS= '10.1.1.120:9092,10.1.1.130:9092,10.1.1.140:9092'
        KAFKA_CONCURRENCY_THREAD_SIZE = 2


    class online:
        'STAGE'
        TOP_NEWS_KAFKA_TOPIC = 'topNewsUpdate'
        KAFKA_BROKER_SERVERS = '10.4.1.201:9092,10.4.1.202:9092,10.4.1.203:9092'
        KAFKA_CONCURRENCY_THREAD_SIZE = 2
