#!/bin/bash
mainClass_SN="Word2VectorTextSegment"
mainCalls_QN="com.caishi.bigdata.categorize.textseg.common.main.Word2VectorTextSegment"
server_log_home="./logs"
server_name="Word2VectorTextSegment"
appenderSelction="rolling"
production_mode="ONLINE"
local_config_enabled="false"

pid=`jps | grep ${mainClass_SN} | grep -v "grep" | grep -v "app_deploy" | awk '{print $1}'`

[ "x$pid" != "x" ] && kill $pid

sleep 5

classpath="."
for file in lib/*.jar
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

echo "========================do task for textseg input text========================================================"
arr_v=(part-r-00000 part-r-00001 part-r-00002 part-r-00003 part-r-00004 part-r-00005 part-r-00006 part-r-00007 part-r-00008 part-r-00009 part-r-00010 part-r-00011 part-r-00012 part-r-00013 part-r-00014 part-r-00015)
#230aeb04-a918-42b9-a414-1436c05169f4
const=$1
for num in ${arr_v[*]}; do
    inputFile=./data/$num"-"$const
    outputFile=./data/word2vector_sampling/"$num-word2vector_sampling.text"
    echo ">>>>>>>>>>>do task inputFile:"${inputFile}-${outputFile}
    /home/hadoop/caishi/local/jdk1.8.0_45/bin/java -classpath $classpath $mem_opts $logconfig  $mainCalls_QN  ${inputFile} ${outputFile}
done;
echo "========================do task sucess====================================="