#!/usr/bin/env python
# -*- coding: utf-8 -*-
import multiprocessing
from gensim.models import word2vec
import codecs
import time
# 引入数据集
sentences=[]
f=codecs.open(u'./data/news_w2v_sampling/part-r-00000-word2vector_sampling.text','r',encoding="utf8")
for line in f:
    replace = line.replace("\n", "")
    sentences.append(replace.encode('utf-8').split())


print 'starting news_w2v_sampling train......'
startTime=time.time()
#构建模型
print "===========================训练的模型==========================="
model = word2vec.Word2Vec(sentences, size=200, window=5, min_count=10, workers=multiprocessing.cpu_count())
# 保存模型，以便重用
model.save(u"./news_w2v_model/news_w2v_file.model")
endTime=time.time()
print "save news_w2v_sampling  successfully",(endTime-startTime)
print "---------------------1.1 进行相关性比较-----------------------------------"
# 进行相关性比较
similar = model.most_similar(['体育'], topn=10)
for word in similar:
    wordName =word[0]
    score = word[1]
    print wordName +":" + str(score)

print "---------------------1.2 进行相似度比较-----------------------------------"
print  model.similarity('体育','北京')


'''
输出结果：

starting news_w2v_sampling train......
===========================训练的模型===========================
save news_w2v_sampling  successfully 171.345999956
---------------------1.1 进行相关性比较-----------------------------------
燕网:0.595151007175
体育项目:0.5943171978
体育赛事:0.590732395649
李永林:0.589524269104
电子竞技:0.583034873009
禹唐:0.582886695862
体育讯:0.582812190056
体育竞技:0.571604788303
钟玲:0.566225111485
篮球:0.563229799271
---------------------1.2 进行相似度比较-----------------------------------
0.24144203532
'''