#!/usr/bin/env python2.7
#coding=utf-8

import os
import sys
import datetime
sys.path.append('..')

import time
import re
import shutil
import argparse
from yaml import load

from lib.log import MyLogger
from lib.hdfsApi import HDFSApi
from lib.oozie_ws import RetrieveAllJobsInfo, JobManagementRequest
from lib.utils import Utils
from model.config.OozieJob import OozieJob
from conf.oozie import OozieConf
from conf.environ import get_config

class ArgsParser():

    @staticmethod
    def getArgs():
        arg_parser = argparse.ArgumentParser()
        arg_parser.add_argument('-c', '--config', help='job management config file: current only support yaml', required=True)
        arg_parser.add_argument('-t', '--type', help='action type: Submit|Kill', required=True)
        arg_parser.add_argument('-p', '--path', help='configs path', required=True)
        args = arg_parser.parse_args()
        return args

class OozieManager():

    class PkgType():

        CONFIGS = 'configs'
        LIBS = 'libs'
        SCRIPT = 'script'

    class JobType():

        WF = "wf"
        COORDINATOR = 'coordinator'
        BUNDLE = 'bundle'

    class JobStatus():

        RUNNING = 'RUNNING'
        SUCCEEDED = 'SUCCEEDED'
        KILLED = 'KILLED'
        FAILED = 'FAILED'

    def __init__(self, config, config_path):
        self.config_path = config_path
        self.config = config
        self.logger = MyLogger.getLogger('OozieManager')

    def submit(self):
        '''
        deploy the files and submit the job
        :return:
        '''
        self.deploy2Hdfs()
        self.deploy2Local()
        self.submitJob()


    def kill(self):
        '''
        kill job
        :return:
        '''
        self.logger.info("Start to kill the job")
        self.retrieveJob()
        self.killJobById()


    def parseYaml(self):
        '''
        parse_yaml
        :return:
        '''
        try:
            stream = file(self.config, 'r')
            self.oozie_job = load(stream)
            return True
        except Exception, ex:
            self.logger.error(ex)
            self.logger.info("Failed to load yaml config file %s" % self.config)
            return False


    def _getOoziePath(self, pkg_type):
        '''
        _getOoziePath
        :param pkg_type:
        :return:
        '''
        local_path = os.path.join(self.config_path, pkg_type)
        return local_path

    def _getHdfsPath(self, pkg_type):
        '''
        _getHdfsPath
        :param pkg_type:
        :return:
        '''
        if str.lower(pkg_type) == self.PkgType.CONFIGS:
            return os.path.join(self.oozie_job.hdfsPath, self.oozie_job.app)
        elif str.lower(pkg_type) == self.PkgType.LIBS:
            return os.path.join(self.oozie_job.hdfsPath, 'lib')

    def _getScriptPath(self, script_type):
        '''
        _getScriptPath
        :param script_type:
        :return:
        '''
        return os.path.join(self.oozie_job.scriptPath, script_type)

    def deploy2Local(self):
        '''
        deploy2Local
        :return:
        '''
        if self.oozie_job.script is not None and len(self.oozie_job.script) != 0:
            for sp in self.oozie_job.script:
                #copy the file to target dir
                if sp['files'] is not None and len(sp['files']) != 0:
                    #start copy the scripts to target path
                    dest_path = self._getScriptPath(sp['type'])
                    if not os.path.exists(dest_path):
                        #create local directory firstly
                        os.mkdir(dest_path)

                    for file in sp['files']:
                        src_path = os.path.join(self._getOoziePath(self.PkgType.SCRIPT), sp['type'], file)
                        try:
                            shutil.copy(src_path, dest_path)
                        except Exception, ex:
                            self.logger.error(ex)
                            self.logger.error("Copy the script file %s to local path %s failed" % (file, dest_path))
                            return
            self.logger.info("Copy the script file successfully")
        else:
            self.logger.info("Empty script files, skip copy scripts")

    def copy2Hdfs(self, pkg_type):
        '''
        copy2Hdfs
        :param pkg_type:
        :return:
        '''
        copy_types = {
            self.PkgType.CONFIGS: 'schedules',
            self.PkgType.LIBS: 'dependencyLibs'
        }

        hdfs = HDFSApi()
        try:
            if (copy_types.has_key(pkg_type)):
                attr = copy_types[pkg_type]
                kvals = getattr(self.oozie_job, attr)
                if kvals is not None and len(kvals) !=0:
                    hdfs_path = self._getHdfsPath(pkg_type)
                    hdfs.createDirectory(hdfs_path)
                    for f in kvals:
                        abs_path = os.path.join(self._getOoziePath(pkg_type), f)
                        hdfs.putFiles(abs_path, hdfs_path)
            else:
                self.logger.warn("Could not find the key in the copy mapping, return")
                return
        except Exception, ex:
            self.logger.error(ex)
            self.logger.error("Failed to deploy the files to HDFS")


    def deploy2Hdfs(self):
        '''
        deploy2Hdfs
        :return:
        '''
        try:
            #copy the job related schedule files to HDFS
            self.copy2Hdfs(self.PkgType.CONFIGS)
            #copy the job related libs files to HDFS
            self.copy2Hdfs(self.PkgType.LIBS)
            if self.oozie_job.type == self.JobType.COORDINATOR:
                #issue: 如果job.properties的配置文件里start时间早于提交时间, Oozie会自动以配置的间隔执行时间为单位,执行所有在这个区间内的任务
                #workaround: 在提交job之前把start时间更新为系统当前时间向后推迟一段，　目前配置为５分钟
                self.logger.info("Due to the Oozie Coordinator job submit issue, update the start time")
                self.updateStartTime()
        except Exception, ex:
            self.logger.error(ex)
            self.logger.error("Deploy the files to HDFS and local failed")
            #Utils.send_notify_mail('Oozie Submit: Failed', 'Deploy the files failed for app %s' % self.app_name)

    def submitJob(self):
        job_file = os.path.join(self._getOoziePath(self.PkgType.CONFIGS), 'job.properties')
        if not os.path.exists(job_file):
            self.logger.error("Could not find the job.properties file before submit the oozie job %s" % self.oozie_job.app)

        cmd = 'oozie job -config %s -submit' % job_file
        if os.system(cmd) != 0:
            self.logger.error("submit the job for %s failed" % self.oozie_job.app)
            Utils.send_notify_mail('Oozie Submit: Failed', 'Submit the job for %s failed' % self.oozie_job.app)
        else:
            self.logger.info("submit the job for %s successfully" % self.oozie_job.app)
            Utils.send_notify_mail('Oozie Submit: Succeed', 'Submit the job for %s successfuly' % self.oozie_job.app)


    def retrieveJob(self):
        '''
        kill Job with job id
        :return:
        '''
        JOB_TYPE_FIELD_MAPPING = {
            self.JobType.WF: ['workflows', 'id'],
            self.JobType.COORDINATOR: ['coordinatorjobs', 'coordJobId']
        }

        self.logger.info("Retrieve the job id firstly")
        self.job_ids=[]
        result = RetrieveAllJobsInfo(jobtype=self.oozie_job.type, filter=str.format('name={0};status={1}', self.oozie_job.name, self.JobStatus.RUNNING)).invoke()
        #result = RetrieveAllJobsInfo(jobtype='wf', filter=str.format('name={0};status=RUNNING', 'fpgrowthWF')).invoke()
        if result is not None:
            resp = result.json()
            if resp['total'] != 0:
                try:
                    jobs = resp[JOB_TYPE_FIELD_MAPPING[self.oozie_job.type][0]]
                    for job in jobs:
                        self.job_ids.append(job[JOB_TYPE_FIELD_MAPPING[self.oozie_job.type][1]])
                except Exception, ex:
                    self.logger.error(ex)
                    self.logger.error("failed to retrieve the jobid for job name %s" % self.oozie_job.name)
            else:
                self.logger.warn("Retrieve empty for job %s" % self.oozie_job.name)
        else:
            self.logger.error("Invoke the RetrieveAllJobsInfo failed for job name %s" % self.oozie_job.name)


    def killJobById(self):
        '''
        killJobById
        :param ids:
        :return:
        '''
        if len(self.job_ids) != 0:
            for id in self.job_ids:
                self.logger.info("start to kill the job with id %s" % id)
                resp = JobManagementRequest(jobid=id, action='kill').invoke()
                if resp is not None:
                    self.logger.info("Kill the job with id %s successfully" % id)
                else:
                    self.logger.error("Invoke the JobManagementRequest for killing job %s failed" % id)
        return True

    def updateStartTime(self):
        job_file = os.path.join(self._getOoziePath(self.PkgType.CONFIGS), 'job.properties')
        try:
            #get file content
            with open(job_file, 'r') as fh:
                content = fh.read()
                pattern = re.compile(r'((start=)(\S*))')
                result = pattern.findall(content)
                # get result: [('start=2016-06-08T10:10+0800', 'start=', '2016-06-08T10:10+0800')]

            #overwrite the file
            with open(job_file, 'w+') as fh:
                if result is not None:
                    #truncate the file content and overwrite the file
                    start_time = result[0][2]
                    now = datetime.datetime.now()
                    future = now + datetime.timedelta(minutes=get_config(OozieConf, 'SUBMIT_DELAY_TIME'))
                    replace_time = future.strftime('%Y-%m-%dT%H:%M+0800')
                    fh.write(content.replace(start_time, replace_time))
                    fh.flush()
                    self.logger.info("The job %s start time updated from %s to %s" % (self.oozie_job.name, start_time, replace_time))

        except Exception, ex:
            self.logger.error(ex)
            self.logger.error("Failed to update the coordinator start time for job %s" % self.oozie_job.app)



if __name__=='__main__':
    args = ArgsParser.getArgs()
    oozie = OozieManager(args.config, args.path)
    if oozie.parseYaml():
        type = str.lower(args.type)
        if type == 'submit':
            oozie.submit()
        elif type == 'kill':
            oozie.kill()
    else:
        print 'Failed to parse yaml config, submit exit'
        sys.exit(255)
