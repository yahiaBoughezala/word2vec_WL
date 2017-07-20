#!/usr/bin/env python2.7
#coding=utf-8

import os,sys
sys.path.append('..')

from conf.app import TopNews
from conf.environ import HQL_PATH, get_config
from conf.redis import ChannenNewsRedis
from conf.kafka import TopNewsKafka
from conf.email import MailServer
from lib.log import MyLogger
from lib.redis_client import RedisClient
from lib.kaffa import KafkaApi
from lib.mail import MonitorEmail
from lib.utils import Utils
from model.constants.NewsRedisKey import NewsRedisKey
from model.constants.NewsType import NewsType
from model.entity.News import News
from model.msg.TopNewsUpdate import TopNewsUpdate, NewsUpdateStatus
import json


class GenerateTopNews():
    '''

     生成排行榜新闻:
        执行hive job, crontab * 0 3/15/21   3点 15点 21点 执行
        结果导入redis 队列
        发送kafka消息给API清缓存

    '''

    def __init__(self, appname):
        self.logger = MyLogger.getLogger(appname)
        self.mail = MonitorEmail(MailServer.SMTP_SEND_TO)


    def exec_hive(self):
        batch = HQL_PATH + get_config(TopNews, 'BATCH_FILE')
        if os.path.exists(batch):
            cmd = 'sh %s' % batch
        else:
            self.logger.error("Exception: could not find the batch file %s" % batch)

        result = os.system(cmd)
        if result == 0:
            self.logger.info("hive job for generate top100 hot news successfully")
        else:
            self.logger.error("hive job execute failed")

        return result


    def send_msg(self, msg):
        broker = get_config(TopNewsKafka, 'KAFKA_BROKER_SERVERS')
        topic = get_config(TopNewsKafka, 'TOP_NEWS_KAFKA_TOPIC')
        kafka = KafkaApi(broker, topic)
        try:
            kafka.send_msg(msg, async=True)
            self.logger.info("Succssfully to send msg to api client, msg=%s" % msg)
        except Exception,ex:
            self.logger.error("Exception: Failed to send kafka msg to api client")
            self.logger.error(ex)


    def import2redis(self):
        host = get_config(ChannenNewsRedis, 'HOST')
        port = get_config(ChannenNewsRedis, 'PORT')
        data_file = get_config(TopNews, 'NEWS_DATA_FILE')
        redisConn = RedisClient(host,port).getConnection()
        if os.path.exists(data_file):
            with open(data_file) as f:
                lines = f.readlines()
                if len(lines) != 0:
                    self.logger.info("Get %s generated top news" % len(lines))
                    key_exist = redisConn.keys(NewsRedisKey.RANK_TOPN_NEWS.keyPattern)
                    if len(key_exist) != 0:
                        self.logger.info("clear all the cached news before update top news")
                        redisConn.delete(NewsRedisKey.RANK_TOPN_NEWS.keyPattern)
                        self.logger.info("the rank news key has alredy deleted")
                else:
                    self.logger.info("Exception: get empty result from top_news.txt, skip import2redis")
                    return False

                for data in lines:
                    try:
                        x = data.strip('\n').split('\t')
                        newsid,score = x[0], x[1]
                        key = News(newsid, NewsType.NEWS)
                        key = str(key)
                        redisConn.zadd(NewsRedisKey.RANK_TOPN_NEWS.keyPattern, key, round(float(score),3))
                        #TODO  set expire time?
                        #redisConn.expire(NewsRedisKey.RANK_TOPN_NEWS.keyPattern, NewsRedisKey.RANK_TOPN_NEWS.ttl)

                    except Exception,ex:
                        self.logger.error("Exception: import news into redis failed, newsid=%s" % newsid)
                        self.logger.error(ex)
                        return False
        else:
            self.logger.error("Exception: Could not found %s, please check the file path" % TopNews.NEWS_DATA_FILE )
            return False

        self.logger.info("Import news into redis successfully")
        return True

    def run(self):
        self.logger.info("Start to exec hive job")
        #import time
        #ts = Utils.get_unix_timestamp(time.time())
        hive_result = self.exec_hive()
        if hive_result == 0:
            redis_result = self.import2redis()
            if redis_result:
                # msg = json.dumps(Utils.convert_object(TopNewsUpdate(ts, NewsUpdateStatus.SUCCESS)))
                # self.send_msg(msg)
                Utils.send_notify_mail('Top_New: All Job Successed', 'All the job execute successfully')
            else:
                # msg = json.dumps(Utils.convert_object(TopNewsUpdate(ts, NewsUpdateStatus.FAILED)))
                # self.logger.info("Exception: failed to import the data to redis, send failed msg to api service")
                # self.send_msg(msg)
                Utils.send_notify_mail('Top_News: Redis Import Failed', 'Import the top news data to redis failed, please check the log')

        else:
            self.logger.info("Excepton: failed to exec hive, skip import result to redis and notify kafka")
            # msg = json.dumps(Utils.convert_object(TopNewsUpdate(ts, NewsUpdateStatus.FAILED)))
            # self.send_msg(msg)
            Utils.send_notify_mail('Top_News: Hive Job Failed', 'Hive job exec failed, please check the job log')

    def test_send_success_msg(self):
        import time
        ts = Utils.get_unix_timestamp(time.time())
        msg = json.dumps(Utils.convert_object(TopNewsUpdate(ts, NewsUpdateStatus.SUCCESS)))
        self.send_msg(msg)

    def test_send_fail_msg(self):
        import time
        ts = Utils.get_unix_timestamp(time.time())
        msg = json.dumps(Utils.convert_object(TopNewsUpdate(ts, NewsUpdateStatus.FAILED)))
        self.send_msg(msg)


if __name__=='__main__':
    app = GenerateTopNews("TopNews")
    app.run()

