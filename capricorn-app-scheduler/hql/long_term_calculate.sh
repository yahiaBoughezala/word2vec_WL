
#!/bin/bash
#########################################################
#######每月一号凌晨2点统计用户上个月的用户catlike########
#########################################################
source ~/.bashrc
if [ -z "$1" ]
then
        ago=1
else
        ago=$1
fi

if [ -z "$2"]
then
        cur=0
else
        cur=$2
fi


start_time=`date -d "-$ago month" +%Y%m0100`
end_time=`date -d "-$cur month" +%Y%m0100`
location="/hivedata/profiles/user_trait/`date -d "-$ago month" +%Y/%m`"
echo $start_time \n $end_time \n $location
mt=`date -d "-$ago month" +%Y%m`
maxseq=`hive -e 'set hive.cli.print.header=false;select CAST(MAX(seq)+1 AS int) from profiles.user_trait'|awk -F "WARN" '{print $1}'`
echo $maxseq
hive -e "alter table profiles.user_trait add if not exists partition (mt='${mt}',seq='$maxseq') location '${location}';"


sql="
use logtmp;
set hive.cli.print.header=false;
set hive.exec.mode.local.auto=true;
set hive.exec.compress.intermediate=true;
set mapreduce.map.output.compress=true;
set mapreduce.map.output.compress.codec=org.apache.hadoop.io.compress.SnappyCodec;
set hive.query.result.fileformat=SequenceFile;
set hive.exec.mode.local.auto=true;
set hive.merge.mapredfiles=true;
set hive.map.aggr=true;
set hive.groupby.skewindata=true;

CREATE TEMPORARY TABLE push_event_all (uid string,cid string, recomCount int, allRecomCount int, clickCount int, allClickCount int);
CREATE TEMPORARY TABLE click_dislike (userid string,catid int,tfidf double);
CREATE TEMPORARY TABLE default_call (userid string,catid string,catcount int,callcount int,islike string);


insert into push_event_all
select uid, cid, sum(recomCount) as recomCountAll, sum(allRecomCount) as allRecomCountAll, sum(clickCount) as clickCountAll, sum(allClickCount) as allClickCountAll
from push_event_hour_store where pt >='$start_time' and pt<'$end_time'
group by uid, cid;


insert into default_call
SELECT dc.userid,
dc.catId,
dc.catCount,
dd.userCount,
'dislike' as islike
FROM
(SELECT userid,
catId,
sum(catCount) AS catCount
FROM
(
SELECT 'default' AS userid,
catId,
sum(callcount) AS catCount
FROM news_social_hour_store
where pt >='$start_time'
and pt<'$end_time'
GROUP BY catId
UNION ALL SELECT 'default' as userid,
cast(catId AS INT) as catId,
count(1) as catCount
FROM common_event_hour_store
where pt >='$start_time'
and pt<'$end_time'
group by catId
) AS all_user_act_table
GROUP BY userid,
catId) dc
JOIN
(
SELECT 'default' as userid,count(1) as userCount FROM common_event_hour_store where pt >='$start_time' and pt<'$end_time'
) dd ON (dc.userid = dd.userid);


insert into click_dislike
SELECT dx.userid,
dx.catid,
case
when islike='clicklike' then
(dx.weight*dy.idf)
when islike='dislike' then dx.weight
end
AS tfidf
FROM
(
select userid,catid,(catcount/callcount) as weight,islike from default_call
UNION ALL

SELECT g.uid as userid,g.cid as catId,(f.weight*g.clickCount*g.allRecomCount/(g.recomCount*g.allClickCount)) AS weight, f.islike as islike from
(SELECT c.userid as userid,c.catId as catId,(c.catCount/d.userCount) AS weight,'clicklike' as islike
FROM
(SELECT userid,
catId,
sum(catCount) AS catCount
FROM
(
SELECT userid,
catId,
sum(callcount) AS catCount
FROM news_social_hour_store
where pt >='$start_time' and pt<'$end_time'
GROUP BY userid,
catId
UNION ALL
SELECT z.userid, z.catId, count(1) as catCount
FROM
(SELECT userid, catId,
case
when sourceType='HOT'
then
count(1)*0.6
else
count(1)
end
AS catCount

FROM common_event_hour_store where pt >='$start_time' and pt<'$end_time'
GROUP By userid,catid, sourceType) z
GROUP By userid,catid
) user_act_table
GROUP BY userid,
catId) c
JOIN
(SELECT userid,
sum(userCount) AS userCount
FROM
(
SELECT userid,
sum(callcount) AS userCount
FROM news_social_hour_store where pt >='$start_time' and pt<'$end_time'
GROUP BY userid
UNION ALL
SELECT userid as userid,count(1) AS userCount
FROM common_event_hour_store where pt >='$start_time' and pt<'$end_time'
GROUP BY userid
) user_act_table
GROUP BY userid
) d ON (c.userid = d.userid)) f
JOIN
(select uid, cid, recomCount, allRecomCount, clickCount, allClickCount from push_event_all) g
ON (f.userid = g.uid and f.catId = g.cid)
)AS dx
left outer JOIN
(
select catid,((callcount-catcount)/callcount) as idf from default_call
) AS dy
ON dx.catid=dy.catid;

INSERT overwrite table profiles.user_trait PARTITION (mt='${mt}', seq='$maxseq')
SELECT tw.userid,cat_like(tw.userid,tw.catId,tw.weight,0) from (
select tx.userid,tx.catid,case when choose.weight is null then tx.weight else tx.weight+choose.weight end as weight from(
select cd.userid,cd.catid,cd.tfidf/tmp.total as weight from click_dislike as cd join (
select userid,sum(tfidf) as total from click_dislike group by userid
) as tmp on tmp.userid=cd.userid
) as tx
left outer join
(
select split(cl.tmpdata,'-')[0] as userid,ct.catid,ct.weight from (
select get_latest_one(userid,cast(createtime as bigint),data) as tmpdata from log.choose_like
where  pt>='$start_time'
AND pt<'$end_time'
group by userid
) as cl
LATERAL VIEW array_udtf(get_json_object(split(cl.tmpdata,'-')[1],'$.fondIds')) ct
) as choose
on tx.userid=choose.userid and tx.catid=choose.catid
left outer join
(
select userid,catid,sum(discount) as discount from profiles.dislike group by userid,catid
)as dis
on dis.userid=tx.userid and dis.catid=tx.catid
WHERE dis.discount is NULL
) as tw where CAST(cd.catid AS INT)<200 GROUP BY tw.userid;
"

echo $sql
hive -e "$sql" 2>> /home/hadoop/apps/capricorn-app-scheduler/logs/month_calculate.log