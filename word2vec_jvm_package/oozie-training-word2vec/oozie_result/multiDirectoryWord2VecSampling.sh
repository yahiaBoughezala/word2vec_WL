#!/bin/bash
mainClass_SN="MultiDirectoryWord2VecSampling"
mainCalls_QN="com.caishi.bigdata.categorize.textseg.common.main.MultiDirectoryWord2VecSampling"
server_log_home="./logs"
server_name="MultiDirectoryWord2VecSampling"
appenderSelction="rolling"
production_mode="ONLINE"
local_config_enabled="false"

pid=`jps | grep ${mainClass_SN} | grep -v "grep" | grep -v "app_deploy" | awk '{print $1}'`

[ "x$pid" != "x" ] && kill $pid

sleep 5

classpath="."
for file in /home/hadoop/apps/multiDirectoryWord2VecSampling/lib/*.jar
do
	classpath="${file}:${classpath}"
done

## server_log_home null check
[ -z ${server_log_home} ] && echo "server_log_home is null!!!" && exit 255

## create log directory
[ ! -e ${server_log_home} ] && mkdir -p ${server_log_home}

##  server_log_home is not a directory
[ ! -d ${server_log_home} ] && echo "server_log_home is not a directory!!!" && exit 255

[ "${production_mode}" == "ONLINE" ] && mem_opts="-Xmx8G -Xms8G -Xmn512m -XX:PermSize=256m -XX:MaxPermSize=256M -Xss256K -XX:+DisableExplicitGC -XX:SurvivorRatio=1 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0 -XX:+CMSClassUnloadingEnabled -XX:LargePageSizeInBytes=128M -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=80"

logconfig="-Dserver_log_home=${server_log_home} -Dserver_name=${server_name} -DappenderSelction=${appenderSelction}"

echo "========================Starting News ChineseToken====================================="
#pt=`date +%Y%m%d`
pt=$1
rm -rf  /home/hadoop/apps/multiDirectoryWord2VecSampling/data/${pt}
hdfs dfs -get  hdfs://10.4.1.1:9000/news/newsContent/V3.0/${pt}    /home/hadoop/apps/multiDirectoryWord2VecSampling/data/

inputFile="/home/hadoop/apps/multiDirectoryWord2VecSampling/data/${pt}/"
outputFile="/home/hadoop/apps/multiDirectoryWord2VecSampling/news/w2v/sampling/${pt}/"
/home/hadoop/caishi/local/jdk1.8.0_45/bin/java -classpath $classpath $mem_opts $logconfig  $mainCalls_QN  ${inputFile} ${outputFile}

hdfs dfs -rmr "hdfs://10.4.1.1:9000/news/w2v/sampling/${pt}"
hdfs dfs -mkdir -p "hdfs://10.4.1.1:9000/news/w2v/sampling/${pt}"
hdfs dfs -put -f ${outputFile}  "hdfs://10.4.1.1:9000/news/w2v/sampling/"
echo "========================All News ChineseToken sucess====================================="
