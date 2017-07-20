# -*- coding: utf-8 -*-
import time
import json
import os
from lib.log import MyLogger
from conf.email import MailServer


class Utils():

    MAX_CATEGORY_SIZE = 200
    '''
    Utils Class
    '''

    @staticmethod
    def get_unix_timestamp(dt):
        return int(round(dt) * 1000)

    @staticmethod
    def convert_object(obj):
        dict={}
        dict.update(obj.__dict__)
        return dict

    @staticmethod
    def transform_json(jsonStr):
        try:
            userCatLike = json.loads(jsonStr)
        except Exception, ex:
            print 'Transform the Json to UserCatLike failed, reason: %s' % str(ex.message)
            raise Exception,ex

        temp = [0.0] * Utils.MAX_CATEGORY_SIZE
        if isinstance(userCatLike, list):
            for catLike in userCatLike['catLikes']:
                catId = int(catLike['catId'])
                if catId <= Utils.MAX_CATEGORY_SIZE:
                    temp[catId-1] = catLike['weight']
                else:
                    print 'CatId greater than max id size %s' % str(Utils.MAX_CATEGORY_SIZE)

        else:
            print 'Type Error for transform'

        map = {}
        map[userCatLike['_id']] = temp
        return map


    @staticmethod
    def send_notify_mail(subject, content):
        '''

        send mail using heirloom-mailx
        echo "content" | mail -s "Subject" "Send_To"

        :param subject:
        :param to_addr:
        :param content:
        :return:
        '''
        add_suffix = lambda x: str(x)+'@9icaishi.net'
        to_addr = map(add_suffix, MailServer.SMTP_SEND_TO)
        mail_cmd = str.format('echo "{}" | mail -s "{}" "{}"', content, subject, ' '.join(to_addr))
        ret = os.system(mail_cmd)
        if ret == 0:
            print "send notify mail successfully"
        else:
            print "send notify mail failed"



if __name__ == '__main__':
    Utils.send_notify_mail("Test", ["xingjie.liu@9icaishi.net"], "This is the test mail")
    pass