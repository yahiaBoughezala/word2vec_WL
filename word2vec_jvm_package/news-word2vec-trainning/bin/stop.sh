#!/bin/bash

mainClass_SN="NewsWord2VecTrainning"

pid=`jps | grep ${mainClass_SN} | grep -v "grep" | grep -v "spark_deploy" | awk '{print $1}'`

[ "x$pid" != "x" ] && kill -9 $pid

sleep 5
