#!/usr/bin/env python
# -*- coding: utf-8 -*-
import multiprocessing
import os

from gensim.models import word2vec
import codecs
import time
# 引入数据集
class MultiGetSentences(object):
    def __init__(self, dirname):
        self.dirname = dirname

    def __iter__(self):
        for fname in os.listdir(self.dirname):
            for line in open(os.path.join(self.dirname, fname)):
                yield line.split()

sentences = MultiGetSentences(u'./data/news_w2v_sampling') # a memory-friendly iterator

print 'starting news_w2v_sampling train......'
startTime=time.time()
#构建模型
print "===========================训练的模型==========================="
model = word2vec.Word2Vec(sentences, size=200, window=5, min_count=10, workers=multiprocessing.cpu_count())
# 保存模型，以便重用
model.save(u"./news_w2v_model/news_w2v_dir.model")
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
save news_w2v_sampling  successfully 802.549000025
---------------------1.1 进行相关性比较-----------------------------------
体育讯:0.582623958588
体育产业:0.560492038727
燕网:0.544538974762
邵苏:0.539950251579
篮球:0.532236814499
体育赛事:0.531794130802
足球:0.531437337399
重竞技:0.527578353882
秦网:0.526996850967
禹唐:0.526661336422
---------------------1.2 进行相似度比较-----------------------------------
0.267453838196
'''