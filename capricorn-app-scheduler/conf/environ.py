#!/usr/bin/env python2.7
#coding=utf-8

'''

Environment Configuration

'''

import os,sys

class MODE():

    DEV = 'dev'
    TEST = 'test'
    STAGE = 'stage'
    ONLINE = 'online'


#Config File For Environment
PRODUCTION_MODE = MODE.STAGE


ABS_DIR = os.path.dirname(os.path.abspath(__file__))
HOME_PATH = os.path.dirname(ABS_DIR)
HQL_PATH = HOME_PATH + '/hql/'
LIB_PATH = HOME_PATH + '/lib/'
MODEL_PATH = HOME_PATH + '/model/'
SPARK_PATH = HOME_PATH + '/spark/'
TOOL_PATH = HOME_PATH + '/tools'
BIN_PATH = HOME_PATH + '/bin/'


def get_config(conf_cls, conf_name):
    try:
        if hasattr(conf_cls, PRODUCTION_MODE):
            mode = getattr(conf_cls, PRODUCTION_MODE)
            if hasattr(mode, conf_name):
                return getattr(mode, conf_name)
            else:
                raise Exception, 'Failed to get %s' % conf_name
        else:
            if hasattr(conf_cls, conf_name):
                config = getattr(conf_cls, conf_name)
                return config
            else:
                raise Exception, 'Failed  to get %s' % conf_name

    except:
        raise Exception, 'Failed to get %s.%s' % (conf_cls, conf_name)
