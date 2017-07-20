#!/bin/bash
source ~/.bashrc

server_log_home="./logs"
class_name="com.caishi.bigdata.news.word2vec.trainer.NewsWord2VecTrainning"

pid=`ps -ef | grep ${class_name} | grep -v "grep" |grep -v "spark_deploy.sh"| awk '{print $2}'`

sleep 5

[ "x$pid" != "x" ] && kill $pid

classpath=""
for file in /home/hadoop/apps/spark-NewsWord2Vec/lib/*.jar
do
        classpath="${file},${classpath}"
done

## server_log_home null check
[ -z ${server_log_home} ] && echo "server_log_home is null!!!" && exit 255

## create log directory
[ ! -e ${server_log_home} ] && mkdir -p ${server_log_home}

##  server_log_home is not a directory
[ ! -d ${server_log_home} ] && echo "server_log_home is not a directory!!!" && exit 255

# News_W2V_Model暂定一个目录，备份左右。生成后后续再考虑到最终的目录下：/news/W2V/News_W2V_Model
#pt=`date +%Y%m%d`
pt=$1
echo "News Word2Vec Model Date:"${pt}
SPARH_HOME=/home/hadoop/caishi/local/spark-1.6.0
/home/hadoop/caishi/local/spark-1.6.0/bin/spark-submit \
    --class com.caishi.bigdata.news.word2vec.trainer.NewsWord2VecTrainning \
    --jars $classpath \
    --properties-file /home/hadoop/apps/spark-NewsWord2Vec/conf/spark.conf \
    /home/hadoop/apps/spark-NewsWord2Vec/news-word2vec-trainning-1.0.jar \
    hdfs://10.4.1.1:9000 spark-NewsWord2VecTrainning  /news/w2v/sampling/*/part-r-*  /news/W2V/${pt}/News_W2V_Model \
    200 100 5 24 5 >/home/hadoop/apps/spark-NewsWord2Vec/logs/NewsWord2Vec.info 2>&1
