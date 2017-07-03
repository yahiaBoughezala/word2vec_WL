#!/usr/bin/env python
# -*- coding: utf-8 -*-
import multiprocessing
from gensim.models import word2vec
import codecs
# 引入数据集
raw_sentences = ["我 爱 北京 天安门", '我 爱 北京 长城']
# 切分词汇
sentences=[s.split(" ") for s in raw_sentences]
print "-----------------------------------------------------------------------------"
results=[]
f=codecs.open(u'./data/zh.jian.wiki.seg-1.3gg.txt','r',encoding="utf8")
for line in f:
    replace = line.replace("\n", "")
    results.append(replace.encode('utf-8').split())
print results


#构建模型
model = word2vec.Word2Vec(sentences, size=200, window=5, min_count=1, workers=multiprocessing.cpu_count())
print "---------------------进行相关性比较-----------------------------------"
# 进行相关性比较
similar = model.most_similar(['爱'], topn=4)
for word in similar:
    wordName =word[0]
    score = word[1]
    print wordName +":" + str(score)

print "---------------------进行相似度比较-----------------------------------"
print  model.similarity('爱','天安门')
