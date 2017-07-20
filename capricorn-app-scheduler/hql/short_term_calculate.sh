#!/bin/bash
source ~/.bashrc
start_time=`date -d "-336 hour" +%Y%m%d%H`
end_time=`date -d "-1 hour" +%Y%m%d%H`

echo "prepare to calculate user catlike between $start_date and $end_date" 

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

CREATE TEMPORARY TABLE push_event_all (uid string,cid string, recomCount int, allRecomCount int, clickCount int, allClickCount int);
CREATE TEMPORARY TABLE click_dislike (userid string,catid int,tfidf double);
CREATE TEMPORARY TABLE default_call (userid string,catid string,catcount int,callcount int,islike string);


insert into push_event_all
select uid, cid, sum(recomCount) as recomCountAll, sum(allRecomCount) as allRecomCountAll, sum(clickCount) as clickCountAll, sum(allClickCount) as allClickCountAll
from push_event_hour_store where pt >='$start_time' and pt<='$end_time'
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
and pt<='$end_time'
GROUP BY catId
UNION ALL SELECT 'default' as userid,
cast(catId AS INT) as catId,
count(1) as catCount
FROM common_event_hour_store
where pt >='$start_time'
and pt<='$end_time'
group by catId
) AS all_user_act_table
GROUP BY userid,
catId) dc
JOIN
(
SELECT 'default' as userid,count(1) as userCount FROM common_event_hour_store where pt >='$start_time' and pt<='$end_time'
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
where pt >='$start_time' and pt<='$end_time'
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

FROM common_event_hour_store where pt >='$start_time' and pt<='$end_time'
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
FROM news_social_hour_store where pt >='$start_time' and pt<='$end_time'
GROUP BY userid
UNION ALL 
SELECT userid as userid,count(1) AS userCount
FROM common_event_hour_store where pt >='$start_time' and pt<='$end_time'
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


CREATE TEMPORARY TABLE click (json String);
insert into click 
SELECT merge_cat_like(ttx.userid,ttx.json,CAST(ttx.seq as int),0) from (
SELECT tw.userid as userid,cat_like(tw.userid,tw.catId,tw.weight,0) as json,0 as seq from (
select tx.userid,tx.catid,case when choose.weight is null then tx.weight else tx.weight+choose.weight end as weight from(
select cd.userid,cd.catid,cd.tfidf/tmp.total as weight from click_dislike as cd join (
select userid,sum(tfidf) as total from click_dislike group by userid
) as tmp on tmp.userid=cd.userid
) as tx
left outer join 
(
select split(cl.tmpdata,'-')[0] as userid,ct.catid,ct.weight from (
select get_latest_one(userid,cast(createtime as bigint),data) as tmpdata from log.choose_like
where  pt>='$start_time' AND pt<='$end_time' group by userid
) as cl 
LATERAL VIEW array_udtf(get_json_object(split(cl.tmpdata,'-')[1],'$.fondIds')) ct 
) as choose
on tx.userid=choose.userid and tx.catid=choose.catid
) as tw  GROUP BY tw.userid
UNION ALL
SELECT userid,json,seq FROM profiles.user_trait
) as ttx 
GROUP BY ttx.userid;

select cat_like(click.userid,CAST(click.catid as int),CAST(click.weight as double), 15) from (
select json_udtf(json) from click 
) as click where CAST(click.catid AS INT)<200 group by click.userid
"
echo $sql
hive -e "$sql" >/home/hadoop/data/hivedata/user_catLike.json
sed -i "/WARN/d" /home/hadoop/data/hivedata/user_catLike.json

file_size=`du /home/hadoop/data/hivedata/user_catLike.json | awk '{print $1}'`
if [ $file_size -ne 0 ]
then
echo "Execute user_catlike job successfully, import the data to mongodb, remove and copy local file to hdfs"
/root/software/mongodb/bin/mongoimport --host "user_profile/10.4.1.25:27017,10.4.1.6:27017,10.4.1.23:27017"  --username userprofile  --password userprofile9icaishi  --authenticationDatabase  user_profiles  --upsert --collection usercatLike --db user_profiles --file /home/hadoop/data/hivedata/user_catLike.json
hadoop fs -rm -r /hivedata/profiles/user_catLike.json
hadoop fs -copyFromLocal /home/hadoop/data/hivedata/user_catLike.json /hivedata/profiles/
else
echo "Execute user_catlike job failed, not data output, do not remove and copy local file to hdfs"
fi
