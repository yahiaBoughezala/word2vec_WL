#!/bin/bash

#requirement: just like other applications, deployed box need capricorn-app-scheduler installed prerequisite
oozie_manager_home=/home/hadoop/apps/capricorn-app-scheduler/bin
log_home="./logs"
base_home=$PWD
config_file="job.yaml"
yaml_file="$PWD/configs/$config_file"

[ ! -f "$yaml_file" ] && echo "$yaml_file not exist!" && exit 255

## create log directory
[ ! -e ${log_home} ] && mkdir -p ${log_home}

## log_home null check
[ -z ${server_log_home} ] && touch logs/start.log



cd $oozie_manager_home && nohup python OozieManager.py -c $yaml_file -t submit -p $base_home &> $base_home/logs/start.log &