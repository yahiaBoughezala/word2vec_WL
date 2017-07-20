#!/bin/bash
source ~/.bashrc

start_time=`date -d "24 hour ago" +%Y%m%d%H`
end_time=`date +%Y%m%d%H`

event_id_prefix_news_pv="c03%"
event_id_click_more_altas="3047"
event_id_click_relevance_news="3048"
event_id_click_altas="1036"
event_id_click_push="1010"
event_id_click_search="1017"
event_id_2_0_recom="1047"
wuli_comment_type="CST002"
pv_rank_weight=0.5
share_rank_weight=0.3
comment_rank_weight=0.2
topN=100

sql="
use log;
set hive.execution.engine=tez;
set hive.cli.print.header=false;

CREATE TEMPORARY TABLE top_news_click_pv (newsId string, click int);

CREATE TEMPORARY TABLE top_news_share (newsId string, share int);

CREATE TEMPORARY TABLE top_news_comment(newsId string, comment int);

--计算新闻点击pv
insert into top_news_click_pv
select t.newsId, sum(t.click) as click_total from
(select userid,get_json_object(data,'$.param.newsId') as newsId, count(1) as click
from log.topic_common_event
where pt>=$start_time and
pt<=$end_time and
userid is not NULL and
(get_json_object(data,'$.event') like '$event_id_prefix_news_pv' or get_json_object(data,'$.event') in ($event_id_2_0_recom,$event_id_click_more_altas, $event_id_click_relevance_news, $event_id_click_altas, $event_id_click_push, $event_id_click_search)) and
get_json_object(data,'$.param.newsType')='NEWS'
group by userid,get_json_object(data,'$.param.newsId'))
t group by t.newsId;

--计算新闻分享
insert into top_news_share
select t.newsId, sum(t.share) as share_total from
(select messageid as newsId, count(1) as share from topic_news_social
where pt>=$start_time and
pt<=$end_time and
messageType='NEWS' and
logtype='SHARE' group by messageid)
t group by t.newsId;

--计算新闻评论
insert into top_news_comment
select messageid as newsId, count(1) as comment from log.topic_comment_event
where pt>=$start_time and
pt<=$end_time and
messagetype='NEWS' and
get_json_object(data, '$.commentSourceType')='$wuli_comment_type' and
get_json_object(data, '$.commentStatus')='ONLINE' group by messageid;

--点击事件新闻id为空时，评论新闻id也为空，取分享新闻id
select x.newsId, round(x.pv_click*$pv_rank_weight+x.share*$share_rank_weight+x.comment*$comment_rank_weight, 3) as score from
(select if(t1.newsId is null, t2.newsId, t1.newsId) as newsId,
 if(t1.click is null, 0, t1.click) as pv_click,
if(t2.share is null, 0, t2.share) as share,
if(t3.comment is null, 0, t3.comment) as comment
from top_news_click_pv t1
full outer join top_news_share t2 on t1.newsId=t2.newsId
full outer join top_news_comment t3 on t1.newsId=t3.newsId) x
order by score desc
limit $topN

"


echo $sql
hive -e "$sql" > /home/hadoop/data/hivedata/top_news.txt 2> /home/hadoop/caishi/log/hadoop/top_news.log
sed -i "/WARN/d" /home/hadoop/data/hivedata/top_news.txt
