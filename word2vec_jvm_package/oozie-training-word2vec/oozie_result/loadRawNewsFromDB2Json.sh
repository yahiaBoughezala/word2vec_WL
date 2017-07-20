#!/bin/bash
source ~/.bashrc

server_log_home="./logs"
class_name="com.caishi.bigdata.categorize.LoadRawNewsFromDB2Json"

pid=`ps -ef | grep ${class_name} | grep -v "grep" |grep -v "spark_deploy.sh"| awk '{print $2}'`

sleep 5

[ "x$pid" != "x" ] && kill $pid

classpath=""
for file in /home/hadoop/apps/spark-word2vec-datasource/lib/*.jar
do
        classpath="${file},${classpath}"
done


## server_log_home null check
[ -z ${server_log_home} ] && echo "server_log_home is null!!!" && exit 255

## create log directory
[ ! -e ${server_log_home} ] && mkdir -p ${server_log_home}

##  server_log_home is not a directory
[ ! -d ${server_log_home} ] && echo "server_log_home is not a directory!!!" && exit 255

SPARH_HOME=/home/hadoop/caishi/local/spark-1.6.0

start_date=`date -d "1 days ago" +%Y%m%d`
#end_date=`date +%Y%m%d`
end_date=$1
/home/hadoop/caishi/local/spark-1.6.0/bin/spark-submit \
    --class com.caishi.bigdata.categorize.LoadRawNewsFromDB2Json \
    --jars $classpath \
    --properties-file /home/hadoop/apps/spark-word2vec-datasource/conf/spark_common.conf \
    /home/hadoop/apps/spark-word2vec-datasource/category-ml-1.0.jar   \
    spark-LoadWord2VecRawNewsFromDB2Json 10.3.1.9:30000,10.3.1.10:30000,10.4.1.3:30000   news news9icaishi news newsContent ${start_date} ${end_date} json  /news/newsContent/V3.0/ 16 >/home/hadoop/apps/spark-word2vec-datasource/logs/spark-LoadRawNewsFromDB2Json.info 2>&1
