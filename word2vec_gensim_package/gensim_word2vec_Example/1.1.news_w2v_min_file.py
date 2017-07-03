#!/usr/bin/env python
# -*- coding: utf-8 -*-
import multiprocessing
from gensim.models import word2vec
import codecs
import time
# 引入数据集
sentences=[]
f=codecs.open(u'./data/part-r-00000-word2vector_sampling.min.text','r',encoding="utf8")
for line in f:
    replace = line.replace("\n", "")
    sentences.append(replace.encode('utf-8').split())


print 'starting news_word2vec_min.custompy train......'
startTime=time.time()
#构建模型
model = word2vec.Word2Vec(sentences, size=5, window=200, min_count=1, workers=multiprocessing.cpu_count())
endTime=time.time()
print "save news_word2vec_min custompy successfully",(endTime-startTime)
print "===========================训练的模型==========================="
print "---------------------1.1 进行相关性比较-----------------------------------"
# 进行相关性比较
similar = model.most_similar(['体育'], topn=4)
for word in similar:
    wordName =word[0]
    score = word[1]
    print wordName +":" + str(score)

print "---------------------1.2 进行相似度比较-----------------------------------"
print  model.similarity('体育','北京')


'''
输出结果：

===========================训练的模型===========================
---------------------1.1 进行相关性比较-----------------------------------
国际组:0.992074728012
毛:0.990448176861
派出:0.98987030983
知名:0.988235831261
---------------------1.2 进行相似度比较-----------------------------------
0.929076223754
'''