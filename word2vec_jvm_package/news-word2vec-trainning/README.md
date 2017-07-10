# News Word2Vec Model 
针对资讯类的新闻进行统计分析，通过使用Word2Vec 获取关键词的相关词和通过Word2Vec 词向量计算两个文本的相似度

#### 1. Word2Vec 模型运行环境
```
Hadoop版本： 2.7.0
Spark的版本：1.6.3
JDK的版本： 1.8+
```

#### 2. 新闻原始数据分词
一般情况下，我们可以直接获取新闻的文章内容title+content，然后通过中文分词器完成分词。这里我们使用Ansj分词。

