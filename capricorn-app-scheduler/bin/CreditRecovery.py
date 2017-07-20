#!/usr/bin/env python2.7
#coding=utf-8

import sys

import time

sys.path.append('..')

from conf.email import MailServer
from lib.log import MyLogger
from lib.kaffa import KafkaApi
from lib.mail import MonitorEmail


class CreditRecovery():
    '''
        当数据导入mysql成功后，发送kafka消息提醒

    '''

    def __init__(self, appname):
        self.logger = MyLogger.getLogger(appname)
        self.mail = MonitorEmail(MailServer.SMTP_SEND_TO)

    def send_msg(self, msg):
        broker = '10.4.1.201:9092,10.4.1.202:9092,10.4.1.203:9092'
        topic = 'topic_credit_recovery'
        kafka = KafkaApi(broker, topic)
        try:
            kafka.send_msg(msg, async=True)
            self.logger.info("Succssfully to send msg to api client, msg=%s" % msg)
        except Exception,ex:
            self.logger.error("Exception: Failed to send kafka msg to api client")
            self.logger.error(ex)



if __name__=='__main__':
    app = CreditRecovery("creditRecovery")
    ISOTIMEFORMAT='%Y%m%d'
    today = time.strftime( ISOTIMEFORMAT, time.localtime())
    app.send_msg("SUCCESS_"+today)

