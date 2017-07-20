#!/bin/bash
source ~/.bashrc
pdatetime=$1

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

alter table push_event_hour_store add if not exists partition (pt='$pdatetime');
alter table news_social_hour_store add if not exists partition (pt='$pdatetime');
alter table common_event_hour_store add if not exists partition (pt='$pdatetime');

INSERT INTO push_event_hour_store PARTITION(pt=$pdatetime)
    select recommendation.uid as uid, recommendation.cid as cid, recommendation.cCount as recomCount, recommendation.allCount as allRecomCount,
           click.cCount as clickCount, click.allCount as allClickCount  from
    (select xx.userid as uid, xx.cid as cid, xx.cCount as cCount, xy.allCount as allCount from
    (select userid, e.cid as cid, count(1) as cCount  from
    (select userid, from_json(b.push_item,'map<string,string>')['categoryIds'] as categoryIds from log.push_news a
        Lateral view explode(json_array(a.data)) b as push_item
        where pt='$pdatetime'
        and topic='topic_recommendation_event'
        and from_json(b.push_item,'map<string,string>')['messageType']='NEWS' ) d
    lateral view explode(split(categoryIds, ',')) e as cid
    group by userid, e.cid) xx
    JOIN
    (select userid, count(1) as allCount from
        (select userid, from_json(b.push_item,'map<string,string>')['categoryIds'] as categoryIds from log.push_news a
             Lateral view explode(json_array(a.data)) b as push_item
             where pt='$pdatetime'
             and topic='topic_recommendation_event'
             and from_json(b.push_item,'map<string,string>')['messageType']='NEWS' ) d
         lateral view explode(split(categoryIds, ',')) e as cid
         group by userid
    ) xy ON (xx.userid = xy.userid)) recommendation
    JOIN
    (select dd.userid as uid, dd.cid as cid, dd.cCount as cCount, de.allCount as allCount from
    (select userid, d.cid as cid, count(1) as cCount from log.topic_common_event
        lateral view explode(split(from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['categoryIds'], ',')) d as cid
        where pt='$pdatetime'
        AND from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['sourceType'] !=''
        AND from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['newsType'] == 'NEWS'
        AND from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['categoryIds'] !=''
        group by userid, d.cid) dd
    JOIN
    (select userid, count(1) as allCount from log.topic_common_event
        lateral view explode(split(from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['categoryIds'], ',')) d as cid
        where pt='$pdatetime'
        AND from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['sourceType'] !=''
        AND from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['newsType'] == 'NEWS'
        AND from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['categoryIds'] !=''
        group by userid) de
     ON (dd.userid = de.userid)) click
     ON (recommendation.uid = click.uid and recommendation.cid = click.cid);


INSERT INTO news_social_hour_store PARTITION(pt='$pdatetime')
SELECT a.userid as userid,
        cast(b.catId AS int) AS catId,
        sum(CASE logtype WHEN 'INTERACT' THEN 2 WHEN 'SHARE' THEN 4 WHEN 'COLLECT' THEN 4 END) AS catCount
FROM log.topic_news_social a LATERAL VIEW explode(json_array(get_json_object(data,'$.categoryIds'))) b AS catId
WHERE pt='$pdatetime'
    AND logtype IN ('INTERACT','SHARE','COLLECT')
        AND get_json_object(a.data,'$.categoryIds[0]') IS NOT NULL
GROUP BY userid,catId;

insert into common_event_hour_store PARTITION(pt='$pdatetime')
select userid, catId, sourceType from(
select click_t.userid as userid, click_t.catId as catId, click_t.sourceType as sourceType from (
select userid, newsid, catId, sourceType from (
select userid,
        from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['newsId'] as newsId,
        from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['categoryIds'] as catids,
        from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['sourceType'] as sourceType
from log.topic_common_event
where
    pt='$pdatetime'
    AND from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['newsType']=='NEWS'
    AND from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['categoryIds'] !=''
    AND from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['sourceType'] !=''
    ) as c LATERAL VIEW explode(split(catids,',')) b AS catId
) click_t
inner join
(
select userid, newsId, catId, durations from (
select userid,
    from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['newsId'] as newsId,
    from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['categoryIds'] as catids,
    from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['duration'] as durations
    from log.topic_common_event
where
    pt='$pdatetime'
    AND from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['duration'] !=''
    AND from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['categoryIds'] !=''
    AND from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['newsType'] == 'NEWS'
    AND from_json(from_json(data,'map<string,string>')['param'],'map<string,string>')['duration'] >= 2000
    ) as e LATERAL VIEW explode(split(catids,',')) f AS catId
) duration_t
on click_t.userid = duration_t.userid
and click_t.newsid = duration_t.newsId
and click_t.catId = duration_t.catId
) temp
where temp.catId rlike '^[0-9]+$';
"

echo $sql
hive -e "$sql" 2>> /home/hadoop/apps/capricorn-app-scheduler/logs/long_term_store_month.log