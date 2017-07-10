#tf-idf/textrank + ansj text seg
对比直接Ansj分词、TF-IDF 分词、TextRank 三种分词情况
### 0. 集成ansj 文本分词
这里使用的是ansj_seg 3.7.6 版本的包
```
<dependency>
    <groupId>org.ansj</groupId>
    <artifactId>ansj_seg</artifactId>
    <version>3.7.6</version>
</dependency>
<dependency>
    <groupId>org.nlpcn</groupId>
    <artifactId>nlp-lang</artifactId>
    <version>1.5</version>
    <scope>compile</scope>
</dependency>
```

### 1.添加停止词
详见./stopwords/*

### 2. 文章分词
```
String title = "长春街头宝马七系豪车车窗被砸 车停了半个月无人问津";
String content = "<div> \n" +
        " <p>${{1}}$</p> \n" +
        " <p>长春街头<a>宝马</a>七系豪车车窗被砸 车停了半个月无人问津</p> \n" +
        " <p>${{2}}$</p> \n" +
        " <p>${{3}}$</p> \n" +
        " <p>长春街头宝马七系豪车车窗被砸 车停了半个月无人问津</p> \n" +
        " <p>3日中午，记者在长春市亚泰富苑后身的小路上看见一辆宝马七系轿车，车辆右后车窗被砸，无人问津。</p> \n" +
        " <p>3日中午，记者来到了长春市亚泰富苑后身的小路上，在小路南侧，停有一辆灰色的<a>宝马</a>七系轿车，右后车窗玻璃被砸，玻璃散落一地，车座上也有不少的玻璃碎屑。同时，记者在车门旁边，还看见一块砖头。据经常在周边吃午饭的上班族兰先生说，这辆车已经在该处停了近半个月的时间了，不知道车主是谁。而通过车牌判定，车辆是内蒙古地区的。</p> \n" +
        " <p>东亚经贸新闻记者 常麟祥 摄影 兰洋</p> \n" +
        "</div>";
```
#### 2.1 直接使用ansj 进行分词效果
参考com.caishi.bigdata.categorize.textseg.common.Word2VectorTextSegmentTest  
```
    /**
     * 对新闻的title+content 进行分词
     */
    @Test
    public void testTextSegments(){
      
        StringBuffer wordsBuffer2 = getWords(title, content);
        System.out.println(wordsBuffer2);
    }

```
分词的结果：  
长春 街头 宝马 系 豪 车 车窗 砸 车 停 半个 月 无人问津 长春 街头 宝马 系 豪 车 车窗 砸 车 停 半个 月 无人问津 长春 街头 宝马 系 豪 车 车窗 砸 车 停 半个 月 无人问津 中午 长春市 亚泰 富苑 后身 小路 宝马 系 轿车 车辆 右后 车窗 砸 无人问津 中午 长春市 亚泰 富苑 后身 小路 小路 南侧 停有 灰色 宝马 系 轿车 右后 车窗 玻璃 砸 玻璃 散落 车座 玻璃 碎屑 车门 砖头 吃 午饭 上班族 兰 说 车 该处 停 半个 月 车主 车牌 判定 车辆 内蒙古地区 东亚 经贸 新闻记者 常麟祥 摄影 兰洋

#### 2.2 通过TF-IDF 提取文章的关键词
```
public void testGetKeyWordsComputeByTFIDF() {
    List<Keyword> keywords = new KeyWordComputer(10).computeArticleTfidf(title, content);
    System.out.println(keywords);
}
```
分词的结果：  
[长春/103.27401269082938, 宝马/69.35736253717518, 车窗/68.479698980868, 无人问津/64.52194781141492, 半个/61.520417471800705, 街头/60.661915565182454, 长春市/16.412366408296606, 富苑/14.153864030577857, 小路/13.10367379139715, 中午/9.960126540036269]


#### 2.3 通过TextRank提取文章的关键词
```
public void testGetKeyWordsComputeByTextRank() {
    Map<String, Float> keysWordsMap =  new TextRankKeyword().getKeywordList(title,content,10);
    System.out.println(keysWordsMap);
}
```

分词的结果：  
{长春市=1.72528, 车辆=1.5629234, 东亚=1.5357165, 富苑=1.522306, 车窗=1.3731439, 宝马=1.2186744, 轿车=1.1566654, 常麟祥=1.1182842, 玻璃=1.1125735, 车牌=1.1088426}

### 3. 参考资料  
https://github.com/NLPchina/ansj_seg  
https://github.com/hankcs/TextRank