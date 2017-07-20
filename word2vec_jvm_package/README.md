#  News Word2Vec Model Train With Spark MLlib  
针对资讯类的新闻进行统计分析，通过使用Word2Vec 获取关键词的相关词和通过Word2Vec 词向量计算两个文本的相似度

## 1. Word2Vec 模型运行环境
```
Hadoop版本： 2.7.0
Spark的版本：1.6.3
JDK的版本： 1.7+
```

## 2. 新闻原始数据分词
一般情况下，我们可以直接获取新闻的文章内容title+content，然后通过中文分词器完成分词。这里我们使用Ansj分词。
可以参考：com.caishi.bigdata.categorize.textseg.common.main.MultiDirectoryWord2VecSampling 中案例，主要通过Ansj 的分词提取

### 2.1 输入数据格式
word2vec_jvm_package/Chinese-text-segmentation/data/20170719/pcat-470000/cat-470100/part-r-00000
```
{
    "id": "1700acb676ee02cb",
    "origin": "作者：佚名",
    "sourceId": 91800,
    "m_classifier": "Adaboost_R2C",
    "categoryIds": "[470100]",
    "pubtime": "1498665600000",
    "title": "为什么男人站着尿，女人就得蹲着？",
    "content": "<div><div><p>男性厕所比女性厕所多了小便池，有效的缓解了男人如厕难得问题，而女性由于厕位较少，曾引发不少问题，2012年在广州就发生了女大学生“占领男士厕所”的行为艺术，守着不让男士入内，还分流一部分女士直接去男厕如厕。为什么女人如厕这么困难呢?为什么不能站着小便呢?今天小百就和你们聊聊这个有趣的话题“为什么男人可以站着尿，女人就得蹲着”。</p><p>${{1}}$</p><div>为什么男人是站着尿尿？ \n &nbsp; <br>\n &nbsp;</div><p>因为男人的尿道是一个S形，当男人站着尿尿的时候，受到重力的影响，尿液可以很自然排出。如果我们坐着排尿，尿道就会因为姿势的变化变成一个倒钩的形状，长期受到压迫后，会造成盆腔充血，尿液相对较难排出，经常坐马桶的亲应该有感觉，坐在上面尿的时候会感觉要用力一些，而且长时间坐着尿，膀胱、尿道可能出现炎症。而蹲着小便会让尿道压力发生变化，尿液可能逆流回到膀胱，也容易诱发炎症，所以我们除非上大的，否则不要没事就蹲着尿哦。</p><p>${{2}}$</p><p>尿尿之后也最好不要立刻就坐下，因为男人排尿后如果马上坐下，会造成残留的一些尿液倒流，给尿道中的细菌可乘之机，容易诱发前列腺炎。排尿后，可用手指在阴囊与肛门之间的会阴处挤压一下，这样做有助于排出膀胱中的残留尿液，还对治疗慢性前列腺炎有一定好处。<br></p><p>${{3}}$</p></div><div><div>为什么女人就得蹲着尿? \n &nbsp; <br>\n &nbsp;</div><p>这是因为女性的尿道与男性的有明显区别，女性并不能像男性呈抛物线一样的小便，而坐着排尿时，尿道会变成一个倒钩形，还会受到压迫，造成盆腔充血，尿液比较难排出体外，时间久了，膀胱、尿道可能出现炎症或其他健康问题。另外，女人站着小便，你觉得她可以向男人一样将尿撒向别人，多半都在自己的身上了，你觉得爱美的女性能够容忍？</p><p>${{4}}$</p><p>另外夏季来临，小百温馨提示不管是男性还是女性都应该注意私处清洁卫生，注意补充水分，保持早睡早起的好习惯，合理健康的饮食都可以有效提高免疫，保证私处健康。</p><p>${{5}}$<br></p></div></div>"
}
```
### 2.2 输出数据格式  
通过Ansj 的NLP 进行中文分词，去除停止词等，最终通过每篇文章空格显示  
```
男人 站 尿 女人 蹲 男性 厕所 女性 厕所 小便池 男人 如厕 女性 厕 位 引发 广州 女 大学生 占领 男士 厕所 行为艺术 守 不让 男士 入 分流 女士 男厕 如厕 女人 如厕 困难 站 小便 百 聊聊 有趣 话题 男人 站 尿 女人 蹲 男人 站 尿 尿 男人 尿道 形 男人 站 尿 尿 重力 影响 尿液 自然 排出 坐 排尿 尿道 姿势 倒钩 形状 长期 压迫 盆腔 充血 尿液 难 排出 坐 马桶 亲 尿 用力 坐 尿 膀胱 尿道 炎症 蹲 小便 尿道 压力 发生变化 尿液 逆流 膀胱 诱发 炎症 没事 蹲 尿 尿 尿 坐下 男人 排尿 坐下 残留 尿液 倒流 尿道 中 细菌 可乘之机 诱发 前列腺炎 排尿 可用 手指 阴囊 肛门 会阴 处 挤压 排出 膀胱 中 残留 尿液 治疗 慢性 前列腺炎 女人 蹲 尿 女性 尿道 男性 女性 男性 呈 抛物线 小便 坐 排尿 时 尿道 倒钩 形 压迫 盆腔 充血 尿液 难 排出 体外 久 膀胱 尿道 炎症 健康 女人 站 小便 男人 尿 撒向 多半 身上 爱美 女性 容忍 夏季 来临 百 温馨 男性 女性 私处 清洁卫生 水分 早睡早起 健康 饮食 免疫 保证 私处 健康
```
实际工程中已经对大量的新闻进行了分词，这里可以直接使用，作为word2vec 训练模型的数据输入.参考：https://pan.baidu.com/s/1eSw3nlo

## 3. 文本的分词结果 进行Word2Vec 模型训练 
### 3.1 模型训练  
使用Spark MLlib 进行Word2Vec 模型训练，其实方法很简单。下面是核心的Spark Word2Vec 的核心训练代码   
```

// create a function to tokenize each document
def tokenize(doc: String): Seq[String] = {
    doc.split("\\s").filter(token => token.size >= 2).toSeq
}

  
val textFile = sc.textFile(hdfsURL + newsW2VSampling)
// 1. word2vec 原始数据分词、过滤
val tokens = textFile.map(doc => tokenize(doc))
// 2. word2vec 模型训练
//val word2vec = new Word2Vec().setVectorSize(200).setMinCount(100).setWindowSize(5)
val word2vec = new Word2Vec()
  .setVectorSize(vectorSize.trim.toInt) // 每个词向量的特征长度
  .setMinCount(minCount.trim.toInt) // 每个词至少出现MinCount
  .setWindowSize(windowSize.trim.toInt) //词的滑动窗口
  .setNumPartitions(numPartitions.trim.toInt)
  .setSeed(42L)
  .setNumIterations(numIterations.trim.toInt)
// 3. 模型保存
word2vecModel.save(sc, hdfsURL + newsW2VModelPath)

其中： newsW2VSampling 是存储在HDFS上的样本数据，即上 2.2 中的结果
```

### 3.2 模型加载
训练出模型后，我们需要实时性的预测一批新闻的word2vec ，根据具体业务需求可以做 关键词同义词提取和计算整个句子的词向量，进一步做文本的相似度比较。  
这里我们使用JAVA 方法进行预测，具体代码（已经训练好的新闻模型： https://pan.baidu.com/s/1o7ZSjk2 ，详见后存储HDFS上就可以直接使用了）：  
```
/**
 * 从HDFS上加载 Word2Vec 模型
 *
 * @param w2vModelPath
 * @return
 */
public static Word2VecModel loadW2VModel(String w2vModelPath) {
    JavaSparkContext jsc = null;
    try {
        SparkConf conf = new SparkConf().setAppName("spark-Word2VecModel").setMaster("local");
        jsc = new JavaSparkContext(conf);
        Word2VecModel w2vModel = Word2VecModel.load(jsc.sc(), w2vModelPath);
        return w2vModel;
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        jsc.stop();
    }
    return null;
}


String w2vModelPath = "hdfs://IP/news/W2V/News_W2V_Model/";
Word2VecModel w2vModel = loadW2VModel(w2vModelPath);

```

### 3.3 模型预测-同义词
```
@Test
public void testfindSynonyms() {
    System.out.println("-------------------------------1.1.【投资】  关键词的同义词-----------------------------------");
    Tuple2<String, Object>[] w2vModelSynonyms = w2vModel.findSynonyms("投资", 10);
    for (Tuple2<String, Object> tuple2 : w2vModelSynonyms) {
        String word = tuple2._1();
        Double cosineSimilarity = (Double) tuple2._2();
        System.out.println(word + "\t" + cosineSimilarity);
    }
    System.out.println("-------------------------------1.2.【黄飞鸿】  关键词的同义词-----------------------------------");
    Tuple2<String, Object>[] w2vModelSynonyms_2 = w2vModel.findSynonyms("黄飞鸿", 10);
    for (Tuple2<String, Object> tuple2 : w2vModelSynonyms_2) {
        String word = tuple2._1();
        Double cosineSimilarity = (Double) tuple2._2();
        System.out.println(word + "\t" + cosineSimilarity);
    }
}
```

同义词数据的结果：  
```
-------------------------------1.1.【投资】  关键词的同义词-----------------------------------
 资本	782408.327137235
 投资者	763129.5662595857
 投资人	743162.1265930445
 资产	721816.647049782
 基金	721212.5180297984
 资金	720170.0363401364
 私募	697714.710380789
 企业	695402.1979681276
 融资	694402.1856521369
 并购	674471.4168534336
 -------------------------------1.2.【黄飞鸿】  关键词的同义词-----------------------------------
 方世玉	3369854.656889707
 警察故事	3327364.619644495
 功夫片	3253980.0145932636
 醉拳	3215501.087689645
 武术指导	3190052.638941494
 武打	3157699.54503617
 武侠片	3132025.28115942
 吴宇森	3116860.397585014
 徐克	3104681.7448405162
 李连杰	3103499.356360355
```
### 3.4 模型预测-构建句子的词向量，计算文本相似度

```
String[] words_1 = "体育 北京 男篮 热身赛 磨合 战术 年轻 新秀 崭露头角 热身赛 中 北京队 小将 张才仁 锻炼 北京首钢队 天津 海峡 杯 比赛 中 大胜 台 啤 队 胜 负 战绩 结束 热身赛 旅 昨晚 比赛 中 后卫 方硕 手感 火热 全队 中锋 张松涛 马布里 面对 防守 马布里 持球 欲 突破 孙悦 上篮 孙悦 突破 马布里 突破 防守 方硕 突破 防守 马布里 撇嘴 莫里斯 遭遇 严防 莫里斯 出阵 张才仁 方硕 上篮 朱彦西 突破 吉喆 打暗号 张松涛 强攻 首钢队 热身赛 中 队伍 磨合 新 战术 李根 队 中 球员 顶替 队员 默契 配合 比赛 建立 首钢队 热身赛 中 锻炼 小将 张才仁 青年队 上调 队员 热身赛 中 打球 尚可 外援 莫里斯 说 打球 整体 打法 首钢队 队长 孙悦 热身赛 新 赛季 孙悦 恢复 寻找 比赛 孙悦 说 场上 稍微 身体 比赛 比赛 中 首钢队 不敌 天津 战全胜 锁定 冠军".split(" ");
int vectorSize = 200;// 词
double[] wordsVector_1 = Word2VecUtils.buildWordsVector(words_1, vectorSize, w2vModel);
System.out.println(Word2VecUtils.getStringWithWord2Vec(wordsVector_1));//0.026998909888789058,-0.01186487590894103,0.024897634619264863,0.05851895920932293,-0.012189864530228078
String[] words_2 = "体育 领队 调侃 技师 调车 时 维泰尔 安静 叫喊 阿里 巴贝内 说 莱科宁 新浪 体育讯 莱科宁 新加坡 站 季军 本赛季 登上 领奖台 队友 维泰尔 拿出 新加坡 统治 级 胜利 排位 赛后 莱科宁 赛车 不太好 度过 困难 正 赛中莱 科宁 工程师 报告 赛车 驾驶 法拉利 领队 阿德里巴 贝 澄清 莱科宁 车队 调侃 称 冰人 爱 抱怨 阿里 巴贝内 说 抱怨 技师 调校 赛车 塞巴 安静 叫喊 谈到 车手 阿里 巴贝内 说 蒙扎 强 塞巴 强 塞巴 赛道 强大 车手 领奖台".split(" ");
double[] wordsVector_2 = Word2VecUtils.buildWordsVector(words_2, vectorSize, w2vModel);
System.out.println(Word2VecUtils.getStringWithWord2Vec(wordsVector_2));//0.004943567619193345,0.010045223403722048,0.03035004214325454,0.02609390113502741,0.017606549547053874
```

具体细节：bigdata\news\word2vec\trainer\NewsWord2VecTrainningTest.java

