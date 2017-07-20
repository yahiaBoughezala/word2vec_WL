#!/bin/bash

#requirement: just like other applications, deployed box need capricorn-app-scheduler installed prerequisite
oozie_manager_home=/home/hadoop/apps/capricorn-app-scheduler/bin
config_file="job.yaml"
base_home=$PWD
config_file="job.yaml"
yaml_file="$PWD/configs/$config_file"

[ ! -f "$yaml_file" ] && echo "$yaml_file not exist!" && exit 255

cd $oozie_manager_home && nohup python OozieManager.py -c $yaml_file -t kill -p $base_home &> $base_home/logs/kill.log &