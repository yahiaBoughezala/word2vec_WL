#!/bin/bash
source ~/.bashrc

server_log_home="./logs"
mainCalls_QN="com.caishi.bigdata.news.word2vec.examples.NewsWord2VecSynonyms"

pid=`ps -ef | grep ${mainCalls_QN} | grep -v "grep" |grep -v "spark_deploy.sh"| awk '{print $2}'`

sleep 5

[ "x$pid" != "x" ] && kill $pid

classpath=""
for file in /home/hadoop/apps/NewsWord2Vec_Example/lib/*.jar
do
        classpath="${file},${classpath}"
done

## server_log_home null check
[ -z ${server_log_home} ] && echo "server_log_home is null!!!" && exit 255

## create log directory
[ ! -e ${server_log_home} ] && mkdir -p ${server_log_home}

##  server_log_home is not a directory
[ ! -d ${server_log_home} ] && echo "server_log_home is not a directory!!!" && exit 255
mem_opts="-Xmx8G -Xms8G -Xmn512m -XX:PermSize=256m -XX:MaxPermSize=256M -Xss256K -XX:+DisableExplicitGC -XX:SurvivorRatio=1 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0 -XX:+CMSClassUnloadingEnabled -XX:LargePageSizeInBytes=128M -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=80"
word=$1
echo "compute word="$word
/home/hadoop/caishi/local/jdk1.8.0_45/bin/java -classpath $classpath $mem_opts  $mainCalls_QN  ${word}

