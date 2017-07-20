#!/usr/bin/env python2.7
#coding=utf-8


import os
import calendar
import commands
from lib.log import MyLogger
from conf.environ import HQL_PATH

class LongTermUserProfile():

    '''
    计算１个月长期用户画像, ，默认１个月前
    '''
    def __init__(self, month_start=1, month_end=0):
        self.logger = MyLogger.getLogger('long_term_user_profile')
        self.month_start = month_start
        self.month_end = month_end

    def run(self):
        get_date_cmd = 'lastmonth=`date -d "-%s month"' % self.month_start + ' +%Y,%m`;echo $lastmonth'
        status, last_year_month = commands.getstatusoutput(get_date_cmd)
        if status == 0:
            self.logger.info('Get last month time successfully')
            year,month = last_year_month.split(',')
        else:
            self.logger.error('Get last month time failed, return')
            return
        self.logger.info("Prepare to store the user profile data for %s%s" % (year,month))
        days = calendar.monthrange(int(year), int(month))

        for i in range(days[0], days[1]+1):
            if i < 10:
                i = '0'+str(i)
            days_format = year+month+str(i)
            self.logger.info("Start to exec hive job for %s" % days_format)
            store_batch_file = HQL_PATH + 'long_user_profile_store.sh'
            cmd = 'sh %s %s' % (store_batch_file, days_format)
            ret = os.system(cmd)
            if ret == 0:
                self.logger.info("Succeed: execute the hive job for %s successfully" % days_format)
            else:
                self.logger.error("Exception: failed to execute the hive job for %s" % days_format)

        self.logger.info("Try to execute the long term user profile calculate batch")
        long_calc_batch = HQL_PATH + 'long_term_calculate.sh'
        cmd = 'sh %s %s %s' % (long_calc_batch, self.month_start, self.month_end)
        ret = os.system(cmd)
        if ret == 0:
            self.logger.info("Succeed: execute the long-term user profile calcuate hive job for year=%s month=%s successfully" % (year, month))
        else:
            self.logger.error("Exception: failed to execute the long-term user profile hive job for year=%s month=%s" % (year, month))

        self.logger.info("All the job execute successfully")

if __name__=='__main__':
    app = LongTermUserProfile()
    app.run()


