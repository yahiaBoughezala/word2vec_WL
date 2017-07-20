package com.caishi.bigdata.categorize.textseg.common.keyword;

import com.caishi.bigdata.categorize.textseg.common.keyword.texrank.TextRankKeyword;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by fuli.shen on 2017/7/10.
 */
public class KeyWordComputerTest {

    String title = "周末湖北湖南等地将有暴雨 东北雨势减弱";
    String content = "<div><p>中国天气网讯 今天（8日）开始，我国南方的雨水随着雨带南压略有增强，江淮、江汉、江南北部和西南地区东部等地部分地区有大到暴雨，局地大暴雨，明天雨势继续增强，贵州、湖南等4省局地有大暴雨。而东北的降雨今天将明显减弱，以阵雨为主，前期的降雨对缓解旱情十分有利。</p><p>${{1}}$<br>7日，重庆巫溪县宁河街道北门沟河水暴涨，临河的车辆泡在积水之中。（ &nbsp; 来源：巫溪网）</p><p>昨天南方强降雨有所减弱，不过在重庆北部、湖北西南部、安徽沿淮地区、河南东南部及云南北部、贵州西南部、广西西北部和南部、广东南部、福建东南部沿海、浙江东南部、江西中部等地仍出现暴雨或大暴雨，河南信阳、安徽六安局地150～205毫米。</p><p>今天开始，随着副热带高压向东移动，雨带略有南压，雨势增强，江淮、江汉、江南北部和西南地区东部等地部分地区有大到暴雨，局地大暴雨， &nbsp; 中央气象台预计贵州南部、湖南西北部、湖北西南部和东北部、重庆东部、河南南部等地有暴雨，湖北西南部局地有大暴雨（100～120毫米）；上述部分地区有短时强降水，局地伴有雷暴大风等强对流天气。</p><p>明天开始，南方此轮降雨将进入最强时段，广西西部和北部、江南西部、江汉东部、江淮西部和北部、黄淮南部、云南西北部等地的部分地区有大到暴雨，贵州南部、湖南西南部和东北部、安徽南部、江苏东部等地局地有大暴雨（100～130毫米），上述部分地区有短时强降水，局地伴有雷暴大风等强对流天气。</p><p>未来三天，江淮、江汉、江南北部和西南地区东部等地雨势凶猛，气象专家提醒，有关部门需特别警惕中小河流洪水和山洪地质灾害。另外，目前长江干支流的部分河段仍处于高水位，需 &nbsp; 关注未来强降雨对防汛制造的压力。</p><p>而在北方，今明两天东北地区降雨将明显减弱，以阵雨为主。这次北方强降雨的范围基本上覆盖了气象干旱特旱的区域，对于增加水库蓄水，缓解旱情十分有利。</p></div>";

    @Test
    public void testGetKeyWordsComputeByTFIDF() {
        List<Keyword> keywords = new KeyWordComputer(10).computeArticleTfidf(title, content);
        System.out.println(keywords);
    }
    /**
     [长春/103.27401269082938, 宝马/69.35736253717518, 车窗/68.479698980868, 无人问津/64.52194781141492, 半个/61.520417471800705, 街头/60.661915565182454, 长春市/16.412366408296606, 富苑/14.153864030577857, 小路/13.10367379139715, 中午/9.960126540036269]
     */
    @Test
    public void testGetKeyWordsComputeByTextRank() {
        Map<String, Float> keysWordsMap =  new TextRankKeyword().getKeywordList(title,content,15);
        System.out.println(keysWordsMap);
    }
    /**
     {长春市=1.72528, 车辆=1.5629234, 东亚=1.5357165, 富苑=1.522306, 车窗=1.3731439, 宝马=1.2186744, 轿车=1.1566654, 常麟祥=1.1182842, 玻璃=1.1125735, 车牌=1.1088426}
     */
}
